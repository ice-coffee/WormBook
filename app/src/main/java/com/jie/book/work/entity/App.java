package com.jie.book.work.entity;

import java.io.Serializable;
import java.util.List;

public class App implements Serializable {
	private int appType;// 0应用1游戏
	private String packageName;
	private String logo;
	private String name;
	private double size;
	private String url;
	private String depict;
	private String shortDepict;
	private List<String> imageurls;

	public App() {
	}

	public App(int appType, String packageName, String logo, String name, double size, String url, String depict,
			String shortDepict, List<String> imageurls) {
		super();
		this.appType = appType;
		this.packageName = packageName;
		this.logo = logo;
		this.name = name;
		this.size = size;
		this.url = url;
		this.depict = depict;
		this.shortDepict = shortDepict;
		this.imageurls = imageurls;
	}

	public int getAppType() {
		return appType;
	}

	public void setAppType(int appType) {
		this.appType = appType;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDepict() {
		return depict;
	}

	public void setDepict(String depict) {
		this.depict = depict;
	}

	public String getShortDepict() {
		return shortDepict;
	}

	public void setShortDepict(String shortDepict) {
		this.shortDepict = shortDepict;
	}

	public List<String> getImageurls() {
		return imageurls;
	}

	public void setImageurls(List<String> imageurls) {
		this.imageurls = imageurls;
	}

}
