package org.nlsde.ac.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.nlsde.ac.util.ClientFileUtil;

import security.container.impl.SimpleSecurityContainer;
import security.container.io.UserValidate;
import security.container.util.Constants;

public class UISignin extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JLabel labelID;
	private JLabel lablePasswd;
	private JTextField textFieldID;
	private JPasswordField passwordField;
	private JButton btnOk;
	private JButton btnExit;

	private boolean success;

	public UISignin() {
		initialize();
		passwordField.requestFocus();
	}

	private void initialize() {
		labelID = new JLabel("ID");
		lablePasswd = new JLabel("密码");
		textFieldID = new JTextField();
		passwordField = new JPasswordField();
		btnOk = new JButton("登录");
		btnExit = new JButton("退出");

		labelID.setBounds(new Rectangle(40, 25, 50, 22));
		labelID.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		textFieldID.setBounds(new Rectangle(95, 25, 150, 22));
		lablePasswd.setBounds(new Rectangle(40, 65, 50, 22));
		lablePasswd.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		passwordField.setBounds(new Rectangle(95, 65, 150, 22));
		passwordField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					// without sign in for development.
					UISignin.this.setVisible(false);
					startMain("Sky@buaa.edu.cn");
					
					// with sign
//					String id = textFieldID.getText().trim();
//					String passwd = new String(passwordField.getPassword());
//					if (id != null && passwd != null && !"".equals(id)
//							&& !"".equals(passwd)) {
//						if (UserValidate.validateUser(id, passwd)) {
//							UISignin.this.setVisible(false);
//							startMain(id);
//						} else {
//							textFieldID.setText("");
//							passwordField.setText("");
//							textFieldID.requestFocus();
//							JOptionPane.showMessageDialog(UISignin.this, "用户 "
//									+ id + " 登陆失败.", "登陆提示",
//									JOptionPane.INFORMATION_MESSAGE);
//						}
//					} else {
//						JOptionPane.showMessageDialog(UISignin.this,
//								"用户名或密码不能为空,请输入!", "输入提示",
//								JOptionPane.WARNING_MESSAGE);
//					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});

		btnOk.setBounds(120, 170, 70, 25);
		btnExit.setBounds(210, 170, 70, 25);
		btnOk.addActionListener(this);
		btnExit.addActionListener(this);

		JLabel welcomLabel = new JLabel("欢迎使用数据共享平台");
		welcomLabel.setBounds(30, 10, 250, 20);
		welcomLabel.setForeground(new Color(66, 110, 180));
		welcomLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));

		this.success = false;

		JPanel loginPanel = new JPanel();

		loginPanel.setLayout(null);
		loginPanel.setBounds(new Rectangle(50, 45, 290, 110));
		loginPanel.setBorder(new TitledBorder(new EtchedBorder(
				EtchedBorder.RAISED, new Color(32, 90, 167), new Color(110,
						195, 201)), "请先登录", TitledBorder.LEFT,
				TitledBorder.TOP, null, new Color(32, 90, 167)));
		loginPanel.setBackground(Color.white);

		this.add(welcomLabel);
		this.add(loginPanel);
		loginPanel.add(labelID);
		loginPanel.add(textFieldID);
		loginPanel.add(lablePasswd);
		loginPanel.add(passwordField);
		this.add(btnOk);
		this.add(btnExit);
		this.add(new JLabel(""));

		this.setSize(400, 270);
		this.setTitle("登录");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btnOk) {
			String id = textFieldID.getText().trim();
			String passwd = new String(passwordField.getPassword());
			if (id != null && passwd != null && !"".equals(id)
					&& !"".equals(passwd)) {
				if (UserValidate.validateUser(id, passwd)) {
					this.setVisible(false);
					startMain(id);

				} else {
					textFieldID.setText("");
					passwordField.setText("");
					textFieldID.requestFocus();
					JOptionPane.showMessageDialog(this, "用户 " + id + " 登陆失败.",
							"登陆提示", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "用户名或密码不能为空,请输入!", "输入提示",
						JOptionPane.WARNING_MESSAGE);
			}

		} else if (obj == btnExit) {
			int quit = JOptionPane.showConfirmDialog(this, "你确认退出系统吗?", "退出提示",
					JOptionPane.YES_NO_OPTION);
			if (quit == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		} else {
			JOptionPane.showMessageDialog(this, "System Error", "错误",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void startMain(String id) {
		SimpleSecurityContainer simpleContainer = new SimpleSecurityContainer(
				ClientFileUtil.DEFAULT_DIR, Constants.WORK_SPACE, id);

		UIWindow window = new UIWindow(simpleContainer);
		window.setVisible(true);
		window.initialize();

	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success
	 *            the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
