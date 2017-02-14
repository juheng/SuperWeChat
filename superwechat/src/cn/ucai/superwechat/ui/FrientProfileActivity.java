package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;

public class FrientProfileActivity extends BaseActivity {
    User user;
    private static final String TAG = FrientProfileActivity.class.getSimpleName();
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.nick)
    TextView nick;
    @BindView(R.id.tv_send_msg)
    TextView tvSendMsg;
    @BindView(R.id.tv_send_video)
    TextView tvSendVideo;
    @BindView(R.id.tv_add_contact)
    TextView tvAddContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frient_profile);
        ButterKnife.bind(this);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("详细资料");
        initData();
    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        L.e(TAG, "user====" + user.toString());
        if (user != null) {
            showUserInfo();
        } else {
            MFGT.finish(this);
        }
    }

    private void showUserInfo() {
        nick.setText(user.getMUserNick());
        EaseUserUtils.setAppUserAvatarbyPath(this, user.getAvatar(), avatar);
        name.setText("微信号：" + user.getMUserName());
        L.e(TAG, "nick====" + nick);
        if (isFirent()) {
            tvSendMsg.setVisibility(View.VISIBLE);
            tvSendVideo.setVisibility(View.VISIBLE);
        } else {
            tvAddContact.setVisibility(View.VISIBLE);
        }
    }

    private boolean isFirent() {
        User u = SuperWeChatHelper.getInstance().getAppContactList().get(user.getMUserName());
        L.e(TAG, "u====" + u);
        if (u == null) {
            return false;
        } else {
            SuperWeChatHelper.getInstance().saveAppContact(user);
            return true;
        }
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        MFGT.finish(this);
    }
}
