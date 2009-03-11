/**
 * Copyright (C) 2008 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atlassian.theplugin.idea.ui;

import com.atlassian.theplugin.util.PluginUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DialogWithDetails extends DialogWrapper {
	private final String description;
	private final String exceptionStr;
	private JLabel ctrlDescription;
	private JTextArea ctrlDetailsText;
	private JPanel rootPane;
	private JScrollPane ctrlDetailsPane;

	protected DialogWithDetails(Project project, String description, Throwable exception) {
		super(project, false);
		this.description = description;
		this.exceptionStr = getExceptionString(exception);

		setTitle(PluginUtil.PRODUCT_NAME);
		init();
	}

	protected DialogWithDetails(Project project, String description, String exceptionString) {
		super(project, false);
		this.description = description;
		this.exceptionStr = exceptionString;

		setTitle(PluginUtil.PRODUCT_NAME);
		init();
	}

	protected DialogWithDetails(Component parent, String description, Throwable exception) {
		super(parent, false);
		this.description = description;
		this.exceptionStr = getExceptionString(exception);

		setTitle(PluginUtil.PRODUCT_NAME);
		init();
		pack();
	}

	public static int showExceptionDialog(Project project, String description, String details) {
		final DialogWithDetails dialog = new DialogWithDetails(project, description, details);
		dialog.show();
		return dialog.getExitCode();
	}

	public static int showExceptionDialog(Project project, String description, Throwable exception) {
		final DialogWithDetails dialog = new DialogWithDetails(project, description, exception);
		dialog.show();
		return dialog.getExitCode();
	}

	public static int showExceptionDialog(Component parent, String description, Throwable exception) {
		final DialogWithDetails dialog = new DialogWithDetails(parent, description, exception);
		dialog.show();
		return dialog.getExitCode();
	}

	@Override
	protected void init() {
		super.init();

		ctrlDescription.setText(description);
		ctrlDescription.setIcon(getIcon());

		ctrlDetailsText.setText(exceptionStr);

		ctrlDetailsPane.setVisible(false);
	}

	@Override
	@Nullable
	protected JComponent createCenterPanel() {
		return rootPane;
	}

	protected Icon getIcon() {
		return Messages.getErrorIcon();
	}

	@Override
	protected Action[] createActions() {
		return new Action[]{getOKAction(), getDetailsAction()};
	}

	public static String getExceptionString(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.getBuffer().toString();
	}

	protected Action getDetailsAction() {
		return new DetailsAction();
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
		rootPane = new JPanel();
		rootPane.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
		ctrlDescription = new JLabel();
		ctrlDescription.setText("Label");
		rootPane.add(ctrlDescription, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		rootPane.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
				GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		ctrlDetailsPane = new JScrollPane();
		rootPane.add(ctrlDetailsPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(600, 250),
				null, 0, false));
		ctrlDetailsText = new JTextArea();
		ctrlDetailsPane.setViewportView(ctrlDetailsText);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPane;
	}

	private class DetailsAction extends AbstractAction {
		private static final String SHOW_TXT = "Show Exception Details";
		private static final String HIDE_TXT = "Hide Exception Details";

		public DetailsAction() {
			putValue(Action.NAME, SHOW_TXT);
		}

		public void actionPerformed(ActionEvent e) {

			if (ctrlDetailsPane.isVisible()) {
				ctrlDetailsPane.setVisible(false);
				putValue(Action.NAME, SHOW_TXT);
			} else {
				ctrlDetailsPane.setVisible(true);
				putValue(Action.NAME, HIDE_TXT);
			}

			pack();
		}
	}
}