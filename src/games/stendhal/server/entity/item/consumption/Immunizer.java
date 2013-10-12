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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.StatusType;

class Immunizer implements Feeder {

	@Override
	public boolean feed(final ConsumableItem item, final Player player) {
		player.getStatusList().setImmune(StatusType.POISONED);
		
		// set a timer to remove the immunity effect after some time
		final TurnNotifier notifier = SingletonRepository.getTurnNotifier();
		// first remove all effects from previously used immunities to
		// restart the timer
		
		final TurnListener tl = new AntidoteEater(player);
		notifier.dontNotify(tl);
		notifier.notifyInTurns(item.getAmount(), tl);
		item.removeOne();
		
		return true;
		
	}

}
