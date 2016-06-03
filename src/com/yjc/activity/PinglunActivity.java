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
 * �������Ž��� 
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
	 * �Կؼ�������
	 */
	private void doViews() {
		// TODO Auto-generated method stub
		pinglun_content.addTextChangedListener(this);//����EditText������
		Animation animation = AnimationUtils.loadAnimation(act, R.anim.ta_pinglpullup);
		pinglun_rl.startAnimation(animation);
	}
	/**
	 * ��ʼ���ؼ�
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
	//-----------------EditText-��content�ļ�������-----------------------------
	//���ݱ仯֮��
	//1 �Է�����ť������
	//2 �Է�����ť���ü���
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
			pinglun_post.setClickable(true);
			pinglun_post.setBackgroundColor(Color.parseColor("#2A90D7"));
			pinglun_post.setOnClickListener(this);
	}
	//���ݱ仯֮ǰ
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}
	//���ݱ仯����
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	//-------------------������ť�ļ�������----------------------------
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int news_id = MyApplication.instance.getNews_id();//����
		if(pinglun_content == null || pinglun_content.equals("")){
			shortToast("���۲���Ϊ��");
			return;
		}
		
		//��ȡ�û���EditText��д����
		String content = pinglun_content.getText().toString().trim();
		filterText(content);//�������д�
		//��ȡ�û������Ϣ
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
		//�첽  ����Http����  post����
		SystemUtils.getHttpUtils().send(HttpMethod.POST, url, params, callBack);
	}
	
	/**
	 * �������дʻ㷽��
	 * @param content
	 */
	private void filterText(String content) {
		// TODO Auto-generated method stub
		//�������дʻ�
		String[] badtext = Config.badtext;
		for (int i = 0; i < badtext.length; i++) {
			//�滻���дʻ�               Ŀ��ʻ㣨���滻�Ĵʻ㣩  �滻�ɵĴʻ�
			content.replace(badtext[i], getStar(badtext[i]));//getStar��ͳ��Ҫ�����ٴʻ�Ȼ���滻���Ǻ�
		}
	}
	/**
	 * �滻��*��  �����ٸ�*�ţ�
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
	//------------------�������� post����------------------------------------
	RequestCallBack<String> callBack = new RequestCallBack<String>() {
		/**
		 * ����ɹ�
		 */
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			//������Json����  ���ص�ResponseInfo<String> arg0������  arg0.result
			try {
				JSONObject jo = new JSONObject(arg0.result);
				if(jo.getString("result").equals("success")){
					//�����ɹ�   ���۷��ͳɹ�
					shortToast("���۳ɹ�");
					Animation animation = AnimationUtils.loadAnimation(act, R.anim.ta_pinglpulldown);
					animation.setAnimationListener(listener);
					pinglun_rl.startAnimation(animation);
				}else{
					//����ʧ��
					pinglun_content.setText("");
					pinglun_post.setClickable(false);
					pinglun_post.setBackgroundColor(Color.parseColor("#CACACA"));
					shortToast("����ʧ��");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				loge(e.getMessage());
				e.printStackTrace();
			}
		}
		
		//����ʧ��
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			loge(arg1);
			pinglun_content.setText("");
			pinglun_post.setClickable(false);
			pinglun_post.setBackgroundColor(Color.parseColor("#CACACA"));	
			shortToast("����ʧ��");
		}
	};
	
	
	/**
	 * ������״�Activity��ʧ
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
	 * �жϵ���������״����Ƿ����״�
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
