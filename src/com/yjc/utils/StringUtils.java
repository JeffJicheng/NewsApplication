package com.yjc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * ×Ö·û´®¹¤¾ßÀà
 * @author yjc jeff.Yao
 *
 */
public class StringUtils {
	public static String getTime(long time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(time));
	}
}
