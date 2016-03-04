package my.project.template.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import my.project.template.view.helper.FontUtils;


/**
 * @author Devishankar
 */
public class AppButton extends Button {

    public AppButton(Context context) {
        super(context);
        if (!isInEditMode())
            FontUtils.applyCustomFont(this, context, null);
    }

    public AppButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())
            FontUtils.applyCustomFont(this, context, attrs);
    }

    public AppButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode())
            FontUtils.applyCustomFont(this, context, attrs);
    }
}