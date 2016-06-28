package com.jie.book.work.utils;

import java.nio.CharBuffer;
import java.util.LinkedList;

public class CharBufferUtil {

	public static void removeLast(LinkedList<CharBuffer> charBufferList) {
		if (charBufferList != null && charBufferList.size() > 0) {
			CharBuffer lastCharBuffer = charBufferList.removeLast();
			destoryCharBuffer(lastCharBuffer);
		}
	}

	public static void removeFirst(LinkedList<CharBuffer> charBufferList) {
		if (charBufferList != null && charBufferList.size() > 0) {
			CharBuffer firstCharBuffer = charBufferList.removeFirst();
			destoryCharBuffer(firstCharBuffer);
		}
	}

	public static void clear(LinkedList<CharBuffer> charBufferList) {
		if (charBufferList != null && charBufferList.size() > 0) {
			for (CharBuffer charBuffer : charBufferList) {
				destoryCharBuffer(charBuffer);
			}
			charBufferList.clear();
			// System.gc();
		}
	}

	// 销毁一个CharBuffer，防止oom
	public static void destoryCharBuffer(CharBuffer charBuffer) {
		if (charBuffer != null) {
			charBuffer.clear();
			// System.gc();
		}
	}
}
