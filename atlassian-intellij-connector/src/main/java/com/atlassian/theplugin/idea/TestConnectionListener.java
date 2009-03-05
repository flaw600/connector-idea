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

package com.atlassian.theplugin.idea;

import com.atlassian.theplugin.ConnectionWrapper;
import com.atlassian.theplugin.commons.cfg.ServerCfg;
import com.atlassian.theplugin.util.Connector;
import com.atlassian.theplugin.util.PluginUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import static com.intellij.openapi.ui.Messages.showDialog;
import static com.intellij.openapi.ui.Messages.showMessageDialog;
import org.apache.log4j.Category;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens for the click action (usually on a 'Test Connection' button), displays progress dialog with Cancel button
 * and run in a separate thread testConnection method on a ConnectionTester object passed to the constructor.
 * Displays message dialog with connection success/failure unless connection test was canceled.
 */
public class TestConnectionListener implements ActionListener {

	public interface ServerCfgProvider {
		ServerCfg getServerCfg();
	}

	private final Project project;
	private Connector connectionTester;
	private final ServerCfgProvider serverCfgProvider;
	private final TestConnectionProcessor processor;

	/**
	 * @param project IDEA project
	 * @param tester			object which provide testConnection method specific to the product (Bamboo/Crucible, etc.)
	 * @param serverCfgProvider provides the data of the server to connect to
	 * @param processor
	 */
	public TestConnectionListener(Project project, Connector tester, @NotNull ServerCfgProvider serverCfgProvider,
			@NotNull TestConnectionProcessor processor) {
		this.project = project;
		connectionTester = tester;
		this.serverCfgProvider = serverCfgProvider;
		this.processor = processor;
	}

	public void actionPerformed(ActionEvent e) {

		Task.Modal testConnectionTask = new TestConnectionTask(project, "Testing Connection", true);
		testConnectionTask.setCancelText("Stop");
		ProgressManager.getInstance().run(testConnectionTask);
	}

	private class TestConnectionTask extends Task.Modal {

		private static final int CHECK_CANCEL_INTERVAL = 500;	// miliseconds
		private final Category log = Category.getInstance(TestConnectionTask.class);
		private final ConnectionWrapper testConnector;

		public TestConnectionTask(Project currentProject, String title, boolean canBeCanceled) {
			super(currentProject, title, canBeCanceled);
			testConnector = new ConnectionWrapper(connectionTester, serverCfgProvider.getServerCfg(), "test thread");
		}

		@Override
		public void run(@NotNull ProgressIndicator indicator) {

			if (indicator == null) {
				PluginUtil.getLogger().error("Progress Indicator is null in TestConnectionTask!!!");
				System.out.println("Progress Indicator is null in TestConnectionTask!!!");
			} else {
				indicator.setText("Connecting...");
				indicator.setFraction(0);
				indicator.setIndeterminate(true);
			}

			testConnector.start();

			while (testConnector.getConnectionState() == ConnectionWrapper.ConnectionState.NOT_FINISHED) {
				try {
					if (indicator.isCanceled()) {
						testConnector.setInterrupted();
						//t.interrupt();
						break;
					} else {
						java.lang.Thread.sleep(CHECK_CANCEL_INTERVAL);
					}
				} catch (InterruptedException e) {
					log.info(e.getMessage());
				}
			}

			ConnectionWrapper.ConnectionState state = testConnector.getConnectionState();
			processor.setConnectionResult(state);
			switch (testConnector.getConnectionState()) {
				case FAILED:
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							if (showDialog(
									getProject(),
									testConnector.getErrorMessage(),
									"Connection Error",
									new String[]{ "OK", "Help" },
									0,
									Messages.getErrorIcon()) == 1) {
								BrowserUtil.launchBrowser(HelpUrl.getHelpUrl(Constants.HELP_TEST_CONNECTION));
							}
						}
					});
					break;
				case INTERUPTED:
					log.debug("Cancel was pressed during 'Test Connection' operation");
					break;
				case SUCCEEDED:
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							showMessageDialog(getProject(), "Connected successfully", "Connection OK",
									Messages.getInformationIcon());
							 processor.onSuccess();
						}
					});
					break;
				default: //NOT_FINISHED:
					log.warn("Unexpected 'Test Connection' thread state: "
							+ testConnector.getConnectionState().toString());
			}
		}
	}
}