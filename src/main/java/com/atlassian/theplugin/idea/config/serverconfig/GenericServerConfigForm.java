package com.atlassian.theplugin.idea.config.serverconfig;

import com.atlassian.theplugin.configuration.Server;
import com.atlassian.theplugin.configuration.ServerBean;
import com.atlassian.theplugin.idea.IdeaHelper;
import com.atlassian.theplugin.idea.config.TestConnectionThread;
import com.atlassian.theplugin.util.Util;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.log4j.Category;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Thread.sleep;

/**
 * Plugin configuration form.
 */
public class GenericServerConfigForm extends JComponent implements ServerPanel {
	private JPanel rootComponent;
	private JTextField serverName;
	private JTextField serverUrl;
	private JTextField username;
	private JPasswordField password;
	private JButton testConnection;
	private JCheckBox chkPasswordRemember;
	private JCheckBox cbEnabled;

	private transient Server originalServer;

	public GenericServerConfigForm(final ConnectionTester tester) {

		$$$setupUI$$$();
		testConnection.addActionListener(new TestConnectionListener(tester));
	}

	public void setData(Server server) {
		this.originalServer = new ServerBean(server);

		serverName.setText(server.getName());
		serverUrl.setText(server.getUrlString());
		username.setText(server.getUserName());
		chkPasswordRemember.setSelected(server.getShouldPasswordBeStored());
		password.setText(server.getPasswordString());
		cbEnabled.setSelected(server.getEnabled());
	}

	public Server getData() {
		serverUrl.setText(Util.addHttpPrefix(serverUrl.getText()));

		Server server = new ServerBean(originalServer);
		server.setName(serverName.getText());
		server.setUrlString(serverUrl.getText());
		server.setUserName(username.getText());
		server.setPasswordString(String.valueOf(password.getPassword()), chkPasswordRemember.isSelected());
		server.setEnabled(cbEnabled.isSelected());
		return server;
	}

	public boolean isModified() {
		boolean isModified = false;

		if (originalServer != null) {
			if (chkPasswordRemember.isSelected() != originalServer.getShouldPasswordBeStored()) {
				return true;
			}
			if (serverName.getText() != null
					? !serverName.getText().equals(originalServer.getName()) : originalServer.getName() != null) {
				return true;
			}
			if (cbEnabled.isSelected() != originalServer.getEnabled()) {
				return true;
			}
			if (serverUrl.getText() != null
					? !serverUrl.getText().equals(originalServer.getUrlString()) : originalServer.getUrlString() != null) {
				return true;
			}
			if (username.getText() != null
					? !username.getText().equals(originalServer.getUserName()) : originalServer.getUserName() != null) {
				return true;
			}
			String pass = String.valueOf(password.getPassword());
			if (!pass.equals(originalServer.getPasswordString())) {
				return true;
			}

		}
		return isModified;
	}


	public JComponent getRootComponent() {
		return rootComponent;
	}

	public void setVisible(boolean visible) {
		rootComponent.setVisible(visible);
	}

	private void createUIComponents() {
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		rootComponent = new JPanel();
		rootComponent.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
		rootComponent.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		serverName = new JTextField();
		serverName.setText("");
		panel1.add(serverName, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("Server Name");
		label1.setDisplayedMnemonic('S');
		label1.setDisplayedMnemonicIndex(0);
		panel1.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		serverUrl = new JTextField();
		panel1.add(serverUrl, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		username = new JTextField();
		panel1.add(username, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		password = new JPasswordField();
		panel1.add(password, new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("Server URL");
		label2.setDisplayedMnemonic('U');
		label2.setDisplayedMnemonicIndex(7);
		panel1.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setText("User Name");
		label3.setDisplayedMnemonic('N');
		label3.setDisplayedMnemonicIndex(5);
		panel1.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setText("Password");
		label4.setDisplayedMnemonic('P');
		label4.setDisplayedMnemonicIndex(0);
		panel1.add(label4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		testConnection = new JButton();
		testConnection.setText("Test Connection");
		testConnection.setMnemonic('T');
		testConnection.setDisplayedMnemonicIndex(0);
		panel1.add(testConnection, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		chkPasswordRemember = new JCheckBox();
		chkPasswordRemember.setSelected(true);
		chkPasswordRemember.setText("Remember password");
		chkPasswordRemember.setMnemonic('R');
		chkPasswordRemember.setDisplayedMnemonicIndex(0);
		panel1.add(chkPasswordRemember, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		cbEnabled = new JCheckBox();
		cbEnabled.setHorizontalTextPosition(11);
		cbEnabled.setText("Server Enabled");
		cbEnabled.setMnemonic('E');
		cbEnabled.setDisplayedMnemonicIndex(7);
		panel1.add(cbEnabled, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		rootComponent.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		label1.setLabelFor(serverName);
		label2.setLabelFor(serverUrl);
		label3.setLabelFor(username);
		label4.setLabelFor(password);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootComponent;
	}

	private class TestConnectionListener implements ActionListener {

		private ConnectionTester connectionTester = null;

		public TestConnectionListener(ConnectionTester tester) {
			connectionTester = tester;
		}

		public void actionPerformed(ActionEvent e) {

			Task.Modal testConnectionTask =	new TestConnectionTask(
					IdeaHelper.getCurrentProject(), "Testing Connection", true, connectionTester);
			testConnectionTask.setCancelText("Stop");

			ProgressManager.getInstance().run(testConnectionTask);
		}

		private class TestConnectionTask extends Task.Modal {

			private TestConnectionThread testConnectionThread = null;
			private static final int CHECK_CANCEL_INTERVAL = 500;	// miliseconds
			private final Category log = Category.getInstance(TestConnectionTask.class);

			public TestConnectionTask(Project currentProject, String title, boolean canBeCanceled, ConnectionTester tester) {

				super(currentProject, title, canBeCanceled);

				testConnectionThread = new TestConnectionThread(tester,
						serverUrl.getText(), username.getText(), String.valueOf(password.getPassword()));
			}

			public void run(ProgressIndicator indicator) {

				indicator.setText("Connecting...");
				indicator.setFraction(0);
				indicator.setIndeterminate(true);

				testConnectionThread.start();

				while (testConnectionThread.isRunning()) {
					try {
						if (indicator.isCanceled()) {
							testConnectionThread.setInterrupted();
							//t.interrupt();
							break;
						} else {
							sleep(CHECK_CANCEL_INTERVAL);
						}
					} catch (InterruptedException e) {
						log.info(e.getMessage());
					}
				}
			}
		}
	}
}