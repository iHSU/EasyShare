package security.container.encrypt.impl;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.crypto.Cipher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.encrypt.Decryptor;

public class AESDecryptor implements Decryptor {
	
	private static Log logger = LogFactory.getLog(AESDecryptor.class);
	
	private String keyPath;
	private String cipherPath;
	private String decPath;
	
	
	public AESDecryptor() {
	}

	@Override
	public boolean decrypt(String cipherPath, String decPath, String keyPath) {
		boolean result = false;
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(cipherPath));
			OutputStream out = new FileOutputStream(decPath);
			long begin = System.currentTimeMillis();
			result =  AESAlgorithms.aes(Cipher.DECRYPT_MODE, in, out, keyPath);
			long time = System.currentTimeMillis() - begin;
			in.close();
			out.close();
			logger.info("AES Decrypt:" + time + "ms");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @return the keyPath
	 */
	public String getKeyPath() {
		return keyPath;
	}

	/**
	 * @param keyPath the keyPath to set
	 */
	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	/**
	 * @return the cipherPath
	 */
	public String getCipherPath() {
		return cipherPath;
	}

	/**
	 * @param cipherPath the cipherPath to set
	 */
	public void setCipherPath(String cipherPath) {
		this.cipherPath = cipherPath;
	}

	/**
	 * @return the decPath
	 */
	public String getDecPath() {
		return decPath;
	}

	/**
	 * @param decPath the decPath to set
	 */
	public void setDecPath(String decPath) {
		this.decPath = decPath;
	}

	
}
