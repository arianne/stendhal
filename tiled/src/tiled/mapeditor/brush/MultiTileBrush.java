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
  private List<TileWrapper> tileList;
  
  
  public MultiTileBrush(MultiTileBrush otherBrush)
  {
    tileList = new ArrayList<TileWrapper>(otherBrush.tileList);
  }

  public MultiTileBrush()
  {
    tileList = new ArrayList<TileWrapper>();
  }
  
  /** adds a tile with position to the brush */
  public void addTile(int x, int y, Tile tile)
  {
    tileList.add(new TileWrapper(x,y,tile));
  }
  
  /** removes a tile from the brush */
  public void removeTile(int x, int y, Tile tile)
  {
    for (TileWrapper tileWrapped : tileList)
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
    
    for (TileWrapper tile : tileList)
    {
      if (p1 == null)
      {
        p1 = new Point(tile.x,tile.y);
        p2 = new Point(p1);
      }
      else
      {
        if (tile.x < p1.x)
          p1.x = tile.x;

        if (tile.y < p1.y)
          p1.y = tile.y;
        
        if (tile.x > p2.x)
          p2.x = tile.x;

        if (tile.y > p2.y)
          p2.y = tile.y;
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
      for (TileWrapper tile : tileList)
      {
        tileLayer.setTileAt(tile.x+x, tile.y+y, tile.tile);
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
    
    
    for (TileWrapper tile : tileList)
    {
      buf.append(tile);
    }
    
    return buf.toString()+"]";
  }
  

  /** stores coordinates for the tile   */
  public class TileWrapper
  {
    public int x;
    public int y;
    public Tile tile;

    public TileWrapper(int x, int y, Tile tile)
    {
      this.x = x;
      this.y = y;
      this.tile = tile;
    }
    
    public String toString()
    {
      return "[TileWrapper: "+x+"x"+y+" "+tile+"]";
    }
    
  }
  
}
