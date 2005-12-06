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

package tiled.mapeditor.brush;


public abstract class AbstractBrush extends Brush
{
    protected int numLayers = 1;

    public AbstractBrush() {
    }

    public AbstractBrush(AbstractBrush ab) {
        numLayers = ab.numLayers;
    }

    public void setAffectedLayers(int num) {
        numLayers = num;
    }

    public int getAffectedLayers() {
        return numLayers;
    }
}
