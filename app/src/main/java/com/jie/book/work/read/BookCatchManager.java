package com.jie.book.work.read;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.bond.bookcatch.BookCatcher;
import com.bond.bookcatch.BookChannel;
import com.bond.bookcatch.BookSync;
import com.bond.bookcatch.BookSync.AuthChannel;
import com.bond.bookcatch.BookType;
import com.bond.bookcatch.callback.ProgressCallback;
import com.bond.bookcatch.easou.EasouRankType;
import com.bond.bookcatch.easou.vo.EasouSubject;
import com.bond.bookcatch.mixed.MixedRankType;
import com.bond.bookcatch.vo.BookCatalog;
import com.bond.bookcatch.vo.BookChapter;
import com.bond.bookcatch.vo.BookDesc;
import com.bond.bookcatch.vo.BookMark;
import com.bond.bookcatch.vo.BookSource;
import com.bond.bookcatch.vo.SearchResult;
import com.bond.common.exception.BadNetworkException;
import com.jie.book.work.R;
import com.jie.book.work.activity.BaseActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.TaskExecutor;

@SuppressLint("HandlerLeak")
public class BookCatchManager {
	private final static int HANDLER_NO_NETWOR = 0;
	private final static int HANDLER_SEARCH_RESULT_CALLBACK = 2;
	private final static int HANDLER_BOOK_DESC_CALLBACK = 3;
	private final static int HANDLER_BOOK_DESC_LIST_CALLBACK = 4;
	private final static int HANDLER_DEFAULT_CALLBACK = 5;
	private final static int HANDLER_SUBJECT_LIST_CALLBACK = 6;
	private final static int HANDLER_CATALOG_LIST_CALLBACK = 7;
	private final static int HANDLER_TIME_CALLBACK = 8;
	private final static int HANDLER_RANGE_CALLBACK = 9;
	private final static int HANDLER_CHAPTER_CALLBACK = 11;// 有返回
	private final static int HANDLER_CHAPTER_ERRO = 12;// 错误
	private final static int HANDLER_CHAPTER_LAST = 13;// 最后一章
	private final static int HANDLER_CHAPTER_FRIST = 14;// 第一章
	private final static int HANDLER_BOOK_SOURCE_LIST = 15;// 来源列表

	private ChapterListenerAdapter chapterCallBack;
	private SearchResultInterface searchResultCallBack;
	private BookDescListInterface bookDescListCallBack;
	private BookDescInterface bookDescCallBack;
	private DefaultInterface defaultCallBack;
	private SubjectListInterface subjectListCallBack;
	private CatalogListInterface catalogListCallBack;
	private LongInterface longCallBack;
	private RangeInterface rangeInterface;
	private BookSourceListInterface bookSourceListInterface;

	private Context context;

