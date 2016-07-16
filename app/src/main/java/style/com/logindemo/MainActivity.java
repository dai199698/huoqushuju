package style.com.logindemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String APP_ID = "1104898485";
    public static final String APP_ID2 = "1104898485";
    UserInfo mInfo;
    //登录的回调接口
    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            Log.e("TAG",values.toString());
            Toast.makeText(MainActivity.this, values.toString(), Toast.LENGTH_SHORT).show();
            try {
                int ret = values.getInt("ret");
                if (ret == 0) {
                    Toast.makeText(MainActivity.this, "登录成功",
                            Toast.LENGTH_LONG).show();

                    String openID = values.getString("openid");
                    String accessToken = values.getString("access_token");
                    String expires = values.getString("expires_in");
                    mTencent.setOpenId(openID);
                    mTencent.setAccessToken(accessToken, expires);
                }
                mInfo = new UserInfo(MainActivity.this, mTencent.getQQToken());
                mInfo.getUserInfo(getUserInfoListener);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    //获取用户信息的回调接口
    IUiListener getUserInfoListener = new BaseUiListener(){
        @Override
        protected void doComplete(JSONObject values) {
            Log.e("TAG",values.toString());
            Toast.makeText(MainActivity.this, values.toString(), Toast.LENGTH_SHORT).show();
        }
    };


    Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
        // 其中APP_ID是分配给第三方应用的appid，类型为String。
        mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
        System.out.println("xxxx");
        setContentView(R.layout.activity_main);
    }




    public void login(View view)
    {
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", loginListener);
        }
//        else {
//            if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
//                mTencent.logout(this);
//                mTencent.login(this, "all", loginListener);
//                isServerSideLogin = false;
//                Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
//                return;
//            }
//            mTencent.logout(this);
//            updateUserInfo();
//            updateLoginButton();
//        }
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            doComplete((JSONObject)response);
        }

        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(MainActivity.this, "onError: " + e.errorDetail, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this, "onCancel: " , Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("TAG", "-->onActivityResult " + requestCode  + " resultCode=" + resultCode);
        Toast.makeText(MainActivity.this,"-->onActivityResult " + requestCode  + " resultCode=" + resultCode , Toast.LENGTH_SHORT).show();

        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
