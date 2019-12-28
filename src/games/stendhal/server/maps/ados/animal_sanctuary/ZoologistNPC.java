/***************************************************************************
 *                   (C) Copyright 2019 - Stendhal                         *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.ados.animal_sanctuary;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.NotificationType;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.NPCEmoteAction;
import games.stendhal.server.entity.npc.action.NPCSetDirection;
import games.stendhal.server.entity.npc.action.PlaySoundAction;
import games.stendhal.server.entity.npc.action.SendPrivateMessageAction;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;

/**
 * NPC used in the Antivenom Ring quest that can extract <code>cobra venom</code>
 * from a <code>venom gland</code>.
 *
 * @author AntumDeluge
 */
public class ZoologistNPC implements ZoneConfigurator {

	/**
	 * Configure the NPC in a zone.
	 */
	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		/**
		 * Create & configure the NPC instance.
		 */
		final SpeakerNPC npc = new SpeakerNPC("Zoey") {
			/**
			 * Configure how NPC will converse with player.
			 */
			@Override
			protected void createDialog() {
				// Too busy to interact.
				add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new OrCondition(
							new NotCondition(new QuestActiveCondition("antivenom_ring")),
							new QuestStateStartsWithCondition("antivenom_ring", "mixing")),
					ConversationStates.IDLE,
					null,
					new MultipleActions(
						new NPCEmoteAction("yawns", false),
						new PlaySoundAction("yawn-female-1"),
						new SendPrivateMessageAction(NotificationType.NORMAL, "She is much too busy to be bothered at the moment."),
						new NPCSetDirection(Direction.UP)));

				addGoodbye();
			}

			/**
			 * Force NPC to face north after speaking with players.
			 */
			@Override
			public void setAttending(final RPEntity rpentity) {
				super.setAttending(rpentity);
				if (rpentity == null) {
					setDirection(Direction.UP);
				}
			}
		};

		npc.setEntityClass("zoologistnpc");
		npc.setPosition(27, 3);
		npc.setDirection(Direction.UP);
		npc.setDescription("You see " + npc.getName() + ", a dedicated zoologist.");
		zone.add(npc);
	}
}
