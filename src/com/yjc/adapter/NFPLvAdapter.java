package com.yjc.adapter;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.yjc.activity.HomeActivity;
import com.yjc.bean.NewsBean;
import com.yjc.config.Config;
import com.yjc.mytopnews.R;
import com.yjc.utils.SystemUtils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 新闻Fragment的PullToRefreshListView的适配器
 * @author yjc Jeff.Yao
 *
 */
public class NFPLvAdapter extends BaseAdapter {
	private List<NewsBean> datas;
	private HomeActivity act;
	
	public NFPLvAdapter(List<NewsBean> datas, HomeActivity act) {
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
		int type = getItemViewType(arg0);
		ToutiaoHolder tth = null;//0
		DantuHolder dth = null;//1
		DuotuHolder doth = null;//2
		WutuHolder wth = null;//3
		if(arg1 == null){
			switch (type) {
			case 0:
				arg1 = act.getLayoutInflater().inflate(R.layout.item_nfplv_adapter_toutiao, null);
				tth = new ToutiaoHolder();
				tth.title = (TextView) arg1.findViewById(R.id.item_nfplv_adapter_toutiao_title);
				tth.img = (ImageView) arg1.findViewById(R.id.item_nfplv_adapter_toutiao_img);
				tth.source = (TextView) arg1.findViewById(R.id.item_nfplv_adapter_toutiao_source);
				tth.time = (TextView) arg1.findViewById(R.id.item_nfplv_adapter_toutiao_time);
				//设置控件
				setHolder(tth,datas.get(arg0));
				arg1.setTag(tth);
				break;
			case 1:
				arg1 = act.getLayoutInflater().inflate(R.layout.item_nfplv_adapter_dantu, null);
				dth = new DantuHolder();
				dth.title = (TextView) arg1.findViewById(R.id.item_nfplv_adapter_dantu_title);
				dth.source = (TextView) arg1.findViewById(R.id.item_nfplv_adapter_dantu_source);
				dth.time = (TextView) arg1.findViewById(R.id.item_nfplv_adapter_dantu_time);
				dth.img = (ImageView) arg1.findViewById(R.id.item_nfplv_adapter_dantu_img);
				setHolder(dth, datas.get(arg0));
				arg1.setTag(dth);
				break;
			case 2:
				arg1 = act.getLayoutInflater().inflate(R.layout.item_nfplv_adapter_duotu, null);
				doth = new DuotuHolder();
				doth.title = (TextView) arg1.findViewById(R.id.item_nfplv_adapter_duotu_title);
				doth.source = (TextView) arg1.findViewById(R.id.item_nfplv_adapter_duotu_source);
				doth.time = (TextView) arg1.findViewById(R.id.item_nfplv_adapter_duotu_time);
				doth.img1 = (ImageView) arg1.findViewById(R.id.item_nfplv_adapter_duotu_img1);
				doth.img2 = (ImageView) arg1.findViewById(R.id.item_nfplv_adapter_duotu_img2);
				doth.img3 = (ImageView) arg1.findViewById(R.id.item_nfplv_adapter_duotu_img3);
				setHolder(doth, datas.get(arg0));
				arg1.setTag(doth);
				break;
			case 3:
				arg1 = act.getLayoutInflater().inflate(R.layout.item_nfplv_adapter_wutu, null);
				wth = new WutuHolder();
				wth.title = (TextView) arg1.findViewById(R.id.item_nfplv_adapter_wutu_title);
				wth.source = (TextView) arg1.findViewById(R.id.item_nfplv_adapter_wutu_source);
				wth.time = (TextView) arg1.findViewById(R.id.item_nfplv_adapter_wutu_time);
				setHolder(wth, datas.get(arg0));
				arg1.setTag(wth);
				break;
			}
		}else{
			switch (type) {
			case 0:
				tth = (ToutiaoHolder) arg1.getTag();
				setHolder(tth, datas.get(arg0));
				break;
			case 1:
				dth = (DantuHolder) arg1.getTag();
				setHolder(dth, datas.get(arg0));
				break;
			case 2:
				doth = (DuotuHolder) arg1.getTag();
				setHolder(doth, datas.get(arg0));
				break;
			case 3:
				wth = (WutuHolder) arg1.getTag();
				setHolder(wth, datas.get(arg0));
				break;
			}
		}
		
		return arg1;
	}
	
	
	
	private void setHolder(Object obj, NewsBean nfpLvBean) {
		// TODO Auto-generated method stub
		Document doc = Jsoup.parse(nfpLvBean.getText());
		if(obj instanceof ToutiaoHolder){
			ToutiaoHolder tth = (ToutiaoHolder) obj;
			tth.title.setText(doc.select("h1.title").first().text());
			tth.source.setText(doc.select("span.author").first().text());
			tth.time.setText(doc.select("span.time").first().text());
			String imgurl = doc.select("img[src]").first().attr("abs:src");
			SystemUtils.getBitMapUtils(act).display(tth.img, imgurl);
		}else if(obj instanceof DantuHolder){
			DantuHolder dth = (DantuHolder) obj;
			dth.title.setText(doc.select("h1.title").first().text());
			dth.source.setText(doc.select("span.author").first().text());
			dth.time.setText(doc.select("span.time").first().text());
			String imgurl = doc.select("img[src]").first().attr("abs:src");
			SystemUtils.getBitMapUtils(act).display(dth.img, imgurl);
		}else if(obj instanceof DuotuHolder){
			DuotuHolder doth = (DuotuHolder) obj;
			doth.title.setText(doc.select("h1.title").first().text());
			doth.source.setText(doc.select("span.author").first().text());
			doth.time.setText(doc.select("span.time").first().text());
			Elements imgs = doc.select("img[src]");
			String[] urls = new String[imgs.size()];
			for (int i = 0; i < imgs.size(); i++) {
				Element e = imgs.get(i);
				urls[i] = e.attr("abs:src");
			}
			SystemUtils.getBitMapUtils(act).display(doth.img1, urls[0]);
			SystemUtils.getBitMapUtils(act).display(doth.img1, urls[1]);
			SystemUtils.getBitMapUtils(act).display(doth.img1, urls[2]);
		}else {
			WutuHolder wth = (WutuHolder) obj;
			wth.title.setText(doc.select("h1.title").first().text());
			wth.source.setText(doc.select("span.author").first().text());
			wth.time.setText(doc.select("span.time").first().text());
		}
	}

	//4个布局
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 4;
	}
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if(datas.get(position).getLeixing().equals(Config.toutiao)){
			return 0;
		}else if(datas.get(position).getLeixing().equals(Config.dantu)){
			return 1;
		}else if(datas.get(position).getLeixing().equals(Config.duotu)){
			return 2;
		}else{
			return 3;
		}
	}
	static class ToutiaoHolder{
		public TextView title;
		public ImageView img;
		public TextView source;
		public TextView time;
	}
	static class DantuHolder{
		public TextView title;
		public ImageView img;
		public TextView source;
		public TextView time;
	}
	static class WutuHolder{
		public TextView title;
		public TextView source;
		public TextView time;
	}
	static class DuotuHolder{
		public TextView title;
		public TextView source;
		public TextView time;
		public ImageView img1,img2,img3;
	}
	

}
