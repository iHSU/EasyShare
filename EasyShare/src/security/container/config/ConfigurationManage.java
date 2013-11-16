package security.container.config;

import java.io.File;
import java.io.IOException;

import security.container.util.FileUtil;

public class ConfigurationManage {
	private PolicyConfiguration policyConfig;

	public ConfigurationManage() {
		initialize();
	}

	private void initialize() {
		File policyConfigFile = new File("config/config-policy.dat");
		try {
			if (!policyConfigFile.exists()) {
				policyConfigFile.createNewFile();
				policyConfig = new PolicyConfiguration();
			} else {
				policyConfig  = (PolicyConfiguration) FileUtil.readObjFromFile(policyConfigFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean setDefaultPolicy(Policy policy) {
		policyConfig.setDefaultPolicy(policy);
		updateSerialize();
		return true;
	}
	
	public Policy getDefaultPolicy() {
		return policyConfig.getDefaultPolicy();
	}
	
	public Policy getPolicy(String name) {
		return policyConfig.getPolicies().get(name);
	}
	
	public void updateSerialize() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				FileUtil.writeObjToFile(policyConfig, "config/config-policy.dat");
			}
		}).start();
	}
	
	public String getPolicyContent(String name) {
		if(exists(name)) {
			return policyConfig.getPolicies().get(name).getContent();
		}
		return null;
	}
	
	public boolean exists(String name) {
		if(policyConfig.getPolicies().containsKey(name)) {
			return true;
		}
		return false;
	}

	public boolean addPolicy(String name, String content) {
		if (name == null || content == null) {
			return false;
		}
		if (name.equals("") || content.equals("")) {
			return false;
		}
		Policy policy = new Policy();
		policy.setName(name);
		policy.setContent(content);
		policyConfig.getPolicies().put(name, policy);
		updateSerialize();
		return true;
	}
	
	public boolean updatePolicy(String name, String content) {
		if (name == null || content == null) {
			return false;
		}
		if (name.equals("") || content.equals("")) {
			return false;
		}
		if (exists(name)) {
			Policy policy = policyConfig.getPolicies().get(name);
			policy.setContent(content);
			updateSerialize();
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean deletePolicy(String name) {
		if (name == null || name.equals("")) {
			return false;
		}
		if (exists(name)) {
			policyConfig.getPolicies().remove(name);
			updateSerialize();
			return true;
		}
		else {
			return false;
		}
	}
	
	public void clear() {
		policyConfig.getPolicies().clear();
		updateSerialize();
	}

	/**
	 * @return the policyConfig
	 */
	public PolicyConfiguration getPolicyConfig() {
		return policyConfig;
	}

	/**
	 * @param policyConfig the policyConfig to set
	 */
	public void setPolicyConfig(PolicyConfiguration policyConfig) {
		this.policyConfig = policyConfig;
	}
}
