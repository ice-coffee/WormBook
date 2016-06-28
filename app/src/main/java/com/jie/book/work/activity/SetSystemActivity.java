package com.jie.book.work.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.jie.book.work.R;
import com.jie.book.work.application.BookActivityManager;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.UIHelper;

public class SetSystemActivity extends BaseActivity implements OnClickListener {
	private ImageButton btnNotifi;
	private ImageButton btnNotifiVoice;
	private ImageButton btnNotifiShake;
	private ImageButton btnReadVoice;
	private ImageButton btnReadAnim;
	private ImageButton btnReadScreenLight;
	private ImageButton btnReadScreenFull;

	public static void luanch(Activity content) {
		Intent intent = new Intent();
		intent.setClass(content, SetSystemActivity.class);
		BookActivityManager.getInstance().goTo(content, intent);
	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(Cookies.getReadSetting().isNightMode() ? R.layout.act_set_system_ef : R.layout.act_set_system);
		UIHelper.setTranStateBar(activity);
		initUI();
		initData();
		initListener();
	}

	@Override
	protected void initUI() {
		btnNotifi = (ImageButton) findViewById(R.id.btn_updata_notifi);
		btnNotifiVoice = (ImageButton) findViewById(R.id.btn_notifi_voice);
		btnNotifiShake = (ImageButton) findViewById(R.id.btn_notifi_shake);
		btnReadVoice = (ImageButton) findViewById(R.id.btn_set_read_voice);
		btnReadAnim = (ImageButton) findViewById(R.id.btn_set_read_anim);
		btnReadScreenLight = (ImageButton) findViewById(R.id.btn_set_read_screen_light);
		btnReadScreenFull = (ImageButton) findViewById(R.id.btn_set_read_screen_full);
	}

	@Override
	protected void initData() {
		boolean isOpen = Cookies.getUserSetting().isNotifiOpen();
		boolean isVoiceOpen = Cookies.getUserSetting().isNotifiVoiceOpen();
		boolean isShakeOpen = Cookies.getUserSetting().isNotifiShakeOpen();
		boolean isReadVoice = Cookies.getUserSetting().isReadVoice();
		boolean isReadAnim = Cookies.getUserSetting().isReadAnim();
		boolean isReadScreenLight = Cookies.getUserSetting().isReadScreenLight();
		boolean isReadScreenFull = Cookies.getUserSetting().isReadScreenFull();
		btnNotifi.setImageResource(isOpen ? R.drawable.setting_checkbox_check_on
				: R.drawable.setting_checkbox_check_off);
		btnNotifiVoice.setImageResource(isVoiceOpen ? R.drawable.setting_checkbox_check_on
				: R.drawable.setting_checkbox_check_off);
		btnNotifiShake.setImageResource(isShakeOpen ? R.drawable.setting_checkbox_check_on
				: R.drawable.setting_checkbox_check_off);
		btnReadVoice.setImageResource(isReadVoice ? R.drawable.setting_checkbox_check_on
				: R.drawable.setting_checkbox_check_off);
		btnReadAnim.setImageResource(isReadAnim ? R.drawable.setting_checkbox_check_on
				: R.drawable.setting_checkbox_check_off);
		btnReadScreenLight.setImageResource(isReadScreenLight ? R.drawable.setting_checkbox_check_on
				: R.drawable.setting_checkbox_check_off);
		btnReadScreenFull.setImageResource(isReadScreenFull ? R.drawable.setting_checkbox_check_on
				: R.drawable.setting_checkbox_check_off);
	}

	@Override
	protected void initListener() {
		findViewById(R.id.book_search_back).setOnClickListener(this);
		btnNotifi.setOnClickListener(this);
		btnNotifiVoice.setOnClickListener(this);
		btnNotifiShake.setOnClickListener(this);
		btnReadVoice.setOnClickListener(this);
		btnReadAnim.setOnClickListener(this);
		btnReadScreenFull.setOnClickListener(this);
		btnReadScreenLight.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book_search_back:
			finishInAnim();
			break;
		case R.id.btn_updata_notifi:
			boolean isOpen = Cookies.getUserSetting().isNotifiOpen();
			btnNotifi.setImageResource(!isOpen ? R.drawable.setting_checkbox_check_on
					: R.drawable.setting_checkbox_check_off);
			Cookies.getUserSetting().setNotifiOpen(!isOpen);
			break;
		case R.id.btn_notifi_voice:
			boolean isVoiceOpen = Cookies.getUserSetting().isNotifiVoiceOpen();
			btnNotifiVoice.setImageResource(!isVoiceOpen ? R.drawable.setting_checkbox_check_on
					: R.drawable.setting_checkbox_check_off);
			Cookies.getUserSetting().setNotifiVoiceOpen(!isVoiceOpen);
			break;
		case R.id.btn_notifi_shake:
			boolean isShakeOpen = Cookies.getUserSetting().isNotifiShakeOpen();
			btnNotifiShake.setImageResource(!isShakeOpen ? R.drawable.setting_checkbox_check_on
					: R.drawable.setting_checkbox_check_off);
			Cookies.getUserSetting().setNotifiShakeOpen(!isShakeOpen);
			break;
		case R.id.btn_set_read_voice:
			boolean isReadVoice = Cookies.getUserSetting().isReadVoice();
			btnReadVoice.setImageResource(!isReadVoice ? R.drawable.setting_checkbox_check_on
					: R.drawable.setting_checkbox_check_off);
			Cookies.getUserSetting().setReadVoice(!isReadVoice);
			break;
		case R.id.btn_set_read_screen_light:
			boolean isReadScreenLight = Cookies.getUserSetting().isReadScreenLight();
			btnReadScreenLight.setImageResource(!isReadScreenLight ? R.drawable.setting_checkbox_check_on
					: R.drawable.setting_checkbox_check_off);
			Cookies.getUserSetting().setReadScreenLight(!isReadScreenLight);
			break;
		case R.id.btn_set_read_screen_full:
			boolean isReadScreenFull = Cookies.getUserSetting().isReadScreenFull();
			btnReadScreenFull.setImageResource(!isReadScreenFull ? R.drawable.setting_checkbox_check_on
					: R.drawable.setting_checkbox_check_off);
			Cookies.getUserSetting().setReadScreenFull(!isReadScreenFull);
			break;
		case R.id.btn_set_read_anim:
			boolean isReadAnim = Cookies.getUserSetting().isReadAnim();
			btnReadAnim.setImageResource(!isReadAnim ? R.drawable.setting_checkbox_check_on
					: R.drawable.setting_checkbox_check_off);
			Cookies.getUserSetting().setReadAnim(!isReadAnim);
			break;
		}
	}

}