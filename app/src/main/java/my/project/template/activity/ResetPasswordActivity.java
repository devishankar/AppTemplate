package my.project.template.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.loopj.android.http.RequestParams;
import my.project.template.R;
import my.project.template.http.response.ApiResponseHandler;
import my.project.template.listener.IHttpResponseListener;
import my.project.template.utils.AppConstants;
import my.project.template.utils.AppHttpClient;
import my.project.template.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

public class ResetPasswordActivity extends BaseActivity
        implements View.OnClickListener, IHttpResponseListener {

    private Context mContext;
    private ProgressBar pbToolbar;
    private EditText txtCurrentPassword;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private String strPassword;
    private Intent extras;
    private int flagFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mContext = this;
        pbToolbar = (ProgressBar) findViewById(R.id.pbToolbar);
        extras = getIntent();
        flagFinish = extras.getIntExtra("finish_and_continue", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        int str = R.string.title_activity_reset_password;
        if (flagFinish == 0)
            str = R.string.title_activity_change_password;
        setTitle(toolbar, str);

        if (extras.getIntExtra("show_back", 1) == 0 && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }


        txtCurrentPassword = (EditText) findViewById(R.id.txtCurrentPassword);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);
        txtConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updatePassword();
                }
                return false;
            }
        });

        Button btnUpdatePassword = (Button) findViewById(R.id.btnUpdatePassword);
        btnUpdatePassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnUpdatePassword) {
            updatePassword();
        }
    }

    private void updatePassword() {
        if (isPassFieldsValid()) {
            pbToolbar.setVisibility(View.VISIBLE);
            RequestParams params = Utils.getRequestParams(this);
            params.put("action", "setUserPassword");
            params.put("password", strPassword);
            String url = AppConstants.BASE_URL + getString(R.string.setData);
            AppHttpClient.get(url, params, new ApiResponseHandler(mContext));
        }
    }

    private boolean isPassFieldsValid() {
        boolean isValid = true;

        String strCurrentPassword = txtCurrentPassword.getText().toString();
        strPassword = txtPassword.getText().toString();
        String strConfirmPassword = txtConfirmPassword.getText().toString();

        if (strCurrentPassword.replace(" ", "").equals("")) {
            txtCurrentPassword.setError(getString(R.string.error_enter_current_password));
            isValid = false;
        }

        if (strPassword.replace(" ", "").equals("")) {
            txtPassword.setError(getString(R.string.error_enter_new_password));
            isValid = false;
        }

        if (strConfirmPassword.replace(" ", "").equals("")) {
            txtConfirmPassword.setError(getString(R.string.error_re_enter_new_password));
            isValid = false;
        }

        if (!strPassword.equals(""))
            if (strPassword.contains(" ")) {
                txtPassword.setError(getString(R.string.error_found_empty_char));
                isValid = false;
            } else if (!strPassword.equals(strConfirmPassword)) {
                txtConfirmPassword.setError(getString(R.string.error_password_doesnot_match));
                isValid = false;
            }


        return isValid;
    }

    @Override
    public void onSuccess(JSONObject body) throws JSONException {
        String msg = body.getString("msg");
        Utils.showLongToast(mContext, msg);
        Utils.getSharedPref(this).edit().putInt(AppConstants.PresConstants.PROPERTY_FORCE_RESET_PASS, 0).apply();
        pbToolbar.setVisibility(View.GONE);
        if (flagFinish == 1) {
            Intent intent = new Intent(mContext, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtras(extras);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onMessage(String resp) {
        Utils.showLongToast(mContext, resp);
        pbToolbar.setVisibility(View.GONE);

    }

    @Override
    public void onFailure(String resp, Throwable throwable) {
        Utils.showLongToast(this, getString(R.string.toast_socket_timeout_error));
        pbToolbar.setVisibility(View.GONE);
    }
}
