package com.atlassian.theplugin.idea.ui.tree.file;

import com.atlassian.theplugin.commons.BambooFileInfo;
import com.atlassian.theplugin.commons.VersionedFileInfo;
import com.atlassian.theplugin.commons.crucible.ValueNotYetInitialized;
import com.atlassian.theplugin.commons.bamboo.BambooChangeSet;
import com.atlassian.theplugin.idea.ui.tree.AtlassianTreeModel;
import com.atlassian.theplugin.idea.ui.tree.AtlassianClickAction;
import com.atlassian.theplugin.idea.ui.tree.AtlassianTreeNode;
import com.atlassian.theplugin.idea.crucible.ReviewData;
import com.atlassian.theplugin.idea.crucible.events.FocusOnGeneralComments;
import com.atlassian.theplugin.idea.crucible.events.FocusOnFileComments;
import com.atlassian.theplugin.idea.crucible.comments.ReviewActionEventBroker;
import com.atlassian.theplugin.idea.crucible.comments.CrucibleReviewActionListener;
import com.atlassian.theplugin.idea.IdeaHelper;
import com.atlassian.theplugin.commons.crucible.api.model.CrucibleFileInfo;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: jgorycki
 * Date: Jul 11, 2008
 * Time: 2:45:53 AM
 * To change this template use File | Settings | File Templates.
 */
public final class FileTreeModelBuilder {

	private FileTreeModelBuilder() {
		// this is a utility class
	}

	public static AtlassianTreeModel buildTreeModelFromCrucibleChangeSet(final ReviewData review)
			throws ValueNotYetInitialized {
		FileNode root = new CrucibleChangeSetTitleNode(review, new AtlassianClickAction() {
			public void execute(AtlassianTreeNode node, int noOfClicks) {
				switch (noOfClicks) {
					case 1:
					case 2:
						ReviewActionEventBroker broker = IdeaHelper.getReviewActionEventBroker();
						broker.trigger(new FocusOnGeneralComments(CrucibleReviewActionListener.ANONYMOUS, review));
						break;
					default:
				}
			}
		});
		FileTreeModel model = new FileTreeModel(root);
		for (CrucibleFileInfo f : review.getFiles()) {
			model.addFile(root, f, review);
		}
		model.compactModel(model.getRoot());
		return model;
	}

	public static AtlassianTreeModel buildTreeModelFromBambooChangeSet(BambooChangeSet changeSet) {
		FileNode root = new FileNode("/", null);
		FileTreeModel model = new FileTreeModel(root);
		for (BambooFileInfo f : changeSet.getFiles()) {
			model.addFile(root, f);
		}
		model.compactModel(model.getRoot());
		return model;
	}

	public static AtlassianTreeModel buildFlatTreeModelFromBambooChangeSet(BambooChangeSet changeSet) {
		FileTreeModel model = new FileTreeModel(new FileNode("/", null));
		for (BambooFileInfo f : changeSet.getFiles()) {
			model.getRoot().addChild(new BambooFileNode(f, AtlassianClickAction.EMPTY_ACTION));
		}
		return model;
	}

	private static class FileTreeModel extends AtlassianTreeModel {
		public FileTreeModel(FileNode root) {
			super(root);
		}

		@Override
		public FileNode getRoot() {
			return (FileNode) super.getRoot();
		}

		public void addFile(FileNode root, BambooFileInfo file) {
			FileNode node = createPlace(root, file);
			node.addChild(new BambooFileNode(file, AtlassianClickAction.EMPTY_ACTION));
		}

		public void addFile(FileNode root, final CrucibleFileInfo file, final ReviewData review) {
			FileNode node = createPlace(root, file);
			// todo lguminski to avoid creation of a new object for each node
			node.addChild(new CrucibleFileNode(file, review, new AtlassianClickAction() {
				public void execute(AtlassianTreeNode node, int noOfClicks) {
					switch (noOfClicks) {
						case 1:
						case 2:
							ReviewActionEventBroker broker = IdeaHelper.getReviewActionEventBroker();
							broker.trigger(new FocusOnFileComments(CrucibleReviewActionListener.ANONYMOUS, review, file));
							break;
					}
				}
			}));
		}


		private FileNode createPlace(FileNode root, VersionedFileInfo file) {
			int idx = 0;
			String fileName = file.getFileDescriptor().getUrl();
			FileNode node = root;
			do {
				int newIdx = fileName.indexOf('/', idx);
				if (newIdx != -1) {
					String newNodeName = fileName.substring(idx, newIdx);
					if (newNodeName.length() > 0) {
						if (!node.hasNode(newNodeName)) {
							FileNode newNode = new FileNode(newNodeName, null);
							node.addChild(newNode);
							node = newNode;
						} else {
							node = node.getNode(newNodeName);
						}
					}
				}
				idx = newIdx + 1;
			} while (idx > 0);
			return node;
		}

		private void compactModel(FileNode node) {
			if (node.isLeaf()) {
				return;
			}

			java.util.List<FileNode> ch = new ArrayList<FileNode>();

			for (FileNode n : node.getChildren().values()) {
				ch.add(n);
			}

			node.removeChildren();

			for (FileNode n : ch) {
				compactModel(n);
				if (n.getChildCount() == 1) {
					FileNode cn = (FileNode) n.getFirstChild();
					if (!cn.isLeaf()) {
						String newName = n.getName() + "/" + cn.getName();
						cn.setName(newName);
						node.addChild(cn);
					} else {
						node.addChild(n);
					}
				} else {
					node.addChild(n);
				}
			}
		}

	}
}
