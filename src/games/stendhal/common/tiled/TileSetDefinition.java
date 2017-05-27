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

import marauroa.common.net.InputSerializer;
import marauroa.common.net.OutputSerializer;
import marauroa.common.net.Serializable;

/**
 * Stores a definition of a tileset. Mainly its name, the source image used and
 * the starting global id.
 *
 * @author miguel
 *
 */
public class TileSetDefinition implements Serializable {
	/** The name of the tileset. Useless */
	private String name;

	/** The source image of this tileset. */
	private String source = null;

	/** The id where this tileset begins to number tiles. */
	private int gid;

	/**
	 * Constructor.
	 *
	 * @param name
	 *            the *useless* name of the tileset.
	 * @param source
	 *            the image filename
	 * @param firstGid
	 *            the id where this tileset begins to number tiles.
	 */
	public TileSetDefinition(final String name, String source, final int firstGid) {
		this.name = name;
		this.source = source;
		this.gid = firstGid;
	}

	/**
	 * Returns the id where this tileset begins to number tiles.
	 *
	 * @return the id where this tileset begins to number tiles
	 */
	public int getFirstGid() {
		return gid;
	}

	/**
	 * Set the filename of the source image of the tileset.
	 *
	 * @param attributeValue
	 *            the filename
	 */
	public void setSource(final String attributeValue) {
		this.source = attributeValue;
	}

	/**
	 * Returns the filename of the source image of the tileset.
	 *
	 * @return the filename of the source image of the tileset.
	 */
	public String getSource() {
		return source;
	}

	public byte[] encode() throws IOException {
		final ByteArrayOutputStream array = new ByteArrayOutputStream();
		final OutputSerializer out = new OutputSerializer(array);

		writeObject(out);

		return array.toByteArray();
	}

	@Override
	public void readObject(final InputSerializer in) throws IOException {
		name = in.readString();
		source = in.readString();
		gid = in.readInt();
	}

	@Override
	public void writeObject(final OutputSerializer out) throws IOException {
		out.write(name);
		out.write(source);
		out.write(gid);
	}

	@Override
	public boolean equals(final Object object) {
		if (object instanceof TileSetDefinition) {

			final TileSetDefinition set = (TileSetDefinition) object;
			return set.name.equals(name) && set.source.equals(source)
					&& (set.gid == gid);
		} else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		int hash = 1;
		if (name != null) {
			hash = hash * name.hashCode();
		}
		if (source != null) {
			hash = hash * source.hashCode();
		}
		hash = hash + gid;
		return hash * super.hashCode();
	}
}
