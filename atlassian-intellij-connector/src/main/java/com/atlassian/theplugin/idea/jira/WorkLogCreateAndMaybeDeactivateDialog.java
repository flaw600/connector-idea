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

package com.atlassian.theplugin.idea.jira;

import com.atlassian.theplugin.commons.remoteapi.ServerData;
import com.atlassian.theplugin.idea.IdeaVersionFacade;
import com.atlassian.theplugin.idea.ui.DialogWithDetails;
import com.atlassian.theplugin.jira.JIRAIssueProgressTimestampCache;
import com.atlassian.theplugin.jira.JIRAServerFacadeImpl;
import com.atlassian.theplugin.jira.JIRAServerFacade;
import com.atlassian.theplugin.jira.api.JIRAIssue;
import com.atlassian.theplugin.jira.api.JIRAAction;
import com.atlassian.theplugin.jira.api.JIRAException;
import com.atlassian.theplugin.util.PluginUtil;
import com.atlassian.theplugin.configuration.JiraWorkspaceConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vcs.changes.ui.MultipleChangeListBrowser;
import com.intellij.util.ui.UIUtil;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.jetbrains.annotations.NotNull;

import javax.management.timer.Timer;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WorkLogCreateAndMaybeDeactivateDialog extends DialogWrapper {

	private JPanel contentPane;
	private JTextArea comment;
	private JButton endDateChange;
	private JLabel endDateLabel;
	private JTextField timeSpentField;
	private JRadioButton btnLeaveUnchanged;
	private JRadioButton btnAutoUpdate;
	private JRadioButton btnUpdateManually;
	private JTextField remainingEstimateField;
	private JTextPane explanationText;
	private JPanel endTimePanel;

    private JRadioButton btnChangeSetDoNothing;
    private JRadioButton btnChangeSetDeactivate;
    private JRadioButton btnChangeSetRemove;
    private ButtonGroup changeSetButtonGroup;

	private JPanel changesetPanel;
	private JPanel changesPanel;
	private JPanel timePanel;
	private JPanel commentPanel;
	private JCheckBox chkLogWork;
	private JCheckBox chkCommitChanges;
    private JIRAIssue issue;
    private final Project project;
	private final boolean deactivateActiveIssue;
    private JiraWorkspaceConfiguration config;
    private Date endTime = Calendar.getInstance().getTime();

	private WdhmInputListener timeSpentListener;
	private WdhmInputListener remainingEstimateListener;
	private MultipleChangeListBrowser changesBrowserPanel;
    private JIRAServerFacade facade;
    private JComboBox actionCombo;
    private JCheckBox cbPerformWorkflowAction;


    /**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */

	private void setupUI() {
		CellConstraints cc = new CellConstraints();

		contentPane = new JPanel(new FormLayout(
				"3dlu, fill:pref:grow, 3dlu",
				"3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, fill:pref:grow, 3dlu"));

		chkLogWork = new JCheckBox("Log Work", config.isActiveIssueLogWork());
		contentPane.add(createTimePanel(), cc.xy(2, 6));

		if (deactivateActiveIssue) {
			timePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Time Tracking"));

            contentPane.add(createWorkflowActionCombo(), cc.xy(2, 2));
			contentPane.add(chkLogWork, cc.xy(2, 4));
			chkCommitChanges = new JCheckBox("Commit Changes", config.isActiveIssueCommitChanges());
			contentPane.add(chkCommitChanges, cc.xy(2, 8));
			contentPane.add(createChangesetPanel(), cc.xy(2, 10));

			contentPane.setMinimumSize(new Dimension(800, 600));
			contentPane.setPreferredSize(new Dimension(800, 600));
		}
	}

    private JPanel createWorkflowActionCombo() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;

        cbPerformWorkflowAction = new JCheckBox("Perform Workflow Action");
        cbPerformWorkflowAction.setSelected(config.isActiveIssueProgressWorkflowAction());
        cbPerformWorkflowAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                actionCombo.setEnabled(cbPerformWorkflowAction.isSelected());
            }
        });
        p.add(cbPerformWorkflowAction, gbc);

        actionCombo = new JComboBox();

        java.util.List<JIRAAction> actions = JiraIssueAdapter.get(issue).getCachedActions();
        if (actions != null) {
            fillActionCombo(actions);
        } else {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        ServerData jiraServer = issue.getServer();

                        if (jiraServer != null) {
                            final List<JIRAAction> actions = facade.getAvailableActions(jiraServer, issue);
                            JiraIssueAdapter.get(issue).setCachedActions(actions);

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    fillActionCombo(actions);
                                    updateOKAction();
                                }
                            });
                        }
                    } catch (JIRAException e) {
                        DialogWithDetails.showExceptionDialog(project, "Failed to retrieve available actions", e);
                    }
                }
            };
            t.start();
        }

        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;

        p.add(actionCombo, gbc);

        JPanel filler = new JPanel();
        filler.setOpaque(false);

        gbc.gridx++;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        p.add(filler, gbc);

        actionCombo.setEnabled(cbPerformWorkflowAction.isSelected());
        
        return p;
    }

    private void fillActionCombo(List<JIRAAction> actions) {
        long lastSelectedAction = config.getSelectedWorkflowAction();

        JIRAAction actionToSelect = null;
        for (JIRAAction action : actions) {
            if (action.getId() == lastSelectedAction) {
                actionToSelect = action;
            }
            actionCombo.addItem(action);
        }
        if (actionToSelect != null) {
            actionCombo.setSelectedItem(actionToSelect);
        }
    }

    public enum AfterCommit {
        DO_NOTHING,
        DEACTIVATE_CHANGESET,
        REMOVE_CHANGESET
    }

    private JPanel createChangesetPanel() {
		CellConstraints cc = new CellConstraints();

		changesetPanel = new JPanel(new FormLayout(
				"3dlu, fill:pref:grow, 3dlu",
				"3dlu, fill:d:grow, 3dlu, pref, 3dlu, pref, 3dlu, pref, pref, pref, 3dlu"));
		changesetPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Changes"));
		changesPanel = new JPanel(new BorderLayout(0, 0));
		changesPanel.setPreferredSize(new Dimension(1, 1));
		changesetPanel.add(changesPanel, cc.xy(2, 2));

		commentPanel = new JPanel(new FormLayout("right:pref, fill:d:grow", "40dlu"));
		commentPanel.add(new JLabel("Comment:"), cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		comment = new JTextArea();
		final JScrollPane scroll = new JScrollPane();
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setViewportView(comment);
		commentPanel.add(scroll, cc.xy(2, 1, CellConstraints.FILL, CellConstraints.FILL));
		changesetPanel.add(commentPanel, cc.xy(2, 4));

        btnChangeSetDoNothing =
                new JRadioButton("Leave the current change list active",
                        config.getActiveIssueAfterCommit() == AfterCommit.DO_NOTHING.ordinal());
        btnChangeSetDeactivate =
                new JRadioButton("Deactivate the currently active change list (activate the default change list)",
                        config.getActiveIssueAfterCommit() == AfterCommit.DEACTIVATE_CHANGESET.ordinal());
        btnChangeSetRemove =
                new JRadioButton("Remove the currently active change list (activate the default change list)",
                        config.getActiveIssueAfterCommit() == AfterCommit.REMOVE_CHANGESET.ordinal());

        changeSetButtonGroup = new ButtonGroup();
        changeSetButtonGroup.add(btnChangeSetDoNothing);
        changeSetButtonGroup.add(btnChangeSetDeactivate);
        changeSetButtonGroup.add(btnChangeSetRemove);

        if (changeSetButtonGroup.getSelection() == null) {
            btnChangeSetDoNothing.setSelected(true);
        }

        changesetPanel.add(new JLabel("After Commit:"), cc.xy(2, 6));
        changesetPanel.add(btnChangeSetDoNothing, cc.xy(2, 8));
        changesetPanel.add(btnChangeSetDeactivate, cc.xy(2, 9));
        changesetPanel.add(btnChangeSetRemove, cc.xy(2, 10));

        UIUtil.setEnabled(changesetPanel, chkCommitChanges.isSelected(), true);

		return changesetPanel;
	}

	private static JTextField createFixedTextField(int width, int height) {
		JTextField f = new JTextField();
		f.setMaximumSize(new Dimension(width, height));
		f.setMinimumSize(new Dimension(width, height));
		f.setPreferredSize(new Dimension(width, height));
		return f;
	}

	private JPanel createTimePanel() {
		CellConstraints cc = new CellConstraints();
		timePanel = new JPanel(new FormLayout(
				"3dlu, right:pref, 3dlu, left:pref, 3dlu, 10dlu, left:pref, 3dlu, left:pref:grow, 3dlu",
				"3dlu, pref, 3dlu, pref, pref, pref, 3dlu, pref, 3dlu"));

		timePanel.add(new JLabel("Time Spent:"), cc.xy(2, 2));

		timeSpentField = createFixedTextField(120, 28);
		timePanel.add(timeSpentField, cc.xy(4, 2));

		explanationText = new JTextPane();
		explanationText.setText(
				"An estimate of how much time you have spent working."
						+ "\nThe format of this is ' *w *d *h *m ' (representing weeks,"
						+ "\ndays, hours and minutes - where * can be any number)"
						+ "\nExamples: 4d, 5h 30m, 60m and 3w.");

		explanationText.setEditable(false);
		explanationText.setEnabled(true);
		explanationText.setFont(new Font(explanationText.getFont().getName(), explanationText.getFont().getStyle(), 10));
		explanationText.setOpaque(false);

		timePanel.add(explanationText, cc.xywh(4, 4, 1, 5));

		timePanel.add(new JLabel("Remaining Estimate:"), cc.xyw(6, 2, 2));

		btnAutoUpdate = new JRadioButton("Auto Update", true);
		btnLeaveUnchanged = new JRadioButton("Leave Unchanged");
		btnUpdateManually = new JRadioButton("Update Manually:");

		timePanel.add(btnAutoUpdate, cc.xy(7, 4));
		timePanel.add(btnLeaveUnchanged, cc.xy(7, 5));
		timePanel.add(btnUpdateManually, cc.xy(7, 6));

		remainingEstimateField = createFixedTextField(120, 28);
		remainingEstimateField.setEnabled(false);
		timePanel.add(remainingEstimateField, cc.xy(9, 6));

		ButtonGroup group = new ButtonGroup();
		group.add(btnUpdateManually);
		group.add(btnAutoUpdate);
		group.add(btnLeaveUnchanged);

		endTimePanel = new JPanel(new FormLayout("fill:pref:grow, 3dlu, pref", "pref"));
		endDateLabel = new JLabel("1/01/08 12:00");
		endTimePanel.add(endDateLabel, cc.xy(1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
		endDateChange = new JButton("Change");
		endTimePanel.add(endDateChange, cc.xy(3, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));

		if (!deactivateActiveIssue) {
			timePanel.add(new JLabel("End Time:"), cc.xy(2, 8));
			timePanel.add(endTimePanel, cc.xy(4, 8));
		}

        UIUtil.setEnabled(timePanel, chkLogWork.isSelected(), true);

		return timePanel;
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return contentPane;
	}

	private class WdhmInputListener extends JiraTimeWdhmTextFieldListener {

		private boolean matchFound = true;

		public WdhmInputListener(JTextField field) {
			super(field, false);
		}

		private class Period {
			public Period(String r) {
				regex = r;
				interval = 0;
			}

			public long getInterval() {
				return interval;
			}

			private long interval;

			public String getRegex() {
				return regex;
			}

			private String regex;

			public void findAndSet(String txt) {
				Pattern p = Pattern.compile("(\\d+)" + regex);
				Matcher m = p.matcher(txt);
				if (m.matches()) {
					String subs = txt.substring(m.start(1), m.end(1));
					interval = Long.valueOf(subs);
				}
			}
		}

		private Period weeks = new Period("w");
		private Period days = new Period("d");
		private Period hours = new Period("h");
		private Period minutes = new Period("m");

		@Override
		public boolean stateChanged() {
			if (!getField().isEnabled()) {
				return false;
			}

			matchFound = !super.stateChanged();

			updateOKAction();

			if (matchFound) {
				String text = getField().getText();
				weeks.findAndSet(text);
				days.findAndSet(text);
				hours.findAndSet(text);
				minutes.findAndSet(text);
			}

			return !matchFound;
		}

		public long getWeeks() {
			return weeks.interval;
		}

		public long getDays() {
			return days.interval;
		}

		public long getHours() {
			return hours.interval;
		}

		public long getMinutes() {
			return minutes.interval;
		}

		public boolean isOk() {
			return !matchFound;
		}
	}

	private void createUIComponents() {
	}

	private void updateOKAction() {
		boolean enable = timeSpentListener.isOk();
		if (remainingEstimateField.isEnabled() && btnUpdateManually.isSelected() && enable) {
			enable = remainingEstimateListener.isOk();
		}
        if (deactivateActiveIssue) {
            enable = enable || !chkLogWork.isSelected();
            enable = enable && actionCombo.getItemCount() > 0;
        }
		setOKActionEnabled(enable);
	}

    @Override
    protected void doOKAction() {
        if (deactivateActiveIssue) {
            config.setActiveIssueLogWork(chkLogWork.isSelected());
            if (actionCombo != null && actionCombo.getSelectedItem() != null) {
                config.setSelectedWorkflowAction(((JIRAAction) actionCombo.getSelectedItem()).getId());
            }
            config.setActiveIssueCommitChanges(chkCommitChanges.isSelected());
            config.setActiveIssueProgressWorkflowAction(cbPerformWorkflowAction.isSelected());
            config.setActiveIssueAfterCommit(btnChangeSetDoNothing.isSelected() ? AfterCommit.DO_NOTHING.ordinal()
                    : (btnChangeSetDeactivate.isSelected()
                        ? AfterCommit.DEACTIVATE_CHANGESET.ordinal() : AfterCommit.REMOVE_CHANGESET.ordinal()));
        }
        super.doOKAction();
    }

    private static final long MILLIS_IN_HOUR = 1000 * 3600;
	private static final long MILLIS_IN_MINUTE = 1000 * 60;
	private static final long MAX_ALLOWED_HOURS = 5;

	@NotNull
	public static String getFormatedDurationString(Date startTime) {
		String result = "";

		Date currentTime = new Date();
		long timediff = currentTime.getTime() - startTime.getTime();
		if (timediff <= 0) {
			return result;
		}

		long hours = timediff / MILLIS_IN_HOUR;

		// if somebody works without a break for more than 5 hours, then they are mutants and I don't serve mutants :)
		if (hours < MAX_ALLOWED_HOURS) {
			if (hours > 0) {
				result += Long.valueOf(hours).toString() + "h";
			}

			long minutes = (timediff % MILLIS_IN_HOUR) / MILLIS_IN_MINUTE;

			if (minutes > 0) {
				if (hours > 0) {
					result += " ";
				}
				result += Long.valueOf(minutes).toString() + "m";
			}
		}
		return result;
	}

	public WorkLogCreateAndMaybeDeactivateDialog(final ServerData jiraServer, final JIRAIssue issue,
			final Project project, final String timeSpent,
			boolean deactivateActiveIssue, @NotNull final JiraWorkspaceConfiguration config) {

		super(false);
        this.issue = issue;

        this.project = project;
		this.deactivateActiveIssue = deactivateActiveIssue;
        this.config = config;

        facade = JIRAServerFacadeImpl.getInstance();

		setupUI();

		if (deactivateActiveIssue) {
			setTitle("Stop Work on Issue " + issue.getKey());

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ChangeListManager changeListManager = ChangeListManager.getInstance(project);
					LocalChangeList chList = changeListManager.getDefaultChangeList();

					changesBrowserPanel = IdeaVersionFacade.getInstance()
							.getChangesListBrowser(project, changeListManager, chList.getChanges());
					changesPanel.add(changesBrowserPanel, BorderLayout.CENTER);
					changesPanel.validate();
				}
			});

			getOKAction().putValue(Action.NAME, "Stop Work");
		} else {
			setTitle("Add Worklog for " + issue.getKey());
			getOKAction().putValue(Action.NAME, "Add Worklog");
		}
		setOKActionEnabled(false);

		timeSpentField.setText(timeSpent);

		Date startProgressTimestamp = JIRAIssueProgressTimestampCache.getInstance().getTimestamp(
				jiraServer, issue);
		
		if (startProgressTimestamp != null) {
			timeSpentField.setText(getFormatedDurationString(startProgressTimestamp));
		}

		final Calendar now = Calendar.getInstance();
		endTime = now.getTime();

		endDateLabel.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(now.getTime()));

		endDateChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					//we catch NPE because of bug in  CalendarPanel.java method  private boolean isShowing(Date date);
					//null minDate. minDate sometines is not initialized see PL-1105
					TimeDatePicker tdp = new TimeDatePicker(endTime);
					if (tdp.isOK()) {
						endTime = tdp.getSelectedDateTime();
						String s = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(endTime);
						endDateLabel.setText(s);
					}
				} catch (NullPointerException npe) {
					PluginUtil.getLogger().error("Cannot create TimeDatePicker object, NPE: " + npe.getMessage());
				}
			}
		});

		btnUpdateManually.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				boolean b = btnUpdateManually.isSelected();
				remainingEstimateField.setEnabled(b);
				updateOKAction();
			}
		});

		if (jiraServer == null) {
			Messages.showErrorDialog(project, "There is no selected JIRA Server", "Error");
			return;
		}

		chkLogWork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UIUtil.setEnabled(timePanel, chkLogWork.isSelected(), true);
                timeSpentListener.stateChanged();
                remainingEstimateListener.stateChanged();
                updateOKAction();
				if (timePanel.isEnabled()) {
					remainingEstimateField.setEnabled(btnUpdateManually.isSelected());
				}
			}
		});
		if (deactivateActiveIssue) {
			chkCommitChanges.addActionListener((new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					UIUtil.setEnabled(changesetPanel, chkCommitChanges.isSelected(), true);
				}
			}));
		}

		init();
		setCommentText();
		validate();

		timeSpentListener = new WdhmInputListener(timeSpentField);
		remainingEstimateListener = new WdhmInputListener(remainingEstimateField);

		timeSpentField.getDocument().addDocumentListener(timeSpentListener);
		remainingEstimateField.getDocument().addDocumentListener(remainingEstimateListener);

		timeSpentListener.stateChanged();
		remainingEstimateListener.stateChanged();

		updateOKAction();
	}

	private void setCommentText() {
		if (deactivateActiveIssue) {
			ChangeListManager changeListManager = ChangeListManager.getInstance(project);
			LocalChangeList chList = changeListManager.getDefaultChangeList();
            if (chList != null) {
                comment.setText(chList.getComment());
            } else {
                comment.setText("");
            }
        }
	}

	public boolean isLogTime() {
		return chkLogWork.isSelected();
	}

	public boolean isCommitChanges() {
		return chkCommitChanges.isSelected();
	}

	public AfterCommit getAfterCommitChangeSetAction() {
        if (btnChangeSetDeactivate.isSelected()) {
            return AfterCommit.DEACTIVATE_CHANGESET;
        } else if (btnChangeSetRemove.isSelected()) {
            return AfterCommit.REMOVE_CHANGESET;
        }
        return AfterCommit.DO_NOTHING;
	}

	public LocalChangeList getCurrentChangeList() {
		return (LocalChangeList) changesBrowserPanel.getSelectedChangeList();
	}

	public java.util.List<Change> getSelectedChanges() {
		return changesBrowserPanel.getCurrentIncludedChanges();
	}

	public String getTimeSpentString() {
		return timeSpentField.getText();
	}

	public String getRemainingEstimateString() {
		return remainingEstimateField.getText();
	}

	public Date getEndDate() {
		return (Date) endTime.clone();
	}

	public Date getStartDate() {
		Date d = endTime;
		long t = d.getTime() - (timeSpentListener.getWeeks() * Timer.ONE_WEEK) - (timeSpentListener.getDays() * Timer.ONE_DAY)
				- (timeSpentListener.getHours() * Timer.ONE_HOUR) - (timeSpentListener.getMinutes() * Timer.ONE_MINUTE);
		d.setTime(t);
		return d;
	}

	public String getComment() {
		return comment.getText();
	}

	public boolean getAutoUpdateRemaining() {
		return btnAutoUpdate.isSelected();
	}

	public boolean getLeaveRemainingUnchanged() {
		return btnLeaveUnchanged.isSelected();
	}

	public boolean getUpdateRemainingManually() {
		return btnUpdateManually.isSelected();
	}

    public JIRAAction getSelectedAction() {
        if (!cbPerformWorkflowAction.isSelected() || actionCombo.getItemCount() == 0) {
            return null;
        }
        return (JIRAAction) actionCombo.getSelectedItem();
    }

	@Override
	protected JComponent createCenterPanel() {
		return contentPane;
	}

	private class TimeDatePicker extends DatePicker {

		private JSpinner hour = new JSpinner();
		private JSpinner minute = new JSpinner();
		private SpinnerNumberModel hourModel;
		private SpinnerNumberModel minuteModel;

		TimeDatePicker(Date now) {
			super("Set End Time", now);
			init();

			Calendar nowcal = Calendar.getInstance();
			nowcal.setTime(now);
			nowcal.set(Calendar.HOUR_OF_DAY, 0);
			nowcal.set(Calendar.MINUTE, 0);
			nowcal.set(Calendar.SECOND, 0);
			nowcal.set(Calendar.MILLISECOND, 0);

			Calendar cal = Calendar.getInstance();
			cal.setTime(now);
			hourModel = new SpinnerNumberModel(cal.get(Calendar.HOUR_OF_DAY), 0, 24, 1);
			minuteModel = new SpinnerNumberModel(cal.get(Calendar.MINUTE), 0, 60, 1);
			hour.setModel(hourModel);
			minute.setModel(minuteModel);

			GridBagConstraints gbc = new GridBagConstraints();

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.weightx = 0.5;
			getPanelComponent().add(new JLabel("hour", SwingConstants.CENTER), gbc);
			gbc.gridy = 0;
			getPanelComponent().add(hour, gbc);

			gbc.gridx = 1;
			gbc.gridy = 0;
			getPanelComponent().add(new JLabel("minute", SwingConstants.CENTER), gbc);
			gbc.gridy = 1;
			getPanelComponent().add(minute, gbc);

			show();
		}

		public Date getSelectedDateTime() {
			Date d = getSelectedDate();
			long newTime = d.getTime();
			newTime += hourModel.getNumber().intValue() * Timer.ONE_HOUR;
			newTime += minuteModel.getNumber().intValue() * Timer.ONE_MINUTE;
			d.setTime(newTime);
			return d;
		}
	}

}
