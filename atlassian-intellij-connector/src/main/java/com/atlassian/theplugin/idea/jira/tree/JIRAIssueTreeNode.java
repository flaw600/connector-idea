package com.atlassian.theplugin.idea.jira.tree;

import com.atlassian.theplugin.idea.jira.renderers.JIRAIssueListOrTreeRendererPanel;
import com.atlassian.theplugin.idea.ui.tree.paneltree.AbstractTreeNode;
import com.atlassian.theplugin.jira.api.JIRAIssue;

import javax.swing.*;

public class JIRAIssueTreeNode extends AbstractTreeNode {
	private final JIRAIssue issue;

	public JIRAIssueTreeNode(JIRAIssue issue) {
		super(issue.getKey() + ": " + issue.getSummary(), null, null);
		this.issue = issue;
		renderer = new JIRAIssueListOrTreeRendererPanel(issue);
	}

	private JIRAIssueListOrTreeRendererPanel renderer;

	@Override
	public JComponent getRenderer(JComponent c, boolean selected, boolean expanded, boolean hasFocus) {
		renderer.setParameters(selected, c.isEnabled());
		return renderer;
	}

	public JIRAIssue getIssue() {
		return issue;
	}

	@Override
	public String toString() {
		return name;
	}
}
