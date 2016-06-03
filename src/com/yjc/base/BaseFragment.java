package com.yjc.base;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
/**
 * »ù²ãFragment
 * @author Administrator
 *
 */
public class BaseFragment extends Fragment {
	public View findviewbyid(int id){
		return getView().findViewById(id);
	}
	public void loge(String text){
		Log.e("yjc", text);
	}
}
