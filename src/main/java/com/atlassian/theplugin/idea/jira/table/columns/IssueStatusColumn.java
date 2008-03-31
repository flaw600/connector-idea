package com.atlassian.theplugin.idea.jira.table.columns;

import com.atlassian.theplugin.idea.TableColumnInfo;
import com.atlassian.theplugin.idea.jira.JiraIssueAdapter;

import java.util.Comparator;

public class IssueStatusColumn extends TableColumnInfo {
	private static final int COL_WIDTH = 20;

	public Object valueOf(Object o) {
		return (JiraIssueAdapter) o;
	}

	public Class getColumnClass() {
		return JiraIssueAdapter.class;
	}

	public Comparator getComparator() {
		return new Comparator() {
			public int compare(Object o, Object o1) {
				return ((JiraIssueAdapter) o).getStatus().compareTo(((JiraIssueAdapter) o1).getStatus());
			}
		};
	}

	public int getPrefferedWidth() {
		return COL_WIDTH;
	}

	public String getColumnName() {
		return "Status";
	}
}