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

package com.atlassian.theplugin.idea.action.crucible;

import com.atlassian.theplugin.idea.action.fisheye.ChangeListUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.util.List;


public abstract class Crucible16MultiRevisionRepositoryAction extends Crucible16RepositoryAction {

	@Override
	public void update(AnActionEvent event) {
		final boolean configured = isAnyCrucibleConfigured(event);
		event.getPresentation().setVisible(configured);

		if (configured) {
			List<String> revisions = ChangeListUtil.getRevisions(event);
			event.getPresentation().setEnabled(revisions != null && !revisions.isEmpty());
		}
	}
}