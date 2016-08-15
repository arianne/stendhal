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
import games.stendhal.server.entity.npc.action.ExamineChatAction;

/**
 * NPCs who creates photos
 */
public class PhotographerNPC implements LoadableContent {

	private void createNPC() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		final SpeakerNPC npc1 = new SpeakerNPC("Bibos") {

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
					new ExamineChatAction("https://stendhalgame.org/content/game/photo.php?outfit=7080202&i=8&h=c072c96d5d3dac664a743d8a2b5149ede2bad4f425174b8123a4639fb1f81da3", "title", "caption"));
			}
		};

		npc1.setPosition(90, 111);
		npc1.setEntityClass("youngsoldiernpc");
		npc1.setDirection(Direction.RIGHT);
		npc1.setDescription("You see Bibos. He creates pictures.");
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
		removeNPC("Bibos");
		return true;
	}
}
