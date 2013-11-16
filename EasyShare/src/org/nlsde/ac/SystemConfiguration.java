package org.nlsde.ac;

import org.nlsde.ac.util.ClientFileUtil;

public class SystemConfiguration {
	private String watcherDir;
	
	public SystemConfiguration() {
		setWatcherDir(ClientFileUtil.DEFAULT_DIR);
	}

	public String getWatcherDir() {
		return watcherDir;
	}

	public void setWatcherDir(String watcherDir) {
		this.watcherDir = watcherDir;
	}
}
