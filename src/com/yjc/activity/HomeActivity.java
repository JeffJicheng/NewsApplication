package com.yjc.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.yjc.adapter.HomeVpAdapter;
import com.yjc.app.MyApplication;
import com.yjc.base.BaseActivity;
import com.yjc.bean.CategoryBean;
import com.yjc.config.Config;
import com.yjc.fragment.NewFragment;
import com.yjc.mytopnews.R;
import com.yjc.utils.DimenUtils;
import com.yjc.utils.JsonUtils;
import com.yjc.utils.SystemUtils;
import com.zhy.view.ColorTrackView;
/**
 * ��ҳ�� 
 * @author yjc  jeff.Yao
 *
 */
public class HomeActivity extends BaseActivity implements OnClickListener, PlatformActionListener, OnPageChangeListener {
	private HomeActivity act;
	private SlidingMenu menu;
	private int width;
	private View left;
	//--------������ؼ�------------
	private ImageView qq,sina;
	private ListView left_lv;
	private ImageView feedback,settings;//����������
	//-----------------------------
	private ImageView home_left_header;
	private long current_time;//��ǰʱ��
	
	private ViewPager home_vp; 
	private HomeVpAdapter adapter;
	private List<Fragment> fragments;
	
	private List<CategoryBean> cbs;
	private LinearLayout home_tab_ll;
	private HorizontalScrollView home_scoll;
	
	private ImageView home_tab_iv;
	
	
	private boolean isclick = false;

