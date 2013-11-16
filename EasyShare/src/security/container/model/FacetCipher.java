package security.container.model;

import java.io.Serializable;

public class FacetCipher implements Serializable {
	private static final long serialVersionUID = 1L;
	private byte[] dataCipher;
	private byte[] signCipher;
	private byte[] keyCipher;
	private byte[] trackCipher;

	/**
	 * @return the keyCipher
	 */
	public byte[] getKeyCipher() {
		return keyCipher;
	}

	/**
	 * @param keyCipher
	 *            the keyCipher to set
	 */
	public void setKeyCipher(byte[] keyCipher) {
		this.keyCipher = keyCipher;
	}

	/**
	 * @return the signCipher
	 */
	public byte[] getSignCipher() {
		return signCipher;
	}

	/**
	 * @param signCipher the signCipher to set
	 */
	public void setSignCipher(byte[] signCipher) {
		this.signCipher = signCipher;
	}

	/**
	 * @return the trackCipher
	 */
	public byte[] getTrackCipher() {
		return trackCipher;
	}

	/**
	 * @param trackCipher the trackCipher to set
	 */
	public void setTrackCipher(byte[] trackCipher) {
		this.trackCipher = trackCipher;
	}

	/**
	 * @return the dataCipher
	 */
	public byte[] getDataCipher() {
		return dataCipher;
	}

	/**
	 * @param dataCipher the dataCipher to set
	 */
	public void setDataCipher(byte[] dataCipher) {
		this.dataCipher = dataCipher;
	}

}
