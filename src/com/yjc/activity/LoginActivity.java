package com.yjc.activity;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

import com.yjc.app.MyApplication;
import com.yjc.base.BaseActivity;
import com.yjc.mytopnews.R;
/**
 * 登录界面
 * @author yjc Jeff.Yao
 *
 */
public class LoginActivity extends BaseActivity implements OnClickListener, PlatformActionListener {
	private LoginActivity act;
	//----------登录界面的上面的控件back-----------
	private ImageView back;
	//-----------登录界面的下面的控件新浪和QQ--------
	private ImageView sina,qq;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.act_login);
		act = this;
		MyApplication.instance.getActs().add(act);
		initViews();
		doViews();
	}
	
	/**
	 * 对控件操作初始化
	 */
	private void doViews() {
		// TODO Auto-generated method stub
		back.setOnClickListener(this);
		sina.setOnClickListener(this);
		qq.setOnClickListener(this);
	}
	/**
	 * 初始化控件
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		back = (ImageView) findViewById(R.id.login_header_left);
		sina = (ImageView) findViewById(R.id.login_sina);
		qq = (ImageView) findViewById(R.id.login_qq);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.instance.getActs().remove(act);
		super.onDestroy();
	}
//------------------监听事件----------------------------------------
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.login_header_left:
			//back键
			act.finish();
			break;
		case R.id.login_qq:
			//qq登录
			Platform plat1 = ShareSDK.getPlatform(QQ.NAME);
			authorize(plat1);
			break;
		case R.id.login_sina:
			//新浪登录
			Platform plat2 = ShareSDK.getPlatform(SinaWeibo.NAME);
			authorize(plat2);
			break;
		}
	}
//---------------------------------------------------------	
	/**
	 * 第三方登录的方法
	 * @param pf
	 */
	private void authorize(Platform pf){
		pf.setPlatformActionListener(this);
		pf.SSOSetting(true);
		pf.showUser(null);
	}
	//-------------第三方登录监听-------------------
	@Override
	public void onCancel(Platform arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		// TODO Auto-generated method stub
		String name = null;//姓名
		String city = null;//城市
		String imgurl = null;//头像地址
		if(arg0.getName().equals(SinaWeibo.NAME)){
			name =arg2.get("name").toString();
			city = arg2.get("location").toString();
			imgurl = arg2.get("profile_imgae_url").toString();
		}else if(arg0.getName().equals(QQ.NAME)){
			imgurl = arg2.get("figureurl").toString();
			name = arg2.get("nickname").toString();
			city = arg2.get("city").toString();
		}
		//将相关信息写入到表中
		getSharedPreferences("user", Context.MODE_PRIVATE).edit()
		.putString("name", name)
		.putString("img", imgurl)
		.putString("address", city)
		.commit();
		shortToast("跳转1");
		startActivity(new Intent(act, PinglunActivity.class));
		shortToast("跳转2");
		act.finish();
		shortToast("结束");
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}
}
