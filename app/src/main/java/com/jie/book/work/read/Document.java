package com.jie.book.work.read;

import java.util.LinkedList;

import android.app.Dialog;
import android.graphics.Paint;
import android.graphics.Rect;

import com.bond.bookcatch.vo.BookCatalog;
import com.bond.bookcatch.vo.BookChapter;
import com.bond.bookcatch.vo.BookDesc;
import com.jie.book.work.R;
import com.jie.book.work.activity.ReadActivity;
import com.jie.book.work.application.BookApplication.Cookies;
import com.jie.book.work.utils.MiscUtils;
import com.jie.book.work.utils.StringUtil;
import com.jie.book.work.utils.UIHelper;

public class Document {
	public static final String TAG = Document.class.getSimpleName();
	public static final int CLICK_TIME = 500;
	private LinkedList<BookChapter> chapterList = new LinkedList<BookChapter>();// 本章和下一章
	private LinkedList<Integer> beaginPositionOffset = new LinkedList<Integer>();// 缓存上一章节开始的位置
	private int beaginPosition = 0;

	public ReadActivity activity;
	private BookChapter curChapter;
	private BookCatalog catalog;
	private BookDesc bookDesc;
	private String curCharBuffer;
	private String readProgressStr = "0 %";
	private long lastTime = 0;
	private boolean chapterBack = false;
	private int lastReadOffline = 0;
	public Dialog dialog;

	private Paint mPaint;
	int mRenderWidth, mRenderHeight;
	int mLineSpacing, mParagraphSpacing;
	int index = 0;
	// 计算一个字的高度
	int mTextHeight;
	// 每页最大的行数
	int mMaxPageSize;
	// 计算页面最大的总字数
	int mMaxCharCountPerPage;
	// 计算每行最多字数
	int mMaxCharCountPerLine;
	// 当前页面汉字的总高度
	int mCurContentHeight;
	public final static byte GET_NEXT_LINE_FLAG_HAS_NEXT_LINE = 1 << 0;
	public final static byte GET_NEXT_LINE_FLAG_SHOULD_JUSTIFY = 1 << 1;
	public final static byte GET_NEXT_LINE_FLAG_PARAGRAPH_ENDS = 1 << 2;
	public static final String TEXT_STR = "中";
	private static final String SMALLEST_CHAR = "i";
	protected StringBuilder mContentBuf = new StringBuilder();
	protected int mEndCharIndex; // 用于存储一个段落的结束时，转向下一个或上一个页面的索引
	protected int mCharCountOfPage; // 储存临时值当getline（for）
	protected int mNewlineIndex; // 当turningnext for储存索引的页或换行
	private final static String NEW_LINE_STR = "\n";
	protected int mPageCharOffsetInBuffer; // char offset in buffer (current

	public BookChapter getCurChapter() {
		return curChapter;
	}

	public Document(ReadActivity activity, int lineSpacing, int paragraphSpacing, int renderWidth, int renderHeight,
			Paint paint) {
		this.activity = activity;
		mLineSpacing = lineSpacing;
		mParagraphSpacing = paragraphSpacing;
		mRenderWidth = renderWidth;
		mRenderHeight = renderHeight;
		mPaint = paint;
		resetTextHeight();
	}

	private final void resetTextHeight() {
		// 获取一个矩形
		Rect rect = RectObjectPool.getObject();
		// 获取一个字的面积F
		mPaint.getTextBounds(TEXT_STR, 0, TEXT_STR.length(), rect);
		// 计算一个字的高度
		mTextHeight = rect.bottom - rect.top;
		// 计算每行 包括字和间隔的高度
		float lineHeight = mTextHeight + mLineSpacing;
		// 每页最大的行数
		mMaxPageSize = (int) (mRenderHeight / lineHeight);
		if ((mRenderHeight % lineHeight) >= mTextHeight)
			++mMaxPageSize;
		// 计算每个字所占宽度
		int textWidth = rect.right - rect.left;
		// 计算每行最多字数
		mMaxCharCountPerLine = (int) (mRenderWidth / textWidth);
		// 计算一个英文字符
		mPaint.getTextBounds(SMALLEST_CHAR, 0, SMALLEST_CHAR.length(), rect);
		// 计算页面最大的总字数
		mMaxCharCountPerPage = (int) (mRenderWidth / (rect.right - rect.left) * mMaxPageSize);
		RectObjectPool.freeObject(rect);
	}

	public void initDocument(BookDesc bookDesc, BookCatalog catalog) {
		this.bookDesc = bookDesc;
		this.catalog = catalog;
		init(true);
	}

