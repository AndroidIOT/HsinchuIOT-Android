package org.slstudio.hsinchuiot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.slstudio.hsinchuiot.Constants;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class SysUtils {

	// 保存序列化对象
	public static void saveObj(String key, Serializable obj) {
		try {
			File d = new File(Constants.CACHE_FILE_PATH + "/serializable/");
			if (!d.exists()) {
				d.mkdirs();
			}

			File file = new File(Constants.CACHE_FILE_PATH + "/serializable/"
					+ key + ".so");
			// if (file.exists()) {
			// file.delete();
			// }

			if (!file.exists()) {
				file.createNewFile();
			}

			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(Constants.CACHE_FILE_PATH
							+ "/serializable/" + key + ".so"));
			out.writeObject(obj);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 读序列化对象
	public static Serializable readObj(String key) {
		Serializable obj = null;
		ObjectInputStream in = null;
		try {
			File file = new File(Constants.CACHE_FILE_PATH + "/serializable/"
					+ key + ".so");
			if (!file.exists()) {
				return null;
			} else {
				in = new ObjectInputStream(new FileInputStream(
						Constants.CACHE_FILE_PATH + "/serializable/" + key
								+ ".so"));
				obj = (Serializable) in.readObject();
			}
		} catch (Exception e) {
			File file = new File(Constants.CACHE_FILE_PATH + "/serializable/"
					+ key + ".so");
			file.delete();
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

	// 删除序列化对象
	public static void deleteObj(final String key) {
		File file = new File(Constants.CACHE_FILE_PATH + "/serializable/" + key
				+ ".so");
		if (file.exists()) {
			file.delete();
		} else {
			File dir = new File(Constants.CACHE_FILE_PATH + "/serializable");
			if (dir.exists() && dir.isDirectory()) {
				File[] listFiles = dir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						if (filename.startsWith(key)) {
							return true;
						}
						return false;
					}
				});

				if (listFiles.length > 0) {
					for (int i = 0; i < listFiles.length; i++) {
						if (listFiles[i].exists()) {
							listFiles[i].delete();
						}
					}
				}
			}
		}
	}

	public static File getFileByUri(Context context, Uri uri) {
		try {

			File file = new File(uri.getPath());
			if (file.exists()) {
				return file;
			}

			String[] projection = { MediaStore.Images.Media.DATA };

			CursorLoader cursorLoader = new CursorLoader(context, uri,
					projection, null, // selection
					null, // selectionArgs
					null // sortOrder
			);

			Cursor cursor = cursorLoader.loadInBackground();

			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return new File(cursor.getString(column_index));
		} catch (Exception e) {
			return null;
		}
	}
}
