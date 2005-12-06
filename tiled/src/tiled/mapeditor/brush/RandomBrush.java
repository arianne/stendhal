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

import java.awt.Rectangle;
import java.awt.geom.Area;

import tiled.core.MultilayerPlane;
import tiled.core.TileLayer;
import tiled.util.MersenneTwister;

public class RandomBrush extends ShapeBrush
{
    private MersenneTwister mt;
    private double ratio = 0.5;

    public RandomBrush() {
        super();
        mt = new MersenneTwister(System.currentTimeMillis());
    }

    public RandomBrush(Area shape) {
        super(shape);
        mt = new MersenneTwister(System.currentTimeMillis());
    }

    public RandomBrush(AbstractBrush sb) {
        super(sb);
        mt = new MersenneTwister(System.currentTimeMillis());
        if (sb instanceof RandomBrush) {
            ratio = ((RandomBrush)sb).ratio;
        }
    }

    public void setRatio(double r) {
        ratio = r;
    }

    public double getRatio() {
        return ratio;
    }

    /**
     * Uses the MersenneTwister to fill in a random amount of the area
     * of the brush. Uses the formula: x % 101 &lt;= 100*ratio where, 'x'
     * is a random number, to determine if a specific tile should be
     * painted or not
     *
     * @see ShapeBrush#commitPaint
     * @return a Rectangle of the bounds of the area that was modified
     * @param mp The multilayer plane that will be modified
     * @param x  The x-coordinate where the click occurred.
     * @param y  The y-coordinate where the click occurred.
     */
    public Rectangle commitPaint(MultilayerPlane mp, int x, int y,
            int initLayer)
    {
        Rectangle bounds = shape.getBounds();
        int centerx = (int)(x - (bounds.width / 2));
        int centery = (int)(y - (bounds.height / 2));

        for (int i = 0; i < numLayers; i++) {
            TileLayer tl = (TileLayer)mp.getLayer(initLayer - i);
            if (tl != null) {
                for (int cy = 0; cy <= bounds.height; cy++) {
                    for (int cx = 0; cx < bounds.width; cx++) {
                        if (shape.contains(cx, cy) && (mt.genrand() % 101) <= (100 * ratio)) {
                            tl.setTileAt(
                                    cx + centerx, cy + centery, paintTile);
                        }
                    }
                }
            }
        }

        return new Rectangle(centerx, centery, bounds.width, bounds.height);
    }
}