	// 初始化数据
	public void init(boolean showLoading) {
		if (bookDesc == null)
			return;
		if (showLoading && !Cookies.getReadSetting().isShowReadingGuide())
			dialog = UIHelper.showProgressDialog(dialog, activity);
		activity.chatchManager.initDoc(bookDesc, catalog, new ChapterListenerAdapter() {
			@Override
			public void getBookChapter(BookChapter chapter) {
				activity.setReady(true);
				curChapter = chapter;
				chapterList.add(chapter);
				delNextLoadChapter();
				showNextPage();
				activity.getReadBorad().forceRedraw(false);
				if (beaginPositionOffset.size() > 0)
					beaginPositionOffset.removeLast();
				// 缓存下一章
				if (chapterList.size() > 0) {
					activity.chatchManager.getNextChapter(bookDesc, chapterList.getLast(),
							new ChapterListenerAdapter() {

								@Override
								public void getBookChapter(BookChapter chapter) {
									chapterList.addLast(chapter);
									// 获取上一章
									if (chapterList.size() > 0) {
										activity.chatchManager.getPreviewChapter(bookDesc, chapterList.getFirst(),
												new ChapterListenerAdapter() {
													@Override
													public void getBookChapter(BookChapter chapter) {
														chapterList.addFirst(chapter);
													}
												});
									}
								}

							});
				}
				chapterBack = true;
				activity.cancleProgressDialog();
				UIHelper.cancleProgressDialog(dialog);
			}

			@Override
			public void onError() {
				chapterBack = true;
				activity.cancleProgressDialog();
				UIHelper.cancleProgressDialog(dialog);
				activity.showToast("网络异常，请检查网络！");
			}
		});
	}

	// 初始化数据
	public void initAfterChangeSource() {
		dialog = UIHelper.showProgressDialog(dialog, activity);
		activity.chatchManager.initDocAfterChangerSource(bookDesc, new ChapterListenerAdapter() {
			@Override
			public void getBookChapter(BookChapter chapter) {
				activity.setReady(true);
				curChapter = chapter;
				chapterList.add(chapter);
				delNextLoadChapter();
				showNextPage();
				activity.getReadBorad().forceRedraw(false);
				if (beaginPositionOffset.size() > 0)
					beaginPositionOffset.removeLast();
				// 缓存下一章
				if (chapterList.size() > 0) {
					activity.chatchManager.getNextChapter(bookDesc, chapterList.getLast(),
							new ChapterListenerAdapter() {

								@Override
								public void getBookChapter(BookChapter chapter) {
									chapterList.addLast(chapter);
									// 获取上一章
									if (chapterList.size() > 0) {
										activity.chatchManager.getPreviewChapter(bookDesc, chapterList.getFirst(),
												new ChapterListenerAdapter() {
													@Override
													public void getBookChapter(BookChapter chapter) {
														chapterList.addFirst(chapter);
													}
												});
									}
								}

							});
				}
				chapterBack = true;
				activity.cancleProgressDialog();
				UIHelper.cancleProgressDialog(dialog);
			}

			@Override
			public void onError() {
				chapterBack = true;
				activity.cancleProgressDialog();
				UIHelper.cancleProgressDialog(dialog);
				activity.showToast("网络异常，请检查网络！");
			}
		});
	}

	// 从某一章重新加载
	public void reloadData(BookCatalog catalog) {
		this.catalog = catalog;
		chapterList.clear();
		beaginPositionOffset.clear();
		mCharCountOfPage = 0;
		beaginPosition = 0;
		readProgressStr = "0 %";
		init(true);
	}

	// 从某一章重新加载
	public void reloadDataAfterChangeSource() {
		chapterList.clear();
		activity.getBookDesc().clearCatalog();
		beaginPositionOffset.clear();
		mCharCountOfPage = 0;
		beaginPosition = 0;
		readProgressStr = "0 %";
		initAfterChangeSource();
	}

	// 从某一章重新加载
	public void reloadData(BookCatalog catalog, int benginPosition) {
		this.catalog = catalog;
		this.beaginPosition = benginPosition;
		chapterList.clear();
		beaginPositionOffset.clear();
		mCharCountOfPage = 0;
		readProgressStr = "0 %";
		init(true);
	}

