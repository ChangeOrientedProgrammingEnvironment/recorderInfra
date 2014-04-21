package edu.oregonstate.cope.fileSender;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.PropertyResourceBundle;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;

public class FTPConnectionProperties {

	private static final char[] sk = { 't', 'h', 'i', 's', ' ', 'i', 's', ' ',
			'a', ' ', 's', 't', 'r', 'i', 'n', 'g', '.', ' ', 'r', 'e', 's', 'p',
			'e', 'c', 't', ' ', 'i', 't', '!' };

	private static final byte[] SALT = { (byte) 0xde, (byte) 0x33, (byte) 0x9,
			(byte) 0x12, (byte) 0xda, (byte) 0x34, (byte) 0x8, (byte) 0x42, };

	protected final static String PROPERTIES_PATH = "resources.ftp";
	protected final static String PROPERTIES_FILE_NAME = "resources/ftp.properties";
	
	protected final static int DEFAULT_FTP_PORT = 22;
	
	protected static PropertyResourceBundle ftpProperties;
		
//	private static FTPConnectionProperties ftpConnectionProperties = null;

//	private COPELogger logger;
	
	public FTPConnectionProperties() {
		
	}
//	public static FTPConnectionProperties getInstance() {
//		if(ftpConnectionProperties == null) {
//			ftpConnectionProperties = new FTPConnectionProperties();
//		}
//		return ftpConnectionProperties;
//	}
	public PropertyResourceBundle getProperties() {
		if (ftpProperties == null) {
			ftpProperties = (PropertyResourceBundle) PropertyResourceBundle.getBundle(PROPERTIES_PATH);
			
		}
		return ftpProperties;
	}
	
	public String getHost() {
		return this.getProperties().getString("host");
	}
	
	public int getPort() {
		String strPort = this.getProperties().getString("port");
		if(strPort != null && strPort != "") {
			return DEFAULT_FTP_PORT;
		}
		return Integer.parseInt(strPort);
	}

	public String getUsername() {
		return this.getProperties().getString("username");
	}

	public String getCronConfiguration() {
		return getFrequency();
	}

	public String getFrequency() {
		return this.getProperties().getString("frequency");
	}

	public String getPassword() {
		String encodedPassword = this.getProperties().getString("password");
		try {
			return decrypt(encodedPassword);
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String encrypt(String property)
			throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(sk));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
	}

	public String decrypt(String property)
			throws GeneralSecurityException, IOException {
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(sk));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
	}

	private String base64Encode(byte[] bytes) {
		return new String(Base64.encodeBase64(bytes));
	}

	private byte[] base64Decode(String property) throws IOException {
		return Base64.decodeBase64(property);
	}
	
}
