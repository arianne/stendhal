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
package games.stendhal.server.entity.creature.impl.poison;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.ConsumableItem;

import org.apache.log4j.Logger;

public class PoisonerFactory {
	static final Attacker nonpoisonous = new NonPoisoner();
	private static Logger logger = Logger.getLogger(PoisonerFactory.class);

	public static Attacker get(final String profile) {
		if (profile != null) {
			final String[] poisonparams = profile.split(",");
			final ConsumableItem poison = (ConsumableItem) SingletonRepository.getEntityManager().getItem(poisonparams[1]);

			if (poison == null) {
				logger .error("Cannot create poisoner with " + poisonparams[1]);
				return nonpoisonous;
			}
			return new Poisoner(Integer.parseInt(poisonparams[0]), poison);
		}
		return nonpoisonous;
	}
}