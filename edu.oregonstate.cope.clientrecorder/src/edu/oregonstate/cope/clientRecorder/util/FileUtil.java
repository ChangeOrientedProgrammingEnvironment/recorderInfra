package edu.oregonstate.cope.clientRecorder.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

public class FileUtil {

	public static String decodeStream(String fileExtension, InputStream inputStream)
			throws IOException {
		if (FileUtil.knownTextFiles.contains(fileExtension))
			return getTextFileContents(inputStream);
		else
			return getBinaryFileContents(inputStream);
	}

	/**
	 * I return a base64 encoding of the file contents.
	 * @param inputStream the InputStream I have to read from.
	 * @return the base64 string containing the encoded file contents.
	 * @throws IOException if I cannot read from the InputStream.
	 */
	private static String getBinaryFileContents(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		readFromTo(inputStream, byteArrayOutputStream);
		byte[] byteArray = Base64.encodeBase64(byteArrayOutputStream.toByteArray());
		byteArrayOutputStream.close();
		return new String(byteArray);
	}

	/**
	 * I return the contents of the file as a String.
	 * @param inputStream the input stream to read from
	 * @return the String containg the file contents, or gibberish if the file is a binary file
	 * @throws IOException if I cannot read from the file
	 */
	private static String getTextFileContents(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		readFromTo(inputStream, byteArrayOutputStream);
		byte[] bytes = byteArrayOutputStream.toByteArray();
		byteArrayOutputStream.close();
		return new String(bytes);
	}

	private static void readFromTo(InputStream inputStream, OutputStream outputStream) throws IOException {
		do {
			byte[] b = new byte[1024];
			int read = inputStream.read(b, 0, 1024);
			if (read == -1)
				break;
			outputStream.write(b, 0, read);
		} while (true);
	}

	public static final List<String> knownTextFiles = Arrays.asList(new String[]{"txt", "java", "xml", "mf", "c", "cpp", "c", "h"});

}
