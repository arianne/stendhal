package games.stendhal.server.core.engine;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import games.stendhal.server.maps.MockStendlRPWorld;

@RunWith(Parameterized.class)
public class StendhalRPZoneTest {

	private String zoneName;
	private String expectedZoneDescription;

	public StendhalRPZoneTest(String zoneName, String expectedZoneDescription) {
		this.zoneName = zoneName;
		this.expectedZoneDescription = expectedZoneDescription;
	}

	@Parameters(name = "{index}: describe({0}) = {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
                {"int_fado_tavern", "inside a building in Fado"},
                {"0_semos_city", "in Semos city"},
                {"0_semos_mountain_n_w4", "north west of Semos mountain"},
                {"-3_semos_dungeon", "deep below ground level at Semos dungeon"},
                {"-1_ados_caves", "below ground level at Ados caves"},
                {"6_kikareukin_islands", "high above the ground level at Kikareukin islands"},
                {"hell", "in Hell"},
                {"malleus_plain", "in Malleus Plain"}
		});
	}

	@Test
	public void testDescribe() throws Exception {
		StendhalRPZone zone = new StendhalRPZone(zoneName);
		MockStendlRPWorld.get().addRPZone(zone);

		assertEquals(expectedZoneDescription, StendhalRPZone.describe(zoneName));

		MockStendlRPWorld.get().removeRPZone(zone.getID());
	}
}
