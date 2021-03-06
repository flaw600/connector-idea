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

import com.atlassian.theplugin.commons.cfg.JiraServerCfg;
import com.atlassian.theplugin.commons.cfg.UserCfg;
import com.atlassian.theplugin.commons.jira.JiraServerFacade;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

/**
 * Plugin configuration form.
 */
public class JiraServerConfigForm {

    private JPanel rootComponent;

    private transient GenericServerConfigForm genericServerConfigForm;
    private JCheckBox cbUseSessionCookies;
    private JPanel panelBasic;
    private final Project project;
    private final UserCfg defaultUser;
    private final transient JiraServerFacade jiraServerFacade;

    public JiraServerCfg getJiraServerCfg() {
        return jiraServerCfg;
    }

    private JiraServerCfg jiraServerCfg;

    public JiraServerConfigForm(Project project, final UserCfg defaultUser, JiraServerFacade jiraServerFacade) {
        this.project = project;
        this.defaultUser = defaultUser;
        this.jiraServerFacade = jiraServerFacade;
        $$$setupUI$$$();
       cbUseSessionCookies.setSelected(false);
    }

    private void enableDisableSessionCookies(AbstractButton button) {
        ButtonModel buttonModel = button.getModel();
    }

    public void setData(@NotNull final JiraServerCfg serverCfg) {
        jiraServerCfg = serverCfg;
        cbUseSessionCookies.setSelected(serverCfg.isUseSessionCookies());
        genericServerConfigForm.setData(serverCfg);
    }

    public void finalizeData() {
        genericServerConfigForm.finalizeData();
    }

    public void saveData() {
        genericServerConfigForm.saveData();
        if (jiraServerCfg != null) {
            jiraServerCfg.setDontUseBasicAuth(true);
            jiraServerCfg.setUseSessionCookies(cbUseSessionCookies.isSelected());
        }
    }

    public JComponent getRootComponent() {
        return rootComponent;
    }

    public void setVisible(boolean visible) {
        rootComponent.setVisible(visible);
    }

    private void createUIComponents() {
        genericServerConfigForm =
                new GenericServerConfigForm(project, defaultUser, new ProductConnector(jiraServerFacade));
        cbUseSessionCookies = new JCheckBox("Do not use HTTP authentication");
    }

    // CHECKSTYLE:OFF

    // CHECKSTYLE:ON

    // for use by unit test only

    public GenericServerConfigForm getGenericServerConfigForm() {
        return genericServerConfigForm;
    }


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
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FormLayout("fill:262px:noGrow", "top:26px:noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(1, 80), null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Additional Configuration"));
        cbUseSessionCookies.setText("Do not use HTTP authentication");
        CellConstraints cc = new CellConstraints();
        panel2.add(cbUseSessionCookies, new CellConstraints(1, 1, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(0, 12, 0, 0)));
        final JTextArea textArea1 = new JTextArea();
        textArea1.setEditable(false);
        textArea1.setFont(new Font(textArea1.getFont().getName(), textArea1.getFont().getStyle(), 10));
        textArea1.setRows(2);
        textArea1.setText("Check this box if you are using NTLM \nauthentiaction method or your  JIRA \nserver is located behind a proxy");
        textArea1.setWrapStyleWord(true);
        panel2.add(textArea1, new CellConstraints(1, 3, 1, 1, CellConstraints.LEFT, CellConstraints.FILL, new Insets(0, 40, 0, 0)));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootComponent;
    }
}