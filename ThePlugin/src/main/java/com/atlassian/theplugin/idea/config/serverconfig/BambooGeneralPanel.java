package com.atlassian.theplugin.idea.config.serverconfig;

import com.atlassian.theplugin.configuration.BambooTooltipOption;
import com.atlassian.theplugin.configuration.BambooConfigurationBean;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;

public class BambooGeneralPanel extends JPanel {
	private JRadioButton allFailuresFirstSuccess;
	private JRadioButton firstFailureFirstSuccess;
	private JRadioButton never;
	private JPanel rootComponent;
	private JSpinner pollTimeSpinner;
	private SpinnerModel model;

	private BambooConfigurationBean bambooConfiguration;

	public BambooGeneralPanel() {
		$$$setupUI$$$();
		model = new SpinnerNumberModel(1, 1, 1000, 1);
		pollTimeSpinner.setModel(model);
		add(rootComponent);
	}


	public void setData(BambooConfigurationBean bambooConfiguration) {
		this.bambooConfiguration = new BambooConfigurationBean(bambooConfiguration);
		BambooTooltipOption configOption = this.bambooConfiguration.getBambooTooltipOption();

		if (configOption != null) {
			switch (configOption) {
				case ALL_FAULIRES_AND_FIRST_SUCCESS:
					allFailuresFirstSuccess.setSelected(true);
					break;
				case FIRST_FAILURE_AND_FIRST_SUCCESS:
					firstFailureFirstSuccess.setSelected(true);
					break;
				case NEVER:
					never.setSelected(true);
					break;
				default:
					never.setSelected(true);
					break;
			}
		} else {
			setDefault();
			configOption = getDefault();
		}
		model.setValue(new Integer(bambooConfiguration.getPollTime()));
	}

	public BambooConfigurationBean getData() {
		bambooConfiguration.setBambooTooltipOption(getBambooTooltipOption());
		bambooConfiguration.setPollTime(((Integer) model.getValue()).intValue());
		return bambooConfiguration;
	}

	private BambooTooltipOption getBambooTooltipOption() {
		if (allFailuresFirstSuccess.isSelected()) {
			return BambooTooltipOption.ALL_FAULIRES_AND_FIRST_SUCCESS;
		} else if (firstFailureFirstSuccess.isSelected()) {
			return BambooTooltipOption.FIRST_FAILURE_AND_FIRST_SUCCESS;
		} else if (never.isSelected()) {
			return BambooTooltipOption.NEVER;
		} else {
			return getDefault();
		}
	}

	public boolean isModified() {
		if (bambooConfiguration.getBambooTooltipOption() != null) {
			if (bambooConfiguration.getBambooTooltipOption() != getBambooTooltipOption()) {
				return true;
			}
		}
		if (((Integer) model.getValue()).intValue() != bambooConfiguration.getPollTime()) {
			return true;
		}

		return false;
	}

	private void setDefault() {
		allFailuresFirstSuccess.setSelected(true);
	}

	private BambooTooltipOption getDefault() {
		return BambooTooltipOption.ALL_FAULIRES_AND_FIRST_SUCCESS;
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
		rootComponent.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:d:grow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow"));
		allFailuresFirstSuccess = new JRadioButton();
		allFailuresFirstSuccess.setSelected(true);
		allFailuresFirstSuccess.setText("All build failures and first build success");
		CellConstraints cc = new CellConstraints();
		rootComponent.add(allFailuresFirstSuccess, cc.xy(3, 1));
		firstFailureFirstSuccess = new JRadioButton();
		firstFailureFirstSuccess.setText("First build failure and first build success");
		rootComponent.add(firstFailureFirstSuccess, cc.xy(3, 3));
		never = new JRadioButton();
		never.setText("Never");
		rootComponent.add(never, cc.xy(3, 5));
		final Spacer spacer1 = new Spacer();
		rootComponent.add(spacer1, cc.xy(3, 9, CellConstraints.DEFAULT, CellConstraints.FILL));
		final Spacer spacer2 = new Spacer();
		rootComponent.add(spacer2, cc.xy(5, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
		final JLabel label1 = new JLabel();
		label1.setText("Show tooltip:");
		rootComponent.add(label1, cc.xy(1, 1));
		final JLabel label2 = new JLabel();
		label2.setText("Polling time [minutes]");
		rootComponent.add(label2, cc.xy(1, 7));
		pollTimeSpinner = new JSpinner();
		rootComponent.add(pollTimeSpinner, cc.xy(3, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
		ButtonGroup buttonGroup;
		buttonGroup = new ButtonGroup();
		buttonGroup.add(allFailuresFirstSuccess);
		buttonGroup.add(firstFailureFirstSuccess);
		buttonGroup.add(never);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootComponent;
	}
}
