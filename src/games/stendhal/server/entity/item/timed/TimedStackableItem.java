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
package games.stendhal.server.entity.item.timed;

import java.lang.ref.WeakReference;
import java.util.Map;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject;

/**
 * Abstract base class for a stackable timed item. Extend this class and
 * implement methods useItem(Player) and itemFinished(Player).
 *
 * @author johnnnny
 */
public abstract class TimedStackableItem extends StackableItem {

	private static Logger logger = Log4J.getLogger(TimedStackableItem.class);

	private WeakReference<Player> player;

	/**
	 * Creates a TimedItem.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public TimedStackableItem(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public TimedStackableItem(final TimedStackableItem item) {
		super(item);
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		boolean result = false;

		if (user instanceof Player) {
			final Player userplayer = (Player) user;

			RPObject base = getBaseContainer();

			if (user.nextTo((Entity) base)) {
				if (useItem(userplayer)) {
					/* set the timer for the duration */
					final TurnNotifier notifier = SingletonRepository.getTurnNotifier();
					notifier.notifyInTurns(getAmount(), this);
					player = new WeakReference<Player>(userplayer);
					this.removeOne();
					user.notifyWorldAboutChanges();
				}
				result = true;
			} else {
				user.sendPrivateText(getTitle() + " is too far away");
				logger.debug(getTitle() + " is too far away");
			}
		} else {
			logger.error("user is no instance of Player but: " + user, new Throwable());
		}

		return result;
	}

	@Override
	public void onTurnReached(final int currentTurn) {
		itemFinished(player.get());
	}

	@Override
	public String describe() {
		String text = "You see " + Grammar.a_noun(getTitle()) + ".";
		if (hasDescription()) {
			text = getDescription();
		}

		final String boundTo = getBoundTo();

		if (isBound()) {
			text = text + " It is a special reward for " + boundTo
					+ ", and cannot be used by others.";
		}

		return text;
	}

	/**
	 * Get the length of the timed event in turns.
	 *
	 * @return length in turns
	 */
	public int getAmount() {
		return getInt("amount");
	}

	/**
	 * Called when the player uses the item. Implement this in a subclass.
	 *
	 * @param player
	 * @return true if the usage is successful
	 */
	public abstract boolean useItem(Player player);

	/**
	 * Called when the used item is finished. Implement this in a subclass.
	 *
	 * @param player
	 */
	public abstract void itemFinished(Player player);

}
