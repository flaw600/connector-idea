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

import com.intellij.ide.BrowserUtil;
import com.atlassian.theplugin.idea.crucible.ReviewDataInfoAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: marek
 * Date: May 20, 2008
 * Time: 2:42:12 PM
 * To change this template use File | Settings | File Templates.
 */

public class ViewReviewAction extends TableSelectedAction {

	protected void itemSelected(Object row) {
		BrowserUtil.launchBrowser(((ReviewDataInfoAdapter) row).getReviewUrl());
	}
}
