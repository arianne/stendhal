/***************************************************************************
 *                   (C) Copyright 2019 - Arianne                          *
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ados.animal_sanctuary.ZoologistNPC;
import games.stendhal.server.maps.ados.church.HealerNPC;
import games.stendhal.server.maps.ados.magician_house.WizardNPC;
import games.stendhal.server.maps.kirdneh.river.RetiredTeacherNPC;
import games.stendhal.server.maps.quests.antivenom_ring.AntivenomRing;
import games.stendhal.server.maps.semos.apothecary_lab.ApothecaryNPC;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class AntivenomRingTest extends ZonePlayerAndNPCTestImpl {
	private static final String ZONE_NAME = "testzone";

	private SpeakerNPC apothecary;
	private SpeakerNPC zoologist;

	private final String questName = "antivenom_ring";
	private final String subquestName = questName + "_extract";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		setZoneForPlayer(ZONE_NAME);
		setNpcNames("Jameson", "Zoey", "Valo", "Haizen", "Ortiv Milquetoast");

		addZoneConfigurator(new ApothecaryNPC(), ZONE_NAME);
		addZoneConfigurator(new ZoologistNPC(), ZONE_NAME);
		addZoneConfigurator(new HealerNPC(), ZONE_NAME);
		addZoneConfigurator(new WizardNPC(), ZONE_NAME);
		addZoneConfigurator(new RetiredTeacherNPC(), ZONE_NAME);

		super.setUp();

		apothecary = getNPC("Jameson");
		zoologist = getNPC("Zoey");

		// initialize quest
		new AntivenomRing().addToWorld();
	}

	@Test
	public void testQuest() {
		testEntities();
		testQuestNotActive();
	}

	private void testEntities() {
		assertNotNull(player);
		assertNotNull(apothecary);
		assertNotNull(zoologist);
	}

	private void testQuestNotActive() {
		assertNull(player.getQuest(questName));
		assertNull(player.getQuest(subquestName));
	}
}
