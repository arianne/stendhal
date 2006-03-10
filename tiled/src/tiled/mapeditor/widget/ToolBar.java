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


import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import tiled.core.Map;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.util.MapEventAdapter;

/**
 * The toolbar
 * 
 * @author Matthias Totz <mtotz@users.sourceforge.net>
 */
public class ToolBar extends JToolBar implements ActionListener
{
  private static final long serialVersionUID = 1L;
  
  private MapEditor mapEditor;

  private AbstractButton moveButton;

  private AbstractButton paintButton;

  private AbstractButton eraseButton;

  private AbstractButton eyedButton;

  private BrushMenu brushMenu;

  private AbstractButton brushButton;

  private AbstractButton brushExtButton;

  public ToolBar(MapEditor mapEditor, MapEventAdapter mapEventAdapter)
  {
    super(JToolBar.HORIZONTAL);
    
    this.mapEditor = mapEditor;

    Icon iconMove = MapEditor.loadIcon("resources/gimp-tool-move-22.png");
    Icon iconPaint = MapEditor.loadIcon("resources/gimp-tool-pencil-22.png");
    Icon iconErase = MapEditor.loadIcon("resources/gimp-tool-eraser-22.png");
    Icon iconEyed = MapEditor.loadIcon("resources/gimp-tool-color-picker-22.png");
    Icon iconBrush = MapEditor.loadIcon("resources/plus.png");
    Icon iconBrushExt = MapEditor.loadIcon("resources/plus2.png");

    paintButton = createToggleButton(iconPaint, "paint", "Paint");
    eraseButton = createToggleButton(iconErase, "erase", "Erase");
    eyedButton = createToggleButton(iconEyed, "eyed", "Eye dropper");
    moveButton = createToggleButton(iconMove, "move", "Move layer");
    brushMenu = new BrushMenu(mapEditor);
    brushButton = createToggleButton(iconBrush, "create brush", "create brush");
    brushExtButton = createToggleButton(iconBrushExt, "create ext. brush", "create extended brush");

    mapEventAdapter.addListener(moveButton);
    mapEventAdapter.addListener(paintButton);
    mapEventAdapter.addListener(eraseButton);
    mapEventAdapter.addListener(eyedButton);

    setFloatable(false);
    add(moveButton);
    add(paintButton);
    add(eraseButton);
    add(eyedButton);
    addSeparator();
    add(new TButton(mapEditor.zoomInAction));
    add(new TButton(mapEditor.zoomOutAction));
    addSeparator();
    
    add(brushMenu);
    addSeparator();
    add(brushButton);
    add(brushExtButton);
    
    addSeparator();
    add(new MemMonitor());
    
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
    button.addActionListener(this);
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
    eyedButton.setSelected(state == MapEditor.PS_EYED);
    moveButton.setSelected(state == MapEditor.PS_MOVE);
  }
  
  /** sets the map */
  public void setMap(Map map)
  {
    brushMenu.setMap(map);
  }

  /** action handler for the buttons */
  public void actionPerformed(ActionEvent e)
  {
    String command = e.getActionCommand();
    if (command.equals("erase"))
    {
      mapEditor.toggleDeleteTile(true);
    } else
    if (command.equals("paint"))
    {
      mapEditor.toggleDeleteTile(false);
    }
    
  }
  

}