	// 读取下一章， 是否是自动滑动
	public boolean getNextChapter() {
		if (!chapterBack || System.currentTimeMillis() - lastTime < CLICK_TIME) {
			return false;
		}
		chapterBack = false;
		lastTime = System.currentTimeMillis();
		if (chapterList.size() > 0) {
			// 如果已经缓存下一章直接读取，否则先缓存在读取，然后在缓存下下一章
			if (chapterList.size() > 1) {
				BookChapter nextBookChapter = getNextBookChapter();
				if (nextBookChapter != null) {
					curChapter = nextBookChapter;
					cleanOfferset();
					showNextPage();
					chapterBack = true;
					// 缓存下下一章
					activity.chatchManager.getNextChapter(bookDesc, chapterList.getLast(),
							new ChapterListenerAdapter() {

								@Override
								public void getBookChapter(BookChapter chapter) {
									chapterList.addLast(chapter);
									removeNextMore();
								}
							});
					return true;
				} else {
					loadNextChapter();
					return false;
				}
			} else {
				loadNextChapter();
			}
		}
		return false;
	}

	// 在线加载下一章
	private void loadNextChapter() {
		if (chapterList.size() == 0 || curChapter == null)
			return;
		dialog = UIHelper.showProgressDialog(dialog, activity);
		activity.chatchManager.getNextChapter(bookDesc, curChapter, new ChapterListenerAdapter() {

			@Override
			public void getBookChapter(BookChapter chapter) {
				cleanOfferset();
				curChapter = chapter;
				chapterList.addLast(chapter);
				removeNextMore();
				showNextPage();
				activity.getReadBorad().forceRedraw(true);
				chapterBack = true;
				UIHelper.cancleProgressDialog(dialog);
				// 缓存下下一章
				if (chapterList.size() > 0) {
					activity.chatchManager.getNextChapter(bookDesc, chapterList.getLast(),
							new ChapterListenerAdapter() {

								@Override
								public void getBookChapter(BookChapter chapter) {
									chapterList.addLast(chapter);
									removeNextMore();
								}

							});
				}
			}

			@Override
			public void onError() {
				activity.showToast(activity.getString(R.string.no_network));
				UIHelper.cancleProgressDialog(dialog);
				chapterBack = true;
			}

			@Override
			public void onLast() {
				super.onLast();
				activity.showToast("已经到最后一章");
				activity.getReadMenu().stopAutoRead();
				UIHelper.cancleProgressDialog(dialog);
				chapterBack = true;
			}
		});
	}

	// 如果缓存的章节大于2就移除第一章
	private void removeNextMore() {
		if (chapterList.size() > 3) {
			chapterList.removeFirst();
		}
		if (chapterList.size() > 3) {
			chapterList.removeFirst();
		}
	}

	// 读取上一章， 是否是自动滑动
	public boolean getPreviewChapter() {
		if (!chapterBack || System.currentTimeMillis() - lastTime < CLICK_TIME) {
			return false;
		}
		chapterBack = false;
		lastTime = System.currentTimeMillis();
		if (chapterList.size() > 0) {
			if (chapterList.size() > 1) {
				BookChapter preBookChapter = getPreBookChapter();
				if (preBookChapter != null) {
					curChapter = preBookChapter;
					delPreLoadChapter();
					showPreviewPage();
					chapterBack = true;
					// 缓存上一章
					activity.chatchManager.getPreviewChapter(bookDesc, curChapter, new ChapterListenerAdapter() {

						@Override
						public void getBookChapter(BookChapter chapter) {
							chapterList.addFirst(chapter);
							removePreMore();
						}
					});
					return true;
				} else {
					loadPreChapter();
					return false;
				}
			} else {
				loadPreChapter();
			}
		}
		return false;
	}

	// 获取在线上一章节
	private void loadPreChapter() {
		if (chapterList.size() == 0 || curChapter == null)
			return;
		dialog = UIHelper.showProgressDialog(dialog, activity);
		activity.chatchManager.getPreviewChapter(bookDesc, curChapter, new ChapterListenerAdapter() {

			@Override
			public void getBookChapter(BookChapter chapter) {
				chapterList.addFirst(chapter);
				curChapter = chapter;
				removePreMore();
				delPreLoadChapter();
				showPreviewPage();
				activity.getReadBorad().forceRedraw(true);
				chapterBack = true;
				UIHelper.cancleProgressDialog(dialog);
				// 缓存上一章
				if (chapterList.size() > 0) {
					activity.chatchManager.getPreviewChapter(bookDesc, chapterList.getFirst(),
							new ChapterListenerAdapter() {

								@Override
								public void getBookChapter(BookChapter chapter) {
									chapterList.addFirst(chapter);
									removePreMore();
								}
							});
				}
			}

			@Override
			public void onError() {
				activity.showToast(activity.getString(R.string.no_network));
				chapterBack = true;
				UIHelper.cancleProgressDialog(dialog);
			}

			@Override
			public void onFrist() {
				super.onFrist();
				UIHelper.cancleProgressDialog(dialog);
				activity.showToast("已经到第一章");
				activity.getReadMenu().stopAutoRead();
				chapterBack = true;
			}
		});
	}

