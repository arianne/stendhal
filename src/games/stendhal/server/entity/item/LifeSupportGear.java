/***************************************************************************
 *                   (C) Copyright 2015 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.GlobalVisualEffectEvent;
import marauroa.common.game.RPObject;

/**
 * an item to survive in some environments (e. g. scuba gear)
 */
public class LifeSupportGear extends Item {

	/**
	 * copy constructor
	 *
	 * @param item item to copy
	 */
	public LifeSupportGear(LifeSupportGear item) {
		super(item);
	}

	/**
	 * creates a new item
	 *
	 * @param name item name
	 * @param clazz item class
	 * @param subclass item subclass
	 * @param attributes attributes
	 */
	public LifeSupportGear(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	@Override
	public boolean onUnequipped() {
		RPObject entity = this.getBaseContainer();
		if (entity instanceof Player) {
			if (isBadSituation((Player) entity)) {
				handleCriticalUnequipped((Player) entity);
			}
		}
		return super.onUnequipped();
	}

	private boolean isBadSituation(final Player player) {
		// was the item removed from the armor slot?
		Item armor = player.getArmor();
		if (armor != this) {
			return false;
		}
		// does the zone require life support?
		String requireLifeSupport = player.getZone().getAttributes().get("life_support_environment");
		if (requireLifeSupport == null) {
			return false;
		}
		// does this item offer required life support
		if (!requireLifeSupport.equals(this.get("life_support"))) {
			return false;
		}

		return true;
	}

	private void handleCriticalUnequipped(final Player player) {
		final String[] target = player.getZone().getAttributes().get("life_support_failure_location").split("[,; ]+");
		player.addEvent(new GlobalVisualEffectEvent("blacken", 1000));
		player.sendPrivateText("You cannot breath, the world around you turns black.");
		TurnNotifier.get().notifyInTurns(4, new TurnListener() {

			@Override
			public void onTurnReached(int currentTurn) {
				StendhalRPZone zone = StendhalRPWorld.get().getZone(target[0].trim());
				player.teleport(zone, MathHelper.parseInt(target[1]), MathHelper.parseInt(target[2]), Direction.DOWN, player);

				int hp = player.getHP();
				player.setHP(Math.min(hp, 50));
				player.sendPrivateText("You recover slowly, wondering where you are.");
			}
		});
	}

	@Override
	public int getDefense() {
		StendhalRPZone zone = ((RPEntity) this.getBaseContainer()).getZone();
		if (zone == null) {
			return super.getDefense();
		}
		String requireLifeSupport = zone.getAttributes().get("life_support_environment");
		if (requireLifeSupport == null) {
			return 0;
		}
		if (!requireLifeSupport.equals(this.get("life_support"))) {
			return 0;
		}
		return super.getDefense();
	}
}
