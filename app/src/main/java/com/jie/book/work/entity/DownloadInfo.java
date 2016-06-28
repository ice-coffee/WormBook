package com.jie.book.work.entity;

import java.io.Serializable;

import android.app.Notification;
import android.widget.RemoteViews;

import com.bond.bookcatch.vo.BookCatalog;
import com.bond.bookcatch.vo.BookDesc;

public class DownloadInfo implements Serializable {
	private static final long serialVersionUID = 1522222211L;
	private BookDesc bookDesc;
	private BookCatalog bookCatalog;
	private int type;
	private int size;
	private int index;
	private int total;
	private int precent;
	private int notifiId;
	private Notification notifi;
	private RemoteViews remoteView;

	public BookDesc getBookDesc() {
		return bookDesc;
	}

	public void setBookDesc(BookDesc bookDesc) {
		this.bookDesc = bookDesc;
	}

	public BookCatalog getBookCatalog() {
		return bookCatalog;
	}

	public void setBookCatalog(BookCatalog bookCatalog) {
		this.bookCatalog = bookCatalog;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPrecent() {
		return precent;
	}

	public void setPrecent(int precent) {
		this.precent = precent;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getNotifiId() {
		return notifiId;
	}

	public void setNotifiId(int notifiId) {
		this.notifiId = notifiId;
	}

	public Notification getNotifi() {
		return notifi;
	}

	public void setNotifi(Notification notifi) {
		this.notifi = notifi;
	}

	public RemoteViews getRemoteView() {
		return remoteView;
	}

	public void setRemoteView(RemoteViews remoteView) {
		this.remoteView = remoteView;
	}

}
