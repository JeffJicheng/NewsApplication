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
 * �鿴������ϸ����
 * @author Administrator
 *
 */
public class NewsActivity extends BaseActivity implements OnClickListener {
	private NewsActivity act;
	private NewsBean nb;//��ϸ������
	
	//----------�����Ŀؼ�----------
	private ImageView back;//�˺�
	private ImageView share;//����
	//----------�м�webView----------
	private WebView wv;//webView
	private ProgressBar news_pb;//������ҳ�Ľ�����
	
	//-----------�ײ��Ŀؼ�-----------
	private TextView write_Pinglun;//д���۵�textView
	
	
	//------------���۵Ŀؼ�-------------------
	private LinearLayout news_ll;
	private PullToRefreshListView plv;
	//--------------���۵�����Դ-----------
	private List<PinglunBean> datas;
	//---------------���۵�������---------------
	private NSPLAdapter adapter;
	//----------------���۵�ͼƬ(��ʾ��Ϣ��ť��ͼƬ)---------------
	private ImageView showpinglun;
	//-------------------�ж������Ƿ��ղصı�־λ--------------
	private boolean isshoucang = false;
	//-------------�ղص�ͼƬ��ť  ����------------------------------
	private ImageView shoucang;
 	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.act_news);
		act = this;
		MyApplication.instance.getActs().add(act);
		nb = (NewsBean) getIntent().getSerializableExtra("news");//�õ����ŵľ�������
		initViews();
		initWebView();
		doViews();
		initRedPoint();//��ʼ�����ݺ����۸���
		initIsShowcang();
	}
 	
 	/**
 	 * �����ж��Ƿ��ղ�
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
 	 * ��ʼ�����ݺ����۸��� 
 	 */
	private void initRedPoint() {
		// TODO Auto-generated method stub
		//��ѯ���۸���  �ڷ������� ����Xutils HTTPUtils'
		String url = Config.baseurl + "PinglunCount";
		SystemUtils.getHttpUtils().send(HttpMethod.GET, url, callBack1);
		
	}
	/**
	 * WebView �ĳ�ʼ��
	 */
	private void initWebView() {
		// TODO Auto-generated method stub
		wv.getSettings().setBuiltInZoomControls(true);//֧������
		wv.getSettings().setJavaScriptEnabled(true);//֧��java�ű�
		wv.setWebViewClient(client);
		wv.addJavascriptInterface(new javaScriptInter(), "imglistener");//��������
		//��loadDataWithBaseURL���Լ���ͼƬ ����loadData(data, mimeType, encoding)���ز���ͼƬ
		//��һ��null����Ϊ���ǲ�֪��URL  �ڶ�����html����  ������������html��������ʽ  ���ĸ������Ǳ���
		wv.loadDataWithBaseURL(null, nb.getText(), "text/html", "UTF-8", null);
	}
	/**
	 * �Կؼ�����
	 */
	private void doViews() {
		// TODO Auto-generated method stub
		back.setOnClickListener(this);
		share.setOnClickListener(this);
		write_Pinglun.setOnClickListener(this);
		plv.setOnRefreshListener(listener);
		plv.setRefreshing();//�Զ�ˢ��
		showpinglun.setOnClickListener(this);
		shoucang.setOnClickListener(this);
	}
	/**
	 * ��ʼ���ؼ�
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
		//back��
		case R.id.news_header_left:
			act.finish();
			break;
		//�����
		case R.id.news_header_right:
			showShare();
			break;
		//д���ۼ�
		case R.id.news_bottom_pinglun:
			MyApplication.instance.setNews_id(nb.getNews_id());//����
			//ȡ���û��� �����ж��û��Ƿ��Ѿ���¼�����ѵ�¼����ת��д���۵�Activity����û��¼������ת��LoginActivity��ʾȥ��¼
			String name = getSharedPreferences("user", Context.MODE_PRIVATE).getString("name", "");
			if(name == null || name.equals("")){
				Intent intent = new Intent(act,LoginActivity.class);
				startActivity(intent);
			}else {
				Intent intent = new Intent(act, PinglunActivity.class);
				startActivity(intent);
			}
			break;
			//��ʾ����
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
				//û�б��ղ�
				int news_id = nb.getNews_id();
				Document doc = Jsoup.parse(nb.getText());
				String title = doc.select("h1.title").first().text();
				String author = doc.select("span.author").first().text();
				String time = doc.select("span.time").first().text();
				ShoucangBean scb = new ShoucangBean(news_id, title, author, time);
				try {
					SystemUtils.getDbUtils(act).save(scb);
					shoucang.setImageResource(R.drawable.ic_action_favor_on_normal);
					shortToast("�ղسɹ�");
					isshoucang = true;
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				//�Ѿ����ղ���
				try {
					SystemUtils.getDbUtils(act).delete(ShoucangBean.class, WhereBuilder.b("news_id", "=", nb.getNews_id()));
					shoucang.setImageResource(R.drawable.ic_action_favor_pressed);
					shortToast("ȡ���ղ�");
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
	 * sharesdk�ķ�����
	 */
	private void showShare() {
		 OnekeyShare oks = new OnekeyShare();
		 //�ر�sso��Ȩ
		 oks.disableSSOWhenAuthorize(); 

		// ����ʱNotification��ͼ�������  2.5.9�Ժ�İ汾�����ô˷���
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
		 oks.setTitle("����");
		 // titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
//		 oks.setTitleUrl("http://sharesdk.cn");
		 // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		 Document doc = Jsoup.parse(nb.getText());
		 String title = doc.select("h1.title").first().text();
		 oks.setText("������: \n"+title+"\n �е���˼");
		 // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		 //oks.setImagePath("/sdcard/test.jpg");//ȷ��SDcard������ڴ���ͼƬ
		 // url����΢�ţ��������Ѻ�����Ȧ����ʹ��
//		 oks.setUrl("http://sharesdk.cn");
		 // comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
//		 oks.setComment("���ǲ��������ı�");
		 // site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
//		 oks.setSite(getString(R.string.app_name));
		 // siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
//		 oks.setSiteUrl("http://sharesdk.cn");

			// ��������GUI
			 oks.show(this);
		 }
	/**
	 * WebView����������
	 */
	WebViewClient client = new WebViewClient(){
		//������Դ
		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			news_pb.setVisibility(View.VISIBLE);//��ʾ
			news_pb.setProgress(60);//�ڼ�����Դʱ����60
			super.onLoadResource(view, url);
		}
		//ҳ�������� 
		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			news_pb.setVisibility(View.VISIBLE);//��ʾ
			news_pb.setProgress(100);//��� 100
			news_pb.setVisibility(View.INVISIBLE);//����ʾ
			AddJs();
			super.onPageFinished(view, url);
		}
		//ҳ����ؿ�ʼ
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			news_pb.setVisibility(View.VISIBLE);//��ʾ
			news_pb.setProgress(20);//�տ�ʼ���ظ�20
			super.onPageStarted(view, url, favicon);
			
		}
		//loadurl��  �������Լ��Ĳ���������URL ����Ҫ��д�˷���  view.loadUrl(url); 
		//���������ǲ��ô˷���
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			return super.shouldOverrideUrlLoading(view, url);
		}
		
	};
	/**
	 * ����һ��js�Ľӿ�
	 */
	class javaScriptInter{
		//��һ��ͼƬ��url
		@JavascriptInterface
		public void openImg(String imgurl){
			Intent intent = new Intent(NewsActivity.this, showImgaActivity.class);
			intent.putExtra("imgurl", imgurl);//��url
			startActivity(intent);
			//�ГQActivity�Ķ��� ,����Ҫ��startActivity����finish֮��  �����ţ�����
			overridePendingTransition(R.anim.sa_act_incoming, R.anim.aa_act_outexit);
		}
	}
	/**
	 * ��webviewע��js�ű�
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
	 * ��OnResume���������ж�PinglunActivity�Ƿ�ոչر�
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(MyApplication.instance.isPinglunAfter()){
			//����Activity�ոչر�
			wv.setVisibility(View.INVISIBLE);
			news_ll.setVisibility(View.VISIBLE);
			plv.setRefreshing();//�Զ�ˢ��
		}
		super.onResume();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		MyApplication.instance.setPinglunAfter(false);
		super.onStop();
	}
	//---------------------------listViewˢ��-----------------------------------------
	private OnRefreshListener<ListView> listener = new OnRefreshListener<ListView>() {

		@Override
		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			/**
			 * bug  �����۵�����û�и���
				�������� ��
				�ͻ���û�и���
				get�����ʱ�� ��ͬһ��url  Ƶ����������  ���������ܲ�����Ӧ ����ʹ�û���
				�������ǿ���ʹ��uuid������������  uuid�ڷ������в���ʶ�𣬲��ᱻ������uuid������Ϊ�����
				�����Ϳ����ǲ�ͬ��һ��url�ˣ��Դ˽����������
			 */
			UUID uuid = UUID.randomUUID();
			String url = Config.baseurl + "PinglunByNews?news_id="+nb.getNews_id()+"&pagesize=-1&uuid="+(uuid.toString());
			SystemUtils.getHttpUtils().send(HttpMethod.GET, url, callBack);
		}
		
	};
	//----------------------------���۵�����ˢ��--------------------------------
	private RequestCallBack<String> callBack = new RequestCallBack<String>() {
		
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			//��������
			datas = JsonUtils.getPinglunContent(arg0.result);
			Collections.reverse(datas);//������Դ�ߵ����Ѵﵽ�µ����۳��ֵ���һ��
			adapter = new NSPLAdapter(datas, act);
			plv.setAdapter(adapter);
			plv.onRefreshComplete();//ˢ�����
		}
		
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			shortToast("ˢ��ʧ��");
			loge(arg1);
		}
	};
	//-----------------�����ѯ���ݸ�����callback1-----------------------
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
				System.out.println("����ʧ��:"+e1.getMessage());
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
