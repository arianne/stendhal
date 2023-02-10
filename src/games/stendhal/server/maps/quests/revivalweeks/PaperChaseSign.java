/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.maps.quests.PaperChase;

/**
 * Adds a hall of fame sign for the paper chase
 *
 * @author hendrik
 */
public class PaperChaseSign implements LoadableContent {
	private Sign sign;

	/**
	 * creates the hall of fame sign
	 */
	private void createHallOfFameSign() {
		sign = new Sign();
		sign.setPosition(69, 106);
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		zone.add(sign);
		updateQuest();
	}

	/**
	 * updates the PaperChase quest so that it points to the correct sign.
	 */
	private void updateQuest() {
		PaperChase paperChase = (PaperChase) StendhalQuestSystem.get().getQuest("PaperChase");
		if (paperChase != null) {
			paperChase.setSign(sign);
		}
	}

	/**
	 * adds the sign to the world
	 */
	@Override
	public void addToWorld() {
		createHallOfFameSign();
	}

	/**
	 * try to remove the content from the world-
	 *
	 * @return <code>true</code>
	 */
	@Override
	public boolean removeFromWorld() {
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		zone.remove(sign);
		updateQuest();
		return true;
	}
}
