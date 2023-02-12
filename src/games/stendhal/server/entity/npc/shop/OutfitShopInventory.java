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

import games.stendhal.server.entity.Outfit;
import marauroa.common.Pair;


/**
 * Represents contents & prices of an outfit shop.
 */
public class OutfitShopInventory extends ShopInventory<String, Pair<String, Integer>> {

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
    put(name, new Pair<String, Integer>(outfit, price));
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
  public void put(final String name, final Outfit outfit, final int price) {
    put(name, outfit.toString(), price);
  }

  /**
   * Retrieves a string representation of an outfit sold by shop.
   *
   * @param name
   *     String identifier.
   * @return
   *     Outfit string or null if name not found.
   */
  public String getOutfitString(final String name) {
    if (containsKey(name)) {
      return get(name).first();
    }
    return null;
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
    final String ost = getOutfitString(name);
    if (ost != null) {
      return new Outfit(ost);
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
