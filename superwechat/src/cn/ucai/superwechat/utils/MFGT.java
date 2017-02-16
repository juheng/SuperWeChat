package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.hyphenate.easeui.domain.User;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.ui.AddContactActivity;
import cn.ucai.superwechat.ui.AddFrientActivity;
import cn.ucai.superwechat.ui.ChatActivity;
import cn.ucai.superwechat.ui.FrientProfileActivity;
import cn.ucai.superwechat.ui.GuideActivity;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.MainActivity;
import cn.ucai.superwechat.ui.ProfileFragment;
import cn.ucai.superwechat.ui.RegisterActivity;
import cn.ucai.superwechat.ui.SettingsActivity;
import cn.ucai.superwechat.ui.UserProfileActivity;


/**
 * Created by Administrator on 2017/1/10 0010.
 */
public class MFGT {

    public static void finish(Activity context, Class<?> clz){
        context.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
        context.finish();
    }
    public static void finish(Activity context){
        context.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
        context.finish();
    }
    public static void startActivity(Activity context, Class<?> clz){
        context.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
        context.startActivity(new Intent(context,clz));
    }
    public static void startActivity(Context context,Intent intent){
        context.startActivity(intent);
    }

    public static void gotoLoginActivity(Activity activity) {
        startActivity(activity, LoginActivity.class);
    }
    public static void gotoLoginCleanTask(Activity activity) {
        startActivity(activity, new Intent(activity,LoginActivity.class)
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

    }

    public static void gotoRegisterActivity(Activity activity) {
        startActivity(activity, RegisterActivity.class);
    }

    public static void gotoGuideActivity(Activity activity) {
        startActivity(activity,GuideActivity.class);
    }

    public static void gotoSettingsActivity(FragmentActivity activity) {
        startActivity(activity, SettingsActivity.class);
    }

    public static void gotoProfileActivity(Activity activity) {
        startActivity(activity, UserProfileActivity.class);
    }

    public static void gotoAddContact(Activity activity) {
        startActivity(activity, AddContactActivity.class);
    }

    public static void gotoFirent(Activity activity, User user) {
        startActivity(activity,new Intent(activity, FrientProfileActivity.class)
        .putExtra(I.User.TABLE_NAME,user));
    }
    public static void gotoFirent(Activity activity, String username) {
        startActivity(activity,new Intent(activity, FrientProfileActivity.class)
        .putExtra(I.User.USER_NAME,username));
    }

    public static void gotoAddFrient(Activity activity,User user) {
        startActivity(activity,new Intent(activity, AddFrientActivity.class)
                .putExtra(I.User.USER_NAME,user));
    }

    public static void gotoChatActivity(Activity activity,User user) {
        startActivity(activity,new Intent(activity, ChatActivity.class).putExtra("userId", user.getMUserName()));
    }

    public static void gotoMainActivity(Activity activity) {
        startActivity(activity,new Intent(activity,MainActivity.class).putExtra("right",true));
    }

    public static void gotoUserProfileActivity(Activity activity) {
        startActivity(activity,UserProfileActivity.class);
    }
}
