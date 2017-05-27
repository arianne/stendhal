/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item.consumption;

import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

public interface Feeder {

	/**
	 *
	 * @param item
	 * @param player
	 * @return true iff feeding is possible
	 */
	boolean feed(ConsumableItem item, Player player);

}
