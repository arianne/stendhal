/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package tiled.mapeditor.widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.util.LayerTableModel;
import tiled.mapeditor.util.MapEventAdapter;

/**
 * Panel with the Layer Table (aka Data Panel)
 * It also shows the minimap.
 * 
 * @author Matthias Totz <mtotz@users.sourceforge.net>
 */
public class LayerEditPanel extends JPanel implements ListSelectionListener, ChangeListener
{
  private static final long serialVersionUID = 314584416481357898L;
  
  private MapEditor mapEditor;

  private MiniMapViewer miniMap;

  private JTable layerTable;

  private JSlider opacitySlider;
  
  private int currentLayer;

  private AbstractButton layerAddButton;

  private AbstractButton layerDelButton;

  private AbstractButton layerCloneButton;

  private AbstractButton layerUpButton;

  private AbstractButton layerDownButton;

  /**
   * 
   */
  public LayerEditPanel(MapEditor mapEditor, MapEventAdapter mapEventAdapter)
  {
    super();
    this.mapEditor = mapEditor;
    
    // Try to load the icons
    Icon imgAdd = MapEditor.loadIcon("resources/gnome-new.png");
    Icon imgDel = MapEditor.loadIcon("resources/gnome-delete.png");
    Icon imgDup = MapEditor.loadIcon("resources/gimp-duplicate-16.png");
    Icon imgUp = MapEditor.loadIcon("resources/gnome-up.png");
    Icon imgDown = MapEditor.loadIcon("resources/gnome-down.png");

    //navigation and tool options
    //TODO: the minimap is prohibitively slow, need to speed this up before it can be used
    miniMap = new MiniMapViewer();
    miniMap.setMainPanel(mapEditor.mapScrollPane);
    JScrollPane miniMapSp = new JScrollPane();
    miniMapSp.getViewport().setView(miniMap);
    miniMapSp.setMinimumSize(new Dimension(120, 120));

    // Layer table
    layerTable = new JTable(new LayerTableModel(mapEditor.currentMap));
    layerTable.getColumnModel().getColumn(0).setPreferredWidth(32);
    layerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    layerTable.getSelectionModel().addListSelectionListener(this);
    JScrollPane layerTableScrollPane = new JScrollPane(layerTable);
    layerTableScrollPane.setMaximumSize(new Dimension(1000,200));
    layerTableScrollPane.setPreferredSize(new Dimension(120,120));
    
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
    sliderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,sliderPanel.getPreferredSize().height));

    // Layer buttons
    layerAddButton = createButton(imgAdd, "Add Layer", "Add Layer");
    layerDelButton = createButton(imgDel, "Delete Layer", "Delete Layer");
    layerCloneButton = createButton(imgDup, "Duplicate Layer","Duplicate Layer");
    layerUpButton = createButton(imgUp, "Move Layer Up", "Move Layer Up");
    layerDownButton = createButton(imgDown, "Move Layer Down","Move Layer Down");

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
    layerButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE,layerButtons.getPreferredSize().height));

    JPanel layerPanel = new JPanel();
    layerPanel.setPreferredSize(new Dimension(120, 120));
    layerPanel.setLayout(new BoxLayout(layerPanel,BoxLayout.Y_AXIS));
    layerPanel.add(sliderPanel);
    layerPanel.add(layerTableScrollPane);
    layerPanel.add(layerButtons);
    

    setLayout(new BorderLayout());
    add(miniMapSp, BorderLayout.NORTH);
    add(layerPanel, BorderLayout.CENTER);
    
  }
  
  public int getCurrentLayer()
  {
    return currentLayer;
  }
  
  private AbstractButton createButton(Icon icon, String command, String tt)
  {
    return createButton(icon, command, false, tt);
  }

  private AbstractButton createButton(Icon icon, String command, boolean toggleButton, String tt)
  {
    AbstractButton button;
    if (toggleButton)
    {
        button = new JToggleButton("", icon);
    }
    else
    {
        button = new JButton("", icon);
    }
    button.setMargin(new Insets(0, 0, 0, 0));
    button.setActionCommand(command);
    button.addActionListener(mapEditor);
    if (tt != null)
    {
        button.setToolTipText(tt);
    }
    return button;
  }
  
  public void updateLayerTable(int currentLayer, Map currentMap)
  {
    if (layerTable.isEditing())
    {
      layerTable.getCellEditor(layerTable.getEditingRow(),layerTable.getEditingColumn()).cancelCellEditing();
    }
    ((LayerTableModel)layerTable.getModel()).setMap(currentMap);

    if (currentMap != null)
    {
      if (currentMap.getTotalLayers() > 0 && currentLayer == -1)
      {
        currentLayer = 0;
      }

      setCurrentLayer(currentLayer,currentMap);
    }
  }
  
  public void setCurrentLayer(int currentLayer, Map currentMap)
  {
    if (currentMap != null)
    {
      int totalLayers = currentMap.getTotalLayers();
      if (totalLayers > currentLayer && currentLayer >= 0)
      {
        mapEditor.currentLayer = currentLayer;
        this.currentLayer = currentLayer;
        layerTable.changeSelection(totalLayers - currentLayer - 1, 0,false, false);
      }
    }
  }
  
  public void updateLayerOperations(boolean validSelection, boolean notBottom, boolean notTop)
  {
    layerCloneButton.setEnabled(validSelection);
    layerDelButton.setEnabled(validSelection);
    layerUpButton.setEnabled(notTop);
    layerDownButton.setEnabled(notBottom);

    opacitySlider.setEnabled(validSelection);
  }

  
  public void valueChanged(ListSelectionEvent e)
  {
    int selectedRow = layerTable.getSelectedRow();

    // At the moment, this can only be a new layer selection
    if (mapEditor.currentMap != null && selectedRow >= 0)
    {
      mapEditor.currentLayer = mapEditor.currentMap.getTotalLayers() - selectedRow - 1;

      float opacity = mapEditor.getCurrentLayer().getOpacity();
      opacitySlider.setValue((int)(opacity * 100));
    }
    else
    {
      mapEditor.currentLayer = -1;
    }

    mapEditor.updateLayerOperations();
  }

  public void stateChanged(ChangeEvent e)
  {
    // At the moment, this can only be movement in the opacity slider

    if (mapEditor.currentMap != null && currentLayer >= 0)
    {
      MapLayer layer = mapEditor.getCurrentLayer();
      layer.setOpacity(opacitySlider.getValue() / 100.0f);
    }
}
  

}
