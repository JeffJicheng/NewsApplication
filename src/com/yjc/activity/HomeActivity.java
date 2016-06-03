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
 * 主页面 
 * @author yjc  jeff.Yao
 *
 */
public class HomeActivity extends BaseActivity implements OnClickListener, PlatformActionListener, OnPageChangeListener {
	private HomeActivity act;
	private SlidingMenu menu;
	private int width;
	private View left;
	//--------左侧抽屉控件------------
	private ImageView qq,sina;
	private ListView left_lv;
	private ImageView feedback,settings;//反馈，设置
	//-----------------------------
	private ImageView home_left_header;
	private long current_time;//当前时间
	
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
		ControlTime();//控制 时间  控制自动登录  计算时间是否超过期限
		act = this;
		MyApplication.instance.addAct(act);
		MyApplication.instance.setEditPingdaoAfter(false);
		registerReceiver();//注册接收者
		initWidth();
		BindSliding();
		initLeft();//初始化左侧抽屉 控件
		initLeftDatas();//对左侧抽屉的ListView数据源初始化
		doLeftViews();
		initHomeViews();//初始化Home的View视图控件
		doHomeViews();
		AutoLogin();//自动登录
		initTab();
		
	}
	/**
	 * 对导航栏获得数据
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
		//如果超过三天 就至空
		if(System.currentTimeMillis() - time > 3 * 24 * 60 * 60 * 1000){
			getSharedPreferences("user", Context.MODE_PRIVATE).edit()
			.putString("img", "")
			.putString("name", "")
			.putString("address","")
			.commit();
		}
	}
	/**
	 * 自动登录  
	 */
	private void AutoLogin() {
		// TODO Auto-generated method stub
		String img = getSharedPreferences("user", Context.MODE_PRIVATE).getString("img", "");
		//为空不自动登录，不为空自动登录
		if(img==null || img.equals("")){
			//为空就不自动登录
		}else{
			//不为空发广播  有头像自动登录
			sendBroadcast(new Intent("changeheader"));
		}
	}
	/**
	 * HomeActivity的控件/组件 点击
	 */
	private void doHomeViews() {
		// TODO Auto-generated method stub
		home_left_header.setOnClickListener(this);
		home_vp.setOffscreenPageLimit(1);//设置ViewPager的缓存为1个,缓解对HTTP请求的数据压力
		home_vp.setOnPageChangeListener(this);
		home_header_center_iv.setOnClickListener(this);
		home_tab_iv.setOnClickListener(this);
	}
	/**
	 * 广播接收者注册
	 */
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter("changeheader");//给个Action,方便发广播时处理
		// TODO Auto-generated method stub
		registerReceiver(receiver, filter);
	}
	/**
	 * 对主页面的控件初始化
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
	 * 对左侧抽屉响应事件
	 *  第三方登录
	 */
	private void doLeftViews() {
		// TODO Auto-generated method stub
		qq.setOnClickListener(this);
		sina.setOnClickListener(this);
	}

	/**
	 * 左侧抽屉数据源初始化
	 */
	private void initLeftDatas() {
		// TODO Auto-generated method stub
		String[] strs = {"收藏","消息","离线","活动","应用"};
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
	 * 对左侧抽屉控件初始化
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
	 * 得到屏幕的宽度
	 */
	private void initWidth() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		// TODO Auto-generated method stub
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		width = outMetrics.widthPixels;
	}
	
	/**
	 * 对Slidingmenu初始化
	 */
	private void BindSliding() {
		// TODO Auto-generated method stub
		menu = new SlidingMenu(act);
		menu.setMode(SlidingMenu.LEFT);
		menu.setBehindWidth((int) (width*0.9));
		menu.setShadowWidth(10);//渐变的阴影效果
		menu.setShadowDrawable(R.drawable.channel_rightblock_night);//渐变的阴影图片
		menu.setFadeDegree(0.35f);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);//滑动的区域
		left = act.getLayoutInflater().inflate(R.layout.drawer_left, null);
		menu.setMenu(left);
		menu.attachToActivity(act, SlidingMenu.SLIDING_CONTENT);
	}
	//************监听事件*****************
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
				menu.showContent();//关闭menu
			}else{
				menu.showMenu();
			}
			break;
		case R.id.home_header_center_iv:
			home_header_center_iv.setVisibility(View.INVISIBLE);
			home_header_center_pb.setVisibility(View.VISIBLE);
			int item = home_vp.getCurrentItem();//得到当前显示的ViewPager
			NewFragment nf = (NewFragment) fragments.get(item);//得到当前显示的Fragment
			nf.setRefresh();//plv刷新
			//刷新完后需要把刷新图标设为显示，progress设为不显示
			changeHeader();
			break;
		case R.id.home_tab_iv:
			//点击+号跳转
			Intent intent = new Intent();
			intent.setClass(act, EditPingdaoActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.sa_act_incoming, R.anim.aa_act_outexit);//进入的动画为sa_act_incoming，退出的动画无
			break;
		}
	}
	
