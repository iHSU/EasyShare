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

import security.container.encrypt.Encryptor;

public class AESEncryptor implements Encryptor {
	
	private static Log logger = LogFactory.getLog(AESEncryptor.class);
	
	private String keyPath;
	private String plantextPath;
	private String encPath;
	
	public AESEncryptor() {
	}

	@Override
	public boolean encrypt(String plantextPath, String encPath, String keyPath) {
		boolean result = false;
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(plantextPath));
			OutputStream out = new FileOutputStream(encPath);
			long begin = System.currentTimeMillis();
			result =  AESAlgorithms.aes(Cipher.ENCRYPT_MODE, in, out, keyPath);
			long time = System.currentTimeMillis() - begin;
			in.close();
			out.close();
			logger.info("AES Encrypt:" + time + "ms");
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
	 * @return the plantextPath
	 */
	public String getPlantextPath() {
		return plantextPath;
	}

	/**
	 * @param plantextPath the plantextPath to set
	 */
	public void setPlantextPath(String plantextPath) {
		this.plantextPath = plantextPath;
	}

	/**
	 * @return the encPath
	 */
	public String getEncPath() {
		return encPath;
	}

	/**
	 * @param encPath the encPath to set
	 */
	public void setEncPath(String encPath) {
		this.encPath = encPath;
	}
	
	
}
