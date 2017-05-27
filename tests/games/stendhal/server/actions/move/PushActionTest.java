/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.move;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.constants.Actions;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import marauroa.common.game.RPAction;
import marauroa.server.game.db.DatabaseFactory;
import utilities.ZoneAndPlayerTestImpl;

/**
 * Test cases for PushAction.
 */
public class PushActionTest extends ZoneAndPlayerTestImpl {
	private static final String ZONE_NAME = "testzone";

	public PushActionTest() {
		super(ZONE_NAME);
	}

	/**
	 * Initialize the world.
	 */
	@BeforeClass
	public static void buildWorld() {
		new DatabaseFactory().initializeDatabase();
		ZoneAndPlayerTestImpl.setupZone(ZONE_NAME, false);
	}

	@AfterClass
	public static void resetWorld() {
		StendhalRPRuleProcessor rp = SingletonRepository.getRuleProcessor();
		if (rp instanceof MockStendhalRPRuleProcessor) {
			((MockStendhalRPRuleProcessor) rp).setTurn(0);
		}
	}

	/**
	 * Test pushing another player.
	 * @throws Exception
	 */
	@Test
	public void testPush() throws Exception {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(ZONE_NAME);
		final Player pushed = createPlayer("bob");
		pushed.setPosition(1, 1);
		zone.add(pushed);

		final Player pusher = createPlayer("alice");
		pusher.setPosition(0, 0);
		zone.add(pusher);

		final Item item = SingletonRepository.getEntityManager().getItem("club");
		zone.add(item);
		item.setPosition(1, 1);
		final Item item2 = SingletonRepository.getEntityManager().getItem("club");
		zone.add(item2);
		item2.setPosition(1, 1);
		item2.setBoundTo("bob");
		final Item item3 = SingletonRepository.getEntityManager().getItem("club");
		zone.add(item3);
		item3.setPosition(1, 1);
		item3.setBoundTo("alice");

		final RPAction push = new RPAction();
		push.put("type", "push");
		push.put(Actions.TARGET, "#" + pushed.getID().getObjectID());

		final PushAction action = new PushAction();
		action.onAction(pusher, push);
		// Out of breath... (turn is 0, and no time has passed)
		assertEquals(1, pushed.getX());
		assertEquals(1, pushed.getY());
		assertEquals("Give yourself a breather before you start pushing again.",
				pusher.events().get(0).get("text"));
		pusher.clearEvents();

		StendhalRPRuleProcessor rp = SingletonRepository.getRuleProcessor();
		if (!(rp instanceof MockStendhalRPRuleProcessor)) {
			throw new Exception("The test works only when using MockStendhalRPRuleProcessor");
		}
		((MockStendhalRPRuleProcessor) rp).setTurn(11);
		action.onAction(pusher, push);
		// Should work now
		assertEquals(1, pushed.getX());
		assertEquals(2, pushed.getY());
		assertEquals(pusher.events().size(), 0);

		// Finally check item locations
		assertEquals(1, item.getX());
		assertEquals(2, item.getY());

		assertEquals(1, item2.getX());
		assertEquals("item bound to pushed player; it should move", 2, item2.getY());

		assertEquals(1, item3.getX());
		assertEquals("item bound to the pushing player, it should not move", 1, item3.getY());
	}

	/**
	 * Test pushing another player when not close enough
	 */
	@Test
	public void testPushTooFar() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(ZONE_NAME);
		final Player pushed = createPlayer("bob");
		pushed.setPosition(1, 2);
		zone.add(pushed);

		final Player pusher = createPlayer("alice");
		pusher.setPosition(0, 0);
		zone.add(pusher);

		final RPAction push = new RPAction();
		push.put("type", "push");
		push.put(Actions.TARGET, "#" + pushed.getID().getObjectID());

		final PushAction action = new PushAction();
		action.onAction(pusher, push);
		// nothing interesting should happen
		assertEquals(1, pushed.getX());
		assertEquals(2, pushed.getY());
		assertEquals(0, pusher.events().size());
	}

	/**
	 * Test pushing something too big
	 */
	@Test
	public void testPushTooLarge() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(ZONE_NAME);
		final Entity giant = SingletonRepository.getEntityManager().getCreature("green dragon");
		giant.setPosition(1, 1);
		zone.add(giant);

		final Player pusher = createPlayer("alice");
		pusher.setPosition(0, 0);
		zone.add(pusher);

		final RPAction push = new RPAction();
		push.put("type", "push");
		push.put(Actions.TARGET, "#" + giant.getID().getObjectID());

		final PushAction action = new PushAction();
		action.onAction(pusher, push);

		assertEquals(1, giant.getX());
		assertEquals(1, giant.getY());
		assertEquals("You're strong, but not that strong!",
				pusher.events().get(0).get("text"));
	}

	/**
	 * Test pushing something too big
	 */
	@Test
	public void testPushCollide() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(ZONE_NAME);
		final Player pushed = createPlayer("bob");
		pushed.setPosition(1, 1);
		zone.add(pushed);
		final Entity giant = SingletonRepository.getEntityManager().getCreature("green dragon");
		giant.setPosition(1, 2);
		zone.add(giant);

		final Player pusher = createPlayer("alice");
		pusher.setPosition(0, 0);
		zone.add(pusher);

		final RPAction push = new RPAction();
		push.put("type", "push");
		push.put(Actions.TARGET, "#" + pushed.getID().getObjectID());

		final PushAction action = new PushAction();
		action.onAction(pusher, push);

		// nothing interesting should happen
		assertEquals(1, pushed.getX());
		assertEquals(1, pushed.getY());
		assertEquals(0, pusher.events().size());
	}
}
