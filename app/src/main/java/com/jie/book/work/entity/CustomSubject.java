package com.jie.book.work.entity;

public class CustomSubject {
	private String title;
	private String time;
	private String subjectStr;
	private int image;

	public CustomSubject(String title, String time, String subjectStr, int image) {
		super();
		this.title = title;
		this.time = time;
		this.subjectStr = subjectStr;
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSubjectStr() {
		return subjectStr;
	}

	public void setSubjectStr(String subjectStr) {
		this.subjectStr = subjectStr;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

}
