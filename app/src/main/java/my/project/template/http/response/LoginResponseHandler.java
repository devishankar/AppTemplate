package my.project.template.http.response;

import android.content.Context;
import com.loopj.android.http.AsyncHttpResponseHandler;
import my.project.template.listener.IHttpResponseListener;
import my.project.template.utils.Logger;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Devishankar
 */
public class LoginResponseHandler extends AsyncHttpResponseHandler {
    private static final String TAG = "LoginResponseHandler";
    private final IHttpResponseListener listener;

    public LoginResponseHandler(Context context) {
        listener = (IHttpResponseListener) context;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        String resp = new String(responseBody);
        Logger.d(TAG, "login resp received" + resp);
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
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        String resp = "";
        Logger.d(TAG, "login request failed");
        if (responseBody != null)
            resp = new String(responseBody);
        listener.onFailure(resp, error);
    }
}
