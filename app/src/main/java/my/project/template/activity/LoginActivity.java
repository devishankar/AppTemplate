package my.project.template.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import my.project.template.R;
import my.project.template.http.response.LoginResponseHandler;
import my.project.template.listener.IHttpResponseListener;
import my.project.template.utils.AppConstants;
import my.project.template.utils.AppHttpClient;
import my.project.template.utils.Logger;
import my.project.template.utils.Utils;

public class LoginActivity extends BaseActivity implements View.OnClickListener, IHttpResponseListener {

    private static final String TAG = "LoginActivity";
    private static final int LOGIN_REQUEST = 1;
    private static final int FORGOT_REQUEST = 2;
    private EditText edtEmail;
    private EditText edtPassword;
    private Context mContext;
    private RelativeLayout rlContent;
    private RelativeLayout rlSplashScreen;
    private int request;
    private RelativeLayout rlLogin;
    private RelativeLayout rlForgotPassword;
    private EditText edtForgotEmail;
    private boolean isForgotPassClicked;
    private Button btnForgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        this.rlContent = (RelativeLayout) findViewById(R.id.rlContent);
        this.rlSplashScreen = (RelativeLayout) findViewById(R.id.rlSplashScreen);
        this.rlLogin = (RelativeLayout) findViewById(R.id.rlLogin);
        this.rlForgotPassword = (RelativeLayout) findViewById(R.id.rlForgotPassword);


        this.edtPassword = (EditText) findViewById(R.id.edtPassword);
        this.edtEmail = (EditText) findViewById(R.id.edtEmail);
        this.edtForgotEmail = (EditText) findViewById(R.id.edtForgotEmail);

