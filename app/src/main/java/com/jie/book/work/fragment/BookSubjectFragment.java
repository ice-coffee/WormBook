package com.jie.book.work.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bond.bookcatch.easou.vo.EasouSubject;
import com.google.gson.Gson;
import com.jie.book.work.R;
import com.jie.book.work.activity.BookSubjectResultActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.entity.SubjectList;
import com.jie.book.work.read.BookCatchManager.SubjectListInterface;
import com.jie.book.work.utils.ImageLoadUtil;
import com.jie.book.work.utils.ImageLoadUtil.ImageType;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.view.EmptyPage;
import com.jie.book.work.view.EmptyPage.onReLoadListener;
import com.jie.book.work.view.pullrefresh.PullToRefreshButtomListView;
import com.jie.book.work.view.pullrefresh.PullToRefreshButtomListView.onRefreshLoadListener;

public class BookSubjectFragment extends BaseFragment implements onReLoadListener {
	private List<EasouSubject> listObj = new ArrayList<EasouSubject>();
	private PullToRefreshButtomListView listView;
	private BookSubjectAdapter adapter;
	private EmptyPage emptyPage;
	private int pageNo = 1;
	private EasouSubject firstSubject;
	private View headView, viewRoot;
	private String subId;// 第一本书的GID

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fg_book_subject, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected void initUI() {
		headView = LayoutInflater.from(activity).inflate(
				Cookies.getReadSetting().isNightMode() ? R.layout.view_book_subject_head_ef
						: R.layout.view_book_subject_head, null);
		listView = (PullToRefreshButtomListView) getView().findViewById(R.id.pull_refresh_list);
		viewRoot = getView().findViewById(R.id.fg_book_subject_root);
		viewRoot.setBackgroundColor(Cookies.getReadSetting().isNightMode() ? getResources().getColor(
				R.color.color_ef_black) : getResources().getColor(R.color.book_shelf_bg));
		listView.getRefreshableView().addHeaderView(headView);
		adapter = new BookSubjectAdapter();
		listView.setAdapter(adapter);
		emptyPage = new EmptyPage(activity);
		emptyPage.setOnReLoadListener(this);
		listView.setonRefreshLoadListener(new onRefreshLoadListener() {

			@Override
			public void onRefreshing() {
				pageNo = 1;
				getData();
			}

			@Override
			public void onLoadMore() {
				getData();
			}
		});

	}

	@Override
	protected void initData() {
		String subjectCookies = Cookies.getUserSetting().getSubjectCookie();
		if (!StringUtil.isEmpty(Cookies.getUserSetting().getSubjectCookie())) {
			SubjectList cookieSubject = new Gson().fromJson(subjectCookies, SubjectList.class);
			if (cookieSubject.getEasouSubject() != null && cookieSubject.getEasouSubject().size() > 0) {
				listObj.addAll(cookieSubject.getEasouSubject());
				notifiDataChange();
			}

		}
		listView.setRefreshing();
	}

	@Override
	public void onload() {
		getData();
	}

	private void getData() {
		chatchManager.getSubjectList(pageNo, new SubjectListInterface() {

			@Override
			public void getSubjectList(List<EasouSubject> subjectList) {
				if (listView.isRefersh()) {
					if (subjectList != null && subjectList.size() > 0) {
						pageNo++;
						listObj.clear();
						listObj.addAll(subjectList);
						notifiDataChange();
						listView.getRefreshableView().setSelection(0);
						subId = subjectList.get(0).getSubid();
						SubjectList subject = new SubjectList();
						subject.setEasouSubject(subjectList);
						Cookies.getUserSetting().setSubjectCookie(new Gson().toJson(subject));
					}
				} else {
					if (subjectList != null && subjectList.size() > 0) {
						String nextSubGid = subjectList.get(0).getSubid();
						if (!subId.equals(nextSubGid)) {
							pageNo++;
							listObj.addAll(subjectList);
							adapter.notifyDataSetChanged();
							subId = nextSubGid;
						}
					} else {
						listView.setCanLoadMore(false);
						showToast("亲，没有更多了哦！");
					}
				}
				listView.onRefreshCompleteAll();
				emptyPage.onReloadComplete();
				listView.setEmptyView(emptyPage);
			}
		});
	}

