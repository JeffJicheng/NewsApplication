package com.yjc.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yjc.bean.CategoryBean;
import com.yjc.bean.NewsBean;
import com.yjc.bean.PinglunBean;
/**
 * Json解析工具  
 * @author yjc Jeff.Yao
 *
 */
public class JsonUtils {
	public static List<CategoryBean> getCategroyContent(String data){
		List<CategoryBean> list = null;
		try {
			JSONObject jo = new JSONObject(data);
			if(jo.getString("result").equals("success")){
				JSONArray array = jo.getJSONArray("content");
				Gson gson = new Gson();
				list = gson.fromJson(array.toString(), new TypeToken<List<CategoryBean>>(){}.getType());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list == null){
			list = new ArrayList<CategoryBean>();
		}
		return list;
	}
	public static List<NewsBean> getNewsByCategory(String data){
		List<NewsBean> list = null;
		try {
			JSONObject jo = new JSONObject(data);
			if(jo.getString("result").equals("success")){
				JSONArray array = jo.getJSONArray("content");
				Gson gson = new Gson();
				list = gson.fromJson(array.toString(), new TypeToken<List<NewsBean>>(){}.getType());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list == null){
			list = new ArrayList<NewsBean>();
		}
		return list;
	}
	/**
	 * 评论的Utils方法
	 */
	public static List<PinglunBean> getPinglunContent(String data){
		List<PinglunBean> list = null;
		try {
			JSONObject jo = new JSONObject(data);
			if(jo.getString("result").equals("success")){
				JSONArray array = jo.getJSONArray("content");
				Gson gson = new Gson();
				list = gson.fromJson(array.toString(), new TypeToken<List<PinglunBean>>(){}.getType());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list == null){
			list = new ArrayList<PinglunBean>();
		}
		return list;
	}
}
