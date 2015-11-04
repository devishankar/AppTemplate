package my.project.template.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import my.project.template.R;
import my.project.template.utils.FontCache;

/**
 * @author Devishankar
 */
public class AppTextView extends TextView {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public AppTextView(Context context) {
        super(context);

        applyCustomFont(context, null);
    }

    public AppTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context, attrs);
    }

    public AppTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context, attrs);
    }

    /*
    * normal	0
    * bold	1
    * italic	2
    * */
    private void applyCustomFont(Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.AppTextView);

        String fontName = attributeArray.getString(R.styleable.AppTextView_font);
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", 0);

        Typeface customFont = selectTypeface(context, fontName, textStyle);
        setTypeface(customFont);

        attributeArray.recycle();
    }

    /*
         information about the TextView textStyle:
         http://developer.android.com/reference/android/R.styleable.html#TextView_textStyle
         */
    private Typeface selectTypeface(Context context, String fontName, int textStyle) {
        /*if (fontName.contentEquals(context.getString(R.string.font_name_fontawesome))) {
            return FontCache.getTypeface("fontawesome.ttf", context);
        } else*/
        if (fontName.contentEquals(context.getString(R.string.font_name_source_sans_pro))) {
            switch (textStyle) {
                case 1: // bold
                    return FontCache.getTypeface("SourceSansPro-Bold.ttf", context);

                case 2: // italic
                    return FontCache.getTypeface("SourceSansPro-Italic.ttf", context);

                case 0: // regular
                default:
                    return FontCache.getTypeface("SourceSansPro-Regular.ttf", context);
            }
        } else {
            // no matching font found return null so Android just uses the standard font (Roboto)
            return null;
        }
    }
}
