package my.project.template.utils;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.bugsnag.android.Bugsnag;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import my.project.template.R;

/**
 * @author Devishankar
 */
public class Utils {
    public static final StringBuilder sb = new StringBuilder();
    public static final double MIN = 60 * 1000.0;
    public static final double HOUR = 60 * MIN;
    public static final double DAY = 24 * HOUR;
    public static final double MONTH = 30 * DAY;
    public static final double YEAR = 365 * DAY;
    private static final String TAG = "Utils";
    private static RequestParams requestParams;
    private static ProgressDialog dialog;
    private static boolean isLoading = false;
    private static int gadgetCount;

    public static void showLoadingDialog(Context context) {
        try {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
            isLoading = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isIsLoading() {
        return isLoading;
    }

    public static void dismissLoadingDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                isLoading = false;
                dialog.dismiss();
            }
        } catch (Exception e) {
            Logger.d(TAG, "Exception when closing dialogue");
        }
    }

    public static void getFacebookHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            assert info.signatures != null;
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Logger.d("facebook Your Tag", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException ignored) {

        }
    }

    public static String getInstallId(Context context) {
        SharedPreferences prefs = getSharedPref(context);
        boolean firstTime = prefs.getBoolean(AppConstants.PresConstants.PROPERTY_FIRST_TIME_USER, true);
        String uniqueId = prefs.getString(AppConstants.PresConstants.PROPERTY_INSTALL_ID, "");
        Log.e(TAG, "first time " + firstTime);

        if (uniqueId.equals("")) {
            uniqueId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            //uniqueId = UUID.randomUUID().toString();
            SharedPreferences.Editor mEditor = prefs.edit();
            mEditor.putBoolean(AppConstants.PresConstants.PROPERTY_FIRST_TIME_USER, false);
            mEditor.putString(AppConstants.PresConstants.PROPERTY_INSTALL_ID, uniqueId);
            mEditor.apply();
        }

        Log.d(TAG, "Install ID " + uniqueId);
        return uniqueId;
    }

    public static String concat(Object... objects) {
        sb.setLength(0);
        for (Object obj : objects) {
            sb.append(obj);
        }
        return sb.toString();
    }

    public static String getLoginSession(Context context) {
        return getSharedPref(context).getString(AppConstants.PresConstants.PROPERTY_LOGIN_SESSION, "");
    }

    public static String getOsVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static boolean isAppOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }


    //info http://android-developers.blogspot.in/2015/09/google-play-services-81-and-android-60.html
    public static Location getLastKnownLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                //return TODO;
                return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        return null;

    }

    public static String getPostalCodeFromLocation(Context context) {
        Location loc = Utils.getLastKnownLocation(context);
        if (loc != null) {
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    Logger.d(TAG, "postal code " + addresses.get(0).getPostalCode());
                    return addresses.get(0).getPostalCode();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String getSessionLocParams(Context context) {
        Location location = Utils.getLastKnownLocation(context);
        String string;

        String installId = Utils.getInstallId(context);
        String loginSession = Utils.getLoginSession(context);
        string = String.format(context.getResources().getString(R.string.sessionData),
                installId, loginSession);

        if (location != null) {
            string += String.format(context.getResources().getString(R.string.locationData),
                    location.getLatitude(), location.getLongitude());
        }

        return string;
    }

    public static RequestParams getRequestParams(Context context) {
        return getRequestParams(context, new HashMap<String, String>());
    }

    public static RequestParams getRequestParams(Context context, HashMap<String, String> map) {
        map.put("install_id", getInstallId(context));
        map.put("ls", getLoginSession(context));

        Location location = Utils.getLastKnownLocation(context);
        if (location != null) {
            map.put("user_lat", String.valueOf(location.getLatitude()));
            map.put("user_long", String.valueOf(location.getLongitude()));
        }

        map.put("flag_android", "1");
        map.put("os", "Android");
        map.put("os_ver", Utils.getOsVersion());
        map.put("app_src", AppConstants.APP_SRC);

        return new RequestParams(map);
    }

    public static RequestParams getRequestParamsWithoutSession(Context context) {
        return getRequestParamsWithoutSession(context, new HashMap<String, String>());
    }

    public static RequestParams getRequestParamsWithoutSession(Context context, HashMap<String, String> map) {
        map.put("install_id", getInstallId(context));

        Location location = Utils.getLastKnownLocation(context);
        if (location != null) {
            map.put("user_lat", String.valueOf(location.getLatitude()));
            map.put("user_long", String.valueOf(location.getLongitude()));
        }

        map.put("flag_android", "1");
        map.put("os", "Android");
        map.put("os_ver", Utils.getOsVersion());
        map.put("app_src", AppConstants.APP_SRC);
        return new RequestParams(map);
    }

    public static void showLongToast(Context context, String resp) {
        Toast.makeText(context,
                resp,
                Toast.LENGTH_SHORT)
                .show();
    }

    public static SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences(AppConstants.PresConstants.PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putString(AppConstants.PresConstants.PROPERTY_REG_ID, regId);
        editor.putInt(AppConstants.PresConstants.PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPref(context);
        String registrationId = prefs.getString(AppConstants.PresConstants.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(AppConstants.PresConstants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void launchMarket(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            //Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    public static void registerInBackground(final GoogleCloudMessaging gcm, final Context context) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    String mRegId = gcm.register(context.getResources().getString(R.string.google_project_number));
                    msg = "Device registered, registration ID=" + mRegId;

                    sendRegistrationIdToBackend(context, mRegId);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, "msg " + msg + "\n");
            }
        }.execute(null, null, null);
    }

    public static void sendRegistrationIdToBackend(Context context, String regId) {
        String result = "";
        HttpClient client = new DefaultHttpClient();

        String postData = String.format(context.getString(R.string.setGcmRegId), regId);
        String requestUrl = String.format(context.getResources().getString(R.string.setData),
                postData, Utils.getSessionLocParams(context));

        requestUrl = context.getResources().getString(R.string.baseUrl) + requestUrl;
        Logger.d(TAG, "set seat arrangements req " + requestUrl);

        Log.d(TAG, "gcm reg req " + requestUrl);
        HttpGet get = new HttpGet(requestUrl);
        HttpResponse response;
        try {
            response = client.execute(get);
            Log.i(TAG, response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inStream = entity.getContent();
                result = Utils.convertStreamToString(inStream);
                Log.d(TAG, "gcm reg resp " + result);
                inStream.close();
                storeRegistrationId(context, regId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convertStreamToString(InputStream instream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                instream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public static String urlEncoding(String request) {

        String url_res = "";
        try {
            String[] temp_str = null;
            String[] req_data = request.split("&");
            for (String aReq_data : req_data) {
                temp_str = aReq_data.split("=");
                url_res += URLEncoder.encode(temp_str[0], "UTF-8") + "=";
                if (temp_str.length > 1) {
                    if (temp_str[1] != null && (!temp_str[1].equals("")))
                        url_res += URLEncoder.encode(temp_str[1], "UTF-8");
                }

                url_res += "&";
            }
            url_res = url_res.substring(0, url_res.length() - 1);
        } catch (Exception e) {
            Logger.e(TAG, "Error in urlEncoding: " + e.toString());
        }
        return url_res;
    }

    public static void storeUserVariables(JSONObject body, Context context) throws JSONException {
        long userId = body.getLong("user_id");
        String ls = body.getString("login_session");
        String userEmail = body.getString("user_email");
        String userFname = body.getString("user_fname");
        String userLname = body.getString("user_lname");
        Bugsnag.setUser(String.valueOf(userId), userEmail, userFname);

        getSharedPref(context).edit().putString(AppConstants.PresConstants.PROPERTY_LOGIN_SESSION, ls).apply();
        getSharedPref(context).edit().putLong(AppConstants.PresConstants.PROPERTY_USER_ID, userId).apply();
        getSharedPref(context).edit().putString(AppConstants.PresConstants.PROPERTY_USER_EMAIL, userEmail).apply();
        getSharedPref(context).edit().putString(AppConstants.PresConstants.PROPERTY_USER_FNAME, userFname).apply();
        getSharedPref(context).edit().putString(AppConstants.PresConstants.PROPERTY_USER_LNAME, userLname).apply();
    }

    public static long getUserId(Context context) {
        return getSharedPref(context).getLong(AppConstants.PresConstants.PROPERTY_USER_ID, 0);
    }

    public static void setUserRegType(Context context, int regType) {
        getSharedPref(context).edit().putInt(AppConstants.PresConstants.PROPERTY_USER_REG_TYPE, regType).apply();
    }

    public static int getUserRegType(Context context) {
        return getSharedPref(context).getInt(AppConstants.PresConstants.PROPERTY_USER_REG_TYPE, 0);
    }

    public static boolean isDeviceSupportCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static void setFbId(Context context, String id) {
        getSharedPref(context).edit().putString(AppConstants.PresConstants.PROPERTY_USER_FB_ID, id).apply();
    }

    public static String getFbId(Context context) {
        return getSharedPref(context).getString(AppConstants.PresConstants.PROPERTY_USER_FB_ID, "");
    }

    public static String getUserName(Context context) {
        return getSharedPref(context).getString(AppConstants.PresConstants.PROPERTY_USER_FNAME, "");
    }

    public static String getUserEmail(Context context) {
        return getSharedPref(context).getString(AppConstants.PresConstants.PROPERTY_USER_EMAIL, "");
    }

    public static void dialPhone(Context context, String number) {
        Intent dial = new Intent();
        dial.setAction("android.intent.action.DIAL");
        dial.setData(Uri.parse("tel:" + number));
        context.startActivity(dial);
    }

    public static void startPlayStore(Context context) {
        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static int getGadgetCount() {
        return gadgetCount;
    }

    public static void setGadgetCount(int gadgetCount) {
        Utils.gadgetCount = gadgetCount;
    }

    public static void setUserName(Context context, String newValue) {
        getSharedPref(context).edit().putString(AppConstants.PresConstants.PROPERTY_USER_FNAME, newValue).apply();
    }


    public static void setGooglePlusUri(Context context, String uri) {
        getSharedPref(context).edit().putString(AppConstants.PresConstants.PROPERTY_GOOGLE_PROFILE, uri).apply();
    }

    public static String getGooglePlusUri(Context context) {
        return getSharedPref(context).getString(AppConstants.PresConstants.PROPERTY_GOOGLE_PROFILE, "");
    }

    public static void showProgress(final SwipeRefreshLayout container) {
        if (container != null && !container.isRefreshing()) {
            container.post(new Runnable() {
                @Override
                public void run() {
                    container.setRefreshing(true);
                }
            });
        }
    }

    public static void hideProgress(final SwipeRefreshLayout container) {
        if (container != null && container.isRefreshing()) {
            container.setRefreshing(false);
        }
    }

    public static void logDeviceAtFirstLaunch(final Context context) {
        RequestParams params = Utils.getRequestParamsWithoutSession(context);
        params.put("action", "log_user_at_first_launch");

        String userRegisterPath = AppConstants.BASE_URL + "logDevice?";
        AppHttpClient.get(userRegisterPath, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Utils.getSharedPref(context).edit().putBoolean(AppConstants.PresConstants.PROPERTY_DEVICE_LOGGED, true).apply();
                Logger.d(TAG, "Device logged successfully");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Logger.d(TAG, "Device logged successfully");
            }
        });
    }
}

