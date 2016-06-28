package com.jie.book.work.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Paint;
import android.graphics.Rect;

import com.bond.bookcatch.local.vo.LocalBookCatalog;
import com.bond.bookcatch.local.vo.LocalBookDesc;
import com.jie.book.work.read.LayoutUtil;
import com.jie.book.work.read.RectObjectPool;
import com.jie.book.work.utils.CharsetUtil;
import com.jie.book.work.utils.StringUtil;

/**
 * @author neevek This class implements the logics to read a plain text file by
 *         block, and provides funcionalities to turn next & previous page. this
 *         class will ensure that memory usage be always kept to a certain limit
 *         (i.e. will not grow unlimitedly even if the file is large, even in
 *         hundred metabytes.)
 */
public abstract class LocalDocument {
	/**
	 * 
	 */
	private final static char NEW_LINE_CHAR = '\n';
	private final static String NEW_LINE_STR = "\n";

	Paint mPaint;
	int mRenderWidth, mRenderHeight;
	int mLineSpacing, mParagraphSpacing;

	int mTextHeight;
	int mMaxPageSize;
	int mMaxCharCountPerPage;
	int mMaxCharCountPerLine;
	int mCurContentHeight;

	LocalBookDesc mBookInfo;
	Encoding mEncoding;

	protected RandomAccessFile mRandBookFile;
	protected long mFileSize; // in bytes

	protected int mReadChapterIndex = 0;

	protected byte[] mByteBuffer;
	protected StringBuilder mContentBuf = new StringBuilder();
	protected int mPageCharOffsetInBuffer; // char offset in buffer (current
											// char offset in mContentBuf)

	protected long mReadByteBeginOffset; // current read buffer start byte
											// position in the file
	protected long mReadByteEndOffset; // current read buffer end byte position
										// in the file
	protected long mPageBeginPosition; // page byte begin position in file
	protected int mNewlineIndex; // for storing index of newline when turning
									// next or previous pages
	protected int mEndCharIndex; // for storing end index of a certain paragraph
									// when turning next or previous pages
	protected int mCharCountOfPage; // for storing temporary values when
									// getLine()

	// for storing byte count of each block we read when reading through the
	// file
	// for prevent mContentBuf from unlimitedly growing, we need to throw away
	// characters at the
	// head of mContentBuf along the way we read through the file, and
	// mByteMetaList is for avoiding
	// cutting off half a GBK char or a UTF-8 char at the head of the buffer
	protected LinkedList<ByteMeta> mByteMetaList = new LinkedList<ByteMeta>();
	// for turning preivous page, for storing temporary offsets
	LinkedList<Integer> mCharOffsetList = new LinkedList<Integer>();
	// for fast turning previous page
	LinkedList<Integer> mCharOffsetCache = new LinkedList<Integer>();

	public LocalDocument(LocalBookDesc bookInfo, int lineSpacing, int paragraphSpacing) throws Exception {
		mBookInfo = bookInfo;
		mByteBuffer = new byte[mRenderWidth <= 320 ? 4096 : 8192];
		mLineSpacing = lineSpacing;
		mParagraphSpacing = paragraphSpacing;
		validateDocument();
	}

	public void validateDocument() throws Exception {
		File bookFile = new File(mBookInfo.getFilePath());
		if (!bookFile.canRead())
			throw new FileNotFoundException("书籍文件不存在");
		if (bookFile.isDirectory())
			throw new InvalidObjectException("不合法的书籍文件");
		if (bookFile.length() < 10)
			throw new InvalidObjectException("书籍文件无可读内容");
	}

	public LocalDocument initDocument(int renderWidth, int renderHeight, Paint paint) {
		mRenderWidth = renderWidth;
		mRenderHeight = renderHeight;

		mPaint = paint;

		extractChapterList();
		resetTextHeight();
		doInit();
		return this;
	}

	protected void doInit() {
	}

