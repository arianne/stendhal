/***************************************************************************
 *                      (C) Copyright 2023 - Stendhal                      *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.behaviour.adder.OutfitChangerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Pair;


/**
 * Handles registering shops & configuring NPCs to sell outfits.
 */
public class OutfitShopsList {

  private static Logger logger = Logger.getLogger(OutfitShopsList.class);

  /** Registered shops. */
  private Map<String, OutfitShopInventory> registry;

  /** Singleton instance. */
  private static OutfitShopsList instance;


  /**
   * Retrieves singlton instance.
   */
  public static OutfitShopsList get() {
    if (instance == null) {
      instance = new OutfitShopsList();
    }
    return instance;
  }

  /**
   * Hidden singleton constructor.
   */
  private OutfitShopsList() {
    registry = new HashMap<String, OutfitShopInventory>();
  }

  /**
   * Registers a new shop.
   *
   * @param id
   *     String identifier.
   * @param inventory
   *     Outfits inventory with prices.
   */
  public void register(final String id, final OutfitShopInventory inventory) {
    registry.put(id, inventory);
  }

  /**
   * Retrieves a registered shop.
   *
   * @param id
   *     String identifier.
   * @return
   *     Outfits inventory or null if id isn't registered.
   */
  public OutfitShopInventory get(final String id) {
    return registry.getOrDefault(id, null);
  }

  /**
   * Configures an NPC to use a shop.
   *
   * @param npc
   *     NPC to configure.
   * @param id
   *     Shop identifier.
   * @param action
   *     NPC action trigger, e.g. "lend", "borrow", "buy", etc.
   * @param offer
   *     Defines if the NPC should react to the word "offer".
   * @param canReturn
   *     If true, player can say "return" to get original outfit back.
   * @param expiration
   *     Amount of time player can wear outfit.
   */
  public void configureSeller(final SpeakerNPC npc, final String id, final String action,
      final boolean offer, final boolean canReturn, final int expiration) {
    if (npc == null) {
      logger.error("Cannot configure outfit shop \"" + id + "\" for non-existing NPC ");
      return;
    }
    final String name = npc.getName();
    final OutfitShopInventory inventory = get(id);
    if (inventory == null) {
      logger.error("Cannot configure non-existing outfit shop for NPC " + name);
      return;
    }
    logger.info("Configuring outfit shop \"" + id + "\" for NPC " + name);
    final Map<String, Integer> pricelist = new TreeMap<String, Integer>();
    for (final Map.Entry<String, Pair<Outfit, Integer>> entry: inventory.entrySet()) {
      pricelist.put(entry.getKey(), entry.getValue().second());
    }
    final OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(pricelist, expiration,
        null, true) {
      @Override
      public void putOnOutfit(final Player player, final String oname) {
        // TODO: update OutfitChangerBehaviour to not set outfit list internally
        if (this.resetBeforeChange) {
          player.returnToOriginalOutfit();
        }
        final boolean temporary = this.getEndurance() != OutfitChangerBehaviour.NEVER_WEARS_OFF;
        player.setOutfit(inventory.getOutfit(oname).putOver(player.getOutfit()), temporary);
      }
    };
    new OutfitChangerAdder().addOutfitChanger(npc, behaviour, action, offer, canReturn);
  }

  /**
   * Configures an NPC to use a shop with outfits that do not expire.
   *
   * @param npc
   *     NPC to configure.
   * @param id
   *     Shop identifier.
   * @param action
   *     NPC action trigger, e.g. "lend", "borrow", "buy", etc.
   * @param offer
   *     Defines if the NPC should react to the word "offer".
   * @param canReturn
   *     If true, player can say "return" to get original outfit back.
   */
  public void configureSeller(final SpeakerNPC npc, final String id, final String action,
      final boolean offer, final boolean canReturn) {
    configureSeller(npc, id, action, offer, canReturn, OutfitChangerBehaviour.NEVER_WEARS_OFF);
  }

  /**
   * Configures an NPC to use a shop.
   *
   * @param name
   *     Name of NPC to configure.
   * @param id
   *     Shop identifier.
   * @param action
   *     NPC action trigger, e.g. "lend", "borrow", "buy", etc.
   * @param offer
   *     Defines if the NPC should react to the word "offer".
   * @param canReturn
   *     If true, player can say "return" to get original outfit back.
   * @param expiration
   *     Amount of time player can wear outfit.
   */
  public void configureSeller(final String name, final String id, final String action,
      final boolean offer, final boolean canReturn, final int expiration) {
    configureSeller(NPCList.get().get(name), id, action, offer, canReturn, expiration);
  }

  /**
   * Configures an NPC to use a shop with outfits that do not expire.
   *
   * @param name
   *     Name of NPC to configure.
   * @param id
   *     Shop identifier.
   * @param action
   *     NPC action trigger, e.g. "lend", "borrow", "buy", etc.
   * @param offer
   *     Defines if the NPC should react to the word "offer".
   * @param canReturn
   *     If true, player can say "return" to get original outfit back.
   */
  public void configureSeller(final String name, final String id, final String action,
      final boolean offer, final boolean canReturn) {
    configureSeller(name, id, action, offer, canReturn, OutfitChangerBehaviour.NEVER_WEARS_OFF);
  }
}
