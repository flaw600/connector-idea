package com.atlassian.theplugin.idea;

import com.atlassian.theplugin.configuration.PluginConfiguration;
import com.atlassian.theplugin.idea.autoupdate.NewVersionChecker;
import com.atlassian.theplugin.idea.autoupdate.NewVersionListener;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: lguminski
 * Date: Feb 26, 2008
 * Time: 9:39:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class GeneralConfigForm {
	private JCheckBox chkAutoUpdateEnabled;
	private JPanel mainPanel;
	private JButton checkNowButton;
	private JPanel autoUpdateConfigPanel;
	private JCheckBox chkUnstableVersionsCheckBox;
	private JCheckBox reportAnonymousUsageStatisticsCheckBox;
	private Boolean isAnonymousFeedbackEnabled;

	public GeneralConfigForm(NewVersionChecker checker, PluginConfiguration pluginConfiguration) {
		checkNowButton.addActionListener(new NewVersionListener(checker, pluginConfiguration));
		chkAutoUpdateEnabled.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				chkUnstableVersionsCheckBox.setEnabled(chkAutoUpdateEnabled.isSelected());
			}
		});
		reportAnonymousUsageStatisticsCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				isAnonymousFeedbackEnabled = reportAnonymousUsageStatisticsCheckBox.isSelected();
			}
		});
	}

	public Component getRootPane() {
		return mainPanel;
	}

	public boolean getIsAutoUpdateEnabled() {
		return chkAutoUpdateEnabled.isSelected();
	}

	public void setAutoUpdateEnabled(boolean autoUpdateEnabled) {
		chkAutoUpdateEnabled.setSelected(autoUpdateEnabled);
		chkUnstableVersionsCheckBox.setEnabled(autoUpdateEnabled);
	}

	public boolean getIsCheckUnstableVersionsEnabled() {
		return chkUnstableVersionsCheckBox.isSelected();
	}

	public void setIsCheckUnstableVersionsEnabled(boolean isCheckUnstableVersionsEnabled) {
		chkUnstableVersionsCheckBox.setSelected(isCheckUnstableVersionsEnabled);
	}

	public boolean getIsAnonymousFeedbackEnabled() {
		return this.isAnonymousFeedbackEnabled;
	}

	public void setIsAnonymousFeedbackEnabled(Boolean isAnonymousFeedbackEnabled) {
		this.isAnonymousFeedbackEnabled = isAnonymousFeedbackEnabled;
		if (isAnonymousFeedbackEnabled == null || !isAnonymousFeedbackEnabled) {
			reportAnonymousUsageStatisticsCheckBox.setSelected(false);
		} else {
			reportAnonymousUsageStatisticsCheckBox.setSelected(true);
		}
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
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12), null));
		autoUpdateConfigPanel = new JPanel();
		autoUpdateConfigPanel.setLayout(new GridLayoutManager(2, 3, new Insets(0, 12, 12, 12), -1, -1));
		GridBagConstraints gbc;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		mainPanel.add(autoUpdateConfigPanel, gbc);
		autoUpdateConfigPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Auto update"));
		checkNowButton = new JButton();
		checkNowButton.setText("Check now");
		checkNowButton.setMnemonic('C');
		checkNowButton.setDisplayedMnemonicIndex(0);
		autoUpdateConfigPanel.add(checkNowButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		autoUpdateConfigPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		chkUnstableVersionsCheckBox = new JCheckBox();
		chkUnstableVersionsCheckBox.setEnabled(false);
		chkUnstableVersionsCheckBox.setText("Check unstable versions");
		panel1.add(chkUnstableVersionsCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(12, -1), null, null, 0, false));
		chkAutoUpdateEnabled = new JCheckBox();
		chkAutoUpdateEnabled.setText("Enabled");
		chkAutoUpdateEnabled.setMnemonic('E');
		chkAutoUpdateEnabled.setDisplayedMnemonicIndex(0);
		autoUpdateConfigPanel.add(chkAutoUpdateEnabled, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		autoUpdateConfigPanel.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel spacer3 = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.VERTICAL;
		mainPanel.add(spacer3, gbc);
		reportAnonymousUsageStatisticsCheckBox = new JCheckBox();
		reportAnonymousUsageStatisticsCheckBox.setEnabled(true);
		reportAnonymousUsageStatisticsCheckBox.setSelected(false);
		reportAnonymousUsageStatisticsCheckBox.setText("Report anonymous usage statistics to help us develop a better plugin");
		reportAnonymousUsageStatisticsCheckBox.setMnemonic('R');
		reportAnonymousUsageStatisticsCheckBox.setDisplayedMnemonicIndex(0);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		mainPanel.add(reportAnonymousUsageStatisticsCheckBox, gbc);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return mainPanel;
	}
}
