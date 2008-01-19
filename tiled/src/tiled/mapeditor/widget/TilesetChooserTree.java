/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for Stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */
package tiled.mapeditor.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import tiled.core.Map;
import tiled.core.TileSet;
import tiled.mapeditor.MapEditor;

/**
 * @author mtotz
 * 
 */
public class TilesetChooserTree extends TilesetChooser implements TreeSelectionListener {
	private static final long serialVersionUID = 1L;
	private JTree tree;
	private JSplitPane tabbedPane;

	/**
	 * @param mapEditor
	 */
	public TilesetChooserTree(MapEditor mapEditor) {
		super(mapEditor);
		createLayout();
	}

	/**
	 * 
	 */
	private void createLayout() {
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		tree = new JTree();
		tree.setShowsRootHandles(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		tabbedPane = new JSplitPane();
		tabbedPane.setLeftComponent(new JScrollPane(tree));
		tabbedPane.setRightComponent(new JPanel());
		tabbedPane.setDividerLocation(200);
		add(tabbedPane);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent e) {
		Object selected = e.getPath().getLastPathComponent();
		if (selected instanceof TilesetTreeNode) {
			TilesetTreeNode node = (TilesetTreeNode) selected;
			// is it a leaf?
			if (node.tileset != null) {
				// yes. Do we have a cached tileset-panel?
				if (node.panel == null) {
					// nope, create it
					TilePalettePanel tilePanel = new TilePalettePanel(mapEditor, node.tileset);
					tilePanel.addTileSelectionListener(this);
					node.panel = new JScrollPane(tilePanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				}

				tabbedPane.setRightComponent(node.panel);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tiled.mapeditor.widget.TilesetChooser#setMap(tiled.core.Map)
	 */
	@Override
	public void setMap(Map currentMap) {
		if (currentMap != null) {
			setTilesets(currentMap.getTilesets());
		} else {
			tree.setModel(null);
		}

	}

	/** sets the tilesets...in fact refresh the tree. */
	@Override
	public void setTilesets(List<TileSet> tilesets) {
		tree.setModel(new TilesetModel(tilesets));

	}

	/** */
	private class TilesetModel implements TreeModel {
		private TilesetTreeNode rootNode;
		private List<TreeModelListener> listener = new ArrayList<TreeModelListener>();

		public TilesetModel(List<TileSet> tilesets) {
			this.rootNode = new TilesetTreeNode("", tilesets);
		}

		public void addTreeModelListener(TreeModelListener l) {
			listener.add(l);
		}

		public void removeTreeModelListener(TreeModelListener l) {
			listener.remove(l);
		}

		public Object getChild(Object parent, int index) {
			return ((TilesetTreeNode) parent).childs.get(index);
		}

		public int getChildCount(Object parent) {
			return ((TilesetTreeNode) parent).childs.size();
		}

		public int getIndexOfChild(Object parent, Object child) {
			return ((TilesetTreeNode) parent).childs.indexOf(child);
		}

		public Object getRoot() {
			return rootNode;
		}

		public boolean isLeaf(Object node) {
			return getChildCount(node) == 0;
		}

		public void valueForPathChanged(TreePath path, Object newValue) {
			throw new IllegalStateException("treemodel cannot be changed");
		}
	}

	/** a tileset treenode. */
	private class TilesetTreeNode {
		public JScrollPane panel;
		private String name;
		private List<TilesetTreeNode> childs = new ArrayList<TilesetTreeNode>();
		private TileSet tileset;

		/**
		 * 
		 */
		public TilesetTreeNode(String path, List<TileSet> tilesets) {
			int index = path.lastIndexOf('/');

			if (index > -1) {
				name = path.substring(index);
				if (name.startsWith("/")) {
					name = name.substring(1, name.length());
				}
			} else {
				name = path.length() > 0 ? path : "/";
			}

			// now find the childs of this node
			List<TileSet> childList = new ArrayList<TileSet>();
			for (TileSet set : tilesets) {
				String name = set.getName();
				if (name.startsWith(path) && name.length() > path.length()) {
					childList.add(set);
				}

				if (path.equals(name)) {
					tileset = set;
				}
			}

			// keep track of already parsed paths...
			List<String> gotPath = new ArrayList<String>();
			// now parse the new childlist
			for (TileSet set : childList) {
				String name = set.getName();
				int newPathIndex = name.indexOf('/', path.length() + 1);
				String newPath = (newPathIndex > 0) ? name.substring(0, newPathIndex) : name;

				// parse a given path only one time
				if (!gotPath.contains(newPath)) {
					childs.add(new TilesetTreeNode(newPath, childList));
					gotPath.add(newPath);
				}
			}

			// last but not least...sort the nodes
			Collections.sort(childs, new NodeComparator());
		}

		@Override
		public String toString() {
			return name;
		}
	}

	/** sorts the tileset nodes. */
	private class NodeComparator implements Comparator<TilesetTreeNode> {
		public int compare(TilesetTreeNode o1, TilesetTreeNode o2) {
			if ((o1.childs.size() == 0 && o2.childs.size() == 0) || (o1.childs.size() > 0 && o2.childs.size() > 0)) {
				// both are folders or both are leafs...sort by name
				return o1.name.compareTo(o2.name);
			}

			// one of them is a folder, the other a leaf
			return o1.childs.size() > 0 ? -1 : 1;
		}

	}

}
