package com.geekwims.hereiam.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.geekwims.hereiam.constant.Font;


public class FontText extends TextView {
    public FontText(Context context) {
        super(context);
        setType(context);
    }

    public FontText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setType(context);
    }

    public FontText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setType(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FontText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setType(context);
    }

    private void setType(Context context) {
        this.setTypeface(Font.getTypeface(context));
    }
}
