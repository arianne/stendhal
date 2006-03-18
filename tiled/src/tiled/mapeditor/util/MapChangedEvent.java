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

package tiled.mapeditor.util;

import tiled.core.Map;

/** indicates the type of map change */
public class MapChangedEvent
{
  private Map map;
  private Type type;

  public MapChangedEvent(Map map, Type type)
  {
    this.map = map;
    this.type = type;
  }

  /** the map */
  public Map getMap()
  {
    return map;
  }
  
  /** type of change */
  public Type getType()
  {
    return type;
  }
  
  /** all change types */
  public static enum Type
  {
    /** name / filename / path changed */
    NAME,
    /** layers removed/added/set/moved up/down */
    LAYERS,
    /** brushes added/removed*/
    BRUSHES,
    /** size of the map changed (and with it, all layers) */
    SIZE,
    /** tileset set/added/removed */
    TILESETS,
    /** other properties changed (tile size etc.) */
    PROPERTIES;
  }
}  
