package com.jie.book.work.local;

import java.util.List;

import android.annotation.SuppressLint;

import com.bond.bookcatch.BookLocal;
import com.bond.bookcatch.local.vo.LocalBookCatalog;
import com.bond.bookcatch.local.vo.LocalBookDesc;
import com.bond.bookcatch.vo.BookMark;

@SuppressLint("HandlerLeak")
public class LocalBookManager {

	// 根据filePath检查书籍是否存在，存在返回true 不存在返回false
	public static boolean checkBookExist(String filePath) {
		return BookLocal.checkBookExist(filePath);
	}

	// 保存本地书籍到书架
	public static void saveBook(String bookName, String filePath) {
		BookLocal.saveBook(bookName, filePath);
	}

	// 保存拆分好的目录列表，需要将目录封装在List中传过来，目录中无需设置相关ID
	public static void saveBookCatalogs(LocalBookDesc desc, List<LocalBookCatalog> catalogs) {
		BookLocal.saveBookCatalogs(desc, catalogs);
	}

	// 获取书籍的目录列表
	public static List<LocalBookCatalog> findBookCatalogs(LocalBookDesc desc) {
		return BookLocal.findBookCatalogs(desc);
	}

	// 删除书籍
	public static void deleteBook(LocalBookDesc desc) {
		BookLocal.deleteBook(desc);
	}

	// 保存书籍最后阅读位置
	public static void saveLastReadPosition(LocalBookDesc desc, long position, int progress) {
		BookLocal.saveLastReadPosition(desc, position, progress);
	}

	// 保存书签
	public synchronized static void saveBookMark(LocalBookDesc desc, LocalBookCatalog catalog, long position,
			String content) {
		BookLocal.saveBookMark(desc, catalog, position, content);
	}

	// 获取某一本的所有书签列表，按添加时间降序
	public static List<BookMark> findBookMarkList(LocalBookDesc desc) {
		return BookLocal.findBookMarkList(desc);
	}

	// 删除书签
	public static void deleteBookMark(BookMark mark) {
		BookLocal.deleteBookMark(mark);
	}

	// 置顶\取消置顶 书籍
	public static void setBookTop(LocalBookDesc desc, boolean isTop) {
		BookLocal.setBookTop(desc, isTop);
	}

	// 记录书籍的操作，用于排序时优先
	public static void recordBookOper(LocalBookDesc desc) {
		BookLocal.recordBookOper(desc);
	}
}
