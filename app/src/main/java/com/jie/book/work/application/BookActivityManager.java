package com.jie.book.work.application;

import java.lang.reflect.Method;
import java.util.LinkedList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.baidu.ops.appunion.sdk.AppUnionSDK;
import com.jie.book.work.R;
import com.jie.book.work.activity.BaseActivity;

/**
 * 这是一个单例模式的 activity管理器，没创建一个activity就加入到这个栈内，finish就将 它移除，
 * 当应用退出时，遍历栈内activity，并且finish退出
 * 
 * @author cwj
 * 
 */
public class BookActivityManager {
	private static BookActivityManager instance = null;
	private LinkedList<Activity> acts;
	public final static int Result_CODE = 100;

	private BookActivityManager() {
		acts = new LinkedList<Activity>();
	};

	public static BookActivityManager getInstance() {
		if (instance == null) {
			instance = new BookActivityManager();
		}
		return instance;
	}

	public LinkedList<Activity> getActs() {
		return acts;
	}

	public void addActivity(Activity act) {
		acts.add(act);
	}

	public void removeActivity(Activity act) {
		if (acts != null && acts.indexOf(act) >= 0) {
			acts.remove(act);
		}
	}

	public Activity getTopActivity() {
		return (acts == null || acts.size() <= 0) ? null : acts.get(acts.size() - 1);
	}

	public void close() {
		Activity act;
		while (acts.size() != 0) {
			act = acts.poll();
			act.finish();
		}
	}

	public void ExitApp(BaseActivity context) {
		try {
			close();
			AppUnionSDK.getInstance(context).quitSdk();
			NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancelAll();
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			Method killBackgroundProcesses = am.getClass().getDeclaredMethod("killBackgroundProcesses", String.class);
			killBackgroundProcesses.setAccessible(true);
			killBackgroundProcesses.invoke(am, context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void goTo(Activity self, Intent it) {
		self.startActivity(it);
		self.overridePendingTransition(R.anim.open_next, R.anim.close_main);
	}

	public void goTo(Activity self, Intent it, int enterAnim, int exitAnim) {
		self.startActivity(it);
		if (enterAnim != 0 && exitAnim != 0)
			self.overridePendingTransition(enterAnim, exitAnim);
	}

	public void goFoResult(Activity self, Intent it, int RequestCode) {
		self.startActivityForResult(it, RequestCode);
		self.overridePendingTransition(R.anim.open_next, R.anim.close_main);
	}
}
