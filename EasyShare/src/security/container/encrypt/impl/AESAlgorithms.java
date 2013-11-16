package security.container.encrypt.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AESAlgorithms {
	private static Log logger = LogFactory.getLog(AESAlgorithms.class);

	public static boolean aes(int mode, InputStream in, OutputStream out,
			String keyPath) {
		try {
			Key key = generateKeyFromFile(keyPath);

			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(mode, key);

			int blockSize = cipher.getBlockSize();
			int outputSize = cipher.getOutputSize(blockSize);
			byte[] inBytes = new byte[blockSize];
			byte[] outBytes = new byte[outputSize];

			int inLength = 0;
			boolean more = true;
//			long all = in.available();
			int sum = 0;
			while (more) {
				inLength = in.read(inBytes);
				if (inLength == blockSize) {
					int outLength = cipher.update(inBytes, 0, blockSize,
							outBytes);
					out.write(outBytes, 0, outLength);
					sum += outLength;
				} else {
					more = false;
				}
			}
			if (inLength > 0) {
				outBytes = cipher.doFinal(inBytes, 0, inLength);
			} else {
				outBytes = cipher.doFinal();
			}
			sum += outBytes.length;
			logger.info("生成密文大小: " + sum);
			out.write(outBytes);
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ShortBufferException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static SecretKeySpec generateKeyFromFile(String path) {
		SecretKeySpec keySpec;
		try {
			@SuppressWarnings("resource")
			InputStream in = new FileInputStream(path);
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			int ch;
			while ((ch = in.read()) != -1) {
				sha.update((byte) ch);
			}
			byte[] hash = sha.digest();

			// SecretKeyFactory keyFactory =
			// SecretKeyFactory.getInstance("AES");
			keySpec = new SecretKeySpec(Arrays.copyOf(hash, 16), "AES");
			// return keyFactory.generateSecret(keySpec);
			return keySpec;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
