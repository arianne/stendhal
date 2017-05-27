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
package games.stendhal.server.entity.spell.effect;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnListenerDecorator;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;

/**
 * Effect for healing a player
 *
 * @author madmetzger
 */
public class HealingEffect extends AbstractEffect implements TurnListener {

	private static final Logger LOGGER = Logger.getLogger(HealingEffect.class);

	private int restAmount;

	private Player playerToHeal;

	/**
	 * Creates a new {@link HealingEffect}.
	 *
	 * @param nature
	 * @param amount
	 * @param atk
	 * @param def
	 * @param lifesteal
	 * @param rate
	 * @param regen
	 * @param modifier
	 */
	public HealingEffect(Nature nature, int amount, int atk, int def, double lifesteal, int rate,
			int regen, double modifier) {
		super(nature, amount, atk, def, lifesteal, rate, regen, modifier);
		this.restAmount = amount;
	}

	@Override
	public void act(Player caster, Entity target) {
		if (target instanceof Player) {
			actInternal(caster, (Player) target);
		} else {
			LOGGER.error("target is no instance of Player but: " + target, new Throwable());
		}
	}

	private void actInternal(Player caster, Player target) {
		playerToHeal = target;
		SingletonRepository.getTurnNotifier().notifyInTurns(0, new TurnListenerDecorator(this));
	}

	@Override
	public void onTurnReached(int currentTurn) {
		if(restAmount > 0) {
			int toHeal = Math.min(restAmount, getRegen());
			playerToHeal.heal(toHeal);
			restAmount = restAmount - toHeal;
			SingletonRepository.getTurnNotifier().notifyInTurns(getRate(), new TurnListenerDecorator(this));
		}
	}

}
