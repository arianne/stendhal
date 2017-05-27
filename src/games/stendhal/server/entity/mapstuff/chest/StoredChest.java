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
package games.stendhal.server.entity.mapstuff.chest;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.transformer.ItemTransformer;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.slot.ChestSlot;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A Chest whose contents are stored by the zone.
 *
 * @author kymara
 */
public class StoredChest extends Chest {
	private static Logger logger = Logger.getLogger(StoredChest.class);
	private ChestListener chestListener;

	/**
	 * Creates a new StoredChest.
	 *
	 */
	public StoredChest() {
		super();
		store();
	}

	/**
	 * Creates a StoredChest based on an existing RPObject. This is just for
	 * loading a chest from the database, use the other constructors.
	 *
	 * @param rpobject
	 */
	public StoredChest(final RPObject rpobject) {
		super(rpobject);
		loadSlotContent();
		store();
		if (has("open")) {
			remove("open");
		}
	}

	@Override
	public void open() {
		if (chestListener == null) {
			chestListener = new ChestListener();
		}
		SingletonRepository.getTurnNotifier().notifyInSeconds(60, chestListener);
		logger.debug("Opening chest in zone " + getZone().getName() + " with " + getSlot("content").size() + " items.");
		super.open();
	}

	@Override
	public void close() {
		super.close();
		SingletonRepository.getTurnNotifier().dontNotify(chestListener);
		StendhalRPZone zone = this.getZone();
		if (zone != null) {
			logger.debug("Storing chest in zone " + zone.getName() + " with " + getSlot("content").size() + " items.");
			zone.storeToDatabase();
		} else {
			logger.error("Closing StoredChest which is in no zone.");
		}
	}

	private void loadSlotContent() {
		if (hasSlot("content")) {
			final RPSlot slot = getSlot("content");
			final List<RPObject> objects = new LinkedList<RPObject>();
			for (final RPObject objectInSlot : slot) {
				objects.add(objectInSlot);
			}
			slot.clear();
			removeSlot("content");

			final RPSlot newSlot = new ChestSlot(this);
			addSlot(newSlot);

			// Restore the stored items
			ItemTransformer transformer = new ItemTransformer();
			for (final RPObject rpobject : objects) {
				try {
					Item item = transformer.transform(rpobject);

					// log removed items
					if (item == null) {
						int quantity = 1;
						if (rpobject.has("quantity")) {
							quantity = rpobject.getInt("quantity");
						}
						logger.warn("Cannot restore " + quantity + " "
								+ rpobject.get("name") + " of stored chest "
								+ " because this item"
								+ " was removed from items.xml");
						continue;
					}

					newSlot.add(item);
				} catch (final Exception e) {
					logger.error("Error adding " + rpobject + " to stored chest slot", e);
				}
			}
		}
	}

	@Override
	public String getDescriptionName(final boolean definite) {
		return Grammar.article_noun("chest in " + this.getZone().getName(), definite);
	}

	/**
	 * Checks if it should close the chest
	 *
	 * @return <code>true</code> if it should be called again.
	 */
	protected boolean chestCloser() {

		if (getZone().getPlayers().size() > 0) {
			// do nothing - people are still in the zone
			return true;
		} else {
			// the zone is empty, close the chest
			close();
			notifyWorldAboutChanges();
		}
		return false;
	}

	/**
	 * A listener for closing the chest
	 */

	protected class ChestListener implements TurnListener {
		/**
		 * This method is called when the turn number is reached.
		 *
		 * @param currentTurn
		 *            The current turn number.
		 */
		@Override
		public void onTurnReached(final int currentTurn) {
			StendhalRPZone zone = getZone();
			if (zone != null) {
				logger.info("Storing chest in zone " + zone.getName() + " with " + getSlot("content").size() + " items while it's open.");
				zone.storeToDatabase();
			}
			if (chestCloser()) {
				SingletonRepository.getTurnNotifier().notifyInSeconds(60, this);
			}
		}
	}

	@Override
	public void onRemoved(final StendhalRPZone zone) {
		SingletonRepository.getTurnNotifier().dontNotify(chestListener);

		super.onRemoved(zone);
	}
}
