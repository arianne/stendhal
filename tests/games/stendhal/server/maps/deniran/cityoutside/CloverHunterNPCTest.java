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
package games.stendhal.server.maps.deniran.cityoutside;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import utilities.ZonePlayerAndNPCTestImpl;


public class CloverHunterNPCTest extends ZonePlayerAndNPCTestImpl {

	@Override
	@Before
	public void setUp() throws Exception {
		setupZone("test_zone", new CloverHunterNPC());
		setNpcNames("Maple");
		setZoneForPlayer("test_zone");

		super.setUp();
	}

	@Test
	public void testDialogue() {
		assertThat(player, notNullValue());
		SpeakerNPC npc = getNPC("Maple");
		Engine en = npc.getEngine();

		en.step(player, "hi");
		assertThat(getReply(npc), is("Hello fellow clover hunter!"));
		en.step(player, "job");
		assertThat(getReply(npc),
				is("I'm a clover hunter. I'm searching for the lucky four-leaf #clover."));
		en.step(player, "help");
		assertThat(getReply(npc), is("Four-leaf #clovers are extremely rare. If you find one, it is"
				+ " said you will have excellent luck!"));
		en.step(player, "offer");
		assertThat(getReply(npc), is("I can tell you a little about #clovers."));
		en.step(player, "clover");
		assertThat(getReply(npc), is("Clovers can grow just about anywhere in the sunlight. So don't go"
				+ " looking for any underground. Ones with four leaves are especially rare and are a"
				+ " challenging #task to find."));
		en.step(player, "clovers");
		assertThat(getReply(npc), is("Clovers can grow just about anywhere in the sunlight. So don't go"
				+ " looking for any underground. Ones with four leaves are especially rare and are a"
				+ " challenging #task to find."));
		en.step(player, "task");
		// reply when quest is not loaded
		assertThat(getReply(npc), is("No, no thank you. I can find a four-leaf clover on my own."));
		en.step(player, "bye");
		assertThat(getReply(npc), is("May luck shine brightly o'er you!"));
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
	}
}