	private ImageView home_header_center_iv;
	private ProgressBar home_header_center_pb;
	
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.act_home);
		ControlTime();//���� ʱ��  �����Զ���¼  ����ʱ���Ƿ񳬹�����
		act = this;
		MyApplication.instance.addAct(act);
		MyApplication.instance.setEditPingdaoAfter(false);
		registerReceiver();//ע�������
		initWidth();
		BindSliding();
		initLeft();//��ʼ�������� �ؼ�
		initLeftDatas();//���������ListView����Դ��ʼ��
		doLeftViews();
		initHomeViews();//��ʼ��Home��View��ͼ�ؼ�
		doHomeViews();
		AutoLogin();//�Զ���¼
		initTab();
		
	}
	/**
	 * �Ե������������
	 */
	private void initTab() {
		// TODO Auto-generated method stub
		UUID uuid = UUID.randomUUID();
		String url = Config.baseurl+"CategoryBySelect?select=1&uuid="+uuid.toString();
		SystemUtils.getHttpUtils().send(HttpMethod.GET, url, callBack);
	}
	private void ControlTime() {
		// TODO Auto-generated method stub
		long time = getSharedPreferences("user", Context.MODE_PRIVATE).getLong("time", 0);
		//����������� ������
		if(System.currentTimeMillis() - time > 3 * 24 * 60 * 60 * 1000){
			getSharedPreferences("user", Context.MODE_PRIVATE).edit()
			.putString("img", "")
			.putString("name", "")
			.putString("address","")
			.commit();
		}
	}
	/**
	 * �Զ���¼  
	 */
	private void AutoLogin() {
		// TODO Auto-generated method stub
		String img = getSharedPreferences("user", Context.MODE_PRIVATE).getString("img", "");
		//Ϊ�ղ��Զ���¼����Ϊ���Զ���¼
		if(img==null || img.equals("")){
			//Ϊ�վͲ��Զ���¼
		}else{
			//��Ϊ�շ��㲥  ��ͷ���Զ���¼
			sendBroadcast(new Intent("changeheader"));
		}
	}
	/**
	 * HomeActivity�Ŀؼ�/��� ���
	 */
	private void doHomeViews() {
		// TODO Auto-generated method stub
		home_left_header.setOnClickListener(this);
		home_vp.setOffscreenPageLimit(1);//����ViewPager�Ļ���Ϊ1��,�����HTTP���������ѹ��
		home_vp.setOnPageChangeListener(this);
		home_header_center_iv.setOnClickListener(this);
		home_tab_iv.setOnClickListener(this);
	}
	/**
	 * �㲥������ע��
	 */
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter("changeheader");//����Action,���㷢�㲥ʱ����
		// TODO Auto-generated method stub
		registerReceiver(receiver, filter);
	}
	/**
	 * ����ҳ��Ŀؼ���ʼ��
	 */
	private void initHomeViews() {
		// TODO Auto-generated method stub
		home_left_header = (ImageView) findViewById(R.id.home_header_left_iv);
		home_tab_ll = (LinearLayout) findViewById(R.id.home_tab_ll);
		home_scoll = (HorizontalScrollView) findViewById(R.id.home_tab_scroll);
		home_vp = (ViewPager) findViewById(R.id.act_home_vp);
		home_header_center_iv = (ImageView) findViewById(R.id.home_header_center_iv);
		home_header_center_pb = (ProgressBar) findViewById(R.id.home_header_center_progresbar);
		home_tab_iv = (ImageView) findViewById(R.id.home_tab_iv);
	}
	
	
	/**
	 * ����������Ӧ�¼�
	 *  ��������¼
	 */
	private void doLeftViews() {
		// TODO Auto-generated method stub
		qq.setOnClickListener(this);
		sina.setOnClickListener(this);
	}

	/**
	 * ����������Դ��ʼ��
	 */
	private void initLeftDatas() {
		// TODO Auto-generated method stub
		String[] strs = {"�ղ�","��Ϣ","����","�","Ӧ��"};
		int[] imgs = {
				R.drawable.ic_drawer_favorite_normal,
				R.drawable.ic_drawer_message_normal,
				R.drawable.ic_drawer_offline_normal,
				R.drawable.left_drawer_activity,
				R.drawable.ic_drawer_appstore_normal
		};
		List<HashMap<String, Object>> datas = new ArrayList<HashMap<String,Object>>();
		for (int i = 0; i < imgs.length; i++) {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("tv", strs[i]);
			hm.put("iv", imgs[i]);
			datas.add(hm);
		}
		SimpleAdapter adapter = new SimpleAdapter(act, datas, R.layout.item_left_lv, new String[]{"tv","iv"}, new int[]{R.id.item_left_lv_tv,R.id.item_left_lv_iv});
		left_lv.setAdapter(adapter);
	}




	/**
	 * ��������ؼ���ʼ��
	 */
	private void initLeft() {
		// TODO Auto-generated method stub
		qq = (ImageView) findviewbyid(left, R.id.drawer_left_top_qq);
		sina = (ImageView) findviewbyid(left, R.id.drawer_left_top_sina);
		left_lv = (ListView) findviewbyid(left, R.id.drawer_left_lv);
		feedback = (ImageView) findviewbyid(left, R.id.drawer_left_bottom_iv1);
		settings = (ImageView) findviewbyid(left, R.id.drawer_left_bottom_iv2);
	}

	/**
	 * �õ���Ļ�Ŀ��
	 */
	private void initWidth() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		// TODO Auto-generated method stub
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		width = outMetrics.widthPixels;
	}
	
	/**
	 * ��Slidingmenu��ʼ��
	 */
	private void BindSliding() {
		// TODO Auto-generated method stub
		menu = new SlidingMenu(act);
		menu.setMode(SlidingMenu.LEFT);
		menu.setBehindWidth((int) (width*0.9));
		menu.setShadowWidth(10);//�������ӰЧ��
		menu.setShadowDrawable(R.drawable.channel_rightblock_night);//�������ӰͼƬ
		menu.setFadeDegree(0.35f);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);//����������
		left = act.getLayoutInflater().inflate(R.layout.drawer_left, null);
		menu.setMenu(left);
		menu.attachToActivity(act, SlidingMenu.SLIDING_CONTENT);
	}
	//************�����¼�*****************
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.drawer_left_top_qq:
			Platform plat = ShareSDK.getPlatform(QQ.NAME);
			authorize(plat);
			break;
		case R.id.drawer_left_top_sina:
			Platform plat1 = ShareSDK.getPlatform(SinaWeibo.NAME);
			authorize(plat1);
			break;
		case R.id.home_header_left_iv:
			if(menu.isMenuShowing()){
				menu.showContent();//�ر�menu
			}else{
				menu.showMenu();
			}
			break;
		case R.id.home_header_center_iv:
			home_header_center_iv.setVisibility(View.INVISIBLE);
			home_header_center_pb.setVisibility(View.VISIBLE);
			int item = home_vp.getCurrentItem();//�õ���ǰ��ʾ��ViewPager
			NewFragment nf = (NewFragment) fragments.get(item);//�õ���ǰ��ʾ��Fragment
			nf.setRefresh();//plvˢ��
			//ˢ�������Ҫ��ˢ��ͼ����Ϊ��ʾ��progress��Ϊ����ʾ
			changeHeader();
			break;
		case R.id.home_tab_iv:
			//���+����ת
			Intent intent = new Intent();
			intent.setClass(act, EditPingdaoActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.sa_act_incoming, R.anim.aa_act_outexit);//����Ķ���Ϊsa_act_incoming���˳��Ķ�����
			break;
		}
	}
	
