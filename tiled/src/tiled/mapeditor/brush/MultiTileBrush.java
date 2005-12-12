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
package tiled.mapeditor.brush;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import tiled.core.MultilayerPlane;
import tiled.core.StatefulTile;
import tiled.core.Tile;
import tiled.core.TileLayer;

/**
 * Brush with a custom tile pattern
 * 
 * @author Matthias Totz <mtotz@users.sourceforge.net>
 */
public class MultiTileBrush extends AbstractBrush
{
  private Rectangle cachedBounds;
  /** the list with the tiles */
  private List<StatefulTile> tileList;
  
  
  public MultiTileBrush(MultiTileBrush otherBrush)
  {
    tileList = new ArrayList<StatefulTile>(otherBrush.tileList);
  }

  public MultiTileBrush()
  {
    tileList = new ArrayList<StatefulTile>();
  }
  
  /** adds a tile with position to the brush */
  public void addTile(int x, int y, Tile tile)
  {
    tileList.add(new StatefulTile(new Point(x,y),0,tile));
  }
  
  /** removes a tile from the brush */
  public void removeTile(int x, int y, Tile tile)
  {
    for (StatefulTile tileWrapped : tileList)
    {
      if (tileWrapped.tile == tile)
      {
        tileList.remove(tileWrapped);
        return;
      }
    }
  }
  
  public Rectangle getBounds()
  {
    if (cachedBounds == null)
    {
      cachedBounds = recalculateBounds();
    }
    return cachedBounds;
  }

  /** calculates the bounds */
  private Rectangle recalculateBounds()
  {
    Point p1 = null;
    Point p2 = null;
    
    for (StatefulTile tile : tileList)
    {
      if (p1 == null)
      {
        p1 = new Point(tile.p);
        p2 = new Point(p1);
      }
      else
      {
        if (tile.p.x < p1.x)
          p1.x = tile.p.x;

        if (tile.p.y < p1.y)
          p1.y = tile.p.y;
        
        if (tile.p.x > p2.x)
          p2.x = tile.p.x;

        if (tile.p.y > p2.y)
          p2.y = tile.p.y;
      }
    }
    
    return (p1 == null) ? new Rectangle() : new Rectangle(p1.x,p1.y,p2.x-p1.x+1,p2.y-p1.y+1);
  }

  /** draws the brush */
  public Rectangle commitPaint(MultilayerPlane mp, int x, int y, int initLayer)
  {
    TileLayer tileLayer = (TileLayer) mp.getLayer(initLayer);
    if (tileLayer != null)
    {
      for (StatefulTile tile : tileList)
      {
        tileLayer.setTileAt(tile.p.x+x, tile.p.y+y, tile.tile);
      }
    }
    
    Rectangle rect = new Rectangle(getBounds());
    rect.x += x;
    rect.y += y;

    return rect;  
  }

  /** not implemented */
  public void paint(Graphics g, int x, int y)
  { }

  /** not implemented */
  public boolean equals(Brush b)
  { return false; }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder(); 
    buf.append("[MultiTileBrush: ");
    
    
    for (StatefulTile tile : tileList)
    {
      buf.append(tile);
    }
    
    return buf.toString()+"]";
  }
  
}
