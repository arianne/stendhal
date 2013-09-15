/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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
import games.stendhal.server.entity.status.PoisonStatus;

/**
 * Poisoner
 */
class Poisoner implements Feeder {

	@Override
	public boolean feed(final ConsumableItem item, final Player player) {
		ConsumableItem splitOff = (ConsumableItem) item.splitOff(1);
		PoisonStatus status = new PoisonStatus(splitOff.getAmount(), splitOff.getFrecuency(), splitOff.getRegen());
		player.getStatusList().inflictStatus(status, splitOff);
		return true;
	}

}
