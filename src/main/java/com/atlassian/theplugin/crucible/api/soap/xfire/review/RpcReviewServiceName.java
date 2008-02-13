
package com.atlassian.theplugin.crucible.api.soap.xfire.review;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF (incubator) 2.0.4-incubator
 * Tue Feb 12 17:36:19 CET 2008
 * Generated source version: 2.0.4-incubator
 * 
 */

@WebService(targetNamespace = "http://rpc.spi.crucible.atlassian.com/", name = "rpcReviewServiceName")

public interface RpcReviewServiceName {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getChildReviews", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetChildReviews")
    @ResponseWrapper(localName = "getChildReviewsResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetChildReviewsResponse")
    @WebMethod
    public java.util.List<com.atlassian.theplugin.crucible.api.soap.xfire.review.ReviewData> getChildReviews(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "arg1", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.PermId arg1
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getAllRevisionComments", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetAllRevisionComments")
    @ResponseWrapper(localName = "getAllRevisionCommentsResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetAllRevisionCommentsResponse")
    @WebMethod
    public java.util.List<java.lang.Object> getAllRevisionComments(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "arg1", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.PermId arg1
    );

    @RequestWrapper(localName = "removeReviewItem", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.RemoveReviewItem")
    @ResponseWrapper(localName = "removeReviewItemResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.RemoveReviewItemResponse")
    @WebMethod
    public void removeReviewItem(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "arg1", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.PermId arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.PermId arg2
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "addFisheyeDiff", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.AddFisheyeDiff")
    @ResponseWrapper(localName = "addFisheyeDiffResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.AddFisheyeDiffResponse")
    @WebMethod
    public java.lang.Object addFisheyeDiff(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "arg1", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.PermId arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        java.lang.String arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        java.lang.String arg3,
        @WebParam(name = "arg4", targetNamespace = "")
        java.lang.String arg4,
        @WebParam(name = "arg5", targetNamespace = "")
        java.lang.String arg5,
        @WebParam(name = "arg6", targetNamespace = "")
        java.lang.String arg6
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getAllReviews", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetAllReviews")
    @ResponseWrapper(localName = "getAllReviewsResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetAllReviewsResponse")
    @WebMethod
    public java.util.List<com.atlassian.theplugin.crucible.api.soap.xfire.review.ReviewData> getAllReviews(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getVersionedComments", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetVersionedComments")
    @ResponseWrapper(localName = "getVersionedCommentsResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetVersionedCommentsResponse")
    @WebMethod
    public java.util.List<java.lang.Object> getVersionedComments(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "arg1", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.PermId arg1
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "addComment", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.AddComment")
    @ResponseWrapper(localName = "addCommentResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.AddCommentResponse")
    @WebMethod
    public java.lang.Object addComment(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "arg1", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.PermId arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        java.lang.Object arg2
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getReviewers", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetReviewers")
    @ResponseWrapper(localName = "getReviewersResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetReviewersResponse")
    @WebMethod
    public java.util.List<java.lang.String> getReviewers(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "arg1", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.PermId arg1
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "addGeneralComment", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.AddGeneralComment")
    @ResponseWrapper(localName = "addGeneralCommentResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.AddGeneralCommentResponse")
    @WebMethod
    public java.lang.Object addGeneralComment(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "arg1", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.PermId arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        java.lang.Object arg2
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "createReviewFromPatch", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.CreateReviewFromPatch")
    @ResponseWrapper(localName = "createReviewFromPatchResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.CreateReviewFromPatchResponse")
    @WebMethod
    public com.atlassian.theplugin.crucible.api.soap.xfire.review.ReviewData createReviewFromPatch(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "review", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.ReviewData review,
        @WebParam(name = "patch", targetNamespace = "")
        java.lang.String patch
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getReviewItemsForReview", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetReviewItemsForReview")
    @ResponseWrapper(localName = "getReviewItemsForReviewResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetReviewItemsForReviewResponse")
    @WebMethod
    public java.util.List<java.lang.Object> getReviewItemsForReview(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "arg1", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.PermId arg1
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "changeState", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.ChangeState")
    @ResponseWrapper(localName = "changeStateResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.ChangeStateResponse")
    @WebMethod
    public com.atlassian.theplugin.crucible.api.soap.xfire.review.ReviewData changeState(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "arg1", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.PermId arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.Action arg2
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getReview", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetReview")
    @ResponseWrapper(localName = "getReviewResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetReviewResponse")
    @WebMethod
    public com.atlassian.theplugin.crucible.api.soap.xfire.review.ReviewData getReview(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "arg1", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.PermId arg1
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getReviewsInStates", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetReviewsInStates")
    @ResponseWrapper(localName = "getReviewsInStatesResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetReviewsInStatesResponse")
    @WebMethod
    public java.util.List<com.atlassian.theplugin.crucible.api.soap.xfire.review.ReviewData> getReviewsInStates(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "arg1", targetNamespace = "")
        java.util.List<com.atlassian.theplugin.crucible.api.soap.xfire.review.State> arg1
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "createReview", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.CreateReview")
    @ResponseWrapper(localName = "createReviewResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.CreateReviewResponse")
    @WebMethod
    public com.atlassian.theplugin.crucible.api.soap.xfire.review.ReviewData createReview(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "review", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.ReviewData review
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getGeneralComments", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetGeneralComments")
    @ResponseWrapper(localName = "getGeneralCommentsResponse", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", className = "com.atlassian.theplugin.crucible.api.soap.xfire.review.GetGeneralCommentsResponse")
    @WebMethod
    public java.util.List<java.lang.Object> getGeneralComments(
        @WebParam(name = "token", targetNamespace = "")
        java.lang.String token,
        @WebParam(name = "arg1", targetNamespace = "")
        com.atlassian.theplugin.crucible.api.soap.xfire.review.PermId arg1
    );
}
