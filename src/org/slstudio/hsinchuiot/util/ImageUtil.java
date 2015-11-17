package org.slstudio.hsinchuiot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.service.http.HttpsImageDownloader;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.content.CursorLoader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

public class ImageUtil {
	private static int FREE_SD_SPACE_NEEDED_TO_CACHE = 1;
	private static int MB = 1024 * 1024;

	public static void initImageEngine(Context context, String root) {
		Context c = context.getApplicationContext();
		File cacheDir = new File(root); // 图片持久化的位置(本地sdcard和机身ROM)
		ImageLoader imageLoader = ImageLoader.getInstance(); // 实例化图片加载引擎
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				c)

				.threadPoolSize(Constants.ImageLoader.IMAGE_ENGINE_CORETHREAD)
				// 可以同步加载图片的个数
				.threadPriority(Thread.NORM_PRIORITY - 1)
				// 可设置当前线程的等级,在Thread.MAX_PRIORITY和Thread.MIN_PROIORITY之间
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(
						new UsingFreqLimitedMemoryCache(
								Constants.ImageLoader.IMAGE_ENGINE_FREQ_LIMITED_MEMECACHE)) // 设置缓存内存的大小,在此设置2M的缓存
				.diskCache(new UnlimitedDiscCache(cacheDir)) // 设置需要持久化的磁盘位置
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // 文件名的生成策略、
				.defaultDisplayImageOptions(
						Constants.ImageLoader.DEFAULT_IMAGE_OPTIONS) // 可以自定义默认设置
				.imageDownloader(new HttpsImageDownloader(context)).build();
		imageLoader.init(config);
	}

	public static String getPathFromUri(Context context, Uri uri) {

		String[] projection = { MediaStore.Images.Media.DATA };

		CursorLoader cursorLoader = new CursorLoader(context, uri, projection,
				null, // selection
				null, // selectionArgs
				null // sortOrder
		);

		Cursor cursor = cursorLoader.loadInBackground();

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	public static Bitmap getImageThumbnail(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(imagePath, options);

		options.inJustDecodeBounds = false;

		int h = options.outHeight;
		int w = options.outWidth;

		if (width <= 0) {
			width = w;
		}
		if (height <= 0) {
			height = h;
		}

		int beWidth = w / width;
		int beHeight = h / height;

		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}

		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(imagePath, options);

		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;

	}

	public static Bitmap getImageThumbnailWithProportion(String imagePath,
			int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(imagePath, options);

		options.inJustDecodeBounds = false;

		int h = options.outHeight;
		int w = options.outWidth;

		int newWidth = width;
		int newHeight = height;

		if (width <= 0) {
			width = w;
		}
		if (height <= 0) {
			height = h;
		}

		float beWidth = (float) w / (float) width;
		float beHeight = (float) h / (float) height;

		float be = 1f;
		if (beWidth < beHeight) {
			be = beWidth;
			newHeight = (int) ((float) h / be);
		} else {
			be = beHeight;
			newWidth = (int) ((float) w / be);
		}

		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = (int) be;
		bitmap = BitmapFactory.decodeFile(imagePath, options);

		bitmap = ThumbnailUtils.extractThumbnail(bitmap, newWidth, newHeight,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;

	}

	public static void writeBitmapToFile(Bitmap bitmap, String filename) {
		if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			return;
		}
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return;
		}

		File file = new File(filename);
		try {
			file.createNewFile();
			OutputStream os = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			os.flush();
			os.close();
		} catch (FileNotFoundException fnfExp) {

		} catch (IOException ioExp) {
			ioExp.printStackTrace();
		}
	}

	public static Bitmap getBitmapFromFile(String filename) {
		FileInputStream fis = null;
		try {
			if (filename == null) {
				return null;
			}
			File f = new File(filename);

			if (f.exists() && f.isFile()) {
				fis = new FileInputStream(f);
				return BitmapFactory.decodeStream(fis);
			} else {
				return null;
			}

		} catch (Exception exp) {
			exp.printStackTrace();
			return null;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static Drawable getDrawableFromFile(String filename) {
		Bitmap bitmap = getBitmapFromFile(filename);
		if (bitmap != null) {
			return new BitmapDrawable(bitmap);
		}
		return null;
	}

	public static Bitmap getBitmapFromResource(Resources resouces, int resId) {
		return BitmapFactory.decodeResource(resouces, resId);
	}

	private static int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize()) / MB;

		return (int) sdFreeMB;
	}
}
