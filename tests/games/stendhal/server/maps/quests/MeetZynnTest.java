/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
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
import static org.junit.Assert.assertThat;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.semos.library.HistorianGeographerNPC;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class MeetZynnTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "int_semos_library";
	private static final String NPC_NAME = "Zynn Iwuhos";

	private SpeakerNPC npc;
	private Engine en;

	public MeetZynnTest() {
		setNpcNames(NPC_NAME);
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new HistorianGeographerNPC(), ZONE_NAME);
	}

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	@Before
	public void setupBefore() {
		// setupQuiz
		loadQuest(this.quest = new MeetZynn());

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
	public void testInformationReponses() {

		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

		builder.put("history",
				"At present, there are two significant powers on Faiumoni; the Deniran Empire, and the dark legions of Blordrough. Blordrough has recently conquered the south of the island, seizing several steel mines and a large gold mine. At present, Deniran still controls the central and northern parts of Faiumoni, including several gold and mithril mines.")
				.put("news",
						"The Deniran Empire is currently seeking adventurers to sign on as mercenaries with their army to retake southern Faiumoni from the forces of Blordrough. Unfortunately Blordrough is still holding out against everything the Empire can throw at him.")
				.put("geography",
						"Let's talk about the different #places you can visit on Faiumoni! I can also help you #get and #use a map, or give you advice on using the psychic #SPS system.")
				.put("places",
						"The most important locations on #Faiumoni are #Or'ril Castle, #Semos, #Ados, #Nalwor, and of course #Deniran City.")
				.put("Faiumoni",
						"Faiumoni is the island on which you stand! You've probably already noticed the mountains to the north. There is also a large desert in the middle of the island, and of course the river which bisects it, just below #Or'ril Castle to the south.")
				.put("Semos",
						"Semos is our town where you are right now. We're on the north side of Faiumoni, with a population of about 40-50.")
				.put("Ados",
						"Ados is an important coastal city to the east of us here in #Semos, where merchants bring trade from #Deniran. It's widely considered to be one of the Empire's most important shipping routes.")
				.put("Or'ril",
						"Or'ril Castle is one of a series of such castles built to defend the imperial road between #Ados and #Deniran. When in use, it housed a fighting force of around 60 swordsmen, plus ancillary staff. Now that most of the Empire's army has been sent south to fend off the invaders, the castle has been abandoned by the military. As far as I'm aware, it should be empty; I hear some rumours, though...")
				.put("Nalwor",
						"Nalwor is an ancient elven city, built deep inside the forest to the southeast of us long before humans ever arrived on this island. Elves don't like mixing with other races, but we're given to understand that Nalwor was built to help defend their capital city of Teruykeh against an evil force.")
				.put("Deniran",
						"The Empire's capital city of Deniran lies in the heart of Faiumoni, and is the main base of operations for the Deniran army. Most of the Empire's main trade routes with other countries originate in this city, then extending north through #Ados, and south to Sikhw. Unfortunately, the southern routes were been destroyed when Blordrough and his armies conquered Sikhw, some time ago now.")
				.put("use",
						"Once you #get a map, there are three scales on which you need to understand it. Firstly, there are the map #levels, then you need to familiarize yourself with the #naming conventions for the different zones within those levels, and lastly you should learn how we describe a person's #positioning within a zone. We'll have you navigating around in no time!")
				.put("levels",
						"Maps are split into levels according to the height of that particular area above or below the surface. Areas on the surface are at level 0. The level number is the first thing in a map's name. For instance, #Semos itself is at ground level, so it is level 0; its map is called \"0_semos_city\". The first level of the dungeon beneath us is at level -1, so its map is called \"-1_semos_dungeon\". You should note, though, that a map of a building's interior will usually have the level at the end of the name instead, with \"int\" (for \"interior\") at the start. For instance, the upstairs floor of the tavern would be mapped out as \"int_semos_tavern_1\".")
				.put("naming",
						"Each map is usually split up into \"sets\" of zones, with a central feature that is used as a reference point. The zones surrounding this central zone are named by the direction in which they lie from it. For instance, from the central zone \"0_semos_city\", you can travel west to the old village at \"0_semos_village_w\", or you could travel two zones north and one west to the mountains at \"0_semos_mountain_n2_w\".")
				.put("positioning",
						"Positioning within a zone is simply described in terms of co-ordinates. We conventionally take the top-left corner (that is, the northwest corner) to be the origin point with co-ordinates (0, 0). The first co-ordinate ('x') increases as you move to the right (that is, east) within the zone, and the second co-ordinate ('y') increases as you move down (that is, south).")
				.put("get",
						"You can get a map of Stendhal at #https://stendhalgame.org/world/atlas.html if you want one. Careful you don't spoil any surprises for yourself, though!")
				.put("SPS",
						"SPS stands for Stendhal Positioning System; you can ask #Io in the Temple about the exact details of how it works. Essentially, it allows you to ascertain the exact current location of yourself or your friends at any time.")
				.put("Io",
						"Her full name is \"Io Flotto\". She spends most of her time in the Temple, um, floating. She may seem weird, but her \"intuition\" works far better than any mere compass, as I can vouch.")
				.build().forEach((String ask, String response) -> {

					en.step(player, ask);
					assertThat(getReply(npc), is(response));

				});
	}

	@Test
	public void testBye() {
		// below 15
		player.setLevel(14);
		en.step(player, "bye");
		assertThat(getReply(npc), is("Bye. Hey, if you're going to hang around the library, don't forget to be quiet; people could be studying!"));
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
		
		// over 14
		en.setCurrentState(ConversationStates.ATTENDING);
		player.setLevel(15);
		en.step(player, "bye");
		assertThat(getReply(npc), is("Bye. Hey, you should consider getting a library card, you know."));
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
	}	

}
