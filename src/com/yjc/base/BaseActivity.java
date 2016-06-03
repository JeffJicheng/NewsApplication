package com.yjc.base;

import cn.pedant.SweetAlert.SweetAlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
/**
 * ����Activity
 * @author yjc jeff.yao
 *
 */
public class BaseActivity extends FragmentActivity {
	/**
	 * �����Ի���
	 * @param title
	 * @param text
	 * @param positive
	 * @param cancel
	 */
	public void showSweetDialog(String title,String text,SweetAlertDialog.OnSweetClickListener positive,SweetAlertDialog.OnSweetClickListener cancel){
			new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)//�����ģʽ
			.setTitleText(title)
			.setContentText(text)
			.setConfirmText("ȷ��")
			.setCancelText("ȡ��")
			.setConfirmClickListener(positive)
			.setCancelClickListener(cancel)
			.show();
	}
	/**
	 * �������ضԻ���
	 * @param text
	 * @return
	 */
	public SweetAlertDialog showSweetDialogProgress(String text){
		SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
		pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
		pDialog.setTitleText(text);
		pDialog.setCancelable(false);
		pDialog.show();
		return pDialog;
	}
	public View findviewbyid(View parentview,int id){
		return parentview.findViewById(id);
	}
	public void shortToast(String text){
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}
	public void longToast(String text){
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}
	public void centerToast(String text){
		Toast to = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		to.setGravity(Gravity.CENTER, 0, 0);
		to.show();
	}
	public void loge(String text){
		Log.e("yjc", text);
	}
}
