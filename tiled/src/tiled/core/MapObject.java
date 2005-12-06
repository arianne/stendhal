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

/*
 *  (c) 2005 - Stendhal, an Arianne powered RPG 
 *  http://arianne.sf.net
 *
 * Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.core;

import java.awt.Rectangle;
import java.util.Properties;


public class MapObject
{
    private Properties properties;

    protected float map_x, map_y;
    protected Rectangle bounds;
    protected boolean bVisible = true;
    protected String source, type;

    public MapObject() {
        bounds = new Rectangle();
        properties = new Properties();
    }

    public void setX(int x) {
        map_x = x;
    }

    public void setY(int y) {
        map_y = y;
    }

    public void setType(String s) {
        type = s;
    }

    public void setSource(String s) {
        source = s;
    }

    public void translate(int x, int y) {
        map_x += x;
        map_y += y;
    }
    
    public int getX() {
        return (int)map_x;
    }

    public int getY() {
        return (int)map_y;
    }

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public Properties getProperties() {
        return properties;
    }

    public String toString() {
        return type + " (" + map_x + "," + map_y + ")";
    }
}
