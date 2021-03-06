package com.atlassian.theplugin.idea.action.jira.ManualFilter;

import com.atlassian.theplugin.commons.cfg.ServerIdImpl;
import com.atlassian.theplugin.commons.jira.cache.JIRAServerModel;
import com.atlassian.theplugin.commons.jira.JiraServerData;
import com.atlassian.theplugin.configuration.JiraWorkspaceConfiguration;
import com.atlassian.theplugin.idea.IdeaHelper;
import com.atlassian.theplugin.idea.jira.IssueListToolWindowPanel;
import com.atlassian.theplugin.idea.jira.tree.JIRAFilterTree;
import com.atlassian.theplugin.jira.model.JIRAFilterListModel;
import com.atlassian.theplugin.jira.model.JiraCustomFilter;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

/**
 * @author pmaruszak
 * @date Aug 10, 2009
 */
public class RemoveAction extends AbstractFilterAction {


    public void actionPerformed(AnActionEvent event) {
        final IssueListToolWindowPanel panel = IdeaHelper.getIssueListToolWindowPanel(event);

        if (panel == null) {
            return;
        }


        JiraServerData jiraServer = panel.getSelectedServer();
        JIRAServerModel jiraServerModel = panel.getJiraServerModel();
        JIRAFilterListModel jiraFilterListModel = panel.getJIRAFilterListModel();


        if (jiraServer != null && jiraServerModel != null && jiraFilterListModel != null) {


            JiraCustomFilter manualFilter = ((JIRAFilterTree) panel.getLeftTree()).getSelectedManualFilter();

            int response = Messages.showYesNoDialog(
						"Are you sure you want to delete the selected custom filter (" + manualFilter.getName() + ") ?",
						"Confirm server delete",
						Messages.getQuestionIcon());

				if (response != 0) {
					return;
				}

            JiraWorkspaceConfiguration jiraProjectCfg = IdeaHelper.getJiraWorkspaceConfiguration(event);
            if (jiraProjectCfg != null
                    && jiraProjectCfg.getFiltersMap().containsKey((ServerIdImpl) jiraServer.getServerId())) {
                jiraProjectCfg.getFiltersMap().get((ServerIdImpl) jiraServer.getServerId()).getCustomFilters()
                        .remove(manualFilter.getUid());
            }

            jiraFilterListModel.removeManualFilter(jiraServer, manualFilter);
            jiraFilterListModel.fireManualFilterRemoved(manualFilter, jiraServer);
        }

    }

    boolean isEnabled(AnActionEvent event) {
           final IssueListToolWindowPanel panel = IdeaHelper.getIssueListToolWindowPanel(event);
        JiraCustomFilter manualFilter =
                     panel != null ? ((JIRAFilterTree) panel.getLeftTree()).getSelectedManualFilter() : null;
        return manualFilter != null;
    }
}
