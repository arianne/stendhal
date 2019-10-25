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
package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ados.rosshouse.FatherNPC;

/**
 * Susi's father during the Mine Town Revival Weeks
 */
public class DadNPC implements LoadableContent {
	private void createDadNPC() {
		final StendhalRPZone zone2 = SingletonRepository.getRPWorld().getZone("int_semos_frank_house");
		final SpeakerNPC npc2 = new SpeakerNPC("Mr Ross") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi there.");
				addJob("I'm on vacation now for the Mine Town Revival Weeks. But I still need to finish some work before my daughter #Susi and I can enjoy the party.");
				addHelp("My daughter Susi is all excited about the Mine Town Revival Weeks. But I am really concerned that something bad may happen to her again. So she will have to wait until I finish my work.");
				addReply("susi", "My daughter Susi is all excited about the Mine Town Revival Weeks. But I am really concerned that something bad may happen to her again. So she will have to wait until I finish my work.");
				addOffer("Sorry I do not have anything to offer you. I have to walk #Susi home to Ados once the party is over.");
				addQuest("Go meet my daughter #Susi, she'd love to make friends.");
				addGoodbye("Bye, nice to meet you.");
			}
		};

		npc2.setOutfit(1, 34, 7, null, 0, null, 27, null, 0);
		npc2.setPosition(21, 10);
		npc2.setDirection(Direction.LEFT);
		npc2.initHP(100);
		zone2.add(npc2);
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
		removeNPC("Mr Ross");
		createDadNPC();
	}


	/**
	 * removes Susi's father from the Mine Town and places him back into his home in Ados.
	 *
	 * @return <code>true</code>, if the content was removed, <code>false</code> otherwise
	 */
	@Override
	public boolean removeFromWorld() {
		removeNPC("Mr Ross");

		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("int_ados_ross_house");
		new FatherNPC().createDadNPC(zone);

		return true;
	}
}
