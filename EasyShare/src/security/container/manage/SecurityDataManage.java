package security.container.manage;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;

import security.container.io.DataIO;
import security.container.model.CloudData;
import security.container.model.SecurityData;
import security.container.util.FileUtil;

public class SecurityDataManage implements Observer {
	private Map<Long, SecurityData> localDatas;
	private Map<Long, SecurityData> localAbleDatas;
	private Map<Long, SecurityData> localDisableDatas;
	private Map<Long, CloudData> cloudDatas;
	private String targetSrc;
	private FileWatcher watcher;

	public SecurityDataManage(String targetSrc) {
		this.targetSrc = targetSrc;
		this.localAbleDatas = new HashMap<Long, SecurityData>();
		this.localDisableDatas = new HashMap<Long, SecurityData>();
		initialize();
		initializeCloudDatas();
	}

	private void initializeCloudDatas() {
		this.cloudDatas = DataIO.getCloudDatas();
	}

	private void initialize() {
		// watcher initialize
		try {
			watcher = new FileWatcher(targetSrc);
			watcher.execute();
			watcher.addObserver(this);
			
			// get all the cipher file include decryptable or dis-decryptable.
			localDatas = new HashMap<Long, SecurityData>();
			File direction = new File(this.targetSrc);
			FilenameFilter filter = new SecurityFileFilter();
			if(direction.isDirectory()) {
				File[] cipherFiles = direction.listFiles(filter);
				for (File file : cipherFiles) {
					SecurityData sd = (SecurityData) FileUtil.readObjFromFile(file);
					localDatas.put(sd.getId(), sd);
					if (isDecryptAble(sd)) {
						localAbleDatas.put(sd.getId(), sd);
					}
					else {
						localDisableDatas.put(sd.getId(), sd);
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isDecryptAble(SecurityData sd) {
		File tmp = new File(this.targetSrc + "/" + sd.getBasic().getName());
		return tmp.exists();
	}
	
	public boolean isCloudContainsFile(String fileName) {
		Map<Long, CloudData> cTmp = getCloudDatas();
		for (Long key : cTmp.keySet()) {
			if(cTmp.get(key).getName().equals(fileName)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isLocalContainsFile(String fileName) {
		String temp;
		for (Long key : localAbleDatas.keySet()) {
			temp = localAbleDatas.get(key).getBasic().getName() + ".cipher";
			if(fileName.equals(temp)) {
				return true;
			}
		}
		return false;
	}
	
	public SecurityData getSecurityData(int id) {
		return localDatas.get(id);
	}
	
	public SecurityData getSecurityData(String fileName) {
		File tmp = new File(this.targetSrc + "/" + fileName);
		if (tmp.exists()) {
			return (SecurityData) FileUtil.readObjFromFile(tmp);
		}
		return null;
	}

	public List<SecurityData> getAllSecurityListDatas() {
		List<SecurityData> all = new ArrayList<SecurityData>();
		Set<Long> keys = localDatas.keySet();
		for (Long key : keys) {
			all.add(localDatas.get(key));
		}
		return all;
	}
	
	public List<SecurityData> getAbleSecurityListDatas() {
		List<SecurityData> all = new ArrayList<SecurityData>();
		Set<Long> keys = localAbleDatas.keySet();
		for (Long key : keys) {
			all.add(localAbleDatas.get(key));
		}
		return all;
	}
	
	public List<SecurityData> getDisableSecurityListDatas() {
		List<SecurityData> all = new ArrayList<SecurityData>();
		Set<Long> keys = localDisableDatas.keySet();
		for (Long key : keys) {
			all.add(localDisableDatas.get(key));
		}
		return all;
	}
	
	public Vector<String> getAllSecurityVectorDatas() {
		Vector<String> all = new Vector<String>();
		Set<Long> keys = localDatas.keySet();
		for (Long key : keys) {
			all.add(localDatas.get(key).getBasic().getName()+".cipher");
		}
		return all;
	}
	
	public Vector<String> getAbleSecurityVectorDatas() {
		Vector<String> all = new Vector<String>();
		Set<Long> keys = localAbleDatas.keySet();
		for (Long key : keys) {
			all.add(localAbleDatas.get(key).getBasic().getName()+".cipher");
		}
		return all;
	}
	
	public Vector<String> getDisableSecurityVectorDatas() {
		Vector<String> all = new Vector<String>();
		Set<Long> keys = localDisableDatas.keySet();
		for (Long key : keys) {
			all.add(localDisableDatas.get(key).getBasic().getName()+".cipher");
		}
		return all;
	}
	
	public Vector<String> getCloudVectorDatas() {
		Vector<String> all = new Vector<String>();
		Set<Long> keys = cloudDatas.keySet();
		for (Long key : keys) {
			all.add(cloudDatas.get(key).getName());
		}
		return all;
	}
	
	public boolean updateCloudDatas() {
		this.cloudDatas = DataIO.getCloudDatas();
		return true;
	}
	
	public boolean updateLocalDatas() {
		localDatas.clear();
		localAbleDatas.clear();
		localDisableDatas.clear();
		
		File direction = new File(this.targetSrc);
		FilenameFilter filter = new SecurityFileFilter();
		if(direction.isDirectory()) {
			File[] cipherFiles = direction.listFiles(filter);
			for (File file : cipherFiles) {
				SecurityData sd = (SecurityData) FileUtil.readObjFromFile(file);
				localDatas.put(sd.getId(), sd);
				if (isDecryptAble(sd)) {
					localAbleDatas.put(sd.getId(), sd);
				}
				else {
					localDisableDatas.put(sd.getId(), sd);
				}
			}
		}
		return true;
	}
	
	public boolean addSecurityData(SecurityData sData) {
		if(sData != null) {
			localDatas.put(sData.getId(), sData);
			return true;
		}
		return false;
	}
	
	public boolean updateSecurityData(SecurityData sData) {
		if (sData != null) {
			localDatas.remove(sData.getId());
			localDatas.put(sData.getId(), sData);
			return true;
		}
		return false;
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		localDatas.clear();
		localAbleDatas.clear();
		localDisableDatas.clear();
		
		File direction = new File(this.targetSrc);
		FilenameFilter filter = new SecurityFileFilter();
		if(direction.isDirectory()) {
			File[] cipherFiles = direction.listFiles(filter);
			for (File file : cipherFiles) {
				SecurityData sd = (SecurityData) FileUtil.readObjFromFile(file);
				localDatas.put(sd.getId(), sd);
				if (isDecryptAble(sd)) {
					localAbleDatas.put(sd.getId(), sd);
				}
				else {
					localDisableDatas.put(sd.getId(), sd);
				}
			}
		}
	}

	/**
	 * @return the watcher
	 */
	public FileWatcher getWatcher() {
		return watcher;
	}

	/**
	 * @param watcher the watcher to set
	 */
	public void setWatcher(FileWatcher watcher) {
		this.watcher = watcher;
	}

	/**
	 * @return the targetSrc
	 */
	public String getTargetSrc() {
		return targetSrc;
	}

	/**
	 * @param targetSrc the targetSrc to set
	 */
	public void setTargetSrc(String targetSrc) {
		this.targetSrc = targetSrc;
	}

	/**
	 * @return the localDatas
	 */
	public Map<Long, SecurityData> getLocalDatas() {
		return localDatas;
	}

	/**
	 * @param localDatas the localDatas to set
	 */
	public void setLocalDatas(Map<Long, SecurityData> localDatas) {
		this.localDatas = localDatas;
	}

	/**
	 * @return the cloudDatas
	 */
	public Map<Long, CloudData> getCloudDatas() {
		return cloudDatas;
	}

	/**
	 * @param cloudDatas the cloudDatas to set
	 */
	public void setCloudDatas(Map<Long, CloudData> cloudDatas) {
		this.cloudDatas = cloudDatas;
	}
}
