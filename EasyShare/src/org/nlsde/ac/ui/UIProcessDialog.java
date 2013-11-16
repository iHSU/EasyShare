package org.nlsde.ac.ui;
import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * 
 * @author RunhuaXU
 *
 */
public class UIProcessDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	
	private JLabel process;
	
	public UIProcessDialog(String msg) {
//		setModal(true);
		process = new JLabel(msg);
//		setUndecorated(true);
		setBackground(Color.RED);
		add(process);
		pack();
		initLocation(this);
	}
	
	public void initLocation(JDialog frame) {
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		int frameWidth = frame.getWidth();
		int frameHeight = frame.getHeight();
		frame.setLocation((screenWidth - frameWidth) / 2,
				(screenHeight - frameHeight) / 2);
	}
}
