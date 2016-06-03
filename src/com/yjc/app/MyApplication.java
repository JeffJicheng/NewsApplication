package com.yjc.app;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;

import android.app.Activity;
import android.app.Application;
/**
 * Ӧ��
 * @author yjc Jeff.Yao
 *
 */
public class MyApplication extends Application {
	private List<Activity> acts;//��������activity
	public static MyApplication instance;//
	private int news_id;
	private boolean isPinglunAfter;//�����ж�PinglunActivtity�Ƿ��Ѿ��ر���
	private boolean isEditPingdaoAfter;//�����ж�EditPingdaoActivity�Ƿ��Ѿ��ر���
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		ShareSDK.initSDK(instance);
		acts = new ArrayList<Activity>();
	}
	public void addAct(Activity act){
		acts.add(act);
	}
	public void removeAct(Activity act){
		acts.remove(act);
	}
	public List<Activity> getActs() {
		return acts;
	}
	public void setActs(List<Activity> acts) {
		this.acts = acts;
	}
	public int getNews_id() {
		return news_id;
	}
	public void setNews_id(int news_id) {
		this.news_id = news_id;
	}
	public boolean isPinglunAfter() {
		return isPinglunAfter;
	}
	public void setPinglunAfter(boolean isPinglunAfter) {
		this.isPinglunAfter = isPinglunAfter;
	}
	public boolean isEditPingdaoAfter() {
		return isEditPingdaoAfter;
	}
	public void setEditPingdaoAfter(boolean isEditPingdaoAfter) {
		this.isEditPingdaoAfter = isEditPingdaoAfter;
	}
	
}
