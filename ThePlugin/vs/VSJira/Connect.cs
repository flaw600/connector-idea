using System;
using Extensibility;
using EnvDTE;
using EnvDTE80;
using Microsoft.VisualStudio.CommandBars;
using System.Resources;
using System.Reflection;
using System.Globalization;
using System.Windows.Forms;
using System.Diagnostics;
using PaZu.models;

namespace PaZu
{
    /// <summary>The object for implementing an Add-in.</summary>
	/// <seealso class='IDTExtensibility2' />
	public class Connect : IDTExtensibility2, IDTCommandTarget
	{
        private SolutionEvents solutionEvents;

        private Command jiraCommand;
        private Window jiraWindow;
        private DTE2 applicationObject;
        private AddIn addInInstance;

        /// <summary>Implements the OnConnection method of the IDTExtensibility2 interface. Receives notification that the Add-in is being loaded.</summary>
		/// <param term='application'>Root object of the host application.</param>
		/// <param term='connectMode'>Describes how the Add-in is being loaded.</param>
		/// <param term='addInInst'>Object representing this Add-in.</param>
		/// <seealso class='IDTExtensibility2' />
		public void OnConnection(object application, ext_ConnectMode connectMode, object addInInst, ref Array custom)
		{
			applicationObject = (DTE2)application;
			addInInstance = (AddIn)addInInst;
			switch (connectMode)
            {
                case ext_ConnectMode.ext_cm_Startup:
                    solutionEvents = applicationObject.Events.SolutionEvents;
                    solutionEvents.Opened += solutionEventsOpened;
                    solutionEvents.BeforeClosing += solutionEventsBeforeClosing;
                    createIssueDetailsWindow();
                    break;
                default:
                    break;
			}
		}

		/// <summary>Implements the OnDisconnection method of the IDTExtensibility2 interface. Receives notification that the Add-in is being unloaded.</summary>
		/// <param term='disconnectMode'>Describes how the Add-in is being unloaded.</param>
		/// <param term='custom'>Array of parameters that are host application specific.</param>
		/// <seealso class='IDTExtensibility2' />
		public void OnDisconnection(ext_DisconnectMode disconnectMode, ref Array custom)
		{
		}

		/// <summary>Implements the OnAddInsUpdate method of the IDTExtensibility2 interface. Receives notification when the collection of Add-ins has changed.</summary>
		/// <param term='custom'>Array of parameters that are host application specific.</param>
		/// <seealso class='IDTExtensibility2' />		
		public void OnAddInsUpdate(ref Array custom)
		{
		}

		/// <summary>Implements the OnStartupComplete method of the IDTExtensibility2 interface. Receives notification that the host application has completed loading.</summary>
		/// <param term='custom'>Array of parameters that are host application specific.</param>
		/// <seealso class='IDTExtensibility2' />
		public void OnStartupComplete(ref Array custom)
		{
            setupMenu();
		}

        public void solutionEventsOpened()
        {
            try
            {
                JiraServerModel.Instance.clear();
                JiraServerModel.Instance.load(applicationObject.Solution.Globals);
                RecentlyViewedIssuesModel.Instance.load(applicationObject.Globals, applicationObject.Solution.FullName);
                JiraCustomFilter.load(applicationObject.Globals, applicationObject.Solution.FullName);
                createJiraWindow();
                IssueDetailsWindow.Instance.Solution = applicationObject.Solution;
            }
            catch (Exception e)
            {
                Debug.WriteLine(e);
            }
        }

        public void solutionEventsBeforeClosing()
        {
            try
            {
                if (jiraWindow == null) return;
                JiraServerModel.Instance.save(applicationObject.Solution.Globals);
                JiraCustomFilter.save(applicationObject.Globals, applicationObject.Solution.FullName);
                JiraIssueListModel.Instance.removeAllListeners();
                jiraWindow.Visible = false;
                jiraWindow.Close(vsSaveChanges.vsSaveChangesYes);
                jiraWindow = null;
                RecentlyViewedIssuesModel.Instance.save(applicationObject.Globals, applicationObject.Solution.FullName);
                IssueDetailsWindow.Instance.clearAllIssues();
                IssueDetailsWindow.Instance.WindowInstance.Visible = false;
            }
            catch (Exception e)
            {
                Debug.WriteLine(e);
            }
        }

		/// <summary>Implements the OnBeginShutdown method of the IDTExtensibility2 interface. Receives notification that the host application is being unloaded.</summary>
		/// <param term='custom'>Array of parameters that are host application specific.</param>
		/// <seealso class='IDTExtensibility2' />
		public void OnBeginShutdown(ref Array custom)
		{
            if (jiraWindow != null)
            {
                jiraWindow.Close(vsSaveChanges.vsSaveChangesYes);
            }
        }
		
		/// <summary>Implements the QueryStatus method of the IDTCommandTarget interface. This is called when the command's availability is updated</summary>
		/// <param term='commandName'>The name of the command to determine state for.</param>
		/// <param term='neededText'>Text that is needed for the command.</param>
		/// <param term='status'>The state of the command in the user interface.</param>
		/// <param term='commandText'>Text requested by the neededText parameter.</param>
		/// <seealso class='Exec' />
		public void QueryStatus(string commandName, vsCommandStatusTextWanted neededText, 
            ref vsCommandStatus status, ref object commandText)
		{
            if (commandName != "PaZu.Connect.PaZuShowHide")
            {
                return;
            }
            switch (neededText)
            {
                case vsCommandStatusTextWanted.vsCommandStatusTextWantedNone:
                    if (jiraWindow != null)
                    {
// ReSharper disable BitwiseOperatorOnEnumWihtoutFlags
                        status = vsCommandStatus.vsCommandStatusSupported | vsCommandStatus.vsCommandStatusEnabled;
// ReSharper restore BitwiseOperatorOnEnumWihtoutFlags
                    }
                    else
                    {
                        status = vsCommandStatus.vsCommandStatusSupported ;
                    }
                    break;
            }
		}

