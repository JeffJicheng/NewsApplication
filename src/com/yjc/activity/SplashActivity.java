package com.yjc.activity;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.yjc.app.MyApplication;
import com.yjc.base.BaseActivity;
import com.yjc.mytopnews.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
/**
 * 应用启动动画
 * @author yjc Jeff.Yao
 *
 */
public class SplashActivity extends BaseActivity implements AnimationListener {
	private ImageView iv;
	private AlphaAnimation aaAnimation;
	private Activity act;//对上下文环境起作用
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_splash);
		initViews();//初始化控件
		doViews();//对初始化的控件做一些基础性的处理
	}
	/**
	 * 对初始化的控件做处理
	 */
	private void doViews() {
		// TODO Auto-generated method stub
		aaAnimation.setAnimationListener(this);
		iv.setAnimation(aaAnimation);
	}
	/**
	 * 初始化控件
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		act = this;
		MyApplication.instance.addAct(act);
		iv = (ImageView) findViewById(R.id.act_splash_iv);
		aaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(act, R.anim.aa_splash);
	}
	//------------------------监听方法-------------------------------
	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		if(isNetConnected()){
			//连上网
			//跳转到HomeActivity
			startActivity(new Intent(act, HomeActivity.class));
			act.finish();
		}else{
			//没连上网
			showSweetDialog("警告", "没有联网喔，亲，去设置网络吧！", positive, cancel);
		}
	}
	private OnSweetClickListener positive = new OnSweetClickListener() {
		
		@Override
		public void onClick(SweetAlertDialog sweetAlertDialog) {
			// TODO Auto-generated method stub
			sweetAlertDialog.dismissWithAnimation();//伴随着动画效果消失
			startActivity(new Intent(Settings.ACTION_SETTINGS));
			act.finish();
			
		}
	};
	private OnSweetClickListener cancel = new OnSweetClickListener() {
		
		@Override
		public void onClick(SweetAlertDialog sweetAlertDialog) {
			// TODO Auto-generated method stub
			sweetAlertDialog.dismissWithAnimation();
			act.finish();
		}
	};
	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 判断手机是否联网  关于网络 权限！！！
	 * @return
	 */
	private boolean isNetConnected(){
		boolean bo = false;
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni == null){
			//无连接设备 飞行模式
			bo = false;
		}else{
			bo = ni.isConnected();//判断是否已经连上网
		}
		return bo;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.instance.removeAct(act);
		super.onDestroy();
	}
}
