package cn.yph.library;

import android.content.Context;
import android.util.TypedValue;

/**
 * @Author penghao
 * @Date 2017/8/9
 */

public class Utils {

    public static int dp2px(Context context, float dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    }
}
