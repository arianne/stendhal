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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import tiled.core.Map;
import tiled.core.TileGroup;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.util.MapChangeListener;
import tiled.mapeditor.util.MapChangedEvent;

/**
 * Shows all user brushes of this map
 * 
 * @author mtotz
 */
public class BrushList extends JPanel implements MapChangeListener
{
  private static final long serialVersionUID = -5536711651529912724L;

  /** the map */
  private Map map;

  private MapEditor mapEditor;

  public BrushList(MapEditor mapEditor)
  {
    this.mapEditor = mapEditor;
    setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
  }
  
  /** sets the map */
  public void setMap(Map map)
  {
    this.map = map;
    if (map != null)
    {
      map.addMapChangeListener(this);
    }
  }

  protected void paintComponent(Graphics g)
  {
    Rectangle rect = g.getClipBounds();
    g.setColor(Color.BLACK);
    g.fillRect(rect.x,rect.y,rect.width, rect.height);
    
    if (map == null)
      return;
    
//    int maxx = 0;
//    int maxy = 0;
//    int currenty = 0;
//    
//    List<List<StatefulTile>> userBrushes = map.getUserBrushes_old();
//    if (userBrushes.size() > 0)
//    {
//      for (List<StatefulTile> list : userBrushes)
//      {
//        if (list.size() > 0)
//        {
//          Tile firstTile = list.get(0).tile;
//          int width = firstTile.getWidth();
//          int height = firstTile.getHeight();
//          int brushHeight = 0;
//          int brushWidth = 0;
//  
//          for (StatefulTile tile : list)
//          {
//            tile.tile.draw(g,tile.p.x*width,currenty+tile.p.y*height,1.0);
//            if (tile.p.y > brushHeight)
//            {
//              brushHeight = tile.p.y; 
//            }
//            
//            if (tile.p.x > brushWidth)
//            {
//              brushWidth = tile.p.x;
//            }
//          }
//          brushHeight++;
//          brushWidth++;
//          
//          g.setColor(Color.YELLOW);
//          g.drawRect(0, currenty, brushWidth * width,height * brushHeight);
//          
//          // update height
//          currenty += height * brushHeight;
//          // update width
//          if (brushWidth*width > maxx)
//          {
//            maxx = brushWidth*width;
//          }
//        }
//      }
//      maxy = currenty;
//      setPreferredSize(new Dimension(maxx+50,maxy));
//      revalidate();
//    }
  }

  /** refreshes the brush list once the map changes */
  public void mapChanged(MapChangedEvent e)
  {
    if (e.getType() != MapChangedEvent.Type.BRUSHES)
      return;
    
    List<TileGroup> groupList = map.getUserBrushes();
    
    removeAll();
    
    int ypos = 0;
    for (TileGroup group : groupList)
    {
      TileGroupButton button = new TileGroupButton(mapEditor,mapEditor.selectBrushAction,group);
      
      add(button);
      
      ypos += button.getPreferredSize().height;
    }
    
    revalidate();
    

//    List<List<StatefulTile>> userBrushes = map.getUserBrushes_old();
//
//    int maxx = 0;
//    int maxy = 0;
//    int currenty = 0;
//
//    // find the size
//    for (List<StatefulTile> list : userBrushes)
//    {
//      Tile firstTile = list.get(0).tile;
//      int width = firstTile.getWidth();
//      int height = firstTile.getHeight();
//      int brushHeight = 0;
//
//      for (StatefulTile tile : list)
//      {
//        if (tile.p.y > brushHeight)
//        {
//          brushHeight = tile.p.y; 
//        }
//        
//        if (tile.p.x*width > maxx)
//        {
//          maxx = tile.p.x*width;
//        }
//      }
//      currenty += height * (brushHeight+1);
//    }
//    maxy = currenty;
//    // reset the size
//    setPreferredSize(new Dimension(maxx+50,maxy));
//    revalidate();
  }
    
}

