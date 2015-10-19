package my.project.template.http.response;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

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
        Logger.d(TAG, "api resp received" + resp);
        if (!resp.equals("")) {
            try {

                JSONObject obj = new JSONObject(resp);
                JSONObject header = obj.getJSONObject("header");
                int status = header.getInt("status");
                String msg = header.getString("msg");
                if (status == 1) {
                    Logger.d(TAG, "status success");

                    listener.onSuccess(obj.getJSONObject("body"));
                } else {
                    listener.onMessage(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
        String resp = "";
        Logger.d(TAG, "api request failed");
        if (responseBody != null)
            resp = new String(responseBody);
        listener.onFailure(resp, error);
    }
}
