package com.jie.book.work.local;

import java.io.File;
import java.io.RandomAccessFile;

import com.bond.bookcatch.local.vo.LocalBookCatalog;
import com.bond.bookcatch.local.vo.LocalBookDesc;
import com.jie.book.work.utils.CharsetUtil;
import com.jie.book.work.utils.StringUtil;

public class LocalTxtDocument extends LocalDocument {
	public LocalTxtDocument(LocalBookDesc bookInfo, int renderWidth, int renderHeight) throws Exception {
		super(bookInfo, renderWidth, renderHeight);

		mRandBookFile = new RandomAccessFile(new File(mBookInfo.getFilePath()), "r");
		mEncoding = CharsetUtil.determineEncoding(mRandBookFile);
		mFileSize = mRandBookFile.length();
		if (mBookInfo.getLastReadPosition() > 0) {
			mReadByteBeginOffset = mBookInfo.getLastReadPosition();
			mReadByteEndOffset = mReadByteBeginOffset;
			mPageBeginPosition = mReadByteBeginOffset;
		}
	}

	protected void doExtractChapter() throws Exception {
		new LocalChapterSplitter(mBookInfo.getFilePath()).splitChapter(new LocalChapterSplitter.ExtractedCallback() {
			public void extract(LocalBookCatalog chapter) {
				if (chapter.getBeginPosition() == 0)
					mChapterList.remove(0);
				mChapterList.add(chapter);
			}

			public boolean isBlocked() {
				return stopProcessFlag;
			}

			public int getSize() {
				return mChapterList.size();
			}

			public LocalBookCatalog getLastItem() {
				return mChapterList.size() == 0 ? null : mChapterList.get(getSize() - 1);
			}
		});
	}

	protected void doInit() {
		scrollDownBuffer();
	}

	public boolean watchingToRead() {
		return true;
	}

	public boolean turnNextChapter() {
		int chapterIndex = getReadingChapterIndex();
		if (mChapterList.size() > ++chapterIndex)
			return adjustReadingProgressByPosition(mChapterList.get(chapterIndex).getBeginPosition());
		return false;
	}

	public boolean turnPreviousChapter() {
		int chapterIndex = getReadingChapterIndex();
		if (chapterIndex > 0)
			return adjustReadingProgressByPosition(mChapterList.get(--chapterIndex).getBeginPosition());
		return false;
	}

	protected void calculateReadChapterIndex() {
		for (int index = mChapterList.size() - 1; index > -1; index--) {
			LocalBookCatalog chapter = mChapterList.get(index);
			if (chapter.getBeginPosition() <= mPageBeginPosition) {
				mReadChapterIndex = index;
				return;
			}
		}
	}

	public boolean adjustReadingProgressByPrecent(float percentage) {
		return adjustReadingProgressByPosition(getSafetyPosition(calculatePosition(percentage)));
	}

	public boolean adjustReadingProgressByPosition(long beginPosition) {
		mContentBuf.setLength(0);
		mPageCharOffsetInBuffer = 0;
		mByteMetaList.clear();
		invalidatePrevPagesCache();
		mReadByteEndOffset = beginPosition;
		mReadByteBeginOffset = beginPosition;
		scrollDownBuffer();
		return true;
	}

	public float calculateReadingProgress() {
		if (mReadByteEndOffset == mFileSize && mPageCharOffsetInBuffer + mCharCountOfPage >= mContentBuf.length())
			return 100;

		long bufferContentByteOffset = mReadByteEndOffset - mReadByteBeginOffset;
		float precentage = (mReadByteBeginOffset + ((float) mPageCharOffsetInBuffer / mContentBuf.length())
				* bufferContentByteOffset)
				/ mFileSize;
		return precentage * 100;
	}

	// 获取当前章节标题
	public String getCurrentChapterTitle() {
		if (mChapterList != null && mChapterList.size() > 0) {
			return mChapterList.get(mReadChapterIndex).getTitle();
		} else {
			return StringUtil.EMPTY;
		}
	}

}