	private void notifiDataChange() {
		firstSubject = listObj.get(0);
		listObj.remove(firstSubject);
		setHeadSubject();
		adapter.notifyDataSetChanged();
	}

	private void setHeadSubject() {
		if (firstSubject != null) {
			ImageView image = (ImageView) headView.findViewById(R.id.item_subject_image);
			TextView tvBookName = (TextView) headView.findViewById(R.id.item_subject_name);
			TextView tvBookTime = (TextView) headView.findViewById(R.id.item_subject_time);
			TextView tvBookInfo = (TextView) headView.findViewById(R.id.item_subject_info);
			ImageLoadUtil.loadImage(image, firstSubject.getImageUrl(), ImageType.SUBJECT);
			tvBookName.setText(firstSubject.getSubName());
			tvBookTime.setText(firstSubject.getDate());
			tvBookInfo.setText(firstSubject.getDesc());
			headView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					BookSubjectResultActivity.luanch(activity, firstSubject, firstSubject.getSubName());
				}
			});
		}
	}

	private class BookSubjectAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listObj.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHoldr holder = null;
			if (convertView == null) {
				holder = new ViewHoldr();
				convertView = LayoutInflater.from(activity).inflate(R.layout.item_book_subject, null);
				holder.ivBookIcon = (ImageView) convertView.findViewById(R.id.item_subject_image);
				holder.tvBookName = (TextView) convertView.findViewById(R.id.item_subject_name);
				holder.tvBookTime = (TextView) convertView.findViewById(R.id.item_subject_time);
				holder.tvBookInfo = (TextView) convertView.findViewById(R.id.item_subject_info);
				holder.itemView = convertView.findViewById(R.id.item_subject_view);
				convertView.setTag(holder);
			} else {
				holder = (ViewHoldr) convertView.getTag();
			}
			final EasouSubject subject = listObj.get(position);
			holder.itemView
					.setBackgroundResource(Cookies.getReadSetting().isNightMode() ? R.drawable.selector_book_shelf_item_ef
							: R.drawable.selector_book_shelf_item);
			holder.tvBookName.setTextColor(Cookies.getReadSetting().isNightMode() ? getResources().getColor(
					R.color.text_ef_light_gray) : getResources().getColor(R.color.text_black));
			holder.tvBookInfo.setTextColor(Cookies.getReadSetting().isNightMode() ? getResources().getColor(
					R.color.text_ef_gray) : getResources().getColor(R.color.text_ef_gray));
			ImageLoadUtil.loadImage(holder.ivBookIcon, subject.getImageUrl(), ImageType.SUBJECT);
			holder.tvBookName.setText(subject.getSubName());
			holder.tvBookTime.setText(subject.getDate());
			holder.tvBookInfo.setText(subject.getDesc());
			holder.itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					BookSubjectResultActivity.luanch(activity, subject, subject.getSubName());
				}
			});

			return convertView;
		}
	}

	private class ViewHoldr {
		ImageView ivBookIcon;
		TextView tvBookName;
		TextView tvBookTime;
		TextView tvBookInfo;
		View itemView;
	}

	public void SwitchModel() {
		viewRoot.setBackgroundColor(Cookies.getReadSetting().isNightMode() ? getResources().getColor(
				R.color.color_ef_black) : getResources().getColor(R.color.book_shelf_bg));
		viewRoot = getView().findViewById(R.id.fg_book_subject_root);
		viewRoot.setBackgroundColor(Cookies.getReadSetting().isNightMode() ? getResources().getColor(
				R.color.color_ef_black) : getResources().getColor(R.color.book_shelf_bg));
		listView.getRefreshableView().removeHeaderView(headView);
		headView = LayoutInflater.from(activity).inflate(
				Cookies.getReadSetting().isNightMode() ? R.layout.view_book_subject_head_ef
						: R.layout.view_book_subject_head, null);
		listView.getRefreshableView().addHeaderView(headView);
		setHeadSubject();
		listView.SwitchModel();
	}
}
