package com.yjc.adapter;

import java.util.List;

import com.yjc.activity.EditPingdaoActivity;
import com.yjc.bean.CategoryBean;
import com.yjc.mytopnews.R;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * ÆµµÀ±à¼­ListView
 * @author yjc Jeff.yao
 *
 */
public class EditPinlgAdapter extends BaseAdapter {
	private List<CategoryBean> datas;
	private EditPingdaoActivity act;
	
	public EditPinlgAdapter(List<CategoryBean> datas, EditPingdaoActivity act) {
		super();
		this.datas = datas;
		this.act = act;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if(arg1 == null){
			arg1 = act.getLayoutInflater().inflate(R.layout.item_editpinglun_gv, null);
		}
		TextView tv = (TextView) arg1.findViewById(R.id.item_eidtpinglun_tv);
		tv.setText(datas.get(arg0).getText());
		return arg1;
	}

}
