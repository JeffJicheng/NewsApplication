package com.yjc.utils;

import android.content.Context;
import android.util.DisplayMetrics;
/**
 * ��λת��
 * @author yjc Jeff.Yao
 *
 */
public class DimenUtils {
	//dp -> px
	public static int dp2px(int dp,Context context){
		DisplayMetrics outMetrics = context.getResources().getDisplayMetrics();//����ֻ������Ϣ
		float density = outMetrics.density;//����ֻ������ܶ�
		return (int) (dp * density + 0.5f);
	}
	//px -> dp
	public static int px2dp(int px,Context context){
		DisplayMetrics outMetrics = context.getResources().getDisplayMetrics();
		float density = outMetrics.density;
		return (int) (px / density + 0.5f);
	}
}
