package org.nlsde.ac.ui;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

public class UITabPane extends JTabbedPane{

	private static final long serialVersionUID = 1L;
	private UIWindow mainWindow;
	private UIConsole console;
	private UITrackManage trackManage;
	private UIConfiguration configuration;
	private UIDataManage dataManage;
	
	public UITabPane (UIWindow _parent) {
		this.mainWindow = _parent;
		
		console = new UIConsole(this);
		trackManage = new UITrackManage(this);
		configuration = new UIConfiguration(this);
		dataManage = new UIDataManage(this);
		
		mainWindow.add(this);
	}

	public void initialize() {
		console.initilize();
		this.addTab("Tracks", new ImageIcon(System.getProperty("user.dir")
				+ "\\images\\log_16x16.png"), trackManage);
		this.addTab("Datas",new ImageIcon(System.getProperty("user.dir")
				+ "\\images\\data_16x16.png"), dataManage);
		this.addTab("Console",new ImageIcon(System.getProperty("user.dir")
				+ "\\images\\console_16x16.png"), console);	
		this.addTab("Configuration", new ImageIcon(System.getProperty("user.dir")
				+ "\\images\\config_16x16.png"), configuration);
	}

	public UIWindow getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(UIWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	public UIConsole getConsole() {
		return console;
	}

	public void setConsole(UIConsole console) {
		this.console = console;
	}

	/**
	 * @return the trackManage
	 */
	public UITrackManage getTrackManage() {
		return trackManage;
	}

	/**
	 * @param trackManage the trackManage to set
	 */
	public void setTrackManage(UITrackManage trackManage) {
		this.trackManage = trackManage;
	}

	
	
}
