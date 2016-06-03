package com.yjc.utils;

import android.content.Context;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.yjc.mytopnews.R;
/**
 * xUtils工具类 系统工具类  
 * @author yjc Jeff.Yao
 *
 */
public class SystemUtils {
	public static BitmapUtils getBitMapUtils(Context context){
		BitmapUtils bu = new BitmapUtils(context);
		bu.configDefaultLoadFailedImage(R.drawable.default_user_leftdrawer);
		bu.configMemoryCacheEnabled(true);
		bu.configDiskCacheEnabled(true);
		return bu;
	}
	public static HttpUtils getHttpUtils(){
		HttpUtils hu = new HttpUtils();
		hu.configResponseTextCharset("UTF-8");//指定编码
		return hu;
	}
	public static DbUtils getDbUtils(Context context){
		DbUtils db = DbUtils.create(context);
		return db;
	}
}
