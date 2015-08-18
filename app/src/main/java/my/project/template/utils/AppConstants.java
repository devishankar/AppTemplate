package my.project.template.utils;


import android.net.Uri;
import my.project.template.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Devishankar
 */
public class AppConstants {
    public static final boolean DEVELOPER_MODE = true;
    public static final String APP_SRC = (DEVELOPER_MODE) ? "DEV" : "PROD";
    public static final String BASE_URL = "http://www.domain.com/";

    public static final String DEFAULT_PRODUCT_ICON = "drawable://" + R.mipmap.ic_launcher;
    public static final int CUSTOM_POSTAL_CODE_LENGTH = 6;
    public static final Uri M_FACEBOOK_URL = Uri.parse("http://m.facebook.com");

    public static final List<String> PERMISSIONS = new ArrayList<String>() {
        {
            add("user_friends");
            add("public_profile");
            add("email");
        }
    };
    public static final String COMPANY = "company name";

    public static class PresConstants {
        public static final String PREF_FILE_NAME = "APPLICATION_PREF";
        public static final String PROPERTY_REG_ID = "NOTIFICATION_REG_ID";
        public static final String PROPERTY_FIRST_TIME_USER = "FIRST_TIME_USER";
        public static final String PROPERTY_USER_LEARNED_DRAWER = "DRAWER_LEARNED";
        public static final String PROPERTY_APP_VERSION = "APP_VER";
        public static final String PROPERTY_INSTALL_ID = "INSTALL_ID";
        public static final String PROPERTY_LOGIN_SESSION = "LS";
        public static final String PROPERTY_USER_ID = "USER_ID";
        public static final String PROPERTY_USER_REG_TYPE = "REG_TYPE";
        public static final String PROPERTY_USER_FB_ID = "FB_ID";
        public static final String PROPERTY_USER_EMAIL = "USER_EMAIL";
        public static final String PROPERTY_USER_FNAME = "USER_FNAME";
        public static final String PROPERTY_USER_LNAME = "USER_LNAME";
        public static final String PROPERTY_GOOGLE_PROFILE = "GOOGLE_PLUS_PROFILE_URI";
        public static final String PROPERTY_FORCE_RESET_PASS = "FORCE_RESET_PASSWORD";
        public static final String PROPERTY_DEFAULT_CITY_ID = "USER_DEFAULT_CITY_ID";
        public static final String PROPERTY_DEFAULT_CITY_NAME = "USER_DEFAULT_CITY_NAME";
    }
}
