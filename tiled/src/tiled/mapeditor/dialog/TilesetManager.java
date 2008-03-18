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

package tiled.mapeditor.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.TileSet;
import tiled.io.MapHelper;
import tiled.io.MapWriter;
import tiled.mapeditor.plugin.PluginClassLoader;
import tiled.mapeditor.util.TiledFileFilter;
import tiled.mapeditor.util.TilesetTableModel;

public class TilesetManager extends JDialog implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = -8924814617314058601L;

	private Map map;

	private JButton saveAsButton, saveButton, embedButton;
	private JButton removeButton, editButton, closeButton;
	private JTable tilesetTable;

	public TilesetManager(JFrame parent, Map map) {
		super(parent, "Tileset Manager", true);
		this.map = map;
		init();
		pack();
		setLocationRelativeTo(getOwner());
	}

	private void init() {
		// Create the tileset table
		tilesetTable = new JTable(new TilesetTableModel(map));
		tilesetTable.getSelectionModel().addListSelectionListener(this);
		JScrollPane tilesetScrollPane = new JScrollPane(tilesetTable);
		tilesetScrollPane.setPreferredSize(new Dimension(360, 150));

		// Create the buttons
		saveButton = new JButton("Save");
		editButton = new JButton("Edit...");
		saveAsButton = new JButton("Save as...");
		embedButton = new JButton("Embed");
		removeButton = new JButton("Remove");
		closeButton = new JButton("Close");

		saveAsButton.addActionListener(this);
		saveButton.addActionListener(this);
		embedButton.addActionListener(this);
		removeButton.addActionListener(this);
		editButton.addActionListener(this);
		closeButton.addActionListener(this);

		// Create the main panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 0;
		c.gridwidth = 7;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		mainPanel.add(tilesetScrollPane, c);
		c.insets = new Insets(5, 0, 0, 5);
		c.gridy = 1;
		c.weighty = 0;
		c.weightx = 0;
		c.gridwidth = 1;
		mainPanel.add(saveButton, c);
		mainPanel.add(saveAsButton, c);
		mainPanel.add(embedButton, c);
		mainPanel.add(removeButton, c);
		mainPanel.add(editButton, c);
		c.weightx = 1;
		mainPanel.add(Box.createGlue(), c);
		c.weightx = 0;
		c.insets = new Insets(5, 0, 0, 0);
		mainPanel.add(closeButton, c);

		getContentPane().add(mainPanel);
		getRootPane().setDefaultButton(closeButton);

		tilesetTable.changeSelection(0, 0, false, false);
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		int selectedRow = tilesetTable.getSelectedRow();
		List<TileSet> tilesets = map.getTilesets();
		TileSet set = null;
		try {
			set = tilesets.get(selectedRow);
		} catch (IndexOutOfBoundsException e) {
		}

		if (command.equals("Close")) {
			dispose();
		} else if (command.equals("Edit...")) {
			if (map != null && selectedRow >= 0) {
				TileDialog tileDialog = new TileDialog(this, set);
				tileDialog.setVisible(true);
			}
		} else if (command.equals("Remove")) {
			try {
				if (checkSetUsage(set) > 0) {
					int ret = JOptionPane.showConfirmDialog(this, "This tileset is currently in use. "
							+ "Are you sure you wish to remove it?", "Sure?", JOptionPane.YES_NO_CANCEL_OPTION);
					if (ret == JOptionPane.YES_OPTION) {
						map.removeTileset(set);
						updateTilesetTable();
					}
				} else {
					map.removeTileset(set);
					updateTilesetTable();
				}
			} catch (ArrayIndexOutOfBoundsException a) {
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		} else if (command.equals("Save as...")) {
			JFileChooser ch = new JFileChooser(map.getFilename());

			MapWriter[] writers = PluginClassLoader.getInstance().getWriters();
			for (int i = 0; i < writers.length; i++) {
				try {
					ch.addChoosableFileFilter(new TiledFileFilter(writers[i].getFilter(), writers[i].getName()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			ch.addChoosableFileFilter(new TiledFileFilter(TiledFileFilter.FILTER_TSX));
			int ret = ch.showSaveDialog(this);
			if (ret == JFileChooser.APPROVE_OPTION) {
				String filename = ch.getSelectedFile().getAbsolutePath();
				File exist = new File(filename);

				if ((exist.exists() && JOptionPane.showConfirmDialog(this,
						"The file already exists. Do you wish to overwrite it?") == JOptionPane.OK_OPTION)
						|| !exist.exists()) {
					try {
						MapHelper.saveTileset(set, filename);
						set.setSource(filename);
						embedButton.setEnabled(true);
						saveButton.setEnabled(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else if (command.equals("Save")) {
			try {
				MapHelper.saveTileset(set, set.getSource());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (command.equals("Embed")) {
			set.setSource(null);
			embedButton.setEnabled(false);
			saveButton.setEnabled(false);
		} else {
			System.out.println("Unimplemented command: " + command);
		}
	}

	private void updateTilesetTable() {
		((TilesetTableModel) tilesetTable.getModel()).setMap(map);
		tilesetTable.repaint();
	}

	private int checkSetUsage(TileSet s) {
		int used = 0;

		for (Tile tile : s) {
			for (MapLayer ml : map.getLayerList()) {
				if ((ml instanceof TileLayer) && (((TileLayer) ml).isUsed(tile))) {
					used++;
					break;
				}
			}
		}

		return used;
	}

	public void valueChanged(ListSelectionEvent event) {
		updateButtons();
	}

	private void updateButtons() {
		int selectedRow = tilesetTable.getSelectedRow();
		List<TileSet> tilesets = map.getTilesets();
		TileSet set = null;
		try {
			set = tilesets.get(selectedRow);
		} catch (IndexOutOfBoundsException e) {
		}

		editButton.setEnabled(set != null);
		removeButton.setEnabled(set != null);
		saveButton.setEnabled(set != null && set.getSource() != null);
		embedButton.setEnabled(set != null && set.getSource() != null);
	}
}
