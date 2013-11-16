package security.container.encrypt.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.encrypt.Decryptor;
import security.container.encrypt.Encryptor;
import security.container.io.HttpClientUtil;
import security.container.util.Constants;

public class SecurityFactory {
	private static SecurityFactory factory = null;
	private final static Log logger = LogFactory.getLog(SecurityFactory.class);
	public static final String ABE_DECRYPT = "abe-dec";
	public static final String ABE_ENCRYPT = "abe-enc";
	public static final String AES_DECRYPT = "aes-dec";
	public static final String AES_ENCRYPT = "aes-enc";
	public static final String IBE = "ibe";

	private Decryptor abeDecryptor;
	private Encryptor abeEncryptor;
	private Decryptor aesDecryptor;
	private Encryptor aesEncryptor;
	private IBE ibe;
	private String workSpace;
	private String id;

	private SecurityFactory(String workSpace, String id) {
		this.workSpace = workSpace;
		this.aesDecryptor = new AESDecryptor();
		this.aesEncryptor = new AESEncryptor();
		this.abeDecryptor = new CPABEDecryptor(workSpace);
		this.abeEncryptor = new CPABEEncryptor(workSpace);
		this.ibe = new IBE(workSpace);
		this.id = id;
		//initialize();
	}

	public static SecurityFactory getInstance(String workSpace, String id) {
		if (factory == null) {
			factory = new SecurityFactory(workSpace, id);
		}
		return factory;
	}

	public boolean initialize() {
		String url_param = "http://security.ihsu.net:8080/SecurityServer/key.do?action=params&p=param";
		String url_abe_pub = "http://security.ihsu.net:8080/SecurityServer/key.do?action=params&p=abe-pub";
		String url_ibe_pub = "http://security.ihsu.net:8080/SecurityServer/key.do?action=params&p=ibe-pub";
		String url_attr_hash = "http://security.ihsu.net:8080/SecurityServer/key.do?action=params&p=attr-hash";
		String url_ibe_private = "http://security.ihsu.net:8080/SecurityServer/key.do?action=key";
		
		if (HttpClientUtil.get(url_param, new HashMap<String, String>(),
				"UTF-8", Constants.PARAM_A1)) {
			logger.info("Get Params Ok!");
		}
		if (HttpClientUtil.get(url_abe_pub, new HashMap<String, String>(),
				"UTF-8", Constants.KEY_ABE_PUBLIC )) {
			logger.info("Get ABE-PUB Ok!");
		}
		if (HttpClientUtil.get(url_ibe_pub, new HashMap<String, String>(),
				"UTF-8", Constants.KEY_IBE_PUBLIC_CA)) {
			logger.info("Get IBE-PUB Ok!");
		}
		if (HttpClientUtil.get(url_attr_hash, new HashMap<String, String>(),
				"UTF-8", Constants.PARAM_ATTR_HASH)) {
			logger.info("Get PARAM-ATTR-HASH Ok!");
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", this.id);
		params.put("type", "I");
		if (HttpClientUtil.get(url_ibe_private, params, "UTF-8", Constants.KEY_IBE_PRIVATE)) {
			logger.info("Get IBE-Private-Key Ok!");
		}
		return true;
	}

	public Object get(String type) {
		if (type == null) {
			return null;
		}
		if (type.equals(ABE_DECRYPT)) {
			return this.abeDecryptor;
		} else if (type.equals(ABE_ENCRYPT)) {
			return this.abeEncryptor;
		} else if (type.equals(AES_DECRYPT)) {
			return this.aesDecryptor;
		} else if (type.equals(AES_ENCRYPT)) {
			return this.aesEncryptor;
		} else if (type.equals(IBE)) {
			return this.ibe;
		} else {
			return null;
		}
	}

	/**
	 * @return the abeDecryptor
	 */
	public Decryptor getAbeDecryptor() {
		return abeDecryptor;
	}

	/**
	 * @param abeDecryptor
	 *            the abeDecryptor to set
	 */
	public void setAbeDecryptor(Decryptor abeDecryptor) {
		this.abeDecryptor = abeDecryptor;
	}

	/**
	 * @return the abeEncryptor
	 */
	public Encryptor getAbeEncryptor() {
		return abeEncryptor;
	}

	/**
	 * @param abeEncryptor
	 *            the abeEncryptor to set
	 */
	public void setAbeEncryptor(Encryptor abeEncryptor) {
		this.abeEncryptor = abeEncryptor;
	}

	/**
	 * @return the aesDecryptor
	 */
	public Decryptor getAesDecryptor() {
		return aesDecryptor;
	}

	/**
	 * @param aesDecryptor
	 *            the aesDecryptor to set
	 */
	public void setAesDecryptor(Decryptor aesDecryptor) {
		this.aesDecryptor = aesDecryptor;
	}

	/**
	 * @return the aesEncryptor
	 */
	public Encryptor getAesEncryptor() {
		return aesEncryptor;
	}

	/**
	 * @param aesEncryptor
	 *            the aesEncryptor to set
	 */
	public void setAesEncryptor(Encryptor aesEncryptor) {
		this.aesEncryptor = aesEncryptor;
	}

	/**
	 * @return the workSpace
	 */
	public String getWorkSpace() {
		return workSpace;
	}

	/**
	 * @param workSpace the workSpace to set
	 */
	public void setWorkSpace(String workSpace) {
		this.workSpace = workSpace;
	}
}
