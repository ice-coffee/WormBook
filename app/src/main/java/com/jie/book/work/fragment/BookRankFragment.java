package com.jie.book.work.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.jie.book.work.R;
import com.jie.book.work.activity.BookRankResultActivity;
import com.jie.book.work.activity.RankMoreFActivity;
import com.jie.book.work.activity.RankMoreMActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.StatisticUtil;

public class BookRankFragment extends BaseFragment implements OnClickListener {
	private List<View> itemList = new ArrayList<View>();
	private TextView tvItem1, tvItem2, tvItem3, tvItem5, tvItem6, tvItem7;
	private RelativeLayout addLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fg_book_rank, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected void initUI() {
		addLayout = (RelativeLayout) getView().findViewById(R.id.fg_book_rank_layout);
		View containView = LayoutInflater.from(activity).inflate(
				Cookies.getReadSetting().isNightMode() ? R.layout.view_book_rank_ef : R.layout.view_book_rank, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		addLayout.addView(containView, params);
		initRealUI();
	}

	private void initRealUI() {
		View itemView1 = getView().findViewById(R.id.rank_item1);
		View itemView2 = getView().findViewById(R.id.rank_item2);
		View itemView3 = getView().findViewById(R.id.rank_item3);
		View itemView4 = getView().findViewById(R.id.rank_item4);
		View itemView5 = getView().findViewById(R.id.rank_item5);
		View itemView6 = getView().findViewById(R.id.rank_item6);
		View itemView7 = getView().findViewById(R.id.rank_item7);
		View itemView8 = getView().findViewById(R.id.rank_item8);
		tvItem1 = (TextView) itemView1.findViewById(R.id.item_book_rank_name);
		tvItem2 = (TextView) itemView2.findViewById(R.id.item_book_rank_name);
		tvItem3 = (TextView) itemView3.findViewById(R.id.item_book_rank_name);
		tvItem5 = (TextView) itemView5.findViewById(R.id.item_book_rank_name);
		tvItem6 = (TextView) itemView6.findViewById(R.id.item_book_rank_name);
		tvItem7 = (TextView) itemView7.findViewById(R.id.item_book_rank_name);
		itemList.add(itemView1);
		itemList.add(itemView2);
		itemList.add(itemView3);
		itemList.add(itemView4);
		itemList.add(itemView5);
		itemList.add(itemView6);
		itemList.add(itemView7);
		itemList.add(itemView8);
		itemView1.setOnClickListener(this);
		itemView2.setOnClickListener(this);
		itemView3.setOnClickListener(this);
		itemView4.setOnClickListener(this);
		itemView5.setOnClickListener(this);
		itemView6.setOnClickListener(this);
		itemView7.setOnClickListener(this);
		itemView8.setOnClickListener(this);
	}

	@Override
	protected void initData() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rank_item1:
			StatisticUtil.sendEvent(activity, StatisticUtil.RANK_HOT);
			BookRankResultActivity.luanch(activity, 0, tvItem1.getText().toString());
			break;
		case R.id.rank_item2:
			StatisticUtil.sendEvent(activity, StatisticUtil.RANK_SELL);
			BookRankResultActivity.luanch(activity, 1, tvItem2.getText().toString());
			break;
		case R.id.rank_item3:
			StatisticUtil.sendEvent(activity, StatisticUtil.RANK_OVER);
			BookRankResultActivity.luanch(activity, 2, tvItem3.getText().toString());
			break;
		case R.id.rank_item4:
			RankMoreMActivity.luanch(activity);
			break;
		case R.id.rank_item5:
			StatisticUtil.sendEvent(activity, StatisticUtil.RANK_RECOMMED);
			BookRankResultActivity.luanch(activity, 4, tvItem5.getText().toString());
			break;
		case R.id.rank_item6:
			StatisticUtil.sendEvent(activity, StatisticUtil.RANK_NEW);
			BookRankResultActivity.luanch(activity, 5, tvItem6.getText().toString());
			break;
		case R.id.rank_item7:
			StatisticUtil.sendEvent(activity, StatisticUtil.RANK_MONTH);
			BookRankResultActivity.luanch(activity, 6, tvItem7.getText().toString());
			break;
		case R.id.rank_item8:
			RankMoreFActivity.luanch(activity);
			break;
		}
	}

	public void SwitchModel() {
		View containView = LayoutInflater.from(activity).inflate(
				Cookies.getReadSetting().isNightMode() ? R.layout.view_book_rank_ef : R.layout.view_book_rank, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		addLayout.removeAllViews();
		addLayout.addView(containView, params);
		initRealUI();
	}
}
