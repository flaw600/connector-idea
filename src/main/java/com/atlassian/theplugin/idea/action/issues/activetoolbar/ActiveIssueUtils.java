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
package com.atlassian.theplugin.idea.action.issues.activetoolbar;

import com.atlassian.theplugin.cfg.CfgUtil;
import com.atlassian.theplugin.commons.cfg.JiraServerCfg;
import com.atlassian.theplugin.commons.util.StringUtil;
import com.atlassian.theplugin.configuration.JiraWorkspaceConfiguration;
import com.atlassian.theplugin.idea.Constants;
import com.atlassian.theplugin.idea.IdeaHelper;
import com.atlassian.theplugin.idea.jira.IssuesToolWindowPanel;
import com.atlassian.theplugin.jira.JIRAServerFacade;
import com.atlassian.theplugin.jira.JIRAServerFacadeImpl;
import com.atlassian.theplugin.jira.api.JIRAException;
import com.atlassian.theplugin.jira.api.JIRAIssue;
import com.atlassian.theplugin.jira.model.ActiveJiraIssue;
import com.atlassian.theplugin.jira.model.ActiveJiraIssueBean;
import com.atlassian.theplugin.util.PluginUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;

/**
 * User: pmaruszak
 */
public final class ActiveIssueUtils {
	private ActiveIssueUtils() {

	}

	public static String getLabelText(ActiveJiraIssue issue) {
		if (issue != null && issue.getIssueKey() != null) {
			return "Active issue: " + issue.getIssueKey();
		}

		return "No active issue";
	}

	public static ActiveJiraIssue getActiveJiraIssue(final AnActionEvent event) {
		final JiraWorkspaceConfiguration conf = IdeaHelper.getProjectComponent(event, JiraWorkspaceConfiguration.class);

		if (conf != null) {
			return conf.getActiveJiraIssue();
		}
		return null;
	}

	public static ActiveJiraIssue getActiveJiraIssue(final Project project) {
		final JiraWorkspaceConfiguration conf = IdeaHelper.getProjectComponent(project, JiraWorkspaceConfiguration.class);

		if (conf != null) {
			return conf.getActiveJiraIssue();
		}
		return null;
	}


	public static void setActiveJiraIssue(final AnActionEvent event, final ActiveJiraIssue issue,
			final JiraServerCfg jiraServerCfg) {
		final JiraWorkspaceConfiguration conf = IdeaHelper.getProjectComponent(event, JiraWorkspaceConfiguration.class);

		if (conf != null) {
			conf.setActiveJiraIssue((ActiveJiraIssueBean) issue);
			conf.addRecentlyOpenIssue(ActiveIssueUtils.getJIRAIssue(jiraServerCfg, issue), jiraServerCfg);
		}
	}

	public static JIRAIssue getSelectedJiraIssue(final AnActionEvent event) {
		return event.getData(Constants.ISSUE_KEY);
	}


	public static JiraServerCfg getSelectedJiraServerByUrl(final AnActionEvent event, String serverUrl) {
		final IssuesToolWindowPanel panel = IdeaHelper.getIssuesToolWindowPanel(event);
		if (panel != null) {
//			//return panel.getSelectedServer();

			final Project project = IdeaHelper.getCurrentProject(event);
			return CfgUtil.getJiraServerCfgByUrl(project, panel.getProjectCfgManager(), serverUrl);
		}
		return null;
	}


	public static JiraServerCfg getSelectedJiraServerById(final AnActionEvent event, String serverId) {
		final IssuesToolWindowPanel panel = IdeaHelper.getIssuesToolWindowPanel(event);
		if (panel != null) {
			final Project project = IdeaHelper.getCurrentProject(event);
			return CfgUtil.getJiraServerCfgbyServerId(project, panel.getProjectCfgManager(), serverId);
		}
		return null;
	}

	//invokeLater necessary
	public static JIRAIssue getJIRAIssue(final AnActionEvent event) {
		return getJIRAIssue(IdeaHelper.getCurrentProject(event));
	}

	//invokeLater necessary
	public static JIRAIssue getJIRAIssue(final Project project) {
		JiraServerCfg jiraServer = getJiraServer(project);
		if (jiraServer != null) {
			final ActiveJiraIssue issue = getActiveJiraIssue(project);
			return getJIRAIssue(jiraServer, issue);
		}
		return null;
	}

