/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.query;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.constants.Actions;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Cat;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;
import utilities.PlayerTestHelper;
import utilities.RPClass.CatTestHelper;
import utilities.RPClass.PetTestHelper;
import utilities.RPClass.SheepTestHelper;

public class WhereActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@After
	public void tearDown() throws Exception {

		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	/**
	 * Tests for onActionNoTarget.
	 */
	@Test
	public void testOnActionNoTarget() {
		final WhereAction pq = new WhereAction();
		final RPAction action = new RPAction();
		action.put(Actions.TYPE, "where");
		final Player player = PlayerTestHelper.createPlayer("player");
		MockStendhalRPRuleProcessor.get().addPlayer(player);

		pq.onAction(player, action);
		assertTrue(player.events().isEmpty());
	}

	/**
	 * Tests for onActionNotThere.
	 */
	@Test
	public void testOnActionNotThere() {
		final WhereAction pq = new WhereAction();
		final RPAction action = new RPAction();
		action.put(Actions.TYPE, "where");
		action.put(Actions.TARGET, "NotThere");

		final Player player = PlayerTestHelper.createPlayer("player");
		MockStendhalRPRuleProcessor.get().addPlayer(player);

		pq.onAction(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("No player or pet named \"NotThere\" is currently logged in."));
	}

	/**
	 * Tests for onAction.
	 */
	@Test
	public void testOnAction() {
		final WhereAction pq = new WhereAction();
		final RPAction action = new RPAction();
		action.put(Actions.TYPE, "where");
		action.put(Actions.TARGET, "bob");

		final Player player = PlayerTestHelper.createPlayer("bob");
		final StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(player);
		MockStendhalRPRuleProcessor.get().addPlayer(player);
		pq.onAction(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("You are in zone at (0,0)"));
		player.clearEvents();

		// test that you can still /where yourself as a ghost
		player.setGhost(true);
		pq.onAction(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("You are in zone at (0,0)"));
		player.clearEvents();

		// test the player before he becomes ghostmode
		final Player ghosted = PlayerTestHelper.createPlayer("ghosted");
		zone.add(ghosted);
		MockStendhalRPRuleProcessor.get().addPlayer(ghosted);
		action.put(Actions.TARGET, ghosted.getName());
		pq.onAction(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("ghosted is in zone at (0,0)"));
		player.clearEvents();

		// test the player after he becomes ghostmode
		ghosted.setGhost(true);
		pq.onAction(player, action);

		assertThat(player.events().get(0).get("text"), equalTo("No player or pet named \"ghosted\" is currently logged in."));

	}

	/**
	 * Tests for onActionSheep.
	 */
	@Test
	public void testOnActionSheep() {
		final WhereAction pq = new WhereAction();
		final RPAction action = new RPAction();
		action.put(Actions.TYPE, "where");
		action.put(Actions.TARGET, "sheep");

		final Player player = PlayerTestHelper.createPlayer("player");
		MockStendhalRPRuleProcessor.get().addPlayer(player);

		pq.onAction(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("No player or pet named \"sheep\" is currently logged in."));
	}

	/**
	 * Tests for onActionPetSheep.
	 */
	@Test
	public void testOnActionPetSheep() {
		SheepTestHelper.generateRPClasses();
		PetTestHelper.generateRPClasses();
		CatTestHelper.generateRPClasses();
		final WhereAction pq = new WhereAction();
		final RPAction action = new RPAction();
		action.put(Actions.TYPE, "where");
		action.put(Actions.TARGET, "pet");

		Player player = PlayerTestHelper.createPlayer("player");

		pq.onAction(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("No player or pet named \"pet\" is currently logged in."));


		final Pet testPet = new Cat();

		final Sheep testSheep = new Sheep();

		player = PlayerTestHelper.createPlayer("player");

		StendhalRPZone stendhalRPZone = new StendhalRPZone("zone");
		MockStendlRPWorld.get().addRPZone(stendhalRPZone);
		stendhalRPZone.add(player);

		stendhalRPZone.add(testSheep);
		stendhalRPZone.add(testPet);
		player.setPet(testPet);

		pq.onAction(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("Your cat is at (0,0)"));
		player.clearEvents();

		player.setSheep(testSheep);

		action.put(Actions.TARGET, "sheep");

		pq.onAction(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("Your sheep is at (0,0)"));
	}

}
