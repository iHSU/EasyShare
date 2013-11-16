package org.nlsde.ac.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.manage.SecurityDataManage;
import security.container.model.SecurityData;

public class UIManagePanel extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(UIManagePanel.class);

	private UIWindow parent;
	private JTree tree;
	private DefaultMutableTreeNode top; 
	private DefaultMutableTreeNode nodeCipherAble;
	private DefaultMutableTreeNode nodeCipherDisable;
	
	private SecurityDataManage dataManage;

	public UIManagePanel(UIWindow _parent) {
		parent = _parent;
		dataManage = parent.getSimpleContainer().getSecurityDataManage();		
		this.setBorder(BorderFactory.createTitledBorder("文件管理"));
		parent.add(this, BorderLayout.WEST);
	}

	public void initialize() {
		top = new DefaultMutableTreeNode("文件夹"); 
		nodeCipherAble = new DefaultMutableTreeNode("已解密数据");
		nodeCipherDisable = new DefaultMutableTreeNode("无法解密数据");
		top.add(nodeCipherAble);
		top.add(nodeCipherDisable);
		tree = new JTree(top);

		List<SecurityData> files = dataManage.getAbleSecurityListDatas();
		for (SecurityData sd : files) {
			nodeCipherAble.add(new DefaultMutableTreeNode(sd.getBasic().getName()));
		}
		
		files = dataManage.getDisableSecurityListDatas();
		for (SecurityData sd : files) {
			nodeCipherDisable.add(new DefaultMutableTreeNode(sd.getBasic().getName()+".cipher"));
		}		
		
		this.setLayout(new BorderLayout());
		this.parent.getSimpleContainer().getSecurityDataManage().getWatcher().addObserver(this);
		
		JScrollPane jsp = new JScrollPane(tree);
		jsp.setPreferredSize(new Dimension(200, 550));
		this.add(jsp, BorderLayout.WEST);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
		List<SecurityData> files = dataManage.getAbleSecurityListDatas();
		nodeCipherAble.removeAllChildren();
		for (SecurityData sd : files) {
			nodeCipherAble.add(new DefaultMutableTreeNode(sd.getBasic().getName()));
		}
		files = dataManage.getDisableSecurityListDatas();
		for (SecurityData sd : files) {
			nodeCipherDisable.add(new DefaultMutableTreeNode(sd.getBasic().getName()+".cipher"));
		}	
		tree.updateUI();
		logger.debug("文件树更新");
	}

	/**
	 * @return the dataManage
	 */
	public SecurityDataManage getDataManage() {
		return dataManage;
	}

	/**
	 * @param dataManage
	 *            the dataManage to set
	 */
	public void setDataManage(SecurityDataManage dataManage) {
		this.dataManage = dataManage;
	}
}
