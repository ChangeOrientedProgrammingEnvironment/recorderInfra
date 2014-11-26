package edu.oregonstate.cope.clientRecorder.util;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class TestFileUtil {
	
	private static final String PLAIN_STRING = "This is a test string";
	private static final String BASE64_STRING = "VGhpcyBpcyBhIHRlc3Qgc3RyaW5n";
	
	private StringBufferInputStream plainBufferInputStream;
	private StringBufferInputStream base64BufferInputStream;
	
	
	@Before
	public void setUp() {
		plainBufferInputStream = new StringBufferInputStream(PLAIN_STRING);
		base64BufferInputStream = new StringBufferInputStream(BASE64_STRING);
	}

	@Test
	public void testEncodePlainString() throws IOException {
		String encodedString = FileUtil.encodeStream("java", plainBufferInputStream);
		assertEquals(PLAIN_STRING, encodedString);
	}
	
	@Test
	public void testEncodeBinaryString() throws IOException {
		String encodedString = FileUtil.encodeStream("notxt", plainBufferInputStream);
		assertEquals(BASE64_STRING, encodedString);
	}
	
	@Test
	public void testDecodeBinaryString() throws IOException {
		InputStream decodedStream = FileUtil.decodeSteam("notxt", base64BufferInputStream);
		String decodedString = getStringFromInputStream(decodedStream);
		assertEquals(decodedString, PLAIN_STRING);
	}
	
	@Test
	public void testDecodePlainString() throws IOException {
		InputStream decodeSteam = FileUtil.decodeSteam("java", plainBufferInputStream);
		String decodedString = getStringFromInputStream(decodeSteam);
		assertEquals(decodedString, PLAIN_STRING);
	}

	private String getStringFromInputStream(InputStream decodedStream)
			throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		FileUtil.readFromTo(decodedStream, outputStream);
		String decodedString = new String(outputStream.toByteArray());
		return decodedString;
	}
}
