package security.container.encrypt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.encrypt.Encryptor;
import security.container.util.SystemUtil;

public class CPABEEncryptor implements Encryptor {

	private static Log logger = LogFactory.getLog(CPABEEncryptor.class);

	private static final String EXECUTE_PATH = "/bin/cpabe-enc.bat";

	private String workspacePath;
	private String cipherPath;
	private String plantextPath;
	private String policyPath;

	public CPABEEncryptor(String wordDir) {
		this.workspacePath = wordDir;
	}
	

	@Override
	public boolean encrypt(String plantextPath, String encPath,
			String policyPath) {
		setPolicyPath(policyPath);
		setPlantextPath(plantextPath);
		setCipherPath(encPath);
		return encrypt();
	}
	
	
	private boolean encrypt() {
		String command = workspacePath + EXECUTE_PATH + " " + workspacePath + " " + cipherPath + " "
				+ plantextPath + " " + policyPath;
		logger.info("Execute ABE-ENC Command: " + command);
		return SystemUtil.execute(command);
	}

	/**
	 * @return the workspacePath
	 */
	public String getWorkspacePath() {
		return workspacePath;
	}

	/**
	 * @param workspacePath
	 *            the workspacePath to set
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
	 * @param cipherPath
	 *            the cipherPath to set
	 */
	public void setCipherPath(String cipherPath) {
		this.cipherPath = cipherPath;
	}

	/**
	 * @return the plantextPath
	 */
	public String getPlantextPath() {
		return plantextPath;
	}

	/**
	 * @param plantextPath
	 *            the plantextPath to set
	 */
	public void setPlantextPath(String plantextPath) {
		this.plantextPath = plantextPath;
	}

	/**
	 * @return the policyPath
	 */
	public String getPolicyPath() {
		return policyPath;
	}

	/**
	 * @param policyPath the policyPath to set
	 */
	public void setPolicyPath(String policyPath) {
		this.policyPath = policyPath;
	}

}
