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
package utilities;

import com.google.common.collect.ImmutableList;

import org.apache.log4j.Logger;

import games.stendhal.server.core.config.ShopGroupsXMLLoader.MerchantConfigurator;
import games.stendhal.server.entity.npc.shop.OutfitShopsList;
import games.stendhal.server.entity.npc.shop.ShopInventory;
import games.stendhal.server.entity.npc.shop.ShopsList;
import games.stendhal.server.entity.npc.shop.ShopType;


public class ShopHelper {

  private static final Logger logger = Logger.getLogger(ShopHelper.class);


  @SuppressWarnings("unchecked")
  private static void initShop(final String id, final ShopType stype) {
    ShopInventory inv;
    if (ShopType.OUTFIT.equals(stype)) {
      inv = OutfitShopsList.get().get(id);
    } else {
      inv = ShopsList.get().get(id, stype);
    }

    if (inv == null) {
      logger.error(stype.toString() + "er shop \"" + id + "\" not found");
      return;
    }

    for (final MerchantConfigurator mc: (ImmutableList<MerchantConfigurator>) inv.getMerchantConfigurators()) {
      mc.configure();
    }
  }

  public static void initSeller(final String id) {
    ShopHelper.initShop(id, ShopType.ITEM_SELL);
  }

  public static void initBuyer(final String id) {
    ShopHelper.initShop(id, ShopType.ITEM_BUY);
  }

  public static void initOutfitter(final String id) {
    ShopHelper.initShop(id, ShopType.OUTFIT);
  }
}
