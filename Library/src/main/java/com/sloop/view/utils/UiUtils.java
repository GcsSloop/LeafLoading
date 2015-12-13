package com.sloop.view.utils;

import android.view.ContextThemeWrapper;

/**
 * UI相关工具类<br/>
 * Author: Sloop
 * Version: v1.1
 * Date: 2015/12/9
 * <ul type="disc">
 * <li><a href="http://www.sloop.icoc.cc"    target="_blank">作者网站</a>      <br/></li>
 * <li><a href="http://weibo.com/5459430586" target="_blank">作者微博</a>      <br/></li>
 * <li><a href="https://github.com/GcsSloop" target="_blank">作者GitHub</a>   <br/></li>
 * </ul>
 */
public class UiUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(ContextThemeWrapper context, float dpValue) {
        return (int) (dpValue * getDensity(context) + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(ContextThemeWrapper context, float pxValue) {
        return (int) (pxValue / getDensity(context) + 0.5f);
    }

    /**
     * @return 屏幕密度
     */
    public static float getDensity(ContextThemeWrapper context) {
        return context.getResources().getDisplayMetrics().density;
    }
}
