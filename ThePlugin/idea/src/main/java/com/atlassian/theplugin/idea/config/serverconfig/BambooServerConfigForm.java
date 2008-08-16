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

package com.atlassian.theplugin.idea.config.serverconfig;

import com.atlassian.theplugin.commons.bamboo.BambooServerFacade;
import com.atlassian.theplugin.commons.cfg.BambooServerCfg;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Plugin configuration form.
 */
public class BambooServerConfigForm {

	private JPanel rootComponent;

	private BambooPlansForm planList;

	private transient GenericServerConfigForm genericServerConfigForm;
	private final Project project;
	private final transient BambooServerFacade bambooServerFacade;

    public BambooServerCfg getBambooServerCfg() {
        return bambooServerCfg;
    }

    private BambooServerCfg bambooServerCfg;

    public BambooServerConfigForm(Project project, BambooServerFacade bambooServerFacadeInstance) {
		this.project = project;
		this.bambooServerFacade = bambooServerFacadeInstance;

		$$$setupUI$$$();
	}

    public void setData(@NotNull final BambooServerCfg serverCfg) {
        bambooServerCfg = serverCfg;
        genericServerConfigForm.setData(serverCfg);
        planList.setEnabled(!serverCfg.isUseFavourites());
        planList.setData(serverCfg);

    }


	public void saveData() {
		genericServerConfigForm.saveData();
		planList.saveData();
    }

	public boolean isModified() {
		return genericServerConfigForm.isModified() || planList.isModified();
	}


	public JComponent getRootComponent() {
		return rootComponent;
	}

	public void setVisible(boolean visible) {
		rootComponent.setVisible(visible);
	}

	private void createUIComponents() {
		genericServerConfigForm = new GenericServerConfigForm(project, new ProductConnector(bambooServerFacade));
		planList = new BambooPlansForm(bambooServerFacade, bambooServerCfg, this);
	}

	// CHECKSTYLE:OFF

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		createUIComponents();
		rootComponent = new JPanel();
		rootComponent.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		rootComponent.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel1.add(genericServerConfigForm.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));
		panel1.add(planList.$$$getRootComponent$$$(), new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootComponent;
	}

	// CHECKSTYLE:ON

	// for use by unit test only
	public GenericServerConfigForm getGenericServerConfigForm() {
		return genericServerConfigForm;
	}

}
