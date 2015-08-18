package my.project.template.http.request;

import android.content.Context;
import com.loopj.android.http.RequestParams;
import my.project.template.http.response.LoginResponseHandler;
import my.project.template.utils.AppConstants;
import my.project.template.utils.AppHttpClient;


/**
 * @author Devishankar
 */
public class LoginRequestHandler {
    public LoginRequestHandler(Context context, RequestParams params) {

        AppHttpClient.get(AppConstants.APP_SRC, params, new LoginResponseHandler(context));
    }
}
