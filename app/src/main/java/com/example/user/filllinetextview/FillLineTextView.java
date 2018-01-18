package com.example.user.filllinetextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2018-01-18.
 */

public class FillLineTextView extends TextView {

    private boolean mIsSpannable = false;

    public FillLineTextView(Context context) {
        super(context);
        init();
    }

    public FillLineTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FillLineTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text instanceof SpannableString)
            mIsSpannable = true;
        super.setText(text, type);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mIsSpannable) {
            super.onDraw(canvas);
            return;
        }

        Paint paint = getCustomPaint();
        List<String> cutStringList = textToCutStringList(paint);

        float startY = calStartDrawY(cutStringList.size(), paint);

        for (String string : cutStringList) {
            canvas.drawText(string, calStartDrawX(string, paint), startY, paint);
            startY += getLineHeight();
        }
    }

    private Paint getCustomPaint() {
        Paint paint = getPaint();
        paint.setColor(getTextColors().getDefaultColor());
        paint.setTextSize(getTextSize());
        if (getTypeface().getStyle() == Typeface.BOLD)  //bold
            paint.setTypeface(Typeface.DEFAULT_BOLD);
        return paint;
    }

    private List<String> textToCutStringList(Paint paint) {
        ArrayList<String> cutStringList = new ArrayList<>();
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        if (availableWidth <= 0)
            return cutStringList;

        String text = getText().toString();
        int lineCount = getLayout().getLineCount();
        int cutIndex;

        for (int i = 0; i < lineCount; i++) {
            cutIndex = paint.breakText(text, true, availableWidth, null);
            if (cutIndex > 0) {
                if (i == lineCount - 1 && cutIndex < text.length() && getEllipsize() == TextUtils.TruncateAt.END) { // ellipsize
                    cutIndex = paint.breakText(text, true, availableWidth - paint.measureText("\u2026"), null);
                    cutStringList.add(text.substring(0, cutIndex) + "\u2026");
                } else {
                    cutStringList.add(text.substring(0, cutIndex));
                    text = text.substring(cutIndex);
                }
            }
        }
        return cutStringList;
    }

    private float calStartDrawX(String string, Paint paint) {
        if (getGravity() == Gravity.CENTER || getGravity() == Gravity.CENTER_HORIZONTAL) {  //center horizontal
            return (getWidth() - paint.measureText(string) - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
        } else {
            return getPaddingLeft();
        }
    }

    private float calStartDrawY(int lines, Paint paint) {
        if (getGravity() == Gravity.CENTER || getGravity() == Gravity.CENTER_VERTICAL) {    //vertical horizontal
            return (getHeight() - getTextSize() * lines - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop() + Math.abs(paint.getFontMetrics().ascent);
        } else {
            return getPaddingTop() + Math.abs(paint.getFontMetrics().ascent);
        }
    }
}
