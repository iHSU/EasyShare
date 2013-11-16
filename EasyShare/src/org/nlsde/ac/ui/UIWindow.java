package org.nlsde.ac.ui;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import security.container.impl.SimpleSecurityContainer;

/**
 * 系统主窗口
 * 
 * @author RunhuaXU
 *
 */
public class UIWindow extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private UITabPane tabPane;
	private UIManagePanel toolPanel;
	private UISignin signin;
	private SimpleSecurityContainer simpleContainer;

	public UIWindow(SimpleSecurityContainer _simpleContainer) {
		this.setTitle("EasyShare");
		this.setSize(1000, 600);
//		this.setResizable(false);
		this.simpleContainer = _simpleContainer;
		
		// initialize the location
		initLocation(this);
		
		// initialize the trayIcon
		new UITrayIcon(this);
		
		// initialize the system menu
		new UIMenu(this);
			
		toolPanel = new UIManagePanel(this);
		
		tabPane = new UITabPane(this);
				
		// set the system exit methods.
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				UICloseDialog dialog = new UICloseDialog(UIWindow.this);
				dialog.setVisible(true);
			}
		});
	}
	
	public void initialize() {
		tabPane.initialize();
		toolPanel.initialize();
	}

	/**
	 * the first location on screen.
	 * @param frame
	 */
	public void initLocation(JFrame frame) {
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		int frameWidth = frame.getWidth();
		int frameHeight = frame.getHeight();
		frame.setLocation((screenWidth - frameWidth) / 2,
				(screenHeight - frameHeight) / 2);
	}
	
	public UITabPane getTabPane() {
		return tabPane;
	}

	public void setTabPane(UITabPane tabPane) {
		this.tabPane = tabPane;
	}

	public UIManagePanel getToolPanel() {
		return toolPanel;
	}

	public void setToolPanel(UIManagePanel toolPanel) {
		this.toolPanel = toolPanel;
	}

	/**
	 * @return the signin
	 */
	public UISignin getSignin() {
		return signin;
	}

	/**
	 * @param signin the signin to set
	 */
	public void setSignin(UISignin signin) {
		this.signin = signin;
	}

	/**
	 * @return the simpleContainer
	 */
	public SimpleSecurityContainer getSimpleContainer() {
		return simpleContainer;
	}

	/**
	 * @param simpleContainer the simpleContainer to set
	 */
	public void setSimpleContainer(SimpleSecurityContainer simpleContainer) {
		this.simpleContainer = simpleContainer;
	}
	
}
