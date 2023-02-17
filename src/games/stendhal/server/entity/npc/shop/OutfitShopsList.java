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
package games.stendhal.server.entity.npc.shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
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
  public void add(final String id, final OutfitShopInventory inventory) {
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
   * @param expiration
   *     Amount of time player can wear outfit.
   * @param wearOffMessage
   *     Message to player when outfit expires.
   * @param flags
   *     Optional flags. Currently supported are 'resetOrig', 'noOffer', & 'returnable'.
   */
  public void configureNPC(final SpeakerNPC npc, final String id, final String action,
      final int expiration, final String wearOffMessage, final List<String> flags) {
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
    logger.info("Configuring outfit shop \"" + id + "\" for NPC " + name + " with flags `"
        + flags + "`");

    // parse flags
    final Map<String, String> fl = new HashMap<>();
    if (flags != null) {
      for (final String flag: flags) {
        if (flag.contains("=")) {
          final String[] tmp = flag.split("=");
          fl.put(tmp[0], tmp[1]);
        } else {
          fl.put(flag, "");
        }
      }
    }

    final Map<String, Integer> pricelist = new TreeMap<String, Integer>();
    for (final Map.Entry<String, Pair<String, Integer>> entry: inventory.entrySet()) {
      pricelist.put(entry.getKey(), entry.getValue().second());
    }
    final OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(pricelist, expiration,
        wearOffMessage, fl.containsKey("resetOrig")) {
      @Override
      public void putOnOutfit(final Player player, final String oname) {
        // TODO: update OutfitChangerBehaviour to not set outfit list internally
        if (this.resetBeforeChange) {
          player.returnToOriginalOutfit();
        }
        final int expiration = this.getEndurance();
        if (expiration == OutfitChangerBehaviour.NEVER_WEARS_OFF) {
          player.setOriginalOutfit(inventory.getOutfit(oname));
        } else {
          player.setTemporaryOutfit(inventory.getOutfit(oname), expiration);
        }
      }
    };
    new OutfitChangerAdder().addOutfitChanger(npc, behaviour, action, !fl.containsKey("noOffer"),
        fl.containsKey("returnable"));
  }

  /**
   * Configures an NPC to use a shop.
   *
   * @param npc
   *     Name of NPC to configure.
   * @param id
   *     Shop identifier.
   * @param action
   *     NPC action trigger, e.g. "lend", "borrow", "buy", etc.
   * @param expiration
   *     Amount of time player can wear outfit.
   * @param wearOffMessage
   *     Message to player when outfit expires.
   * @param flags
   *     Optional flags. Currently supported are 'resetOrig', 'noOffer', & 'returnable'.
   */
  public void configureNPC(final String npc, final String id, final String action,
      final int expiration, final String wearOffMessage, final List<String> flags) {
    configureNPC(NPCList.get().get(npc), id, action, expiration, wearOffMessage, flags);
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
  @Deprecated
  public void configureNPC(final SpeakerNPC npc, final String id, final String action,
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

    String msg = "Configuring outfit shop \"" + id + "\" for NPC " + name + " with offer "
      + (offer ? "enabled" : "disabled");
    logger.info(msg);
    final Map<String, Integer> pricelist = new TreeMap<String, Integer>();
    for (final Map.Entry<String, Pair<String, Integer>> entry: inventory.entrySet()) {
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
  @Deprecated
  public void configureNPC(final SpeakerNPC npc, final String id, final String action,
      final boolean offer, final boolean canReturn) {
    configureNPC(npc, id, action, offer, canReturn, OutfitChangerBehaviour.NEVER_WEARS_OFF);
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
  @Deprecated
  public void configureNPC(final String name, final String id, final String action,
      final boolean offer, final boolean canReturn, final int expiration) {
    configureNPC(NPCList.get().get(name), id, action, offer, canReturn, expiration);
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
  @Deprecated
  public void configureNPC(final String name, final String id, final String action,
      final boolean offer, final boolean canReturn) {
    configureNPC(name, id, action, offer, canReturn, OutfitChangerBehaviour.NEVER_WEARS_OFF);
  }
}
