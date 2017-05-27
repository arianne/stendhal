/**************************************************************************
 *                 (C) Copyright 2003-2013 - Stendhal team                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item.behavior;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.constants.Events;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPEvent;
import utilities.PlayerTestHelper;

/**
 * Tests for MessagingUseBehavior.
 */
public class MessagingUseBehaviorTest {
	private static final String TEXT_ATTR = "text";
	private static final String WRONG_PUBLIC = "Wrong public message";
	private static final String WRONG_PRIVATE = "Wrong private message";
	private static final String MSG_1 = "diibadaa";
	private static final String MSG_2 = "dumdidum";

	/**
	 * Setup.
	 */
	@BeforeClass
	public static void setUpBeforeClass() {
		MockStendlRPWorld.get();
	}

	/**
	 * Get text sent as private message.
	 *
	 * @param entity message recipient
	 * @return message, or <code>null</code> if the entity does not have
	 */
	private String getPrivate(RPEntity entity) {
		for (RPEvent e : entity.events()) {
			if (Events.PRIVATE_TEXT.equals(e.getName())) {
				assertEquals("Wrong message type for private message",
						"PRIVMSG", e.get("texttype"));
				return e.get(TEXT_ATTR);
			}
		}
		return null;
	}

	/**
	 * Test sending private messages.
	 */
	@Test
	public void testPrivate() {
		final Item item = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		Map<String, String> params = new HashMap<String, String>();
		params.put("private", MSG_1);
		RPEntity entity = PlayerTestHelper.createPlayer("player");
		UseBehavior b = new MessagingUseBehavior(params);
		b.use(entity, item);
		assertEquals(WRONG_PRIVATE, MSG_1, getPrivate(entity));

		entity.clearEvents();
		assertEquals(WRONG_PRIVATE, null, getPrivate(entity));

		// The same through item's use()
		item.setUseBehavior(b);
		item.onUsed(entity);
		assertEquals(WRONG_PRIVATE, MSG_1, getPrivate(entity));

		// Other RPEntities should not be send private messages
		entity = new MockNPC();
		b.use(entity, item);
		assertEquals(WRONG_PRIVATE, null, getPrivate(entity));
	}

	/**
	 * Test sending public messages.
	 */
	@Test
	public void testPublic() {
		final Item item = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		Map<String, String> params = new HashMap<String, String>();
		params.put("public", MSG_1);
		RPEntity entity = PlayerTestHelper.createPlayer("player");
		UseBehavior b = new MessagingUseBehavior(params);
		b.use(entity, item);
		assertEquals(WRONG_PUBLIC, MSG_1, entity.get(TEXT_ATTR));

		// The same through item's use()
		item.setUseBehavior(b);
		item.onUsed(entity);
		assertEquals(WRONG_PUBLIC, MSG_1, entity.get(TEXT_ATTR));

		// NPCs can trigger the public part
		MockNPC npc = new MockNPC();
		item.onUsed(npc);
		assertEquals(WRONG_PUBLIC, MSG_1, npc.message);
	}

	/**
	 * Test behavior with both public and private messages.
	 */
	@Test
	public void testCombined() {
		final Item item = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		Map<String, String> params = new HashMap<String, String>();
		params.put("private", MSG_1);
		params.put("public", MSG_2);
		RPEntity entity = PlayerTestHelper.createPlayer("player");
		UseBehavior b = new MessagingUseBehavior(params);
		b.use(entity, item);
		assertEquals(WRONG_PRIVATE, MSG_1, getPrivate(entity));
		assertEquals(WRONG_PUBLIC, MSG_2, entity.get(TEXT_ATTR));

		entity.clearEvents();
		entity.remove(TEXT_ATTR);
		assertEquals(WRONG_PRIVATE, null, getPrivate(entity));
		assertFalse(WRONG_PUBLIC, entity.has(TEXT_ATTR));

		// The same through item's use()
		item.setUseBehavior(b);
		item.onUsed(entity);
		assertEquals(WRONG_PRIVATE, MSG_1, getPrivate(entity));
		assertEquals(WRONG_PUBLIC, MSG_2, entity.get(TEXT_ATTR));

		// NPCs get only the public part
		MockNPC npc = new MockNPC();
		item.onUsed(npc);
		assertEquals(WRONG_PRIVATE, null, getPrivate(npc));
		assertEquals(WRONG_PUBLIC, MSG_2, npc.message);
	}

	/**
	 * Test using an item that's not reachable by the player.
	 */
	@Test
	public void testUseUnreachable() {
		final Item item = new Item("name1", "class", "subclass",
				new HashMap<String, String>());
		Map<String, String> params = new HashMap<String, String>();
		params.put("private", MSG_1);
		params.put("public", MSG_2);
		RPEntity entity = PlayerTestHelper.createPlayer("player");
		entity.setPosition(10, 10);
		UseBehavior b = new MessagingUseBehavior(params);
		b.use(entity, item);
		assertEquals(WRONG_PRIVATE, "That name1 is too far away.", getPrivate(entity));
		assertEquals(WRONG_PUBLIC, null, entity.get(TEXT_ATTR));
	}

	/**
	 * NPC that stores the message it says.
	 */
	private static class MockNPC extends NPC {
		private String message;

		@Override
		public void say(String msg) {
			message = msg;
		}
	}
}
