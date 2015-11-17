package org.slstudio.hsinchuiot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil {
	public final static String getStringMD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getLargeFileMD5String(File file)
			throws OutOfMemoryError, IOException {

		FileInputStream in = new FileInputStream(file);
		MessageDigest messagedigest;
		try {
			messagedigest = MessageDigest.getInstance("MD5");

			byte[] buffer = new byte[1024 * 100];
			int len = 0;

			while ((len = in.read(buffer)) > 0) {
				messagedigest.update(buffer, 0, len);
			}

			return byte2hex(messagedigest.digest());
		} catch (NoSuchAlgorithmException e) {
			IOTLog.e("getFileSha1->NoSuchAlgorithmException###", e.toString());
			e.printStackTrace();
		} catch (OutOfMemoryError e) {

			IOTLog.e("getFileSha1->OutOfMemoryError###", e.toString());
			e.printStackTrace();
			throw e;
		} finally {
			in.close();
		}
		return null;
	}

	public static String getFileMD5String(File file) throws OutOfMemoryError,
			IOException {
		if (file.length() > (1024 * 1024)) {
			return getLargeFileMD5String(file);
		}
		try {
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			FileInputStream in = new FileInputStream(file);
			FileChannel ch = in.getChannel();
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY,
					0, file.length());
			mdInst.update(byteBuffer);
			return bufferToHex(mdInst.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		char c1 = hexDigits[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	private static String getLargeFileSha1String(File file)
			throws OutOfMemoryError, IOException {

		FileInputStream in = new FileInputStream(file);
		MessageDigest messagedigest;
		try {
			messagedigest = MessageDigest.getInstance("SHA-1");

			byte[] buffer = new byte[1024 * 1024 * 10];
			int len = 0;

			while ((len = in.read(buffer)) > 0) {
				messagedigest.update(buffer, 0, len);
			}

			return byte2hex(messagedigest.digest());
		} catch (NoSuchAlgorithmException e) {
			IOTLog.e("getFileSha1->NoSuchAlgorithmException###", e.toString());
			e.printStackTrace();
		} catch (OutOfMemoryError e) {

			IOTLog.e("getFileSha1->OutOfMemoryError###", e.toString());
			e.printStackTrace();
			throw e;
		} finally {
			in.close();
		}
		return null;
	}

	public static String getLargeFileSha512String(File file)
			throws OutOfMemoryError, IOException {

		FileInputStream in = new FileInputStream(file);
		MessageDigest messagedigest;
		try {
			messagedigest = MessageDigest.getInstance("SHA-512");

			byte[] buffer = new byte[1024 * 100];
			int len = 0;

			while ((len = in.read(buffer)) > 0) {
				messagedigest.update(buffer, 0, len);
			}
			return byte2hex(messagedigest.digest());
		} catch (NoSuchAlgorithmException e) {
			IOTLog.e("getFileSha512->NoSuchAlgorithmException###", e.toString());
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			IOTLog.e("getFileSha512->OutOfMemoryError###", e.toString());
			e.printStackTrace();
			throw e;
		} finally {
			in.close();
		}
		return null;
	}

	public static String getFileSha1String(File file) throws OutOfMemoryError,
			IOException {
		if (file.length() > (1024 * 1024)) {
			return getLargeFileSha1String(file);
		}
		FileInputStream in = new FileInputStream(file);
		MessageDigest messagedigest;
		try {
			messagedigest = MessageDigest.getInstance("SHA-1");

			byte[] buffer = new byte[1024 * 512];
			int len = 0;

			while ((len = in.read(buffer)) > 0) {
				messagedigest.update(buffer, 0, len);
			}

			return byte2hex(messagedigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
		return null;
	}

	private static String byte2hex(byte[] arr) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; ++i) {
			sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1,
					3));
		}
		return sb.toString();
	}

	private final static String[] str = { "0", "1", "2", "3", "4", "5", "6",
			"7", "8", "9" };
	private final static String[] str1 = { "q", "w", "e", "r", "t", "y", "u",
			"i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z",
			"x", "c", "v", "b", "n", "m" };

	public static String randomPassword() {

		String password = "";

		for (int i = 0; i < 2; i++) {
			int a = (int) (Math.random() * 26);
			password += str1[a];
		}
		for (int i = 0; i < 3; i++) {
			int a = (int) (Math.random() * 9);
			password += str[a];
		}
		for (int i = 0; i < 3; i++) {
			int a = (int) (Math.random() * 26);
			password += str1[a].toUpperCase();
		}
		return password;

	}
}
