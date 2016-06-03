package com.yjc.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.yjc.adapter.EditPinlgAdapter;
import com.yjc.app.MyApplication;
import com.yjc.base.BaseActivity;
import com.yjc.bean.CategoryBean;
import com.yjc.config.Config;
import com.yjc.mytopnews.R;
import com.yjc.utils.JsonUtils;
import com.yjc.utils.SystemUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
/**
 * 导航栏频道编辑
 * @author yjc Jeff.yao
 *
 */
public class EditPingdaoActivity extends BaseActivity implements OnClickListener {
	private GridView sel_gv,un_gv;
	private EditPingdaoActivity act;
	private EditPinlgAdapter sel_adapter;
	private EditPinlgAdapter un_adapter;
	private List<CategoryBean> sel_datas;
	private List<CategoryBean> un_datas;
	private ImageView editpingdao_back;
	private int sel_pos;
	private int un_pos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		act = this;
		MyApplication.instance.getActs().add(act);
		MyApplication.instance.setEditPingdaoAfter(false);
		setContentView(R.layout.act_editpingdao);
		initViews();
		doViews();
		initDatas();
	}
	/**
	 * 数据源初始化
	 */
	private void initDatas() {
		// TODO Auto-generated method stub
		UUID uuid1 = UUID.randomUUID();
		String url = Config.baseurl+"CategoryBySelect?select=1&uuid="+uuid1.toString();
		SystemUtils.getHttpUtils().send(HttpMethod.GET, url, callBack);
		UUID uuid2 = UUID.randomUUID();
		String url1 = Config.baseurl + "CategoryBySelect?select=0&uuid="+uuid2.toString();
		SystemUtils.getHttpUtils().send(HttpMethod.GET, url1, callBack1);
	}
	/**
	 * 操作
	 */
	private void doViews() {
		// TODO Auto-generated method stub
		editpingdao_back.setOnClickListener(this);
		sel_gv.setOnItemClickListener(sel_listener);
		un_gv.setOnItemClickListener(un_listener);
	}
	/**
	 * 初始化控件
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		sel_gv = (GridView) findViewById(R.id.editpingdao_gv_sel);
		un_gv = (GridView) findViewById(R.id.editpingdao_gv_un);
		editpingdao_back = (ImageView) findViewById(R.id.editpingdao_back);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.instance.getActs().remove(act);
		super.onDestroy();
	}
	//-----------------------请求事件监听方法 ->已选中的类别查询-----------------------------------
	private RequestCallBack<String> callBack = new RequestCallBack<String>() {
		
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			sel_datas = JsonUtils.getCategroyContent(arg0.result);
			sel_adapter = new EditPinlgAdapter(sel_datas, act);
			sel_gv.setAdapter(sel_adapter);
		}
		
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			loge(arg0.getMessage()+":"+arg1);
		}
	};
	//-----------------------------请求事件监听方法 ->未选中的类别查询--------------------------------------
	private RequestCallBack<String> callBack1 = new RequestCallBack<String>() {
		
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			un_datas = JsonUtils.getCategroyContent(arg0.result);
			un_adapter = new EditPinlgAdapter(un_datas, act);
			un_gv.setAdapter(un_adapter);
		}
		
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			loge(arg0.getMessage()+":"+arg1);
		}
	};
	//-------------------back--------------------------
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.editpingdao_back:
			act.finish();
			break;
		}
	}
	//----------------------GridView点击监听事件---------------------------------
	private OnItemClickListener sel_listener = new OnItemClickListener() {

		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if(arg2 > 2){
				sel_pos = arg2;
				String url = Config.baseurl + "CategoryAddSelect";
				RequestParams params = new RequestParams();
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("category_id", sel_datas.get(arg2).getCategory_id()+""));
				nameValuePairs.add(new BasicNameValuePair("select", 0+""));
				params.addBodyParameter(nameValuePairs);
				SystemUtils.getHttpUtils().send(HttpMethod.POST, url, params, sel_callBack);
			}
		}
	}; 
	
	private OnItemClickListener un_listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			un_pos = arg2;
			String url = Config.baseurl + "CategoryAddSelect";
			RequestParams params = new RequestParams();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("category_id", un_datas.get(arg2).getCategory_id()+""));
			nameValuePairs.add(new BasicNameValuePair("select", 1+""));
			params.addBodyParameter(nameValuePairs);
			SystemUtils.getHttpUtils().send(HttpMethod.POST, url, params, un_callBack);
		}
	};
	private RequestCallBack<String> sel_callBack = new RequestCallBack<String>() {
		
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			JSONObject jo = null;
			try {
				jo = new JSONObject(arg0.result);
				if(jo.getString("result").equals("success")){
					un_datas.add(sel_datas.remove(sel_pos));
					sel_adapter.notifyDataSetChanged();
					un_adapter.notifyDataSetChanged();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			loge(arg0.getMessage()+"----" + arg1);
		}
	};
	private RequestCallBack<String> un_callBack = new RequestCallBack<String>() {
		
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			JSONObject jo = null;
			try {
				jo = new JSONObject(arg0.result);
				if(jo.getString("result").equals("success")){
					sel_datas.add(un_datas.remove(un_pos));
					sel_adapter.notifyDataSetChanged();
					un_adapter.notifyDataSetChanged();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			loge(arg0.getMessage()+"----" + arg1);
		}
	};
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		MyApplication.instance.setEditPingdaoAfter(true);
		super.onPause();
	}
	
}