	public static JIRAIssue getJIRAIssue(final JiraServerCfg jiraServer, final ActiveJiraIssue activeIssue) {
		if (jiraServer != null && activeIssue != null) {

			JIRAServerFacade facade = JIRAServerFacadeImpl.getInstance();
			try {
				return facade.getIssue(jiraServer, activeIssue.getIssueKey());
			} catch (JIRAException e) {
				PluginUtil.getLogger().error(e.getMessage());
			}
		}
		return null;
	}


	public static JiraServerCfg getJiraServer(final AnActionEvent event) {
		return getJiraServer(IdeaHelper.getCurrentProject(event));

	}

	public static JiraServerCfg getJiraServer(final Project project) {
		final ActiveJiraIssue issue = getActiveJiraIssue(project);
		return getJiraServer(project, issue);
	}

	public static JiraServerCfg getJiraServer(final Project project, final ActiveJiraIssue activeIssue) {
		final IssuesToolWindowPanel panel = IdeaHelper.getIssuesToolWindowPanel(project);
		JiraServerCfg jiraServer = null;

		if (panel != null && activeIssue != null) {
			jiraServer = CfgUtil.getJiraServerCfgbyServerId(project, panel.getProjectCfgManager(), activeIssue.getServerId());
		}
		return jiraServer;
	}

	public static void activateIssue(final AnActionEvent event, final ActiveJiraIssue newActiveIssue,
			final JiraServerCfg jiraServerCfg) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				final ActiveJiraIssue activeIssue = ActiveIssueUtils.getActiveJiraIssue(event);
				boolean isAlreadyActive = activeIssue != null;
				boolean isDeactivated = true;
				if (isAlreadyActive) {

					isDeactivated = Messages.showYesNoDialog(IdeaHelper.getCurrentProject(event),
							activeIssue.getIssueKey()
									+ " is active. Would you like to deactivate it first and proceed?",
							"Deactivating current issue",
							Messages.getQuestionIcon()) == DialogWrapper.OK_EXIT_CODE;
				}
				if (isDeactivated && ActiveIssueUtils.deactivate(event)) {
					final boolean isActivated = ActiveIssueUtils.activate(event, newActiveIssue, jiraServerCfg);
					if (isActivated) {
						ActiveIssueUtils.setActiveJiraIssue(event, newActiveIssue, jiraServerCfg);
					} else {
						ActiveIssueUtils.setActiveJiraIssue(event, null, jiraServerCfg);
					}
				}
			}
		});
	}

	private static boolean activate(final AnActionEvent event, final ActiveJiraIssue newActiveIssue,
			final JiraServerCfg jiraServerCfg) {
		final Project project = IdeaHelper.getCurrentProject(event);
		boolean isOk = true;
		final IssuesToolWindowPanel panel = IdeaHelper.getIssuesToolWindowPanel(project);
		final JIRAIssue jiraIssue = ActiveIssueUtils.getJIRAIssue(jiraServerCfg, newActiveIssue);

		if (panel != null && jiraIssue != null && jiraServerCfg != null) {
			if (jiraServerCfg != null && !jiraServerCfg.getUsername().equals(jiraIssue.getAssigneeId())) {
				isOk = Messages.showYesNoDialog(IdeaHelper.getCurrentProject(event),
						"Is already assigned to " + jiraIssue.getAssignee()
								+ ". Do you want to overwrite assignee and start progress?",
						"Issue " + jiraIssue.getKey(),
						Messages.getQuestionIcon()) == DialogWrapper.OK_EXIT_CODE;
			}

			if (isOk) {
				//assign to me and start working
				isOk = panel.startWorkingOnIssue(jiraIssue, jiraServerCfg);
			}
		}
		return isOk;
	}

	public static boolean deactivate(final AnActionEvent event) {
		final JiraWorkspaceConfiguration conf = IdeaHelper.getProjectComponent(event, JiraWorkspaceConfiguration.class);
		if (conf != null) {
			ActiveJiraIssueBean activeIssue = conf.getActiveJiraIssue();
			if (activeIssue != null) {
				final IssuesToolWindowPanel panel = IdeaHelper.getIssuesToolWindowPanel(event);
				final Project project = IdeaHelper.getCurrentProject(event);
				final JIRAIssue jiraIssue = ActiveIssueUtils.getJIRAIssue(project);
				if (panel != null && jiraIssue != null) {
					boolean isOk = true;
					final JiraServerCfg jiraServer = ActiveIssueUtils.getJiraServer(project);


					isOk = panel.logWorkOrDeactivateIssue(jiraIssue,
							jiraServer,
							StringUtil.generateJiraLogTimeString(activeIssue.recalculateTimeSpent()),
							true);


					return isOk;

				}

			}
		}
		return true;
	}
}
