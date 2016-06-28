package com.jie.book.work.entity;

public class TypefaceInfo {
	private double size;
	private String imageUrl;
	private String fileUrl;
	private String fileName;
	private String name;
	private int precent = -1;// -1还未开始 0等待下载-2完成 -3下载错误

	public TypefaceInfo() {
	}

	public TypefaceInfo(String fileName, String name) {
		this.precent = -2;
		this.fileName = fileName;
		this.name = name;
	}

	public TypefaceInfo(double size, String imageUrl, String fileUrl, String fileName, String name) {
		super();
		this.size = size;
		this.imageUrl = imageUrl;
		this.fileUrl = fileUrl;
		this.fileName = fileName;
		this.name = name;
		// this.id = id;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrecent() {
		return precent;
	}

	public void setPrecent(int precent) {
		this.precent = precent;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
