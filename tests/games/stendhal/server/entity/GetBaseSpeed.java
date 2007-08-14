package games.stendhal.server.entity;


import static org.junit.Assert.assertEquals;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.creature.Cat;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.RPWorld;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GetBaseSpeed {

	@Before
	public void setUp() throws Exception {
  new MockStendhalRPWorld();

	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testgetBaseSpeed() throws Exception {




		assertEquals(0.2,(new SpeakerNPC("bob")).getBaseSpeed());
		assertEquals(0.0, (new Creature()).getBaseSpeed());
		assertEquals(1.0,(new Player(new RPObject())).getBaseSpeed());
		assertEquals(0.9,(new Cat()).getBaseSpeed());
		assertEquals(0.25,(new Sheep()).getBaseSpeed());

	}

class MockStendhalRPWorld extends StendhalRPWorld{
    @Override
    protected void initialize() {
    }
    @Override
	public void modify(RPObject object) {
	}
}
}
