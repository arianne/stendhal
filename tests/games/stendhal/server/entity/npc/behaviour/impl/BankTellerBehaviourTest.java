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
package games.stendhal.server.entity.npc.behaviour.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;
import static utilities.PlayerTestHelper.createPlayer;
import static utilities.PlayerTestHelper.equipWithMoney;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BankTellerAdder;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;


public class BankTellerBehaviourTest {

  @BeforeClass
  public static void setUpBeforeClass() {
    MockStendlRPWorld.get();
  }

  /**
   * Helper function to get account balance.
   *
   * @return
   *   Current account balance of player.
   */
  private int getBalance(final Player player) {
    return BankTellerBehaviour.getBalanceAmount(player);
  }

  /**
   * Helper function to build an NPC.
   *
   * @param name
   *   Name of new NPC.
   * @return
   *   SpeakerNPC instance.
   */
  private SpeakerNPC buildNPC(final String name) {
    final SpeakerNPC npc = new SpeakerNPC(name);
    assertNotNull(npc);
    npc.addGreeting();
    npc.addGoodbye();
    return npc;
  }

  @Test
  public void testNPC1() {
    final Player player = createPlayer("player");
    assertNotNull(player);

    final SpeakerNPC npc = buildNPC("npc");
    assertFalse(BankTellerAdder.isTeller(npc));

    final Engine en = npc.getEngine();

    en.step(player, "hi");
    npc.clearEvents();
    assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
    en.step(player, "help");
    assertNull(getReply(npc));
    en.step(player, "job");
    assertNull(getReply(npc));

    BankTellerAdder.addTeller(npc);
    assertTrue(BankTellerAdder.isTeller(npc));
    en.step(player, "help");
    assertEquals("I can help you manage your bank #balance. Tell me if you would like to"
        + " #deposit to or #withdraw from your account. If you want you can #'deposit all' or"
        + " #'withdraw all'.",
        getReply(npc));
    en.step(player, "job");
    assertEquals("I can #help you manage your bank #balance.", getReply(npc));

    en.step(player, "bye");
    assertEquals(ConversationStates.IDLE, en.getCurrentState());
    npc.clearEvents();
  }

  @Test
  public void testNPC2() {
    final Player player = createPlayer("player");
    assertNotNull(player);

    final SpeakerNPC npc = buildNPC("npc");
    npc.addHelp("help message.");
    npc.addJob("job message.");
    assertFalse(BankTellerAdder.isTeller(npc));

    final Engine en = npc.getEngine();

    en.step(player, "hi");
    npc.clearEvents();
    assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
    en.step(player, "help");
    assertEquals("help message.", getReply(npc));
    en.step(player, "job");
    assertEquals("job message.", getReply(npc));

    BankTellerAdder.addTeller(npc);
    assertTrue(BankTellerAdder.isTeller(npc));
    en.step(player, "help");
    assertEquals("help message."
        + " I can also help you manage your bank #balance. Tell me if you would like to"
        + " #deposit to or #withdraw from your account. If you want you can #'deposit all' or"
        + " #'withdraw all'.",
        getReply(npc));
    en.step(player, "job");
    assertEquals("job message.", getReply(npc));

    en.step(player, "bye");
    assertEquals(ConversationStates.IDLE, en.getCurrentState());
    npc.clearEvents();
  }

  @Test
  public void testTransactions() {
    final Player player = createPlayer("player");
    assertNotNull(player);
    assertEquals(0, getBalance(player));
    assertEquals(0, player.getNumberOfEquipped("money"));

    final SpeakerNPC npc = buildNPC("npc");
    BankTellerAdder.addTeller(npc);

    final Engine en = npc.getEngine();

    en.step(player, "hi");
    npc.clearEvents();
    assertEquals(ConversationStates.ATTENDING, en.getCurrentState());

    en.step(player, "balance");
    assertEquals("You have no money in your account.", getReply(npc));

    en.step(player, "withdraw");
    assertEquals("Please specify an amount of money to withdraw.", getReply(npc));
    en.step(player, "withdraw foo");
    assertEquals("Please specify a valid amount of money to withdraw.", getReply(npc));
    en.step(player, "withdraw 0");
    assertEquals("You cannot withdraw 0 money.", getReply(npc));
    en.step(player, "withdraw -1");
    assertEquals("You cannot withdraw -1 money.", getReply(npc));
    en.step(player, "withdraw 1");
    assertEquals("You have no money in your account to withdraw.", getReply(npc));

    en.step(player, "deposit");
    assertEquals("Please specify an amount of money to deposit.", getReply(npc));
    en.step(player, "deposit foo");
    assertEquals("Please specify a valid amount of money to deposit.", getReply(npc));
    en.step(player, "deposit 0");
    assertEquals("You cannot deposit 0 money.", getReply(npc));
    en.step(player, "deposit -1");
    assertEquals("You cannot deposit -1 money.", getReply(npc));
    en.step(player, "deposit 1");
    assertEquals("You aren't carrying any money to deposit.", getReply(npc));

    equipWithMoney(player, 5);

    assertEquals(5, player.getNumberOfEquipped("money"));
    assertEquals(0, getBalance(player));

    en.step(player, "deposit 10");
    assertEquals("You aren't carrying that much money.", getReply(npc));

    assertEquals(5, player.getNumberOfEquipped("money"));
    assertEquals(0, getBalance(player));

    en.step(player, "deposit 1");
    assertEquals("You deposited 1 money.", getReply(npc));

    assertEquals(4, player.getNumberOfEquipped("money"));
    assertEquals(1, getBalance(player));

    en.step(player, "deposit 4");
    assertEquals("You deposited 4 money.", getReply(npc));

    assertEquals(0, player.getNumberOfEquipped("money"));
    assertEquals(5, getBalance(player));

    en.step(player, "withdraw 10");
    assertEquals("You don't have that much money in your bank account.", getReply(npc));

    assertEquals(0, player.getNumberOfEquipped("money"));
    assertEquals(5, getBalance(player));

    en.step(player, "withdraw 1");
    assertEquals("You withdrew 1 money.", getReply(npc));

    assertEquals(1, player.getNumberOfEquipped("money"));
    assertEquals(4, getBalance(player));

    en.step(player, "withdraw 4");
    assertEquals("You withdrew 4 money.", getReply(npc));

    assertEquals(5, player.getNumberOfEquipped("money"));
    assertEquals(0, getBalance(player));

    en.step(player, "bye");
    assertEquals(ConversationStates.IDLE, en.getCurrentState());
    npc.clearEvents();
  }
}
