package com.jie.book.work.utils;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bond.bookcatch.vo.BookDesc;
import com.jie.book.work.R;
import com.jie.book.work.activity.BaseActivity;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.service.DownloadService;
import com.jie.book.work.view.ProgressDialog;
import com.umeng.update.UpdateResponse;

public class UIHelper {
	private static float density;
	private static long lastTime = 0;

	/** dip转px */
	public static int dipToPx(int dip) {
		if (density <= 0) {
			density = BookApplication.getInstance().getResources().getDisplayMetrics().density;
		}
		return (int) (dip * density + 0.5f);
	}

	/** px转dip */
	public static int pxToDip(int px) {
		if (density <= 0) {
			density = BookApplication.getInstance().getResources().getDisplayMetrics().density;
		}
		return (int) ((px - 0.5f) / density);
	}

	public static String getClipboard(Context paramContext) {
		return ((ClipboardManager) paramContext.getSystemService("clipboard")).getText().toString();
	}

	public static float getDensity(Context paramContext) {
		return paramContext.getResources().getDisplayMetrics().density;
	}

	public static void getEditTextRequest(EditText paramEditText) {
		paramEditText.setFocusable(true);
		paramEditText.setFocusableInTouchMode(true);
		paramEditText.requestFocus();
	}

	public static int getMeasureHeight(View paramView) {
		paramView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
		return paramView.getMeasuredHeight();
	}

	public static int getMeasureWidth(View paramView) {
		paramView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
		return paramView.getMeasuredWidth();
	}

	public static int getScreenPixHeight(Context paramContext) {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		if (!(paramContext instanceof Activity))
			return paramContext.getResources().getDisplayMetrics().heightPixels;
		WindowManager localWindowManager = ((Activity) paramContext).getWindowManager();
		if (localWindowManager == null)
			return paramContext.getResources().getDisplayMetrics().heightPixels;
		localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
		return localDisplayMetrics.heightPixels;
	}

	public static int getScreenPixWidth(Context paramContext) {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		if (!(paramContext instanceof Activity))
			return paramContext.getResources().getDisplayMetrics().widthPixels;
		WindowManager localWindowManager = ((Activity) paramContext).getWindowManager();
		if (localWindowManager == null)
			return paramContext.getResources().getDisplayMetrics().widthPixels;
		localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
		return localDisplayMetrics.widthPixels;
	}

