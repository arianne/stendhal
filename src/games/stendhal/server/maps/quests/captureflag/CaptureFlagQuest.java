/***************************************************************************
 *                    (C) Copyright 2011 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.captureflag;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.AbstractQuest;

import java.util.List;

/**
 * sets the capture the flag quest up.
 *
 * @author hendrik, sjtsp
 */
public class CaptureFlagQuest extends AbstractQuest {
	private StendhalRPZone zone = null;

	@Override
	public String getSlotName() {
		return null;
	}

	@Override
	public List<String> getHistory(Player player) {
		return null;
	}

	@Override
	public String getName() {
		return "CaptureTheFlag";
	}

	private void addTeamManagerNPC() {
		final SpeakerNPC npc = new SpeakerNPC("Thumb") {

			@Override
			protected void createPath() {
				// don't move
			}

			@Override
			protected void createDialog() {
				addGreeting("Oh hello."); // TODO: better greeting
				// TODO: addJob("");
				// TODO: addHelp("");
				add(ConversationStates.ATTENDING,
						"play", null, ConversationStates.ATTENDING,
						"Have fun",
						new JoinCaptureFlagAction());
				addGoodbye();
			}
		};

		npc.setEntityClass("oldheronpc"); // TODO: different sprite
		npc.setPosition(100, 119);
		npc.initHP(100);
		npc.setDescription("You see Thumb"); // TODO: Describe NPC
		zone.add(npc);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		addTeamManagerNPC();
	}

}
