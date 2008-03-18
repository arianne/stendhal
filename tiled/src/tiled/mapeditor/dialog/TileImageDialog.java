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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import tiled.core.TileSet;
import tiled.mapeditor.util.ImageCellRenderer;
import tiled.mapeditor.widget.VerticalStaticJPanel;

public class TileImageDialog extends JDialog implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = -6087925281416649006L;

	private JList imageList;
	private JButton bOk, bCancel;
	private JCheckBox horizFlipCheck, vertFlipCheck, rotateCheck;
	private int imageId, imageOrientation;
	private TileSet tileset;
	private JLabel imageLabel;
	private int[] imageIds;

	public TileImageDialog(Dialog parent, TileSet set) {
		this(parent, set, 0, 0);
	}

	public TileImageDialog(Dialog parent, TileSet set, int id, int orientation) {
		super(parent, "Choose Tile Image", true);
		tileset = set;
		imageId = id;
		imageOrientation = orientation;
		init();
		queryImages();
		updateImageLabel();
		pack();
		setLocationRelativeTo(getOwner());
	}

	private void init() {
		// image list
		imageList = new JList();
		imageList.setCellRenderer(new ImageCellRenderer());
		imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		imageList.addListSelectionListener(this);
		JScrollPane sp = new JScrollPane();
		sp.getViewport().setView(imageList);
		sp.setPreferredSize(new Dimension(150, 150));

		// image panel
		JPanel image_panel = new JPanel();
		image_panel.setLayout(new BoxLayout(image_panel, BoxLayout.Y_AXIS));
		imageLabel = new JLabel(new ImageIcon());
		horizFlipCheck = new JCheckBox("Flip horizontally", (imageOrientation & 1) == 1);
		horizFlipCheck.addActionListener(this);
		vertFlipCheck = new JCheckBox("Flip vertically", (imageOrientation & 2) == 2);
		vertFlipCheck.addActionListener(this);
		rotateCheck = new JCheckBox("Rotate", (imageOrientation & 4) == 4);
		rotateCheck.addActionListener(this);
		image_panel.add(imageLabel);
		image_panel.add(horizFlipCheck);
		image_panel.add(vertFlipCheck);
		image_panel.add(rotateCheck);

		// buttons
		bOk = new JButton("OK");
		bOk.addActionListener(this);
		bCancel = new JButton("Cancel");
		bCancel.addActionListener(this);
		JPanel buttons = new VerticalStaticJPanel();
		buttons.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(bCancel);
		buttons.add(bOk);

		// main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		mainPanel.add(sp, c);
		c.weightx = 0;
		c.gridx = 1;
		mainPanel.add(image_panel, c);
		c.gridx = 0;
		c.weighty = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		mainPanel.add(buttons, c);
		getContentPane().add(mainPanel);
		getRootPane().setDefaultButton(bOk);
	}

	public void queryImages() {
		List<Image> listData = new ArrayList<Image>();
		int initialIndex = 0;

		Iterator<String> ids = tileset.getImageIds().iterator();
		imageIds = new int[tileset.getTotalImages()];
		for (int i = 0; i < imageIds.length; ++i) {
			imageIds[i] = Integer.parseInt((String) ids.next());
		}

		java.util.Arrays.sort(imageIds);

		for (int i = 0; i < imageIds.length; ++i) {
			if (imageIds[i] == imageId) {
				initialIndex = i;
			}
			Image img = tileset.getImageById(Integer.toString(imageIds[i]));
			// assert img != null;
			listData.add(img);
		}

		imageList.setListData(listData.toArray());
		imageList.setSelectedIndex(initialIndex);
		imageList.ensureIndexIsVisible(initialIndex);
	}

	private void updateEnabledState() {
		bOk.setEnabled(imageId >= 0);
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == bOk) {
			this.dispose();
		} else if (source == bCancel) {
			imageId = -1;
			this.dispose();
		} else if (source == horizFlipCheck) {
			imageOrientation ^= 1;
			updateImageLabel();
		} else if (source == vertFlipCheck) {
			imageOrientation ^= 2;
			updateImageLabel();
		} else if (source == rotateCheck) {
			imageOrientation ^= 4;
			updateImageLabel();
		}

		repaint();
	}

	private void updateImageLabel() {
		if (imageId >= 0) {
			Image img = tileset.getImageById(Integer.toString(imageId));
			img = TileSet.generateImageWithOrientation(img, imageOrientation);
			imageLabel.setIcon(new ImageIcon(img));
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		imageId = imageIds[imageList.getSelectedIndex()];
		updateImageLabel();
		updateEnabledState();
	}

	int getImageId() {
		return imageId;
	}

	int getImageOrientation() {
		return imageOrientation;
	}
}
