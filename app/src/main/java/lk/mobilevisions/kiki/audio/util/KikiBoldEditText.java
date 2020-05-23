package lk.mobilevisions.kiki.audio.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

public class KikiBoldEditText extends AppCompatEditText {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";
    Paint.FontMetricsInt fontMetricsInt;

    public KikiBoldEditText(Context context) {
        super(context);

        applyCustomFont(context, null);
    }

    public KikiBoldEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context, attrs);
    }

    public KikiBoldEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.BOLD);

        Typeface customFont = selectTypeface(context, textStyle);
        setTypeface(customFont);
    }

    private Typeface selectTypeface(Context context, int textStyle) {
        /*
         * information about the TextView textStyle:
         * http://developer.android.com/reference/android/R.styleable.html#TextView_textStyle
         */


        return FontCache.getTypeface("Roboto-Bold.ttf", context);
//        return FontCache.getTypeface("FuturaNext-Medium.otf", context);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //   if (adjustTopForAscent) {
        if (fontMetricsInt == null) {
            fontMetricsInt = new Paint.FontMetricsInt();
            getPaint().getFontMetricsInt(fontMetricsInt);
        }
        canvas.translate(0, fontMetricsInt.top - fontMetricsInt.ascent);
        // }
        super.onDraw(canvas);
    }
}
