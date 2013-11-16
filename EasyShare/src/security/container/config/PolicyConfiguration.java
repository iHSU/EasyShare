package security.container.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PolicyConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, Policy> policies;
	private Policy defaultPolicy;
	
	public PolicyConfiguration() {
		defaultPolicy = null;
		policies = new HashMap<String, Policy>();
	}
	
	/**
	 * @return the policies
	 */
	public Map<String, Policy> getPolicies() {
		return policies;
	}

	/**
	 * @param policies
	 *            the policies to set
	 */
	public void setPolicies(Map<String, Policy> policies) {
		this.policies = policies;
	}

	/**
	 * @return the defaultPolicy
	 */
	public Policy getDefaultPolicy() {
		return defaultPolicy;
	}

	/**
	 * @param defaultPolicy
	 *            the defaultPolicy to set
	 */
	public void setDefaultPolicy(Policy defaultPolicy) {
		this.defaultPolicy = defaultPolicy;
	}
}
