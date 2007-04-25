package games.stendhal.server.tools;

import static org.junit.Assert.*;
import games.stendhal.tools.tiled.LayerDefinition;
import games.stendhal.tools.tiled.StendhalMapStructure;
import games.stendhal.tools.tiled.TileSetDefinition;

import org.junit.Before;
import org.junit.Test;


public class LayerDefinitionTest {
	StendhalMapStructure map;
	
	@Before
	public void setUp() {
		map=new StendhalMapStructure(64, 64);
		TileSetDefinition set=null;
		
		set=new TileSetDefinition("name1", 1);
		set.setSource("source1");
		map.addTileset(set);
		
		set=new TileSetDefinition("name2", 10);
		set.setSource("source2");
		map.addTileset(set);

		set=new TileSetDefinition("name3", 55);
		set.setSource("source3");
		map.addTileset(set);

		set=new TileSetDefinition("name4", 100);
		set.setSource("source4");
		map.addTileset(set);
		
		LayerDefinition layer=null;
		layer=new LayerDefinition(64, 64);
		layer.build();
		
		layer.setName("layer1");
		layer.set(10, 20, 1);
		layer.set(19, 7, 10);
		layer.set(11, 2, 120);
		layer.set(15, 21, 64);
		map.addLayer(layer);
	}
	
	@Test 
	public void testBelongToTileset() {
		LayerDefinition layer=map.getLayer("layer1");
		assertNotNull(layer);
		int tileid=layer.getTileAt(10, 20);
		assertEquals(1, tileid);
		assertEquals("source1",layer.getTilesetFor(tileid)); 
	}
	
	
}
