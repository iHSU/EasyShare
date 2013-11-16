package security.container.encrypt.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.util.Constants;
import security.container.util.SystemUtil;

public class IBE {
	private static Log logger = LogFactory.getLog(IBE.class);

	private static final String ENCRYPT_PATH = "/bin/ibe-encrypt.exe";
	private static final String DECRYPT_PATH = "/bin/ibe-decrypt.exe";
	private static final String SIGN_PATH = "/bin/ibe-sign.exe";
	private static final String VERIFY_PATH = "/bin/ibe-verify.exe";

	final static int BLOCK_SIZE = 260;

	private String paramFile;
	private String publicFile;
	private String workDir;

	public IBE(String workDir) {
		this.workDir = workDir;
		this.paramFile = Constants.PARAM_A1;
		this.publicFile = Constants.KEY_IBE_PUBLIC_CA;
	}
	
	public IBE(String workDir, String paramFile, String publicFile) {
		this.paramFile = paramFile;
		this.publicFile = publicFile;
		this.workDir = workDir;
	}

	public boolean encrypt(String ca, String id, String textPath,
			String cipherPath) {
		String ID = ca + "|" + id;
		String command = workDir + ENCRYPT_PATH + " " + ID + " " + textPath
				+ " " + cipherPath + " " + paramFile + " " + publicFile;

		logger.info("Execute IBE-ENCRYPT Command:" + command);
		return SystemUtil.execute(command);

	}

	public boolean decrypt(String keyPath, String textPath, String cipherPath) {
		String command = workDir + DECRYPT_PATH + " " + keyPath + " " + textPath + " "
				+ cipherPath + " " + paramFile + " " + publicFile;

		logger.info("Execute IBE-DECRYPT Command:" + command);
		return SystemUtil.execute(command);

	}

	public boolean sign(String keyFile, String textFile, String signFile) {
		try {
			String hashFile = computeHash(textFile);
			String command = workDir + SIGN_PATH + " " + keyFile + " " + hashFile + " "
					+ signFile + " " + paramFile + " " + publicFile;
			logger.info("Execute IBE-SIGN Command:" + command);
			return SystemUtil.execute(command);
		} catch (IOException e) {
			return false;
		} catch (NoSuchAlgorithmException e) {
			return false;
		}
	}

	public String verify(String textFile, String signFile) {
		String author = "";
		try {
			String hashFile = computeHash(textFile);
			String command = workDir + VERIFY_PATH + " " + hashFile + " " + signFile
					+ " " + paramFile + " " + publicFile;
			logger.info("Execute IBE-VERIFY Command:" + command);
			boolean flag = SystemUtil.execute(command);
			if (flag) { // verified
				author = getAuthor(signFile);
			}
		} catch (IOException e) {
			author = "";
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			author = "";
			e.printStackTrace();
		}
		return author;
	}

	public String getAuthor(String signFile) {
		File file = new File(signFile);
		int len = (int) file.length() - 4 - 2 * BLOCK_SIZE;
		
		try {
			InputStream in = new FileInputStream(file);
			in.skip(4L);
			byte[] bytes = new byte[len];
			in.read(bytes);
			in.close();
			return new String(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String computeHash(String textFile) throws IOException,
			NoSuchAlgorithmException {
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		InputStream in = new FileInputStream(textFile);
		int ch;
		while ((ch = in.read()) != -1) {
			sha.update((byte) ch);
		}
		byte[] hash = sha.digest();

		// store it in temp file
		File file = File.createTempFile("sha", "hash");
		OutputStream out = new FileOutputStream(file);
		out.write(hash);
		out.close();
		in.close();
		return file.getAbsolutePath();
	}

	public static String authorOfKey(String keyFile) {
		String id = "";
		try {
			File file = new File(keyFile);
			long size = file.length();
			long len = size - BLOCK_SIZE - 4;
			InputStream in = new FileInputStream(file);
			in.skip(BLOCK_SIZE + 4);
			byte[] buf = new byte[(int) len];
			in.read(buf);
			String content = new String(buf);
			int pos = content.indexOf("|");
			id = content.substring(pos + 1);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return id;
	}
}
