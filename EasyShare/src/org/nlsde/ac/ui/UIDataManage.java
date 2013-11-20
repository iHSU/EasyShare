package org.nlsde.ac.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nlsde.ac.service.DownloadTask;
import org.nlsde.ac.service.UploadTask;

import security.container.intercepter.ProxyFactory;
import security.container.intercepter.TracksProxyHandler;
import security.container.intercepter.TransferTask;
import security.container.io.HttpClientUtil;
import security.container.manage.SecurityDataManage;
import security.container.model.SecurityData;
import security.container.util.FileUtil;
import security.container.util.SystemUtil;

public class UIDataManage extends JPanel implements ActionListener,
		ListSelectionListener, Observer, TransferTask {
	private static Log logger = LogFactory.getLog(UIDataManage.class);

	private static final long serialVersionUID = 1L;
	private UITabPane parent;
	private String targetSrc;
	private String userID;

	private JList<String> localDataList;
	private JList<String> cloudDataList;
	private JButton btnShare;
	private JButton btnDownload;
	private JButton btnUpdate;
	private JButton btnRefresh;

	private SecurityDataManage dataManage;
	private String selectLocalItem;
	private String selectCloudItem;
	
	private Timer timer;

	public UIDataManage(UITabPane _parent) {
		this.parent = _parent;
		this.dataManage = this.parent.getMainWindow().getSimpleContainer()
				.getSecurityDataManage();
		this.parent.getMainWindow().getSimpleContainer()
				.getSecurityDataManage().getWatcher().addObserver(this);
		this.targetSrc = dataManage.getTargetSrc();
		this.userID = parent.getMainWindow().getSimpleContainer().getId();
		this.selectCloudItem = null;
		this.selectLocalItem = null;

		initializeUI();
		parent.add(this, BorderLayout.CENTER);
	}

	private void initializeUI() {
		this.setLayout(new BorderLayout());

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());

		localDataList = new JList<String>();
		localDataList.setListData(dataManage.getAbleSecurityVectorDatas());
		localDataList.addListSelectionListener(this);
		localDataList.addMouseListener(new ListClickListener());
		localDataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		localDataList.addMouseMotionListener(new LocalDataTipsListener());

		JScrollPane leftJSP = new JScrollPane(localDataList);
		leftJSP.setPreferredSize(new Dimension(340, 400));
		leftJSP.setBorder(BorderFactory.createTitledBorder("本地可解密密文数据"));
		leftPanel.add(leftJSP, BorderLayout.CENTER);

		this.add(leftPanel, BorderLayout.WEST);

		JPanel midPanel = new JPanel();
		midPanel.setLayout(new BorderLayout());

		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(null);
		btnShare = new JButton("共享");
		btnShare.setBounds(10, 180, 80, 25);
		btnShare.addActionListener(this);
		btnShare.setEnabled(false);
		btnPanel.add(btnShare);
		btnDownload = new JButton("下载");
		btnDownload.setBounds(10, 215, 80, 25);
		btnDownload.setEnabled(false);
		btnDownload.addActionListener(this);
		btnPanel.add(btnDownload);
		btnUpdate = new JButton("更新");
		btnUpdate.setBounds(10, 250, 80, 25);
		btnUpdate.addActionListener(this);
		btnUpdate.setEnabled(false);
		btnPanel.add(btnUpdate);
		btnPanel.setPreferredSize(new Dimension(100, 60));

		midPanel.add(btnPanel, BorderLayout.CENTER);
		this.add(midPanel, BorderLayout.CENTER);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		// String[] test = { "test1", "test2", "test3" };
		cloudDataList = new JList<String>();
		cloudDataList.setListData(dataManage.getCloudVectorDatas());
		cloudDataList.addListSelectionListener(this);
//		cloudDataList.addMouseMotionListener(new CloudDataTipsListener());

		JScrollPane rightJSP = new JScrollPane(cloudDataList);
		rightJSP.setPreferredSize(new Dimension(340, 400));
		rightJSP.setBorder(BorderFactory.createTitledBorder("云端密文数据"));
		JPanel refreshPanel = new JPanel();
		refreshPanel.setLayout(new BorderLayout());
		btnRefresh = new JButton("刷新");
		btnRefresh.addActionListener(this);
		refreshPanel.add(btnRefresh, BorderLayout.EAST);
		rightPanel.add(refreshPanel, BorderLayout.NORTH);
		rightPanel.add(rightJSP, BorderLayout.CENTER);

		this.add(rightPanel, BorderLayout.EAST);
	}

	@Override
	public void valueChanged(ListSelectionEvent lse) {
		@SuppressWarnings("unchecked")
		JList<String> list = (JList<String>) lse.getSource();
		if (list == localDataList) {
			btnDownload.setEnabled(false);
			this.selectLocalItem = list.getSelectedValue();
			if (dataManage.isCloudContainsFile(selectLocalItem)) {
				btnShare.setEnabled(false);
				btnUpdate.setEnabled(true);
			}
			else {
				btnShare.setEnabled(true);
				btnUpdate.setEnabled(false);
			}
			logger.debug("Local Data Selected: " + selectLocalItem);
		} else if (list == cloudDataList) {
			btnShare.setEnabled(false);
			btnUpdate.setEnabled(false);			
			this.selectCloudItem = list.getSelectedValue();
			btnDownload.setEnabled(true);
			logger.debug("Cloud Data Selected: " + selectCloudItem);
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();
		if (obj == btnShare) {
			if (this.selectLocalItem == null) {
				JOptionPane.showMessageDialog(this, "请先选择需要共享的文件");
				return;
			}
			String path = this.targetSrc + "/" + this.selectLocalItem;
			TracksProxyHandler handler = new TracksProxyHandler(this, userID, this.targetSrc);
			TransferTask proxy = (TransferTask) ProxyFactory.getProxy(this, handler);
			proxy.startUploadTask(path);
		}
		else if (obj == btnUpdate) {
			
			if (this.selectLocalItem == null) {
				JOptionPane.showMessageDialog(this, "请先选择需要共享的文件");
				return;
			}
			String path = this.targetSrc + "/" + this.selectLocalItem;
			// 判断本地文件是否为最新文件
			SecurityData sd = (SecurityData) FileUtil.readObjFromFile(path);
			String url = "http://security.ihsu.net:8080/SecurityServer/datauser.do?action=fileLastUpdateTime";
			Map<String, String> params = new HashMap<String, String>();
			params.put("fileName", this.selectLocalItem);
			String result = HttpClientUtil.get(url, params, "UTF-8");
			if (result != null && "success".equals(result)) {
				long localTime = sd.getBasic().getLastUpdateTime().getTime();
				long cloudTime = Long.parseLong(result.split("-")[1]);
				if (localTime >= cloudTime) {
					// use the proxy to handle the update method.
					TracksProxyHandler handler = new TracksProxyHandler(this, userID, this.targetSrc);
					TransferTask proxy = (TransferTask) ProxyFactory.getProxy(this, handler);
					proxy.startUpdateTask(path);
				}
				else {
					JOptionPane.showMessageDialog(this, "云端数据版本高于本地数据，请先同步云端数据");
					return;
				}
			}
			else {
				logger.debug("acquire cloud data update time failed.");
				return;
			}
			
		}
		else if (obj == btnDownload) {
			if (dataManage.isLocalContainsFile(selectCloudItem)) {
				int result = JOptionPane.showConfirmDialog(this, "本地已经存在有效的密文，是否继续下载覆盖？");
				if (result == JOptionPane.OK_OPTION) {
					startDownloadTask();
				}
				else {
					return;
				}
			}
			startDownloadTask();
		}
		else if (obj == btnRefresh) {
			// update the cloud file data list.
			dataManage.updateCloudDatas();
			SystemUtil.sleep(1000);
			cloudDataList.setListData(dataManage.getCloudVectorDatas());
			cloudDataList.updateUI();
			logger.info("The Cloud Data List updated.");
		}
	}

	public void startUploadTask(String path) {
		String url = "http://security.ihsu.net:8080/SecurityServer/datauser.do?action=upload";
//		String path = this.targetSrc + "/" + this.selectLocalItem;
		Map<String, String> params = new HashMap<String, String>();
		params.put("owner", parent.getMainWindow().getSimpleContainer()
				.getId());
		SecurityData sd = (SecurityData) FileUtil.readObjFromFile(path);
		params.put("description", sd.getId() + "");

		final UploadTask ut = new UploadTask(url, path, params, dataManage, cloudDataList);
		final Thread targetThread = new Thread(ut);
		targetThread.start();
		final ProgressMonitor dialog = new ProgressMonitor(this, "正在上传",
				"已上传：", 0, (int) ut.getAmount());
		timer = new Timer(300, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("transferred " + ut.getTransferred());
				dialog.setProgress((int) ut.getTransferred());
				double percent = (double)ut.getTransferred()/ut.getAmount()*100;
				dialog.setNote("已上传：" + (double)(Math.round(percent)) + "%");
				if (dialog.isCanceled()) {
					timer.stop();
					targetThread.interrupt();
				}
				// accomplished the upload, stop the time
				if (ut.getAmount() <= ut.getTransferred()) {
					timer.stop();
				}
			}
		});
		timer.start();
	}
	
	public void startUpdateTask(String path) {
		String url = "http://security.ihsu.net:8080/SecurityServer/datauser.do?action=update";
		Map<String, String> params = new HashMap<String, String>();
		
		final UploadTask ut = new UploadTask(url, path, params, dataManage, cloudDataList);
		final Thread targetThread = new Thread(ut);
		targetThread.start();
		final ProgressMonitor dialog = new ProgressMonitor(this, "正在更新",
				"已经更新：", 0, (int) ut.getAmount());
		timer = new Timer(300, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("transferred " + ut.getTransferred());
				dialog.setProgress((int) ut.getTransferred());
				double percent = (double)ut.getTransferred()/ut.getAmount()*100;
				dialog.setNote("已经更新：" + (double)(Math.round(percent)) + "%");
				if (dialog.isCanceled()) {
					timer.stop();
					targetThread.interrupt();
				}
				// accomplished the upload, stop the time
				if (ut.getAmount() <= ut.getTransferred()) {
					timer.stop();
				}
			}
		});
		timer.start();
	}

	public void startDownloadTask() {
		String url = "http://security.ihsu.net:8080/SecurityServer/datauser.do?action=download";
		Map<String, String> params = new HashMap<String, String>();
		params.put("fileName", this.selectCloudItem);
		params.put("user", parent.getMainWindow().getSimpleContainer().getId());
		final String targetPath = this.targetSrc + "/" + this.selectCloudItem;
		final File tempFile;
		try {
			tempFile = File.createTempFile(new Date().getTime() + "download", ".tmp");
			final DownloadTask dt = new DownloadTask(url, tempFile.getAbsolutePath(), params, dataManage, localDataList);
			final Thread targetThread = new Thread(dt);
			targetThread.start();
			final ProgressMonitor dialogDown = new ProgressMonitor(this, "正在下载",
					"已下载：", 0, (int) dt.getAmount());
			logger.debug("amount: " + dt.getAmount());
			timer = new Timer(300, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					logger.debug("transferred " + dt.getTransferred());
					dialogDown.setProgress((int) dt.getTransferred());
					double percent = (double)dt.getTransferred()/dt.getAmount()*100;
					dialogDown.setNote("已下载：" + (double)(Math.round(percent)) + "%");
					if (dialogDown.isCanceled()) {
						timer.stop();
						targetThread.interrupt();
					}
					// accomplished the download, stop the time
					if (dt.getAmount() <= dt.getTransferred()) {
						timer.stop();
						FileUtil.copy(tempFile, targetPath);
						FileUtil.deleteFile(tempFile.getAbsolutePath());
					}
				}
			});
			timer.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	class ListClickListener extends MouseAdapter {
		private JList<String> localDataList;

		// the return value of e.getButton() is 1，2，3
		// 1: mouse left key; 3: mouse right key
		public void mouseClicked(MouseEvent e) {
			localDataList = UIDataManage.this.localDataList;

			int index = localDataList.locationToIndex(e.getPoint());
			localDataList.setSelectedIndex(index);

			// mouse right key.
			if (e.getButton() == 3
					&& localDataList.getSelectedValuesList().size() >= 0) {

				return;
			}

			// mouse left double key.
			if (localDataList.getSelectedIndex() != -1) {
				if (e.getClickCount() == 2) {
					String select = localDataList.getSelectedValue();
					String path = UIDataManage.this.targetSrc + "/" + select;
					path = path.substring(0, path.lastIndexOf("."));
					File file = new File(path);
					if (file.exists()) {
						SystemUtil.openFile(file.getAbsolutePath());
					} else {
						JOptionPane.showMessageDialog(UIDataManage.this,
								"没有发现该文件解密文件");
					}
				}
			}
		}
	}
	
	class LocalDataTipsListener extends MouseAdapter {
		public void mouseMoved(MouseEvent me) {
			// show the tips including the owner
			int index = localDataList.locationToIndex(me.getPoint());
			if (index > -1) {
				Object value = localDataList.getModel().getElementAt(index);
				if ( null != value && !"".equals(value)) {
					String selectName = value.toString();
					SecurityData sd = dataManage.getSecurityData(selectName);
					localDataList.setToolTipText("<html>" 
								+ "文件名: " + selectName 
								+ "<p>拥有者: " + sd.getBasic().getOwner()
								+ "<p>创建时间: " + sd.getBasic().getCreateTime()
								+ "<p>更新时间: " + sd.getBasic().getLastUpdateTime()
								+ "</html>");
				}
				else {
					localDataList.setToolTipText(null);
				}
			}
		}
		
	}
	
	class CloudDataTipsListener extends MouseAdapter {
		public void mouseMoved(MouseEvent me) {
			// show the tips including the owner
			int index = cloudDataList.locationToIndex(me.getPoint());
			if (index > -1) {
				Object value = cloudDataList.getModel().getElementAt(index);
				if ( null != value && !"".equals(value)) {
					String selectName = value.toString();
					SecurityData sd = dataManage.getSecurityData(selectName);
					cloudDataList.setToolTipText("<html>" 
							+ "文件名: " + selectName 
							+ "<p>拥有者: " + sd.getBasic().getOwner()
							+ "<p>创建时间: " + sd.getBasic().getCreateTime()
							+ "<p>更新时间: " + sd.getBasic().getLastUpdateTime()
							+ "</html>");
				}
				else {
					cloudDataList.setToolTipText(null);
				}
			}
		}
		
	}
	/**
	 * when file changed on the target folder, trigger this function.
	 */
	@Override
	public void update(Observable o, Object arg) {
		localDataList.setListData(dataManage.getAbleSecurityVectorDatas());
		localDataList.updateUI();
	}
}
