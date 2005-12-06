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
 */

package tiled.mapeditor.util;

//import java.util.EventObject;

import tiled.core.Map;


public class MapChangedEvent //extends EventObject
{
    private Map map;

    public MapChangedEvent(Map map) {
        this.map = map;
    }

    public Map getMap() {
        return map;
    }
}
