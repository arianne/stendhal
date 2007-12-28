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
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.mapeditor.dialog;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

import tiled.core.*;
import tiled.mapeditor.util.TransparentImageFilter;
import tiled.mapeditor.widget.*;

/**
 * A dialog for creating a new tileset.
 */
public class NewTilesetDialog extends JDialog implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 3434235417235699312L;

	private Map map;
	private TileSet newTileset;
	private JTextField tileWidth, tileHeight;
	private IntegerSpinner tileSpacing;
	private JTextField tilesetName;
	private JTextField tilebmpFile;
	private JLabel nameLabel, tileWidthLabel, tileHeightLabel, spacingLabel;
	private JLabel tilebmpFileLabel;
	private JCheckBox tilebmpCheck, tileAutoCheck, transCheck;
	private JButton okButton, cancelButton, browseButton;
	private ColorButton colorButton;
	private String path;

	public NewTilesetDialog(JFrame parent, Map map) {
		super(parent, "New Tileset", true);
		this.map = map;
		path = map.getFilename();
		init();
		pack();
		setLocationRelativeTo(parent);
	}

	private void init() {
		// Create the primitives

		nameLabel = new JLabel("Tileset name: ");
		tileWidthLabel = new JLabel("Tile width: ");
		tileHeightLabel = new JLabel("Tile height: ");
		spacingLabel = new JLabel("Tile spacing: ");
		tilebmpFileLabel = new JLabel("Tile image: ");

		tilesetName = new JTextField("Untitled");
		tileWidth = new JTextField("" + map.getTileWidth(), 3);
		tileHeight = new JTextField("" + map.getTileHeight(), 3);
		tileSpacing = new IntegerSpinner(0, 0);
		tilebmpFile = new JTextField(10);
		tilebmpFile.setEnabled(false);

		tileWidthLabel.setEnabled(false);
		tileHeightLabel.setEnabled(false);
		tileWidth.setEnabled(false);
		tileHeight.setEnabled(false);

		tilebmpCheck = new JCheckBox("Reference tileset image", false);
		tilebmpCheck.addChangeListener(this);

		tileAutoCheck = new JCheckBox("Automatically create tiles from images", true);
		tileAutoCheck.setEnabled(false);

		transCheck = new JCheckBox("Use transparent color");
		transCheck.addChangeListener(this);

		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		browseButton = new JButton("Browse...");
		colorButton = new ColorButton(new Color(255, 0, 255));
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		browseButton.addActionListener(this);
		colorButton.addActionListener(this);

		// Combine browse button and tile bitmap path text field

		JPanel tilebmpPathPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		tilebmpPathPanel.add(tilebmpFile, c);
		c.gridx = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 5, 0, 0);
		tilebmpPathPanel.add(browseButton, c);

		// Combine transparent color label and button

		JPanel tileColorPanel = new JPanel(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		tileColorPanel.add(transCheck, c);
		c.gridx = 1;
		tileColorPanel.add(colorButton);

		// Create the tile bitmap import setting panel

		JPanel tilebmpPanel = new VerticalStaticJPanel();
		tilebmpPanel.setLayout(new GridBagLayout());
		tilebmpPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("From tileset image"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.insets = new Insets(5, 0, 0, 0);
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		tilebmpPanel.add(tilebmpCheck, c);
		c.gridy = 1;
		tilebmpPanel.add(tileAutoCheck, c);
		c.gridy = 2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		tilebmpPanel.add(tilebmpFileLabel, c);
		c.gridy = 3;
		tilebmpPanel.add(spacingLabel, c);
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		tilebmpPanel.add(tilebmpPathPanel, c);
		c.gridy = 3;
		tilebmpPanel.add(tileSpacing, c);
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		tilebmpPanel.add(tileColorPanel, c);
		c.gridx = 1;
		c.gridwidth = 1;

		// OK and Cancel buttons

		JPanel buttons = new VerticalStaticJPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(Box.createGlue());
		buttons.add(okButton);
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		buttons.add(cancelButton);

		// Top part of form

		JPanel miscPropPanel = new VerticalStaticJPanel();
		miscPropPanel.setLayout(new GridBagLayout());
		miscPropPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5, 0, 0, 0);
		miscPropPanel.add(nameLabel, c);
		c.gridy = 1;
		miscPropPanel.add(tileWidthLabel, c);
		c.gridy = 2;
		miscPropPanel.add(tileHeightLabel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		miscPropPanel.add(tilesetName, c);
		c.gridy = 1;
		miscPropPanel.add(tileWidth, c);
		c.gridy = 2;
		miscPropPanel.add(tileHeight, c);

		// Main panel

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.add(miscPropPanel);
		mainPanel.add(tilebmpPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPanel.add(Box.createGlue());
		mainPanel.add(buttons);

		getContentPane().add(mainPanel);
		getRootPane().setDefaultButton(okButton);

		setUseTileBitmap(tilebmpCheck.isSelected());
	}

	public TileSet create() {
		setVisible(true);
		return newTileset;
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == okButton) {
			newTileset = new TileSet();
			newTileset.setName(tilesetName.getText());

			if (tilebmpCheck.isSelected()) {
				String file = tilebmpFile.getText();
				int spacing = tileSpacing.intValue();
				try {
					if (!transCheck.isSelected()) {
						newTileset.importTileBitmap(file, map.getTileWidth(), map.getTileHeight(), spacing,
								tileAutoCheck.isSelected());
					} else {
						try {
							Toolkit tk = Toolkit.getDefaultToolkit();
							Image orig = ImageIO.read(new File(file));
							Image trans = tk.createImage(new FilteredImageSource(orig.getSource(),
									new TransparentImageFilter(colorButton.getColor().getRGB())));
							BufferedImage img = new BufferedImage(trans.getWidth(null), trans.getHeight(null),
									BufferedImage.TYPE_INT_ARGB);

							img.getGraphics().drawImage(trans, 0, 0, null);

							newTileset.importTileBitmap(img, map.getTileWidth(), map.getTileHeight(), spacing,
									tileAutoCheck.isSelected());

							newTileset.setTransparentColor(colorButton.getColor());

							newTileset.setTilesetImageFilename(file);
						} catch (Exception e) {
						}
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, e.getMessage(), "Error while importing tileset",
							JOptionPane.ERROR_MESSAGE);
					newTileset = null;
				}
			}

			dispose();
		} else if (source == browseButton) {
			JFileChooser ch = new JFileChooser(path);

			int ret = ch.showOpenDialog(this);
			if (ret == JFileChooser.APPROVE_OPTION) {
				path = ch.getSelectedFile().getAbsolutePath();
				tilebmpFile.setText(path);
			}
		} else if (source == colorButton) {
			ImageColorDialog icd;
			try {
				icd = new ImageColorDialog(ImageIO.read(new File(tilebmpFile.getText())));
				Color c = icd.showDialog();
				if (c != null) {
					colorButton.setColor(c);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(getOwner(), "Error while loading image: " + e.getMessage(),
						"Error while choosing color", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			dispose();
		}
	}

	public void stateChanged(ChangeEvent event) {
		Object source = event.getSource();

		if (source == tilebmpCheck) {
			setUseTileBitmap(tilebmpCheck.isSelected());
		} else if (source == transCheck) {
			colorButton.setEnabled(tilebmpCheck.isSelected() && transCheck.isSelected());
		}
	}

	private void setUseTileBitmap(boolean value) {
		tilebmpFile.setEnabled(value);
		tilebmpFileLabel.setEnabled(value);
		browseButton.setEnabled(value);
		tileSpacing.setEnabled(value);
		spacingLabel.setEnabled(value);
		tileAutoCheck.setEnabled(value);
		transCheck.setEnabled(value);
		colorButton.setEnabled(value && transCheck.isSelected());
	}
}
