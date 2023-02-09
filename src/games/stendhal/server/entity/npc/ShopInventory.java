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

import java.util.List;
import java.util.TreeMap;


/**
 * Represents contents & prices of a shop.
 */
public abstract class ShopInventory<String, V> extends TreeMap<String, V> {

  /**
   * Retrieves the price of an item sold by a shop.
   *
   * @param name
   *     Name or identifier of item sold.
   * @return
   *     Amount of money required to buy item.
   */
  public abstract Integer getPrice(final String name);

  /**
   * Retrieves list of names or identifiers of items sold by a shop.
   *
   * @return
   *    Item list.
   */
  public List<String> getItemList() {
    return (List<String>) keySet();
  }
}
