package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.ui.GuideActivity;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.RegisterActivity;


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

    public static void gotoRegisterActivity(Activity activity) {
        startActivity(activity, RegisterActivity.class);
    }

    public static void gotoGuideActivity(Activity activity) {
        startActivity(activity,GuideActivity.class);
    }
}