//*******************************************
	private void authorize(Platform plat) {
		 plat.setPlatformActionListener(this);
		 // true不使用SSO授权，false使用SSO授权
		 plat.SSOSetting(true);
		 //获取用户资料
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
		//获得第三方登录的基本信息
		if(arg0.getName().equals(QQ.NAME)){
			img = arg2.get("figureurl").toString();
			name = arg2.get("nickname").toString();
			address = arg2.get("city").toString();
		}else{
			img = arg2.get("profile_imgae_url").toString();//获得头像地址
			name = arg2.get("name").toString();
			address = arg2.get("location").toString();
		}
		//用sharedPreferences来保存基本信息
		getSharedPreferences("user", Context.MODE_PRIVATE).edit()
		.putString("img", img)
		.putString("name", name)
		.putString("address", address)
		.commit();//记得要提交！！！！
		
		//发广播
		sendBroadcast(new Intent("changeheader"));
		
	}
	
	
	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		shortToast("登录失败 : "+arg2.toString());
	}
	
	/**
	 * 发广播更新头像图片
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			//取得头像地址
			String img = getSharedPreferences("user", Context.MODE_PRIVATE).getString("img", "");//后面那个是默认值
			//加载进去
			SystemUtils.getBitMapUtils(act).display(home_left_header, img);
		}
		
	};
	
	/**
	 * 点击按键事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(menu.isMenuShowing()){
				menu.showContent();
			}else{
				if(Math.abs(System.currentTimeMillis() - current_time) > 2000){
					centerToast("再点一次退出喔！");
					current_time = System.currentTimeMillis();
				}else{
					act.finish();
				}
			}
			return true;//拦截
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
			cbs = JsonUtils.getCategroyContent(data);//获得了数据
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
	 * 创建一组 views 显示数据
	 */
	protected void getTabViews() {
		// TODO Auto-generated method stub
		home_tab_ll.removeAllViews();//移除全部views,防止在添加的时候重复添加
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
			//点击的选项卡
			ctview.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					for (int j = 0; j < home_tab_ll.getChildCount(); j++) {
						if(arg0 == home_tab_ll.getChildAt(j)){
							/**
							 * setCurrentItem后 会首先运行setCurrentItem但是 运行后就会直接运行下一句
							 * isclick = true;
							 * 当运行完再运行onPageScrolled和onPageSelected
							 */
							home_vp.setCurrentItem(j);//点击后转为相应的ViewPager
							isclick = true;
							//选中的
							((ColorTrackView)home_tab_ll.getChildAt(j)).setProgress(1);
							//点击的view离屏幕的左侧的距离
							int l = home_tab_ll.getChildAt(j).getLeft();
							//点击的view的宽度
							int w = home_tab_ll.getChildAt(j).getWidth();
							//w/2+l就等于点击view的中心点
							int x = l + w / 2 - width / 2;//找到中心点  点击选项卡滑动到中间位置
							home_scoll.smoothScrollTo(x, 0);//滑动
						}else{
							//未选中改的
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
	 * ViewPager滑动
	 * 
	 * onPageScrollStateChanged 与 onPageScrolled 有一定的时间差！！！
	 * 
	 */ 
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		isclick = false;
	}
	//arg0是选中的那个ViewPager  arg1是百分比（只有滑动后确认后才为0）  
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
	//pager选中
	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		if(!isclick){
			ColorTrackView select = (ColorTrackView) home_tab_ll.getChildAt(arg0);
			int l = select.getLeft();
			//点击的view的宽度
			int w = select.getWidth();
			//w/2+l就等于点击view的中心点
			int x = l + w / 2 - width / 2;//找到中心点  点击选项卡滑动到中间位置
			home_scoll.smoothScrollTo(x, 0);//滑动
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
