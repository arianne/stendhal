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

import java.util.LinkedHashMap;


/**
 * Represents contents & prices of a shop.
 */
public abstract class ShopInventory<T, V> extends LinkedHashMap<String, V> {

  /**
   * Retrieves the price of an item sold by a shop.
   *
   * @param name
   *     Name or identifier of item sold.
   * @return
   *     Amount of money required to buy item.
   */
  public abstract Integer getPrice(final String name);

}
