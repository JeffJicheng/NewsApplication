package com.yjc.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.yjc.app.MyApplication;
import com.yjc.base.BaseActivity;
import com.yjc.config.Config;
import com.yjc.mytopnews.R;
import com.yjc.utils.StringUtils;
import com.yjc.utils.SystemUtils;
/**
 * 评论新闻界面 
 * @author yjc Jeff.Yao
 *
 */
public class PinglunActivity extends BaseActivity implements TextWatcher, OnClickListener {
	private PinglunActivity act;
	private EditText pinglun_content;
	private Button pinglun_post;
	private RelativeLayout pinglun_rl;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.act_pinglun);
		act = this;
		MyApplication.instance.getActs().add(act);
		MyApplication.instance.setPinglunAfter(false);
		initViews();
		doViews();
	}
	/**
	 * 对控件做操作
	 */
	private void doViews() {
		// TODO Auto-generated method stub
		pinglun_content.addTextChangedListener(this);//监听EditText的内容
		Animation animation = AnimationUtils.loadAnimation(act, R.anim.ta_pinglpullup);
		pinglun_rl.startAnimation(animation);
	}
	/**
	 * 初始化控件
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		pinglun_content = (EditText) findViewById(R.id.pinglun_content);
		pinglun_post = (Button) findViewById(R.id.pinglun_post);
		pinglun_rl = (RelativeLayout) findViewById(R.id.pinglun_rl);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.instance.getActs().remove(act);
		
		super.onDestroy();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		MyApplication.instance.setPinglunAfter(true);
		super.onPause();
	}
	//-----------------EditText-》content的监听方法-----------------------------
	//内容变化之后
	//1 对发布按钮做处理
	//2 对发布按钮设置监听
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
			pinglun_post.setClickable(true);
			pinglun_post.setBackgroundColor(Color.parseColor("#2A90D7"));
			pinglun_post.setOnClickListener(this);
	}
	//内容变化之前
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}
	//内容变化当中
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	//-------------------发布按钮的监听方法----------------------------
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int news_id = MyApplication.instance.getNews_id();//新闻
		if(pinglun_content == null || pinglun_content.equals("")){
			shortToast("评论不能为空");
			return;
		}
		
		//获取用户在EditText编写的字
		String content = pinglun_content.getText().toString().trim();
		filterText(content);//过滤敏感词
		//获取用户相关信息
		String fromname = getSharedPreferences("user",Context.MODE_PRIVATE).getString("name", "");
		String fromaddr = getSharedPreferences("user", Context.MODE_PRIVATE).getString("address", "");
		String fromimg  = getSharedPreferences("user", Context.MODE_PRIVATE).getString("img", "");
		String url = Config.baseurl + "PinglunAdd";
		RequestParams params = new RequestParams("UTF-8");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		BasicNameValuePair pair1 = new BasicNameValuePair("text", content);
		BasicNameValuePair pair2 = new BasicNameValuePair("fromname", fromname);
		BasicNameValuePair pair3 = new BasicNameValuePair("fromaddr", fromaddr);
		BasicNameValuePair pair4 = new BasicNameValuePair("fromimg", fromimg);
		BasicNameValuePair pair5 = new BasicNameValuePair("news_id", news_id+"");
		nameValuePairs.add(pair5);
		nameValuePairs.add(pair4);
		nameValuePairs.add(pair3);
		nameValuePairs.add(pair2);
		nameValuePairs.add(pair1);
		params.addBodyParameter(nameValuePairs);
		//异步  发送Http请求  post请求
		SystemUtils.getHttpUtils().send(HttpMethod.POST, url, params, callBack);
	}
	
	/**
	 * 过滤敏感词汇方法
	 * @param content
	 */
	private void filterText(String content) {
		// TODO Auto-generated method stub
		//过滤敏感词汇
		String[] badtext = Config.badtext;
		for (int i = 0; i < badtext.length; i++) {
			//替换敏感词汇               目标词汇（被替换的词汇）  替换成的词汇
			content.replace(badtext[i], getStar(badtext[i]));//getStar是统计要换多少词汇然后替换成星号
		}
	}
	/**
	 * 替换成*号  （多少个*号）
	 * @param string
	 * @return
	 */
	private CharSequence getStar(String string) {
		// TODO Auto-generated method stub
		String text = "";
		for (int i = 0; i < string.length(); i++) {
			text += "*";
		}
		return text;
	}
	//------------------发布评论 post请求------------------------------------
	RequestCallBack<String> callBack = new RequestCallBack<String>() {
		/**
		 * 请求成功
		 */
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			//返回是Json数据  返回到ResponseInfo<String> arg0这里面  arg0.result
			try {
				JSONObject jo = new JSONObject(arg0.result);
				if(jo.getString("result").equals("success")){
					//解析成功   评论发送成功
					shortToast("评论成功");
					Animation animation = AnimationUtils.loadAnimation(act, R.anim.ta_pinglpulldown);
					animation.setAnimationListener(listener);
					pinglun_rl.startAnimation(animation);
				}else{
					//解析失败
					pinglun_content.setText("");
					pinglun_post.setClickable(false);
					pinglun_post.setBackgroundColor(Color.parseColor("#CACACA"));
					shortToast("评论失败");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				loge(e.getMessage());
				e.printStackTrace();
			}
		}
		
		//请求失败
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			loge(arg1);
			pinglun_content.setText("");
			pinglun_post.setClickable(false);
			pinglun_post.setBackgroundColor(Color.parseColor("#CACACA"));	
			shortToast("评论失败");
		}
	};
	
	
	/**
	 * 点击留白处Activity消失
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			PointF point = new PointF(event.getX(), event.getY());
			if(!isInView(point)){
				Animation animation = AnimationUtils.loadAnimation(act, R.anim.ta_pinglpulldown);
				animation.setAnimationListener(listener);
				pinglun_rl.startAnimation(animation);
			}
			return true;
		}
		return super.onTouchEvent(event);
	}
	/**
	 * 判断点击的是留白处还是非留白处
	 */
	private boolean isInView(PointF point){
		boolean bo = false;
		int top = pinglun_rl.getTop();
		int bottom = pinglun_rl.getBottom();
		int left = pinglun_rl.getLeft();
		int right = pinglun_rl.getRight();
		if(point.x > left && point.x < right && point.y > top && point.y < bottom){
			bo = true;
		}
		return bo;
	}
	private Animation.AnimationListener listener = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationRepeat(Animation arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationEnd(Animation arg0) {
			// TODO Auto-generated method stub
			act.finish();
		}
	};
}
