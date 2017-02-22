package cn.linghu.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by linghu on 2017/1/13.
 *
 *
 */
public class Utils {

    public static int dp2px(int dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static <T> List<T> asList(T... arr) {
        return arr == null ? null : new ArrayList(Arrays.asList(arr));
    }

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean inArray(T t, List<T> list) {
        return t != null && !isEmpty(list) && list.contains(t);
    }

    public static <T> void addAll(List<T> list, T... ts) {
        List newList = Arrays.asList(ts);
        list.addAll(newList);
    }

    public static <T> int size(List<T> list) {
        int size = 0;
        if (!isEmpty(list)) {
            size = list.size();
        }

        return size;
    }

    private static int screenHeight = -1;
    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(View view) {
        if (screenHeight == -1) {
            DisplayMetrics displayMetrics = view.getResources().getDisplayMetrics();
            screenHeight = displayMetrics.heightPixels;
        }
        return screenHeight;
    }

}
