package org.nlsde.ac.ui;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * 关闭按钮弹出对话框操作。
 * @author RunhuaXU
 *
 */
public class UIPolicyDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JButton okButton;
	private JButton cancellButton;
	private JTextArea textArea;
	private JLabel nameLabel;
	private JTextField nameField;
	private UIConfiguration parent;
	
	/**
	 * 
	 * @param _parent
	 * @param _editFlag true为edit状态，false为添加状态
	 */
	public UIPolicyDialog(UIConfiguration _parent) {
		//super(parent, "策略编辑");
		this.parent = _parent;
		setModal(true);
				
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JPanel northPanel = new JPanel();
		nameLabel = new JLabel("策略名称");
		northPanel.add(nameLabel);
		nameField = new JTextField();
		nameField.setColumns(100);
		northPanel.add(nameField);
		panel.add(northPanel, BorderLayout.NORTH);
		
		textArea = new JTextArea("这里填写内容");
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setLineWrap(true);        //激活自动换行功能 
		textArea.setWrapStyleWord(true);
		textArea.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				if (textArea.getText().trim().equals("这里填写内容")) {
					textArea.setText("");
				}
			}
			@Override
			public void focusLost(FocusEvent arg0) {
			}});
		panel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel btnPanel = new JPanel();
		okButton = new JButton("确定");
		okButton.addActionListener(this);
		okButton.requestFocus();
		btnPanel.add(okButton);
		cancellButton = new JButton("取消");
		cancellButton.addActionListener(this);
		btnPanel.add(cancellButton);
		panel.add(btnPanel, BorderLayout.SOUTH);

		
		this.add(panel);
		this.setTitle("策略编辑");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(740, 550);
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
	
	/**
	 * set the edit model
	 * @param name
	 * @param content
	 */
	public void setEditStatus(String name, String content) {
		nameField.setText(name);
		nameField.setEditable(false);
		textArea.setText(content);
	}
	
	public void setDisplayStatus(String name, String content) {
		nameField.setText(name);
		nameField.setEditable(false);
		textArea.setText(content);
		textArea.setEditable(false);
		okButton.setEnabled(false);
		cancellButton.setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			String policyName = nameField.getText();
			String policyContent = textArea.getText();
			if (policyName != null && policyContent != null
					&& !"".equals(policyName) && !"".equals(policyContent)) {
				this.parent.getConfigManage().addPolicy(policyName, policyContent);
				this.parent.updatePolicyList();
				setVisible(false);
			}
			else {
				JOptionPane.showMessageDialog(this, "策略名称和策略文件不能为空");
			}
		}
		else {
			setVisible(false);
		}
	}

	/**
	 * @return the parent
	 */
	public UIConfiguration getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(UIConfiguration parent) {
		this.parent = parent;
	}
}
