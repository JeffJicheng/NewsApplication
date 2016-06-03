package com.yjc.adapter;

import java.util.List;

import com.yjc.activity.NewsActivity;
import com.yjc.bean.PinglunBean;
import com.yjc.mytopnews.R;
import com.yjc.utils.SystemUtils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 展示评论的PullToRefresh的适配器
 * @author yjc Jeff.Yao
 *
 */
public class NSPLAdapter extends BaseAdapter {
	private List<PinglunBean> datas;
	private NewsActivity act;
	
	public NSPLAdapter(List<PinglunBean> datas, NewsActivity act) {
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
		ViewHolder vh = null;
		if(arg1 == null){
			arg1 = act.getLayoutInflater().inflate(R.layout.item_nsplv_adapter, null);
			vh = new ViewHolder();
			vh.header  = (ImageView) arg1.findViewById(R.id.item_nsplv_header);
			vh.fromname = (TextView) arg1.findViewById(R.id.item_nsplv_fromname);
			vh.fromaddr = (TextView) arg1.findViewById(R.id.item_nsplv_fromaddr);
			vh.text = (TextView) arg1.findViewById(R.id.item_nsplv_text);
			arg1.setTag(vh);
		}else{
			vh = (ViewHolder) arg1.getTag();
		}
		SystemUtils.getBitMapUtils(act).display(vh.header, datas.get(arg0).getFromimg());
		vh.fromname.setText(datas.get(arg0).getFromname());
		vh.fromaddr.setText(datas.get(arg0).getFromaddr());
		vh.text.setText(datas.get(arg0).getText());
		return arg1;
	}
	static class ViewHolder{
		ImageView header;
		TextView fromname,fromaddr,text;
	}

}
