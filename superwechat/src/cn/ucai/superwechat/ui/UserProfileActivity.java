package cn.ucai.superwechat.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.domain.Result;
import cn.ucai.superwechat.net.NetDao;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.OkHttpUtils;
import cn.ucai.superwechat.utils.PreferenceManager;
import cn.ucai.superwechat.utils.ResultUtils;

public class UserProfileActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = UserProfileActivity.class.getSimpleName();

    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_userinfo_avatar)
    ImageView ivUserinfoAvatar;
    @BindView(R.id.tv_userinfo_nick)
    TextView tvUserinfoNick;
    @BindView(R.id.tv_userinfo_name)
    TextView tvUserinfoName;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_user_profile);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    private void initView() {
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("个人信息");
    }

    private void initListener() {
        String username = EMClient.getInstance().getCurrentUser();
        tvUserinfoName.setText("微信号：" + username);
        EaseUserUtils.setAppUserNick(username, tvUserinfoNick);
        EaseUserUtils.setAppUserAvatar(this, username, ivUserinfoAvatar);
    }

   /* @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_userinfo_avatar:
                uploadHeadPhoto();
                break;
            case R.id.tv_userinfo_nick:
                final EditText editText = new EditText(this);
                new Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                        .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nickString = editText.getText().toString();
                                if (TextUtils.isEmpty(nickString)) {
                                    Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                updateRemoteNick(nickString);
                            }
                        }).setNegativeButton(R.string.dl_cancel, null).show();
                break;
            default:
                break;
        }
*/


    public void asyncFetchUserInfo(String username) {
        NetDao.getUserInfoByUsername(this, username, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                L.e(TAG, "s=" + s);
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    if (result != null && result.isRetMsg()) {
                        L.e(TAG, "result=" + result.toString());
                        User user = (User) result.getRetData();
                        SuperWeChatHelper.getInstance().saveAppContact(user);
                        tvUserinfoNick.setText(user.getMUserNick());
                        if (!TextUtils.isEmpty(user.getAvatar())) {
                            Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(ivUserinfoAvatar);
                        } else {
                            Glide.with(UserProfileActivity.this).load(R.drawable.em_default_avatar).into(ivUserinfoAvatar);
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                L.e(TAG, "error=" + error);
            }
        });
    }


    private void uploadHeadPhoto() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                L.e(TAG,"pickIntent1======"+pickIntent.toString());
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image*//*");
                                L.e(TAG,"pickIntent2======"+pickIntent.toString());
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                L.e(TAG,"pickIntent3======"+pickIntent.toString());
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private File saveBitmapFile(Intent pictata) {
        Bundle extras = pictata.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            String imagePath = EaseImageUtils.getImagePath(EMClient.getInstance().getCurrentUser()
                    + I.AVATAR_SUFFIX_JPG);
            File file = new File(imagePath);//将要保存图片的路径
            L.e("file path=" + file.getAbsolutePath());
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return file;
        }
        return null;

    }

    private void updateRemoteNick(final String nickName) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
        L.e(TAG, "nickName=" + nickName);
        NetDao.updateNick(this, EMClient.getInstance().getCurrentUser(), nickName, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                dialog.dismiss();
                if (s != null) {
                    L.e(TAG, "S=" + s);
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    if (result != null && result.isRetMsg()) {
                        User user = (User) result.getRetData();
                        if (user != null) {
                            tvUserinfoNick.setText(nickName);
                            SuperWeChatHelper.getInstance().saveAppContact(user);
                            PreferenceManager.getInstance().setCurrentUserNick(nickName);
                            CommonUtils.showShortToast(R.string.toast_updatenick_success);
                        }
                    } else if (result.getRetCode() == I.MSG_USER_SAME_NICK) {
                        CommonUtils.showShortToast(R.string.toast_updatenick_fail);
                    } else {
                        CommonUtils.showShortToast(R.string.toast_updatenick_fail);
                    }
                } else {
                    CommonUtils.showShortToast(R.string.toast_updatenick_fail);
                }
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                CommonUtils.showShortToast(R.string.toast_updatenick_fail);

            }
        });
       /* new Thread(new Runnable() {

            @Override
            public void run() {
                boolean updatenick = SuperWeChatHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nickName);
                if (UserProfileActivity.this.isFinishing()) {
                    return;
                }
                if (!updatenick) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
                                    .show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
                                    .show();
                            tvUserinfoNick.setText(nickName);
                        }
                    });
                }
            }
        }).start();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                L.e(TAG,"data======"+data.toString());
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    //setPicToView(data);
                    uploadAppUserAvatar(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * save the picture data
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            ivUserinfoAvatar.setImageDrawable(drawable);
            uploadUserAvatar(Bitmap2Bytes(photo));
        }

    }

    private void uploadAppUserAvatar(Intent picdata) {
        File file=saveBitmapFile(picdata);
        L.e(TAG,"file="+file);
        if(file==null){
            return;
        }
        L.e(TAG,"file="+file.getAbsolutePath());
        dialog=ProgressDialog.show(this,getString(R.string.dl_update_photo),getString(R.string.dl_waiting));
        NetDao.updateAvatar(this, EMClient.getInstance().getCurrentUser(), file, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                L.e(TAG,"s="+s);
                Result result=ResultUtils.getResultFromJson(s,User.class);
                if(result!=null){
                    if(result.isRetMsg()){
                        User user= (User) result.getRetData();
                        if(user!=null){
                            PreferenceManager.getInstance().setCurrentUserAvatar(user.getAvatar());
                            SuperWeChatHelper.getInstance().saveAppContact(user);
                            EaseUserUtils.setUserAvatar(UserProfileActivity.this,user.getMUserName(),ivUserinfoAvatar);
                            CommonUtils.showShortToast(R.string.toast_updatephoto_success);
                        }
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                CommonUtils.showShortToast(R.string.toast_updatephoto_fail);

            }
        });
    }

    private void uploadUserAvatar(final byte [] data) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String avatarUrl = SuperWeChatHelper.getInstance().getUserProfileManager().uploadUserAvatar(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (avatarUrl != null) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }).start();

        dialog.show();
    }


    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        MFGT.finish(this);
    }

    @OnClick({R.id.iv_userinfo_avatar, R.id.tv_userinfo_nick})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_userinfo_avatar:
                uploadHeadPhoto();
                break;
            case R.id.tv_userinfo_nick:
                final EditText editText = new EditText(this);
                new Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                        .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nickString = editText.getText().toString();
                                if (TextUtils.isEmpty(nickString)) {
                                    Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                updateRemoteNick(nickString);
                            }
                        }).setNegativeButton(R.string.dl_cancel, null).show();
                break;
        }
    }
}
