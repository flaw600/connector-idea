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

package com.atlassian.theplugin.commons.crucible.api.model;

public enum PredefinedFilter {
    ToReview("toReview", "To Review"),
    RequireMyApproval("requireMyApproval", "Require My Approval"),
    ToSummarize("toSummarize", "To Summarize"),
    OutForReview("outForReview", "Out For Review"),
    Drafts("drafts", "Drafts"),
    Open("open", "Open"),
    Closed("closed", "Closed"),
    Abandoned("trash", "Abandoned");
//    AllOpen("allOpenReviews", "All Open Reviews"),
//    AllClosed("allClosedReviews", "All Closed Reviews"),
//    All("allReviews", "All Reviews");

    private String filterUrl;
    private String filterName;

    PredefinedFilter(String filterUrl, String filterName) {
        this.filterUrl = filterUrl;
        this.filterName = filterName;
    }
    
    public String getFilterUrl() {
        return filterUrl;
    }

    public String getFilterName() {
        return filterName;
    }
}
