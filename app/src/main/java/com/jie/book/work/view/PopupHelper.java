package com.jie.book.work.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.jie.book.work.utils.UIHelper;

public class PopupHelper {

	public enum PopGravity {
		BOTTOM_RIGHT, BOTTOM, LEFT, CENTER, TOP, TOP_CENTER;
	}

	public enum PopStyle {
		MATCH_PARENT, WRAP_CONTENT
	}

	public static PopupWindow newBasicPopupWindow(Context context, PopStyle popStyle) {
		final PopupWindow window = new PopupWindow(context);
		window.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					window.dismiss();
					return true;
				}
				return false;
			}
		});

		if (popStyle == PopStyle.MATCH_PARENT) {
			window.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
			window.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
		} else {
			window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
			window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		}
		window.setTouchable(true);
		window.setFocusable(true);
		window.setOutsideTouchable(true);

		window.setBackgroundDrawable(new BitmapDrawable());

		return window;
	}

	public static void showLocationPop(PopupWindow window, View anchor, PopGravity gravity) {
		int paddingTop = UIHelper.dipToPx(65);
		if (gravity == PopGravity.TOP_CENTER) {
			window.showAtLocation(anchor, Gravity.TOP | Gravity.CENTER, 0, paddingTop);
		} else if (gravity == PopGravity.CENTER) {
			window.showAtLocation(anchor, Gravity.CENTER, 0, 0);
		} else if (gravity == PopGravity.TOP) {
			window.showAtLocation(anchor, Gravity.TOP, 0, 0);
		} else if (gravity == PopGravity.BOTTOM_RIGHT) {
			window.showAtLocation(anchor, Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
		} else if (gravity == PopGravity.BOTTOM) {
			window.showAtLocation(anchor, Gravity.BOTTOM, 0, 0);
		}
	}

}
