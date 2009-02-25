package com.atlassian.theplugin.idea.action.issues;

import com.atlassian.theplugin.idea.Constants;
import com.atlassian.theplugin.idea.IdeaHelper;
import com.atlassian.theplugin.idea.jira.IssuesToolWindowPanel;
import com.atlassian.theplugin.jira.api.JIRAIssue;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ChangeListManager;

public class CreateChangeListAction extends JIRAAbstractAction {
	@Override
	public void actionPerformed(AnActionEvent anActionEvent) {
		final IssuesToolWindowPanel panel = IdeaHelper.getIssuesToolWindowPanel(anActionEvent);
		final JIRAIssue issue = anActionEvent.getData(Constants.ISSUE_KEY);
		if (panel != null && issue != null) {
			panel.createChangeListAction(issue);
		}
	}

	public void onUpdate(AnActionEvent event) {
	}

	public void onUpdate(AnActionEvent event, boolean enabled) {
//		if (enabled) {
		final JIRAIssue issue = event.getData(Constants.ISSUE_KEY);
		event.getPresentation().setEnabled(issue != null);

		if (issue != null) {
			String changeListName = issue.getKey() + " - " + issue.getSummary();
			final Project project = event.getData(DataKeys.PROJECT);
			if (ChangeListManager.getInstance(project).findChangeList(changeListName) == null) {
				event.getPresentation().setText("Create ChangeList");
			} else {
				event.getPresentation().setText("Activate ChangeList");
			}
		}
	}
//	}
}