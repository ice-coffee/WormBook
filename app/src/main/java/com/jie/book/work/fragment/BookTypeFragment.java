package com.jie.book.work.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.jie.book.work.R;
import com.jie.book.work.activity.BookTypeResultActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.StatisticUtil;

public class BookTypeFragment extends BaseFragment implements OnClickListener {
	private RelativeLayout addLayout;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fg_book_type, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected void initUI() {
		addLayout = (RelativeLayout) getView().findViewById(R.id.fg_book_type_layout);
		SwitchModel();
	}

	public void SwitchModel() {
		View containView = LayoutInflater.from(activity).inflate(
				Cookies.getReadSetting().isNightMode() ? R.layout.view_book_type_ef : R.layout.view_book_type, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		addLayout.removeAllViews();
		addLayout.addView(containView, params);
		getView().findViewById(R.id.book_type_item1_1).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_2).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_3).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_4).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_5).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_6).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_7).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_8).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_9).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_10).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_11).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_12).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_13).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_14).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item1_15).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_1).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_2).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_3).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_4).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_5).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_6).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_7).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_8).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_9).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_10).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_11).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_12).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_13).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_14).setOnClickListener(this);
		getView().findViewById(R.id.book_type_item2_15).setOnClickListener(this);
	}

	@Override
	protected void initData() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.book_type_item1_1:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_QH);
			launchResult(v, 11);
			break;
		case R.id.book_type_item1_2:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_XH);
			launchResult(v, 12);
			break;
		case R.id.book_type_item1_3:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_WX);
			launchResult(v, 13);
			break;
		case R.id.book_type_item1_4:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_XX);
			launchResult(v, 14);
			break;
		case R.id.book_type_item1_5:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_WY);
			launchResult(v, 15);
			break;
		case R.id.book_type_item1_6:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_KH);
			launchResult(v, 16);
			break;
		case R.id.book_type_item1_7:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_JS);
			launchResult(v, 17);
			break;
		case R.id.book_type_item1_8:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_LS);
			launchResult(v, 18);
			break;
		case R.id.book_type_item1_9:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_JD);
			launchResult(v, 19);
			break;
		case R.id.book_type_item1_10:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_JD);
			launchResult(v, 110);
			break;
		case R.id.book_type_item1_11:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_JD);
			launchResult(v, 111);
			break;
		case R.id.book_type_item1_12:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_JD);
			launchResult(v, 112);
			break;
		case R.id.book_type_item1_13:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_JD);
			launchResult(v, 113);
			break;
		case R.id.book_type_item1_14:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_JD);
			launchResult(v, 114);
			break;
		case R.id.book_type_item1_15:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_JD);
			launchResult(v, 115);
			break;
		case R.id.book_type_item2_1:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_YQ);
			launchResult(v, 21);
			break;
		case R.id.book_type_item2_2:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_CY);
			launchResult(v, 22);
			break;
		case R.id.book_type_item2_3:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_DS);
			launchResult(v, 23);
			break;
		case R.id.book_type_item2_4:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_XY);
			launchResult(v, 24);
			break;
		case R.id.book_type_item2_5:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_QC);
			launchResult(v, 25);
			break;
		case R.id.book_type_item2_6:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_TR);
			launchResult(v, 26);
			break;
		case R.id.book_type_item2_7:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_ZT);
			launchResult(v, 27);
			break;
		case R.id.book_type_item2_8:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_LY);
			launchResult(v, 28);
			break;
		case R.id.book_type_item2_9:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_XUY);
			launchResult(v, 29);
			break;
		case R.id.book_type_item2_10:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_XUY);
			launchResult(v, 210);
			break;
		case R.id.book_type_item2_11:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_XUY);
			launchResult(v, 211);
			break;
		case R.id.book_type_item2_12:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_XUY);
			launchResult(v, 212);
		case R.id.book_type_item2_13:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_XUY);
			launchResult(v, 213);
			break;
		case R.id.book_type_item2_14:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_XUY);
			launchResult(v, 214);
			break;
		case R.id.book_type_item2_15:
			StatisticUtil.sendEvent(activity, StatisticUtil.TYPE_XUY);
			launchResult(v, 215);
			break;

		}

	}

	private void launchResult(View v, int position) {
		TextView title = (TextView) v.findViewById(R.id.book_type_text);
		BookTypeResultActivity.luanch(activity, position, title.getText().toString());
	}

}
