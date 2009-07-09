package com.atlassian.theplugin.idea.config.serverconfig;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import static com.intellij.openapi.ui.Messages.showMessageDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.ProgressManager;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.atlassian.theplugin.commons.cfg.*;
import com.atlassian.theplugin.commons.ServerType;
import com.atlassian.theplugin.commons.remoteapi.ServerData;
import com.atlassian.theplugin.commons.crucible.CrucibleServerFacadeImpl;
import com.atlassian.theplugin.commons.jira.JIRAServerFacadeImpl;
import com.atlassian.theplugin.idea.TestConnectionTask;
import com.atlassian.theplugin.idea.TestConnectionProcessor;
import com.atlassian.theplugin.idea.util.IdeaUiMultiTaskExecutor;
import com.atlassian.theplugin.idea.ui.DialogWithDetails;
import com.atlassian.theplugin.idea.config.serverconfig.util.ServerNameUtil;
import com.atlassian.theplugin.ConnectionWrapper;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.*;

import org.jetbrains.annotations.NotNull;

/**
 * User: kalamon
 * Date: Jul 8, 2009
 * Time: 3:03:34 PM
 */
public class JiraStudioConfigDialog extends DialogWrapper {
    private ConfigPanel rootPanel;
    private JTextField serverName;
    private JTextField serverUrl;
    private JTextField userName;
    private JPasswordField password;
    private JCheckBox rememberPassword;
    private JButton testConnection;
    private JCheckBox useDefaultCredentials;
    private DocumentListener documentListener;
    private Project project;
    private ServerTreePanel serverTree;
    private UserCfg defaultUser;

    protected JiraStudioConfigDialog(Project project, ServerTreePanel serverTree,
                                     UserCfg defaultUser, Collection<ServerCfg> servers) {
        super(project, false);
        this.project = project;

        this.serverTree = serverTree;
        this.defaultUser = defaultUser;

        setTitle("Create JIRA Studio Server");

        documentListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent documentEvent) {
                updateButtons();
            }

            public void removeUpdate(DocumentEvent documentEvent) {
                updateButtons();
            }

