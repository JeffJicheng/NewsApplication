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
 * 展示图片界面 
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
	 * 控件的操作
	 */
	private void doViews() {
		// TODO Auto-generated method stub
		//异步加载用BitmapUtils
		img.setOnClickListener(this);
		SystemUtils.getBitMapUtils(act).display(img, imgurl);
		save.setOnClickListener(this);
	}

	/**
	 * 对控件初始化
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
		//图片
		case R.id.showimg_img:
			issaveshow = !issaveshow;
			if(issaveshow){
				save.setVisibility(View.VISIBLE);
			}else{
				save.setVisibility(View.INVISIBLE);
			}
			break;
			//下载图片
		case R.id.showimg_save:
			File dir = new File("/mnt/sdcard/MyTopNews/pic/");
			if(!dir.exists()){
				dir.mkdirs();
			}
			SystemUtils.getHttpUtils().download(imgurl, "/mnt/sdcard/MyTopNews/pic/"+System.currentTimeMillis()+".png", callback);
			pdialog = showSweetDialogProgress("下载中");
			break;
		}
	}
	RequestCallBack<File> callback = new RequestCallBack<File>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			pdialog.dismissWithAnimation();//Sweetprogressdialog消失
			centerToast("下载失败");
			save.setVisibility(View.INVISIBLE);//让保存按钮不显示
			issaveshow = false;
		}

		@Override
		public void onSuccess(ResponseInfo<File> arg0) {
			// TODO Auto-generated method stub
			pdialog.dismissWithAnimation();//Sweetprogressdialog消失
			centerToast("下载成功");
			save.setVisibility(View.INVISIBLE);//让保存按钮不显示
			issaveshow = false;
		}
	};
	
	/**
	 *判断点击的坐标是否在图片内,也就是说是否不在留白处
	 * PointF 是一个javabean 用来表示坐标等等，详细可以看源码
	 */
	public boolean isPicIn(PointF pf){
		//PointF 是一个javabean 用来表示坐标等等，详细可以看源码
		int top = img.getTop();//得到顶部
		int bottom = img.getBottom();//得到底部
		int left = img.getLeft();//得到左边界
		int right = img.getRight();//得到右边界
		boolean bo = false;//用来判断是否在图片之类
		if(pf.x > left && pf.x < right && pf.y > top && pf.y < bottom){
			bo = true;
		}
		return bo;
	}
	
	/**
	 * 点击屏幕的判断
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//判断是否按下 然后判断点击的是否在图片之类
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
