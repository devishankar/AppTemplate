package my.project.template.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author Devishankar
 */
public class AppHttpClient {


    private static final String TAG = "AppHttpClient";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Logger.d(TAG, "requested url " + AsyncHttpClient.getUrlWithQueryString(true, url, params));
        client.get(url, params, responseHandler);
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(url, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Logger.d(TAG, "requested url " + AsyncHttpClient.getUrlWithQueryString(true, url, params));
        client.setURLEncodingEnabled(true);
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return AppConstants.BASE_URL;
    }
}

