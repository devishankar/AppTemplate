package my.project.template.view;

import android.content.Context;
import android.util.AttributeSet;

import com.facebook.login.widget.LoginButton;

import my.project.template.view.helper.FontUtils;

/**
 * @author Devishankar
 */
public class AppFacebookButton extends LoginButton {

    public AppFacebookButton(Context context) {
        super(context);
        if (!isInEditMode())
            FontUtils.applyCustomFont(this, context, null);
    }

    public AppFacebookButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode())
            FontUtils.applyCustomFont(this, context, attrs);
    }

    public AppFacebookButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!isInEditMode())
            FontUtils.applyCustomFont(this, context, attrs);
    }
}