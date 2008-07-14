package com.atlassian.theplugin.idea.bamboo;

import com.atlassian.theplugin.commons.VersionedFileDescriptor;
import com.atlassian.theplugin.commons.bamboo.Commit;
import com.atlassian.theplugin.idea.Constants;
import com.atlassian.theplugin.idea.IdeaHelper;
import com.atlassian.theplugin.idea.TableColumnInfo;
import com.atlassian.theplugin.idea.ui.AtlassianTableView;
import com.atlassian.theplugin.idea.ui.TableColumnProvider;
import com.atlassian.theplugin.idea.ui.TableItemSelectedListener;
import com.atlassian.theplugin.idea.ui.tree.AtlassianTree;
import com.atlassian.theplugin.idea.ui.tree.file.FileTreeModelBuilder;
import com.atlassian.theplugin.util.ColorToHtml;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.peer.PeerFactory;
import com.intellij.ui.content.Content;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.*;
import java.awt.*;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public final class BuildChangesToolWindow {

	public interface ChangesTree extends Expandable {
		boolean GROUP_BY_DIRECTORY_DEFAULT = true;
		void showDiff();
		void showDiffWithLocal();
		void showRepositoryVersion();
		boolean isGroupByDirectory();
		void setGroupByDirectory(boolean groupByDirectory);
	}

	private static final String TOOL_WINDOW_TITLE = "Bamboo Build Changes";

	private static BuildChangesToolWindow instance = new BuildChangesToolWindow();

	private static HashMap<String, CommitDetailsPanel> panelMap = new HashMap<String, CommitDetailsPanel>();

	private BuildChangesToolWindow() {
	}

	public static BuildChangesToolWindow getInstance() {
		return instance;
	}

	public static ChangesTree getChangesTree(String name) {
		return panelMap.get(name);
	}

	public void showBuildChanges(String buildKey, String buildNumber, List<Commit> commits) {
		CommitDetailsPanel detailsPanel;
		String contentKey = buildKey + "-" + buildNumber;


		ToolWindowManager twm = ToolWindowManager.getInstance(IdeaHelper.getCurrentProject());
		ToolWindow commitDetailsToolWindow = twm.getToolWindow(TOOL_WINDOW_TITLE);
		if (commitDetailsToolWindow == null) {
			commitDetailsToolWindow = twm.registerToolWindow(TOOL_WINDOW_TITLE, true, ToolWindowAnchor.BOTTOM);
			commitDetailsToolWindow.setIcon(Constants.BAMBOO_COMMITS_ICON);
		}

		Content content = commitDetailsToolWindow.getContentManager().findContent(contentKey);

		if (content == null) {
			detailsPanel = new CommitDetailsPanel(contentKey, commits);
			panelMap.remove(contentKey);
			panelMap.put(contentKey, detailsPanel);

			PeerFactory peerFactory = PeerFactory.getInstance();
			content = peerFactory.getContentFactory().createContent(detailsPanel, contentKey, true);
			content.setIcon(Constants.BAMBOO_COMMITS_ICON);
			content.putUserData(com.intellij.openapi.wm.ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
			commitDetailsToolWindow.getContentManager().addContent(content);
		}

		commitDetailsToolWindow.getContentManager().setSelectedContent(content);
		commitDetailsToolWindow.show(null);
	}

	private class CommitDetailsPanel extends JPanel implements ChangesTree {
		private static final float SPLIT_RATIO = 0.6f;
		private JTree fileTree;
		private JScrollPane fileScroll;
		private boolean isByDir = ChangesTree.GROUP_BY_DIRECTORY_DEFAULT;

		private List<Commit> commits;
		private AtlassianTableView commitsTable;

		public CommitDetailsPanel(String name, final List<Commit> commits) {
			super();

			this.commits = commits;

			if (commits == null || commits.size() == 0) {
				add(new JLabel("No commits in " + name));
				return;
			}

			setLayout(new GridBagLayout());

			ActionManager manager = ActionManager.getInstance();
			ActionGroup group = (ActionGroup) manager.getAction("ThePlugin.Bamboo.CommitListToolBar");
			ActionToolbar toolbar = manager.createActionToolbar(name, group, true);

			Splitter split = new Splitter(false, SPLIT_RATIO);
			split.setShowDividerControls(true);

			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;

			JPanel tablePanel = new JPanel();
			tablePanel.setLayout(new GridBagLayout());
			GridBagConstraints gbc1 = new GridBagConstraints();

			gbc1.gridx = 0;
			gbc1.gridy = 0;
			gbc1.weightx = 1.0;
			gbc1.weighty = 0.0;
			gbc1.fill = GridBagConstraints.HORIZONTAL;

			JLabel l = new JLabel("Commit List");
			Dimension d = l.getMinimumSize();
			d.height = toolbar.getMaxButtonHeight();
			l.setMinimumSize(d);

			tablePanel.add(l, gbc1);

			gbc1.gridy = 1;
			gbc1.weighty = 1.0;
			gbc1.fill = GridBagConstraints.BOTH;

			commitsTable = createCommitsTable(commits);
			tablePanel.add(new JScrollPane(commitsTable), gbc1);

			split.setFirstComponent(tablePanel);

			JPanel fileTreePanel = new JPanel();
			fileTreePanel.setLayout(new GridBagLayout());

			gbc1.gridy = 0;
			gbc1.weighty = 0.0;
			gbc1.weightx = 1.0;
			gbc1.fill = GridBagConstraints.HORIZONTAL;
			gbc1.anchor = GridBagConstraints.LINE_START;

			JComponent comp = toolbar.getComponent();
			fileTreePanel.add(comp, gbc1);

			gbc1.gridy = 1;
			JLabel label = new JLabel("Changed Files");
			fileTreePanel.add(label, gbc1);

			gbc1.gridy = 2;
			gbc1.weighty = 1.0;
			gbc1.fill = GridBagConstraints.BOTH;

			fileScroll = new JScrollPane();
			fileTreePanel.add(fileScroll, gbc1);

			split.setSecondComponent(fileTreePanel);

			add(split, gbc);
		}


		private class AuthorColumn extends TableColumnInfo {
            private static final int PREFERRED_WIDTH = 100;

            public String getColumnName() {
				return "Author";
			}

			public Class getColumnClass() {
				return String.class;
			}

			public int getPrefferedWidth() {
				return PREFERRED_WIDTH;
			}

			public Object valueOf(Object o) {
				return ((Commit) o).getAuthor();
			}

			public Comparator getComparator() {
				return new Comparator() {
					public int compare(Object o, Object o1) {
						return ((Commit) o).getAuthor().compareTo(((Commit) o1).getAuthor());
					}
				};
			}
		}

		private class DateColumn extends TableColumnInfo {
            private static final int PREFERRED_WIDTH = 100;

            public String getColumnName() {
				return "Date";
			}

			public Class getColumnClass() {
				return Date.class;
			}

			public int getPrefferedWidth() {
				return PREFERRED_WIDTH;
			}

			public Object valueOf(Object o) {
				return ((Commit) o).getCommitDate();
			}

			public Comparator getComparator() {
				return new Comparator() {
					public int compare(Object o, Object o1) {
						return ((Commit) o).getCommitDate().compareTo(((Commit) o1).getCommitDate());
					}
				};
			}
		}

		private class CommentColumn extends TableColumnInfo {
            private static final int PREFERRED_WIDTH = 600;

            public String getColumnName() {
				return "Comment";
			}

			public Class getColumnClass() {
				return String.class;
			}

			public int getPrefferedWidth() {
				return PREFERRED_WIDTH;
			}

			public Object valueOf(Object o) {
				return ((Commit) o).getComment();
			}

			public Comparator getComparator() {
				return new Comparator() {
					public int compare(Object o, Object o1) {
						return ((Commit) o).getComment().compareTo(((Commit) o1).getComment());
					}
				};
			}
		}

		private AtlassianTableView createCommitsTable(final List<Commit> commitList) {
			TableColumnProvider prov = new TableColumnProvider() {
				public TableColumnInfo[] makeColumnInfo() {
					return new TableColumnInfo[] { new AuthorColumn(), new DateColumn(), new CommentColumn() };
				}

				public TableCellRenderer[] makeRendererInfo() {
					return new TableCellRenderer[] { null, null, null };
				}
			};
			final AtlassianTableView atv = new AtlassianTableView(prov,
					new ListTableModel<Commit>(prov.makeColumnInfo(), commitList, 0), null);
			atv.addItemSelectedListener(new TableItemSelectedListener() {
				public void itemSelected(Object item, int noClicks) {
					Commit c = (Commit) item;
					createTree(c);
				}
			});
			return atv;
		}

		public void showDiff() {
			// todo
		}

		public void showDiffWithLocal() {
			// todo
		}

		public void showRepositoryVersion() {
			// todo
		}

		public boolean isGroupByDirectory() {
			return isByDir;
		}

		public void setGroupByDirectory(boolean groupByDirectory) {
			isByDir = groupByDirectory;
			createTree((Commit) commitsTable.getSelectedObject());
		}

		private void createTree(Commit commit) {
			if (commit.getFiles().size() > 0) {
				if (isByDir) {
					fileTree = new AtlassianTree(FileTreeModelBuilder.buildTreeModelFromFiles(commit.getFiles()));
					fileTree.setRootVisible(false);
					fileScroll.setViewportView(fileTree);
					expand();
				} else {
					fileTree = createFlatTree(commit.getFiles());
					fileTree.setRootVisible(false);
					fileScroll.setViewportView(fileTree);
				}
			} else {
				fileScroll.setViewportView(new JLabel("no commits", SwingConstants.CENTER));
			}
		}

		private class FileNode extends DefaultMutableTreeNode {
			private VersionedFileDescriptor file;

			public FileNode(VersionedFileDescriptor f) {
				super(f.getFileName().substring(f.getFileName().lastIndexOf('/') + 1));
				file = f;
			}

			public String getRevision() {
				return file.getRevision();
			}

			public String getDirectory() {
				return file.getFileName().substring(0, file.getFileName().lastIndexOf('/'));
			}

			public String getName() {
				return file.getFileName();
			}
		}

		private JTree createFlatTree(List<VersionedFileDescriptor> files) {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			for (VersionedFileDescriptor f : files) {
				root.add(new FileNode(f));
			}
			TreeModel model = new DefaultTreeModel(root);
			JTree myTree = new JTree(model);
			myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			myTree.setCellRenderer(new DefaultTreeCellRenderer() {
                @Override
                public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                        boolean expanded, boolean leaf, int row, boolean hasFocus) {

					Component c = super.getTreeCellRendererComponent(
							tree, value, selected, expanded, leaf, row, hasFocus);

					FileNode node = (FileNode) value;
					Color statsColor = selected	? UIUtil.getTreeSelectionForeground() : UIUtil.getTreeSelectionBackground();
					StringBuilder txt = new StringBuilder();
					txt.append("<html><body>");
					txt.append(getText());
					txt.append(" <font color=");
					txt.append(ColorToHtml.getHtmlFromColor(statsColor));
					txt.append(">(");
					txt.append(node.getDirectory());
					txt.append(")</font>");
					txt.append(" rev: ");
					txt.append(node.getRevision());
					txt.append("</body></html>");
					setText(txt.toString());

					FileTypeManager mgr = FileTypeManager.getInstance();
					FileType type = mgr.getFileTypeByFileName(node.getName());
					setIcon(type.getIcon());

					return c;
				}
			});

			return myTree;
		}

		public void expand() {
			if (fileTree == null) {
				return;
			}
			for (int row = 0; row < fileTree.getRowCount(); ++row) {
				fileTree.expandRow(row);
			}
		}

		public void collapse() {
			if (fileTree == null) {
				return;
			}
			for (int row = fileTree.getRowCount(); row >= 0; --row) {
				fileTree.collapseRow(row);
			}
		}
	}
}
