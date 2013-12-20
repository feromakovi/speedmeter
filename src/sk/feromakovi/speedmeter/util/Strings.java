package sk.feromakovi.speedmeter.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Strings {

	public static boolean isValidEmailAddress(String mail) {
		return mail.matches("^[a-zA-Z0-9._]+@[a-zA-Z0-9.]+\\..{2,3}$");
	}

	public static boolean isValidPassword(String pass) {
		return (pass != null && pass.length() > 0);
	}

	public static String getHash(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] sha1hash = md.digest(text.getBytes("UTF-8"));
			return convToHex(sha1hash);
		} catch (UnsupportedEncodingException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	private static String convToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	// return "txt" from "filename.txt"
	public static final String getExtension(String fileName) {
		if (fileName != null && fileName.contains(".")) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		return null;
	}
	
	public static final String getSimpleName(String fileName) {
		if (fileName != null && fileName.contains(".")) {
			return fileName.substring(0,fileName.lastIndexOf("."));
		}
		return null;
	}

	public static final String capitelizeFirst(final String s){
		char[] array = s.toCharArray();
		array[0] = Character.toUpperCase(array[0]);
		return new String(array);
	}
	
	public static final String niceFileName(String fileName){
		fileName = getSimpleName(fileName);
		return capitelizeFirst(fileName);
	}
}
