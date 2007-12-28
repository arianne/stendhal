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

package tiled.mapeditor.widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.actions.AddLayerAction;
import tiled.mapeditor.actions.DelLayerAction;
import tiled.mapeditor.actions.DuplicateLayerAction;
import tiled.mapeditor.actions.MoveLayerDownAction;
import tiled.mapeditor.actions.MoveLayerUpAction;
import tiled.mapeditor.brush.Brush;
import tiled.mapeditor.util.LayerTableModel;
import tiled.mapeditor.util.MapEventAdapter;

/**
 * Panel with the Layer Table (aka Data Panel) It also shows the minimap.
 * 
 * @author Matthias Totz <mtotz@users.sourceforge.net>
 */
public class LayerEditPanel extends JPanel implements ListSelectionListener, ChangeListener, TableModelListener {
	private static final long serialVersionUID = 314584416481357898L;

	private MapEditor mapEditor;

	private MiniMapViewer miniMap;

	private JTable layerTable;
	private JSlider opacitySlider;

	private int currentLayer;

	// the buttons
	private AbstractButton layerAddButton;
	private AbstractButton layerDelButton;
	private AbstractButton layerCloneButton;
	private AbstractButton layerUpButton;
	private AbstractButton layerDownButton;

	private BrushPreview brushPreview;

	/**
	 * 
	 */
	public LayerEditPanel(MapEditor mapEditor, MapEventAdapter mapEventAdapter) {
		super();
		this.mapEditor = mapEditor;

		miniMap = new MiniMapViewer();
		miniMap.setMainPanel(mapEditor.mapScrollPane);
		JScrollPane miniMapSp = new JScrollPane();
		miniMapSp.getViewport().setView(miniMap);
		miniMapSp.setMinimumSize(new Dimension(120, 120));

		// Layer table
		LayerTableModel tableModel = new LayerTableModel(mapEditor.currentMap);
		tableModel.addTableModelListener(this);
		layerTable = new JTable(tableModel);
		layerTable.getColumnModel().getColumn(0).setPreferredWidth(32);
		layerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		layerTable.getSelectionModel().addListSelectionListener(this);
		JScrollPane layerTableScrollPane = new JScrollPane(layerTable);
		layerTableScrollPane.setMaximumSize(new Dimension(1000, 200));
		layerTableScrollPane.setPreferredSize(new Dimension(120, 120));

		// Opacity slider
		opacitySlider = new JSlider(0, 100, 100);
		opacitySlider.addChangeListener(this);
		JLabel opacityLabel = new JLabel("Opacity: ");
		opacityLabel.setLabelFor(opacitySlider);

		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
		sliderPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		sliderPanel.add(opacityLabel);
		sliderPanel.add(opacitySlider);
		sliderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, sliderPanel.getPreferredSize().height));

		// Layer buttons
		layerAddButton = createButton(mapEditor.actionManager.getAction(AddLayerAction.class));
		layerDelButton = createButton(mapEditor.actionManager.getAction(DelLayerAction.class));
		layerCloneButton = createButton(mapEditor.actionManager.getAction(DuplicateLayerAction.class));
		layerUpButton = createButton(mapEditor.actionManager.getAction(MoveLayerUpAction.class));
		layerDownButton = createButton(mapEditor.actionManager.getAction(MoveLayerDownAction.class));

		mapEventAdapter.addListener(layerAddButton);

		JPanel layerButtons = new JPanel();
		layerButtons.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		layerButtons.add(layerAddButton, c);
		layerButtons.add(layerUpButton, c);
		layerButtons.add(layerDownButton, c);
		layerButtons.add(layerCloneButton, c);
		layerButtons.add(layerDelButton, c);
		layerButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE, layerButtons.getPreferredSize().height));

		JPanel layerPanel = new JPanel();
		layerPanel.setPreferredSize(new Dimension(120, 120));
		layerPanel.setLayout(new BoxLayout(layerPanel, BoxLayout.Y_AXIS));
		layerPanel.add(sliderPanel);
		layerPanel.add(layerTableScrollPane);
		layerPanel.add(layerButtons);

		setLayout(new BorderLayout());
		add(miniMapSp, BorderLayout.NORTH);

		brushPreview = new BrushPreview();

		JTabbedPane south = new JTabbedPane();
		south.addTab("Layer", layerPanel);
		south.addTab("Brush Preview", brushPreview);
		add(south, BorderLayout.CENTER);

	}

	/** creates an image-only button. */
	private AbstractButton createButton(Action action) {
		JButton button = new JButton(action);
		button.setText("");
		button.setMargin(new Insets(0, 0, 0, 0));
		return button;
	}

	public int getCurrentLayer() {
		return currentLayer;
	}

	/** updates the map (called when a new map is loaded). */
	public void setMap(int currentLayer, Map currentMap) {
		if (layerTable.isEditing()) {
			layerTable.getCellEditor(layerTable.getEditingRow(), layerTable.getEditingColumn()).cancelCellEditing();
		}
		((LayerTableModel) layerTable.getModel()).setMap(currentMap);

		if (currentMap != null) {
			if (currentMap.getTotalLayers() > 0 && currentLayer == -1) {
				currentLayer = 0;
			}

			setLayer(currentLayer, currentMap);
		}
	}

	/** updates the selected layer. */
	public void setLayer(int currentLayer, Map currentMap) {
		if (currentMap != null) {
			int totalLayers = currentMap.getTotalLayers();
			if (totalLayers > currentLayer && currentLayer >= 0) {
				this.currentLayer = currentLayer;
				layerTable.changeSelection(totalLayers - currentLayer - 1, 0, false, false);
			}
		}
	}

	/** enables/disables the layer operation buttons. */
	public void updateLayerOperations(boolean validSelection, boolean notBottom, boolean notTop) {
		layerCloneButton.setEnabled(validSelection);
		layerDelButton.setEnabled(validSelection);
		layerUpButton.setEnabled(notTop);
		layerDownButton.setEnabled(notBottom);

		opacitySlider.setEnabled(validSelection);
	}

	/** returns the minimap panel. */
	public MiniMapViewer getMiniMap() {
		return miniMap;
	}

	//
	// callback interfaces
	//
	public void valueChanged(ListSelectionEvent e) {
		int selectedRow = layerTable.getSelectedRow();

		// At the moment, this can only be a new layer selection
		if (mapEditor.currentMap != null && selectedRow >= 0) {
			mapEditor.setCurrentLayer(mapEditor.currentMap.getTotalLayers() - selectedRow - 1);

			// set opacity
			float opacity = mapEditor.getCurrentLayer().getOpacity();
			opacitySlider.setValue((int) (opacity * 100));
		} else {
			mapEditor.setCurrentLayer(-1);
		}

		mapEditor.updateLayerOperations();
	}

	public void stateChanged(ChangeEvent e) {
		// At the moment, this can only be movement in the opacity slider

		if (mapEditor.currentMap != null && currentLayer >= 0) {
			// set opacity
			MapLayer layer = mapEditor.getCurrentLayer();
			layer.setOpacity(opacitySlider.getValue() / 100.0f);
			// redraw the map
			mapEditor.mapEditPanel.repaint();
			// refresh minimap
			mapEditor.mapEditPanel.getMapView().updateMinimapImage(
					new Rectangle(0, 0, layer.getWidth(), layer.getHeight()));
			miniMap.repaint();
		}
	}

	/** called when a new Brush is selected. */
	public void updateBrush(Brush brush) {
		brushPreview.updateBrush(brush);
	}

	/** only consider. */
	public void tableChanged(TableModelEvent e) {
		mapEditor.mapEditPanel.repaint();
	}
}