	// 如果缓存的章节大于2就移除最后一章
	private void removePreMore() {
		// 如果缓存的章节大于2就移除第一章
		if (chapterList.size() > 3) {
			chapterList.removeLast();
		}
		if (chapterList.size() > 3) {
			chapterList.removeLast();
		}
	}

	public boolean showNextPage() {
		if (curChapter == null || curChapter.getContent() == null)
			return false;
		int preBeaginPosition = beaginPosition + mCharCountOfPage;
		if (preBeaginPosition < curChapter.getContent().length()) {
			if (mCharCountOfPage > 0)
				beaginPositionOffset.add(beaginPosition);
			beaginPosition += mCharCountOfPage;
			curCharBuffer = curChapter.getContent().substring(beaginPosition);
			activity.getReadPanle().setViewEmptyVisble();
			calculateProgress();
			return true;
		} else {// 获取下一章
			return getNextChapter();
		}
	}

	// 是否在这里缓存以前的偏移值
	public boolean showPreviewPage() {
		if (curChapter == null || curChapter.getContent() == null)
			return false;
		beaginPosition = beaginPositionOffset.size() > 0 ? beaginPositionOffset.getLast() : 0;
		if (beaginPositionOffset.size() > 0) {
			beaginPositionOffset.removeLast();
			curCharBuffer = curChapter.getContent().substring(beaginPosition);
			activity.getReadPanle().setViewEmptyVisble();
			calculateProgress();
			return true;
		} else {
			return getPreviewChapter();
		}
	}

	public void cleanOfferset() {
		mCharCountOfPage = 0;
		beaginPosition = 0;
		beaginPositionOffset.clear();
	}

	public void cleanAll() {
		mCharCountOfPage = 0;
		beaginPosition = 0;
		chapterList.clear();
		beaginPositionOffset.clear();
	}

	// 获取当前章节的上一章节
	private BookChapter getPreBookChapter() {
		int curChapterIndex = 0;
		for (int i = 0; i < chapterList.size(); i++) {
			BookChapter chapter = chapterList.get(i);
			if (chapter.getUrl() != null && curChapter.getUrl() != null && chapter.getUrl().equals(curChapter.getUrl())) {
				curChapterIndex = i;
			}
		}
		int preChapterIndex = curChapterIndex - 1;
		if (preChapterIndex >= 0) {
			BookChapter retrunChapter = chapterList.get(preChapterIndex);
			return retrunChapter == curChapter ? null : retrunChapter;
		} else {
			return null;
		}
	}

	// 获取当前章节的下一章节
	private BookChapter getNextBookChapter() {
		int curChapterIndex = chapterList.size() - 1;
		for (int i = 0; i < chapterList.size(); i++) {
			BookChapter chapter = chapterList.get(i);
			if (chapter != null)
				if (chapter.getUrl() != null && curChapter.getUrl() != null
						&& chapter.getUrl().equals(curChapter.getUrl())) {
					curChapterIndex = i;
				}
		}
		int nextChapterIndex = curChapterIndex + 1;
		if (nextChapterIndex <= chapterList.size() - 1) {
			BookChapter retrunChapter = chapterList.get(nextChapterIndex);
			return retrunChapter == curChapter ? null : retrunChapter;
		} else {
			return null;
		}
	}

	private int preBeaginPosition = 0;

	// 预加载某一章节
	private void preLoadChapter(String content) {
		preBeaginPosition += mCharCountOfPage;
		beaginPositionOffset.add(preBeaginPosition);
		curCharBuffer = content.substring(preBeaginPosition);
		mNewlineIndex = 0;
		prepareGetLines();
		StringBuilder sb = MiscUtils.getThreadSafeStringBuilder();
		byte flags = 0;
		while (true) {
			flags = getNextLine(sb);
			if (flags == 0)
				break;
		}
		if (preBeaginPosition < content.length() && content.length() > 0)
			preLoadChapter(content);
	}

	// 处理预加载上一章
	private void delPreLoadChapter() {
		mCharCountOfPage = 0;
		preBeaginPosition = 0;
		beaginPosition = 0;
		beaginPositionOffset.clear();
		preLoadChapter(curChapter.getContent());
		if (beaginPositionOffset.size() > 0) {
			beaginPositionOffset.removeLast();
		}
	}

