package org.nlsde.ac.ui;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * 关闭按钮弹出对话框操作。
 * @author RunhuaXU
 *
 */
public class UICloseDialog extends JDialog implements ItemListener, ActionListener{
	private static final long serialVersionUID = 1L;
	private JRadioButton closeWindow;
	private JRadioButton exitSystem;
	private JButton okButton;
	private JButton cancellButton;
	private boolean btnGroupResult = false;
	
	public UICloseDialog(UIWindow parent) {
		super(parent, "Close");
		setModal(true);
		
		closeWindow = new JRadioButton("关闭窗口", true);
		exitSystem = new JRadioButton("退出系统", false);
		
		okButton = new JButton("确定");
		cancellButton = new JButton("取消");
		
		closeWindow.addItemListener(this);
		exitSystem.addItemListener(this);
		
		okButton.addActionListener(this);
		cancellButton.addActionListener(this);
		
		ButtonGroup closeButtonGroup = new ButtonGroup();
		closeButtonGroup.add(exitSystem);
		closeButtonGroup.add(closeWindow);
		
		JPanel p = new JPanel();
		p.add(exitSystem);
		p.add(closeWindow);
		p.add(cancellButton);
		p.add(okButton);
		okButton.requestFocus();
		
		add(p);
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
	
	@Override
	public void itemStateChanged(ItemEvent itemEvent) {
		if (itemEvent.getSource() == exitSystem) {
			btnGroupResult = true;
		}
		else {
			btnGroupResult = false;
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			if (btnGroupResult) {
				System.exit(0);
			}
			else {
				setVisible(false);
				this.getParent().setVisible(false);
			}
		}
		else {
			setVisible(false);
		}
	}
}
