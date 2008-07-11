package games.stendhal.server.entity.mapstuff.portal;

import static org.junit.Assert.*;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Corpse;

import org.junit.Test;

public class GateTest {
	@Test
	public void testOpenCloseGate() throws Exception {
		Gate gate = new Gate();
		gate.open();
		assertTrue(gate.isOpen());
		gate.close();
		assertFalse(gate.isOpen());
	}

	@Test
	public void testCloseOpenGate() throws Exception {
		Gate gate = new Gate();
		gate.close();
		assertFalse(gate.isOpen());
		gate.open();
		assertTrue(gate.isOpen());
	}

	@Test
	public void testUseGateNotNExtTo() throws Exception {
		Gate gate = new Gate();
		gate.setPosition(5, 5);
		assertFalse(gate.isOpen());
		RPEntity user = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {

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
		Gate gate = new Gate();

		RPEntity user = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {

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
		Gate gate = new Gate();

		RPEntity user = new RPEntity() {

			@Override
			protected void dropItemsOn(Corpse corpse) {

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
