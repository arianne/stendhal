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

package tiled.core;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.Properties;


/**
 * A layer of a map.
 *
 * @see tiled.core.Map
 * @see tiled.core.MultilayerPlane
 */
public abstract class MapLayer implements Cloneable
{
    /** MIRROR_HORIZONTAL */
    public static final int MIRROR_HORIZONTAL = 1;
    /** MIRROR_VERTICAL */
    public static final int MIRROR_VERTICAL   = 2;

    /** ROTATE_90 */
    public static final int ROTATE_90  = 90;
    /** ROTATE_180 */
    public static final int ROTATE_180 = 180;
    /** ROTATE_270 */
    public static final int ROTATE_270 = 270;

    protected String name;
    protected boolean isVisible = true;
    protected boolean bLocked = false;
    protected Map myMap;
    protected float opacity = 1.0f;
    protected Rectangle bounds;
    private Properties properties;

    public MapLayer() {
        bounds = new Rectangle();
        setMap(null);
        properties = new Properties();
    }

    /**
     * @param w width in tiles
     * @param h height in tiles
     */
    public MapLayer(int w, int h) {
        this(new Rectangle(0, 0, w, h));
    }

    public MapLayer(Rectangle r) {
        this();
        setBounds(r);
    }

    public MapLayer(MapLayer ml) {
        this(ml.getBounds());
        name = ml.getName();

        // TODO: Copy properties
    }

    /**
     * @param m the map this layer is part of
     */
    MapLayer(Map m) {
        this();
        setMap(m);
    }

    /**
     * @param m the map this layer is part of
     * @param w width in tiles
     * @param h height in tiles
     */
    public MapLayer(Map m, int w, int h) {
        this(w, h);
        setMap(m);
    }

    /**
     * Performs a linear translation of this layer by (<i>dx, dy</i>).
     *
     * @param dx distance over x axis
     * @param dy distance over y axis
     */
    public void translate(int dx, int dy) {
        bounds.x += dx;
        bounds.y += dy;
    }

    public abstract void rotate(int angle);

    public abstract void mirror(int dir);

    /**
     * Sets the bounds (in tiles) to the specified Rectangle.
     *
     * @param bounds
     */
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    /**
     * Sets the name of this layer.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the map this layer is part of.
     *
     * @param m the Map object
     */
    public void setMap(Map m) {
        myMap = m;
    }

    /**
     * Sets layer opacity. If it is different from the previous value and the
     * layer is visible, a MapChangedEvent is fired.
     *
     * @param opacity the new opacity for this layer
     */
    public void setOpacity(float opacity) {
        if (this.opacity != opacity) {
            this.opacity = opacity;

            if (isVisible() && myMap != null) {
                myMap.fireMapChanged();
            }
        }
    }

    /**
     * Sets the visibility of this map layer. If it changes from its current
     * value, a MapChangedEvent is fired.
     *
     * @param visible <code>true</code> to make the layer visible;
     *                <code>false</code> to make it invisible
     */
    public void setVisible(boolean visible) {
        if (isVisible != visible) {
            isVisible = visible;
            if (myMap != null) {
                myMap.fireMapChanged();
            }
        }
    }

    /**
     * Sets the offset of this map layer. The offset is a distance by which to
     * shift this layer from the origin of the map.
     *
     * @param xOff x offset in tiles
     * @parem yOff y offset in tiles
     */
    public void setOffset(int xOff, int yOff) {
        bounds.x = xOff;
        bounds.y = yOff;
    }

    /**
     * Returns the name of this layer.
     *
     * @return a java.lang.String of the name of the layer
     */
    public String getName() {
        return name;
    }

    /**
     * Returns layer width in tiles.
     */
    public int getWidth() {
        return bounds.width;
    }

    /**
     * Returns layer height in tiles.
     */
    public int getHeight() {
        return bounds.height;
    }

    /**
     * Returns layer bounds in tiles.
     *
     * @return a java.awt.Rectangle object with the bounds in tiles of the
     *         layer
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * A convenience method to check if a point in tile-space is within
     * the layer boundaries.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return <code>true</code> if the point (x,y) is within the layer
     *         boundaries, <code>false</code> otherwise.
     */
    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

    /**
     * Returns layer opacity.
     *
     * @return layer opacity, ranging from 0.0 to 1.0
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * Returns whether this layer is visible.
     *
     * @return <code>true</code> if the layer is visible, <code>false</code>
     * otherwise.
     */
    public boolean isVisible() {
        return isVisible;
    }

    public abstract void mergeOnto(MapLayer other);

    public abstract void copyFrom(MapLayer other);

    public abstract void maskedCopyFrom(MapLayer other, Area mask);

    public abstract MapLayer createDiff(MapLayer ml);

    /**
     * Unlike mergeOnto, copyTo includes the null tile when merging
     *
     * @see tiled.core.MapLayer#copyFrom
     * @see tiled.core.MapLayer#mergeOnto
     * @param other the layer to copy this layer to
     */
    public abstract void copyTo(MapLayer other);

    public abstract boolean isUsed(Tile t);

    /**
     * Creates a copy of this layer.
     *
     * @see java.lang.Object#clone
     * @return a clone of this layer, as complete as possible
     * @exception CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        MapLayer clone = null;
        clone = (MapLayer)super.clone();

        // Create a new bounds object
        clone.bounds = new Rectangle(bounds);

        return clone;
    }

    /**
     * @see MultilayerPlane#resize
     *
     * @param width  the new width of the layer
     * @param height the new height of the layer
     * @param dx     the shift in x direction
     * @param dy     the shift in y direction
     */
    public abstract void resize(int width, int height, int dx, int dy);

    /**
     * Get the locked status of the layer.
     *
     * @see MapLayer#setLocked(boolean)
     */
    public boolean getLocked() {
        return bLocked;
    }

    /**
     * Set the locked status of the layer. A locked layer can't be edited.
     *
     * @param lock <code>true</code> to lock the layer, <code>false</code> to
     *             unlock the layer
     */
    public void setLocked(boolean lock) {
        bLocked = lock;
    }

    public void setProperties(Properties p) {
        properties = p;
    }

    public Properties getProperties() {
        return properties;
    }

    public boolean canEdit() {
        return !getLocked() && isVisible();
    }
}
