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

import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.contains;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.ados.fishermans_hut.TeacherNPC;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class FishermansLicenseQuizTest extends ZonePlayerAndNPCTestImpl {
	
	private static final String ZONE_NAME = "int_ados_fishermans_hut_west";
	private static final String NPC_NAME = "Santiago";	

	private SpeakerNPC npc;
	private Engine en;		

	public FishermansLicenseQuizTest() {
		setNpcNames(NPC_NAME);
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new TeacherNPC(), ZONE_NAME);
	}
	
	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);		
	}	

	@Before
	public void setupBefore() {		
		// setupQuiz
		loadQuest(this.quest = new FishermansLicenseQuiz());
		loadQuest(new FishermansLicenseCollector());		

		npc = getNPC(NPC_NAME);
		en = npc.getEngine();
		en.setCurrentState(ConversationStates.ATTENDING);
	}

	@Override
	@After
	public void tearDown() throws Exception {		
		super.tearDown();
	}

	@Test
	public void testHiHelpJob() {		
		en.setCurrentState(ConversationStates.IDLE);
		en.stepTest(player, "hi");
		
		assertThat(getReply(npc), is("Hello greenhorn!"));
		
		en.stepTest(player, "help");
		assertThat(getReply(npc), is("If you explore Faiumoni you will find several excellent fishing spots."));
		
		en.stepTest(player, "job");
		assertThat(getReply(npc), is("I'm a teacher for fishermen. People come to me to take their #exams."));
	}
	
	@Test
	public void testQuestWhenExamNotPassed() {
		en.stepTest(player, "quest");
		assertEquals(
				"I don't need anything from you, but if you like, you can do an #exam to get a fisherman's license.",
				getReply(npc));
	}

	@Test
	public void testExamDecline() {
		
		en.stepTest(player, "exam");
		assertThat(getReply(npc), is("Are you ready for the first part of your exam?"));
		
		en.stepTest(player, "no");
		assertThat(getReply(npc), is("Come back when you're ready."));
		
		assertLoseKarma(0);
		assertThat(player.getQuest(FishermansLicenseQuiz.QUEST_SLOT), is("rejected"));		
	}
	
	@Test
	public void testQuestWhenExamPassedButNotCollector() {
		player.setQuest(FishermansLicenseQuiz.QUEST_SLOT, "done");

		en.stepTest(player, "quest");
		assertEquals(
				"I don't need anything from you, but if you like, you can do an #exam to get a fisherman's license.",
				getReply(npc));
	}	

	@Test
	public void testExamFailure() {
		
		player.setQuest(FishermansLicenseQuiz.QUEST_SLOT, null);
		
		Set<String> fishInExam = ImmutableSet.of("trout", "perch",
				"mackerel", "cod", "roach", "char", "clownfish", "surgeonfish");
		
		en.stepTest(player, "exam");

		assertThat(getReply(npc), is("Are you ready for the first part of your exam?"));
		assertThat(en.getCurrentState(), is(ConversationStates.QUEST_OFFERED));

		en.stepTest(player, "yes");

		assertThat(getReply(npc), is("Fine. The first question is: What kind of fish is this?"));

		System.out.println("TEST zone " + zone.getName() + " " + zone.hashCode());
		
		
		// assert fish on the table
		Optional<Item> itemOnGround = zone.getItemsOnGround().stream().findFirst();
		
		assertThat(itemOnGround.isPresent(), is(true));
		assertThat(itemOnGround.get().getItemClass(), is("food"));
		
		assertThat(fishInExam, hasItem(itemOnGround.get().getName()));
		
		// wrong answear
		en.stepTest(player, "notAFish");
		assertThat(getReply(npc), is("No, that's wrong. Unfortunately you have failed, but you can try again tomorrow."));
		
		// cannot take test immediately 
		en.stepTest(player, "exam");
		assertThat(getReply(npc), startsWith("You can only do the quiz once a day. Come back in"));
		
		// can take test after time passed
		player.setQuest(FishermansLicenseQuiz.QUEST_SLOT, "1");
		en.stepTest(player, "exam");
		assertThat(getReply(npc), is("Are you ready for the first part of your exam?"));
	}
	
	@Test
	public void testExamSuccess() {
		
		player.setQuest(FishermansLicenseQuiz.QUEST_SLOT, null);
		
		Set<String> fishInExam = ImmutableSet.of("trout", "perch",
				"mackerel", "cod", "roach", "char", "clownfish", "surgeonfish");
		
		Set<String> guessedFish = new HashSet<String>();
		
		en.stepTest(player, "exam");

		assertThat(getReply(npc), is("Are you ready for the first part of your exam?"));
		assertThat(en.getCurrentState(), is(ConversationStates.QUEST_OFFERED));

		en.stepTest(player, "yes");

		assertThat(getReply(npc), is("Fine. The first question is: What kind of fish is this?"));
				
		
		for(int i = 0 ; i < fishInExam.size(); i++)
		{
			// fish presented
			Optional<Item> itemOnGround = zone.getItemsOnGround().stream().findFirst();
			assertThat(itemOnGround.isPresent(), is(true));
			
			// say correct fish name + store for tracking presented fish
			String fishName = itemOnGround.get().getName();
			guessedFish.add(fishName);
			en.stepTest(player, fishName);
			
			// validate fish description doesn't contain fish name
			assertThat(itemOnGround.get().getDescription(), not(containsString(fishName)));
			
			if(i == fishInExam.size() - 1)
			{
				assertThat(getReply(npc), is("Correct! Congratulations, you have passed the first part of the #exam."));
				
				assertThat(player.getQuest(FishermansLicenseQuiz.QUEST_SLOT), is("done"));
				assertGainKarma(15);
				assertThat(player.getXP(), is(500));
			}
			else
			{
				
				assertThat(getReply(npc), startsWith("Correct!"));
			}			
		}
		
		// presented fish are as test expectation
		assertThat(guessedFish.size(), is(fishInExam.size()));
		assertThat(Sets.intersection(guessedFish, fishInExam).size(), is(guessedFish.size()));
	}
	
	@Test
	public void testQuestWhenExamAndCollectorExamPassed() {
		player.setQuest(FishermansLicenseQuiz.QUEST_SLOT, "done");
		player.setQuest(FishermansLicenseCollector.QUEST_SLOT, "done");

		en.stepTest(player, "quest");
		assertEquals("I don't have a task for you, and you already have a fisherman's license.", getReply(npc));
	}
	
	@Test
	public void testExamWhenExamAndCollectorExamPassed() {
		player.setQuest(FishermansLicenseQuiz.QUEST_SLOT, "done");
		player.setQuest(FishermansLicenseCollector.QUEST_SLOT, "done");

		en.stepTest(player, "exam");
		assertEquals("You have already got your fisherman's license.", getReply(npc));
	}
	
	@Test
	public void testExamCollectorStarted() {
		player.setQuest(FishermansLicenseQuiz.QUEST_SLOT, "done");
		player.setQuest(FishermansLicenseCollector.QUEST_SLOT, "");

		en.stepTest(player, "exam");
		assertEquals("I hope you were not lazy and that you brought me some other fish #species.", getReply(npc));
		
		assertThat(en.getCurrentState(), is(ConversationStates.ATTENDING));
	}
	
	
	@Test
	public void testHistory() {
		
		// no quest		
		assertThat(quest.getHistory(player).size(), is(0));
		
		// quest failed can do it again		
		player.setQuest(FishermansLicenseQuiz.QUEST_SLOT, "12");
		
		assertThat(quest.getHistory(player), contains("I met Santiago in a hut in Ados city. If I pass his quiz I get a fishing license.",
				"Although I failed the last exam, I could now try again."));

		// quest failed and I can try tomorrow
		player.setQuest(FishermansLicenseQuiz.QUEST_SLOT, String.valueOf(Calendar.getInstance().getTimeInMillis()));
		
		assertThat(quest.getHistory(player), contains("I met Santiago in a hut in Ados city. If I pass his quiz I get a fishing license.",
				"I failed the last exam and it's too soon to try again."));
		
		// quest completed
		player.setQuest(FishermansLicenseQuiz.QUEST_SLOT, "done");
		
		assertThat(quest.getHistory(player), contains("I met Santiago in a hut in Ados city. If I pass his quiz I get a fishing license.",
				"I got all the names of the fish right and now I'm a better fisherman!"));
	}
	
	
}
