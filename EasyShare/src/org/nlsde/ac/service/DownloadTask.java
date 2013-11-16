package org.nlsde.ac.service;

import java.util.Map;

import javax.swing.JList;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.io.HttpClientUtil;
import security.container.io.Transfer;
import security.container.manage.SecurityDataManage;

public class DownloadTask implements Runnable {
	private final static Log logger = LogFactory.getLog(DownloadTask.class);

	private boolean result;
	private String url;
	private String path;
	private Map<String, String> params;
	private SecurityDataManage dataManage;
	private JList<String> localDataList;
	
	private Transfer transfer;
	
	public DownloadTask(String url, String path, Map<String, String> params,
			SecurityDataManage dataManage, JList<String> localDataList) {
		this.result = false;
		this.url = url;
		this.params = params;
		this.path = path;
		this.transfer = new Transfer();
		String file_length_url = "http://security.ihsu.net:8080/SecurityServer/datauser.do?action=fileLength";
		this.transfer.setAmount(HttpClientUtil.getFileLength(file_length_url, params, "UTF-8"));
		this.transfer.setTransferred(0);
		this.dataManage = dataManage;
		this.localDataList = localDataList;
	}

	@Override
	public void run() {
		try {
			result = HttpClientUtil.download(url, params, "UTF-8", transfer, path);
			while (!result) {
				Thread.sleep(1000);
			}
			if (result) {
				// update the cloud items
				dataManage.updateLocalDatas();
				Thread.sleep(1000);
				localDataList.setListData(dataManage.getAbleSecurityVectorDatas());
				localDataList.updateUI();
				JOptionPane.showMessageDialog(null, "文件下载成功");
				logger.info("文件下载成功");
			} else {
				JOptionPane.showMessageDialog(null, "文件下载失败");
				logger.info("文件下载失败");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public long getAmount() {
		return transfer.getAmount();
	}

	public long getTransferred() {
		return transfer.getTransferred();
	}
}
