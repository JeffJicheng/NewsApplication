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
 * ��¼����
 * @author yjc Jeff.Yao
 *
 */
public class LoginActivity extends BaseActivity implements OnClickListener, PlatformActionListener {
	private LoginActivity act;
	//----------��¼���������Ŀؼ�back-----------
	private ImageView back;
	//-----------��¼���������Ŀؼ����˺�QQ--------
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
	 * �Կؼ�������ʼ��
	 */
	private void doViews() {
		// TODO Auto-generated method stub
		back.setOnClickListener(this);
		sina.setOnClickListener(this);
		qq.setOnClickListener(this);
	}
	/**
	 * ��ʼ���ؼ�
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
//------------------�����¼�----------------------------------------
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.login_header_left:
			//back��
			act.finish();
			break;
		case R.id.login_qq:
			//qq��¼
			Platform plat1 = ShareSDK.getPlatform(QQ.NAME);
			authorize(plat1);
			break;
		case R.id.login_sina:
			//���˵�¼
			Platform plat2 = ShareSDK.getPlatform(SinaWeibo.NAME);
			authorize(plat2);
			break;
		}
	}
//---------------------------------------------------------	
	/**
	 * ��������¼�ķ���
	 * @param pf
	 */
	private void authorize(Platform pf){
		pf.setPlatformActionListener(this);
		pf.SSOSetting(true);
		pf.showUser(null);
	}
	//-------------��������¼����-------------------
	@Override
	public void onCancel(Platform arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		// TODO Auto-generated method stub
		String name = null;//����
		String city = null;//����
		String imgurl = null;//ͷ���ַ
		if(arg0.getName().equals(SinaWeibo.NAME)){
			name =arg2.get("name").toString();
			city = arg2.get("location").toString();
			imgurl = arg2.get("profile_imgae_url").toString();
		}else if(arg0.getName().equals(QQ.NAME)){
			imgurl = arg2.get("figureurl").toString();
			name = arg2.get("nickname").toString();
			city = arg2.get("city").toString();
		}
		//�������Ϣд�뵽����
		getSharedPreferences("user", Context.MODE_PRIVATE).edit()
		.putString("name", name)
		.putString("img", imgurl)
		.putString("address", city)
		.commit();
		shortToast("��ת1");
		startActivity(new Intent(act, PinglunActivity.class));
		shortToast("��ת2");
		act.finish();
		shortToast("����");
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}
}
