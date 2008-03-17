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
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import tiled.util.TiledConfiguration;
import tiled.mapeditor.widget.*;

public class ConfigurationDialog extends JDialog implements ActionListener, ChangeListener, ItemListener {
	private static final long serialVersionUID = 1243451595127896953L;

	private JButton bOk;
	private int bApply;
	private int bCancel;
	private JPanel layerOps;
	private JPanel generalOps;
	private JPanel tilesetOps;
	private JPanel gridOps;
	private IntegerSpinner undoDepth;
	private IntegerSpinner gridOpacity;
	private JCheckBox cbBinaryEncode;
	private JCheckBox cbCompressLayerData;
	private JCheckBox cbEmbedImages;
	private JCheckBox cbReportIOWarnings;
	private JRadioButton rbEmbedInTiles;
	private JRadioButton rbEmbedInSet;
	private JCheckBox cbGridAA;
	private TiledConfiguration configuration;

	public ConfigurationDialog(JFrame parent) {
		super(parent, "Preferences", true);
		configuration = TiledConfiguration.getInstance();
		init();
		setLocationRelativeTo(parent);
	}

	private void init() {
		// Create primitives

		cbBinaryEncode = new JCheckBox("Use binary encoding");
		cbCompressLayerData = new JCheckBox("Compress layer data (gzip)");
		cbEmbedImages = new JCheckBox("Embed images (png)");
		cbReportIOWarnings = new JCheckBox("Report I/O messages");
		rbEmbedInTiles = new JRadioButton("Embed images in tiles");
		rbEmbedInSet = new JRadioButton("Use Tileset (shared) images");
		ButtonGroup bg = new ButtonGroup();
		bg.add(rbEmbedInTiles);
		bg.add(rbEmbedInSet);
		undoDepth = new IntegerSpinner();
		cbGridAA = new JCheckBox("Antialiasing");
		gridOpacity = new IntegerSpinner(0, 0, 255);
		// gridColor = new JColorChooser();
		cbBinaryEncode.addItemListener(this);
		cbCompressLayerData.addItemListener(this);
		cbEmbedImages.addItemListener(this);
		cbReportIOWarnings.addItemListener(this);
		cbGridAA.addItemListener(this);
		undoDepth.addChangeListener(this);
		gridOpacity.addChangeListener(this);
		// gridColor.addChangeListener(this);

		cbBinaryEncode.setActionCommand("tmx.save.encodeLayerData");
		cbCompressLayerData.setActionCommand("tmx.save.layerCompression");
		// cbEmbedImages.setActionCommand("tmx.save.embedImages");
		cbReportIOWarnings.setActionCommand("tiled.report.io");

		rbEmbedInTiles.setActionCommand("tmx.save.embedImages");
		rbEmbedInTiles.setEnabled(false);
		rbEmbedInSet.setActionCommand("tmx.save.tileSetImages");
		rbEmbedInSet.setEnabled(false);
		undoDepth.setName("tiled.undo.depth");
		cbGridAA.setActionCommand("tiled.grid.antialias");
		gridOpacity.setName("tiled.grid.opacity");
		// gridColor.setName("tiled.grid.color");

		bOk = new JButton("OK");
		bApply = new JButton("Apply");
		bCancel = new JButton("Cancel");
		bOk.addActionListener(this);
		bApply.addActionListener(this);
		bCancel.addActionListener(this);
		bApply.setEnabled(false);

		/* LAYER OPTIONS */
		layerOps = new VerticalStaticJPanel();
		layerOps.setLayout(new GridBagLayout());
		layerOps.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Layer Options"),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		layerOps.add(cbBinaryEncode, c);
		c.gridy = 1;
		c.insets = new Insets(0, 10, 0, 0);
		layerOps.add(cbCompressLayerData, c);

		/* GENERAL OPTIONS */
		generalOps = new VerticalStaticJPanel();
		generalOps.setLayout(new GridBagLayout());
		generalOps.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		generalOps.add(new JLabel("Undo Depth:  "), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.weightx = 1;
		generalOps.add(undoDepth, c);
		c.gridy = 1;
		c.gridx = 0;
		generalOps.add(cbReportIOWarnings, c);

		/* TILESET OPTIONS */
		tilesetOps = new VerticalStaticJPanel();
		tilesetOps.setLayout(new GridBagLayout());
		tilesetOps.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Tileset Options"),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		tilesetOps.add(cbEmbedImages, c);
		c.gridy = 1;
		c.insets = new Insets(0, 10, 0, 0);
		tilesetOps.add(rbEmbedInTiles, c);
		c.gridy = 2;
		c.insets = new Insets(0, 10, 0, 0);
		tilesetOps.add(rbEmbedInSet, c);

		/* GRID OPTIONS */
		gridOps = new VerticalStaticJPanel();
		gridOps.setLayout(new GridBagLayout());
		gridOps.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		c = new GridBagConstraints();
		gridOps.add(new JLabel("Opacity:  "), c);
		c.weightx = 1;
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		gridOps.add(gridOpacity, c);
		c.gridwidth = 2;
		c.gridy = 1;
		c.gridx = 0;
		gridOps.add(cbGridAA, c);
		// c.gridy = 2; c.weightx = 0;
		// gridOps.add(new JLabel("Color: "), c);
		// c.gridx = 1;
		// gridOps.add(gridColor, c);

		/* BUTTONS PANEL */
		JPanel buttons = new VerticalStaticJPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(Box.createGlue());
		buttons.add(bOk);
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		buttons.add(bApply);
		buttons.add(Box.createRigidArea(new Dimension(5, 0)));
		buttons.add(bCancel);

		JPanel saving = new JPanel();
		saving.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		saving.setLayout(new BoxLayout(saving, BoxLayout.Y_AXIS));
		saving.add(layerOps);
		saving.add(tilesetOps);

		JPanel general = new JPanel();
		general.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		general.setLayout(new BoxLayout(general, BoxLayout.Y_AXIS));
		general.add(generalOps);

		JPanel grid = new JPanel();
		grid.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
		grid.add(gridOps);

		// Put together the tabs

		JTabbedPane perfs = new JTabbedPane();
		perfs.addTab("General", general);
		perfs.addTab("Saving", saving);
		perfs.addTab("Grid", grid);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.add(perfs);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPanel.add(buttons);

		getContentPane().add(mainPanel);
		getRootPane().setDefaultButton(bOk);
		pack();
	}

	public void configure() {
		updateFromConf();
		setVisible(true);
	}

	private void updateFromConf() {
		undoDepth.setValue(configuration.getIntValue(undoDepth.getName(), 30));
		gridOpacity.setValue(configuration.getIntValue(gridOpacity.getName(), 255));

		if (configuration.keyHasValue("tmx.save.embedImages", "1")) {
			cbEmbedImages.setSelected(true);
			rbEmbedInTiles.setSelected(true);
		}

		// Handle checkboxes
		updateFromConf(layerOps);
		updateFromConf(generalOps);
		updateFromConf(tilesetOps);
		updateFromConf(gridOps);

		cbCompressLayerData.setEnabled(cbBinaryEncode.isSelected());

		bApply.setEnabled(false);
	}

	private void updateFromConf(Container container) {
		for (int i = 0; i < container.getComponentCount(); i++) {
			Component c = container.getComponent(i);
			try {
				AbstractButton b = (AbstractButton) c;
				if (b.getClass().equals(JCheckBox.class)) {
					if (configuration.keyHasValue(b.getActionCommand(), "1")) {
						b.setSelected(true);
					}
				}
			} catch (ClassCastException e) {
			}
		}
	}

	private void processOptions() {
		configuration.addConfigPair(undoDepth.getName(), "" + undoDepth.intValue());
		configuration.addConfigPair(gridOpacity.getName(), "" + gridOpacity.intValue());

		// Handle checkboxes
		processOptions(layerOps);
		processOptions(generalOps);
		processOptions(tilesetOps);
		processOptions(gridOps);

		bApply.setEnabled(false);
	}

	private void processOptions(Container container) {
		for (int i = 0; i < container.getComponentCount(); i++) {
			Component c = container.getComponent(i);
			try {
				AbstractButton b = (AbstractButton) c;
				if (b.getClass().equals(JCheckBox.class)) {
					configuration.addConfigPair(b.getActionCommand(), b.isSelected() ? "1" : "0");
				}
			} catch (ClassCastException e) {
			}
		}
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == bOk) {
			processOptions();
			dispose();
		} else if (source == bCancel) {
			dispose();
		} else if (source == bApply) {
			processOptions();
		}
	}

	public void stateChanged(ChangeEvent event) {
		bApply.setEnabled(true);
	}

	public void itemStateChanged(ItemEvent event) {
		Object source = event.getItemSelectable();

		if (source == cbBinaryEncode) {
			cbCompressLayerData.setEnabled(cbBinaryEncode.isSelected());
		} else if (source == cbEmbedImages) {
			rbEmbedInTiles.setSelected(cbEmbedImages.isSelected() && rbEmbedInTiles.isSelected());
			rbEmbedInTiles.setEnabled(cbEmbedImages.isSelected());
			rbEmbedInSet.setSelected(cbEmbedImages.isSelected() && rbEmbedInSet.isSelected());
			rbEmbedInSet.setEnabled(cbEmbedImages.isSelected());
		}

		bApply.setEnabled(true);
	}
}