		/// <summary>Implements the Exec method of the IDTCommandTarget interface. This is called when the command is invoked.</summary>
		/// <param term='commandName'>The name of the command to execute.</param>
		/// <param term='executeOption'>Describes how the command should be run.</param>
		/// <param term='varIn'>Parameters passed from the caller to the command handler.</param>
		/// <param term='varOut'>Parameters passed from the command handler to the caller.</param>
		/// <param term='handled'>Informs the caller if the command was handled or not.</param>
		/// <seealso class='Exec' />
		public void Exec(string commandName, vsCommandExecOption executeOption, 
            ref object varIn, ref object varOut, ref bool handled)
		{
			handled = false;
		    if (executeOption != vsCommandExecOption.vsCommandExecOptionDoDefault) return;
		    if (commandName != "PaZu.Connect.PaZuShowHide" || jiraWindow == null) return;
		    jiraWindow.Visible = !jiraWindow.Visible;
		    handled = true;
		    return;
		}

        private void createJiraWindow()
        {
            try
            {
                const string guid = "{CB0B2DD2-8849-431d-B92D-E86B97115345}";

                object obj = null;
                Windows2 windows2 = (Windows2)applicationObject.Windows;
                string loc = Assembly.GetExecutingAssembly().Location;
                jiraWindow = windows2.CreateToolWindow2(addInInstance, loc, "PaZu.PaZuWindow", "Atlassian", guid, ref obj);

                // todo: this does not work - need to figure out why. Probably need to load bitmap from a satellite DLL
                // it is all sort of very stupid, no obvious reason for the failure and the exception (ArgumentException)
                // does not explain anything

                //Bitmap bmp = Properties.Resources.ide_plugin_161;
                //jiraWindow.SetTabPicture(bmp.GetHbitmap());

                jiraWindow.Visible = true;
            }
            catch (Exception e)
            {
                MessageBox.Show("Creation of the Atlassian Connector window failed \n\n" + e);
            }
        }

        private void createIssueDetailsWindow()
        {
            try
            {
                const string guid = "{0D8A8CD2-1B6F-4efe-AB9B-B295A59CB76F}";

                object obj = null;
                Windows2 windows2 = (Windows2)applicationObject.Windows;
                string loc = Assembly.GetExecutingAssembly().Location;
                Window issuesWindow = windows2.CreateToolWindow2(
                    addInInstance, loc, "PaZu.IssueDetailsWindow", "Issues - JIRA", guid, ref obj);

                IssueDetailsWindow.Instance.WindowInstance = issuesWindow;

                issuesWindow.Visible = false;
            }
            catch (Exception e)
            {
                MessageBox.Show("Creation of the issue details window failed \n\n" + e);
            }
        }

        private void setupMenu()
        {
            object[] contextGuids = new object[] { };
            Commands2 commands = (Commands2)applicationObject.Commands;
            string toolsMenuName;

            try
            {
                string resourceName;
                ResourceManager resourceManager = new ResourceManager("PaZu.CommandBar", Assembly.GetExecutingAssembly());
                CultureInfo cultureInfo = new CultureInfo(applicationObject.LocaleID);

                if (cultureInfo.TwoLetterISOLanguageName == "zh")
                {
                    CultureInfo parentCultureInfo = cultureInfo.Parent;
                    resourceName = String.Concat(parentCultureInfo.Name, "Tools");
                }
                else
                {
                    resourceName = String.Concat(cultureInfo.TwoLetterISOLanguageName, "Tools");
                }
                toolsMenuName = resourceManager.GetString(resourceName);
            }
            catch
            {
                toolsMenuName = "Tools";
            }

            Microsoft.VisualStudio.CommandBars.CommandBar menuBarCommandBar =
                ((CommandBars)applicationObject.CommandBars)["MenuBar"];

            CommandBarControl toolsControl = menuBarCommandBar.Controls[toolsMenuName];
            CommandBarPopup toolsPopup = (CommandBarPopup)toolsControl;

            try
            {
                // todo: need to add custom icon instead of the icon 487 from VS
                // and also change vsCommandStyle.vsCommandStyleText to  
                // vsCommandStyle.vsCommandStylePictAndText
                jiraCommand = commands.AddNamedCommand2(
                    addInInstance, "PaZuShowHide", "Toggle Atlassian Connector Window", "Shows or hides Atlassian Connector window", true, 487,
                    ref contextGuids, (int)vsCommandStatus.vsCommandStatusSupported + (int)vsCommandStatus.vsCommandStatusEnabled,
                    (int)vsCommandStyle.vsCommandStyleText, vsCommandControlType.vsCommandControlTypeButton);

                if ((jiraCommand != null) && (toolsPopup != null))
                {
                    jiraCommand.AddControl(toolsPopup.CommandBar, 1);
                }
            }
            catch (ArgumentException e)
            {
                Debug.WriteLine(e.Message);
            }
        }
	}
}