//*******************************************
	private void authorize(Platform plat) {
		 plat.setPlatformActionListener(this);
		 // true��ʹ��SSO��Ȩ��falseʹ��SSO��Ȩ
		 plat.SSOSetting(true);
		 //��ȡ�û�����
		 plat.showUser(null);
	}
	@Override
	public void onCancel(Platform arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		// TODO Auto-generated method stub
		String img;
		String name;
		String address;
		//��õ�������¼�Ļ�����Ϣ
		if(arg0.getName().equals(QQ.NAME)){
			img = arg2.get("figureurl").toString();
			name = arg2.get("nickname").toString();
			address = arg2.get("city").toString();
		}else{
			img = arg2.get("profile_imgae_url").toString();//���ͷ���ַ
			name = arg2.get("name").toString();
			address = arg2.get("location").toString();
		}
		//��sharedPreferences�����������Ϣ
		getSharedPreferences("user", Context.MODE_PRIVATE).edit()
		.putString("img", img)
		.putString("name", name)
		.putString("address", address)
		.commit();//�ǵ�Ҫ�ύ��������
		
		//���㲥
		sendBroadcast(new Intent("changeheader"));
		
	}
	
	
	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		shortToast("��¼ʧ�� : "+arg2.toString());
	}
	
	/**
	 * ���㲥����ͷ��ͼƬ
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			//ȡ��ͷ���ַ
			String img = getSharedPreferences("user", Context.MODE_PRIVATE).getString("img", "");//�����Ǹ���Ĭ��ֵ
			//���ؽ�ȥ
			SystemUtils.getBitMapUtils(act).display(home_left_header, img);
		}
		
	};
	
	/**
	 * ��������¼�
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(menu.isMenuShowing()){
				menu.showContent();
			}else{
				if(Math.abs(System.currentTimeMillis() - current_time) > 2000){
					centerToast("�ٵ�һ���˳�ร�");
					current_time = System.currentTimeMillis();
				}else{
					act.finish();
				}
			}
			return true;//����
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		getSharedPreferences("user", Context.MODE_PRIVATE).edit()
		.putLong("time", System.currentTimeMillis()).commit();
		MyApplication.instance.getActs().remove(act);
		super.onDestroy();
	}
	//------------------------------------------------------------------------------
	
	RequestCallBack<String> callBack = new RequestCallBack<String>() {
		
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			String data = arg0.result;
			cbs = JsonUtils.getCategroyContent(data);//���������
			fragments = new ArrayList<Fragment>();
			for (int i = 0; i < cbs.size(); i++) {
				NewFragment newFragment = NewFragment.newInstance(cbs.get(i).getCategory_id());
				fragments.add(newFragment);
			}
			adapter = new HomeVpAdapter(getSupportFragmentManager(), fragments);
			home_vp.setAdapter(adapter);
			getTabViews();
		}
		
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			loge(arg1);
		}
	};

	/**
	 * ����һ�� views ��ʾ����
	 */
	protected void getTabViews() {
		// TODO Auto-generated method stub
		home_tab_ll.removeAllViews();//�Ƴ�ȫ��views,��ֹ����ӵ�ʱ���ظ����
		for (int i = 0; i < cbs.size(); i++) {
			ColorTrackView ctview = new ColorTrackView(act);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
			params.leftMargin = DimenUtils.dp2px(5, act);
			params.rightMargin = DimenUtils.dp2px(5, act);
			ctview.setLayoutParams(params);
			ctview.setPadding(DimenUtils.dp2px(5, act), DimenUtils.dp2px(5, act), DimenUtils.dp2px(5, act), DimenUtils.dp2px(5, act));
			ctview.setText(cbs.get(i).getText());
			ctview.setTextSize(DimenUtils.dp2px(18, act));
			ctview.setTextOriginColor(Config.origin_color);
			ctview.setTextChangeColor(Config.change_color);
			//�����ѡ�
			ctview.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					for (int j = 0; j < home_tab_ll.getChildCount(); j++) {
						if(arg0 == home_tab_ll.getChildAt(j)){
							/**
							 * setCurrentItem�� ����������setCurrentItem���� ���к�ͻ�ֱ��������һ��
							 * isclick = true;
							 * ��������������onPageScrolled��onPageSelected
							 */
							home_vp.setCurrentItem(j);//�����תΪ��Ӧ��ViewPager
							isclick = true;
							//ѡ�е�
							((ColorTrackView)home_tab_ll.getChildAt(j)).setProgress(1);
							//�����view����Ļ�����ľ���
							int l = home_tab_ll.getChildAt(j).getLeft();
							//�����view�Ŀ��
							int w = home_tab_ll.getChildAt(j).getWidth();
							//w/2+l�͵��ڵ��view�����ĵ�
							int x = l + w / 2 - width / 2;//�ҵ����ĵ�  ���ѡ��������м�λ��
							home_scoll.smoothScrollTo(x, 0);//����
						}else{
							//δѡ�иĵ�
							((ColorTrackView)home_tab_ll.getChildAt(j)).setProgress(0);
						}
					}
				}
			});
			if(i == 0){
				ctview.setProgress(1);
			}else{
				ctview.setProgress(0);
			}
			home_tab_ll.addView(ctview);
		}
	}
	
	/**
	 * ViewPager����
	 * 
	 * onPageScrollStateChanged �� onPageScrolled ��һ����ʱ������
	 * 
	 */ 
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		isclick = false;
	}
	//arg0��ѡ�е��Ǹ�ViewPager  arg1�ǰٷֱȣ�ֻ�л�����ȷ�Ϻ��Ϊ0��  
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		if(!isclick){
			if(arg1 > 0){
				ColorTrackView left = (ColorTrackView) home_tab_ll.getChildAt(arg0);
				ColorTrackView right = (ColorTrackView) home_tab_ll.getChildAt(arg0+1);
				left.setDirection(1);
				right.setDirection(0);
				left.setProgress(1-arg1);
				right.setProgress(arg1);
			}
		}
		
	}
	//pagerѡ��
	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		if(!isclick){
			ColorTrackView select = (ColorTrackView) home_tab_ll.getChildAt(arg0);
			int l = select.getLeft();
			//�����view�Ŀ��
			int w = select.getWidth();
			//w/2+l�͵��ڵ��view�����ĵ�
			int x = l + w / 2 - width / 2;//�ҵ����ĵ�  ���ѡ��������м�λ��
			home_scoll.smoothScrollTo(x, 0);//����
		}
	
	}
	
	//-------------------------------------------------------------------------------
	public void changeHeader(){
		if(home_header_center_pb.getVisibility() == View.VISIBLE){
			home_header_center_pb.setVisibility(View.INVISIBLE);
			home_header_center_iv.setVisibility(View.VISIBLE);
		}else {
			home_header_center_pb.setVisibility(View.VISIBLE);
			home_header_center_iv.setVisibility(View.INVISIBLE);
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(MyApplication.instance.isEditPingdaoAfter()){
			initTab();
		}
		super.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		MyApplication.instance.setEditPingdaoAfter(false);
		super.onPause();
	}
}
