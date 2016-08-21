/***************************************************************************
 *                   (C) Copyright 2016 - Faiumoni e. V.                   *
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
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * NPCs who creates photos
 */
public class PhotographerNPC implements LoadableContent {

	private void createNPC() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		final SpeakerNPC npc1 = new SpeakerNPC("Kirla") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("#Pictures! Good #Pictures! Memory #Pictures! Buy #Pictures!");
				addJob("I create #pictures from your memories.");
				addGoodbye("Take care.");

				add(ConversationStates.ATTENDING,
					"picture",
					null,
					ConversationStates.ATTENDING,
					"Ohmmmmm, I see blury mist, Ohmmmmm. The picture is getting clearer, Ohmmmmm. Just a view more seconds...",
					new PhotographerChatAction(zone));
			}
		};

		npc1.setPosition(68, 119);
		npc1.setEntityClass("photographernpc");
		npc1.setDirection(Direction.DOWN);
		npc1.setDescription("You see Kirla. She creates pictures.");
		npc1.initHP(100);
		zone.add(npc1);
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
	 * removes NPC from the Mine Town
	 *
	 * @return <code>true</code>, if the content was removed, <code>false</code> otherwise
	 */
	@Override
	public boolean removeFromWorld() {
		removeNPC("Kirla");
		return true;
	}
}
