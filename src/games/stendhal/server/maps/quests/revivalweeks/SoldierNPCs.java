/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Soldiers protecting the entrance to Semos Mine.
 */
public class SoldierNPCs implements LoadableContent {

	private void createNPC() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		final SpeakerNPC npc1 = new SpeakerNPC("Hibitus") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("...");
				addJob("I protect the festival from the evil creatures lurking deep inside the mines.");
				addGoodbye("Be careful.");
			}
		};

		npc1.setPosition(88, 98);
		npc1.setEntityClass("youngsoldiernpc");
		npc1.setDirection(Direction.RIGHT);
		npc1.setDescription("You see Hibitus. He guards the entrance to Semos Mine.");
		npc1.initHP(100);
		zone.add(npc1);

		final SpeakerNPC npc2 = new SpeakerNPC("Lucanus") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("...");
				addJob("I protect the festival from the evil creatures lurking deep inside the mines.");
				addGoodbye("Be careful.");
			}
		};

		npc2.setPosition(92, 98);
		npc2.setEntityClass("youngsoldiernpc");
		npc2.setDirection(Direction.LEFT);
		npc2.setDescription("You see Lucanus. He guards the entrance to Semos Mine.");
		npc2.initHP(100);
		zone.add(npc2);
	}


	/**
	 * removes an NPC from the world and NPC list
	 *
	 * @param name name of NPC
	 */
	private void removeNPC(String name) {
		SpeakerNPC npc = NPCList.get().get(name);
		if (npc == null) {
			return;
		}
		npc.getZone().remove(npc);
	}

	@Override
	public void addToWorld() {
		createNPC();
	}


	/**
	 * removes NPCs from the Mine Town
	 *
	 * @return <code>true</code>, if the content was removed, <code>false</code> otherwise
	 */
	@Override
	public boolean removeFromWorld() {
		removeNPC("Hibitus");
		removeNPC("Lucanus");
		return true;
	}
}
