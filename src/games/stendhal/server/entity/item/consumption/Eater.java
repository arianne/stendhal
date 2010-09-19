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

import games.stendhal.common.Grammar;
import games.stendhal.common.NotificationType;
import games.stendhal.common.Rand;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

class Eater implements Feeder {

	public boolean feed(final ConsumableItem item, final Player player) {
		if (player.isChokingToDeath()) {
			// kill the player!
			player.setHP(0);
			player.onDead(item);
			player.sendPrivateText(NotificationType.NEGATIVE, "You choked to death on " + Grammar.a_noun(item.getName()) + ".");
			player.notifyWorldAboutChanges();
			return false;
		}
		
		if (player.isChoking()) {
			// remove some HP so they know we are serious about this
			int playerHP = player.getHP();
			int chokingDamage = Rand.rand(playerHP / 3);
			player.setHP(playerHP - chokingDamage);
			player.sendPrivateText(NotificationType.NEGATIVE, "You eat so much at once that you choke on your food and lose " + Integer.toString(chokingDamage) + " health points. If you eat more you could choke to death.");
			player.notifyWorldAboutChanges();
		} else if (player.isFull()) {
			player.sendPrivateText("You are now full and shouldn't eat any more.");
		} 
		player.eat((ConsumableItem) item.splitOff(1));
		return true;
	}

}
