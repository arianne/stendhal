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
package games.stendhal.server.maps.nalwor.hell;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.NotificationType;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.player.Player;

/**
 * Builds the 2nd reaper in hell.
 * @author kymara
 */
public class Reaper2NPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		SpeakerNPC npc = createNPC("repaeR mirG");
		npc.setPosition(68, 76);
		zone.add(npc);
	}

	static SpeakerNPC createNPC(String name) {
		final SpeakerNPC npc = new SpeakerNPC(name) {

			@Override
			protected void createPath() {
				// doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("#elddir a evlos tsum uoy ecalp siht #evael ot kees uoy fI");
				add(ConversationStates.ATTENDING, "evael", null, ConversationStates.QUESTION_1, "?erus uoy erA .truh lliw tI", null);
				final List<ChatAction> processStep = new LinkedList<ChatAction>();
				processStep.add(new TeleportAction("int_afterlife", 31, 23, Direction.UP));
				processStep.add(new DecreaseKarmaAction(100.0));
				processStep.add(new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						player.subXP(10000);
						// wipe riddle slot if player decided they couldnt answer it and would take the karma hit
						if (player.hasQuest("solve_riddles")) {
							player.removeQuest("solve_riddles");
						}
						player.sendPrivateText(NotificationType.NEGATIVE, "The Reaper took 10000 XP and gave you bad karma.");
					}
				});
				add(ConversationStates.QUESTION_1, Arrays.asList("yes", "sey", "ok", "ko"), null, ConversationStates.IDLE, "!ahahahaH", new MultipleActions(processStep));
				add(ConversationStates.QUESTION_1, Arrays.asList(ConversationPhrases.NO_EXPRESSION, "on"), null, ConversationStates.ATTENDING, ".eniF", null);
				addReply("elddir", ".rorrim ym ksA");
				addJob(".gnivil eht fo sluos eht tsevrah I");
				addHelp("#evael ot hsiw uoy dluohs ,lleh fo setag eht ot syek eht dloh I");
				addOffer("... luos ruoy ekat ot em hsiw uoy sselnU");
				addGoodbye("... yawa dessap sah sgniht fo redro dlo ehT");
			}
		};
		npc.setEntityClass("grim_reaper2_npc");
		npc.setPosition(68, 76);
		npc.initHP(100);
		npc.setDescription("You see the repaeR mirG. His mirror will give you liberty.");
		return npc;
	}
}
