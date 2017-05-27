/***************************************************************************
 *                   (C) Copyright 2003-2015 - Stendhal                    *
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

import java.util.Arrays;
import java.util.List;

import games.stendhal.common.NotificationType;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.DrunkStatus;
import games.stendhal.server.entity.status.EatStatus;
import games.stendhal.server.entity.status.StatusType;

/**
 * eats food or drinks
 */
class Eater implements Feeder {
	private static final int COUNT_CHOKING_TO_DEATH = 8;
	private static final int COUNT_CHOKING = 5;
	private static final int COUNT_FULL = 4;

	@Override
	public boolean feed(final ConsumableItem item, final Player player) {
		int count = player.getStatusList().countStatusByType(StatusType.EATING);
		if (count > COUNT_CHOKING_TO_DEATH) {
			int playerHP = player.getHP();
			int chokingDamage = damage(2 * playerHP / 3);
			player.setHP(playerHP - chokingDamage);
			player.sendPrivateText(NotificationType.NEGATIVE, "You ate so much that you vomited on the ground and lost " + Integer.toString(chokingDamage) + " health points.");
			final Item sick = SingletonRepository.getEntityManager().getItem("vomit");
			player.getZone().add(sick);
			sick.setPosition(player.getX(), player.getY() + 1);
			player.getStatusList().removeAll(EatStatus.class);
			player.notifyWorldAboutChanges();
			return false;
		}

		if (count > COUNT_CHOKING) {
			// remove some HP so they know we are serious about this
			int playerHP = player.getHP();
			int chokingDamage = damage(playerHP / 3);
			player.setHP(playerHP - chokingDamage);
			player.sendPrivateText(NotificationType.NEGATIVE, "You eat so much at once that you choke on your food and lose " + Integer.toString(chokingDamage) + " health points. If you eat more you could be very sick.");
			player.notifyWorldAboutChanges();
		} else if (count > COUNT_FULL) {
			player.sendPrivateText("You are now full and shouldn't eat any more.");
		}

		ConsumableItem splitOff = (ConsumableItem) item.splitOff(1);
		EatStatus status = new EatStatus(splitOff.getAmount(), splitOff.getFrecuency(), splitOff.getRegen());
		player.getStatusList().inflictStatus(status, splitOff);

		List<String> alcoholicDrinks = Arrays.asList("beer", "pina colada", "wine", "strong koboldish torcibud", "vsop koboldish torcibud");
		if (alcoholicDrinks.contains(item.getName())) {
			DrunkStatus drunkStatus = new DrunkStatus();
			player.getStatusList().inflictStatus(drunkStatus, item);
		}
		return true;
	}

	/**
	 * Get damage done by overeating.
	 *
	 * @param maxDamage upper limit of damage
	 * @return random damage between 0 and maxDamage - 1
	 */
	private int damage(int maxDamage) {
		// Avoid calling rand(0)
		if (maxDamage > 0) {
			return Rand.rand(maxDamage);
		}
		return 0;
	}
}
