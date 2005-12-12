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
package tiled.mapeditor.builder;

import java.awt.Point;
import java.awt.Rectangle;

import tiled.core.Map;
import tiled.mapeditor.brush.Brush;

/**
 * This builder simply places the current brush at the building location
 * 
 * @author mtotz
 */
public class SimpleBuilder extends AbstractBuilder
{
  /**
   * Creates a new builder. The map/brush/startLayer must be set before using
   * the builder 
   */
  public SimpleBuilder()
  {
    super();
  }

  /** creates a new builder */
  public SimpleBuilder(Map map, Brush brush, int startLayer)
  {
    super(map,brush, startLayer);
  }
  
  /** draws the brush */
  private Rectangle draw(Point tile, boolean ignoreBrushSize)
  {
    Rectangle brushSize = brush.getBounds();
    
    if (lastPoint == null)
    {
      lastPoint = new Point(tile);
    }
    
    int dx = tile.x - lastPoint.x;
    int dy = tile.y - lastPoint.y;
    
    // do not override the last brush 
    if (dx % brushSize.width == 0 && dy % brushSize.height == 0 || ignoreBrushSize)
    {
      Rectangle modified = brush.commitPaint(map,tile.x,tile.y,startLayer);
      return modified;
    }
    return null;
  }
  

  /** starts the builder. simply commits the brush to the given tile */
  public Rectangle startBuilder(Point tile)
  {
    Rectangle modified = draw(tile,true);
    isStarted = true;
    return modified;
  }

  /** commits the brush to the given tile */
  public Rectangle moveBuilder(Point tile)
  {
    if (!tile.equals(lastPoint))
    {
      return draw(tile,false);
    }
    return null;
  }

  /** finished the builder. the last brush commit. */
  public Rectangle finishBuilder(Point tile)
  {
    Rectangle modified = null;
    if (!tile.equals(lastPoint))
    {
      modified = draw(tile,false);
    }
    isStarted = false;
    updateLastPoint(null);
    return modified;
  }

  /** returns the brush's bounds */
  public Rectangle getBounds()
  {
    return brush.getBounds();
  }

}
