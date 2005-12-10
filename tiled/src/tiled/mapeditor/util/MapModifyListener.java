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
package tiled.mapeditor.util;

import java.awt.Rectangle;

import tiled.core.Map;

/**
 * This listener will be notified when the map content changes.
 * This will not be notified when another map is loaded
 *
 * @author Matthias Totz <mtotz@users.sourceforge.net>
 */
public interface MapModifyListener
{
  public void mapModified(Map map, Rectangle modifiedRegion);
}
