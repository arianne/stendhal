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
package games.stendhal.server.entity.npc.behaviour.adder;

import java.util.Arrays;
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.BankTellerBehaviour;
import games.stendhal.server.entity.player.Player;


/**
 * Adds bank teller behaviour to an NPC.
 */
public class BankTellerAdder {

  /** Action when player deposits money into account. */
  private static final ChatAction depositAction = new ChatAction() {
    @Override
    public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
      if ("deposit".equals(sentence.getTrimmedText().toLowerCase())) {
        npc.say("Please specify an amount of money to deposit.");
        return;
      }
      npc.say(BankTellerBehaviour.deposit(player,
          BankTellerAdder.parseTransactionAmount(player, sentence, true)));
    }
  };

  /** Action when player withdraws money from account. */
  private static final ChatAction withdrawAction = new ChatAction() {
    @Override
    public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
      if ("withdraw".equals(sentence.getTrimmedText().toLowerCase())) {
        npc.say("Please specify an amount of money to withdraw.");
        return;
      }
      npc.say(BankTellerBehaviour.withdraw(player,
          BankTellerAdder.parseTransactionAmount(player, sentence, false)));
    }
  };

  /** Action when player wants to know account balance. */
  private static final ChatAction queryBalanceAction = new ChatAction() {
    @Override
    public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
      npc.say(BankTellerBehaviour.queryBalance(player));
    }
  };


  /**
   * Adds bank teller behaviour to an NPC.
   *
   * @param npc
   *   SpeakerNPC to receive behaviour.
   */
  public static void addTeller(final SpeakerNPC npc) {
    npc.put("job_teller", "");

    final String help_res = npc.getReply("help");
    if (help_res == null) {
      npc.addHelp("I can help you manage your bank #balance. Tell me if you would like to"
          + " #deposit to or #withdraw from your account. If you want you can #'deposit all' or"
          + " #'withdraw all'.");
    } else {
      npc.addHelp("I can also help you manage your bank #balance. Tell me if you would like to"
          + " #deposit to or #withdraw from your account. If you want you can #'deposit all' or"
          + " #'withdraw all'.");
    }
    final String job_res = npc.getJob();
    if (job_res == null) {
      npc.addJob("I can #help you manage your bank #balance.");
    }

    npc.add(
      ConversationStates.ATTENDING,
      "deposit",
      null,
      ConversationStates.ATTENDING,
      null,
      depositAction);

    npc.add(
      ConversationStates.ATTENDING,
      "withdraw",
      null,
      ConversationStates.ATTENDING,
      null,
      withdrawAction);

    npc.add(
      ConversationStates.ATTENDING,
      "balance",
      null,
      ConversationStates.ATTENDING,
      null,
      queryBalanceAction);
  }

  /**
   * Checks if an NPC is a bank teller.
   *
   * @param npc
   *   SpeakerNPC to check.
   * @return
   *   `true` if NPC has "job_teller" attribute.
   */
  public static boolean isTeller(final SpeakerNPC npc) {
    return npc.has("job_teller");
  }

  /**
   * Parses requested transaction amount from player's sentence.
   *
   * @param player
   *   Player requesting transaction.
   * @param sentence
   *   Sentence to parse.
   * @param deposit
   *   `true` for deposits, `false` for withdrawals.
   * @return
   *   Amount of money for transaction or `null` if invalid amount.
   */
  private static Integer parseTransactionAmount(final Player player, final Sentence sentence,
      final boolean deposit) {
    Integer amount = null;
    final List<String> statement = Arrays.asList(sentence.getTrimmedText().split(" "));
    if (statement.size() < 2) {
      return null;
    }
    try {
      amount = Integer.parseInt(statement.get(1));
    } catch (final NumberFormatException e) {
      if ("all".equals(statement.get(1).toLowerCase())) {
        // player wants to withdraw or deposit all money
        if (deposit) {
          amount = player.getNumberOfEquipped("money");
        } else {
          amount = BankTellerBehaviour.getBalance(player);
        }
      }
    }
    return amount;
  }
}
