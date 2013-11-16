package org.nlsde.ac.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nlsde.ac.util.ClientFileUtil;

import security.container.encrypt.Encryptor;
import security.container.encrypt.impl.IBE;
import security.container.encrypt.impl.SecurityFactory;
import security.container.model.FacetCipher;
import security.container.util.Constants;
import security.container.util.FileUtil;
import security.container.util.PolicyUtil;
import security.container.util.SystemUtil;

public class UIEncrypt extends JPanel implements ActionListener {
	private static Log logger = LogFactory.getLog(UIEncrypt.class);

	private static final long serialVersionUID = 1L;
	private UITabPane parent;
	private JLabel labelOrigin;
	private JLabel labelPolicy;
	private JLabel labelEncrypt;
	private JTextField textOrigin;
	private JTextField textPolicy;
	private JTextField textResult;
	private JButton btnOrigin;
	private JButton btnPolicy;
	private JButton btnEncrypt;
	private JButton btnResult;

	private SecurityFactory securityFactory;

	public UIEncrypt(UITabPane _parent) {
		this.parent = _parent;
		this.securityFactory = parent.getMainWindow().getSimpleContainer().getSecurityFactory();

		this.setLayout(null);

		labelOrigin = new JLabel("源文件");
		labelOrigin.setBounds(50, 20, 50, 30);

		labelPolicy = new JLabel("访问策略");
		labelPolicy.setBounds(50, 60, 50, 30);

		labelEncrypt = new JLabel("加密结果");
		labelEncrypt.setBounds(50, 100, 50, 30);

		textOrigin = new JTextField();
		textOrigin.setBounds(100, 20, 200, 30);
		textOrigin.setEditable(false);

		textPolicy = new JTextField();
		textPolicy.setBounds(100, 60, 200, 30);
		textPolicy.setEditable(false);

		textResult = new JTextField();
		textResult.setBounds(100, 100, 200, 30);
		textResult.setEditable(false);

		btnOrigin = new JButton("选择");
		btnOrigin.setBounds(320, 20, 50, 30);
		btnOrigin.addActionListener(this);

		btnPolicy = new JButton("选择");
		btnPolicy.setBounds(320, 60, 50, 30);
		btnPolicy.addActionListener(this);

		btnResult = new JButton("选择");
		btnResult.setBounds(320, 100, 50, 30);
		btnResult.addActionListener(this);

		btnEncrypt = new JButton("加密");
		btnEncrypt.setBounds(50, 140, 300, 30);
		btnEncrypt.addActionListener(this);

		this.add(labelOrigin);
		this.add(textOrigin);
		this.add(btnOrigin);

		this.add(labelPolicy);
		this.add(textPolicy);
		this.add(btnPolicy);

		this.add(labelEncrypt);
		this.add(textResult);
		this.add(btnResult);
		this.add(btnEncrypt);

		parent.add(this, BorderLayout.CENTER);
	}

	/**
	 * @return the parent
	 */
	public UITabPane getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(UITabPane parent) {
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();
		if (obj == btnOrigin) {
			JFileChooser jfc = new JFileChooser("D:/develop-env/test/");
			jfc.setDialogTitle("选择待加密文件");
			int result = jfc.showOpenDialog(this);
			jfc.setVisible(true);
			File file = null;
			if (result == JFileChooser.APPROVE_OPTION) {
				file = jfc.getSelectedFile();
				if (file.exists()) {
					textOrigin.setText(file.getAbsolutePath());
				} else {
					logger.info("选择文件不存在");
				}
			} else if (result == JFileChooser.CANCEL_OPTION) {
				logger.info("File Chooser Cancel");
			} else if (result == JFileChooser.ERROR_OPTION) {
				logger.info("File Chooser Error");
			} else {
				logger.info("File Chooser don't get the choice");
			}
		} else if (obj == btnPolicy) {
			JFileChooser jfc = new JFileChooser("D:/develop-env/test/");
			jfc.setDialogTitle("选择策略文件");
			int result = jfc.showOpenDialog(this);
			jfc.setVisible(true);
			File file = null;
			if (result == JFileChooser.APPROVE_OPTION) {
				file = jfc.getSelectedFile();
				if (file.exists()) {
					textPolicy.setText(file.getAbsolutePath());
				} else {
					logger.info("选择文件不存在");
				}
			} else if (result == JFileChooser.CANCEL_OPTION) {
				logger.info("File Chooser Cancel");
			} else if (result == JFileChooser.ERROR_OPTION) {
				logger.info("File Chooser Error");
			} else {
				logger.info("File Chooser don't get the choice");
			}
		} else if (obj == btnResult) {
			JFileChooser jfc = new JFileChooser("D:/develop-env/test/");
			jfc.setDialogTitle("选择加密结果文件夹");
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = jfc.showOpenDialog(this);
			jfc.setVisible(true);
			File file = null;
			if (result == JFileChooser.APPROVE_OPTION) {
				file = jfc.getSelectedFile();
				if (file.exists()) {
					textResult.setText(file.getAbsolutePath());
				} else {
					logger.info("选择文件不存在");
				}
			} else if (result == JFileChooser.CANCEL_OPTION) {
				logger.info("File Chooser Cancel");
			} else if (result == JFileChooser.ERROR_OPTION) {
				logger.info("File Chooser Error");
			} else {
				logger.info("File Chooser don't get the choice");
			}
		} else if (obj == btnEncrypt) {
			if (textOrigin != null && textPolicy != null && textResult != null
					&& !"".equals(textOrigin.getText().trim())
					&& !"".equals(textPolicy.getText().trim())
					&& !"".equals(textResult.getText().trim())) {

				PolicyUtil policyUtil = new PolicyUtil(textPolicy.getText());
				String policyDataFilePath = Constants.TEMP_PATH
						+ "/cs.policy.dat";
				policyUtil.toFile(policyDataFilePath);

				FacetCipher fc = new FacetCipher();
				Encryptor abeEncrypt = (Encryptor) securityFactory
						.get(SecurityFactory.ABE_ENCRYPT);
				boolean res = abeEncrypt.encrypt(Constants.TEMP_KEY_AES,
						Constants.TEMP_KEY_AES_CIPHER, policyDataFilePath);
				fc.setKeyCipher(FileUtil.writeToBytes(Constants.TEMP_KEY_AES_CIPHER));

				if (true) {
					logger.info("CP-ABE Success");
					
					Encryptor aesEncrypt = (Encryptor) securityFactory
							.get(SecurityFactory.AES_ENCRYPT);
					String cipherPath = ClientFileUtil.createCipherFileName(textResult.getText().trim(),
									textOrigin.getText().trim());
					res = aesEncrypt.encrypt(textOrigin.getText(), cipherPath, Constants.TEMP_KEY_AES);
					fc.setDataCipher(FileUtil.writeToBytes(cipherPath));
					
					if (res) {
						logger.info("AES Success");
						
						IBE ibe = (IBE) securityFactory.get(SecurityFactory.IBE);
						final String signFile = SystemUtil.getTempFileName(
								Constants.WORK_SPACE, ".sign");
						ibe.sign(Constants.KEY_IBE_PRIVATE, cipherPath,	signFile);
						
						logger.info("IBE Sign Success");
					}
				}
				
				
			} else {
				logger.error("加密参数缺少");
			}
		} else {

		}
	}
}
