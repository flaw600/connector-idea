using System;
using Extensibility;
using EnvDTE;
using EnvDTE80;
using Microsoft.VisualStudio.CommandBars;
using System.Resources;
using System.Reflection;
using System.Globalization;
using System.Windows.Forms;
using System.Collections;
using System.Diagnostics;
using System.Drawing;

using PaZu.models;

namespace PaZu
{
    /// <summary>The object for implementing an Add-in.</summary>
	/// <seealso class='IDTExtensibility2' />
	public class Connect : IDTExtensibility2, IDTCommandTarget
	{
        private SolutionEvents solutionEvents;

        private Command jiraCommand;
        private EnvDTE.Window jiraWindow;
        private DTE2 _applicationObject;
        private AddIn _addInInstance;

		/// <summary>Implements the constructor for the Add-in object. Place your initialization code within this method.</summary>
		public Connect()
		{
		}

		/// <summary>Implements the OnConnection method of the IDTExtensibility2 interface. Receives notification that the Add-in is being loaded.</summary>
		/// <param term='application'>Root object of the host application.</param>
		/// <param term='connectMode'>Describes how the Add-in is being loaded.</param>
		/// <param term='addInInst'>Object representing this Add-in.</param>
		/// <seealso class='IDTExtensibility2' />
		public void OnConnection(object application, ext_ConnectMode connectMode, object addInInst, ref Array custom)
		{
			_applicationObject = (DTE2)application;
			_addInInstance = (AddIn)addInInst;
			switch (connectMode)
            {
                case ext_ConnectMode.ext_cm_Startup:
                    solutionEvents = _applicationObject.Events.SolutionEvents;
                    solutionEvents.Opened += new _dispSolutionEvents_OpenedEventHandler(SolutionEvents_Opened);
                    solutionEvents.BeforeClosing += new _dispSolutionEvents_BeforeClosingEventHandler(SolutionEvents_BeforeClosing);
                    break;
                case ext_ConnectMode.ext_cm_UISetup:
                case ext_ConnectMode.ext_cm_AfterStartup:
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
            setupMenu(false);
		}

        public void SolutionEvents_Opened()
        {
            JiraServerModel.Instance.clear();
            JiraServerModel.Instance.load(_applicationObject.Solution.Globals);
            createJiraWindow();
        }

        public void SolutionEvents_BeforeClosing()
        {
            if (jiraWindow != null)
            {
                JiraServerModel.Instance.save(_applicationObject.Solution.Globals);
                JiraIssueListModel.Instance.removeAllListeners();
                jiraWindow.Visible = false;
                jiraWindow.Close(vsSaveChanges.vsSaveChangesYes);
                jiraWindow = null;
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
		public void QueryStatus(string commandName, vsCommandStatusTextWanted neededText, ref vsCommandStatus status, ref object commandText)
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
                        status = (vsCommandStatus)vsCommandStatus.vsCommandStatusSupported | vsCommandStatus.vsCommandStatusEnabled;
                    }
                    else
                    {
                        status = (vsCommandStatus)vsCommandStatus.vsCommandStatusSupported ;
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
		public void Exec(string commandName, vsCommandExecOption executeOption, ref object varIn, ref object varOut, ref bool handled)
		{
			handled = false;
			if(executeOption == vsCommandExecOption.vsCommandExecOptionDoDefault)
			{
                if (commandName == "PaZu.Connect.PaZuShowHide" && jiraWindow != null)
				{
                    jiraWindow.Visible = !jiraWindow.Visible;
                    handled = true;
					return;
				}
			}
		}

        private void createJiraWindow()
        {
            try
            {
                String guid = "{CB0B2DD2-8849-431d-B92D-E86B97115345}";

                object obj = null;
                EnvDTE80.Windows2 windows2 = (EnvDTE80.Windows2)_applicationObject.Windows;
                string loc = System.Reflection.Assembly.GetExecutingAssembly().Location;
                jiraWindow = windows2.CreateToolWindow2(_addInInstance, loc, "PaZu.PaZuWindow", "Atlassian", guid, ref obj);

                // todo: this does not work - need to figure out why. Probably need to load bitmap from a satellite DLL
                // it is all sort of very stupid, no obvious reason for the failure and the exception (ArgumentException)
                // does not explain anything

                //Bitmap bmp = Properties.Resources.ide_plugin_161;
                //jiraWindow.SetTabPicture(bmp.GetHbitmap());

                jiraWindow.Visible = true;
            }
            catch (System.Exception e)
            {
                MessageBox.Show("Creation of the Atlassian Connector window failed \n\n" + e.ToString());
            }
        }

        private void setupMenu(bool show)
        {
            object[] contextGUIDS = new object[] { };
            Commands2 commands = (Commands2)_applicationObject.Commands;
            string toolsMenuName;

            try
            {
                string resourceName;
                ResourceManager resourceManager = new ResourceManager("PaZu.CommandBar", Assembly.GetExecutingAssembly());
                CultureInfo cultureInfo = new CultureInfo(_applicationObject.LocaleID);

                if (cultureInfo.TwoLetterISOLanguageName == "zh")
                {
                    System.Globalization.CultureInfo parentCultureInfo = cultureInfo.Parent;
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
                ((Microsoft.VisualStudio.CommandBars.CommandBars)_applicationObject.CommandBars)["MenuBar"];

            CommandBarControl toolsControl = menuBarCommandBar.Controls[toolsMenuName];
            CommandBarPopup toolsPopup = (CommandBarPopup)toolsControl;

            try
            {
                // todo: need to add custom icon instead of the icon 487 from VS
                // and also change vsCommandStyle.vsCommandStyleText to  
                // vsCommandStyle.vsCommandStylePictAndText
                jiraCommand = commands.AddNamedCommand2(
                    _addInInstance, "PaZuShowHide", "Toggle Atlassian Connector Window", "Shows or hides Atlassian Connector window", true, 487,
                    ref contextGUIDS, (int)vsCommandStatus.vsCommandStatusSupported + (int)vsCommandStatus.vsCommandStatusEnabled,
                    (int)vsCommandStyle.vsCommandStyleText, vsCommandControlType.vsCommandControlTypeButton);

                if ((jiraCommand != null) && (toolsPopup != null))
                {
                    jiraCommand.AddControl(toolsPopup.CommandBar, 1);
                }
            }
            catch (System.ArgumentException e)
            {
                Debug.WriteLine(e.Message);
            }
        }
	}
}