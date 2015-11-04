package my.project.template.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.facebook.login.LoginManager;

import my.project.template.utils.AppConstants;
import my.project.template.utils.Logger;
import my.project.template.utils.Utils;

/**
 * @author Devishankar
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setTitle(Toolbar toolbar, int titleId) {
        setTitle(toolbar, getString(titleId));
    }

    void setTitle(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }

    protected void logOut() {
        SharedPreferences pref = Utils.getSharedPref(this);
        pref.edit().putString(AppConstants.PresConstants.PROPERTY_INSTALL_ID, "").apply();
        pref.edit().putString(AppConstants.PresConstants.PROPERTY_LOGIN_SESSION, "").apply();
        pref.edit().putLong(AppConstants.PresConstants.PROPERTY_USER_ID, 0).apply();

        int regType = pref.getInt(AppConstants.PresConstants.PROPERTY_USER_REG_TYPE, 0);
        Logger.d(TAG, "reg type " + regType);
        Intent intent = new Intent(this, MainActivity.class);
        if (regType == 2) {
            LoginManager.getInstance().logOut();
        }

        clearUserVariables();

        startActivity(intent);
        finish();
    }

    private void clearUserVariables() {
        SharedPreferences pref = Utils.getSharedPref(this);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(AppConstants.PresConstants.PROPERTY_INSTALL_ID, "");
        editor.putString(AppConstants.PresConstants.PROPERTY_LOGIN_SESSION, "");
        editor.putLong(AppConstants.PresConstants.PROPERTY_USER_ID, 0);
        editor.putInt(AppConstants.PresConstants.PROPERTY_USER_REG_TYPE, 0);
        editor.putString(AppConstants.PresConstants.PROPERTY_USER_FB_ID, "");
        editor.putString(AppConstants.PresConstants.PROPERTY_USER_EMAIL, "");
        editor.putString(AppConstants.PresConstants.PROPERTY_USER_FNAME, "");
        editor.putString(AppConstants.PresConstants.PROPERTY_USER_LNAME, "");
        editor.apply();
    }

    public void logLocalyticsEvent() {

    }

}
