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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import marauroa.common.net.InputSerializer;
import marauroa.common.net.OutputSerializer;

public class TileSetDefinitionTest {
	private StendhalMapStructure map;

	@Before
	public void setUp() {
		map = new StendhalMapStructure(64, 64);
		TileSetDefinition set = new TileSetDefinition("name1", "source1", 1);
		map.addTileset(set);

		set = new TileSetDefinition("name2", "source2", 10);
		map.addTileset(set);

		set = new TileSetDefinition("name3", "source3", 55);
		map.addTileset(set);

		set = new TileSetDefinition("name4", "source4", 100);
		map.addTileset(set);
	}

	/**
	 * Tests for serialization.
	 *
	 * @throws IOException
	 */
	@Test
	public void testSerialization() throws IOException { //, ClassNotFoundException
		final List<TileSetDefinition> tilesets = map.getTilesets();

		final ByteArrayOutputStream array = new ByteArrayOutputStream();
		final OutputSerializer out = new OutputSerializer(array);

		out.write(tilesets.size());
		for (final TileSetDefinition set : tilesets) {
			set.writeObject(out);
		}

		final byte[] serialized = array.toByteArray();

		final ByteArrayInputStream sarray = new ByteArrayInputStream(serialized);
		final InputSerializer in = new InputSerializer(sarray);

		final int amount = in.readInt();
		assertEquals(amount, tilesets.size());
		final List<TileSetDefinition> serializedTilesets = new LinkedList<TileSetDefinition>();

		for (int i = 0; i < amount; i++) {
			serializedTilesets.add((TileSetDefinition) in.readObject(new TileSetDefinition(null, null, 0)));
		}

		assertEquals(tilesets, serializedTilesets);
	}

}