	public static int getStatusBarHeight(Context paramContext) {
		try {
			Class localClass = Class.forName("com.android.internal.R$dimen");
			Object localObject = localClass.newInstance();
			int i = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
			int j = paramContext.getResources().getDimensionPixelSize(i);
			return j;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return 0;
	}

	public static void hideInputMethodWinods(Context paramContext) {
		InputMethodManager localInputMethodManager = (InputMethodManager) paramContext.getSystemService("input_method");
		View localView = ((Activity) paramContext).getCurrentFocus();
		if ((localView == null) || (localView.getWindowToken() == null))
			return;
		localInputMethodManager.hideSoftInputFromWindow(localView.getWindowToken(), 0);
	}

	public static boolean setClipboard(Context paramContext, String paramString) {
		try {
			((ClipboardManager) paramContext.getSystemService("clipboard")).setText(paramString);
			return true;
		} catch (Exception localException) {
		}
		return false;
	}

	public static void setFullScreen(Activity paramActivity) {
		paramActivity.getWindow().setFlags(1024, 1024);
	}

	public static void setNoActionbar(Activity paramActivity) {
		paramActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	public static void showInputMethodWinods(Context paramContext) {
		((InputMethodManager) paramContext.getSystemService("input_method")).toggleSoftInput(0, 2);
	}

	public static void showToast(Activity activity, String paramString) {
		if (System.currentTimeMillis() - lastTime > 2000) {
			showToast(activity, paramString, Toast.LENGTH_SHORT);
			lastTime = System.currentTimeMillis();
		}
	}

	public static void showToast(final Activity activity, final String paramString, final int paramInt) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(activity, paramString, paramInt).show();
			}
		});
	}

	public static Dialog showProgressDialog(Dialog orldDialog, Context context) {
		if (orldDialog != null) {
			orldDialog.dismiss();
			orldDialog = null;
		}
		Dialog dialog = ProgressDialog.createDialog(context);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		return dialog;
	}

	public static Dialog showProgressDialog(Dialog orldDialog, Context context, String content) {
		if (orldDialog != null) {
			orldDialog.dismiss();
			orldDialog = null;
		}
		Dialog dialog = ProgressDialog.createDialog(context, content);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		return dialog;
	}

	// 去掉进度条
	public static void cancleProgressDialog(Dialog dialog) {
		if (dialog != null) {
			dialog.dismiss();
			dialog.cancel();
		}
	}

	public static boolean isApkDebugable() {
		try {
			ApplicationInfo info = BookApplication.getInstance().getApplicationInfo();
			return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		} catch (Exception e) {

		}
		return false;
	}

	public static void showShakeAnim(Context context, View view, String toast) {
		Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
		view.startAnimation(shake);
		view.requestFocus();
		((BaseActivity) context).showToast(toast);
	}

	// 选择对话框
	public static Dialog showChooseDialog(Context context, String message1, String message2, String message3,
			String message4, String message5, final OnDialogClickListener listener1,
			final OnDialogClickListener listener2, final OnDialogClickListener listener3,
			final OnDialogClickListener listener4, final OnDialogClickListener listener5) {
		try {
			final Dialog dialog = new Dialog(context, R.style.CustomDialog);
			View contentView = LayoutInflater.from(context).inflate(
					Cookies.getReadSetting().isNightMode() ? R.layout.dialog_choose_ef : R.layout.dialog_choose, null);
			Button tv1 = (Button) contentView.findViewById(R.id.dialog_choose_tv1);
			Button tv2 = (Button) contentView.findViewById(R.id.dialog_choose_tv2);
			Button tv3 = (Button) contentView.findViewById(R.id.dialog_choose_tv3);
			Button tv4 = (Button) contentView.findViewById(R.id.dialog_choose_tv4);
			Button tv5 = (Button) contentView.findViewById(R.id.dialog_choose_tv5);
			contentView.findViewById(R.id.dialog_choose_layout1).setVisibility(
					StringUtil.isEmpty(message1) ? View.GONE : View.VISIBLE);
			contentView.findViewById(R.id.dialog_choose_layout2).setVisibility(
					StringUtil.isEmpty(message2) ? View.GONE : View.VISIBLE);
			contentView.findViewById(R.id.dialog_choose_layout3).setVisibility(
					StringUtil.isEmpty(message3) ? View.GONE : View.VISIBLE);
			contentView.findViewById(R.id.dialog_choose_layout4).setVisibility(
					StringUtil.isEmpty(message4) ? View.GONE : View.VISIBLE);
			contentView.findViewById(R.id.dialog_choose_layout5).setVisibility(
					StringUtil.isEmpty(message5) ? View.GONE : View.VISIBLE);
			tv1.setText(StringUtil.isEmpty(message1) ? StringUtil.EMPTY : message1);
			tv2.setText(StringUtil.isEmpty(message2) ? StringUtil.EMPTY : message2);
			tv3.setText(StringUtil.isEmpty(message3) ? StringUtil.EMPTY : message3);
			tv4.setText(StringUtil.isEmpty(message4) ? StringUtil.EMPTY : message4);
			tv5.setText(StringUtil.isEmpty(message5) ? StringUtil.EMPTY : message5);
			tv1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (listener1 != null)
						listener1.onClick();
				}
			});
			tv2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (listener2 != null)
						listener2.onClick();
				}
			});
			tv3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (listener3 != null)
						listener3.onClick();
				}
			});
			tv4.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (listener4 != null)
						listener4.onClick();
				}
			});
			tv5.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (listener5 != null)
						listener5.onClick();
				}
			});
			dialog.setContentView(contentView);
			dialog.show();
			return dialog;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	public static String getUnEmptyString(String content) {
		String result = content == null ? StringUtil.EMPTY : content;
		result = result.replaceAll("\n", StringUtil.EMPTY);
		return result;
	}

	public static void setText(TextView textView, String content) {
		if (textView != null) {
			textView.setText(getUnEmptyString(content));
		}
	}

	public static void setText(TextView textView, String content, String defaultContent) {
		if (textView != null) {
			if (StringUtil.isEmpty(getUnEmptyString(content))) {
				textView.setText(defaultContent);
			} else {
				textView.setText(getUnEmptyString(content));
			}
		}
	}

	public static Dialog showOneButtonDialog(Context context, String message, String btnStr, boolean canCancel,
			final OnDialogClickListener listener) {
		return showOneButtonDialog(context, message, btnStr, canCancel, true, listener);
	}

	// 一个按钮对话框
	public static Dialog showOneButtonDialog(Context context, String message, String btnStr, boolean canCancel,
			final boolean canDismiss, final OnDialogClickListener listener) {
		try {
			final Dialog dialog = new Dialog(context, R.style.CustomDialog);
			dialog.setCancelable(canCancel);
			View contentView = LayoutInflater.from(context)
					.inflate(
							Cookies.getReadSetting().isNightMode() ? R.layout.dialog_one_button_ef
									: R.layout.dialog_one_button, null);
			TextView tvMsg = (TextView) contentView.findViewById(R.id.tv_msg);
			Button btn1 = (Button) contentView.findViewById(R.id.btn1);
			tvMsg.setText(message);
			btn1.setText(btnStr);
			btn1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (canDismiss)
						dialog.dismiss();
					if (listener != null)
						listener.onClick();
				}
			});
			dialog.setContentView(contentView);
			dialog.show();
			return dialog;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	// 两个按钮对话框
	public static Dialog showTowButtonDialog(Context context, String message, String btnStr1, String btnStr2,
			boolean canCancel, final OnDialogClickListener listener1, final OnDialogClickListener listener2) {
		try {
			final Dialog dialog = new Dialog(context, R.style.CustomDialog);
			dialog.setCancelable(canCancel);
			View contentView = LayoutInflater.from(context)
					.inflate(
							Cookies.getReadSetting().isNightMode() ? R.layout.dialog_tow_button_ef
									: R.layout.dialog_tow_button, null);
			TextView tvMsg = (TextView) contentView.findViewById(R.id.tv_msg);
			Button btn1 = (Button) contentView.findViewById(R.id.btn1);
			Button btn2 = (Button) contentView.findViewById(R.id.btn2);
			tvMsg.setText(message);
			btn1.setText(btnStr1);
			btn2.setText(btnStr2);
			btn1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (listener1 != null)
						listener1.onClick();
				}
			});
			btn2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (listener2 != null)
						listener2.onClick();
				}
			});
			dialog.setContentView(contentView);
			dialog.show();
			return dialog;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	public interface OnDialogClickListener {
		public void onClick();
	}

	/**
	 * 
	 * @param activity
	 * @return > 0 success; <= 0 fail
	 */
	public static int getStatusHeight(Activity activity) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = activity.getResources().getDimensionPixelSize(x);
			return sbar;
		} catch (Exception e1) {
			e1.printStackTrace();
			return UIHelper.dipToPx(25);
		}
	}

	// 更新对话框
	public static Dialog showUpdateDialog(final BaseActivity context, final UpdateResponse updateInfo,
			final boolean canCancel) {
		try {
			if (updateInfo != null) {
				final Dialog dialog = new Dialog(context, R.style.CustomDialog);
				dialog.setCancelable(canCancel);
				dialog.setCanceledOnTouchOutside(false);
				View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);
				LayoutParams params = getDialogAttributes(0.8f);
				TextView tvVersion = (TextView) contentView.findViewById(R.id.update_version);
				TextView tvSize = (TextView) contentView.findViewById(R.id.update_size);
				TextView tvInfo = (TextView) contentView.findViewById(R.id.update_info);
				TextView tvUpdateDelay = (TextView) contentView.findViewById(R.id.update_delay);
				TextView tvUpdateNow = (TextView) contentView.findViewById(R.id.update_now);
				float size = Float.valueOf(updateInfo.target_size) / (1024 * 1024);
				DecimalFormat df2 = new DecimalFormat("###.0");
				tvSize.setText("大小:" + df2.format(size) + "M");
				tvVersion.setText("版本:" + updateInfo.version);
				tvInfo.setText(updateInfo.updateLog);
				tvUpdateDelay.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (!canCancel)
							BookActivityManager.getInstance().ExitApp(context);
						dialog.cancel();
					}
				});
				tvUpdateNow.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.cancel();
						suerDownload(context, updateInfo, canCancel);
					}
				});
				dialog.setContentView(contentView, params);
				dialog.show();
				return dialog;
			} else {
				return null;
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	// 确认下载
	public static void suerDownload(final BaseActivity context, final UpdateResponse updateInfo, final boolean canCancel) {
		if (MiscUtils.isWifiConnected(context)) {
			DownloadService.luanch(context, "棒棒书城V" + updateInfo.version, updateInfo.path);
			if (!canCancel) {
				UIHelper.showOneButtonDialog(context, "棒棒书城V" + updateInfo.version + "正在下载中...", "返回桌面", false, false,
						new OnDialogClickListener() {

							@Override
							public void onClick() {
								Intent home = new Intent(Intent.ACTION_MAIN);
								home.addCategory(Intent.CATEGORY_HOME);
								context.startActivity(home);
							}
						});
			}
		} else {
			UIHelper.showTowButtonDialog(context, "当前不是wifi网络,是否现在升级？", "取消", "确定", canCancel,
					new OnDialogClickListener() {

						@Override
						public void onClick() {
							if (!canCancel)
								BookActivityManager.getInstance().ExitApp(context);
						}
					}, new OnDialogClickListener() {

						@Override
						public void onClick() {
							DownloadService.luanch(context, "棒棒书城V" + updateInfo.version, updateInfo.path);
							if (!canCancel) {
								UIHelper.showOneButtonDialog(context, "棒棒书城V" + updateInfo.version + "正在下载中...",
										"返回桌面", false, false, new OnDialogClickListener() {

											@Override
											public void onClick() {
												Intent home = new Intent(Intent.ACTION_MAIN);
												home.addCategory(Intent.CATEGORY_HOME);
												context.startActivity(home);
											}
										});
							}
						}
					});

		}
	}

	// 对话框的大小
	public static LayoutParams getDialogAttributes(float width) {
		int heigths = LayoutParams.WRAP_CONTENT;
		int widths = (int) (getScreenPixWidth(BookApplication.getInstance()) * width);
		return new LayoutParams(widths, heigths);
	}

	// 获取当前系统亮度
	public static float getScreenBrightness(Context context) {
		int screenBrightness = 255;
		try {
			screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
		} catch (Exception localException) {
			return 1f;
		}
		float screenBrightnessPre = (float) screenBrightness / 255;
		return screenBrightnessPre;
	}

	// 设置当前系统亮度
	public static void setScreenBrightness(BaseActivity context, float brightness) {
		WindowManager.LayoutParams lp = context.getWindow().getAttributes();
		lp.screenBrightness = brightness;
		context.getWindow().setAttributes(lp);
	}

	// 判断是否开启了自动亮度调节
	public static boolean isAutoBrightness(Context context) {
		boolean automicBrightness = false;
		try {
			automicBrightness = Settings.System.getInt(context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return automicBrightness;
	}

	// 停止自动亮度调节
	public static void stopAutoBrightness(Context activity) {
		Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	}

	// 开启亮度自动调节
	public static void startAutoBrightness(Activity activity) {
		Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
	}

	// 初始化亮度
	public static void initBrightness(Context activity) {
		if (!UIHelper.isAutoBrightness(activity)) {
			BookApplication.getInstance().lunchBrightness = UIHelper.getScreenBrightness(activity);
			Cookies.getReadSetting().setBrightness(BookApplication.getInstance().lunchBrightness);
		} else {
			BookApplication.getInstance().lunchBrightness = Cookies.getReadSetting().getBrightness();
		}
	}

	// 设置沉浸式通知栏
	@SuppressLint("InlinedApi")
	public static void setTranStateBar(Activity activity) {
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			RelativeLayout actionBarLayout = (RelativeLayout) activity.findViewById(R.id.actionbar_layout);
			int statusBarHeight = getStatusBarHeight(activity);
			actionBarLayout.setPadding(0, statusBarHeight, 0, 0);
			// 透明状态栏
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
//			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}

	public static void delSetWifi(Context context, final DelSetWifiCallBack callBack) {
		if (!MiscUtils.isNetwork(context)) {
			Toast.makeText(context, "网络异常，请检查网络", Toast.LENGTH_SHORT).show();
		} else {
			if (MiscUtils.isWifiConnected(context)) {
				callBack.doPlay();
			} else {
				UIHelper.showTowButtonDialog(context, "当前不是wifi网络，是否继续下载？", "取消", "确定", true, null,
						new OnDialogClickListener() {

							@Override
							public void onClick() {
								callBack.doPlay();
							}
						});
			}
		}

	}

	public interface DelSetWifiCallBack {
		public void doPlay();
	}

	public static void luanchListen(BookDesc bookDesc, final Activity activity) {
		if (bookDesc != null) {

            /* $$$目前FM功能处于未开放状态 */
            UIHelper.showTowButtonDialog(activity, "敬请期待!", "取消", "确定", true, null, null);
			/*if (MiscUtils.checkApkExist(activity, "com.jie.listen.book")) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("com.jie.listen.book",
						"com.jie.listen.book.activity.LuanchActivity"));
				intent.putExtra("bookName", bookDesc.getBookName());
				activity.startActivity(intent);
			} else {

				UIHelper.showTowButtonDialog(activity, "收听本书需要安装《书城听书FM》，是否现在安装？", "取消", "确定", true, null,
						new OnDialogClickListener() {
							@Override
							public void onClick() {
								UIHelper.delSetWifi(activity, new DelSetWifiCallBack() {
									@Override
									public void doPlay() {
										try {
											HttpDownloader.download(activity, Config.LISETN_SITE_URL,
													new httpDownloadCallBack() {
														@Override
														public void onResult(String result) {
															if (!StringUtil.isEmpty(result)) {
																App app = new Gson().fromJson(result, App.class);
																if (app != null) {
																	String url = app.getUrl();
																	DownloadService.luanch(activity, "书城听书FM", url);
																}
															}
														}
													});
										} catch (Exception e) {
											e.printStackTrace();
										}

									}
								});
							}
						});
			}*/
		}
	}

	public static Drawable color2Drawble(int colorId) {
		Drawable drawable = new ColorDrawable(colorId);
		return drawable;
	}
}