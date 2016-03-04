package my.project.template.http.response;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import my.project.template.listener.IHttpResponseListener;
import my.project.template.utils.Logger;


/**
 * @author Devishankar
 */
public class ApiResponseHandler extends AsyncHttpResponseHandler {
    private static final String TAG = "ApiResponseHandler";
    private final IHttpResponseListener listener;

    public ApiResponseHandler(Context context) {
        listener = (IHttpResponseListener) context;
    }

    @Override
    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
        String resp = new String(responseBody);
        Logger.i(TAG, "Received response");
        Logger.i(TAG, "status code " + statusCode);
        Logger.i(TAG, "headers " + Arrays.toString(headers));
        Logger.json(TAG, "api resp received " + resp);
        if (!resp.equals("")) {
            try {
                JSONObject obj = new JSONObject(resp);
                JSONObject header = obj.getJSONObject("header");
                int status = header.optInt("status");
                String msg = header.optString("msg");
                if (status == 1) {
                    Logger.i(TAG, "status success");
                    listener.onSuccess(obj.getJSONObject("body"));
                    if (!msg.equals("")) {
                        Logger.i(TAG, "Got msg with status code 1");
                        listener.onMessage(header.optString("msg"));
                    }
                } else {
                    Logger.w(TAG, "Json with status code 0 received");
                    listener.onMessage(msg);
                }
            } catch (JSONException e) {
                Logger.w(TAG, "Failed to parse json");
                Logger.getStackTraceString(e);
                listener.onJsonParseError();
                e.printStackTrace();
            }
        } else {
            Logger.w(TAG, "Response is empty in api request");
            listener.onMessage("Something unexpected happened, please try again!");
        }
    }

    @Override
    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
        String resp = "";
        Logger.d(TAG, "api request failed");
        if (responseBody != null)
            resp = new String(responseBody);
        Logger.json(TAG, "api resp failed " + resp);
        listener.onFailure(resp, error);
    }

    @Override
    public void onRetry(int retryNo) {
        Logger.d(TAG, "Request is retried, retry no. %d " + retryNo);
    }
}
