package com.atlassian.theplugin.idea.action.crucible;

import com.atlassian.theplugin.commons.crucible.api.model.Action;

@Deprecated
public class SubmitReviewAction extends AbstractTransitionReviewAction {
	protected Action getRequestedTransition() {
		return Action.SUBMIT;
	}
}
