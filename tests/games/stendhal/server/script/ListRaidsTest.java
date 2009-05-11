package games.stendhal.server.script;


import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class ListRaidsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}


	@Test
	public void testname() throws Exception {
		ListRaids script = new ListRaids();
		Player player = PlayerTestHelper.createPlayer("george");
		script.execute(player, null);
		assertThat(player.events().get(0).toString(), containsString("CreateRaid"));
		
	}

}
