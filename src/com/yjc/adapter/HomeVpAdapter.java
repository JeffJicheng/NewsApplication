package com.yjc.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
/**
 * 首页的新闻ViewPager的适配器 继承FragmentPagerAdapter
 * @author yjc Jeff.Yao
 *
 */
public class HomeVpAdapter extends FragmentPagerAdapter{
	private List<Fragment> fragments;
	public HomeVpAdapter(FragmentManager fragmentManager,List<Fragment> datas) {
		super(fragmentManager);
		// TODO Auto-generated constructor stub
		this.fragments = datas;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.size();
	}

}
