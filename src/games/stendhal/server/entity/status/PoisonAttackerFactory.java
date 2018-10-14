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
package games.stendhal.server.entity.status;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.ConsumableItem;

public class PoisonAttackerFactory {
	private static Logger logger = Logger.getLogger(PoisonAttackerFactory.class);

	public static PoisonAttacker get(final String profile) {
		if (profile != null) {
			final String[] statusparams = profile.split(",");
			final ConsumableItem status = (ConsumableItem) SingletonRepository.getEntityManager().getItem(statusparams[1]);

			if (status == null) {
				logger .error("Cannot create status attacker with " + statusparams[1]);
				return null;
			}
			return new PoisonAttacker(Integer.parseInt(statusparams[0]), status);
		}
		return null;
	}
}