	// 处理预加载上一章
	private void delNextLoadChapter() {
		if (beaginPosition > 0 && curChapter != null && curChapter.getContent() != null) {
			preBeaginPosition = 0;
			mCharCountOfPage = 0;
			beaginPositionOffset.clear();
			int endPosition = beaginPosition <= curChapter.getContent().length() ? beaginPosition : curChapter
					.getContent().length();
			preLoadChapter(curChapter.getContent().substring(0, endPosition));
		}
	}

	// 计算当前阅读百分比
	public void calculateProgress() {
		if (curChapter != null && curChapter.getContent() != null) {
			float progress = (float) beaginPosition / curChapter.getContent().length();
			int readProgress = (int) (progress * 100) > 100 ? 100 : (int) (progress * 100);
			readProgressStr = readProgress + " %";
			saveReadPosition();
		}
	}

	// 保存最近阅读记录
	public void saveReadPosition() {
		activity.chatchManager.saveLastReadPosition(bookDesc, curChapter, beaginPosition);
	}

	public int getTextHeight() {
		return mTextHeight;
	}

	public void prepareGetLines() {
		mNewlineIndex = 0;
		mCurContentHeight = 0;
		mCharCountOfPage = 0;
		mContentBuf.delete(0, mContentBuf.length());
		if (!StringUtil.isEmpty(curCharBuffer))
			mContentBuf.append(curCharBuffer);
	}

	public final byte getNextLine(StringBuilder sb) {

		mCurContentHeight += mTextHeight;
		if (mCurContentHeight > mRenderHeight) {
			return 0;
		}
		byte flags = GET_NEXT_LINE_FLAG_HAS_NEXT_LINE;

		int index = mPageCharOffsetInBuffer + mCharCountOfPage;

		if (index == mNewlineIndex) {
			mNewlineIndex = mContentBuf.indexOf(NEW_LINE_STR, mNewlineIndex);
			mEndCharIndex = (mNewlineIndex != -1) ? mNewlineIndex : mContentBuf.length();
		}

		int charCount = LayoutUtil.breakText(mContentBuf, index, mEndCharIndex, mRenderWidth, mPaint);

		if (charCount > 0) {
			sb.append(mContentBuf, index, index + charCount);
			LayoutUtil.trimSpaces(sb);
			mCharCountOfPage += charCount;
		}

		int endIndex = mPageCharOffsetInBuffer + mCharCountOfPage;
		if (endIndex == mNewlineIndex) {
			int carrigeReturnIndex = sb.length() - 1;
			// ignore the carriage return character if it exists
			if (carrigeReturnIndex >= 0 && sb.charAt(carrigeReturnIndex) == 0x0D)
				sb.deleteCharAt(carrigeReturnIndex);
			++mCharCountOfPage;
			++mNewlineIndex;
			mCurContentHeight += mParagraphSpacing;
			flags |= GET_NEXT_LINE_FLAG_PARAGRAPH_ENDS;
		} else {
			if (endIndex < mContentBuf.length()) {
				flags |= GET_NEXT_LINE_FLAG_SHOULD_JUSTIFY;
			}
			mCurContentHeight += mLineSpacing;
		}
		return flags;
	}

	public String getCurChapterName() {
		if (curChapter != null) {
			return curChapter.getTitle();
		} else {
			return bookDesc == null ? StringUtil.EMPTY : bookDesc.getBookName();
		}
	}

	public String getReadProgressStr() {
		return readProgressStr;
	}

	public void setReadProgressStr(String readProgressStr) {
		this.readProgressStr = readProgressStr;
	}

	public final void onResetTextSize() {
		resetTextHeight();
	}

	public int getLastReadOffline() {
		return lastReadOffline;
	}

	public void setLastReadOffline(int lastReadOffline) {
		this.lastReadOffline = lastReadOffline;
	}

	public boolean isChapterBack() {
		return chapterBack;
	}

	public void setBeaginPosition(int beaginPosition) {
		this.beaginPosition = beaginPosition;
	}

	public void restRender(int renderWidth, int renderHeight) {
		mRenderWidth = renderWidth;
		mRenderHeight = renderHeight;
		resetTextHeight();
	}

	public void restLineSpace(int lineSpace) {
		mLineSpacing = lineSpace;
		resetTextHeight();
	}

	public BookChapter getCurrentChapter() {
		return curChapter;
	}

	public int getLastReadPosition() {
		return beaginPosition;
	}
}
