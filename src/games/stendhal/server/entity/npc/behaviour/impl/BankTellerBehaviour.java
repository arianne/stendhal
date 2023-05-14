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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;


/**
 * Behaviour for bank tellers.
 */
public class BankTellerBehaviour {

  private static final Logger logger = Logger.getLogger(BankTellerBehaviour.class);

  private static final String QUEST_SLOT = "bank_deposits";


  /**
   * Retrieves the amount of money player has deposited in bank account.
   *
   * @param player
   *   Player requesting transaction.
   * @return
   *   Amount of money in account.
   */
  public static int getBalance(final Player player) {
    // get current deposits from quest slot
    String state = player.getQuest(QUEST_SLOT);
    state = state != null ? state : "money=0";
    for (final String item: state.split(";")) {
      if (item.startsWith("money=")) {
        return MathHelper.parseIntDefault(item.split("=")[1], 0);
      }
    }
    return 0;
  }

  /**
   * Sets the amount of deposited money in player bank account.
   *
   * @param player
   *   Player requesting transaction.
   * @param amount
   *   Amount of money to be stored in account.
   * @return
   *   Amount of money in account.
   */
  private static int setBalance(final Player player, final int amount) {
    int index = 0;
    final String state = player.getQuest(QUEST_SLOT);
    final List<String> tmp = state != null ? Arrays.asList(state.split(";")) : new ArrayList<>();
    for (int idx = 0; idx < tmp.size(); idx++) {
      if (tmp.get(idx).startsWith("money=")) {
        index = idx;
        break;
      }
    }
    player.setQuest(QUEST_SLOT, index, "money=" + amount);
    return getBalance(player);
  }

  /**
   * Adds money to player's account from inventory.
   *
   * @param player
   *   Player requesting transaction.
   * @param amount
   *   Amount of money to be added to account.
   * @return
   *   Response from teller NPC.
   */
  public static String deposit(final Player player, final Integer amount) {
    if (amount == null) {
      return "Please specify a valid amount of money to deposit.";
    }
    if (amount < 1) {
      return "You cannot deposit " + amount + " money.";
    }
    final int carrying_start = player.getNumberOfEquipped("money");
    if (carrying_start == 0) {
      return "You aren't carrying any money to deposit.";
    }
    if (carrying_start < amount) {
      return "You aren't carrying that much money.";
    }

    final int balance_start = getBalance(player);
    final int deposited = BankTellerBehaviour.setBalance(player, balance_start + amount)
        - balance_start;
    if (deposited != amount) {
      logger.error("Player " + player.getName() + " requested to deposit " + amount + " money, but"
          + " actual amount was " + deposited);
    }
    player.drop("money", deposited);
    return "You deposited " + deposited + " money.";
  }

  /**
   * Subtracts money from player's account & adds to inventory.
   *
   * @param player
   *   Player requesting transaction.
   * @param amount
   *   Amount of money to be subtracted from account.
   * @return
   *   Response from teller NPC.
   */
  public static String withdraw(final Player player, final Integer amount) {
    if (amount == null) {
      return "Please specify a valid amount of money to withdraw.";
    }
    if (amount < 1) {
      return "You cannot withdraw " + amount + " money.";
    }
    final int balance_start = BankTellerBehaviour.getBalance(player);
    if (balance_start == 0) {
      return "You have no money in your account to withdraw.";
    }
    if (balance_start < amount) {
      return "You don't have that much money in your bank account.";
    }

    final int withdrawn = balance_start
        - BankTellerBehaviour.setBalance(player, balance_start - amount);
    if (withdrawn != amount) {
      logger.error("Player " + player.getName() + " requested to withdraw " + amount + " money, but"
          + " actual amount was " + withdrawn);
    }
    final StackableItem money = (StackableItem) SingletonRepository.getEntityManager()
        .getItem("money");
    money.setQuantity(withdrawn);
    player.equipOrPutOnGround(money);
    return "You withdrew " + money.getQuantity() + " money.";
  }

  /**
   * Checks player's account balance.
   *
   * @param player
   *   Player requesting transaction.
   * @return
   *   Response from teller NPC.
   */
  public static String queryBalance(final Player player) {
    final int deposits = BankTellerBehaviour.getBalance(player);
    if (deposits == 0) {
      return "You have no money in your account.";
    }
    return "Your account balance is " + deposits + " money.";
  }
}
