package com.jie.book.work.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.jie.book.work.R;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.LogUtil;

public class ProgressDialog extends Dialog {
	public ProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	private static ProgressDialog customProgressDialog = null;

	public static ProgressDialog createDialog(Context context) {
		customProgressDialog = new ProgressDialog(context, R.style.CustomDialog);
		customProgressDialog.setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.dialog_loading_ef
				: R.layout.dialog_loading);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		return customProgressDialog;
	}

	public static ProgressDialog createDialog(Context context, String content) {
		customProgressDialog = new ProgressDialog(context, R.style.CustomDialog);
		customProgressDialog.setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.dialog_loading_ef
				: R.layout.dialog_loading);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		TextView tvContent = (TextView) customProgressDialog.findViewById(R.id.progress_content);
		tvContent.setText(content);
		return customProgressDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		if (customProgressDialog == null) {
			return;
		}
		ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
		animationDrawable.start();
	}

	public ProgressDialog setTitile(String strTitle) {
		return customProgressDialog;
	}

	public void dismiss() {
		try {
			super.dismiss();
		} catch (Throwable t) {
			LogUtil.log(t);
		}
	}

}
