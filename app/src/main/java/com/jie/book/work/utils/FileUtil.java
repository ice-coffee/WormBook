package com.jie.book.work.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import android.os.Environment;

public class FileUtil {
	private static String SDPATH = Environment.getExternalStorageDirectory() + "/";;

	public String getSDPATH() {
		return SDPATH;
	}

	/**
	 * 在SD卡上创建文件
	 */
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 */
	public File creatSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public static boolean isFileExist(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	/** 删除目录 */
	public static boolean deleteDirectory(File dir) {
		File[] bookFiles = dir.listFiles();
		for (File bookFile : bookFiles) {
			if (bookFile.isDirectory())
				deleteDirectory(bookFile);
			bookFile.delete();
		}
		return dir.delete();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File write2SDFromInput(String path, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		try// InputStream里面的数据写入到SD卡中的固定方法
		{
			creatSDDir(path);
			file = creatSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static String getSizeKBStr(int f) {
		DecimalFormat fmt = new DecimalFormat("0.#");
		String s = fmt.format(f) + "KB";
		float f2 = f;
		if (f2 > 1024) {
			f2 = f / 1024.0f;
			s = fmt.format(f2) + "MB";
		}
		return s;
	}

	public static String getSizeStr(long f) {
		DecimalFormat fmt = new DecimalFormat("0.#");
		float f1 = 1;
		if (f >= 1024) {
			f1 = f / 1024.0f;
		}
		String s = fmt.format(f1) + "KB";
		float f2 = 1;
		if (f1 >= 1024) {
			f2 = f1 / 1024.0f;
			s = fmt.format(f2) + "MB";
		}
		return s;
	}

	public static boolean untieGzip(String GzipPath, String filePath) {

		boolean mk = false;
		try {
			GZIPInputStream in = new GZIPInputStream(new FileInputStream(GzipPath));
			OutputStream out = new FileOutputStream(filePath);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			mk = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mk;
	}

	public static boolean isBook(String finaName) {
		return finaName.toUpperCase().indexOf("LOG") == -1;
	}

	// ===================下载=========================

	private final static ThreadLocal<StringBuilder> threadSafeStrBuilder = new ThreadLocal<StringBuilder>();
	private final static ThreadLocal<StringBuffer> threadSafeStrBuf = new ThreadLocal<StringBuffer>();
	private final static ThreadLocal<byte[]> threadSafeByteBuf = new ThreadLocal<byte[]>();
	private final static Pattern p = Pattern.compile(" ");

	public static StringBuffer getThreadSafeStringBuffer() {
		StringBuffer sb = threadSafeStrBuf.get();
		if (sb == null) {
			sb = new StringBuffer();
			threadSafeStrBuf.set(sb);
		}
		sb.setLength(0);
		return sb;
	}

	public static StringBuilder getThreadSafeStringBuilder() {
		StringBuilder sb = threadSafeStrBuilder.get();
		if (sb == null) {
			sb = new StringBuilder();
			threadSafeStrBuilder.set(sb);
		}
		sb.setLength(0);
		return sb;
	}

	public static byte[] getThreadSafeByteBuffer() {
		byte[] buf = threadSafeByteBuf.get();
		if (buf == null) {
			buf = new byte[8192];
			threadSafeByteBuf.set(buf);
		}
		return buf;
	}

	public static String normalizeUrl(String url) {
		if (url.indexOf(" ") != -1) {
			url = p.matcher(url).replaceAll("%20");
		}
		return url;
	}

	public static void closeCloseable(Closeable obj) {
		try {
			if (obj != null)
				obj.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeSocket(Socket socket) {
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}