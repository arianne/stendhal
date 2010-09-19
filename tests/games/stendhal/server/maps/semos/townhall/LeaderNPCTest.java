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
package games.stendhal.server.maps.semos.townhall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

/**
 * Test for LeaderNPC - excepting his idle conversations with cadets.
 *
 * @author kymara
 */
public class LeaderNPCTest {
	
	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		ItemTestHelper.generateRPClasses();
		
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new LeaderNPC().configureZone(zone, null);

	}
	
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		npc = SingletonRepository.getNPCList().get("Lieutenant Drilenun");
		en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Oh hi, we're just taking a break here. My three cadets just got a reward from the Mayor for helping defend Semos.", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Don't forget to listen in on my teachings to these cadets, you may find it helpful!", getReply(npc));
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {

		npc = SingletonRepository.getNPCList().get("Lieutenant Drilenun");
		en = npc.getEngine();

		//test the basic messages
		assertTrue(en.step(player, "hi"));
		assertEquals("Oh hi, we're just taking a break here. My three cadets just got a reward from the Mayor for helping defend Semos.", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("I'm in charge of these three cadets. They need a lot of instruction, which I will have to go back to soon. Feel free to listen in, you may learn something!", getReply(npc));

		assertTrue(en.step(player, "quest"));
		assertEquals("Let me advise you on your #weapon.", getReply(npc));

		assertTrue(en.step(player, "help"));
		assertEquals("I can give you advice on your #weapon.", getReply(npc));

		assertTrue(en.step(player, "offer"));
		assertEquals("I'd like to comment on your #weapon, if I may.", getReply(npc));
		
		assertTrue(en.step(player, "weapon"));
		assertEquals("Oh, I can't comment on your weapon, as you have none equipped. That's not very wise in these dangerous times!", getReply(npc));
		
		final Item weapon = new Item("club", "club", "subclass", null);
		weapon.setEquipableSlots(Arrays.asList("lhand"));
		weapon.put("atk", 6);
		weapon.put("rate", 4);
		player.equipToInventoryOnly(weapon);

		assertTrue(player.isEquipped("club"));
		
		assertTrue(en.step(player, "weapon"));
		assertEquals("Well, your club has quite low damage capability, doesn't it? You should look for something with a better attack to rate ratio.", getReply(npc));
		
		player.drop(weapon);
		
		final Item weapon2 = new Item("ice sword", "sword", "subclass", null);
		weapon2.setEquipableSlots(Arrays.asList("lhand"));
		weapon2.put("atk", 29);
		weapon2.put("rate", 5);
		player.equipToInventoryOnly(weapon2);
		assertTrue(player.isEquipped("ice sword"));
		
		assertTrue(en.step(player, "weapon"));
		assertEquals("That ice sword is a powerful weapon, it has a good damage to rate ratio.", getReply(npc));
		player.drop(weapon2);
		
		final Item weapon3 = new Item("vampire sword", "sword", "subclass", null);
		weapon3.setEquipableSlots(Arrays.asList("lhand"));
		weapon3.put("atk", 22);
		weapon3.put("rate", 5);
		weapon3.put("lifesteal", 0.1);
		player.equipToInventoryOnly(weapon3);
		assertTrue(player.isEquipped("vampire sword"));
		
		assertTrue(en.step(player, "weapon"));
		assertEquals("Well, your vampire sword has quite low damage capability, doesn't it? You should look for something with a better attack to rate ratio. The positive lifesteal of 0.1 will increase your health as you use it.", getReply(npc));
		player.drop(weapon3);
		
		final Item weapon4 = new Item("club of thorns", "club", "subclass", null);
		weapon4.setEquipableSlots(Arrays.asList("lhand"));
		weapon4.put("atk", 48);
		weapon4.put("rate", 7);
		weapon4.put("lifesteal", -0.1);
		player.equipToInventoryOnly(weapon4);
		assertTrue(player.isEquipped("club of thorns"));
		
		assertTrue(en.step(player, "weapon"));
		assertEquals("That club of thorns is a powerful weapon, it has a good damage to rate ratio. It should be useful against strong creatures. Remember though that something weaker but faster may suffice against lower level creatures. The negative lifesteal of -0.1 will drain your health as you use it.", getReply(npc));
		player.drop(weapon4);
		
		final Item weapon5 = new Item("l hand sword", "sword", "subclass", null);
		weapon5.setEquipableSlots(Arrays.asList("lhand"));
		player.equipToInventoryOnly(weapon5);
		assertTrue(player.isEquipped("l hand sword"));
		
		assertTrue(en.step(player, "weapon"));
		assertEquals("I see you use twin swords. They have a superb damage capability but as you cannot wear a shield with them, you will find it harder to defend yourself if attacked.", getReply(npc));
		player.drop(weapon5);
		
		final Item weapon6 = new Item("black sword", "sword", "subclass", null);
		weapon6.setEquipableSlots(Arrays.asList("lhand"));
		weapon6.put("atk", 40);
		weapon6.put("rate", 7);
		player.equipToInventoryOnly(weapon6);
		assertTrue(player.isEquipped("black sword"));
		
		assertTrue(en.step(player, "weapon"));
		assertEquals("That black sword is a powerful weapon, it has a good damage to rate ratio. It should be useful against strong creatures. Remember though that something weaker but faster may suffice against lower level creatures.", getReply(npc));
		player.drop(weapon6);
		
		final Item weapon7 = new Item("obsidian knife", "sword", "subclass", null);
		weapon7.setEquipableSlots(Arrays.asList("lhand"));
		weapon7.put("atk", 4);
		weapon7.put("rate", 1);
		player.equipToInventoryOnly(weapon7);
		assertTrue(player.isEquipped("obsidian knife"));
		
		assertTrue(en.step(player, "weapon"));
		assertEquals("That obsidian knife is a powerful weapon, it has a good damage to rate ratio. Despite the fast rate being useful, the low attack will not help you against strong creatures. Something heavier would be better then.", getReply(npc));
		player.drop(weapon7);
		
		final Item weapon8 = new Item("assassin dagger", "sword", "subclass", null);
		weapon8.setEquipableSlots(Arrays.asList("lhand"));
		weapon8.put("atk", 6);
		weapon8.put("rate", 2);
		player.equipToInventoryOnly(weapon8);
		assertTrue(player.isEquipped("assassin dagger"));
		
		assertTrue(en.step(player, "weapon"));
		assertEquals("Well, your assassin dagger has quite low damage capability, doesn't it? You should look for something with a better attack to rate ratio. At least you can hit fast with it, so it may be good enough against creatures weaker than you.", getReply(npc));		
		player.drop(weapon8);
		
		//say goodbye
		assertTrue(en.step(player, "bye"));
		assertEquals("Don't forget to listen in on my teachings to these cadets, you may find it helpful!", getReply(npc));
	}
}
