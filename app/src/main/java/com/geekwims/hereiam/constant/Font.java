package com.geekwims.hereiam.constant;

import android.content.Context;
import android.graphics.Typeface;


public class Font {
    private static Typeface typeface;

    public static Typeface getTypeface(Context context) {
        if (typeface == null)
            typeface = Typeface.createFromAsset(context.getAssets(), "Binggrae.otf");

        return typeface;
    }
}
