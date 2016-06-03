package com.yjc.utils;

import android.content.Context;
import android.util.DisplayMetrics;
/**
 * 单位转换
 * @author yjc Jeff.Yao
 *
 */
public class DimenUtils {
	//dp -> px
	public static int dp2px(int dp,Context context){
		DisplayMetrics outMetrics = context.getResources().getDisplayMetrics();//获得手机相关信息
		float density = outMetrics.density;//获得手机像素密度
		return (int) (dp * density + 0.5f);
	}
	//px -> dp
	public static int px2dp(int px,Context context){
		DisplayMetrics outMetrics = context.getResources().getDisplayMetrics();
		float density = outMetrics.density;
		return (int) (px / density + 0.5f);
	}
}
