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
package com.atlassian.theplugin.idea.action.bamboo;

import com.atlassian.theplugin.commons.bamboo.BambooServerFacadeImpl;
import com.atlassian.theplugin.commons.exception.ServerPasswordNotProvidedException;
import com.atlassian.theplugin.commons.remoteapi.RemoteApiException;
import com.atlassian.theplugin.idea.IdeaHelper;
import com.atlassian.theplugin.idea.bamboo.BambooBuildAdapterIdea;
import com.atlassian.theplugin.idea.bamboo.BuildCommentForm;
import com.atlassian.theplugin.idea.bamboo.BuildLabelForm;
import com.atlassian.theplugin.util.PluginUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author Jacek Jaroczynski
 */
public abstract class AbstractBuildAction extends AnAction {

	private void setStatusMessageUIThread(final Project project, final String message) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				setStatusMessage(project, message);
			}
		});
	}

	private void setStatusErrorMessageUIThread(final Project project, final String message) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				setStatusErrorMessage(project, message);
			}
		});
	}

	protected abstract void setStatusMessage(final Project project, final String message);
	protected abstract void setStatusErrorMessage(final Project project, final String message);

	protected abstract BambooBuildAdapterIdea getBuild(final AnActionEvent event);


	protected void runBuild(AnActionEvent e) {
		final Project project = IdeaHelper.getCurrentProject(e);
		final BambooBuildAdapterIdea build = getBuild(e);

		if (project != null && build != null && build.getBuildKey() != null) {

			Task.Backgroundable executeTask = new Task.Backgroundable(project, "Starting Build", false) {
				@Override
				public void run(@NotNull final ProgressIndicator indicator) {

					try {
						setStatusMessageUIThread(project, "Starting build on plan: " + build.getBuildKey());
						BambooServerFacadeImpl.getInstance(PluginUtil.getLogger()).
								executeBuild(build.getServer(), build.getBuildKey());
						setStatusMessageUIThread(project, "Build executed on plan: " + build.getBuildKey());
					} catch (ServerPasswordNotProvidedException e) {
						setStatusErrorMessageUIThread(project, "Build not executed: Password not provided for server");
					} catch (RemoteApiException e) {
						setStatusErrorMessageUIThread(project, "Build not executed: " + e.getMessage());
					}
				}
			};

			ProgressManager.getInstance().run(executeTask);
		}
	}

	protected void openBuildInBrowser(final AnActionEvent e) {
		final BambooBuildAdapterIdea build = getBuild(e);

		if (build != null) {
			BrowserUtil.launchBrowser(build.getBuildResultUrl());
		}
	}

	protected void labelBuild(final AnActionEvent e) {
		final Project project = IdeaHelper.getCurrentProject(e);
		final BambooBuildAdapterIdea build = getBuild(e);

		if (project != null && build != null) {
			BuildLabelForm buildLabelForm = new BuildLabelForm(build);
			buildLabelForm.show();
			if (buildLabelForm.getExitCode() == 0) {
				labelBuild(project, build, buildLabelForm.getLabel());
			}
		}
	}

	private void labelBuild(@NotNull final Project project, @NotNull final BambooBuildAdapterIdea build, final String label) {

		Task.Backgroundable labelTask = new Task.Backgroundable(project, "Labeling Build", false) {
			@Override
			public void run(@NotNull final ProgressIndicator indicator) {
				setStatusMessageUIThread(project, "Applying label on build...");
				try {
					BambooServerFacadeImpl.getInstance(PluginUtil.getLogger()).
							addLabelToBuild(build.getServer(), build.getBuildKey(),
									build.getBuildNumber(), label);
					setStatusMessageUIThread(project, "Label applied on build");
				} catch (ServerPasswordNotProvidedException e) {
					setStatusErrorMessageUIThread(project, "Label not applied: Password on provided for server");
				} catch (RemoteApiException e) {
					setStatusErrorMessageUIThread(project, "Label not applied: " + e.getMessage());
				}
			}
		};

		ProgressManager.getInstance().run(labelTask);
	}


	protected void commentBuild(AnActionEvent e) {
		final Project project = IdeaHelper.getCurrentProject(e);
		final BambooBuildAdapterIdea build = getBuild(e);

		if (project != null && build != null) {
			BuildCommentForm buildCommentForm = new BuildCommentForm(build);
			buildCommentForm.show();
			if (buildCommentForm.getExitCode() == 0) {
				commentBuild(project, build, buildCommentForm.getCommentText());
			}
		}
	}

		private void commentBuild(@NotNull final Project project,
				@NotNull final BambooBuildAdapterIdea build, final String commentText) {

			Task.Backgroundable commentTask = new Task.Backgroundable(project, "Commenting Build", false) {
				@Override
				public void run(@NotNull final ProgressIndicator indicator) {
					setStatusMessageUIThread(project, "Adding comment label on build...");
					try {
						BambooServerFacadeImpl.getInstance(PluginUtil.getLogger()).
								addCommentToBuild(build.getServer(), build.getBuildKey(), build.getBuildNumber(), commentText);
						setStatusMessageUIThread(project, "Comment added to build");
					} catch (ServerPasswordNotProvidedException e) {
						setStatusErrorMessageUIThread(project, "Comment not added: Password not provided for server");
					} catch (RemoteApiException e) {
						setStatusErrorMessageUIThread(project, "Comment not added: " + e.getMessage());
					}
				}
			};

			ProgressManager.getInstance().run(commentTask);
		}
}
