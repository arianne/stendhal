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


import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import tiled.mapeditor.MapEditor;
import tiled.mapeditor.util.MapEventAdapter;

/**
 * The toolbar
 * 
 * @author Matthias Totz <mtotz@users.sourceforge.net>
 */
public class ToolBar extends JToolBar
{
  private static final long serialVersionUID = -7604870611719476825L;
  
  private MapEditor mapEditor;

  private AbstractButton paintButton;

  private AbstractButton eraseButton;

  private AbstractButton pourButton;

  private AbstractButton eyedButton;

  private AbstractButton marqueeButton;

  private AbstractButton moveButton;

  private AbstractButton objectMoveButton;

  public ToolBar(MapEditor mapEditor, MapEventAdapter mapEventAdapter)
  {
    super(JToolBar.HORIZONTAL);
    
    this.mapEditor = mapEditor;

    Icon iconMove = MapEditor.loadIcon("resources/gimp-tool-move-22.png");
    Icon iconPaint = MapEditor.loadIcon("resources/gimp-tool-pencil-22.png");
    Icon iconErase = MapEditor.loadIcon("resources/gimp-tool-eraser-22.png");
    Icon iconPour = MapEditor.loadIcon("resources/gimp-tool-bucket-fill-22.png");
    Icon iconEyed = MapEditor.loadIcon("resources/gimp-tool-color-picker-22.png");
    Icon iconMarquee = MapEditor.loadIcon("resources/gimp-tool-rect-select-22.png");
    Icon iconMoveObject = MapEditor.loadIcon("resources/gimp-tool-object-move-22.png");

    paintButton = createToggleButton(iconPaint, "paint", "Paint");
    eraseButton = createToggleButton(iconErase, "erase", "Erase");
    pourButton = createToggleButton(iconPour, "pour", "Fill");
    eyedButton = createToggleButton(iconEyed, "eyed", "Eye dropper");
    marqueeButton = createToggleButton(iconMarquee, "marquee", "Select");
    moveButton = createToggleButton(iconMove, "move", "Move layer");
    objectMoveButton = createToggleButton(iconMoveObject, "moveobject", "Move Object");

    mapEventAdapter.addListener(moveButton);
    mapEventAdapter.addListener(paintButton);
    mapEventAdapter.addListener(eraseButton);
    mapEventAdapter.addListener(pourButton);
    mapEventAdapter.addListener(eyedButton);
    mapEventAdapter.addListener(marqueeButton);
    mapEventAdapter.addListener(objectMoveButton);

    setFloatable(false);
    add(moveButton);
    add(paintButton);
    add(eraseButton);
    add(pourButton);
    add(eyedButton);
    add(marqueeButton);
    add(Box.createRigidArea(new Dimension(5, 5)));
    //TODO: put this back when working...
    //add(objectMoveButton);
    //add(Box.createRigidArea(new Dimension(0, 5)));
    add(new TButton(mapEditor.zoomInAction));
    add(new TButton(mapEditor.zoomOutAction));

  }
  
  private AbstractButton createToggleButton(Icon icon, String command,String tt)
  {
    return createButton(icon, command, true, tt);
  }

  private AbstractButton createButton(Icon icon, String command, boolean toggleButton, String tt)
  {
    AbstractButton button;
    if (toggleButton) {
        button = new JToggleButton("", icon);
    } else {
        button = new JButton("", icon);
    }
    button.setMargin(new Insets(0, 0, 0, 0));
    button.setActionCommand(command);
    button.addActionListener(mapEditor);
    if (tt != null) {
        button.setToolTipText(tt);
    }
    return button;
  }

  /**
   * @param state
   */
  public void setButtonStates(int state)
  {
    // Select the matching button
    paintButton.setSelected(state == MapEditor.PS_PAINT);
    eraseButton.setSelected(state == MapEditor.PS_ERASE);
    pourButton.setSelected(state == MapEditor.PS_POUR);
    eyedButton.setSelected(state == MapEditor.PS_EYED);
    marqueeButton.setSelected(state == MapEditor.PS_MARQUEE);
    moveButton.setSelected(state == MapEditor.PS_MOVE);
    objectMoveButton.setSelected(state == MapEditor.PS_MOVEOBJ);
  }
  
  

}
