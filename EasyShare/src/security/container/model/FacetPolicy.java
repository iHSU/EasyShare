package security.container.model;

import java.io.Serializable;

public class FacetPolicy implements Serializable {
	private static final long serialVersionUID = 1L;
	private byte[] policy;
	private String src;
	/**
	 * @return the policy
	 */
	public byte[] getPolicy() {
		return policy;
	}
	/**
	 * @param policy the policy to set
	 */
	public void setPolicy(byte[] policy) {
		this.policy = policy;
	}
	/**
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}
	/**
	 * @param src the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}
	
	
}
