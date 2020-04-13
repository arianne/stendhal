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
package games.stendhal.server.entity.item;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;

public class WeddingRingTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();
		ItemTestHelper.generateRPClasses();

		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("int_semos_guard_house", 100, 100));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerTestHelper.removeAllPlayers();
	}

	/**
	 * Tests for describe.
	 */
	@Test
	public void testDescribe() {
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		assertThat(ring.describe(), is("You see a §'wedding ring'."));
		ring.setInfoString("juliet");
		assertThat(ring.describe(), is("You see a §'wedding ring'. Its engraving says: \"In eternal love to juliet\"."));
	}

	/**
	 * Test when player attempts to use ring without equipping it to slot.
	 */
	@Test
	public void testOnUsedOnGround() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");

		assertFalse(ring.onUsed(romeo));
		assertEquals("You should pick the wedding ring up to use it.", romeo.events().get(0).get("text"));
	}

	/**
	 * Tests for onUsedNotMarried.
	 */
	@Test
	public void testOnUsedNotMarried() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		romeo.equip("finger", ring);

		assertFalse(ring.onUsed(romeo));
		assertEquals("This wedding ring hasn't been engraved yet.", romeo.events().get(0).get("text"));
	}

	/**
	 * Tests for onUsedNotOnline.
	 */
	@Test
	public void testOnUsedNotOnline() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");

		ring.setInfoString("juliet");
		romeo.equip("finger", ring);
		assertFalse(ring.onUsed(romeo));
		assertEquals("juliet is not online.", romeo.events().get(0).get("text"));
	}

	/**
	 * Tests for onUsedOnlineButNotWearingTheRing.
	 */
	@Test
	public void testOnUsedOnlineButNotWearingTheRing() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final Player juliet = PlayerTestHelper.createPlayer("juliet");
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");

		PlayerTestHelper.registerPlayer(juliet);

		ring.setInfoString("juliet");
		romeo.equip("finger", ring);
		assertFalse(ring.onUsed(romeo));
		assertEquals("juliet is not wearing the wedding ring.", romeo.events().get(0).get("text"));
	}

	/**
	 * Tests for onUsedOnlineButEngaged.
	 */
	@Test
	public void testOnUsedOnlineButEngaged() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final Player juliet = PlayerTestHelper.createPlayer("juliet");
		PlayerTestHelper.registerPlayer(juliet);

		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring.setInfoString("juliet");
		romeo.equip("finger", ring);

		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		juliet.equipToInventoryOnly(ring2);

		assertFalse(ring.onUsed(romeo));

		assertEquals("Sorry, juliet has divorced you and is now engaged to someone else.", romeo.events().get(0).get("text"));
	}

	/**
	 * Tests for onUsedOnlineButRemarried.
	 */
	@Test
	public void testOnUsedOnlineButRemarried() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final Player juliet = PlayerTestHelper.createPlayer("juliet");
		PlayerTestHelper.registerPlayer(juliet);

		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring.setInfoString("juliet");
		romeo.equip("finger", ring);

		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring2.setInfoString("paris");
		juliet.equipToInventoryOnly(ring2);

		assertFalse(ring.onUsed(romeo));

		assertEquals("Sorry, juliet has divorced you and is now remarried.", romeo.events().get(0).get("text"));
	}

	/**
	 * Tests for noTeleportOut.
	 */
	@Test
	public void testNoTeleportOut() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final Player juliet = PlayerTestHelper.createPlayer("juliet");
		PlayerTestHelper.registerPlayer(romeo, "int_semos_guard_house");
		PlayerTestHelper.registerPlayer(juliet, "int_semos_guard_house");

		final StendhalRPZone zone = (StendhalRPZone) MockStendlRPWorld.get().getRPZone("int_semos_guard_house");
		zone.disallowOut();

		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring.setInfoString("juliet");
		romeo.equip("finger", ring);

		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring2.setInfoString("romeo");
		juliet.equipToInventoryOnly(ring2);

		assertFalse(ring.onUsed(romeo));
		assertEquals(romeo.events().get(0).get("text"), "The strong anti magic aura in this area prevents the wedding ring from working!");
		// no such thing as removing teleport restrictions
		MockStendlRPWorld.get().removeZone(zone);
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("int_semos_guard_house", 100, 100));
	}

	/**
	 * Tests for noTeleportIn.
	 */
	@Test
	public void testNoTeleportIn() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final Player juliet = PlayerTestHelper.createPlayer("juliet");
		PlayerTestHelper.registerPlayer(romeo, "int_semos_guard_house");
		PlayerTestHelper.registerPlayer(juliet, "int_semos_guard_house");

		final StendhalRPZone zone = (StendhalRPZone) MockStendlRPWorld.get().getRPZone("int_semos_guard_house");
		zone.disallowIn();

		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring.setInfoString("juliet");
		romeo.equip("finger", ring);

		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring2.setInfoString("romeo");
		juliet.equipToInventoryOnly(ring2);

		assertFalse(ring.onUsed(romeo));
		assertEquals(romeo.events().get(0).get("text"), "The strong anti magic aura in the destination area prevents the wedding ring from working!");
		// no such thing as removing teleport restrictions
		MockStendlRPWorld.get().removeZone(zone);
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("int_semos_guard_house", 100, 100));
	}

	/**
	 * Tests for notVisited.
	 */
	@Test
	public void testNotVisited() {
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("moon", 10, 10));
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final Player juliet = PlayerTestHelper.createPlayer("juliet");
		PlayerTestHelper.registerPlayer(romeo, "int_semos_guard_house");
		PlayerTestHelper.registerPlayer(juliet, "moon");

		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring.setInfoString("juliet");
		romeo.equip("finger", ring);

		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring2.setInfoString("romeo");
		juliet.equipToInventoryOnly(ring2);

		assertFalse(ring.onUsed(romeo));
		assertEquals(romeo.events().get(0).get("text"), "Although you have heard a lot of rumors about the destination, you cannot join juliet there because it is still an unknown place for you.");
	}

	/**
	 * Tests for onUsedSuccesfull.
	 */
	@Test
	public void testOnUsedSuccesfull() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final Player juliet = PlayerTestHelper.createPlayer("juliet");
		PlayerTestHelper.registerPlayer(romeo, "int_semos_guard_house");
		PlayerTestHelper.registerPlayer(juliet, "int_semos_guard_house");

		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring.setInfoString("juliet");
		romeo.equip("finger", ring);

		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring2.setInfoString("romeo");
		juliet.equipToInventoryOnly(ring2);

		assertTrue(ring.onUsed(romeo));
	}

	/**
	 * Tests for coolingTime.
	 */
	@Test
	public void testCoolingTime() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final Player juliet = PlayerTestHelper.createPlayer("juliet");
		PlayerTestHelper.registerPlayer(romeo, "int_semos_guard_house");
		PlayerTestHelper.registerPlayer(juliet, "int_semos_guard_house");

		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring.setInfoString("juliet");
		romeo.equip("finger", ring);

		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring2.setInfoString("romeo");
		juliet.equipToInventoryOnly(ring2);

		assertTrue(ring.onUsed(romeo));
		assertFalse(ring.onUsed(romeo));
		assertTrue(romeo.events().get(0).get("text").startsWith("The ring has not yet regained its power."));
	}

	/**
	 * Tests for coolingTimePassed.
	 */
	@Test
	public void testCoolingTimePassed() {
		final Player romeo = PlayerTestHelper.createPlayer("romeo");
		final Player juliet = PlayerTestHelper.createPlayer("juliet");
		PlayerTestHelper.registerPlayer(romeo, "int_semos_guard_house");
		PlayerTestHelper.registerPlayer(juliet, "int_semos_guard_house");

		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring.setInfoString("juliet");
		romeo.equip("finger", ring);

		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		ring2.setInfoString("romeo");
		juliet.equipToInventoryOnly(ring2);

		ring.onUsed(romeo);
		// a time well in the past
		ring.put("amount", 0);
		assertTrue(ring.onUsed(romeo));
	}

	/**
	 * Tests for addToSlotUnmarked.
	 */
	@Test
	public void testAddToSlotUnmarked() {
		final Player frodo = PlayerTestHelper.createPlayer("frodo");
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");

		frodo.equip("bag", ring);
		frodo.equip("bag", ring2);

		assertNotNull(frodo.getAllEquipped("wedding ring"));
		assertEquals(frodo.getAllEquipped("wedding ring").size(), 2);
	}

	/**
	 * Tests for addToSlotOneMarked.
	 */
	@Test
	public void testAddToSlotOneMarked() {
		final Player frodo = PlayerTestHelper.createPlayer("frodo");
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");

		ring.setBoundTo("frodo");
		frodo.equip("bag", ring);
		frodo.equip("bag", ring2);

		assertNotNull(frodo.getAllEquipped("wedding ring"));
		assertEquals(frodo.getAllEquipped("wedding ring").size(), 2);
	}

	/**
	 * Tests for addToSlotTwoMarkedSame.
	 */
	@Test
	public void testAddToSlotTwoMarkedSame() {
		final Player frodo = PlayerTestHelper.createPlayer("frodo");
		final Player galadriel = PlayerTestHelper.createPlayer("galadriel");
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		final WeddingRing ring3 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");

		PlayerTestHelper.registerPlayer(frodo, "int_semos_guard_house");
		PlayerTestHelper.registerPlayer(galadriel, "int_semos_guard_house");

		ring.setBoundTo("frodo");
		ring2.setBoundTo("frodo");
		ring2.setInfoString("galadriel");
		frodo.equip("bag", ring);
		frodo.equip("bag", ring2);

		assertNotNull(frodo.getAllEquipped("wedding ring"));
		assertEquals("one should be destroyed", frodo.getAllEquipped("wedding ring").size(), 1);

		ring3.setInfoString("frodo");
		galadriel.equipToInventoryOnly(ring3);

		assertFalse(((WeddingRing) frodo.getFirstEquipped("wedding ring")).onUsed(frodo));
		assertTrue("Should use up the energy at destruction", frodo.events().get(0).get("text").startsWith("The ring has not yet regained its power."));
	}

	/**
	 * Tests for addToSlotTwoMarkedDifferent.
	 */
	@Test
	public void testAddToSlotTwoMarkedDifferent() {
		final Player frodo = PlayerTestHelper.createPlayer("frodo");
		final WeddingRing ring = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");
		final WeddingRing ring2 = (WeddingRing) SingletonRepository.getEntityManager().getItem("wedding ring");

		ring.setBoundTo("frodo");
		ring2.setBoundTo("gollum");
		frodo.equip("bag", ring);
		frodo.equip("bag", ring2);

		assertNotNull(frodo.getAllEquipped("wedding ring"));
		assertEquals(frodo.getAllEquipped("wedding ring").size(), 2);
	}
}
