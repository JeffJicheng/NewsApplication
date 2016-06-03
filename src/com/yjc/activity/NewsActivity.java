package com.yjc.activity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.yjc.adapter.NSPLAdapter;
import com.yjc.app.MyApplication;
import com.yjc.base.BaseActivity;
import com.yjc.bean.NewsBean;
import com.yjc.bean.PinglunBean;
import com.yjc.bean.ShoucangBean;
import com.yjc.config.Config;
import com.yjc.customview.RedPointView;
import com.yjc.mytopnews.R;
import com.yjc.utils.JsonUtils;
import com.yjc.utils.SystemUtils;
/**
 * 查看新闻详细界面
 * @author Administrator
 *
 */
public class NewsActivity extends BaseActivity implements OnClickListener {
	private NewsActivity act;
	private NewsBean nb;//详细的新闻
	
	//----------顶部的控件----------
	private ImageView back;//退后
	private ImageView share;//分享
	//----------中间webView----------
	private WebView wv;//webView
	private ProgressBar news_pb;//加载网页的进度条
	
	//-----------底部的控件-----------
	private TextView write_Pinglun;//写评论的textView
	
	
	//------------评论的控件-------------------
	private LinearLayout news_ll;
	private PullToRefreshListView plv;
	//--------------评论的数据源-----------
	private List<PinglunBean> datas;
	//---------------评论的适配器---------------
	private NSPLAdapter adapter;
	//----------------评论的图片(显示消息按钮的图片)---------------
	private ImageView showpinglun;
	//-------------------判断新闻是否被收藏的标志位--------------
	private boolean isshoucang = false;
	//-------------收藏的图片按钮  星星------------------------------
	private ImageView shoucang;
 	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.act_news);
		act = this;
		MyApplication.instance.getActs().add(act);
		nb = (NewsBean) getIntent().getSerializableExtra("news");//得到新闻的具体内容
		initViews();
		initWebView();
		doViews();
		initRedPoint();//初始化气泡和评论个数
		initIsShowcang();
	}
 	
 	/**
 	 * 用来判断是否被收藏
 	 */
 	private void initIsShowcang() {
 		// TODO Auto-generated method stub
 		try {
			List<ShoucangBean> list = SystemUtils.getDbUtils(act).findAll(Selector.from(ShoucangBean.class).where("news_id", "=", nb.getNews_id()));
			if(list == null || list.size() == 0){
				isshoucang = false;
			}else{
				isshoucang = true;
				shoucang.setImageResource(R.drawable.ic_action_favor_on_normal);
			}
 		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
 	 * 初始化气泡和评论个数 
 	 */
	private void initRedPoint() {
		// TODO Auto-generated method stub
		//查询评论个数  在服务器查 利用Xutils HTTPUtils'
		String url = Config.baseurl + "PinglunCount";
		SystemUtils.getHttpUtils().send(HttpMethod.GET, url, callBack1);
		
	}
	/**
	 * WebView 的初始化
	 */
	private void initWebView() {
		// TODO Auto-generated method stub
		wv.getSettings().setBuiltInZoomControls(true);//支持缩放
		wv.getSettings().setJavaScriptEnabled(true);//支持java脚本
		wv.setWebViewClient(client);
		wv.addJavascriptInterface(new javaScriptInter(), "imglistener");//给个别名
		//用loadDataWithBaseURL可以加载图片 而用loadData(data, mimeType, encoding)加载不了图片
		//第一个null是因为我们不知道URL  第二个是html代码  第三个参数是html的内容形式  第四个参数是编码
		wv.loadDataWithBaseURL(null, nb.getText(), "text/html", "UTF-8", null);
	}
	/**
	 * 对控件操作
	 */
	private void doViews() {
		// TODO Auto-generated method stub
		back.setOnClickListener(this);
		share.setOnClickListener(this);
		write_Pinglun.setOnClickListener(this);
		plv.setOnRefreshListener(listener);
		plv.setRefreshing();//自动刷新
		showpinglun.setOnClickListener(this);
		shoucang.setOnClickListener(this);
	}
	/**
	 * 初始化控件
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		back = (ImageView) findViewById(R.id.news_header_left);
		share = (ImageView) findViewById(R.id.news_header_right);
		news_pb = (ProgressBar) findViewById(R.id.news_progressbar);
		wv = (WebView) findViewById(R.id.news_wv);
		write_Pinglun = (TextView) findViewById(R.id.news_bottom_pinglun);
		news_ll = (LinearLayout) findViewById(R.id.news_ll);
		plv = (PullToRefreshListView) findViewById(R.id.news_plv);
		showpinglun = (ImageView) findViewById(R.id.news_bottom_showpinglun);
		shoucang = (ImageView) findViewById(R.id.new_bottom_shoucang);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.instance.getActs().remove(act);
		super.onDestroy();
	}
	//-------------------------------------------------------
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		//back键
		case R.id.news_header_left:
			act.finish();
			break;
		//分享键
		case R.id.news_header_right:
			showShare();
			break;
		//写评论键
		case R.id.news_bottom_pinglun:
			MyApplication.instance.setNews_id(nb.getNews_id());//新闻
			//取出用户名 用来判断用户是否已经登录，若已登录则跳转到写评论的Activity，若没登录，则跳转到LoginActivity提示去登录
			String name = getSharedPreferences("user", Context.MODE_PRIVATE).getString("name", "");
			if(name == null || name.equals("")){
				Intent intent = new Intent(act,LoginActivity.class);
				startActivity(intent);
			}else {
				Intent intent = new Intent(act, PinglunActivity.class);
				startActivity(intent);
			}
			break;
			//显示评论
		case R.id.news_bottom_showpinglun:
			if(news_ll.getVisibility() == View.INVISIBLE){
				wv.setVisibility(View.INVISIBLE);
				news_ll.setVisibility(View.VISIBLE);
			}else{
				wv.setVisibility(View.VISIBLE);
				news_ll.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.new_bottom_shoucang:
			if(!isshoucang){
				//没有被收藏
				int news_id = nb.getNews_id();
				Document doc = Jsoup.parse(nb.getText());
				String title = doc.select("h1.title").first().text();
				String author = doc.select("span.author").first().text();
				String time = doc.select("span.time").first().text();
				ShoucangBean scb = new ShoucangBean(news_id, title, author, time);
				try {
					SystemUtils.getDbUtils(act).save(scb);
					shoucang.setImageResource(R.drawable.ic_action_favor_on_normal);
					shortToast("收藏成功");
					isshoucang = true;
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				//已经被收藏了
				try {
					SystemUtils.getDbUtils(act).delete(ShoucangBean.class, WhereBuilder.b("news_id", "=", nb.getNews_id()));
					shoucang.setImageResource(R.drawable.ic_action_favor_pressed);
					shortToast("取消收藏");
					isshoucang = false;
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		}
	}
	/**
	 * sharesdk的分享方法
	 */
	private void showShare() {
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 

		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle("分享");
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		 oks.setTitleUrl("http://sharesdk.cn");
		 // text是分享文本，所有平台都需要这个字段
		 Document doc = Jsoup.parse(nb.getText());
		 String title = doc.select("h1.title").first().text();
		 oks.setText("此新闻: \n"+title+"\n 有点意思");
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		 // url仅在微信（包括好友和朋友圈）中使用
//		 oks.setUrl("http://sharesdk.cn");
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		 oks.setComment("我是测试评论文本");
		 // site是分享此内容的网站名称，仅在QQ空间使用
//		 oks.setSite(getString(R.string.app_name));
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//		 oks.setSiteUrl("http://sharesdk.cn");

			// 启动分享GUI
			 oks.show(this);
		 }
	/**
	 * WebView的生命周期
	 */
	WebViewClient client = new WebViewClient(){
		//加载资源
		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			news_pb.setVisibility(View.VISIBLE);//显示
			news_pb.setProgress(60);//在加载资源时，给60
			super.onLoadResource(view, url);
		}
		//页面加载完毕 
		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			news_pb.setVisibility(View.VISIBLE);//显示
			news_pb.setProgress(100);//完成 100
			news_pb.setVisibility(View.INVISIBLE);//不显示
			AddJs();
			super.onPageFinished(view, url);
		}
		//页面加载开始
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			news_pb.setVisibility(View.VISIBLE);//显示
			news_pb.setProgress(20);//刚开始加载给20
			super.onPageStarted(view, url, favicon);
			
		}
		//loadurl的  若想用自己的布局来加载URL 必须要重写此方法  view.loadUrl(url); 
		//在这里我们不用此方法
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			return super.shouldOverrideUrlLoading(view, url);
		}
		
	};
	/**
	 * 定义一个js的接口
	 */
	class javaScriptInter{
		//传一个图片的url
		@JavascriptInterface
		public void openImg(String imgurl){
			Intent intent = new Intent(NewsActivity.this, showImgaActivity.class);
			intent.putExtra("imgurl", imgurl);//传url
			startActivity(intent);
			//切換Activity的动画 ,必须要在startActivity或者finish之后  紧挨着！！！
			overridePendingTransition(R.anim.sa_act_incoming, R.anim.aa_act_outexit);
		}
	}
	/**
	 * 往webview注入js脚本
	 */
	public void AddJs(){
		String url = "javascript:(function(){"
				+ "var objs = document.getElementsByTagName(\"img\"); "
				+ "for(var i=0;i<objs.length;i++) " + "{"
				+ "    objs[i].onclick=function()   "  + "   {  "
				+ "         window.imglistener.openImg(this.src);  "
				+ "     }  "  +  "}" + "})()";
		wv.loadUrl(url);
	}
	
	
	/**
	 * 在OnResume方法里面判断PinglunActivity是否刚刚关闭
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(MyApplication.instance.isPinglunAfter()){
			//评论Activity刚刚关闭
			wv.setVisibility(View.INVISIBLE);
			news_ll.setVisibility(View.VISIBLE);
			plv.setRefreshing();//自动刷新
		}
		super.onResume();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		MyApplication.instance.setPinglunAfter(false);
		super.onStop();
	}
	//---------------------------listView刷新-----------------------------------------
	private OnRefreshListener<ListView> listener = new OnRefreshListener<ListView>() {

		@Override
		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			/**
			 * bug  刚评论的内容没有更新
				服务器中 有
				客户端没有更新
				get请求的时候 对同一个url  频繁发送请求  服务器可能不做响应 而是使用缓存
				在这我们可以使用uuid来解决这个问题  uuid在服务器中不被识别，不会被解析，uuid可以作为随机码
				这样就可以是不同的一个url了，以此解决以上问题
			 */
			UUID uuid = UUID.randomUUID();
			String url = Config.baseurl + "PinglunByNews?news_id="+nb.getNews_id()+"&pagesize=-1&uuid="+(uuid.toString());
			SystemUtils.getHttpUtils().send(HttpMethod.GET, url, callBack);
		}
		
	};
	//----------------------------评论的下拉刷新--------------------------------
	private RequestCallBack<String> callBack = new RequestCallBack<String>() {
		
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			//评论数据
			datas = JsonUtils.getPinglunContent(arg0.result);
			Collections.reverse(datas);//将数据源颠倒，已达到新的评论呈现到第一条
			adapter = new NSPLAdapter(datas, act);
			plv.setAdapter(adapter);
			plv.onRefreshComplete();//刷新完成
		}
		
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			shortToast("刷新失败");
			loge(arg1);
		}
	};
	//-----------------请求查询气泡个数的callback1-----------------------
	private RequestCallBack<String> callBack1 = new RequestCallBack<String>() {
		
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			System.out.println("----------"+arg0);
			int count = 0;
			try {
				JSONObject jo = new JSONObject(arg0.result);
				if(jo.getString("result").equals("success")){
					System.out.println("----------ok1");
					count = jo.getInt("content");
//					shortToast("ok");
					System.out.println("----------ok2");
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				System.out.println("请求失败:"+e1.getMessage());
				e1.printStackTrace();
			}
			generateRedpoints(count);	
		}
		
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			System.out.println(arg1+"---------------------");
			loge(arg1);
		}
	};
	protected void generateRedpoints(int count) {
		// TODO Auto-generated method stub
		RedPointView rpv = new RedPointView(act, showpinglun);
		rpv.setColorBg(Color.RED);
		rpv.setColorContent(Color.WHITE);
		rpv.setSizeContent(10);
		rpv.setPosition(Gravity.RIGHT, Gravity.TOP);
	}
}
