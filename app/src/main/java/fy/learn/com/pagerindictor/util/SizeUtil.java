package fy.learn.com.pagerindictor.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class SizeUtil {

    public static int dp2px(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (dp * displayMetrics.density);
    }

    public static float applyDimension(Context context, int unit, float value) {
        return TypedValue.applyDimension(unit, value, context.getResources().getDisplayMetrics());
    }

}
