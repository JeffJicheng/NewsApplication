package com.yjc.config;

import android.graphics.Color;
/**
 * 配置文件
 * @author yjc jeff.yao
 *
 */
public interface Config {
	String baseurl = "http://wwwtest.rupeng.cn:9099/MyTouTiao/";
	int origin_color = Color.BLACK;
	int change_color = Color.RED;
	String toutiao = "头条";
	String dantu = "单图";
	String duotu = "多图";
	String wutu = "无图";
	//过滤词汇  
	String[] badtext = {"擦","日","你大爷","操","习近平","李克强","共产党"};  
	
	
}
