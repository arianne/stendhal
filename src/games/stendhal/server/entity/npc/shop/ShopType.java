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


public enum ShopType {
	ITEM_BUY("buy"),
	ITEM_SELL("sell"),
	TRADE("trade"),
	OUTFIT("outfit");

	private final String value;

	private ShopType(final String value) {
		this.value = value;
	}

	public String toString() {
		return this.value;
	}

	public static ShopType fromString(final String s) {
		for (final ShopType t: values()) {
			if (t.value.equals(s)) {
				return t;
			}
		}
		return null;
	}
}
