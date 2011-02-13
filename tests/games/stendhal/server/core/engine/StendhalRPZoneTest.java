package games.stendhal.server.core.engine;

import static org.junit.Assert.*;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StendhalRPZoneTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		final StendhalRPZone fado_tavern = new StendhalRPZone("int_fado_tavern");
		MockStendlRPWorld.get().addRPZone(fado_tavern);
		
		final StendhalRPZone semos_city = new StendhalRPZone("0_semos_city");
		MockStendlRPWorld.get().addRPZone(semos_city);
		
		final StendhalRPZone semos_mountain = new StendhalRPZone("0_semos_mountain_n_w4");
		MockStendlRPWorld.get().addRPZone(semos_mountain);
		
		final StendhalRPZone semos_dungeon = new StendhalRPZone("-3_semos_dungeon");
		MockStendlRPWorld.get().addRPZone(semos_dungeon);
		
		final StendhalRPZone ados_cave = new StendhalRPZone("-1_ados_caves");
		MockStendlRPWorld.get().addRPZone(ados_cave);
		
		final StendhalRPZone kikareukin_islands = new StendhalRPZone("6_kikareukin_islands");
		MockStendlRPWorld.get().addRPZone(kikareukin_islands);
		
		final StendhalRPZone hell = new StendhalRPZone("hell");
		MockStendlRPWorld.get().addRPZone(hell);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDescribeString() {
		assertEquals(StendhalRPZone.describe("int_fado_tavern"),"inside a building in Fado");
		assertEquals(StendhalRPZone.describe("0_semos_city"),"in Semos city");
		assertEquals(StendhalRPZone.describe("0_semos_mountain_n_w4"),"north west of Semos mountain");
		assertEquals(StendhalRPZone.describe("-3_semos_dungeon"),"deep below ground level at Semos dungeon");
		assertEquals(StendhalRPZone.describe("-1_ados_caves"),"below ground level at Ados caves");
		assertEquals(StendhalRPZone.describe("6_kikareukin_islands"),"high above the ground level at Kikareukin islands");
		assertEquals(StendhalRPZone.describe("hell"),"in Hell");
	}

}
