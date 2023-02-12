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
package games.stendhal.server.entity.mapstuff.sign;

import java.util.Map;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.shop.OutfitShopInventory;
import games.stendhal.server.entity.npc.shop.OutfitShopsList;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ShowOutfitListEvent;
import marauroa.common.Pair;


/**
 * A shop sign representing contents of an outfit shop.
 */
public class OutfitShopSign extends ShopSign {

	private final OutfitShopsList oshops;

	private Map<String, String> addLayers;
	private final boolean showBase;
	private Map<String, String> hideOverrides;
	private Map<String, Map<String, Integer>> indexes;


	/**
	 * Creates a new outfit shop sign.
	 *
	 * @param name
	 *     Shop identifier.
	 * @param title
	 *     Text displayed as sign title.
	 * @param caption
	 *     Text displayed above inventory list.
	 * @param addLayers
	 *     Additional outfit layers to include in preview.
	 * @param showBase
	 *     Set to `true` to override hidden base layers (body, head, & eyes) for all outfits.
	 * @param hideOverrides
	 *     Manually override each hidden base layer (e.g. "showbody,showhead,showeyes" or "showall")
	 *     for each outfit.
	 * @param indexes
	 *     Override default display indexes (defaults: x=1 (center frame), y=2 (forward-facing)).
	 */
	public OutfitShopSign(final String name, final String title, final String caption,
			final Map<String, String> addLayers, final boolean showBase,
			final Map<String, String> hideOverrides, final Map<String, Map<String, Integer>> indexes) {
		super(name, title, caption, true);
		this.oshops = OutfitShopsList.get();
		this.addLayers = addLayers;
		this.showBase = showBase;
		this.hideOverrides = hideOverrides;
		this.indexes = indexes;
	}

	/**
	 * Creates a new outfit shop sign.
	 *
	 * @param name
	 *     Shop identifier.
	 * @param title
	 *     Text displayed as sign title.
	 * @param caption
	 *     Text displayed above inventory list.
	 * @param addLayers
	 *     Additional outfit layers to include in preview.
	 * @param showBase
	 *     Set to `true` to override hidden base layers (body, head, & eyes) for all outfits.
	 */
	public OutfitShopSign(final String name, final String title, final String caption,
			final Map<String, String> addLayers, final boolean showBase) {
		this(name, title, caption, addLayers, showBase, null, null);
	}

	/**
	 * Creates a new outfit shop sign.
	 *
	 * @param name
	 *     Shop identifier.
	 * @param title
	 *     Text displayed as sign title.
	 * @param caption
	 *     Text displayed above inventory list.
	 * @param addLayers
	 *     Additional outfit layers to include in preview.
	 * @param hideOverrides
	 *     Manually override each hidden base layer (e.g. "showbody,showhead,showeyes" or "showall")
	 *     for each outfit.
	 */
	public OutfitShopSign(final String name, final String title, final String caption,
			final Map<String, String> addLayers, final Map<String, String> hideOverrides) {
		this(name, title, caption, addLayers, false, hideOverrides, null);
	}

	/**
	 * Creates a new outfit shop sign.
	 *
	 * @param name
	 *     Shop identifier.
	 * @param title
	 *     Text displayed as sign title.
	 * @param caption
	 *     Text displayed above inventory list.
	 */
	public OutfitShopSign(final String name, final String title, final String caption) {
		this(name, title, caption, null, false, null, null);
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		if (!(user instanceof Player)) {
			return false;
		}

		final OutfitShopInventory inventory = oshops.get(shopName);
		if (inventory == null) {
			return false;
		}

		final StringBuilder info = new StringBuilder();
		for (final String label: inventory.keySet()) {
			final Pair<String, Integer> value = inventory.get(label);
			String outfitString = value.first();
			final int price = value.second();
			if (info.length() > 0) {
				info.append(":");
			}
			if (this.addLayers != null && this.addLayers.containsKey(label)) {
				outfitString += "," + this.addLayers.get(label);
			}
			info.append(label + ";" + outfitString + ";" + String.valueOf(price) + ";");
			if (this.hideOverrides != null && this.hideOverrides.containsKey(label)) {
				info.append(this.hideOverrides.get(label));
			}
			if (this.indexes != null && this.indexes.containsKey(label)) {
				final Map<String, Integer> idxOverrides = this.indexes.get(label);
				info.append(";");
				if (idxOverrides.containsKey("x")) {
					info.append(String.valueOf(idxOverrides.get("x")));
				}
				info.append(";");
				if (idxOverrides.containsKey("y")) {
					info.append(String.valueOf(idxOverrides.get("y")));
				}
			}
		}

		final ShowOutfitListEvent event = new ShowOutfitListEvent(title, caption, info.toString());
		if (showBase) {
			event.put("show_base", "");
		}
		user.addEvent(event);
		user.notifyWorldAboutChanges();
		return true;
	}
}
