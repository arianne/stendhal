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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import marauroa.common.net.InputSerializer;
import marauroa.common.net.OutputSerializer;

public class LayerDefinitionTest {
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

		final LayerDefinition layer = new LayerDefinition(64, 64);
		layer.build();

		layer.setName("layer1");
		layer.set(10, 20, 1);
		layer.set(19, 7, 10);
		layer.set(11, 2, 120);
		layer.set(15, 21, 64);
		map.addLayer(layer);
	}

	/**
	 * Tests for belongToTileset.
	 */
	@Test
	public void testBelongToTileset() {
		final LayerDefinition layer = map.getLayer("layer1");
		assertNotNull(layer);

		final int tileid = layer.getTileAt(10, 20);
		assertEquals(1, tileid);

		assertEquals("source1",
				layer.getTilesetFor(layer.getTileAt(10, 20)).getSource());
		assertEquals("source2",
				layer.getTilesetFor(layer.getTileAt(19, 7)).getSource());
		assertEquals("source4",
				layer.getTilesetFor(layer.getTileAt(11, 2)).getSource());
		assertEquals("source3",
				layer.getTilesetFor(layer.getTileAt(15, 21)).getSource());

		assertEquals(0, layer.getTileAt(57, 34));
		assertNull(layer.getTilesetFor(layer.getTileAt(57, 34)));
	}

	/**
	 * Tests for serialization.
	 *
	 * @throws IOException
	 */
	@Test
	public void testSerialization() throws IOException { //, ClassNotFoundException
		final ByteArrayOutputStream array = new ByteArrayOutputStream();
		final OutputSerializer out = new OutputSerializer(array);

		final LayerDefinition layer = map.getLayer("layer1");
		layer.writeObject(out);

		final byte[] serialized = array.toByteArray();

		final ByteArrayInputStream sarray = new ByteArrayInputStream(serialized);
		final InputSerializer in = new InputSerializer(sarray);

		final LayerDefinition serializedLayer = (LayerDefinition) in.readObject(new LayerDefinition(
				0, 0));

		assertEquals(layer.getName(), serializedLayer.getName());
		assertEquals(layer.getWidth(), serializedLayer.getWidth());
		assertEquals(layer.getHeight(), serializedLayer.getHeight());
		assertEquals(layer.exposeRaw().length,
				serializedLayer.exposeRaw().length);

		final byte[] rawData = layer.exposeRaw();
		final byte[] serializedData = serializedLayer.exposeRaw();
		assertArrayEquals(rawData, serializedData);
	}

}
