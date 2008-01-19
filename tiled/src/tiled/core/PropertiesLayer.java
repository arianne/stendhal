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
 *  Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */

package tiled.core;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.Properties;

/**
 * The PropertiesLayer contains Property-Objects for each position. You need
 * only one per map.
 * 
 * @author mtotz
 * 
 */
public class PropertiesLayer extends MapLayer {
	private Properties[][] properties;

	/**
	 * @param width
	 * @param height
	 */
	public PropertiesLayer(int width, int height) {
		this(0, 0, width, height);
	}

	/**
	 * @param width
	 * @param height
	 */
	public PropertiesLayer(int x, int y, int width, int height) {
		super(new Rectangle(x, y, width, height));
		properties = new Properties[width][height];
		setName("properties");
	}

	/**
	 * Returns the properties at x,y. Returns null when x,y is not inside the
	 * layer. <b>Note:</b> This returns the actual <code>Properties</code>
	 * object, so any changes to the returned object are reflected immediatly in
	 * the map. Also note that the object itself is synchonized (it is a
	 * <code>Hashtable</code>), but concurrent access may result in a
	 * <code>CuncurrentModificationException</code>.
	 */
	public Properties getProps(int x, int y) {
		if (!contains(x, y)) {
			return null;
		}

		Properties props = properties[x][y];
		if (props == null) {
			props = new Properties();
			properties[x][y] = props;
		}
		return props;
	}

	/** returns the properties at x,y. Never returns null */
	public void setProps(int x, int y, Properties properties) {
		if (!contains(x, y)) {
			return;
		}
		Properties props = new Properties();

		if (properties != null) {
			// copy properties list
			props.putAll(properties);
		}
		this.properties[x][y] = props;
	}

	/**
	 * creates a diff of the layers. The return layers contains all differences
	 * of <code>ml</code>
	 */
	public MapLayer createDiff(MapLayer ml) {
		if (!(ml instanceof PropertiesLayer)) {
			return null;
		}

		PropertiesLayer other = (PropertiesLayer) ml;

		Rectangle r = null;

		for (int y = bounds.y; y < bounds.height + bounds.y; y++) {
			for (int x = bounds.x; x < bounds.width + bounds.x; x++) {
				if (other.getProps(x, y).equals(getProps(x, y))) {
					if (r != null) {
						r.add(x, y);
					} else {
						r = new Rectangle(x, y);
					}
				}
			}
		}

		if (r != null) {
			MapLayer diff = new PropertiesLayer(r.x, r.y, r.width + 1, r.height + 1);
			diff.copyFrom(ml);
			return diff;
		} else {
			return new PropertiesLayer(0, 0);
		}
	}

	/**
	 * Copy data from another layer onto this layer. All previous Properties are
	 * overwritten.
	 * 
	 * @see tiled.core.MapLayer#copyFrom
	 * @param other
	 */
	public void copyFrom(MapLayer other) {
		if (!canEdit() || !(other instanceof PropertiesLayer)) {
			return;
		}

		PropertiesLayer otherLayer = (PropertiesLayer) other;

		for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
			for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
				setProps(x, y, otherLayer.getProps(x, y));
			}
		}
	}

	/**
	 * Merges data from another layer onto this layer. All previous Properties
	 * are appended.
	 * 
	 * Note: duplicate Properties will be overwritten.
	 * 
	 * @see tiled.core.MapLayer#mergeOnto
	 * @param other
	 */
	public void mergeOnto(MapLayer other) {
		if (!canEdit() || !(other instanceof PropertiesLayer)) {
			return;
		}

		PropertiesLayer otherLayer = (PropertiesLayer) other;

		for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
			for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
				getProps(x, y).putAll(otherLayer.getProps(x, y));
			}
		}
	}

	/**
	 * Unlike mergeOnto, copyTo includes the null tile when merging.
	 * 
	 * @see tiled.core.MapLayer#copyFrom
	 * @see tiled.core.MapLayer#mergeOnto
	 * @param other
	 *            the layer to copy this layer to
	 */
	public void copyTo(MapLayer other) {
		if (!canEdit() || !(other instanceof PropertiesLayer)) {
			return;
		}

		PropertiesLayer otherLayer = (PropertiesLayer) other;

		for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
			for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
				setProps(x, y, otherLayer.getProps(x, y));
			}
		}
	}

	/**
	 * Creates a copy of this layer.
	 * 
	 * @see java.lang.Object#clone
	 * @return a clone of this layer, as complete as possible
	 * @exception CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException {
		PropertiesLayer clone = (PropertiesLayer) super.clone();

		// Clone the layer data
		clone.properties = new Properties[getWidth()][getHeight()];
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				Properties newProps = null;
				Properties props = properties[x][y];
				if (props != null && props.size() > 0) {
					newProps = new Properties();
					newProps.putAll(props);
				}
				clone.properties[x][y] = newProps;
			}
		}
		return clone;
	}

	// TODO: not yet implemented
	public void rotate(int angle) {
	}

	public void mirror(int dir) {
	}

	public void resize(int width, int height, int dx, int dy) {
	}

	public void maskedCopyFrom(MapLayer other, Area mask) {
	}

	/** returns a copy of the layer. */
	public MapLayer getLayerCopy(Rectangle bounds) {
		return null;
	}
}
