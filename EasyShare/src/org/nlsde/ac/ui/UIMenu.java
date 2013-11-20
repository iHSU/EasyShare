package org.nlsde.ac.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UIMenu {
	private static Log logger = LogFactory.getLog(UIMenu.class); 
	
	private UIWindow parent;
	private JMenuBar menuBar;
	private JMenu helpMenu;
	private JMenu fileMenu;
	private JMenuItem exitMenuItem;
	private JMenuItem changMenuItem;
	private JFileChooser fileChooser;

	public UIMenu(UIWindow _parent) {
		this.parent = _parent;
		initialize();
	}

	private void initialize() {
		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(new BorderLayout());
		menuBar = new JMenuBar();
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		fileMenu = new JMenu("文件");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);

		exitMenuItem = new JMenuItem("退出");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		changMenuItem = new JMenuItem("改变监控目录");
		changMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				logger.debug("workspace changed");
//				int intRetVal = fileChooser.showOpenDialog(parent);
//				if (intRetVal == JFileChooser.APPROVE_OPTION) {
//					String watchDir = fileChooser.getSelectedFile().getAbsolutePath();
//					logger.debug("Choosed Watch Dir: " + watchDir);
//					parent.getConfig().setWatcherDir(watchDir);
//					try {
//						parent.getWatcher().updateWatchDir(parent.getConfig());
//					} catch (IOException e) {
//						logger.error(e);
//					}
//				}
			}
		});
		fileMenu.add(changMenuItem);
		fileMenu.add(exitMenuItem);

		helpMenu = new JMenu("帮助");
		helpMenu.setMnemonic('H');
		menuBar.add(helpMenu);
		
		menuPanel.add(menuBar, BorderLayout.CENTER);
		JMenuBar userBar = new JMenuBar();
		JMenu userMenu = new JMenu("欢迎, " + parent.getSimpleContainer().getId());
		userBar.add(userMenu);
		menuPanel.add(userBar, BorderLayout.EAST);
		parent.add(menuPanel, BorderLayout.NORTH);
	}
}
