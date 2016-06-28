package com.jie.book.work.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

/**
 * 对Bitmap资源的优化处理，按照显示区域的尺寸来获取经过缩放的图片， 最大限度避免OutOfMemoryError VM Buget导致强制退出的问题
 * 
 * @author vince
 * @since 2012-05-24
 */
public class BitmapUtil {

	public static Bitmap loadBitmapInRes(int resId, View view) {
		if (resId <= 0)
			return null;
		int[] dimension = getViewDimension(view);
		return loadBitmapInRes(view.getResources(), resId, dimension[0], dimension[1]);
	}

	public static Bitmap loadBitmapInRes(Resources res, int resId, int reqWidth, int reqHeight) {
		if (resId <= 0 || reqWidth <= 0 || reqHeight <= 0)
			return null;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		if (options.outWidth <= 0 || options.outHeight <= 0)
			return null;

		int inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
		if (inSampleSize <= 1)
			return BitmapFactory.decodeResource(res, resId);

		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static Bitmap loadBitmapInFile(String filePath, View view) {
		if (StringUtil.isEmpty(filePath))
			return null;
		int[] dimension = getViewDimension(view);
		return loadBitmapInFile(filePath, dimension[0], dimension[1]);
	}

	/**
	 * NOTE : inSampleSize ==
	 * 1时，如果按照这个option去decode，会取到比源图大一倍的Bitmap，所以直接decode返回即可
	 */
	public static Bitmap loadBitmapInFile(String filePath, int reqWidth, int reqHeight) {
		if (StringUtil.isEmpty(filePath))
			return null;
		if (reqWidth <= 0 || reqHeight <= 0)
			return null;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		if (options.outWidth <= 0 || options.outHeight <= 0)
			return null;

		int inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
		if (inSampleSize <= 1)
			return BitmapFactory.decodeFile(filePath);

		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}

	public static int calculateInSampleSize(int outWidth, int outHeight, int reqWidth, int reqHeight) {
		int inSampleSize = 1;
		if (outHeight > reqHeight || outWidth > reqWidth) {
			inSampleSize = Math.round(outWidth > outHeight ? outHeight / reqHeight : outWidth / reqWidth);
		}
		// System.out.println("outWidth : " + outWidth + " outHeight : " +
		// outHeight + " reqWidth : " + reqWidth + " reqHeight : " + reqHeight +
		// " inSampleSize : " + inSampleSize);
		return inSampleSize;
	}

	/**
	 * width & height 设置为 wrap_content | fill_parent | match_parent
	 * 时，取屏幕尺寸作为View尺寸， 注意，应该尽量避免设置这种未知尺寸的属性，因为基本上图片都会限制显示区域，很少有全屏显示的！！！！！！
	 **/
	public static int[] getViewDimension(View view) {
		LayoutParams lotParams = view.getLayoutParams();

		int width = lotParams.width;
		if (width <= 0) {
			width = ((WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
					.getWidth();
		}

		int height = lotParams.height;
		if (height <= 0) {
			height = ((WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
					.getHeight();
		}

		return new int[] { width, height };
	}

	public static void recyledBitmapDrawable(BitmapDrawable bitmapDrawable) {
		Bitmap bitmap = bitmapDrawable.getBitmap();
		recyledBitmap(bitmap);
	}

	public static void recyledBitmap(Bitmap bitmap) {
		try {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
				System.gc();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
