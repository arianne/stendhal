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

import games.stendhal.server.entity.Outfit;
import marauroa.common.Pair;


/**
 * Represents contents & prices of an outfit shop shop.
 */
public class OutfitShopInventory extends ShopInventory<String, Pair<Outfit, Integer>> {

  /**
   * Adds an outfit to shop.
   *
   * @param name
   *     String identifier.
   * @param outfit
   *     Outfit to be sold.
   * @param price
   *     Amount of money required to buy outfit.
   */
  public void put(final String name, final Outfit outfit, final int price) {
    put(name, new Pair<Outfit, Integer>(outfit, price));
  }

  /**
   * Adds an outfit to shop.
   *
   * @param name
   *     String identifier.
   * @param outfit
   *     Outfit to be sold.
   * @param price
   *     Amount of money required to buy outfit.
   */
  public void put(final String name, final String outfit, final int price) {
    put(name, new Outfit(outfit), price);
  }

  /**
   * Retrieves an outfit sold by shop.
   *
   * @param name
   *     String identifier.
   * @return
   *     Outfit or null if name not found.
   */
  public Outfit getOutfit(final String name) {
    if (containsKey(name)) {
      return get(name).first();
    }
    return null;
  }

  /**
   * Retrieves the price of an outfit sold by shop.
   *
   * @param name
   *     String identifier.
   * @return
   *     Amount of money required to buy outfit or null if name not found.
   */
  public Integer getPrice(final String name) {
    if (containsKey(name)) {
      return get(name).second();
    }
    return null;
  }
}
