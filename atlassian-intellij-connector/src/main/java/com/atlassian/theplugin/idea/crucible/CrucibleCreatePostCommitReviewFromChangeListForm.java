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
package com.atlassian.theplugin.idea.crucible;

import com.atlassian.theplugin.commons.cfg.CfgManager;
import com.atlassian.theplugin.commons.cfg.CrucibleServerCfg;
import com.atlassian.theplugin.commons.crucible.CrucibleServerFacade;
import com.atlassian.theplugin.commons.crucible.api.model.Review;
import com.atlassian.theplugin.commons.exception.ServerPasswordNotProvidedException;
import com.atlassian.theplugin.commons.remoteapi.RemoteApiException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.ChangeList;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CrucibleCreatePostCommitReviewFromChangeListForm extends CrucibleReviewCreateForm {
	private final ChangeList[] changes;

	private static String getReviewTitle(final ChangeList[] changes) {
		if (changes != null && changes.length == 1) {
			return changes[0].getName();
		} else {
			return "";
		}

	}

	public CrucibleCreatePostCommitReviewFromChangeListForm(final Project project,
			final CrucibleServerFacade crucibleServerFacade,
			final ChangeList[] changes, @NotNull final CfgManager cfgManager) {
		super(project, crucibleServerFacade, getReviewTitle(changes), cfgManager, "Create Post-Commit Review");
		this.changes = changes;
		setCustomComponent(null);
		pack();
	}

	@Override
	protected boolean isValid(final ReviewProvider reviewProvider) {
		return (reviewProvider.getRepoName() != null);
	}

	@Override
	protected boolean shouldAutoSelectRepo(final CrucibleReviewCreateForm.CrucibleServerData crucibleServerData) {
		return crucibleServerData.getRepositories().size() == 1;
	}

	@Override
	protected Review createReview(final CrucibleServerCfg server, final ReviewProvider reviewProvider)
			throws RemoteApiException, ServerPasswordNotProvidedException {
		if (reviewProvider.getRepoName() == null) {
			Messages.showErrorDialog(project, "Repository not selected. Unable to create review.\n", "Repository required");
			return null;
		}
		java.util.List<String> revisions = new ArrayList<String>();
		if (changes != null) {
			for (ChangeList change : changes) {
				if (change instanceof CommittedChangeList) {
					CommittedChangeList committedChangeList = (CommittedChangeList) change;
					revisions.add(Long.toString(committedChangeList.getNumber()));
				}
			}
		}
		return crucibleServerFacade.createReviewFromRevision(server, reviewProvider, revisions);
	}

}
