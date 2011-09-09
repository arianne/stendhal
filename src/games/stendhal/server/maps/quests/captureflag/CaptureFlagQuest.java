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
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.AbstractQuest;

import games.stendhal.server.maps.quests.captureflag.JoinCaptureFlagAction;
import games.stendhal.server.maps.quests.captureflag.LeaveCaptureFlagAction;
import games.stendhal.server.maps.quests.captureflag.ProvideCTFFlagsAction;

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

				addGreeting("Hi, thanks for helping out with Capture the Flag (CTF) testing.  You can #play, #stop, and request #flag and #arrows.");

				addJob("We are helping to test ideas to make CTF fun.");
				
				addHelp("You can test CTF with another player.  One of you puts a #flag in your hand.  The other equips a bow and fumble arrows, and tags (left-clicks) the flag carrier, to make the carrier drop.");
				
				add(ConversationStates.ATTENDING,
					"play",
					null,
					ConversationStates.ATTENDING,
					"Ok, now other players can tag you, if you have the flag and they have special arrows.  Have fun.",
					new JoinCaptureFlagAction());

				// TODO: if player has not started, say something
				// TODO: if player has arrows/flag, remove them?
				add(ConversationStates.ATTENDING,
					"stop",
					null, 
					ConversationStates.ATTENDING,
					"Thanks for playing.  Come back again.  Please provide feedback in arianne IRC or the wiki",
					new LeaveCaptureFlagAction());

				// XXX need a condition to check that player has joined game
				add(ConversationStates.ATTENDING,
					"flag",
					null,
					ConversationStates.ATTENDING,
					"Here you go.",
					new ProvideCTFFlagsAction());

				// XXX need a condition to check that player has joined game
				add(ConversationStates.ATTENDING,
					"arrows",
					null,
					ConversationStates.ATTENDING,
					"Here you go.  Sorry the arrows look the same right now.  You'll have to look at them to see which type they are.",
					new ProvideArrowsAction());
				
				
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
