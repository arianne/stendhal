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
package games.stendhal.server.entity.npc.behaviour.journal;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.behaviour.impl.HealerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.RepairerBehaviour;
import marauroa.common.Pair;

public class ServicersRegister {

	/** The singleton instance. */
	private static ServicersRegister instance;

	private final List<Pair<String, HealerBehaviour>> healers;
	private final List<Pair<String, OutfitChangerBehaviour>> outfitChangers;
	private final List<Pair<String, RepairerBehaviour>> repairers;


	/**
	 * Singleton access method.
	 *
	 * @return
	 *     The static instance.
	 */
	public static ServicersRegister get() {
		if (instance == null) {
			instance = new ServicersRegister();
		}

		return instance;
	}

	protected ServicersRegister() {
		instance = this;
		healers  = new LinkedList<Pair<String, HealerBehaviour>>();
		outfitChangers  = new LinkedList<Pair<String, OutfitChangerBehaviour>>();
		repairers  = new LinkedList<Pair<String, RepairerBehaviour>>();
	}

	/**
	 * Adds an NPC to the NPCList. Does nothing if an NPC with the same name
	 * already exists. This makes sure that each NPC can be uniquely identified
	 * by his/her name.
	 *
	 * @param npcName
	 *            The NPC that should be added
	 * @param behaviour
	 *            The ServicersBehaviour of that NPC
	 */
	public void add(final String npcName, final HealerBehaviour behaviour) {
		Pair<String, HealerBehaviour> pair = new Pair<String, HealerBehaviour>(npcName, behaviour);
		healers.add(pair);
	}

	public void add(final String npcName, final OutfitChangerBehaviour behaviour) {
		Pair<String, OutfitChangerBehaviour> pair = new Pair<String, OutfitChangerBehaviour>(npcName, behaviour);
		outfitChangers.add(pair);
	}

	public void add(final String npcName, final RepairerBehaviour behaviour) {
		Pair<String, RepairerBehaviour> pair = new Pair<String, RepairerBehaviour>(npcName, behaviour);
		repairers.add(pair);
	}

	public List<Pair<String, HealerBehaviour>> getHealers() {
		return healers;
	}

	public List<Pair<String, OutfitChangerBehaviour>> getOutfitChangers() {
		return outfitChangers;
	}

	public List<Pair<String, RepairerBehaviour>> getRepairers() {
		return repairers;
	}

}
