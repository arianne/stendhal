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

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.AbstractQuest;

/**
 * sets the capture the flag quest up.
 *
 * @author hendrik, sjtsp
 */
public class CaptureFlagQuest extends AbstractQuest {

	/** name for the internal slot to store quest data */
	private static final String SLOT_NAME = "capture_the_flag";

	/** player visible name for the quest */
	private static final String QUEST_NAME = "CaptureTheFlag";

	private StendhalRPZone zone = null;

	@Override
	public String getSlotName() {
		return SLOT_NAME;
	}

	@Override
	public List<String> getHistory(Player player) {
		return new LinkedList<String>();
	}

	@Override
	public String getName() {
		return QUEST_NAME;
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

				addHelp("You can test CTF with other players.  One of you puts a #flag in your hand.  The others equips fumble arrows (and bow), and tags (left-clicks) the flag carrier, to make the carrier drop.  Note that attacking does not work - you have to left-click each time.");

				// TODO: count the number of *full* matches that player has participated in
				add(ConversationStates.ATTENDING,
					"play",
					new NotCondition(new PlayingCTFCondition()),
					ConversationStates.ATTENDING,
					"Ok, now other players can tag you, if you have the flag and they have special arrows.  Have fun.  Let me know if you need #help",
					new JoinCaptureFlagAction());
				add(ConversationStates.ATTENDING,
					"play",
					new PlayingCTFCondition(),
					ConversationStates.ATTENDING,
					"You are already playing.",
					null);

				add(ConversationStates.ATTENDING,
					"stop",
					new PlayingCTFCondition(),
					ConversationStates.ATTENDING,
					"Thanks for playing.  Come back again.  Please provide feedback in arianne IRC or the wiki",
					new LeaveCaptureFlagAction());
				add(ConversationStates.ATTENDING,
						"stop",
						new NotCondition(new PlayingCTFCondition()),
						ConversationStates.ATTENDING,
						"You are not playing right now.",
						null);

				add(ConversationStates.ATTENDING,
					"flag",
					new PlayingCTFCondition(),
					ConversationStates.ATTENDING,
					"Here you go.",
					new ProvideCTFFlagsAction());
				add(ConversationStates.ATTENDING,
						"flag",
						new NotCondition(new PlayingCTFCondition()),
						ConversationStates.ATTENDING,
						"You must #play to be able to receive a test flag.",
						null);

				// TODO: just use a compound action for all types of ammo
				add(ConversationStates.ATTENDING,
					"snowballs",
					new PlayingCTFCondition(),
					ConversationStates.ATTENDING,
					"Here you go.  Sorry all the arrows look the same right now.  You'll have to look at them to see which type they are.",
					new EquipItemAction("fumble arrow", 100));
				add(ConversationStates.ATTENDING,
						"snowballs",
						new NotCondition(new PlayingCTFCondition()),
						ConversationStates.ATTENDING,
						"You must #play to be able to get more arrows.",
						null);

				// TODO: just use a compound action for all types of ammo
				add(ConversationStates.ATTENDING,
					"snowballs",
					new PlayingCTFCondition(),
					ConversationStates.ATTENDING,
					"Here you go.  Sorry all the snowballs look the same right now.  You'll have to look at them to see which type they are.",
					new EquipItemAction("fumble snowball", 100));
				add(ConversationStates.ATTENDING,
						"snowballs",
						new NotCondition(new PlayingCTFCondition()),
						ConversationStates.ATTENDING,
						"You must #play to be able to get more arrows.",
						null);

				// TODO: remove from game, remove all ctf gear, ...
				// TODO: the cleanup needs to happen even if player logs out, or walks away (different code path)
				addGoodbye();
			}
		};

		npc.setEntityClass("oldheronpc");
		npc.setPosition(100, 119);
		npc.initHP(100);
		npc.setDescription("You see Thumb"); // TODO: Describe NPC
		zone.add(npc);
	}

	@Override
	public void addToWorld() {
		zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		addTeamManagerNPC();
	}

	@Override
	public String getNPCName() {
		return "Thumb";
	}

}