        this.edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    processLogin();
                }
                return false;
            }
        });

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnForgotPass = (Button) findViewById(R.id.btnForgotPassword);
        Button btnForgot = (Button) findViewById(R.id.btnForgot);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        btnLogin.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
        btnForgot.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnLogin) {
            if (isLoginFieldsValid()) {
                processLogin();
            }
        } else if (id == R.id.btnForgotPassword) {
            isForgotPassClicked = true;
            btnForgotPass.setVisibility(View.GONE);
            rlLogin.setVisibility(View.GONE);
            rlForgotPassword.setVisibility(View.VISIBLE);
            edtForgotEmail.requestFocus();
        } else if (id == R.id.btnCancel) {
            isForgotPassClicked = false;
            btnForgotPass.setVisibility(View.VISIBLE);
            rlLogin.setVisibility(View.VISIBLE);
            rlForgotPassword.setVisibility(View.GONE);
            edtEmail.requestFocus();
        } else if (id == R.id.btnForgot) {
            if (isForgotFieldsValid()) {

                rlContent.setVisibility(View.GONE);
                rlSplashScreen.setVisibility(View.VISIBLE);

                request = FORGOT_REQUEST;
                RequestParams params = new RequestParams();
                params.put("user_email", edtForgotEmail.getText().toString());
                params.put("install_id", Utils.getInstallId(this));

                String userRegisterPath = AppConstants.BASE_URL + "reset api path";
                AppHttpClient.get(userRegisterPath, params, new LoginResponseHandler(mContext));
            }
        }
    }

    private boolean isForgotFieldsValid() {
        boolean isValid = true;
        if (edtForgotEmail.getText().toString().replace(" ", "").equals("")) {
            edtForgotEmail.setError(getString(R.string.hint_enter_email));
            isValid = false;
        }
        return isValid;
    }

    private void processLogin() {
        if (isLoginFieldsValid()) {
            request = LOGIN_REQUEST;
            Utils.setUserRegType(this, 1);
            rlContent.setVisibility(View.GONE);
            rlSplashScreen.setVisibility(View.VISIBLE);

            RequestParams params = Utils.getRequestParamsWithoutSession(mContext);
            params.put("user_email", edtEmail.getText().toString());
            params.put("password", edtPassword.getText().toString());
            params.put("reg_type", 1);
            params.put("install_id", Utils.getInstallId(this));
            params.put("os", "Android");
            params.put("os_ver", Utils.getOsVersion());

            String userRegisterPath = AppConstants.BASE_URL + getString(R.string.userLogin);
            AppHttpClient.get(userRegisterPath, params, new LoginResponseHandler(mContext));
        }
    }

    private boolean isLoginFieldsValid() {
        boolean isValid = true;
        if (edtEmail.getText().toString().replace(" ", "").equals("")) {
            edtEmail.setError(getString(R.string.error_enter_email));
            isValid = false;
        }
        if (edtPassword.getText().toString().replace(" ", "").equals("")) {
            edtPassword.setError(getString(R.string.error_enter_password));
            isValid = false;
        }
        return isValid;
    }


    @Override
    public void onSuccess(JSONObject body) throws JSONException {
        Logger.d(TAG, "success");
        switch (request) {
            case LOGIN_REQUEST:
                int nu = body.getInt("new_user");
                Utils.storeUserVariables(body, this);
                int flagForcePass = body.getInt("force_pass_reset");
                Utils.getSharedPref(this).edit().putInt(AppConstants.PresConstants.PROPERTY_FORCE_RESET_PASS, flagForcePass).apply();
                Intent intent;
                if (flagForcePass == 0) {
                    intent = new Intent(mContext, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                } else {
                    intent = new Intent(mContext, ResetPasswordActivity.class);
                    intent.putExtra("finish_and_continue", 1);
                }
                intent.putExtra("nu", nu);
                Logger.d(TAG, "selected city id " + body.getInt("user_preferred_city_id"));
                Logger.d(TAG, "selected city name " + body.getString("user_preferred_city_name"));
                Utils.getSharedPref(this).edit().putInt(AppConstants.PresConstants.PROPERTY_DEFAULT_CITY_ID, body.getInt("user_preferred_city_id")).apply();
                Utils.getSharedPref(this).edit().putString(AppConstants.PresConstants.PROPERTY_DEFAULT_CITY_NAME, body.getString("user_preferred_city_name")).apply();
                if (nu == 1)
                    intent.putExtra("cities", body.getString("available_cities"));

                startActivity(intent);
                if (flagForcePass == 0)
                    finish();
                break;
            case FORGOT_REQUEST:
                isForgotPassClicked = false;
                rlContent.setVisibility(View.VISIBLE);
                rlSplashScreen.setVisibility(View.GONE);
                Utils.getSharedPref(this).edit().putInt(AppConstants.PresConstants.PROPERTY_FORCE_RESET_PASS, 1).apply();
                String msg = body.getString("msg");
                Utils.showLongToast(mContext, msg);
                rlForgotPassword.setVisibility(View.GONE);
                rlLogin.setVisibility(View.VISIBLE);

                edtEmail.setText(edtForgotEmail.getText().toString());
                edtPassword.requestFocus();

                break;
            default:
                isForgotPassClicked = false;
                break;
        }
    }

    @Override
    public void onMessage(String resp) {
        Logger.d(TAG, "message");
        rlContent.setVisibility(View.VISIBLE);
        rlSplashScreen.setVisibility(View.GONE);

        Utils.showLongToast(this, resp);
    }

    @Override
    public void onFailure(String resp, Throwable throwable) {
        Logger.d(TAG, "failed");
        rlContent.setVisibility(View.VISIBLE);
        rlSplashScreen.setVisibility(View.GONE);

        Utils.showLongToast(this, getString(R.string.toast_socket_timeout_error));
    }

    @Override
    public void onJsonParseError() {

        rlContent.setVisibility(View.VISIBLE);
        rlSplashScreen.setVisibility(View.GONE);

        Utils.showLongToast(this, getString(R.string.json_parser_exception));
    }

    @Override
    public void onBackPressed() {
        if (isForgotPassClicked) {
            btnForgotPass.setVisibility(View.VISIBLE);
            rlLogin.setVisibility(View.VISIBLE);
            rlForgotPassword.setVisibility(View.GONE);
            edtEmail.requestFocus();
            isForgotPassClicked = false;
            return;
        }

        super.onBackPressed();
    }
}
