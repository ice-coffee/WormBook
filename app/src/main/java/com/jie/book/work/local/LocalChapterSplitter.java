package com.jie.book.work.local;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import com.bond.bookcatch.local.vo.LocalBookCatalog;
import com.jie.book.work.utils.CharsetUtil;

/**
 * @author vince
 * @since 2011-12-04
 */
public class LocalChapterSplitter {
	public LocalChapterSplitter(String filePath) {
		bookFile = new File(filePath);
	}
	
	private File bookFile;
	private Encoding encoding;
	private long bufBeginPosition, fileSize;
	private RandomAccessFile randFile;
	
	private int[] breakWordInts;
	private int paraMaxLength, catalogMaxLength;
	private int readLength, paraInCount, paraLength, beginIndex, endIndex, matchCount, wordEndIndex;
	
	private int byteBufLength = 5 * 1024;
	private byte[] bytes = new byte[byteBufLength];
	
	/**
	 * 经测试，BIG5、GBK、UTF8编码下的结果保持一致，UTF16LE、UTF16BE会出现少量章节获取偏差的问题，在此不再查究原因
	 * 注意：出现断取不到某些章节的问题时，可调整paraMaxLength值来测试，下面忽略太近章节的操作也会对结果有所影响
	 * 另外，在调整paraMaxLength值及忽略太近章节的算法后需要做严格的多编码测试，建议不要轻易为之
	 */
	public void splitChapter(ExtractedCallback changedCallback) throws Exception {
		randFile = new RandomAccessFile(bookFile, "r");
		
		encoding = CharsetUtil.determineEncoding(randFile);
		if(encoding == Encoding.UNKNOWN) throw new UnsupportedEncodingException("not determine file Encoding!");
		
		fileSize = bookFile.length();
		
		//确定章节段的上限字节数
		String testStr = null;
		if(encoding == Encoding.BIG5) {
			testStr = "正文 第一千七百三十二章 風流邪神在都市之：找份能夠養活自己的工作 手機閱讀 mbook.cn\r\n";
			paraMaxLength = testStr.getBytes(encoding.getName()).length;
		} else {
			testStr = "正文 第一千七百三十二章 风流邪神在都市之：找份能够养活自己的工作 手机阅读 mbook.cn\r\n";
			paraMaxLength = testStr.getBytes(encoding.getName()).length;
		}
		
		//确定章节两个关键字('第'、'章')之间的上限字节数
		testStr = "第一千七百三十二章";
		catalogMaxLength = testStr.getBytes(encoding.getName()).length;
		
		breakWordInts = getWordByteInts("\n");
		int[] firstWordInts = getWordByteInts("第");
		int[] lastWordInts1 = getWordByteInts("章");
		int[] lastWordInts2 = getWordByteInts("节");
		
		do {
			randFile.seek(bufBeginPosition);
			readLength = randFile.read(bytes);
			readLength -= CharsetUtil.getByteCountOfLastIncompleteChar(bytes, readLength, encoding);
			
			for (beginIndex = 0, endIndex = 0; endIndex < readLength - breakWordInts.length; endIndex++) {
				matchCount = 0;
				for (int i = 0; i < breakWordInts.length; i++) {
					if((bytes[endIndex++] & 0xFF) != breakWordInts[i]) break;
					matchCount++;
				}
				endIndex--;
				
				if(matchCount == breakWordInts.length) {
					paraLength = endIndex - beginIndex;
					if(paraLength < paraMaxLength) {
						if(!splitWithFormula(firstWordInts, lastWordInts1, changedCallback))
							splitWithFormula(firstWordInts, lastWordInts2, changedCallback);
					}
					bufBeginPosition += paraLength + 1;
					beginIndex = endIndex + 1;
				}
			}
			//当前内容块没有任何换行符
			if(beginIndex == 0) bufBeginPosition += readLength;
			if(changedCallback.isBlocked()) return;
		} while (bufBeginPosition < fileSize);
	}
	
	private boolean splitWithFormula(int[] firstWordInts, int[] lastWordInts, ExtractedCallback changedCallback) throws UnsupportedEncodingException {
		//从该段落中找到两个断章关键字(从后向前搜)
		paraInCount = endIndex - firstWordInts.length;
		for (int index = beginIndex; index < paraInCount; index++) {
			matchCount = 0;
			for (int i = 0; i < lastWordInts.length; i++) {
				if((bytes[index++] & 0xFF) != lastWordInts[i]) break;
				matchCount++;
			}
			index--;
			
			if(matchCount == lastWordInts.length) {
				wordEndIndex = index;
				for (index -= lastWordInts.length; index > beginIndex; index--) {
					matchCount = 0;
					for (int i = firstWordInts.length - 1; i > -1; i--) {
						if((bytes[index--] & 0xFF) != firstWordInts[i]) break;
						matchCount++;
					}
					index++;
					
					if(matchCount == firstWordInts.length) {
						if(wordEndIndex - index < catalogMaxLength) {
							//检测当前章节与上一章的距离，忽略离得太近的两个章节
							if(changedCallback.getSize() == 0 || bufBeginPosition - changedCallback.getLastItem().getEndPosition() > paraMaxLength) {
//								int length = paraLength - breakWordInts.length;
								int length = paraLength;
								//utf16 最后会有一个值为13的字节转换不了，原因未明
								if(encoding == Encoding.UTF16BE || encoding == Encoding.UTF16LE) length--;
								
								changedCallback.extract(new LocalBookCatalog(
									LocalChapterSplitter.trimContinuousSpaces(new String(bytes, beginIndex, length, encoding.getName())),
//									CatalogSplitter.trimContinuousSpaces(new String(bytes, index, endIndex - index, encoding.getName())),
									bufBeginPosition, bufBeginPosition + length
								));
								
								return true;
							}
						}
					}
				}
				//如果未能找到'第'字，将当前位置移到'章'字后一位，再重新开始查找
				if(index < paraInCount) index = wordEndIndex;
			}
		}
		return false;
	}
	
	public int[] getWordByteInts(String word) throws Exception {
		byte[] textBytes = word.getBytes(encoding.getName());
		int[] textInts = new int[textBytes.length];
		for (int i = 0; i < textBytes.length; i++) {
			textInts[i] = textBytes[i] & 0xFF;
		}
		return textInts;
	}
	
	public static String trimContinuousSpaces(String str) {
		StringBuilder sb = new StringBuilder(str);
		int index = -1;
		
		//replace chinese space and tab space
		while((index = sb.indexOf("　")) > -1 || (index = sb.indexOf("	")) > -1) {
			sb.deleteCharAt(index).insert(index, ' ');
		}
		
		//replace continuous spaces
		while((index = sb.indexOf("  ")) > -1) {
			sb.delete(index, index + 2).insert(index, ' ');
		}
		
		return sb.toString().trim();
	}
	
	public int computeProgress() {
		return (int) Math.floor(Float.valueOf(bufBeginPosition).floatValue() / fileSize * 100);
	}
	
	public interface ExtractedCallback {
		void extract(LocalBookCatalog chapter);
		LocalBookCatalog getLastItem();
		boolean isBlocked();
		int getSize();
	}
}
