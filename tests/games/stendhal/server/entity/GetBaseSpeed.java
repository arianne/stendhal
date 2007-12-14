package games.stendhal.server.entity;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.creature.Cat;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.SpeakerNPC;
import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utilities.PlayerTestHelper;

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

		assertEquals(0.2, (new SpeakerNPC("bob")).getBaseSpeed(), 0.001);
		assertEquals(0.0, (new Creature()).getBaseSpeed(), 0.001);
		assertEquals(1.0, (PlayerTestHelper.createPlayer()).getBaseSpeed(),
				0.001);
		assertEquals(0.9, (new Cat()).getBaseSpeed(), 0.001);
		assertEquals(0.25, (new Sheep()).getBaseSpeed(), 0.001);

	}

	class MockStendhalRPWorld extends StendhalRPWorld {
		@Override
		protected void initialize() {
		}

		@Override
		public void modify(RPObject object) {
		}
	}
}
