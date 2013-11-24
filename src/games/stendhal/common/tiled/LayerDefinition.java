/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.tiled;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import marauroa.common.net.InputSerializer;
import marauroa.common.net.OutputSerializer;
import marauroa.common.net.Serializable;

/**
 * The class that stores the definition of a layer. A Layer consists mainly of:
 * <ul>
 * <li>width and height
 * <li>name <b>VERY IMPORTANT</b>
 * <li>data
 * </ul>
 *
 * @author miguel
 *
 */
public class LayerDefinition implements Serializable {
	/** To which map this layer belong. */
	private StendhalMapStructure map = null;

	/** Width of the layer that SHOULD be the same that the width of the map. */
	private int width;

	/** Height of the layer that SHOULD be the same that the height of the map. */
	private int height;

	/**
	 * Name of the layer that MUST be one of the available:
	 * <ul>
	 * <li>0_floor
	 * <li>1_terrain
	 * <li>2_object
	 * <li>3_roof
	 * <li>4_roof_add
	 * <li>objects
	 * <li>collision
	 * <li>protection
	 * </ul>.
	 */
	private String name;

	/** The data encoded as int in a array of size width*height .*/
	private int[] data = null;

	/**
	 * The same data in a raw byte array, so we save reencoding it again for
	 * serialization.
	 */
	private byte[] raw;

	/**
	 * Constructor.
	 *
	 * @param layerWidth
	 *            the width of the layer.
	 * @param layerHeight
	 *            the height of the layer
	 */
	public LayerDefinition(final int layerWidth, final int layerHeight) {
		raw = new byte[4 * layerWidth * layerHeight];
		data = new int[layerWidth * layerHeight];
		width = layerWidth;
		height = layerHeight;
	}

	/**
	 * Sets the map to which this layer belong to.
	 *
	 * @param map
	 *            the map
	 */
	public void setMap(final StendhalMapStructure map) {
		this.map = map;
	}

	/**
	 * Builds the real data array based on the byte array. It is only needed for
	 * objects, collision and protection, which is at most 40% of the layers.
	 */
	public void build() {
		data = new int[height * width];
		int offset = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int tileId = 0;
				tileId |= (raw[0 + offset] & 0xFF);
				tileId |= (raw[1 + offset] & 0xFF) << 8;
				tileId |= (raw[2 + offset] & 0xFF) << 16;
				tileId |= (raw[3 + offset] & 0xFF) << 24;

				data[x + y * width] = tileId;
				offset += 4;
			}
		}
	}

	/**
	 * @return the allocated raw array so it can be modified.
	 */
	public byte[] exposeRaw() {
		return raw;
	}

	/**
	 * @return the allocated data array of size width*height containing ints.
	 */
	public int[] expose() {
		return data;
	}

	/**
	 * Set a tile at the given x,y position.
	 *
	 * @param x
	 *            the x position
	 * @param y
	 *            the y position
	 * @param tileId
	 *            the tile code to set ( Use 0 for none ).
	 */
	public void set(final int x, final int y, final int tileId) {
		final int offset = 4 * (x + y * width);

		raw[0 + offset] = (byte) (tileId & 0xFF);
		raw[1 + offset] = (byte) ((tileId >>> 8) & 0xFF);
		raw[2 + offset] = (byte) ((tileId >>> 16) & 0xFF);
		raw[3 + offset] = (byte) ((tileId >>> 24) & 0xFF);

		data[y * width + x] = tileId;
	}

	/**
	 * Returns the tile at the x,y position.
	 *
	 * @param x
	 *            the x position
	 * @param y
	 *            the y position
	 * @return the tile that exists at that position or 0 for none.
	 */
	public int getTileAt(final int x, final int y) {
		return data[y * width + x];
	}

	/**
	 *
	 * @return the encode byte array
	 * @throws IOException
	 */
	public byte[] encode() throws IOException {
		final ByteArrayOutputStream array = new ByteArrayOutputStream();
		final DeflaterOutputStream out_stream = new DeflaterOutputStream(array);
		final OutputSerializer out = new OutputSerializer(out_stream);

		writeObject(out);
		out_stream.close();

		return array.toByteArray();
	}

	/**
	 * Deserializes a layer definition.
	 *
	 * @param in
	 *            input serializer
	 * @return an instance of a layer definition
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static LayerDefinition decode(final InputStream in) throws IOException,
			ClassNotFoundException {
		LayerDefinition layer = new LayerDefinition(0, 0);

		final InflaterInputStream szlib = new InflaterInputStream(in, new Inflater());
		final InputSerializer ser = new InputSerializer(szlib);

		layer = (LayerDefinition) ser.readObject(layer);
		layer.build();
		return layer;
	}

	/**
	 * Returns the width of the layer.
	 *
	 * @return the layer's width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the layer.
	 *
	 * @return the layer's height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the name of the tileset a tile belongs to.
	 *
	 * @param value
	 *            the tile id
	 * @return the name of the tileset
	 */
	public TileSetDefinition getTilesetFor(final int value) {
		if (value == 0) {
			return null;
		}

		final List<TileSetDefinition> tilesets = map.getTilesets();

		int pos = 0;
		for (pos = 0; pos < tilesets.size(); pos++) {
			if (value < tilesets.get(pos).getFirstGid()) {
				break;
			}
		}

		return tilesets.get(pos - 1);
	}

	/**
	 * Sets the name of the layer.
	 *
	 * @param layerName
	 *            the name of the layer
	 */
	public void setName(final String layerName) {
		name = layerName;
	}

	/**
	 * Returns the name of the layer.
	 *
	 * @return the layer's name
	 */
	public String getName() {
		return name;
	}

	@Override
	public void readObject(final InputSerializer in) throws IOException {
		name = in.readString();
		width = in.readInt();
		height = in.readInt();
		raw = in.readByteArray();
	}

	@Override
	public void writeObject(final OutputSerializer out) throws IOException {
		out.write(name);
		out.write(width);
		out.write(height);
		out.write(raw);
	}
}
