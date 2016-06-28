package com.jie.book.work.entity;

import java.io.Serializable;

public class FileInfo implements Serializable {
	private static final long serialVersionUID = 167263823823L;
	private String fileUrl;
	private String fileName;
	private String fileSize;

	public FileInfo() {
		super();
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		return "FileInfo [fileUrl=" + fileUrl + ", fileName=" + fileName + ", fileSize=" + fileSize + "]";
	}

}
