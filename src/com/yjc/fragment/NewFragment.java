package com.yjc.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.yjc.activity.HomeActivity;
import com.yjc.activity.NewsActivity;
import com.yjc.adapter.NFPLvAdapter;
import com.yjc.base.BaseFragment;
import com.yjc.bean.NewsBean;
import com.yjc.config.Config;
import com.yjc.mytopnews.R;
import com.yjc.utils.DimenUtils;
import com.yjc.utils.JsonUtils;
import com.yjc.utils.StringUtils;
import com.yjc.utils.SystemUtils;
/**
 * 创建一个Fragment类型 可以有多个实例对象
 * @author yjc Jeff.Yao
 *
 */
public class NewFragment extends BaseFragment implements OnItemClickListener {
	private HomeActivity act;
	private PullToRefreshListView plv;
	private NFPLvAdapter adapter;
	private List<NewsBean> datas;
	private int count = 1;
	
//	private NewFragment(){}
	public static NewFragment newInstance(int category_id){
		NewFragment nf = new NewFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("category_id", category_id);
		nf.setArguments(bundle);//用来保存数据，方便在各个周期调用，在生命周期之前绑定数据
		return nf;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		act = (HomeActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_home_vp, null);
	}
	/**
	 * activity创建
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initViews();
		initPlv();//对plv进行初始化设置
		super.onActivityCreated(savedInstanceState);
	}
	/*
	 * 对PullToRefreshListView进行设置
	 */
	private void initPlv() {
		// TODO Auto-generated method stub
		plv.setMode(Mode.BOTH);//下拉刷新和上拉加载都有
		ILoadingLayout start = plv.getLoadingLayoutProxy(true, false);//头部分
		start.setLastUpdatedLabel(StringUtils.getTime(System.currentTimeMillis()));
		start.setPullLabel("下拉刷新");
		start.setReleaseLabel("释放刷新");
		start.setRefreshingLabel("刷新中...");
		ILoadingLayout end = plv.getLoadingLayoutProxy(false, true);//尾部分
		end.setPullLabel("加载更多");
		end.setReleaseLabel("释放加载");
		end.setRefreshingLabel("加载中...");
		//得到里面的ListView 只有ListView才有selector 按压效果
		ListView lv = plv.getRefreshableView();
		lv.setSelector(R.drawable.sel_newfragment_home_vp_lv);
		lv.setCacheColorHint(Color.parseColor("#00000000"));
		lv.setDividerHeight(DimenUtils.dp2px(1, act));
		Drawable divider = getResources().getDrawable(R.drawable.sel_newfragment_home_vp_lv);
		lv.setDivider(divider);
		plv.setOnRefreshListener(listener);//刷新监听
		plv.setOnItemClickListener(this);//点击事件监听
		plv.setRefreshing();//自动刷新 或者 plv.setRefreshing(true);  
		//要实现自动刷新需要改动其源码 对空那里处理  因为我们是后面设置适配器的所以去掉空的判断
	}
	/*初始化Plv
	 *
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		plv = (PullToRefreshListView) findviewbyid(R.id.fragment_home_vp_plv);
	}
	//*********************监听***********************************
	/**
	 * 下拉刷新和上拉加载都有，所以选择是OnRefreshListener2 
	 */
	OnRefreshListener2<ListView> listener = new OnRefreshListener2<ListView>() {
		//下拉刷新
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			// TODO Auto-generated method stub
			act.changeHeader();
			//得到服务器的新闻类型的id
			int id = getArguments().getInt("category_id");
			String url = Config.baseurl+"NewsByCategory?category_id="+id+"&&pagesize=-1";
			//发Http请求
			SystemUtils.getHttpUtils().send(HttpMethod.GET, url, callBack);
			
		}
		//上拉加载
		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// TODO Auto-generated method stub
			//count初始值为1 加载是第几页的数据，所以在做适配器的时候，需要把之前的数据加在一起
			count++;
			int id = getArguments().getInt("category_id");
			String url = Config.baseurl+"NewsByCategory?category_id="+id+"&&pagesize="+count;
			SystemUtils.getHttpUtils().send(HttpMethod.GET, url, callBack1);
			
		}
	};
	//下拉刷新的callback
	RequestCallBack<String> callBack = new RequestCallBack<String>() {
		//刷新成功
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			datas = new ArrayList<NewsBean>();//为了防止 报null指针异常，所以先new出来 初始化
			datas = JsonUtils.getNewsByCategory(arg0.result);//Json解析，具体用到gson
			adapter = new NFPLvAdapter(datas, act);//对适配器进行更换  因为数据变了
			plv.setAdapter(adapter);//设置适配器
			plv.onRefreshComplete();//刷新完成
			//刷新完后让刷新图标显示
			act.changeHeader();
		}
		//刷新失败  log打印
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			loge(arg1);
		}
	};
	//上拉加载的callback1
	RequestCallBack<String> callBack1 = new RequestCallBack<String>() {
		//加载失败 打印log
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			loge(arg1);
		}
		//加载成功 
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			List<NewsBean> lists = JsonUtils.getNewsByCategory(arg0.result);//取得第几页的数据
			//将之前的数据加载一起
			datas.addAll(lists);
			//对适配器进行改变，注意是改变而不是更换
			adapter.notifyDataSetChanged();
			//plv刷新完成
			plv.onRefreshComplete();
		}
	};
	/**
	 * 让HomeActivity控制 点击刷新图标进行刷新
	 */
	public void setRefresh(){
		plv.setRefreshing();
	}
	//***********************plv的Item点击监听*****************************
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		//跳转到新闻的详细页面
		Intent intent = new Intent(act, NewsActivity.class);
		//将新闻具体内容带到另外一个Activity
		intent.putExtra("news", datas.get(arg2-1));//减1的原因是plv的下标是从1开始的，因为头部分也占了位置，下标为0
		startActivity(intent);
		
	}
	//***********************plv的Item点击监听*****************************
}
	