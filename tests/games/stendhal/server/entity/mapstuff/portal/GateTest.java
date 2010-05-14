package games.stendhal.server.entity.mapstuff.portal;

import static org.junit.Assert.*;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

import org.junit.BeforeClass;
import org.junit.Test;

public class GateTest {
	
	@BeforeClass
	public static void setupBeforeClass() {
		MockStendlRPWorld.get();
		if (!RPClass.hasRPClass("gate")) {
			Gate.generateGateRPClass();
		}
		
	}
	
	/**
	 * Tests for openCloseGate.
	 */
	@Test
	public void testOpenCloseGate() throws Exception {
		
		final Gate gate = new Gate();
		gate.open();
		assertTrue(gate.isOpen());
		gate.close();
		assertFalse(gate.isOpen());
	}
	
	/**
	 * Tests that closing fails if there's something on the way.
	 */
	@Test
	public void testCloseGateBlocked() {
		final Gate gate = new Gate();
		gate.open();
		StendhalRPZone zone = new StendhalRPZone("room", 5, 5);
		gate.setPosition(3, 3);
		zone.add(gate);
		assertTrue("Sanity check", gate.isOpen());
		final Creature creature = SingletonRepository.getEntityManager().getCreature("rat");
		creature.setPosition(3, 3);
		zone.add(creature);
		System.err.println("RESISTANCE: " + creature.getResistance());
		gate.close();
		assertTrue("Rat in the way", gate.isOpen());
		// A "ghostmode" rat
		creature.setResistance(0);
		gate.close();
		assertFalse("Ghost in the way", gate.isOpen());
	}

	/**
	 * Tests for closeOpenGate.
	 */
	@Test
	public void testCloseOpenGate() throws Exception {
		final Gate gate = new Gate();
		gate.close();
		assertFalse(gate.isOpen());
		gate.open();
		assertTrue(gate.isOpen());
	}

	/**
	 * Tests for useGateNotNExtTo.
	 */
	@Test
	public void testUseGateNotNExtTo() throws Exception {
		final Gate gate = new Gate();
		gate.setPosition(5, 5);
		assertFalse(gate.isOpen());
		final RPEntity user = new RPEntity() {

			@Override
			protected void dropItemsOn(final Corpse corpse) {

			}

			@Override
			public void logic() {

			}
		};
		assertFalse(gate.nextTo(user));
		assertFalse(gate.isOpen());
		gate.onUsed(user);
		assertFalse(gate.isOpen());
		gate.open();
		gate.onUsed(user);
		assertTrue(gate.isOpen());
	}

	/**
	 * Tests for useGateNextTo.
	 */
	@Test
	public void testUseGateNextTo() throws Exception {
		final Gate gate = new Gate();

		final Player user = new Player(new RPObject()) {

			@Override
			protected void dropItemsOn(final Corpse corpse) {

			}

			@Override
			public void logic() {

			}
		};
		assertTrue(gate.nextTo(user));

		assertFalse(gate.isOpen());
		gate.onUsed(user);
		assertTrue(gate.isOpen());

		gate.open();
		gate.onUsed(user);
		assertFalse(gate.isOpen());
	}
	
	/**
	 * Tests for isObstacle.
	 */
	@Test
	public void testIsObstacle() throws Exception {
		final Gate gate = new Gate();

		final RPEntity user = new RPEntity() {

			@Override
			protected void dropItemsOn(final Corpse corpse) {

			}

			@Override
			public void logic() {

			}
		};
		assertFalse(gate.isOpen());
		assertTrue(gate.isObstacle(user));

		gate.open();
		assertTrue(gate.isOpen());
		assertFalse(gate.isObstacle(user));

	}
}
