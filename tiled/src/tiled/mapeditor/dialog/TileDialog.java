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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import tiled.core.*;
import tiled.mapeditor.util.*;
import tiled.mapeditor.widget.*;

public class TileDialog extends JDialog implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = -124706696709249122L;

	private Tile currentTile;
	private TileSet tileset;
	private JList tileList, imageList;
	private JTable tileProperties;
	private JButton bOk, bNew, bDelete, bChangeI, bDuplicate;
	private JButton bAddImage, bDeleteImage, bDeleteAllUnusedImages;
	private String location;
	private JTextField tilesetNameEntry;
	private JCheckBox externalBitmapCheck;
	// private JCheckBox sharedImagesCheck;
	private JTabbedPane tabs;
	private int currentImageIndex = -1;

	public TileDialog(Dialog parent, TileSet s) {
		super(parent, "Edit Tileset '" + s.getName() + "'", true);
		location = "";
		init();
		setTileset(s);
		setCurrentTile(null);
		pack();
		setLocationRelativeTo(getOwner());
	}

	private JPanel createTilePanel() {
		// Create the buttons

		bDelete = new JButton("Delete Tile");
		bChangeI = new JButton("Change Image");
		bDuplicate = new JButton("Duplicate Tile");
		bNew = new JButton("Add Tile");

		bDelete.addActionListener(this);
		bChangeI.addActionListener(this);
		bDuplicate.addActionListener(this);
		bNew.addActionListener(this);

		tileList = new JList();
		tileList.setCellRenderer(new TileDialogListRenderer());

		// Tile properties table

		tileProperties = new JTable(new PropertiesTableModel(null));
		tileProperties.getSelectionModel().addListSelectionListener(this);
		JScrollPane propScrollPane = new JScrollPane(tileProperties);
		propScrollPane.setPreferredSize(new Dimension(150, 150));

		// Tile list

		tileList.addListSelectionListener(this);
		JScrollPane sp = new JScrollPane();
		sp.getViewport().setView(tileList);
		sp.setPreferredSize(new Dimension(150, 150));

		// The split pane

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		splitPane.setResizeWeight(0.25);
		splitPane.setLeftComponent(sp);
		splitPane.setRightComponent(propScrollPane);

		// The buttons

		JPanel buttons = new VerticalStaticJPanel();
		buttons.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(bNew);
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		buttons.add(bDelete);
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		buttons.add(bChangeI);
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		buttons.add(bDuplicate);
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		buttons.add(Box.createGlue());

		// Putting it all together

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		mainPanel.add(splitPane, c);
		c.weightx = 0;
		c.weighty = 0;
		c.gridy = 1;
		mainPanel.add(buttons, c);

		return mainPanel;
	}

	private JPanel createTilesetPanel() {
		JLabel name_label = new JLabel("Name: ");
		tilesetNameEntry = new JTextField(32);
		// sharedImagesCheck = new JCheckBox("Use shared images");
		externalBitmapCheck = new JCheckBox("Use external bitmap");
		// sharedImagesCheck.addActionListener(this);
		externalBitmapCheck.addActionListener(this);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(name_label, c);
		c.gridx = 1;
		c.gridy = 0;
		mainPanel.add(tilesetNameEntry);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		// mainPanel.add(sharedImagesCheck, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		mainPanel.add(externalBitmapCheck, c);

		return mainPanel;
	}

	private JPanel createImagePanel() {
		imageList = new JList();
		imageList.setCellRenderer(new ImageCellRenderer());
		imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		imageList.addListSelectionListener(this);
		JScrollPane sp = new JScrollPane();
		sp.getViewport().setView(imageList);
		sp.setPreferredSize(new Dimension(150, 150));

		// Buttons
		bAddImage = new JButton("Add Image");
		bAddImage.addActionListener(this);
		bDeleteImage = new JButton("Delete Image");
		bDeleteImage.addActionListener(this);
		bDeleteAllUnusedImages = new JButton("Delete Unused Images");
		bDeleteAllUnusedImages.addActionListener(this);
		JPanel buttons = new VerticalStaticJPanel();
		buttons.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(bAddImage);
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		buttons.add(bDeleteImage);
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		buttons.add(bDeleteAllUnusedImages);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		mainPanel.add(sp, c);
		c.weightx = 0;
		c.weighty = 0;
		c.gridy = 1;
		mainPanel.add(buttons, c);
		return mainPanel;
	}

	private void init() {
		tabs = new JTabbedPane(JTabbedPane.TOP);
		tabs.addTab("Tileset", createTilesetPanel());
		tabs.addTab("Tiles", createTilePanel());
		tabs.addTab("Images", createImagePanel());

		bOk = new JButton("OK");
		bOk.addActionListener(this);

		JPanel buttons = new VerticalStaticJPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(Box.createGlue());
		buttons.add(bOk);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.add(tabs);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPanel.add(buttons);

		getContentPane().add(mainPanel);
		getRootPane().setDefaultButton(bOk);
	}

	private void changeImage() {
		if (currentTile == null) {
			return;
		}
		if (tileset.usesSharedImages()) {
			TileImageDialog d = new TileImageDialog(this, tileset, currentTile.getImageId(),
					currentTile.getImageOrientation());
			d.setVisible(true);
			if (d.getImageId() >= 0) {
				currentTile.setImage(d.getImageId());
				currentTile.setImageOrientation(d.getImageOrientation());
			}
		} else {
			Image img = loadImage();
			if (img != null) {
				currentTile.setImage(img);
			}
		}
	}

	private Image loadImage() {
		JFileChooser ch = new JFileChooser(location);
		int ret = ch.showOpenDialog(this);

		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = ch.getSelectedFile();
			try {
				BufferedImage image = ImageIO.read(file);
				if (image != null) {
					location = file.getAbsolutePath();
					return image;
				} else {
					JOptionPane.showMessageDialog(this, "Error loading image", "Error loading image",
							JOptionPane.ERROR_MESSAGE);
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error loading image", JOptionPane.ERROR_MESSAGE);
			}
		}

		return null;
	}

	private void newTile() {
		if (tileset.usesSharedImages()) {
			TileImageDialog d = new TileImageDialog(this, tileset);
			d.setVisible(true);
			if (d.getImageId() >= 0) {
				currentTile = new Tile(tileset);
				currentTile.setImage(d.getImageId());
				currentTile.setImageOrientation(d.getImageOrientation());
				tileset.addNewTile(currentTile);
				queryTiles();
			}
			return;
		}

		File[] files;
		JFileChooser ch = new JFileChooser(location);
		ch.setMultiSelectionEnabled(true);
		BufferedImage image = null;

		ch.showOpenDialog(this);
		files = ch.getSelectedFiles();

		for (int i = 0; i < files.length; i++) {
			try {
				image = ImageIO.read(files[i]);
				// TODO: Support for a transparent color
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
				return;
			}

			Tile newTile = new Tile(tileset);
			newTile.setImage(image);
			tileset.addNewTile(newTile);
		}

		if (files.length > 0) {
			location = files[0].getAbsolutePath();
		}

		queryTiles();
	}

	public void setTileset(TileSet s) {
		tileset = s;

		if (tileset != null) {
			// Find new tile images at the location of the tileset
			if (tileset.getSource() != null) {
				location = tileset.getSource();
			} else if (tileset.getMap() != null) {
				location = tileset.getMap().getFilename();
			}
			tilesetNameEntry.setText(tileset.getName());
			// sharedImagesCheck.setSelected(tileset.usesSharedImages());
			externalBitmapCheck.setSelected(tileset.getTilebmpFile() != null);
		}

		queryTiles();
		queryImages();
		updateEnabledState();
	}

	public void queryTiles() {
		List<Tile> listData;

		if (tileset != null && tileset.size() > 0) {
			listData = new ArrayList<Tile>();
			Iterator<Tile> tileIterator = tileset.iterator();

			while (tileIterator.hasNext()) {
				Tile tile = tileIterator.next();
				listData.add(tile);
			}

			tileList.setListData(listData.toArray());
		}

		if (currentTile != null) {
			tileList.setSelectedIndex(currentTile.getId() - 1);
			tileList.ensureIndexIsVisible(currentTile.getId() - 1);
		}
	}

	public void queryImages() {
		List<Image> listData = new ArrayList<Image>();

		Iterator<String> ids = tileset.getImageIds().iterator();
		while (ids.hasNext()) {
			Image img = tileset.getImageById(ids.next());
			if (img != null) {
				listData.add(img);
			}
		}

		imageList.setListData(listData.toArray());
		if (currentImageIndex != -1) {
			imageList.setSelectedIndex(currentImageIndex);
			imageList.ensureIndexIsVisible(currentImageIndex);
		}
	}

	private void setCurrentTile(Tile tile) {
		// Update the old current tile's properties
		// (happens automatically as properties are changed in place now)
		// ...
		// Enabled again. The table model now copies the Properties
		if (currentTile != null) {
			PropertiesTableModel model = (PropertiesTableModel) tileProperties.getModel();
			currentTile.setProperties(model.getProperties());
		}

		currentTile = tile;
		updateTileInfo();
		updateEnabledState();
	}

	private void setImageIndex(int i) {
		currentImageIndex = i;
		updateEnabledState();
	}

	private void updateEnabledState() {
		// boolean internal = (tileset.getSource() == null);
		boolean tilebmp = (tileset.getTilebmpFile() != null);
		boolean tileSelected = (currentTile != null);
		boolean sharedImages = tileset.usesSharedImages();
		boolean atLeastOneSharedImage = sharedImages && tileset.getTotalImages() >= 1;

		bNew.setEnabled(atLeastOneSharedImage || !tilebmp);
		bDelete.setEnabled((sharedImages || !tilebmp) && tileSelected);
		bChangeI.setEnabled((atLeastOneSharedImage || !tilebmp) && tileSelected);
		bDuplicate.setEnabled((sharedImages || !tilebmp) && tileSelected);
		tileProperties.setEnabled((sharedImages || !tilebmp) && tileSelected);
		externalBitmapCheck.setEnabled(tilebmp); // Can't turn this off yet
		// sharedImagesCheck.setEnabled(!tilebmp || !sharedImages
		// || tileset.safeToDisableSharedImages());
		tabs.setEnabledAt(2, sharedImages);
		if (sharedImages) {
			bAddImage.setEnabled(!tilebmp);
			bDeleteAllUnusedImages.setEnabled(!tilebmp);
			boolean image_used = false;
			Iterator tileIterator = tileset.iterator();

			while (tileIterator.hasNext()) {
				Tile tile = (Tile) tileIterator.next();
				if (tile.getImageId() == currentImageIndex) {
					image_used = true;
				}
			}
			bDeleteImage.setEnabled(!tilebmp && currentImageIndex >= 0 && !image_used);
		}
	}

	/**
	 * Updates the properties table with the properties of the current tile.
	 */
	private void updateTileInfo() {
		if (currentTile == null) {
			return;
		}

		Properties tileProps = currentTile.getProperties();
		PropertiesTableModel tilePropertiesModel = new PropertiesTableModel(tileProps);
		tileProperties.setModel(tilePropertiesModel);
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == bOk) {
			tileset.setName(tilesetNameEntry.getText());
			this.dispose();
		} else if (source == bDelete) {
			int answer = JOptionPane.showConfirmDialog(this, "Delete tile?", "Are you sure?",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (answer == JOptionPane.YES_OPTION) {
				Tile tile = (Tile) tileList.getSelectedValue();
				if (tile != null) {
					tileset.removeTile(tile.getId());
				}
				queryTiles();
			}
		} else if (source == bChangeI) {
			changeImage();
		} else if (source == bNew) {
			newTile();
		} else if (source == bDuplicate) {
			Tile n = new Tile(currentTile);
			tileset.addNewTile(n);
			queryTiles();
			// Select the last (cloned) tile
			tileList.setSelectedIndex(tileset.size() - 1);
			tileList.ensureIndexIsVisible(tileset.size() - 1);
		} else if (source == externalBitmapCheck) {
			if (!externalBitmapCheck.isSelected()) {
				int answer = JOptionPane.showConfirmDialog(this,
						"Warning: this operation cannot currently be reversed.\n"
								+ "Disable the use of an external bitmap?", "Are you sure?", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if (answer == JOptionPane.YES_OPTION) {
					tileset.setTilesetImageFilename(null);
					updateEnabledState();
				} else {
					externalBitmapCheck.setSelected(true);
				}
			}
		}
		/*
		 * else if (source == sharedImagesCheck) { if
		 * (sharedImagesCheck.isSelected()) { tileset.enableSharedImages();
		 * updateEnabledState(); } else { int answer = JOptionPane.YES_OPTION;
		 * if (!tileset.safeToDisableSharedImages()) { answer =
		 * JOptionPane.showConfirmDialog( this, "This tileset uses features that
		 * require the " + "use of shared images. Disable the use of shared " +
		 * "images?", "Are you sure?", JOptionPane.YES_NO_OPTION,
		 * JOptionPane.QUESTION_MESSAGE); } if (answer ==
		 * JOptionPane.YES_OPTION) { tileset.disableSharedImages();
		 * updateEnabledState(); } else { sharedImagesCheck.setSelected(true); } } }
		 */
		else if (source == bAddImage) {
			Image img = loadImage();
			if (img != null) {
				tileset.addImage(img);
			}
			queryImages();
		} else if (source == bDeleteImage) {
			int answer = JOptionPane.showConfirmDialog(this, "Delete this image?", "Are you sure?",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (answer == JOptionPane.YES_OPTION) {
				Image img = (Image) imageList.getSelectedValue();
				tileset.removeImage(Integer.toString(tileset.getIdByImage(img)));
				queryImages();
			}
		} else if (source == bDeleteAllUnusedImages) {
			int answer = JOptionPane.showConfirmDialog(this, "Delete all unused images?", "Are you sure?",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (answer == JOptionPane.YES_OPTION) {

				Iterator<String> ids = tileset.getImageIds().iterator();
				while (ids.hasNext()) {
					int id = Integer.parseInt((String) ids.next());
					boolean image_used = false;
					Iterator tileIterator = tileset.iterator();

					while (tileIterator.hasNext()) {
						Tile tile = (Tile) tileIterator.next();
						if (tile.getImageId() == id) {
							image_used = true;
						}
					}

					if (!image_used) {
						tileset.removeImage(Integer.toString(id));
					}
				}

				queryImages();
			}
		}

		repaint();
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == tileList) {
			setCurrentTile((Tile) tileList.getSelectedValue());
		} else if (e.getSource() == imageList) {
			setImageIndex(imageList.getSelectedIndex());
		}
	}
}
