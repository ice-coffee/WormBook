package com.jie.book.work.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.UIHelper;
import com.jie.book.work.view.colorPicker.ColorPickerDialog;

public class CustomThemeActivity extends BaseActivity implements OnClickListener {
	public static final int REQUEST_CODE = 1001;
	private ImageView ivBgImage;
	private ImageView ivTextImage;
	private int bgId = -2386539;
	private int textId = -9476249;

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, CustomThemeActivity.class);
		BookActivityManager.getInstance().goFoResult(content, intent, REQUEST_CODE);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.act_custom_theme);
		UIHelper.setTranStateBar(activity);
		initUI();
		initListener();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initUI() {
		ivBgImage = (ImageView) findViewById(R.id.custom_theme_bg_image);
		ivTextImage = (ImageView) findViewById(R.id.custom_theme_text_image);
		int cacheBg = Cookies.getReadSetting().getReadThemeBg();
		int cacheText = Cookies.getReadSetting().getReadThemeText();
		bgId = cacheBg == 0 ? bgId : cacheBg;
		textId = cacheText == 0 ? textId : cacheText;
		ivBgImage.setBackgroundDrawable(UIHelper.color2Drawble(bgId));
		ivTextImage.setBackgroundDrawable(UIHelper.color2Drawble(textId));
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void initListener() {
		findViewById(R.id.custom_theme_back).setOnClickListener(this);
		findViewById(R.id.custom_theme_bg).setOnClickListener(this);
		findViewById(R.id.custom_theme_text).setOnClickListener(this);
		findViewById(R.id.custom_theme_sure).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.custom_theme_back:
			finishInAnim();
			break;
		case R.id.custom_theme_bg:
			onClickColorPickerDialog(ivBgImage, bgId, 0);
			break;
		case R.id.custom_theme_text:
			onClickColorPickerDialog(ivTextImage, textId, 1);
			break;
		case R.id.custom_theme_sure:
			if (bgId != 0 && textId != 0) {
				Cookies.getReadSetting().setReadThemeText(textId);
				Cookies.getReadSetting().setReadThemeBg(bgId);
				setResult(RESULT_OK);
				finishInAnim();
			}
			break;
		}
	}

	public void onClickColorPickerDialog(final ImageView imageView, final int defaultColor, final int type) {
		final ColorPickerDialog colorDialog = new ColorPickerDialog(this, defaultColor);

		colorDialog.setAlphaSliderVisible(false);
		colorDialog.setTitle(null);

		colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						imageView.setBackgroundColor(colorDialog.getColor());
						if (type == 0) {
							bgId = colorDialog.getColor();
						} else {
							textId = colorDialog.getColor();
						}
					}
				});

		colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		colorDialog.show();
	}

}