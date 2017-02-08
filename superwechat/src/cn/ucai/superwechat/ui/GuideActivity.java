package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.MFGT;

public class GuideActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_login, R.id.bt_sign_up})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                MFGT.gotoLoginActivity(this);
                break;
            case R.id.bt_sign_up:
                MFGT.gotoRegisterActivity(this);
                break;
        }
    }
}
