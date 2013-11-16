package org.nlsde.ac.service;

import java.io.File;
import java.util.Map;

import javax.swing.JList;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.io.CountingHttpEntity;
import security.container.io.HttpClientUtil;
import security.container.manage.SecurityDataManage;

public class UploadTask implements Runnable {
	private final static Log logger = LogFactory.getLog(UploadTask.class);

	private String result;
	private String url;
	private String path;
	private Map<String, String> params;
	private SecurityDataManage dataManage;
	private JList<String> cloudDataList;

	private CountingHttpEntity che;

	private volatile long transferred;
	private long amount;

	public UploadTask(String url, String path, Map<String, String> params,
			SecurityDataManage dataManage, JList<String> cloudDataList) {
		this.result = "";
		this.url = url;
		this.params = params;
		this.path = path;
		this.transferred = 0;
		this.amount = new File(path).length();
		this.che = new CountingHttpEntity(transferred);
		this.dataManage = dataManage;
		this.cloudDataList = cloudDataList;
	}

	@Override
	public void run() {
		try {
			result = HttpClientUtil.upload(url, params, path, che);
			while (result.equals("")) {
				Thread.sleep(1000);
			}
			if (result.equals("Success")) {
				// update the cloud items
				dataManage.updateCloudDatas();
				Thread.sleep(1000);
				cloudDataList.setListData(dataManage.getCloudVectorDatas());
				cloudDataList.updateUI();
				JOptionPane.showMessageDialog(null, "文件上传成功");
				logger.info("文件上传成功");
			} else {
				JOptionPane.showMessageDialog(null, "文件上传失败");
				logger.info("文件上传失败");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getResult() {
		return this.result;
	}

	public long getAmount() {
		return amount;
	}

	public long getTransferred() {
		return che.getTransferred();
	}
}
