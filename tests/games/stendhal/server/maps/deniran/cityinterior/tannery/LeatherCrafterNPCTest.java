/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityinterior.tannery;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;


public class LeatherCrafterNPCTest extends ZonePlayerAndNPCTestImpl {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone("testzone");
	}

	@Override
	@Before
	public void setUp() throws Exception {
		setZoneForPlayer("testzone");
		setNpcNames("Tinny");
		addZoneConfigurator(new LeatherCrafterNPC(), "testzone");

		super.setUp();
	}

	@Test
	public void testDialogue() {
		final Player player = PlayerTestHelper.createPlayer("player");
		final SpeakerNPC npc = SingletonRepository.getNPCList().get("Tinny");

		assertThat(player, notNullValue());
		assertThat(npc, notNullValue());

		final Engine en = npc.getEngine();
		en.step(player, "hi");
		assertThat(getReply(npc), is("Hello, how can I help you?"));
		en.step(player, "job");
		assertThat(getReply(npc), is("I am a leather crafter. I recently completed my apprenticeship"
				+ " under Skinner and will one day take over responsibility of the tannery."));
		en.step(player, "help");
		assertThat(getReply(npc), is("If you are interested in a pouch to carry your money in, speak to"
				+ " Skinner."));
		en.step(player, "task");
		assertThat(getReply(npc), is("There is nothing I need help with at this time."));
		en.step(player, "bye");
		assertThat(getReply(npc), is("Goodbye."));
	}
}
