/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.creature;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Killer;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

/**
 * An ItemGuardCreature is a creature that is responsible for guarding a special
 * item (e.g. a key). Once it is killed, a copy of this special item is given to
 * the player who killed it.
 *
 * If a quest is specified then the player only gets the item if the quest isn't completed.
 * If a queststate (and optionally, index) is also specified then the player only gets the item
 * if the quest is in that state
 */
public class ItemGuardCreature extends Creature {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(ItemGuardCreature.class);

	/** which Item to drop */
	private final String itemType;

	/** optional Item info string */
	private final String itemInfostring;

	/** optional Item description string */
	private final String itemDescr;

	/** which quest slot to check */
	private final String questSlot;

	/** which quest state to compare to check */
	private final String questState;

	/** which index of the quest state to check */
	private final int questIndex;

	/**
	 * Creates an ItemGuardCreature.
	 * @param copy
	 *            base creature
	 * @param itemType
	 *            the quest item to drop on death
	 */
	public ItemGuardCreature(final Creature copy, final String itemType) {
		this(copy, itemType, null, null, 0);
	}

	/**
	 * Creates an ItemGuardCreature.
	 *
	 * @param copy
	 *            base creature
	 * @param itemType
	 *            the quest item to drop on death
	 * @param questSlot
	 *            the quest slot for the active quest
	 * @param questState
	 * 			  the state of the quest to check on dead for
	 */
	public ItemGuardCreature(final Creature copy, final String itemType, final String questSlot, final String questState) {
		this(copy, itemType, questSlot, questState, 0);
	}

	/**
	 * Creates an ItemGuardCreature.
	 *
	 * @param copy
	 *            base creature
	 * @param itemType
	 *            the quest item to drop on death
	 * @param questSlot
	 *            the quest slot for the active quest
	 * @param questState
	 * 			  the state of the quest to check on dead for
	 * @param questIndex
	 * 			  the index of the quest slot to look in
	 */
	public ItemGuardCreature(final Creature copy, final String itemType, final String questSlot, final String questState, final int questIndex) {
		this(copy, itemType, null, null, questSlot, questState, questIndex);
	}

	/**
	 * Creates an ItemGuardCreature.
	 *
	 * @param copy
	 *            base creature
	 * @param itemType
	 *            the quest item to drop on death
	 * @param itemInfostring
	 *            optional info string to add to item
	 * @param itemDescr
	 *            optional description string to add to item
	 * @param questSlot
	 *            the quest slot for the active quest
	 * @param questState
	 * 			  the state of the quest to check on dead for
	 * @param questIndex
	 * 			  the index of the quest slot to look in
	 */
	public ItemGuardCreature(final Creature copy, final String itemType, final String itemInfostring, final String itemDescr,
			final String questSlot, final String questState, final int questIndex) {
		super(copy);

		this.itemType = itemType;
		this.itemInfostring = itemInfostring;
		this.itemDescr = itemDescr;
		this.questSlot = questSlot;
		this.questState = questState;
		this.questIndex = questIndex;

		noises = new LinkedHashMap<String, LinkedList<String>>(noises);
		final LinkedList<String> ll = new LinkedList<String>();
		ll.add("Thou shall not obtain the " + itemType + "!");
		// add to all states except death - in death player will get itemType.
	    noises.put("idle", ll);
	    noises.put("fight", ll);
	    noises.put("follow", ll);

		if (!SingletonRepository.getEntityManager().isItem(
				itemType)) {
			logger.error(copy.getName() + " drops unexisting item " + itemType);
		}
	}

	@Override
	public Creature getNewInstance() {
		return new ItemGuardCreature(this, itemType, itemInfostring, itemDescr, questSlot, questState, questIndex);
	}

	@Override
	public void onDead(final Killer killer, final boolean remove) {
		if (killer instanceof Player) {
			final Player killerPlayer = (Player) killer;
			boolean playerEquipped;

			if (itemInfostring != null) {
				playerEquipped = killerPlayer.isEquippedWithInfostring(itemType, itemInfostring);
			} else {
				playerEquipped = killerPlayer.isEquipped(itemType);
			}

			if (!playerEquipped) {
				if ((questSlot == null) || !killerPlayer.isQuestCompleted(questSlot)) {
					if(questState != null) {
						if (killerPlayer.isQuestInState(questSlot, questIndex, questState)) {
							equipPlayerWithGuardedItem(killerPlayer);
						}
					} else {
						equipPlayerWithGuardedItem(killerPlayer);
					}
				}
			}
		}
		super.onDead(killer, remove);
	}

	private void equipPlayerWithGuardedItem(final Player killerPlayer) {
		final Item item = SingletonRepository.getEntityManager().getItem(itemType);

		if (itemInfostring != null) {
			item.put("infostring", itemInfostring);
		}
		if (itemDescr != null) {
			item.put("description", itemDescr);
		}

		item.setBoundTo(killerPlayer.getName());
		killerPlayer.equipOrPutOnGround(item);
	}
}
