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
package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tiled.core.TileGroup;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.brush.TileGroupBrush;
import tiled.mapeditor.widget.TileGroupButton;

/**
 * @author mtotz
 *
 */
public class SelectBrushAction extends AbstractAction
{
  private static final long serialVersionUID = -1872602576673195898L;

  private MapEditor mapEditor;
  
  public SelectBrushAction(MapEditor mapEditor)
  {
    super("Select Brush");
    this.mapEditor = mapEditor;
  }

  public void actionPerformed(ActionEvent e)
  {
    Object source = e.getSource();
    
    if (source != null && source instanceof TileGroupButton)
    {
      TileGroupButton button = (TileGroupButton) source;
      TileGroup group = button.getTileGroup();
      // set the brush
      mapEditor.setBrush(new TileGroupBrush(group));
    }
  }
}