            public void changedUpdate(DocumentEvent documentEvent) {
                updateButtons();
            }
        };

        serverName = new JTextField(ServerNameUtil.suggestNewName(servers));
        serverName.getDocument().addDocumentListener(documentListener);
        serverUrl = new JTextField();
        serverUrl.getDocument().addDocumentListener(documentListener);
        serverUrl.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent focusEvent) {
                serverUrl.getDocument().removeDocumentListener(documentListener);
                String url = GenericServerConfigForm.adjustUrl(serverUrl.getText());
                serverUrl.setText(url);
                serverUrl.getDocument().addDocumentListener(documentListener);
            }
        });
        userName = new JTextField();
        password = new JPasswordField();
        rememberPassword = new JCheckBox("Remember Password");
        testConnection = new JButton("Test Connection");
        testConnection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                testServerConnections();
            }
        });
        useDefaultCredentials = new JCheckBox("Use Default Credentials");

        rootPanel = new ConfigPanel();

        updateButtons();

        init();
    }

    private Map<String, Throwable> connectionErrors = new HashMap<String, Throwable>();

    private void testServerConnections() {

        TestConnectionProcessor processor = new TestConnectionProcessor() {
            // kalamon: ok, I know, this counter is lame as hell. But so what,
            // it is "the simplest thing that could ever work". And I challenge
            // you to invent something more clever that does not result in
            // overcomplication of the code
            private int counter = 1;

            public void setConnectionResult(ConnectionWrapper.ConnectionState result) {
            }

            public void onSuccess() {
                if (counter-- > 0) {
                    testCrucibleConnection(this);
                } else {
                    showResultDialog();
                }
            }

            public void onError(String errorMessage, Throwable exception, String helpUrl) {
                connectionErrors.put(errorMessage, exception);
                if (counter-- > 0) {
                    testCrucibleConnection(this);
                } else {
                    showResultDialog();
                }
            }
        };

        connectionErrors.clear();
        testJiraConnection(processor);
    }

    private void showResultDialog() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (connectionErrors.size() > 0) {
                    List<IdeaUiMultiTaskExecutor.ErrorObject> errors = new ArrayList<IdeaUiMultiTaskExecutor.ErrorObject>();
                    for (String error : connectionErrors.keySet()) {
                        errors.add(new IdeaUiMultiTaskExecutor.ErrorObject(error, connectionErrors.get(error)));
                    }
                    DialogWithDetails.showExceptionDialog(rootPanel, errors);
                } else {
                    showMessageDialog(project, "Connected successfully", "Connection OK", Messages.getInformationIcon());
                }
            }
        });
    }

    private void testJiraConnection(final TestConnectionProcessor processor) {
        final Task.Modal testConnectionTask = new TestConnectionTask(project,
                new ProductConnector(JIRAServerFacadeImpl.getInstance()),
                new ServerData(generateJiraServerCfg(), defaultUser),
                processor, "Testing JIRA Connection", true, false, false);
        testConnectionTask.setCancelText("Stop");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ProgressManager.getInstance().run(testConnectionTask);
            }
        });
    }

    private void testCrucibleConnection(final TestConnectionProcessor processor) {
        final Task.Modal testConnectionTask = new TestConnectionTask(project,
                new ProductConnector(CrucibleServerFacadeImpl.getInstance()),
                new ServerData(generateCrucibleServerCfg(), defaultUser),
                processor, "Testing Crucible Connection", true, false, false);
        testConnectionTask.setCancelText("Stop");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ProgressManager.getInstance().run(testConnectionTask);
            }
        });
    }

    protected JComponent createCenterPanel() {
        return rootPanel;
    }

    @Override
    protected Action[] createActions() {
        return new Action[] {
                getOKAction(),
                getCancelAction()
        };
    }

    @Override
    protected void doOKAction() {
        serverUrl.getDocument().removeDocumentListener(documentListener);
        GenericServerConfigForm.adjustUrl(serverUrl.getText());
        generateAllStudioServers();
        super.doOKAction();
    }

    private @NotNull JiraServerCfg generateJiraServerCfg() {
        ServerIdImpl idJira = new ServerIdImpl();

        String name = serverName.getText().trim();
        JiraServerCfg jira = new JiraServerCfg(true, name, idJira);

        jira.setUrl(serverUrl.getText());

        String user = userName.getText();
        if (user.length() > 0) {
            jira.setUsername(user);
        }
        jira.setPassword(new String(password.getPassword()));
        jira.setPasswordStored(rememberPassword.isSelected());
        jira.setUseDefaultCredentials(useDefaultCredentials.isSelected());

        return jira;
    }

    private @NotNull CrucibleServerCfg generateCrucibleServerCfg() {
        ServerIdImpl idCrucible = new ServerIdImpl();

        String name = serverName.getText().trim();
        CrucibleServerCfg cru = new CrucibleServerCfg(true, name, idCrucible);

        cru.setUrl(serverUrl.getText() + "/source");

        String user = userName.getText();
        if (user.length() > 0) {
            cru.setUsername(user);
        }
        cru.setPassword(new String(password.getPassword()));
        cru.setPasswordStored(rememberPassword.isSelected());
        cru.setUseDefaultCredentials(useDefaultCredentials.isSelected());
        cru.setFisheyeInstance(true);

        return cru;
    }

    private void generateAllStudioServers() {
        serverTree.addNewServerCfg(ServerType.JIRA_SERVER, generateJiraServerCfg());
        serverTree.addNewServerCfg(ServerType.CRUCIBLE_SERVER, generateCrucibleServerCfg());
    }

    private class ConfigPanel extends JPanel {

        private ConfigPanel() {
            setLayout(new FormLayout("3dlu, right:pref, 3dlu, fill:pref:grow, right:pref, 3dlu",
                    "3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu"));
            CellConstraints cc = new CellConstraints();

            //CHECKSTYLE:MAGIC:OFF
            add(new JLabel("Server Name:"), cc.xy(2, 2));
            add(serverName, cc.xyw(4, 2, 2));
            add(new JLabel("Server URL:"), cc.xy(2, 4));
            add(serverUrl, cc.xyw(4, 4, 2));
            add(new JLabel("Username:"), cc.xy(2, 6));
            add(userName, cc.xyw(4, 6, 2));
            add(new JLabel("Password:"), cc.xy(2, 8));
            add(password, cc.xyw(4, 8, 2));
            add(rememberPassword, cc.xy(4, 10));
            add(testConnection, cc.xy(5, 10));
            add(useDefaultCredentials, cc.xy(4, 12));
            //CHECKSTYLE:MAGIC:ON
        }
    }

    private void updateButtons() {
        boolean enabled =
                serverName.getText().trim().length() > 0
                && serverUrl.getText().trim().length() > 0;
        setOKActionEnabled(enabled);
        testConnection.setEnabled(enabled);
    }
}