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
 * ����һ��Fragment���� �����ж��ʵ������
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
		nf.setArguments(bundle);//�����������ݣ������ڸ������ڵ��ã�����������֮ǰ������
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
	 * activity����
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initViews();
		initPlv();//��plv���г�ʼ������
		super.onActivityCreated(savedInstanceState);
	}
	/*
	 * ��PullToRefreshListView��������
	 */
	private void initPlv() {
		// TODO Auto-generated method stub
		plv.setMode(Mode.BOTH);//����ˢ�º��������ض���
		ILoadingLayout start = plv.getLoadingLayoutProxy(true, false);//ͷ����
		start.setLastUpdatedLabel(StringUtils.getTime(System.currentTimeMillis()));
		start.setPullLabel("����ˢ��");
		start.setReleaseLabel("�ͷ�ˢ��");
		start.setRefreshingLabel("ˢ����...");
		ILoadingLayout end = plv.getLoadingLayoutProxy(false, true);//β����
		end.setPullLabel("���ظ���");
		end.setReleaseLabel("�ͷż���");
		end.setRefreshingLabel("������...");
		//�õ������ListView ֻ��ListView����selector ��ѹЧ��
		ListView lv = plv.getRefreshableView();
		lv.setSelector(R.drawable.sel_newfragment_home_vp_lv);
		lv.setCacheColorHint(Color.parseColor("#00000000"));
		lv.setDividerHeight(DimenUtils.dp2px(1, act));
		Drawable divider = getResources().getDrawable(R.drawable.sel_newfragment_home_vp_lv);
		lv.setDivider(divider);
		plv.setOnRefreshListener(listener);//ˢ�¼���
		plv.setOnItemClickListener(this);//����¼�����
		plv.setRefreshing();//�Զ�ˢ�� ���� plv.setRefreshing(true);  
		//Ҫʵ���Զ�ˢ����Ҫ�Ķ���Դ�� �Կ����ﴦ��  ��Ϊ�����Ǻ�������������������ȥ���յ��ж�
	}
	/*��ʼ��Plv
	 *
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		plv = (PullToRefreshListView) findviewbyid(R.id.fragment_home_vp_plv);
	}
	//*********************����***********************************
	/**
	 * ����ˢ�º��������ض��У�����ѡ����OnRefreshListener2 
	 */
	OnRefreshListener2<ListView> listener = new OnRefreshListener2<ListView>() {
		//����ˢ��
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			// TODO Auto-generated method stub
			act.changeHeader();
			//�õ����������������͵�id
			int id = getArguments().getInt("category_id");
			String url = Config.baseurl+"NewsByCategory?category_id="+id+"&&pagesize=-1";
			//��Http����
			SystemUtils.getHttpUtils().send(HttpMethod.GET, url, callBack);
			
		}
		//��������
		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// TODO Auto-generated method stub
			//count��ʼֵΪ1 �����ǵڼ�ҳ�����ݣ�����������������ʱ����Ҫ��֮ǰ�����ݼ���һ��
			count++;
			int id = getArguments().getInt("category_id");
			String url = Config.baseurl+"NewsByCategory?category_id="+id+"&&pagesize="+count;
			SystemUtils.getHttpUtils().send(HttpMethod.GET, url, callBack1);
			
		}
	};
	//����ˢ�µ�callback
	RequestCallBack<String> callBack = new RequestCallBack<String>() {
		//ˢ�³ɹ�
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			datas = new ArrayList<NewsBean>();//Ϊ�˷�ֹ ��nullָ���쳣��������new���� ��ʼ��
			datas = JsonUtils.getNewsByCategory(arg0.result);//Json�����������õ�gson
			adapter = new NFPLvAdapter(datas, act);//�����������и���  ��Ϊ���ݱ���
			plv.setAdapter(adapter);//����������
			plv.onRefreshComplete();//ˢ�����
			//ˢ�������ˢ��ͼ����ʾ
			act.changeHeader();
		}
		//ˢ��ʧ��  log��ӡ
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			loge(arg1);
		}
	};
	//�������ص�callback1
	RequestCallBack<String> callBack1 = new RequestCallBack<String>() {
		//����ʧ�� ��ӡlog
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			loge(arg1);
		}
		//���سɹ� 
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			List<NewsBean> lists = JsonUtils.getNewsByCategory(arg0.result);//ȡ�õڼ�ҳ������
			//��֮ǰ�����ݼ���һ��
			datas.addAll(lists);
			//�����������иı䣬ע���Ǹı�����Ǹ���
			adapter.notifyDataSetChanged();
			//plvˢ�����
			plv.onRefreshComplete();
		}
	};
	/**
	 * ��HomeActivity���� ���ˢ��ͼ�����ˢ��
	 */
	public void setRefresh(){
		plv.setRefreshing();
	}
	//***********************plv��Item�������*****************************
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		//��ת�����ŵ���ϸҳ��
		Intent intent = new Intent(act, NewsActivity.class);
		//�����ž������ݴ�������һ��Activity
		intent.putExtra("news", datas.get(arg2-1));//��1��ԭ����plv���±��Ǵ�1��ʼ�ģ���Ϊͷ����Ҳռ��λ�ã��±�Ϊ0
		startActivity(intent);
		
	}
	//***********************plv��Item�������*****************************
}
	