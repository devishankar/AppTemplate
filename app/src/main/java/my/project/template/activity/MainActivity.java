package my.project.template.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.bugsnag.android.Bugsnag;
import com.bugsnag.android.MetaData;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
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

//ref http://stackoverflow.com/questions/29396265/i-need-users-email-address-after-successful-facebook-login-in-android-using-sdk
//ref http://stackoverflow.com/questions/29295987/android-facebook-4-0-sdk-how-to-get-email-date-of-birth-and-gender-of-user
//ref https://www.google.co.in/search?sclient=psy-ab&site=webhp&source=hp&q=FacebookCallback%3CLoginResult%3E%20on%20success%20read%20email&oq=&gs_l=&pbx=1&bav=on.2,or.r_cp.&biw=1366&bih=653&dpr=1&ion=1&ech=1&psi=v08lVo_wBcapuQS-majABg.1445285825500.3&ei=v08lVo_wBcapuQS-majABg&emsg=NCSR&noj=1

public class MainActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        IHttpResponseListener {

    private static final String TAG = "MainActivity";
    private static final String SAVED_PROGRESS = "sign_in_progress";
    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private static final int RC_SIGN_IN = 0;
    private static final int DIALOG_PLAY_SERVICES_ERROR = 0;
    private Context mContext;
    private RelativeLayout rlContent;
    private RelativeLayout rlSplashScreen;
    private AlertDialog dialog;
    private Bundle toPass;
    private int mSignInProgress;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mSignInIntent;
    private int mSignInError;
    private boolean signInClicked = false;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.getFacebookHash(this);
        Bugsnag.init(this);
        MetaData metaData = new MetaData();
        metaData.addToTab("User", "username", "Devishankar");
        metaData.addToTab("User", "email", "devishankargru@gmail.com");
        Bugsnag.notify(new RuntimeException("Non-fatal"), metaData);
        mContext = this;
        toPass = new Bundle();

        if (savedInstanceState != null) {
            mSignInProgress = savedInstanceState
                    .getInt(SAVED_PROGRESS, STATE_DEFAULT);
        }

        if (Utils.getSharedPref(this).getInt(AppConstants.PresConstants.PROPERTY_FORCE_RESET_PASS, 0) == 1) {
            Intent intent = new Intent(this, ResetPasswordActivity.class);
            intent.putExtra("finish_and_continue", 1);
            intent.putExtra("show_back", 0);
            startActivity(intent);
            finish();
            return;
        } else if (Utils.getUserId(mContext) != 0 && !Utils.getLoginSession(mContext).equals("")) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        mGoogleApiClient = buildGoogleApiClient();
        callbackManager = CallbackManager.Factory.create();

        //Button btnFb = (Button) findViewById(R.id.btnFacebook);
        LoginButton btnFacebook = (LoginButton) findViewById(R.id.btnFacebook);
        btnFacebook.setReadPermissions(AppConstants.PERMISSIONS);
        Button btnGoogle = (Button) findViewById(R.id.btnGoogle);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnSignup = (Button) findViewById(R.id.btnSignup);
        rlContent = (RelativeLayout) findViewById(R.id.rlContent);
        rlSplashScreen = (RelativeLayout) findViewById(R.id.rlSplashScreen);

        //btnFb.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);

        btnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Logger.d(TAG, "Login successful");
                initMe(loginResult);
            }

            @Override
            public void onCancel() {
                Logger.d(TAG, "Login canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                Logger.d(TAG, "Login error");
                exception.printStackTrace();
            }
        });

        boolean isLogged = Utils.getSharedPref(mContext).getBoolean(AppConstants.PresConstants.PROPERTY_DEVICE_LOGGED, false);
        if (!isLogged)
            Utils.logDeviceAtFirstLaunch(mContext);

        float fbIconScale = 1.45F;
        Drawable drawable = getResources().getDrawable(
                com.facebook.R.drawable.com_facebook_button_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * fbIconScale),
                (int) (drawable.getIntrinsicHeight() * fbIconScale));
        //btnFacebook.setCompoundDrawables(drawable, null, null, null);
        btnFacebook.setCompoundDrawables(null, null, null, null);
        btnFacebook.setCompoundDrawablePadding(getResources().
                getDimensionPixelSize(R.dimen.fb_margin_override_textpadding));
        btnFacebook.setPadding(
                getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_lr),
                getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_top),
                0,
                getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_bottom));
    }

    private void initMe(LoginResult loginResult) {
        Log.e("onSuccess", "--------" + loginResult.getAccessToken());
        Log.e("Token", "--------" + loginResult.getAccessToken().getToken());
        Log.e("Permision", "--------" + loginResult.getRecentlyGrantedPermissions());

        Log.e("OnGraph", "------------------------");
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        Profile profile = Profile.getCurrentProfile();
                        Log.e("ProfileDataNameF", "--" + profile.getFirstName());
                        Log.e("ProfileDataNameL", "--" + profile.getLastName());

                        Log.e("Image URI", "--" + profile.getLinkUri());
                        Log.e("GraphResponse", "-------------" + response.toString());
                        Logger.d(TAG, "fb email " + object.optString("email"));
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,gender,birthday,email");
        request.setParameters(parameters);
        request.executeAsync();

        /*GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject me, GraphResponse response) {
                        if (response.getError() != null) {
                            // handle error
                        } else {
                            String email = me.optString("email");
                        }
                    }
                }).executeAsync();*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_PROGRESS, mSignInProgress);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RC_SIGN_IN == requestCode) {
            if (resultCode == RESULT_OK) {
                mSignInProgress = STATE_SIGN_IN;
            } else {
                mSignInProgress = STATE_DEFAULT;
            }

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            /*case R.id.btnFacebook:
                //setUpFacebook();
                break;*/
            case R.id.btnGoogle:
                if (!mGoogleApiClient.isConnecting()) {
                    Logger.d(TAG, "Signing in with g+");
                    resolveSignInError();
                    break;
                }
                break;
            case R.id.btnLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.btnSignup:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }

    /*private boolean setUpFacebook() {
        if (Session.getActiveSession() == null ||
                !Session.getActiveSession().isOpened()) {
            Session.openActiveSession(
                    this,
                    true,
                    AppConstants.PERMISSIONS,
                    new Session.StatusCallback() {
                        @Override
                        public void call(Session session, SessionState state, Exception exception) {
                            onSessionStateChanged(session, state, exception);
                        }
                    });
            return false;
        }
        return true;
    }*/

    private void resolveSignInError() {
        if (mSignInIntent != null) {
            try {
                mSignInProgress = STATE_IN_PROGRESS;
                startIntentSenderForResult(mSignInIntent.getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.i(TAG, "Sign in intent could not be sent: "
                        + e.getLocalizedMessage());
                mSignInProgress = STATE_SIGN_IN;
                mGoogleApiClient.connect();
            }
        } else {
            showDialog(DIALOG_PLAY_SERVICES_ERROR);
        }
    }


    //region INFO facebook login methods starts
    /*private void onSessionStateChanged(final Session session, SessionState state, Exception exception) {
        if (state.isOpened() && !sessionHasNecessaryPerms(session)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.need_perms_alert_text);
            builder.setPositiveButton(
                    R.string.need_perms_alert_button_ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            session.requestNewReadPermissions(
                                    new Session.NewPermissionsRequest(
                                            MainActivity.this,
                                            getMissingPermissions(session)));
                        }
                    });
            builder.setNegativeButton(
                    R.string.need_perms_alert_button_quit,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
        } else if (state.isOpened()) {
            Logger.d(TAG, "session opened");
            initMeRequest(session);
        }
    }

    private void initMeRequest(final Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(final GraphUser user, Response response) {
                Logger.d(TAG, "facebook response raw " + response.getRawResponse());
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                        Utils.setUserRegType(mContext, 2);
                        Utils.setFbId(mContext, user.getId());
                        rlContent.setVisibility(View.GONE);
                        rlSplashScreen.setVisibility(View.VISIBLE);
                        if (user.asMap().get("email") == null) {
                            final LinearLayout ll = new LinearLayout(mContext);
                            ll.setOrientation(LinearLayout.VERTICAL);
                            final EditText email = new EditText(mContext);
                            email.setText("");
                            email.setHint(getString(R.string.hint_enter_email));
                            email.setHintTextColor(getResources().getColor(R.color.textHintColor));
                            email.setInputType(InputType.TYPE_CLASS_TEXT);
                            email.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

                            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            nameParams.gravity = Gravity.CENTER;
                            nameParams.setMargins(24, 24, 24, 16);
                            email.setLayoutParams(nameParams);
                            ll.addView(email);

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(getString(R.string.msg_box_title_add_email));
                            builder.setView(ll);
                            builder.setPositiveButton("Add", null);
                            builder.setNegativeButton("Cancel", null);
                            dialog = builder.create();
                            dialog.show();

                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String emailStr = email.getText().toString().trim();

                                    if (!TextUtils.isEmpty(emailStr)) {
                                        finishFbLogin(user, emailStr);
                                        dialog.cancel();
                                    } else {
                                        if (TextUtils.isEmpty(emailStr))
                                            email.setError(getString(R.string.error_email_empty));
                                    }
                                }
                            });
                            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.cancel();
                                    logOut();
                                }
                            });
                        } else {
                            Logger.d(TAG, "user email " + user.asMap().get("email").toString());
                            finishFbLogin(user, user.asMap().get("email").toString());
                        }

                    }
                }
                if (response.getError() != null) {
                    handleError(response.getError());
                }
            }
        });
        request.executeAsync();
    }

    private void finishFbLogin(GraphUser user, String email) {

        Logger.d(TAG, "user fb id " + user.getId());
        Logger.d(TAG, "user first name " + user.getFirstName());
        Logger.d(TAG, "user last name " + user.getLastName());
        Logger.d(TAG, "user DOB " + user.getBirthday());

        RequestParams params = Utils.getRequestParams(mContext);
        params.put("email", email);
        params.put("name", user.getName());
        params.put("fname", user.getFirstName());
        params.put("lname", user.getLastName());
        params.put("fb_id", user.getId());
        params.put("reg_type", 2);
        params.put("flag_android", 1);
        params.put("os", "Android");
        params.put("os_ver", Utils.getOsVersion());
        params.put("app_src", AppConstants.APP_SRC);

        toPass.putString("profile_image", user.getId());

        AppHttpClient.get(AppConstants.APP_SRC, params, new LoginResponseHandler(mContext));
    }

    private boolean sessionHasNecessaryPerms(Session session) {
        if (session != null && session.getPermissions() != null) {
            for (String requestedPerm : AppConstants.PERMISSIONS) {
                if (!session.getPermissions().contains(requestedPerm)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private List<String> getMissingPermissions(Session session) {
        List<String> missingPerms = new ArrayList<>(AppConstants.PERMISSIONS);
        if (session != null && session.getPermissions() != null) {
            for (String requestedPerm : AppConstants.PERMISSIONS) {
                if (session.getPermissions().contains(requestedPerm)) {
                    missingPerms.remove(requestedPerm);
                }
            }
        }
        return missingPerms;
    }

    private void handleError(FacebookRequestError error) {
        DialogInterface.OnClickListener listener = null;
        String dialogBody;

        if (error == null) {
            dialogBody = getString(R.string.msg_btn_error_dialog_default_text);
        } else {
            switch (error.getCategory()) {
                case AUTHENTICATION_RETRY:
                    // tell the user what happened by getting the message id, and
                    // retry the operation later
                    String userAction = (error.shouldNotifyUser()) ? "" :
                            getString(error.getUserActionMessageId());
                    dialogBody = getString(R.string.error_authentication_retry, userAction);
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, AppConstants.M_FACEBOOK_URL);
                            startActivity(intent);
                        }
                    };
                    break;

                case AUTHENTICATION_REOPEN_SESSION:
                    // close the session and reopen it.
                    dialogBody = getString(R.string.error_authentication_reopen);
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Session session = Session.getActiveSession();
                            if (session != null && !session.isClosed()) {
                                session.closeAndClearTokenInformation();
                            }
                        }
                    };
                    break;

                case PERMISSION:
                    // request the publish permission
                    dialogBody = getString(R.string.error_permission);
                    break;

                case SERVER:
                case THROTTLING:
                    // this is usually temporary, don't clear the fields, and
                    // ask the user to try again
                    dialogBody = getString(R.string.error_server);
                    break;

                case BAD_REQUEST:
                    // this is likely a coding error, ask the user to file a bug
                    dialogBody = getString(R.string.error_bad_request, error.getErrorMessage());
                    break;

                case OTHER:
                case CLIENT:
                default:
                    // an unknown issue occurred, this could be a code error, or
                    // a server side issue, log the issue, and either ask the
                    // user to retry, or file a bug
                    dialogBody = getString(R.string.error_unknown, error.getErrorMessage());
                    break;
            }
        }

        String title = "";
        String message = "";
        if (error != null) {
            title = error.getErrorUserTitle();
            message = error.getErrorUserMessage();
        }
        if (message == null) {
            message = dialogBody;
        }
        if (title == null) {
            title = getResources().getString(R.string.msg_btn_error_dialog_title);
        }

        new AlertDialog.Builder(getApplicationContext())
                .setPositiveButton(R.string.msg_btn_error_dialog_button_text, listener)
                .setTitle(title)
                .setMessage(message)
                .create();
    }*/
    //endregion

    /*INFO google+ login methods starts*/
    private GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected");
        Utils.setUserRegType(mContext, 3);
        rlContent.setVisibility(View.GONE);
        rlSplashScreen.setVisibility(View.VISIBLE);
        signInClicked = false;
        Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        String googleProfileUri = currentUser.getImage().getUrl();
        googleProfileUri = googleProfileUri.replace("?sz=50", "?sz=400");
        Utils.setGooglePlusUri(mContext, googleProfileUri);

        Logger.d(TAG, email + " " + currentUser.getDisplayName() + " " + currentUser.getId() + " " +
                currentUser.getGender() + " " + currentUser.getImage().getUrl() + " " + googleProfileUri);
        RequestParams params = new RequestParams();
        params.put("name", currentUser.getDisplayName());
        params.put("email", email);
        params.put("fname", currentUser.getDisplayName());
        params.put("lname", "");
        params.put("google_id", currentUser.getId());
        params.put("gender", currentUser.getGender());
        params.put("reg_type", 3);
        params.put("flag_android", 1);
        params.put("os", "Android");
        params.put("os_ver", Utils.getOsVersion());
        params.put("app_src", AppConstants.APP_SRC);

        AppHttpClient.get(AppConstants.APP_SRC, params, new LoginResponseHandler(mContext));

        mSignInProgress = STATE_DEFAULT;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());

        if (mSignInProgress != STATE_IN_PROGRESS) {
            mSignInIntent = result.getResolution();
            mSignInError = result.getErrorCode();

            if (mSignInProgress == STATE_SIGN_IN) {
                resolveSignInError();
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_PLAY_SERVICES_ERROR:
                if (GooglePlayServicesUtil.isUserRecoverableError(mSignInError)) {
                    return GooglePlayServicesUtil.getErrorDialog(
                            mSignInError,
                            this,
                            RC_SIGN_IN,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    Log.e(TAG, "Google Play services resolution cancelled");
                                    mSignInProgress = STATE_DEFAULT;
                                    Logger.d(TAG, "Signed out");
                                }
                            });
                } else {
                    return new AlertDialog.Builder(this)
                            .setMessage(R.string.play_services_error)
                            .setPositiveButton(R.string.msg_btn_close,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.e(TAG, "Google Play services error could not be "
                                                    + "resolved: " + mSignInError);
                                            mSignInProgress = STATE_DEFAULT;
                                            Logger.d(TAG, "Signed out");
                                        }
                                    }).create();
                }
            default:
                return super.onCreateDialog(id);
        }
    }
    /*INFO google+ login methods ends*/

    @Override
    public void onSuccess(JSONObject body) throws JSONException {
        Logger.d(TAG, "success");
        int nu = body.getInt("new_user");
        Utils.storeUserVariables(body, this);
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("nu", nu);

    }

    @Override
    public void onMessage(String resp) {
        Logger.d(TAG, "message");
        Utils.showLongToast(this, resp);
        rlContent.setVisibility(View.VISIBLE);
        rlSplashScreen.setVisibility(View.GONE);
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
}
