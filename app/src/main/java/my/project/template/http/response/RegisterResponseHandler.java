package my.project.template.http.response;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import my.project.template.listener.IHttpResponseListener;
import my.project.template.utils.Logger;

/**
 * @author Devishankar
 */
public class RegisterResponseHandler extends AsyncHttpResponseHandler {
    private static final String TAG = "RegisterResponseHandler";
    private final IHttpResponseListener listener;

    public RegisterResponseHandler(Context context) {
        listener = (IHttpResponseListener) context;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        Logger.d(TAG, "Received response");
        Logger.d(TAG, "status code " + statusCode);
        Logger.d(TAG, "headers " + Arrays.toString(headers));
        String resp = new String(responseBody);
        Logger.json(TAG, "register resp received " + resp);
        if (!resp.equals("")) {
            try {

                JSONObject obj = new JSONObject(resp);
                JSONObject header = obj.getJSONObject("header");
                int status = header.getInt("status");
                String msg = header.getString("msg");
                if (status == 1) {
                    listener.onSuccess(obj.has("body") ? obj.getJSONObject("body") : null);
                } else {
                    listener.onMessage(msg);
                }
            } catch (JSONException e) {
                listener.onJsonParseError();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        String resp = "";
        if (responseBody != null)
            resp = new String(responseBody);
        Logger.json(TAG, "register resp failed " + resp);
        listener.onFailure(resp, error);
    }

    @Override
    public void onRetry(int retryNo) {
        Logger.d(TAG, "Request is retried, retry no. %d " + retryNo);
    }
}
