package games.stendhal.server.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.item.Corpse;
import marauroa.common.game.RPObject;
import marauroa.common.game.SlotIsFullException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ChestTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test (expected= SlotIsFullException.class)
	public final void testSize() {
		Entity.generateRPClass();
		Chest.generateRPClass();
		Chest ch = new Chest();
		assertEquals(0, ch.size());
		for (int i = 0; i < 30; i++) {

			ch.add(new PassiveEntity() {});
		}
		assertEquals(30, ch.size());
		ch.add(new PassiveEntity() {});
	}

	@Test (expected= SlotIsFullException.class)
	public final void testChestRPObject() {
		Entity.generateRPClass();
		Chest.generateRPClass();
		Chest ch = new Chest(new RPObject());
		assertEquals(0, ch.size());
		for (int i = 0; i < 4; i++) {

			ch.add(new PassiveEntity() {});
		}

	}


	@Test
	public final void testOpen() {
		Entity.generateRPClass();
		Chest.generateRPClass();
		Chest ch = new Chest();
		assertFalse(ch.isOpen());
		ch.open();

		assertTrue(ch.isOpen());
		ch.close();
		assertFalse(ch.isOpen());

	}


	@Test
	@Ignore
	public final void testGetContent() {

	}

	@Test
	public final void testOnUsed() {
		Entity.generateRPClass();
		Chest.generateRPClass();
		Chest ch = new Chest();
		assertFalse(ch.isOpen());
		ch.onUsed(new RPEntity(){

			@Override
			protected void dropItemsOn(Corpse corpse) {
			}

			@Override
			public void logic() {

			}});

		assertTrue(ch.isOpen());
		ch.onUsed(new RPEntity(){

			@Override
			protected void dropItemsOn(Corpse corpse) {
			}

			@Override
			public void logic() {

			}});
		assertFalse(ch.isOpen());
	}

}
