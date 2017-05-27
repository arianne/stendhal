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
package games.stendhal.server.entity.creature;

import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

/**
 * A creature that will be attacked by normal Creatures.
 *
 * @author hendrik
 */
public class AttackableCreature extends Creature {

	/** name of player who summoned the attackable creature */
	private String master;

	/**
	 * Class Constructor.
	 *
	 * @param copy
	 */
	public AttackableCreature(final Creature copy) {
		super(copy);
	}

	@Override
	public List<RPEntity> getEnemyList() {
		List<RPEntity> res = this.getAttackingRPEntities();

		Player masterPlayer = SingletonRepository.getRuleProcessor().getPlayer(master);

		if (masterPlayer != null) {
			res.addAll(masterPlayer.getAttackingRPEntities());
		}
		return res;
	}

	/**
	 * Sets the master of this creature.
	 *
	 * @param master
	 *            master
	 */
	public void setMaster(final String master) {
		this.master = master;
	}

	@Override
	public Creature getNewInstance() {
		return new AttackableCreature(this);
	}

}
