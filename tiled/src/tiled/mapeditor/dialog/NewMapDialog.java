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
import javax.swing.*;

import tiled.core.*;
import tiled.mapeditor.widget.*;

public class NewMapDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -5637072413306626416L;

	private Map newMap;
	private IntegerSpinner mapWidth, mapHeight;
	private IntegerSpinner tileWidth, tileHeight;
	private JComboBox mapTypeChooser;

	public NewMapDialog(JFrame parent) {
		super(parent, "New Map", true);
		init();
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}

	private void init() {
		// Create the primitives

		mapWidth = new IntegerSpinner(64, 1);
		mapHeight = new IntegerSpinner(64, 1);
		tileWidth = new IntegerSpinner(35, 1);
		tileHeight = new IntegerSpinner(35, 1);

		// Map size fields

		JPanel mapSize = new VerticalStaticJPanel();
		mapSize.setLayout(new GridBagLayout());
		mapSize.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Map size"),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5, 0, 0, 0);
		mapSize.add(new JLabel("Width: "), c);
		c.gridy = 1;
		mapSize.add(new JLabel("Height: "), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		mapSize.add(mapWidth, c);
		c.gridy = 1;
		mapSize.add(mapHeight, c);

		// Tile size fields

		JPanel tileSize = new VerticalStaticJPanel();
		tileSize.setLayout(new GridBagLayout());
		tileSize.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Tile size"),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		tileSize.add(new JLabel("Width: "), c);
		c.gridy = 1;
		tileSize.add(new JLabel("Height: "), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		tileSize.add(tileWidth, c);
		c.gridy = 1;
		tileSize.add(tileHeight, c);

		// OK and Cancel buttons

		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		JPanel buttons = new VerticalStaticJPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(Box.createGlue());
		buttons.add(okButton);
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		buttons.add(cancelButton);

		// Map type and name inputs

		mapTypeChooser = new JComboBox();
		mapTypeChooser.addItem("Orthogonal");
		mapTypeChooser.addItem("Isometric");
		mapTypeChooser.addItem("Shifted (iso and hex)");
		// TODO: Enable when view is implemented
		// mapTypeChooser.addItem("Oblique");
		mapTypeChooser.addItem("Hexagonal (experimental)");

		JPanel miscPropPanel = new VerticalStaticJPanel();
		miscPropPanel.setLayout(new GridBagLayout());
		miscPropPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		miscPropPanel.add(new JLabel("Map type: "), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		miscPropPanel.add(mapTypeChooser, c);

		// Putting two size panels next to eachother

		JPanel sizePanels = new JPanel();
		sizePanels.setLayout(new BoxLayout(sizePanels, BoxLayout.X_AXIS));
		sizePanels.add(mapSize);
		sizePanels.add(Box.createRigidArea(new Dimension(5, 0)));
		sizePanels.add(tileSize);

		// Main panel

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.add(miscPropPanel);
		mainPanel.add(sizePanels);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPanel.add(Box.createGlue());
		mainPanel.add(buttons);

		getContentPane().add(mainPanel);
		getRootPane().setDefaultButton(okButton);
	}

	public Map create() {
		setVisible(true);
		return newMap;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			int w = mapWidth.intValue();
			int h = mapHeight.intValue();
			int twidth = tileWidth.intValue();
			int theight = tileHeight.intValue();
			int orientation = Map.MDO_ORTHO;
			String mapTypeString = (String) mapTypeChooser.getSelectedItem();

			if (mapTypeString.equals("Isometric")) {
				orientation = Map.MDO_ISO;
			} else if (mapTypeString.equals("Oblique")) {
				orientation = Map.MDO_OBLIQUE;
			} else if (mapTypeString.equals("Hexagonal (experimental)")) {
				orientation = Map.MDO_HEX;
			} else if (mapTypeString.equals("Shifted (iso and hex)")) {
				orientation = Map.MDO_SHIFTED;
			}

			newMap = new Map(w, h);
			newMap.addLayer();
			newMap.setTileWidth(twidth);
			newMap.setTileHeight(theight);
			newMap.setOrientation(orientation);

			dispose();
		} else {
			dispose();
		}
	}
}
