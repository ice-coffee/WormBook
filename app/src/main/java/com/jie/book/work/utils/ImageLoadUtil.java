package com.jie.book.work.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.jie.book.work.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageLoadUtil {

	public static final int MAX_DISK_CACHE_FILE_SIZE = 50 * 1024 * 1024;

	public enum ImageType {
		NULL, BOOK, SUBJECT, APP_ICON, APP_IMAGE;
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(MAX_DISK_CACHE_FILE_SIZE)
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}

	public static void loadImage(ImageView imageView, String url, ImageType imageType) {
		if (imageView == null || StringUtil.isEmpty(url) || !url.startsWith("http://")) {
			return;
		}
		int loadImageRes = 0;
		int erroImageRes = 0;
		if (imageType == ImageType.BOOK) {
			loadImageRes = R.drawable.bg_default_book;
			erroImageRes = R.drawable.bg_null_book;
		}
		if (imageType == ImageType.SUBJECT) {
			loadImageRes = R.drawable.bg_default_subject;
			erroImageRes = R.drawable.bg_default_subject;
		}
		if (imageType == ImageType.APP_ICON) {
			loadImageRes = R.drawable.bg_default_game;
			erroImageRes = R.drawable.bg_default_game;
		}
		if (imageType == ImageType.APP_IMAGE) {
			loadImageRes = R.drawable.bg_default_app_image;
			erroImageRes = R.drawable.bg_default_app_image;
		}
		if (imageType == ImageType.NULL) {
			loadImageRes = 0;
			erroImageRes = 0;
		}
		DisplayImageOptions option = createDisplayOption(loadImageRes, erroImageRes, true, null);
		ImageLoader.getInstance().displayImage(url, imageView, option);
	} 

	public static void loadRoundImage(ImageView imageView, int defaultRes, String url) {
		imageView.measure(0, 0);
		int mWidth = imageView.getMeasuredWidth();
		int width = imageView.getWidth();
		DisplayImageOptions option = createDisplayOption(defaultRes, defaultRes, true, new RoundedBitmapDisplayer(
				mWidth > width ? mWidth : width));
		ImageLoader.getInstance().displayImage(url, imageView, option);
	}

	private static DisplayImageOptions createDisplayOption(int placeHolderImgRes, int erroImgRes,
			boolean cacheInMemory, BitmapDisplayer displayer) {
		DisplayImageOptions.Builder bulider = new DisplayImageOptions.Builder();
		bulider.showImageOnLoading(placeHolderImgRes);
		bulider.showImageForEmptyUri(erroImgRes);
		bulider.showImageOnFail(erroImgRes);
		bulider.cacheInMemory(true);
		bulider.cacheOnDisk(true);
		bulider.considerExifParams(true);
		bulider.bitmapConfig(Bitmap.Config.RGB_565);
		if (displayer != null)
			bulider.displayer(displayer);
		return bulider.build();
	}
}
