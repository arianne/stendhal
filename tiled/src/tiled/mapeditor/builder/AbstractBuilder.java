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
 * Default builder base implementation. Takes care of handling the brush
 * and map storage.
 * 
 * @author mtotz
 */
public abstract class AbstractBuilder implements Builder
{
  /** the map */
  protected Map map;
  /** the brush */
  protected Brush brush;
  /** the last point where the builder was called */
  protected Point lastPoint;
  /** is builder started? */
  protected boolean isStarted;
  /** the layer where to start drawing (for multilayer brushes) */
  protected int startLayer;

  
  /**
   * Creates a new builder. The map/brush/startLayer must be set before using
   * the builder 
   */
  public AbstractBuilder()
  {
    this.isStarted = false;
  }

  /**
   * creates a new builder
   * @param map the map
   * @param brush the brush
   */
  public AbstractBuilder(Map map, Brush brush, int startLayer)
  {
    this();
    this.map = map;
    this.brush = brush;
    this.startLayer = startLayer;
  }
  
  /**
   * sets the map
   * @param map the map
   */
  public void setMap(Map map)
  {
    this.map = map;
    propertyChanged();
  }

  /**
   * sets the brush
   * @param brush the brush
   */
  public void setBrush(Brush brush)
  {
    this.brush = brush;
    propertyChanged();
  }
  
  /**
   * sets layer where the builder should start paining
   * @param startLayer the layer where to start painting
   */
  public void setStartLayer(int startLayer)
  {
    this.startLayer = startLayer;
    propertyChanged();
  }
  
  /**
   * Ensures that the builder is started. Calls startBuilder when is is not.
   * 
   * @param tile the tile 
   */
  protected void ensureStarted(Point tile)
  {
    if (!isStarted)
    {
      startBuilder(tile);
    }
  }

  /**
   * Ensures that the builder is stopped. calls finishBuilder when is is not.
   * @param tile the tile 
   */
  protected void ensureStopped(Point tile)
  {
    if (isStarted && lastPoint != null)
    {
      finishBuilder(tile);
    }
  }
  
  /** updates the last drawn point */
  protected void updateLastPoint(Point tile)
  {
    if (tile == null)
    {
      lastPoint = null;
    }
    else
    {
      lastPoint = new Point(tile);
    }
  }
  
  /** 
   * Called when one of the properties (map/brush/startLayer) was changed.
   * Implementing classes may override this method to update cached states or
   * stop/finish the building process. 
   */
  protected void propertyChanged()
  {
    // no defaultt implementation
  }

  /** 
   * Starts the building process. Either the user has clicked the tile (then
   * there is an imediate finishBuilder()) or he started a drag (expect some
   * moveBuilder() and a finishBuilder()).
   * 
   * The implemeting class should set the <i>isStarted</i> flag to <i>true</i>
   * and update the last drawn point (updateLastPoint())
   * 
   * @param tile the tile where to start the builder
   * @return modified region in tile coordinate space (may be null)
   */
  public abstract Rectangle startBuilder(Point tile);
  
  /** 
   * Extends the building process to the given tile.
   * 
   * The implemeting class should update the last drawn point
   * (updateLastPoint())
   * 
   * @param tile the tile where to extend the building process to
   * @return modified region in tile coordinate space (may be null)
   */
  public abstract Rectangle moveBuilder(Point tile);
  
  /** 
   * Stops and finished the building process.
   * 
   * The implemeting class should set the <i>isStarted</i> flag to <i>false</i>.
   * and set the last drawn point to null (updateLastPoint())
   * 
   * @param tile the tile where to start the builder
   * @return modified region in tile coordinate space (may be null)
   */
  public abstract Rectangle finishBuilder(Point tile);
  
  /**
   * Returns the bounds of the builder. This is mainly to show the user a 
   * modification cursor.
   * 
   * @return the bounds
   */
  public abstract Rectangle getBounds();
}
