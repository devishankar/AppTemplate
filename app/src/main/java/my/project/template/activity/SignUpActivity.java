package my.project.template.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import my.project.template.R;
import my.project.template.http.response.RegisterResponseHandler;
import my.project.template.listener.IHttpResponseListener;
import my.project.template.utils.AppConstants;
import my.project.template.utils.AppHttpClient;
import my.project.template.utils.Logger;
import my.project.template.utils.Utils;

public class SignUpActivity extends BaseActivity implements View.OnClickListener, IHttpResponseListener {

    private static final String TAG = "SignUpActivity";
    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPhone;
    private EditText edtPassword;
    private RelativeLayout rlContent;
    private RelativeLayout rlSplashScreen;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mContext = this;
        rlContent = (RelativeLayout) findViewById(R.id.rlContent);
        rlSplashScreen = (RelativeLayout) findViewById(R.id.rlSplashScreen);

        this.edtPhone = (EditText) findViewById(R.id.edtPhone);
        this.edtPassword = (EditText) findViewById(R.id.edtPassword);
        this.edtEmail = (EditText) findViewById(R.id.edtEmail);
        this.edtName = (EditText) findViewById(R.id.edtName);

        Button btnRegister = (Button) findViewById(R.id.btnSignup);
        btnRegister.setOnClickListener(this);

        View actionBar = getActionBarView();
        if (actionBar != null) {
            actionBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSignup) {
            Log.d(TAG, "Register clicked");
            if (areFieldsValid()) {
                Utils.setUserRegType(this, 1);
                rlContent.setVisibility(View.GONE);
                rlSplashScreen.setVisibility(View.VISIBLE);

                String strName, strEmail, strPhone, strPassword;
                strName = this.edtName.getText().toString();
                strEmail = this.edtEmail.getText().toString();
                strPassword = this.edtPassword.getText().toString();

                RequestParams params = Utils.getRequestParamsWithoutSession(mContext);
                params.put("user_fname", strName);
                params.put("user_lname", "");
                params.put("user_email", strEmail);
                params.put("password", strPassword);
                params.put("os", "Android");
                params.put("os_ver", Utils.getOsVersion());

                String userRegisterPath = AppConstants.BASE_URL + getString(R.string.userSignUp);
                AppHttpClient.get(userRegisterPath, params, new RegisterResponseHandler(mContext));
            }
        }
    }

    private boolean areFieldsValid() {
        boolean isValid = true;
        if (this.edtName.getText().toString().replace(" ", "").equals("")) {
            this.edtName.setError(getString(R.string.error_enter_name));
            isValid = false;
        }
        if (this.edtPassword.getText().toString().replace(" ", "").equals("")) {
            this.edtName.setError(getString(R.string.error_enter_password));
            isValid = false;
        }
        if (this.edtEmail.getText().toString().replace(" ", "").equals("")) {
            this.edtEmail.setError(getString(R.string.error_enter_email));
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void onSuccess(JSONObject body) throws JSONException {
        Logger.d(TAG, "success");
        Utils.storeUserVariables(body, this);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMessage(String resp) {
        rlContent.setVisibility(View.VISIBLE);
        rlSplashScreen.setVisibility(View.GONE);

        Utils.showLongToast(this, resp);
    }

    @Override
    public void onFailure(String resp, Throwable throwable) {
        rlContent.setVisibility(View.VISIBLE);
        rlSplashScreen.setVisibility(View.GONE);

        Utils.showLongToast(this, getString(R.string.toast_socket_timeout_error));
    }

    @Override
    public void onJsonParseError() {
        rlContent.setVisibility(View.VISIBLE);
        rlSplashScreen.setVisibility(View.GONE);

        Utils.showLongToast(this, getString(R.string.toast_socket_timeout_error));
    }

    public View getActionBarView() {
        Window window = getWindow();
        View v = window.getDecorView();
        int resId;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            resId = getResources().getIdentifier(
                    "action_bar_container", "id", getPackageName());
        } else {
            resId = Resources.getSystem().getIdentifier(
                    "action_bar_container", "id", "android");
        }
        if (resId != 0) {
            return v.findViewById(resId);
        } else {
            return null;
        }
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
        super.onSupportActionModeStarted(mode);
        View actionBar = getActionBarView();
        if (actionBar != null) {
            actionBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        View actionBar = getActionBarView();
        if (actionBar != null) {
            actionBar.setVisibility(View.GONE);
        }
    }
}
