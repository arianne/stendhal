/*
 * @(#) src/games/stendhal/server/entity/ShopSign.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.sign;

//
//

import games.stendhal.server.entity.npc.ShopList;

/**
 * A sign for a ShopList.
 */
public class ShopSign extends Sign {
	/**
	 * The shop list.
	 */
	protected ShopList shops = ShopList.get();


	/**
	 * Create a shop list sign.
	 *
	 * @param	name		The list name/mnemonic.
	 * @param	title		The sign title.
	 */
	public ShopSign(final String name, final String title) {
		setText(shops.toString(name, title));
	}
}
