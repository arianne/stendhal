/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.creature.impl.attack;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import marauroa.common.Pair;

/**
 * Strategy that combines different sub-strategies for attacking, targeting and
 * positioning to a complete fighting strategy.
 */
class CompoundAttackStrategy implements AttackStrategy {
	private final TargetSelectionStrategy targeter;
	private final PositioningStrategy positioner;
	private final AttackStrategy base;

	/**
	 * Create a new strategy.
	 *
	 * @param params String describing the sub-strategies. It should be a comma
	 *	separated list of strategy names of the form "attack,target,position".
	 *	Parameters to the sub-strategies can be passed in parentheses. Example:
	 *	"archer(5),attack weakest,dual attack", will create a strategy of using
	 *	range 5 archer for attacking, preferring the weakest opponent next to
	 *	the creature, and using "dual attack" profile for positioning.
	 *
	 * @return compound strategy
	 */
	static AttackStrategy create(String params) {
		String[] arg = params.split(",", -1);
		if (arg.length != 3) {
			throw new IllegalArgumentException("Invalid compound description: '"
					+ params + "'");
		}
		Pair<String, String> desc = parseStrategy(arg[1]);
		TargetSelectionStrategy targeter = TargetSelectionStrategyFactory.get(desc.first(), desc.second());
		desc = parseStrategy(arg[2]);
		PositioningStrategy positioner = PositioningStrategyFactory.get(desc.first(), desc.second());
		return new CompoundAttackStrategy(getSubStrategy(arg[0]),
				targeter, positioner);
	}

	/**
	 * Extract strategy name and an optional parameter from a strategy
	 * description.
	 *
	 * @param desc strategy description
	 *
	 * @return a pair where the first object is the strategy name, and second is
	 * 	the parameter to that strategy, or empty string if none is provided.
	 */
	private static Pair<String, String> parseStrategy(String desc) {
		String[] arg = desc.split("\\(|\\)");
		Pair<String, String> rval = new Pair<String, String>(arg[0], "");
		if (arg.length > 1) {
			rval.setSecond(arg[1]);
		}
		return rval;
	}

	/**
	 * Get a sub-strategy corresponding to a strategy description string.
	 *
	 * @param desc description
	 * @return strategy
	 */
	private static AttackStrategy getSubStrategy(String desc) {
		Pair<String, String> args = parseStrategy(desc);
		Map<String, String> map = new HashMap<String, String>();
		map.put(args.first(), args.second());

		return AttackStrategyFactory.get(map);
	}

	/**
	 * Create a new CompoundAttackStrategy.
	 *
	 * @param base strategy used for attacking
	 * @param targeter strategy used for choosing the target
	 * @param positioner strategy used for choosing the attacking position
	 */
	private CompoundAttackStrategy(AttackStrategy base, TargetSelectionStrategy targeter, PositioningStrategy positioner) {
		this.base = base;
		this.targeter = targeter;
		this.positioner = positioner;
	}

	@Override
	public void attack(Creature creature) {
		base.attack(creature);
	}

	@Override
	public boolean canAttackNow(Creature creature) {
		return base.canAttackNow(creature);
	}

	@Override
	public void findNewTarget(Creature creature) {
		targeter.findNewTarget(creature);
	}

	@Override
	public void getBetterAttackPosition(Creature creature) {
		positioner.getBetterAttackPosition(creature);
	}

	@Override
	public int getRange() {
		return base.getRange();
	}

	@Override
	public boolean hasValidTarget(Creature creature) {
		return targeter.hasValidTarget(creature);
	}

	@Override
	public boolean canAttackNow(Creature attacker, RPEntity target) {
		return base.canAttackNow(attacker, target);
	}
}