	public BookCatchManager(Context context) {
		this.context = context;
	}

	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_NO_NETWOR:
				if (context != null)
					((BaseActivity) context).showToast(context.getResources().getString(R.string.no_network));
				break;
			case HANDLER_CHAPTER_CALLBACK:
				if (chapterCallBack != null)
					chapterCallBack.getBookChapter((BookChapter) msg.obj);
				else
					chapterCallBack.onError();
				break;
			case HANDLER_CHAPTER_ERRO:
				if (chapterCallBack != null)
					chapterCallBack.onError();
				break;
			case HANDLER_CHAPTER_LAST:
				if (chapterCallBack != null)
					chapterCallBack.onLast();
				break;
			case HANDLER_CHAPTER_FRIST:
				if (chapterCallBack != null)
					chapterCallBack.onFrist();
				break;
			case HANDLER_SEARCH_RESULT_CALLBACK:
				if (searchResultCallBack != null)
					searchResultCallBack.getSearchResultList((List<SearchResult>) msg.obj);
				break;
			case HANDLER_BOOK_DESC_CALLBACK:
				if (bookDescCallBack != null)
					bookDescCallBack.getBookDesc((BookDesc) msg.obj);
				break;
			case HANDLER_BOOK_DESC_LIST_CALLBACK:
				if (bookDescListCallBack != null)
					bookDescListCallBack.getBookDescList((List<BookDesc>) msg.obj);
				break;
			case HANDLER_DEFAULT_CALLBACK:
				if (defaultCallBack != null)
					defaultCallBack.getDefault((Boolean) msg.obj);
				break;
			case HANDLER_SUBJECT_LIST_CALLBACK:
				if (subjectListCallBack != null)
					subjectListCallBack.getSubjectList((List<EasouSubject>) msg.obj);
				break;
			case HANDLER_CATALOG_LIST_CALLBACK:
				if (catalogListCallBack != null)
					catalogListCallBack.getCatalogList((List<BookCatalog>) msg.obj);
				break;
			case HANDLER_TIME_CALLBACK:
				if (longCallBack != null)
					longCallBack.getTime((Long) msg.obj);
				break;
			case HANDLER_RANGE_CALLBACK:
				if (rangeInterface != null)
					rangeInterface.getRange((int[]) msg.obj);
				break;
			case HANDLER_BOOK_SOURCE_LIST:
				if (bookSourceListInterface != null)
					bookSourceListInterface.getBookSourceList((List<BookSource>) msg.obj);
				break;
			}

		}

	};

	// 通过作者或者书名查找
	public void getSearchResultList(final BookChannel channel, final String keyword, final int pageNo,
			final SearchResultInterface callBack) {
		this.searchResultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					List<SearchResult> list = BookCatcher.search(channel, keyword, pageNo);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = list;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_NO_NETWOR);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = null;
					mHandler.sendMessage(msg);
				}

			}
		});
	}

	// 初始化文档
	public void initDoc(final BookDesc desc, final BookCatalog catalog, final ChapterListenerAdapter callBack) {
		this.chapterCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					if (desc.getCatalogs() == null) {
						BookCatcher.loadBookCatalog(desc, context);
					} else {
						if (desc.getCatalogs().size() == 0)
							BookCatcher.loadBookCatalog(desc, context);
					}
					BookCatalog newCatalog = catalog == null ? catalog : BookCatcher.locationCatalog(desc, catalog);
					if (desc.getIsStore() == 1)
						recordBookDesc(desc);
					BookChapter curChapter = null;
					if (catalog == null) {// 从缓存章节加载
						curChapter = desc.getLastReadChapter();
						if (curChapter == null || StringUtil.isEmpty(curChapter.getContent())) {
							if (desc.getCatalogs() != null && desc.getCatalogs().size() > 0)
								curChapter = BookCatcher.catchChapter(desc, desc.getCatalogs().get(0), context);
							else
								curChapter = BookCatcher.catchChapter(desc, newCatalog, context);
						}
					} else {// 从某一张开始加载
						curChapter = BookCatcher.catchChapter(desc, newCatalog, context);
					}
					if (curChapter == null) {
						mHandler.sendEmptyMessage(HANDLER_CHAPTER_ERRO);
					} else {
						Message msg = Message.obtain();
						msg.what = HANDLER_CHAPTER_CALLBACK;
						msg.obj = curChapter;
						mHandler.sendMessage(msg);
					}
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_CHAPTER_ERRO);
				}
			}
		});
	}

	// 初始化文档
	public void initDocAfterChangerSource(final BookDesc desc, final ChapterListenerAdapter callBack) {
		this.chapterCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					BookChapter curChapter = null;
					BookCatcher.loadBookCatalog(desc, context);
					BookCatalog catalog = desc.getLastReadCatalog();
					if (desc.getIsStore() == 1)
						recordBookDesc(desc);
					if (catalog == null) {// 从缓存章节加载
						if (desc.getCatalogs() != null && desc.getCatalogs().size() > 0)
							curChapter = BookCatcher.catchChapter(desc, desc.getCatalogs().get(0), context);
					} else {// 从某一张开始加载
						curChapter = BookCatcher.catchChapter(desc, catalog, context);
					}
					if (curChapter == null) {
						mHandler.sendEmptyMessage(HANDLER_CHAPTER_ERRO);
					} else {
						Message msg = Message.obtain();
						msg.what = HANDLER_CHAPTER_CALLBACK;
						msg.obj = curChapter;
						mHandler.sendMessage(msg);
					}
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_CHAPTER_ERRO);
				}
			}
		});
	}

	// 获取下一章
	public void getNextChapter(final BookDesc bookDesc, final BookChapter chapter, final ChapterListenerAdapter callBack) {
		this.chapterCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					BookChapter nextChapter = BookCatcher.nextChapter(bookDesc, chapter, context);
					if (nextChapter == null) {
						mHandler.sendEmptyMessage(HANDLER_CHAPTER_LAST);
					} else {
						Message msg = Message.obtain();
						msg.what = HANDLER_CHAPTER_CALLBACK;
						msg.obj = nextChapter;
						mHandler.sendMessage(msg);
					}
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_CHAPTER_ERRO);
				}
			}
		});
	}

	// 获取上一章
	public void getPreviewChapter(final BookDesc bookDesc, final BookChapter chapter,
			final ChapterListenerAdapter callBack) {
		this.chapterCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					BookChapter previewChapter = BookCatcher.prevChapter(bookDesc, chapter, context);
					if (previewChapter == null) {
						mHandler.sendEmptyMessage(HANDLER_CHAPTER_FRIST);
					} else {
						Message msg = Message.obtain();
						msg.what = HANDLER_CHAPTER_CALLBACK;
						msg.obj = previewChapter;
						mHandler.sendMessage(msg);
					}
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_CHAPTER_ERRO);
				}
			}
		});
	}

	// 从搜索结果中获取详情
	public void getBookDescBySearchResult(final SearchResult searchResult, final BookDescInterface callBack) {
		this.bookDescCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					BookDesc bookDesc = BookCatcher.catchBookDesc(searchResult, context);
					Message msg = Message.obtain();
					msg.what = HANDLER_BOOK_DESC_CALLBACK;
					msg.obj = bookDesc;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_NO_NETWOR);
					Message msg = Message.obtain();
					msg.what = HANDLER_BOOK_DESC_CALLBACK;
					msg.obj = null;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 加载章节目录
	public void loadBookCatalog(final BookDesc bookDesc, final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					BookCatcher.loadBookCatalog(bookDesc, context);
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = true;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_NO_NETWOR);
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = false;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 查询书架中的书籍
	public void getBookShelfList(final BookDescListInterface callBack) {
		this.bookDescListCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				List<BookDesc> books = BookCatcher.findBookInStore(context);
				Message msg = Message.obtain();
				msg.what = HANDLER_BOOK_DESC_LIST_CALLBACK;
				msg.obj = books;
				mHandler.sendMessage(msg);
			}
		});
	}

	// 添加到书架
	public void addBookDesc(final BookDesc desc, final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				boolean result = BookCatcher.saveBook(desc, context);
				Message msg = Message.obtain();
				msg.what = HANDLER_DEFAULT_CALLBACK;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		});
	}

	// 添加到书架
	public void addBookDesc(BookDesc desc) {
		BookCatcher.saveBook(desc, context);
	}

	// 添加到书架
	public void addBookDesc(final SearchResult result, final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					BookDesc bookDesc = BookCatcher.catchBookDesc(result, context);
					BookCatcher.saveBook(bookDesc, context);
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = true;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					e.printStackTrace();
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = false;
				}

			}
		});
	}

	// 添加到书架并保存最后阅读记录
	public void addBookDescAndSave(final Document doc, final BookDesc desc, final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				boolean result = BookCatcher.saveBook(desc, context);
				if (doc != null)
					doc.saveReadPosition();
				Message msg = Message.obtain();
				msg.what = HANDLER_DEFAULT_CALLBACK;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		});
	}

	// 从书架删除书籍
	public void deleteBookDesc(final SearchResult result) {
		BookCatcher.deleteBook(result, context);
	}

	// 从书架删除书籍
	public void deleteBookDesc(final BookDesc desc, final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				boolean result = BookCatcher.deleteBook(desc, context);
				Message msg = Message.obtain();
				msg.what = HANDLER_DEFAULT_CALLBACK;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		});
	}

	// 记录最近阅读书籍
	public void recordBookDesc(final BookDesc desc) {
		if (desc != null)
			BookCatcher.recordBookOper(desc, context);
	}

	// 从书架删除书籍
	public void addBookDesc(final String bookId, final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				boolean result = BookCatcher.deleteBookById(bookId, context);
				Message msg = Message.obtain();
				msg.what = HANDLER_DEFAULT_CALLBACK;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		});
	}

	// 置顶或者取消置顶
	public void setBookDescTop(final BookDesc desc, final boolean istop, final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				boolean result = BookCatcher.setBookTop(desc, istop, context);
				Message msg = Message.obtain();
				msg.what = HANDLER_DEFAULT_CALLBACK;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		});
	}

	// 在线批量检查时候有更新章节
	public void updateAllBookOnline(final List<BookDesc> descList, final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					BookCatcher.updateBookOnline(descList, context);
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = true;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = false;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 获取宜搜排行榜
	public void getEasouBookRank(final EasouRankType type, final int pageNo, final SearchResultInterface callBack) {
		this.searchResultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					List<SearchResult> list = BookCatcher.easouRank(type, pageNo);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = list;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_NO_NETWOR);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = null;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 获取神器排行榜
	public void getMixeBookRank(final MixedRankType type, final SearchResultInterface callBack) {
		this.searchResultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					List<SearchResult> list = BookCatcher.mixedRank(type);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = list;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_NO_NETWOR);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = null;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 百度热搜榜
	public void getBaiduHotRank(final int pageNo, final SearchResultInterface callBack) {
		this.searchResultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					List<SearchResult> list = BookCatcher.baiduRank(pageNo);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = list;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_NO_NETWOR);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = null;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 百度推荐榜
	public void getBaiduCommRank(final int pageNo, final SearchResultInterface callBack) {
		this.searchResultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					List<SearchResult> list = BookCatcher.baiduRecommend(pageNo);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = list;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_NO_NETWOR);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = null;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 获取宜搜分类
	public void getBookType(final BookType type, final int pageNo, final SearchResultInterface callBack) {
		this.searchResultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					List<SearchResult> list = BookCatcher.search(type, pageNo);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = list;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_NO_NETWOR);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = null;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 获取宜搜专题列表
	public void getSubjectList(final int pageNo, final SubjectListInterface callBack) {
		this.subjectListCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					List<EasouSubject> list = BookCatcher.catchEasouSubject(pageNo);
					Message msg = Message.obtain();
					msg.what = HANDLER_SUBJECT_LIST_CALLBACK;
					msg.obj = list;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_NO_NETWOR);
					Message msg = Message.obtain();
					msg.what = HANDLER_SUBJECT_LIST_CALLBACK;
					msg.obj = null;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 获取专题下的书籍列表
	public void getSubjectResult(final EasouSubject sub, final SearchResultInterface callBack) {
		this.searchResultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					List<SearchResult> list = BookCatcher.catchEasouSubjectBooks(sub);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = list;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_NO_NETWOR);
					Message msg = Message.obtain();
					msg.what = HANDLER_SEARCH_RESULT_CALLBACK;
					msg.obj = null;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 保存最近阅读记录
	public void saveLastReadPosition(final BookDesc desc, final BookChapter chapter, final int position) {
		BookCatcher.saveLastReadPosition(desc, chapter, position, context);
	}

	// 获取推荐书籍
	public void getRecBookList(final List<String> ids, final BookChannel channel, final BookDescListInterface callBack) {
		this.bookDescListCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					List<BookDesc> books = BookCatcher.catchBookDesc(ids, channel);
					Message msg = Message.obtain();
					msg.what = HANDLER_BOOK_DESC_LIST_CALLBACK;
					msg.obj = books;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					Message msg = Message.obtain();
					msg.what = HANDLER_BOOK_DESC_LIST_CALLBACK;
					msg.obj = null;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 加载书籍的离线目录
	public void loadBookCatalogOffline(final BookDesc desc, final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				if (desc != null) {
					BookCatcher.loadBookCatalogOffline(desc, context);
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = true;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 缓存书籍
	public void downloadBook(final BookDesc bookDesc, final BookCatalog currentCatalog, final int size,
			final ProgressCallback callback) {
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					BookCatcher.downloadBook(bookDesc, currentCatalog, size, callback, context);
				} catch (BadNetworkException e) {
					mHandler.sendEmptyMessage(HANDLER_NO_NETWOR);
				}
			}
		});
	}

	// 获取一个全新状态的BookDesc
	public BookDesc reLoadBookDesc(BookDesc desc) {
		if (desc == null)
			return null;
		BookDesc newBookDesc = BookCatcher.findBookInStoreById(desc.getId(), desc.getChannel(), context);
		return newBookDesc == null ? desc : newBookDesc;
	}

	// 刷新bookdesc解决不同步的问题
	public void refreshBookDesc(BookDesc desc) {
		BookCatcher.refreshBook(desc, context);
	}

	// 清理缓存
	public void deleteOfferLineData(final BookDesc desc, final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				if (desc != null) {
					BookCatcher.deleteOfflineData(desc, context);
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = true;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 获取网络时间
	public void getOnlineTime(final LongInterface callBack) {
		this.longCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				long time = MiscUtils.getOnlineTime(context);
				Message msg = Message.obtain();
				msg.what = HANDLER_TIME_CALLBACK;
				msg.obj = time;
				mHandler.sendMessage(msg);
			}
		});
	}

	public void getOfflineCatalogRange(final BookDesc desc, final RangeInterface rangeInterface) {
		this.rangeInterface = rangeInterface;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				int[] range = BookCatcher.getOfflineCatalogRange(desc, context);
				Message msg = Message.obtain();
				msg.what = HANDLER_RANGE_CALLBACK;
				msg.obj = range;
				mHandler.sendMessage(msg);
			}
		});
	}

	// 检查书籍要离线的章节是否连续
	public void downloadCheck(BookDesc desc, BookCatalog catalog, int size, Context context,
			final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		try {
			if (desc != null) {
				boolean result = BookCatcher.downloadCheck(desc, catalog, size, context);
				Message msg = Message.obtain();
				msg.what = HANDLER_DEFAULT_CALLBACK;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		} catch (BadNetworkException e) {
			Message msg = Message.obtain();
			msg.what = HANDLER_DEFAULT_CALLBACK;
			msg.obj = false;
			mHandler.sendMessage(msg);
		}
	}

	// 获取书籍多来源列表
	public void catchBookSource(final BookDesc desc, final BookCatalog catalog,
			final BookSourceListInterface bookSourceListInterface) {
		this.bookSourceListInterface = bookSourceListInterface;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					List<BookSource> bookSources = BookCatcher.catchBookSource(desc, catalog);
					Message msg = Message.obtain();
					msg.what = HANDLER_BOOK_SOURCE_LIST;
					msg.obj = bookSources;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					Message msg = Message.obtain();
					msg.what = HANDLER_BOOK_SOURCE_LIST;
					msg.obj = null;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	// 用户注册，注册成功后自动登录
	public void register(final AuthChannel channel, final String authUId, final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					boolean result = BookSync.register(channel, authUId);
					if (result) {
						int count = BookSync.sync();
						Cookies.getUserSetting().setLastSyncTime();
						Cookies.getUserSetting().setCloudBookCount(count);
						Message msg = Message.obtain();
						msg.what = HANDLER_DEFAULT_CALLBACK;
						msg.obj = true;
						mHandler.sendMessage(msg);
					} else {
						Message msg = Message.obtain();
						msg.what = HANDLER_DEFAULT_CALLBACK;
						msg.obj = false;
						mHandler.sendMessage(msg);
					}
				} catch (BadNetworkException e) {
					e.printStackTrace();
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = false;
					mHandler.sendMessage(msg);
				}

			}
		});
	}

	// 同步书籍
	public void sync(final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					int count = BookSync.sync();
					Cookies.getUserSetting().setCloudBookCount(count);
					Cookies.getUserSetting().setLastSyncTime();
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = true;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					e.printStackTrace();
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = false;
					mHandler.sendMessage(msg);
				}

			}
		});
	}

	// 判断用户是否已登录
	public boolean isLogin() {
		return BookSync.isLogin();
	}

	// 注销
	public void logout() {
		if (BookSync.isLogin()) {
			BookSync.logout();
		}
	}

	// 同步并注销
	public void logoutWithSync(final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					int count = BookSync.sync();
					Cookies.getUserSetting().setCloudBookCount(count);
					Cookies.getUserSetting().setLastSyncTime();
					BookSync.logout();
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = true;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					e.printStackTrace();
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = false;
					mHandler.sendMessage(msg);
				}

			}
		});
	}

	// 激活广告
	public void activeAdv(final DefaultInterface callBack) {
		this.defaultCallBack = callBack;
		TaskExecutor.getInstance().executeTask(new Runnable() {
			public void run() {
				try {
					BookSync.activeAdv();
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = true;
					mHandler.sendMessage(msg);
				} catch (BadNetworkException e) {
					e.printStackTrace();
					Message msg = Message.obtain();
					msg.what = HANDLER_DEFAULT_CALLBACK;
					msg.obj = false;
					mHandler.sendMessage(msg);
				}

			}
		});
	}

	// 获取广告是否已激活
	public boolean isActivedAdv() {
		return BookSync.isActivedAdv();
	}

	// 换源，将当前源的数据更新到对应的章节或目录中，仅本地操作
	public void changeBookSource(BookDesc desc, BookCatalog catalog, BookSource source) {
		BookCatcher.changeBookSource(desc, catalog, source, context);
	}

	// 刷新目录的数据，本地操作，用于宜搜百度换源后刷新目录后再加载章节
	public void refreshCatalog(BookCatalog catalog) {
		BookCatcher.refreshCatalog(catalog, context);
	}

	// 添加书签
	public void addMark(BookDesc desc, BookChapter chapter, int position) {
		BookCatcher.saveBookMark(desc, chapter, position, context);
	}

	// 添加书签
	public List<BookMark> getBookMarks(BookDesc desc) {
		return BookCatcher.findBookMarkList(desc, context);
	}

	// 获取缓存的最后章节
	public String findLastOfflineCatalog(BookDesc desc, Context context) {
		return BookCatcher.findLastOfflineCatalog(desc, context);
	}

	// 添加书签
	public void deletBookMark(BookMark bookMark) {
		BookCatcher.deleteBookMark(bookMark, context);
	}

	// 定位目录
	public BookCatalog locationCatalog(BookDesc desc, BookCatalog catalog) {
		return BookCatcher.locationCatalog(desc, catalog);
	}

	// 获取养肥区书籍数量
	public int findBookCountInKeep() {
		return BookCatcher.findBookCountInKeep(context);
	}

	// 批量更新养肥区书籍养肥时间
	public List<BookDesc> updateBookKeepTime(List<BookDesc> descList, int days) {
		return BookCatcher.updateBookKeepTime(descList, days, context);
	}

	// 查询养肥区中所有的书籍列表
	public List<BookDesc> findBookInKeep() {
		return BookCatcher.findBookInKeep(context);
	}

	// 将书架中的书籍转移到养肥区（批量）
	public void moveBookToKeepTime(List<BookDesc> descList, int days) {
		BookCatcher.moveBookToKeep(descList, days, context);
	}

	// 将书架中的书籍转移到养肥区（单个）
	public void moveBookToKeepTime(BookDesc desc, int days) {
		BookCatcher.moveBookToKeep(desc, days, context);
	}

	// 将养肥区中的书籍移回到书架，即移除养肥区（单个）
	public void moveBookToStore(BookDesc desc) {
		BookCatcher.moveBookToStore(desc, context);
	}

	// 获取被移除的书名
	public String findKeepTimeExpireBookTitle() {
		return BookCatcher.findKeepTimeExpireBookTitle(context);
	}

	// 查询该书籍是否在书架中已存在 已存在则返回true
	public Boolean checkIfExists(SearchResult result, Context context) {
		return BookCatcher.checkIfExists(result, context);
	}

	// 关闭数据库
	public void closeDatabase() {
		BookCatcher.closeDatabase();
	}

	public interface SearchResultInterface {
		public void getSearchResultList(List<SearchResult> searchResult);

	}

	public interface BookDescListInterface {
		public void getBookDescList(List<BookDesc> bookDescList);

	}

	public interface BookDescInterface {
		public void getBookDesc(BookDesc desc);
	}

	public interface DefaultInterface {
		public void getDefault(boolean haBack);
	}

	public interface SubjectListInterface {
		public void getSubjectList(List<EasouSubject> subjectList);
	}

	public interface CatalogListInterface {
		public void getCatalogList(List<BookCatalog> catalogList);
	}

	public interface LongInterface {
		public void getTime(long time);
	}

	public interface RangeInterface {
		public void getRange(int[] range);
	}

	public interface BookSourceListInterface {
		public void getBookSourceList(List<BookSource> bookSourceList);
	}
}
