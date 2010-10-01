package com.atlassian.theplugin.idea.action.reviews;

import com.atlassian.theplugin.commons.crucible.api.model.CrucibleAction;

/**
 * @author jgorycki
 */
public class RecoverReviewAction extends AbstractTransitionReviewAction {
	@Override
	protected CrucibleAction getRequestedTransition() {
		return CrucibleAction.RECOVER;
	}
}
