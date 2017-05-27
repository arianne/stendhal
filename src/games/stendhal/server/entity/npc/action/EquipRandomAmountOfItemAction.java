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
package games.stendhal.server.entity.npc.action;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Chooses and equips the specified item from a list
 */
@Dev(category=Category.ITEMS_OWNED, label="Item+")
public class EquipRandomAmountOfItemAction implements ChatAction {
	private static Logger logger = Logger.getLogger(EquipRandomAmountOfItemAction.class);

	private final String item;
	private final int min;
	private final int max;
	private final int increment;


	/**
	 * Creates a new EquipRandomAmountOfItemAction.<br/>
	 * Since stackable, min and max must be > 0.<br/>
	 * If min > max, min is treated like max and vice versa
	 *
	 * @param item
	 *           stackable item
	 * @param min
	 * 			 minimum quantity
	 * @param max
	 * 			 maximum quantity
	 */
	public EquipRandomAmountOfItemAction(final String item, final int min, final int max) {
		this(item, min, max, 1);
	}

	/**
	 * Creates a new EquipRandomItemAction.<br/>
	 * Since stackable, min and max must be > 0.<br/>
	 * If min > max, min is treated like max and vice versa
	 *
	 * @param item
	 *           stackable item
	 * @param min
	 * 			 lower bound
	 * @param max
	 * 			 upper bound
	 * @param multiplayer
	 * 			 ie, only return numbers multiples of X
	 */
	@Dev
	public EquipRandomAmountOfItemAction(final String item, final int min, final int max, @Dev(defaultValue="1") final int multiplayer) {
		this.item = checkNotNull(item);
		this.min = min;
		this.max = max;
		this.increment = multiplayer;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		if ( (increment <= 0) || ((increment > max) && ((increment > min)) ) ){
			logger.error("Increment value '" + increment + "' is invalid when max is '" + max + "'.", new Throwable());
		} else if ((min <= 0) || (max <= 0)){
			logger.error("Invalid min/max values '" + min + "', '" + max + "'.", new Throwable());
		} else{
			final String itemName = item;
			int attempt = Rand.randUniform(min, max);
			if (attempt % increment != 0){
				// if the number isn't a multiple of increment, round it to nearest
				attempt = (attempt / increment) * increment;
				if (attempt == 0){
					attempt = Math.min(min, max);
				}
			}
			final Integer amount = attempt;
			new EquipItemAction(itemName, amount, false).fire(player, null, null);
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("equip stackable item: ");
		sb.append(item);
		sb.append(" in random quantity range <");
		sb.append(min);
		sb.append(", ");
		sb.append(max);
		sb.append(">");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return 5179 * item.hashCode() + min * max * increment;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EquipRandomAmountOfItemAction other = (EquipRandomAmountOfItemAction) obj;
		if (item == null) {
			if (other.item != null) {
				return false;
			}
		} else if (!item.equals(other.item)) {
			return false;
		}
		if ((min != other.min) || (max != other.max) || (increment != other.increment)){
			return false;
		}
		return true;
	}

}
