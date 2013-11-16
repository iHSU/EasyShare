package org.nlsde.ac.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.config.ConfigurationManage;

public class UIConfiguration extends JPanel implements ActionListener,
		ListSelectionListener {
	private static Log logger = LogFactory.getLog(UIConfiguration.class);

	private static final long serialVersionUID = 1L;
	private UITabPane parent;
	private JList<String> fileList;
	private Vector<String> files;
	private JButton btnPolicyAdd;
	private JButton btnPolicyEdit;
	private JButton btnPolicySet;
	private ConfigurationManage configManage;
	private String selectItem;

	private JTextField defaultPolicy;

	public UIConfiguration(UITabPane _parent) {
		this.parent = _parent;
		this.configManage = parent.getMainWindow().getSimpleContainer()
				.getConfigurationManage();
		selectItem = null;
		this.setLayout(new BorderLayout());

		JPanel westPanel = new JPanel(new BorderLayout());
		
		fileList = new JList<String>();
		files = new Vector<String>();
		Set<String> policyNames = this.configManage.getPolicyConfig()
				.getPolicies().keySet();
		for (String name : policyNames) {
			files.add(name);
		}
		fileList.setListData(files);
		fileList.addListSelectionListener(this);
		fileList.addMouseListener(new MouseListListener());
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane policyPanel = new JScrollPane(fileList);
		policyPanel.setPreferredSize(new Dimension(170, 400));
		policyPanel.setBorder(BorderFactory.createTitledBorder("策略文件组"));
		westPanel.add(policyPanel, BorderLayout.CENTER);

		JPanel btnPanel = new JPanel();
		btnPolicyAdd = new JButton("添加");
		btnPolicyAdd.addActionListener(this);
		btnPanel.add(btnPolicyAdd);
		btnPolicyEdit = new JButton("编辑");
		btnPolicyEdit.addActionListener(this);
		btnPanel.add(btnPolicyEdit);
		btnPolicySet = new JButton("-->");
		btnPolicySet.addActionListener(this);
		btnPanel.add(btnPolicySet);
		westPanel.add(btnPanel, BorderLayout.SOUTH);
		
		this.add(westPanel, BorderLayout.WEST);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		
		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.setBorder(BorderFactory.createTitledBorder("默认策略文件"));
		defaultPolicy = new JTextField();
		defaultPolicy.setEditable(false);
		defaultPolicy.setColumns(50);
		defaultPolicy.setBorder(BorderFactory.createEmptyBorder());
		defaultPolicy.setText(configManage.getDefaultPolicy() == null ? "没有设定默认策略"
				: configManage.getDefaultPolicy().getName());
		statusPanel.add(defaultPolicy, BorderLayout.CENTER);
		
		centerPanel.add(statusPanel, BorderLayout.NORTH);
		
		this.add(centerPanel, BorderLayout.CENTER);

		parent.add(this, BorderLayout.CENTER);
	}

	public void updatePolicyList() {
		Set<String> policyNames = configManage.getPolicyConfig().getPolicies()
				.keySet();
		files.removeAllElements();
		for (String name : policyNames) {
			files.add(name);
		}
		fileList.setListData(files);
		fileList.validate();
		logger.debug("The Policy List Update.");
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
		if (obj == btnPolicyAdd) {
			UIPolicyDialog policyDialog = new UIPolicyDialog(this);
			policyDialog.setVisible(true);
		} 
		else if (obj == btnPolicyEdit) {
			UIPolicyDialog policyDialog = new UIPolicyDialog(this);
			policyDialog.setEditStatus(this.selectItem,
					configManage.getPolicyContent(selectItem));
			policyDialog.setVisible(true);
		} 
		else if (obj == btnPolicySet) {
			if (this.selectItem == null || "".equals(this.selectItem)) {
				return;
			}
			defaultPolicy.setText(this.selectItem);
			configManage.setDefaultPolicy(configManage.getPolicy(selectItem.trim()));
		}
		else {

		}
	}

	@Override
	public void valueChanged(ListSelectionEvent evt) {
		// When the user release the mouse button and completes the selection,
		// getValueIsAdjusting() becomes false
		if (!evt.getValueIsAdjusting()) {
			@SuppressWarnings("unchecked")
			JList<String> list = (JList<String>) evt.getSource();
			// Get all selected items
			this.selectItem = list.getSelectedValue();
			logger.debug("Item " + selectItem + " have been selected.");
		}
	}

	private class MouseListListener extends MouseAdapter {
		private JList<String> jList;

		// the return value of e.getButton() is 1，2，3
		// 1: mouse left key; 3: mouse right key
		public void mouseClicked(MouseEvent e) {
			jList = UIConfiguration.this.fileList;
			int index = jList.locationToIndex(e.getPoint());
			jList.setSelectedIndex(index);
			if (e.getButton() == 3 && jList.getSelectedValuesList().size() >= 0) {
				JPopupMenu popupMenu = new JPopupMenu();
				ImageIcon icon = new ImageIcon("images/delete_16x16.png");
				JMenuItem deleteItem = new JMenuItem("删除", icon);
				deleteItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						UIConfiguration.this.configManage
								.deletePolicy(MouseListListener.this.jList
										.getSelectedValue());
						UIConfiguration.this.updatePolicyList();
					}
				});
				popupMenu.add(deleteItem);
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
				return;
			}
			if (jList.getSelectedIndex() != -1) {
				if (e.getClickCount() == 2) {
					UIPolicyDialog policyDialog = new UIPolicyDialog(UIConfiguration.this);
					policyDialog.setDisplayStatus(UIConfiguration.this.selectItem,
							UIConfiguration.this.configManage.getPolicyContent(selectItem));
					policyDialog.setVisible(true);
				}
			}
		}
	}

	/**
	 * @return the fileList
	 */
	public JList<String> getFileList() {
		return fileList;
	}

	/**
	 * @param fileList
	 *            the fileList to set
	 */
	public void setFileList(JList<String> fileList) {
		this.fileList = fileList;
	}

	/**
	 * @return the configManage
	 */
	public ConfigurationManage getConfigManage() {
		return configManage;
	}

	/**
	 * @param configManage
	 *            the configManage to set
	 */
	public void setConfigManage(ConfigurationManage configManage) {
		this.configManage = configManage;
	}
}
