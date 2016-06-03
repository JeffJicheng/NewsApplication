package com.yjc.activity;

import java.io.File;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.yjc.app.MyApplication;
import com.yjc.base.BaseActivity;
import com.yjc.mytopnews.R;
import com.yjc.utils.SystemUtils;
/**
 * չʾͼƬ���� 
 * @author yjc Jeff.Yao
 *
 */
public class showImgaActivity extends BaseActivity implements OnClickListener {
	private showImgaActivity act;
	private ImageView img;
	private ImageView save;
	private String imgurl;
	private boolean issaveshow;
	private SweetAlertDialog pdialog;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		act = this;
		MyApplication.instance.getActs().add(act);
		setContentView(R.layout.act_showimg);
		imgurl = getIntent().getStringExtra("imgurl");
		initViews();
		doViews();
	}

	/**
	 * �ؼ��Ĳ���
	 */
	private void doViews() {
		// TODO Auto-generated method stub
		//�첽������BitmapUtils
		img.setOnClickListener(this);
		SystemUtils.getBitMapUtils(act).display(img, imgurl);
		save.setOnClickListener(this);
	}

	/**
	 * �Կؼ���ʼ��
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		img = (ImageView) findViewById(R.id.showimg_img);
		save = (ImageView) findViewById(R.id.showimg_save);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.instance.getActs().remove(act);
		super.onDestroy();
	}
//-------------------------------------------------------------------
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		//ͼƬ
		case R.id.showimg_img:
			issaveshow = !issaveshow;
			if(issaveshow){
				save.setVisibility(View.VISIBLE);
			}else{
				save.setVisibility(View.INVISIBLE);
			}
			break;
			//����ͼƬ
		case R.id.showimg_save:
			File dir = new File("/mnt/sdcard/MyTopNews/pic/");
			if(!dir.exists()){
				dir.mkdirs();
			}
			SystemUtils.getHttpUtils().download(imgurl, "/mnt/sdcard/MyTopNews/pic/"+System.currentTimeMillis()+".png", callback);
			pdialog = showSweetDialogProgress("������");
			break;
		}
	}
	RequestCallBack<File> callback = new RequestCallBack<File>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			pdialog.dismissWithAnimation();//Sweetprogressdialog��ʧ
			centerToast("����ʧ��");
			save.setVisibility(View.INVISIBLE);//�ñ��水ť����ʾ
			issaveshow = false;
		}

		@Override
		public void onSuccess(ResponseInfo<File> arg0) {
			// TODO Auto-generated method stub
			pdialog.dismissWithAnimation();//Sweetprogressdialog��ʧ
			centerToast("���سɹ�");
			save.setVisibility(View.INVISIBLE);//�ñ��水ť����ʾ
			issaveshow = false;
		}
	};
	
	/**
	 *�жϵ���������Ƿ���ͼƬ��,Ҳ����˵�Ƿ������״�
	 * PointF ��һ��javabean ������ʾ����ȵȣ���ϸ���Կ�Դ��
	 */
	public boolean isPicIn(PointF pf){
		//PointF ��һ��javabean ������ʾ����ȵȣ���ϸ���Կ�Դ��
		int top = img.getTop();//�õ�����
		int bottom = img.getBottom();//�õ��ײ�
		int left = img.getLeft();//�õ���߽�
		int right = img.getRight();//�õ��ұ߽�
		boolean bo = false;//�����ж��Ƿ���ͼƬ֮��
		if(pf.x > left && pf.x < right && pf.y > top && pf.y < bottom){
			bo = true;
		}
		return bo;
	}
	
	/**
	 * �����Ļ���ж�
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//�ж��Ƿ��� Ȼ���жϵ�����Ƿ���ͼƬ֮��
		PointF pf = new PointF(event.getX(), event.getY());
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(!isPicIn(pf)){
				showImgaActivity.this.finish();
			}
			return true;
		}
		return super.onTouchEvent(event);
	}
}
