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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.semos.temple.TelepathNPC;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class MeetIoTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "int_semos_temple";
	private static final String NPC_NAME = "Io Flotto";	

	private SpeakerNPC npc;
	private Engine en;		

	public MeetIoTest() {
		setNpcNames(NPC_NAME);
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new TelepathNPC(), ZONE_NAME);
	}
	
	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);		
	}	

	@Before
	public void setupBefore() {		
		// setupQuiz
		loadQuest(this.quest = new MeetIo());	

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
	public void testHistory() {		
		assertThat(quest.getHistory(player), is(empty()));
		
		player.setQuest(quest.getSlotName(), "done");		
		assertThat(quest.getHistory(player), contains("I met the telepath Io Flotto in Semos Temple.", "Io taught me the six basic elements of telepathy and promised to remind me if I need to refresh my knowledge."));
	}
	
	@Test
	public void testQuestInfo() {
		QuestInfo questInfo = quest.getQuestInfo(player);
		
		assertThat(questInfo.getName(), is("Meet Io"));
		assertThat(questInfo.getDescription(), is("Io Flotto can teach about how to communicate."));
		assertThat(questInfo.getRepeatable(), is(false));		
	}

	@Test
	public void testDontWantToLearn() {
		en.step(player, "help");
		assertThat(getReply(npc), is("I'm a telepath and a telekinetic; I can help you by sharing my mental skills with you. Do you want me to teach you the six basic elements of telepathy? I already know the answer but I'm being polite..."));
		assertThat(en.getCurrentState(), is(ConversationStates.ATTENDING));
		
		progressToStateInformation6();
		
		en.step(player, "no");
		assertThat(getReply(npc), is("If you ever decide to widen the frontiers of your mind a bit more, drop by and say hello. Farewell for now!"));
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
		
		assertGainXP(0);
		assertThat(player.getQuest(quest.getSlotName()), is(nullValue()));
	}	

	@Test
	public void testQuestComplition() {
		
		en.step(player, "help");
		assertThat(getReply(npc), is("I'm a telepath and a telekinetic; I can help you by sharing my mental skills with you. Do you want me to teach you the six basic elements of telepathy? I already know the answer but I'm being polite..."));
		assertThat(en.getCurrentState(), is(ConversationStates.ATTENDING));
		
		progressToStateInformation6();
		
		en.step(player, "Yes");
		assertThat(getReply(npc), is("*yawns* Maybe I'll show you later... I don't want to overload you with too much information at once. You can get a summary of all those lessons at any time, incidentally, just by typing #/help.\nRemember, don't let anything disturb your concentration."));
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));

		assertGainXP(10);
		assertGainMoney(10);
		assertThat(player.getQuest(quest.getSlotName()), is("done"));
	}
	
	@Test
	public void testHelpWithQuestDone() {
		
		player.setQuest(quest.getSlotName(), "done");
		
		en.step(player, "help");
		assertThat(getReply(npc), is("Do you want to repeat the six basic elements of telepathy? I already know the answer but I'm being polite..."));
		assertThat(en.getCurrentState(), is(ConversationStates.ATTENDING));		
		
		progressToStateInformation6();
				
		en.step(player, "Yes");
		assertThat(getReply(npc), is("*yawns* Maybe I'll show you later... I don't want to overload you with too much information at once. You can get a summary of all those lessons at any time, incidentally, just by typing #/help.\nHey! I know what you're thinking, and I don't like it!"));
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));	
		
		assertGainXP(0);
	}
	
	private void progressToStateInformation6() {
		en.step(player, "yes");
		assertThat(getReply(npc), is("Type #/who to ascertain the names of those adventurers who are currently present in the world of Stendhal. Do you want to learn the second basic element of telepathy?"));
		assertThat(en.getCurrentState(), is(ConversationStates.INFORMATION_1));		
		
		en.step(player, "yes");
		assertThat(getReply(npc), is("Type #/where #username to discern where in Stendhal that person is currently roaming; you can use #'/where sheep' to keep track of any sheep you might own. To understand the system used for defining positions in Stendhal, try asking #Zynn; he knows more about it than I do. Ready for the third lesson?"));
		assertThat(en.getCurrentState(), is(ConversationStates.INFORMATION_2));
		
		en.step(player, "Zynn");
		assertThat(getReply(npc), is("His full name is Zynn Iwuhos. He spends most of his time in the library, making maps and writing historical record books. Ready for the next lesson?"));
		assertThat(en.getCurrentState(), is(ConversationStates.INFORMATION_2));
		
		en.step(player, "Yes");
		assertThat(getReply(npc), is("Type #'/tell username message' or #'/msg username message' to talk to anybody you wish, no matter where in Stendhal that person is.  You can type #'// response' to continue talking to the last person you send a message to. Ready to learn my fourth tip?"));
		assertThat(en.getCurrentState(), is(ConversationStates.INFORMATION_3));
		
		en.step(player, "Yes");
		assertThat(getReply(npc), is("Press #Shift+Up at the same time to recall things you previously said, in case you need to repeat yourself. Okay, shall we move on to the fifth lesson?"));
		assertThat(en.getCurrentState(), is(ConversationStates.INFORMATION_4));
		
		en.step(player, "Yes");
		assertThat(getReply(npc), is("Type #/support #<message> to report a problem. You can also try the IRC channel ##arianne on #'irc.libera.chat'. There is a web frontend at #https://stendhalgame.org/development/chat.html \nOkay, time for your last lesson in mental manipulation!"));
		assertThat(en.getCurrentState(), is(ConversationStates.INFORMATION_5));

		en.step(player, "Yes");
		assertThat(getReply(npc), is("You can travel to the astral plane at any time, thereby saving and closing your game. Just type #/quit, or press the #Esc key, or even simply close the window. Okay! Hmm, I think you want to learn how to float in the air like I do."));
		assertThat(en.getCurrentState(), is(ConversationStates.INFORMATION_6));
	}
	
	
}
