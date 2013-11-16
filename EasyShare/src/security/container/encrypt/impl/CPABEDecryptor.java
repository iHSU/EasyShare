package security.container.encrypt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.encrypt.Decryptor;
import security.container.util.SystemUtil;

public class CPABEDecryptor implements Decryptor {
	
	private static Log logger = LogFactory.getLog(CPABEDecryptor.class);
	private static final String EXECUTE_PATH = "/bin/cpabe-dec.exe";
	
	private String workspacePath;
	private String cipherPath;
	private String plaintextPath;
	private String keyPath;
	
	public CPABEDecryptor(String workDir) {
		this.workspacePath = workDir;
	}
	
	/**
	 * keyPath the ibe key path to find the id of the user.
	 */
	@Override
	public boolean decrypt(String cipherPath, String decPath, String keyPath) {
		this.cipherPath = cipherPath;
		this.keyPath = keyPath;
		this.plaintextPath = decPath;
		
		return decrypt();
	}

	private boolean decrypt() {
		String command = workspacePath + EXECUTE_PATH + " " + workspacePath + " "
				+ cipherPath + " " + plaintextPath + " " 
				+ keyPath;
		logger.info("Execute ABE-DEC Command: " + command);
		return SystemUtil.execute(command);
	}
	

	/**
	 * @return the plaintextPath
	 */
	public String getPlaintextPath() {
		return plaintextPath;
	}

	/**
	 * @param plaintextPath the plaintextPath to set
	 */
	public void setPlaintextPath(String plaintextPath) {
		this.plaintextPath = plaintextPath;
	}
	
	/**
	 * @return the workspacePath
	 */
	public String getWorkspacePath() {
		return workspacePath;
	}

	/**
	 * @param workspacePath the workspacePath to set
	 */
	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
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
}
