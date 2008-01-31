package com.atlassian.theplugin.idea.serverconfig.model;

import com.atlassian.theplugin.configuration.ServerBean;

/**
 * Represents Crucible Server in servers JTree
 * User: mwent
 * Date: 2008-01-29
 * Time: 09:51:00
 */
public class CrucibleServerNode extends ServerNode {
	public CrucibleServerNode(ServerBean aServer) {
		super(aServer);
	}
}
