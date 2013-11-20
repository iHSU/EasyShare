package org.nlsde.ac.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UITrayIcon {
	private TrayIcon trayIcon;
	private UIWindow parent;

	public UITrayIcon(UIWindow parent) {
		this.parent = parent;
		initTray();
	}

	public void initTray() {
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			PopupMenu popuMenu = new PopupMenu();
			MenuItem exitItem = new MenuItem("退出");
			MenuItem sysconfigItem = new MenuItem("系统配置");
			MenuItem openItem = new MenuItem("主菜单");

			popuMenu.add(openItem);
			popuMenu.add(sysconfigItem);
			popuMenu.add(exitItem);

			Image icon = Toolkit.getDefaultToolkit()
					.getImage(
							System.getProperty("user.dir")
									+ "\\images\\icon_16x16.png");
			trayIcon = new TrayIcon(icon, "A Demo", popuMenu);

			trayIcon.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent me) {
					if (me.getClickCount() == 2) {
						showIconTray(true);
					}
				}
			});

			exitItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					try {
						System.exit(0);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			openItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showIconTray(true);
				}
			});
			
			sysconfigItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					parent.getTabPane().setSelectedIndex(3);
					showIconTray(true);					
				}
			});

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("System does not support the tray!");
		}
	}
	
	private void showIconTray(boolean b) {
		if (parent.isVisible() != b) {
			parent.setVisible(b);
		}
	}
}
