package my.project.template.http.response;

import android.content.Context;
import com.loopj.android.http.AsyncHttpResponseHandler;
import my.project.template.listener.IHttpResponseListener;
import my.project.template.utils.Logger;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

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
        Logger.d(TAG, new String(responseBody));
        String resp = new String(responseBody);
        if (!resp.equals("")) {
            try {

                JSONObject obj = new JSONObject(resp);
                JSONObject header = obj.getJSONObject("header");
                int status = header.getInt("status");
                String msg = header.getString("msg");
                if (status == 1) {

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
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        String resp = "";
        if (responseBody != null)
            resp = new String(responseBody);
        listener.onFailure(resp, error);
    }

    @Override
    public void onRetry(int retryNo) {
        Logger.d(TAG, "Request is retried, retry no. %d " + retryNo);
    }
}
