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
package games.stendhal.server.maps.quests;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.abandonedkeep.DwarfBuyerGuyNPC;
import games.stendhal.server.maps.ados.goldsmith.MithrilForgerNPC;
import games.stendhal.server.maps.fado.house.WomanNPC;
import games.stendhal.server.maps.kalavan.castle.MadScientist1NPC;
import games.stendhal.server.maps.kalavan.castle.MadScientist2NPC;
import games.stendhal.server.maps.orril.dwarfmine.BlacksmithNPC;
import games.stendhal.server.maps.semos.caves.BabyDragonSellerNPC;
import games.stendhal.server.maps.semos.kanmararn.DwarfGuardNPC;
import games.stendhal.server.maps.semos.library.HistorianGeographerNPC;
import games.stendhal.server.maps.semos.pad.DealerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;
import utilities.RPClass.BabyDragonTestHelper;
import utilities.RPClass.ItemTestHelper;

public class JailedDwarfTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "-7_kanmararn_prison";
	private static final String NPC_NAME = "Hunel";

	private SpeakerNPC npc;
	private Engine en;

	public JailedDwarfTest() {
		setNpcNames(NPC_NAME);
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new DwarfGuardNPC(), ZONE_NAME);
	}

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	@Before
	public void setupBefore() {
		// setupQuiz
		loadQuest(this.quest = new JailedDwarf());

		npc = getNPC(NPC_NAME);
		en = npc.getEngine();		
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testGreetingStartsQuest() {

		en.step(player, "hi");		
		assertThat(getReply(npc), is("Help! The duergars have raided the prison and locked me up! I'm supposed to be the Guard! It's a shambles."));		
		assertThat(player.getQuest(this.quest.getSlotName()), is("start"));
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
	}

	@Test
	public void testGreetingWhenQuestCompleted() {

		player.setQuest(this.quest.getSlotName(), "done");
		
		en.step(player, "hi");		
		
		assertThat(getReply(npc), is("Hi. As you see, I am still too nervous to leave ..."));		
		assertThat(player.getQuest(this.quest.getSlotName()), is("done"));
		assertThat(en.getCurrentState(), is(ConversationStates.ATTENDING));
	}
	
	@Test
	public void testQuestComplition() {		
		
		player.setQuest(this.quest.getSlotName(), "start");
		
		// quest started, greeting without a key
		en.step(player, "hi");
		assertThat(getReply(npc), startsWith("Help! The duergars have raided"));
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
		
		// quest completes when player has a key to unlock Hunel
		player.equipToInventoryOnly(SingletonRepository.getEntityManager().getItem("kanmararn prison key"));

		en.step(player, "hi");
		
		assertThat(getReply(npc), is("You got the key to unlock me! *mumble*  Errrr ... it doesn't look too safe out there for me ... I think I'll just stay here ... perhaps someone could #offer me some good equipment ... "));
		assertThat(player.getQuest(this.quest.getSlotName()), is("done"));		
		assertThat(en.getCurrentState(), is(ConversationStates.ATTENDING));
		assertThat(player.getXP(), is(2000));
		assertGainKarma(20);
	}	
	
	@Test
	public void testQuestHistory() {
		
		assertThat(quest.getHistory(player), empty());
		
		player.setQuest(this.quest.getSlotName(), "start");
		assertThat(quest.getHistory(player), contains("I need to get a key to unlock Hunel."));
		
		player.setQuest(this.quest.getSlotName(), "done");		
		assertThat(quest.getHistory(player), contains("I need to get a key to unlock Hunel.","I killed the Duergar King and got the key to unlock Hunel. But now he's too afraid to leave, wanting to buy more and more armor before he feels safe. Poor Hunel."));
	}
}
