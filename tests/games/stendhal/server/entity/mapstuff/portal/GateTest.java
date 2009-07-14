package games.stendhal.server.entity.mapstuff.portal;

import static org.junit.Assert.*;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.maps.MockStendlRPWorld;

import marauroa.common.game.RPClass;

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
	
	@Test
	public void testOpenCloseGate() throws Exception {
		
		final Gate gate = new Gate();
		gate.open();
		assertTrue(gate.isOpen());
		gate.close();
		assertFalse(gate.isOpen());
	}

	@Test
	public void testCloseOpenGate() throws Exception {
		final Gate gate = new Gate();
		gate.close();
		assertFalse(gate.isOpen());
		gate.open();
		assertTrue(gate.isOpen());
	}

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

	@Test
	public void testUseGateNextTo() throws Exception {
		final Gate gate = new Gate();

		final RPEntity user = new RPEntity() {

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
