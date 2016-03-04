package my.project.template.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import my.project.template.view.helper.FontUtils;


/**
 * @author Devishankar
 */
public class AppEditText extends EditText {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public AppEditText(Context context) {
        super(context);

        if (!isInEditMode())
            FontUtils.applyCustomFont(this, context, null);
    }

    public AppEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode())
            FontUtils.applyCustomFont(this, context, attrs);
    }

    public AppEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!isInEditMode())
            FontUtils.applyCustomFont(this, context, attrs);
    }
}