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
 * Ӧ����������
 * @author yjc Jeff.Yao
 *
 */
public class SplashActivity extends BaseActivity implements AnimationListener {
	private ImageView iv;
	private AlphaAnimation aaAnimation;
	private Activity act;//�������Ļ���������
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_splash);
		initViews();//��ʼ���ؼ�
		doViews();//�Գ�ʼ���Ŀؼ���һЩ�����ԵĴ���
	}
	/**
	 * �Գ�ʼ���Ŀؼ�������
	 */
	private void doViews() {
		// TODO Auto-generated method stub
		aaAnimation.setAnimationListener(this);
		iv.setAnimation(aaAnimation);
	}
	/**
	 * ��ʼ���ؼ�
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		act = this;
		MyApplication.instance.addAct(act);
		iv = (ImageView) findViewById(R.id.act_splash_iv);
		aaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(act, R.anim.aa_splash);
	}
	//------------------------��������-------------------------------
	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		if(isNetConnected()){
			//������
			//��ת��HomeActivity
			startActivity(new Intent(act, HomeActivity.class));
			act.finish();
		}else{
			//û������
			showSweetDialog("����", "û������ร��ף�ȥ��������ɣ�", positive, cancel);
		}
	}
	private OnSweetClickListener positive = new OnSweetClickListener() {
		
		@Override
		public void onClick(SweetAlertDialog sweetAlertDialog) {
			// TODO Auto-generated method stub
			sweetAlertDialog.dismissWithAnimation();//�����Ŷ���Ч����ʧ
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
	 * �ж��ֻ��Ƿ�����  �������� Ȩ�ޣ�����
	 * @return
	 */
	private boolean isNetConnected(){
		boolean bo = false;
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni == null){
			//�������豸 ����ģʽ
			bo = false;
		}else{
			bo = ni.isConnected();//�ж��Ƿ��Ѿ�������
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
