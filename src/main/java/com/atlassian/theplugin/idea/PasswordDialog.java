package com.atlassian.theplugin.idea;

import com.atlassian.theplugin.LoginDataProvided;
import com.atlassian.theplugin.util.Connector;
import com.atlassian.theplugin.bamboo.BambooServerFactory;
import com.atlassian.theplugin.bamboo.api.BambooLoginException;
import com.atlassian.theplugin.configuration.Server;
import com.atlassian.theplugin.exception.ThePluginException;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PasswordDialog extends JDialog implements LoginDataProvided {

	private JPanel passwordPanel;
	private JCheckBox chkRememberPassword;
	private JPasswordField passwordField;
	private JButton testConnectionButton;
	private JLabel lblCommand;
	private JTextField userName;
	private transient Server server;

	public PasswordDialog(final Server server) {
		this.server = server;
		setContentPane(passwordPanel);
		setModal(true);
// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		lblCommand.setText("<html><p>Please provide password to connect \"" + this.server.getName() + "\" server:</p> <p><i>" + this.server.getUrlString() + "</i></p></html>");
// call onCancel() on ESCAPE
		passwordPanel.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		testConnectionButton.addActionListener(new TestConnectionListener(new Connector() {

			public void connect() throws ThePluginException {
				this.validate();
				try {
					BambooServerFactory.getBambooServerFacade().testServerConnection(
							super.getUrl(), super.getUserName(), super.getPassword());
				} catch (BambooLoginException e) {
					throw new ThePluginException("Error conecting bamboo server.", e);
				}
			}

		}, this));
	}

	private void onCancel() {
// add your code here if necessary
		dispose();
	}


	public JPanel getPasswordPanel() {
		return passwordPanel;
	}

	public String getPasswordString() {
		return String.valueOf(passwordField.getPassword());
	}

	public Boolean getShouldPasswordBeStored() {
		return chkRememberPassword.isSelected();
	}

	public void setUserName(String anUsername) {
		this.userName.setText(anUsername);
	}

	public String getUserName() {
		return this.userName.getText();
	}

	private void createUIComponents() {
		// TODO: place custom component creation code here
	}

	public String getServerUrl() {
		return server.getUrlString();
	}

	public String getPassword() {
		return getPasswordString();
	}

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		passwordPanel = new JPanel();
		passwordPanel.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
		passwordPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		passwordField = new JPasswordField();
		panel1.add(passwordField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		testConnectionButton = new JButton();
		testConnectionButton.setText("Test connection");
		testConnectionButton.setMnemonic('T');
		testConnectionButton.setDisplayedMnemonicIndex(0);
		panel1.add(testConnectionButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		chkRememberPassword = new JCheckBox();
		chkRememberPassword.setEnabled(true);
		chkRememberPassword.setSelected(false);
		chkRememberPassword.setText("Store password in configuration");
		panel1.add(chkRememberPassword, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		lblCommand = new JLabel();
		lblCommand.setText("");
		panel1.add(lblCommand, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("Password");
		panel1.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("User Name");
		panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		userName = new JTextField();
		panel1.add(userName, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		label1.setLabelFor(passwordField);
		label2.setLabelFor(userName);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return passwordPanel;
	}
}
