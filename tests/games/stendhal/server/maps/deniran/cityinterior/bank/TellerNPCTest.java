/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityinterior.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;


public class TellerNPCTest extends SpeakerNPCTestHelper {

  @Before
  public void setUp() {
    final StendhalRPZone zone = new StendhalRPZone("test_zone");
    // initialize NPC
    new TellerNPC().configureZone(zone, null);
  }

  @Test
  public void init() {
    final Player player = PlayerTestHelper.createPlayer("player");
    assertNotNull(player);

    final SpeakerNPC npc = getSpeakerNPC("Telly");
    assertNotNull(npc);

    final Engine en = npc.getEngine();

    assertTrue(en.step(player, "hi"));
    assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
    assertEquals("Hello, welcome to Deniran bank.", getReply(npc));

    assertTrue(en.step(player, "job"));
    assertEquals("I can #help you manage your bank #balance.", getReply(npc));
    assertTrue(en.step(player, "offer"));
    assertEquals("I can #help you manage your bank #balance.", getReply(npc));
    assertTrue(en.step(player, "help"));
    assertEquals("I can help you manage your bank #balance. Tell me if you would like to"
        + " #deposit to or #withdraw from your account. If you want you can #'deposit all' or"
        + " #'withdraw all'.",
        getReply(npc));
    assertTrue(en.step(player, "task"));
    assertEquals("There is nothing I need help with.", getReply(npc));

    assertTrue(en.step(player, "bye"));
    assertEquals(ConversationStates.IDLE, en.getCurrentState());
    assertEquals("Bye.", getReply(npc));
  }
}