	protected boolean stopProcessFlag;
	protected List<LocalBookCatalog> mChapterList;

	public void restRender(int renderWidth, int renderHeight) {
		mRenderWidth = renderWidth;
		mRenderHeight = renderHeight;
		resetTextHeight();
	}

	private void extractChapterList() {
		mChapterList = LocalBookManager.findBookCatalogs(mBookInfo);
		if (mChapterList.size() > 0)
			return;
		Thread processDocThread = new Thread(new Runnable() {
			public void run() {
				try {
					doExtractChapter();
					if (!stopProcessFlag)
						LocalBookManager.saveBookCatalogs(mBookInfo, mChapterList);
					System.gc();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		processDocThread.setPriority(Thread.MIN_PRIORITY);
		processDocThread.start();
	}

	protected abstract void doExtractChapter() throws Exception;

	public void switchOrientation(int renderWidth, int renderHeight) {
		mRenderWidth = renderWidth;
		mRenderHeight = renderHeight;
		resetTextHeight();
	}

	public static final String TEXT_STR = "中";
	private static final String SMALLEST_CHAR = "i";

	private final void resetTextHeight() {
		Rect rect = RectObjectPool.getObject();
		mPaint.getTextBounds(TEXT_STR, 0, TEXT_STR.length(), rect);
		mTextHeight = rect.bottom - rect.top;
		float lineHeight = mTextHeight + mLineSpacing;

		mMaxPageSize = (int) (mRenderHeight / lineHeight);
		if ((mRenderHeight % lineHeight) >= mTextHeight)
			++mMaxPageSize;

		int textWidth = rect.right - rect.left;
		mMaxCharCountPerLine = (int) (mRenderWidth / textWidth);

		mPaint.getTextBounds(SMALLEST_CHAR, 0, SMALLEST_CHAR.length(), rect);
		mMaxCharCountPerPage = (int) (mRenderWidth / (rect.right - rect.left) * mMaxPageSize);

		RectObjectPool.freeObject(rect);
	}

	public final void onResetTextSize() {
		invalidatePrevPagesCache();
		resetTextHeight();
	}

	protected final void scrollDownBuffer() {
		// throw away characters at the head of the buffer if some requrements
		// are met
		if (mByteMetaList.size() > 0 && mPageCharOffsetInBuffer >= mByteMetaList.peek().charCount) {
			// once we throw away characters at the beginning of the buffer, the
			// cache will be invalid, we MUST clear it
			invalidatePrevPagesCache();

			ByteMeta meta = mByteMetaList.removeFirst();
			mContentBuf.delete(0, meta.charCount);

			// "meta.charCount" characters were thrown away, so we have to minus
			// the "offset in buffer" by "meta.charCount"
			mPageCharOffsetInBuffer -= meta.charCount;
			// increase the start byte offset where we start reading the file
			mReadByteBeginOffset = meta.byteOffset + meta.byteCount;
		}

		try {
			// position the file pointer at the end position of the
			// last/previous read
			mRandBookFile.seek(mReadByteEndOffset);
			int lenRead = mRandBookFile.read(mByteBuffer);
			if (lenRead > 0) {
				// skip last incomplete bytes if there are some of them
				lenRead -= CharsetUtil.getByteCountOfLastIncompleteChar(mByteBuffer, lenRead, mEncoding);

				// append the text to the end of the buffer
				String content = new String(mByteBuffer, 0, lenRead, mEncoding.getName());
				mContentBuf.append(content);

				// store the meta data of the current read
				mByteMetaList.add(new ByteMeta(mReadByteEndOffset, lenRead, content.length()));
				// grow the end byte offset
				mReadByteEndOffset += lenRead;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected final void scrollUpBuffer() {
		try {
			// we read backwards at most "mByteBuffer.length" bytes each time
			long positionToSeek = mReadByteBeginOffset - mByteBuffer.length;
			int bytesToRead = mByteBuffer.length;
			if (positionToSeek < 0) { // if we reach the beginning of the file
				positionToSeek = 0;
				bytesToRead = (int) mReadByteBeginOffset;
			}
			mRandBookFile.seek(positionToSeek);
			int lenRead = mRandBookFile.read(mByteBuffer, 0, bytesToRead);
			if (lenRead > 0) {
				int incompleteByteCount = 0;
				if (positionToSeek > 0) { // if we are not at the beginning of
											// the file
					incompleteByteCount = CharsetUtil
							.getByteCountOfFirstIncompleteChar(mByteBuffer, lenRead, mEncoding);
					if (incompleteByteCount > 0) {
						lenRead -= incompleteByteCount;
					}
				}
				String content = new String(mByteBuffer, incompleteByteCount, lenRead, mEncoding.getName());
				mContentBuf.insert(0, content);

				// since we are reading backwards(towards the beginning of the
				// file), we need to decrease
				// the current "start byte offset" from where we start reading
				// the file
				mReadByteBeginOffset -= lenRead;
				mPageCharOffsetInBuffer += content.length();
				// prepend the meta data to the beginning of the file
				mByteMetaList.addFirst(new ByteMeta(mReadByteBeginOffset, lenRead, content.length()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getCharCountOfCurPage() {
		return mCharCountOfPage;
	}

	public boolean turnNextPage() {
		if (!isLastPage()) {
			mCharOffsetCache.add(mPageCharOffsetInBuffer);
			if (mCharOffsetCache.size() > 20)
				mCharOffsetCache.removeFirst();
			mPageCharOffsetInBuffer += mCharCountOfPage;
			return true;
		}
		return false;
	}

	public boolean isLastPage() {
		return mPageCharOffsetInBuffer + mCharCountOfPage < mContentBuf.length() ? false : true;
	}

	public boolean turnPreviousPage() {
		if (mCharOffsetCache.size() > 0) {
			mPageCharOffsetInBuffer = mCharOffsetCache.removeLast();
			return true;
		}
		// check if we need to read some bytes and prepend them to the beginning
		// of the buffer(if we are near the beginning of the buffer)
		if (mReadByteBeginOffset > 0 && mPageCharOffsetInBuffer - mMaxCharCountPerPage < 0) {
			scrollUpBuffer();
		}
		if (mPageCharOffsetInBuffer > 0) {
			mCharOffsetList.clear();
			// *previous* page should start from here *at most*(if the
			// *previous* page contains mMaxCharCountPerPage characters, which
			// is quite unusual)
			int beginCharOffset = mPageCharOffsetInBuffer - mMaxCharCountPerPage;
			if (beginCharOffset < 0) { // if we reach the beginning of the file
				beginCharOffset = 0;
			} else {
				// try finding a NEWLINE(paragraph boundary)
				while (beginCharOffset > 0) {
					if (mContentBuf.lastIndexOf(NEW_LINE_STR, beginCharOffset) != -1)
						break;
					--beginCharOffset;
				}
			}
			// end(exclusive) of the *previous* page is the start(inclusive) of
			// the current page
			int endCharOffset = mPageCharOffsetInBuffer;
			if (mContentBuf.charAt(endCharOffset - 1) == NEW_LINE_CHAR) {
				--endCharOffset;
			}
			int lineCharOffset = beginCharOffset;
			int newlineOffset = mContentBuf.lastIndexOf(NEW_LINE_STR, endCharOffset - 1);
			if (newlineOffset != -1) {
				lineCharOffset = newlineOffset + 1;

				int lineCount = 0;
				while (endCharOffset > beginCharOffset) {
					if (lineCharOffset == endCharOffset) {
						newlineOffset = mContentBuf.lastIndexOf(NEW_LINE_STR, endCharOffset - 1);
						if (newlineOffset != -1) {
							lineCharOffset = newlineOffset + 1;
						} else {
							lineCharOffset = beginCharOffset;
						}

						lineCount = 0;
					}
					int charCount = LayoutUtil.breakText(mContentBuf, lineCharOffset, endCharOffset, mRenderWidth,
							mPaint);

					// note: addFirst
					mCharOffsetList.addFirst(lineCharOffset);

					lineCharOffset += charCount;
					++lineCount;
					if (lineCharOffset == endCharOffset) {
						while (--lineCount >= 0) {
							// note: removeFirst and then add to the last
							mCharOffsetList.add(mCharOffsetList.removeFirst());
						}
						if (mCharOffsetList.size() >= mMaxPageSize)
							break;

						lineCharOffset = endCharOffset = newlineOffset;
					}
				}
			} else {
				while (lineCharOffset < endCharOffset) {
					int charCount = LayoutUtil.breakText(mContentBuf, lineCharOffset, endCharOffset, mRenderWidth,
							mPaint);

					mCharOffsetList.addFirst(lineCharOffset);

					lineCharOffset += charCount;
				}
			}

			int contentHeight = 0;
			for (int i = 0; i < mCharOffsetList.size(); ++i) {
				if (contentHeight > 0) {
					boolean isNewline = mContentBuf.charAt(mCharOffsetList.get(i - 1) - 1) == NEW_LINE_CHAR;
					if (isNewline)
						contentHeight += mParagraphSpacing;
					else
						contentHeight += mLineSpacing;
				}
				contentHeight += mTextHeight;
				if (contentHeight > mRenderHeight) {
					mPageCharOffsetInBuffer = mCharOffsetList.get(i - 1);
					break;
				} else if (i == 0) {
					mPageCharOffsetInBuffer = mCharOffsetList.get(mCharOffsetList.size() - 1);
				}
			}

			return true;
		}
		return false;
	}

	public void prepareGetLines() {
		mCurContentHeight = 0;
		mCharCountOfPage = 0;

		// only do this check once per page
		if (mReadByteEndOffset < mFileSize && mPageCharOffsetInBuffer + mMaxCharCountPerPage > mContentBuf.length())
			scrollDownBuffer();

		// reset newline to the current offset
		mNewlineIndex = mPageCharOffsetInBuffer;
	}

	public final static byte GET_NEXT_LINE_FLAG_HAS_NEXT_LINE = 1 << 0;
	public final static byte GET_NEXT_LINE_FLAG_SHOULD_JUSTIFY = 1 << 1;
	public final static byte GET_NEXT_LINE_FLAG_PARAGRAPH_ENDS = 1 << 2;

	public final byte getNextLine(StringBuilder sb) {
		// reach the end of the file
		if (mPageCharOffsetInBuffer + mCharCountOfPage >= mContentBuf.length())
			return 0;

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

	public boolean watchingToRead() {
		return true;
	}

	protected void invalidatePrevPagesCache() {
		mCharOffsetCache.clear();
	}

	protected boolean alwayWaitProcess = true;

	public boolean isAlwayWaitProcess() {
		return alwayWaitProcess;
	}

	public abstract boolean turnNextChapter();

	public abstract boolean turnPreviousChapter();

	public boolean hasNextChapter() {
		return mChapterList.size() > mReadChapterIndex + 1;
	}

	public boolean hasPreviousChapter() {
		return mReadChapterIndex > 0;
	}

	public void mergeAdditionalChapter(List<LocalBookCatalog> additionalChapterList) {
		for (LocalBookCatalog bookChapter : additionalChapterList) {
			mChapterList.add(bookChapter);
		}
	}

	protected void calculateReadChapterIndex() {
	}

	public void calculatePagePosition() {
		try {
			mPageBeginPosition = mReadByteBeginOffset
					+ mContentBuf.substring(0, mPageCharOffsetInBuffer).getBytes(mEncoding.getName()).length;
			calculateReadChapterIndex();
			LocalBookManager.saveLastReadPosition(mBookInfo, mPageBeginPosition, mBookInfo.getProgress());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final String getCurrentPageFrontText(int length) {
		if (mPageCharOffsetInBuffer + length < mContentBuf.length())
			return mContentBuf.substring(mPageCharOffsetInBuffer, mPageCharOffsetInBuffer + length);
		return mContentBuf.substring(mPageCharOffsetInBuffer);
	}

	public String getCurrentChapterFrontText(int wordCount) {
		try {
			byte[] tempContentBuf = new byte[mEncoding.getMaxCharLength() * wordCount];
			mRandBookFile.seek(mChapterList.get(mReadChapterIndex).getBeginPosition());
			int lenRead = mRandBookFile.read(tempContentBuf);
			if (lenRead > 0) {
				// lenRead -=
				// CharsetUtil.getByteCountOfLastIncompleteChar(tempContentBuf,
				// lenRead, mEncoding);
				return StringUtil.removeEmptyChar(new String(tempContentBuf, 0, lenRead, mEncoding.getName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected long calculatePosition(float percentage) {
		return percentage >= 1f ? mFileSize - 200 : (long) (mFileSize * percentage);
	}

	protected final long getSafetyPosition(long fileBeginPosition) {
		if (fileBeginPosition == 0)
			return 0;
		try {
			byte[] tempContentBuf = new byte[1024 * 4];
			mRandBookFile.seek(fileBeginPosition);
			int lenRead = mRandBookFile.read(tempContentBuf);

			int skippedBytes = CharsetUtil.getByteCountOfFirstIncompleteChar(tempContentBuf, lenRead, mEncoding);
			if (tempContentBuf[skippedBytes] == NEW_LINE_CHAR) {
				++skippedBytes;
			} else if (tempContentBuf[skippedBytes] == '\r') {
				++skippedBytes;
				if (tempContentBuf[skippedBytes] == NEW_LINE_CHAR)
					++skippedBytes;
			}
			return fileBeginPosition + skippedBytes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileBeginPosition;
	}

	protected final long getBackmostPosition() {
		long beginPosition = mFileSize
				- Double.valueOf(mMaxCharCountPerLine * (mMaxPageSize / 3) * mEncoding.getMaxCharLength()).intValue();
		return beginPosition < 1 ? 0 : getSafetyPosition(beginPosition);
	}

	public abstract boolean adjustReadingProgressByPrecent(float percentage);

	public abstract boolean adjustReadingProgressByPosition(long beginPosition);

	public abstract float calculateReadingProgress();

	public long getPageBeginPosition() {
		return mPageBeginPosition;
	}

	public int getChapterCount() {
		return mChapterList.size();
	}

	public int getPageLineCount() {
		return mMaxPageSize;
	}

	public int getTextHeight() {
		return mTextHeight;
	}

	public int getReadingChapterIndex() {
		return mReadChapterIndex == -1 ? 0 : mReadChapterIndex;
	}

	public List<LocalBookCatalog> getChapterList() {
		return mChapterList;
	}

	public LocalBookCatalog getCurrentReadingChapter() {
		if (mChapterList != null && mReadChapterIndex >= 0 && mChapterList.size() > mReadChapterIndex) {
			return mChapterList.get(mReadChapterIndex);
		}
		return null;
	}

	public boolean canRead() {
		return mReadChapterIndex == 0;
	}

	class ByteMeta {
		long byteOffset;
		int byteCount;
		int charCount;

		public ByteMeta(long byteOffset, int byteCount, int charCount) {
			this.byteOffset = byteOffset;
			this.byteCount = byteCount;
			this.charCount = charCount;
		}
	}

	public void onStop() {
		stopProcessFlag = true;
		try {
			if (mRandBookFile != null)
				mRandBookFile.close();
		} catch (Exception e) {
			// System.out.println("Document OnStop Error " + e.getMessage());
		}
		System.gc();
	}

	public void restLineSpace(int lineSpace) {
		mLineSpacing = lineSpace;
		resetTextHeight();
	}

}
