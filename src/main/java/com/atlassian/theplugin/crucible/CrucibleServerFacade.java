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

package com.atlassian.theplugin.crucible;

import com.atlassian.theplugin.commons.exception.ServerPasswordNotProvidedException;
import com.atlassian.theplugin.crucible.api.*;
import com.atlassian.theplugin.commons.remoteapi.ProductServerFacade;
import com.atlassian.theplugin.commons.remoteapi.RemoteApiException;
import com.atlassian.theplugin.commons.Server;

import java.util.List;

public interface CrucibleServerFacade extends ProductServerFacade {
	ReviewData createReview(Server server, ReviewData review)
			throws RemoteApiException, ServerPasswordNotProvidedException;

	List<ReviewDataInfo> getAllReviews(Server server)
			throws RemoteApiException, ServerPasswordNotProvidedException;

	List<ReviewDataInfo> getActiveReviewsForUser(Server server)
			throws RemoteApiException, ServerPasswordNotProvidedException;

	ReviewData createReviewFromPatch(Server server, ReviewData reviewData, String patch)
			throws RemoteApiException, ServerPasswordNotProvidedException;

	List<ReviewItemData> getReviewItems(Server server, PermId permId)
			throws RemoteApiException, ServerPasswordNotProvidedException;

	List<GeneralComment> getComments(Server server, PermId permId)
			throws RemoteApiException, ServerPasswordNotProvidedException;

	List<ProjectData> getProjects(Server server)
			throws RemoteApiException, ServerPasswordNotProvidedException;

	List<RepositoryData> getRepositories(Server server) 
			throws RemoteApiException, ServerPasswordNotProvidedException;

	SvnRepositoryData getRepository(Server server, String repoName) 
			throws RemoteApiException, ServerPasswordNotProvidedException;

}
