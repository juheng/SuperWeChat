package cn.ucai.superwechat.net;

import android.content.Context;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.utils.OkHttpUtils;

/**
 * Created by Administrator on 2017/2/8 0008.
 */

public class NetDao {
    public static void register(Context context, String userName, String userNick, String password,
                                OkHttpUtils.OnCompleteListener<String> listener){
OkHttpUtils<String>utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .post()
                .addParam(I.User.USER_NAME,userName)
                .addParam(I.User.NICK,userNick)
                .addParam(I.User.PASSWORD,password)
                .targetClass(String.class)
                .execute(listener);
    }
    public static void unRegister(Context context, String userName, OkHttpUtils.OnCompleteListener<String> listener){
        OkHttpUtils<String>utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UNREGISTER)
                .addParam(I.User.USER_NAME,userName)
                .targetClass(String.class)
                .execute(listener);
    }
}
