/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityoutside;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTextAction;

/**
 * The cashier stands near the entrance to the museum. One has to talk to him and pay the fee to enter the museum.
 * The idea is, that Iker needs more pocket money and so came up with the idea of turning the old empty building
 * into the museum he heart of in a joke to take gullible tourists for a ride. So he is the manager but cleverly
 * denies being it to dodge responsibility.
 *
 * The taking of the fee is in MuseumEntranceFee.java and the door condition in deniran.xml
 *
 * An addition could be to make him attackable, and when a player does so, the father appears next to the player,
 * knocking the player out. The player would come back to consciousness outside of the city, with the HP halved
 * and some money and items like food missing, not rings, armor, or weapons.
 *
 * Iker is a Basque name meaning visit, visitation
 *
 * @author kribbel
 */

public class MuseumCashierNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildMuseumCashierNPC(zone);
	}
	private void buildMuseumCashierNPC(final StendhalRPZone zone) {
		final SpeakerNPC museumcashier = new SpeakerNPC("Iker") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE,ConversationPhrases.GREETING_MESSAGES,ConversationStates.ATTENDING,null,
					new MultipleActions(
						new SayTextAction("!me greats you in an over-polite vein."),
						new SayTextAction("Hello, I hope I can interest you in a #visit of the famous Deniran Air And Space Museum.")
					)
				);
				addHelp("Please pay the entrance fee first, then you are allowed to walk through the door to #visit the famous Deniran Air And Space Museum.");
				addJob("I am very proud to be the cashier of the famous Deniran Air And Space Museum.");
				addOffer("I'm allowed to grant you access to the famous Deniran Air And Space Museum, if you like to pay a #visit.");
				add(ConversationStates.ATTENDING,
					Arrays.asList("refund","fraud","hoax","scam","swindle","rip-of","swizz","screw","screwed","diddle","diddled"),
					ConversationStates.IDLE,
					null,
					new MultipleActions(
						new SayTextAction("It saddens us that you are not satisfied with our service."),
						new SayTextAction("!me wears a serious expression."),
						new SayTextAction("No refund!"),
						new SayTextAction("!me wears a very friendly expression again."),
						new SayTextAction("Please visit us again!")
				)
				);
				add(ConversationStates.ATTENDING,
					Arrays.asList("parents","father","dad","mother","mom", "guardian"),
					ConversationStates.ATTENDING,
					"Talking to my parents won't help you. You need to talk to the #director.",
					null
				);
				addReply("manager","I'm very sorry, the manager is currently unavailable.");
				addReply("director","I'm very sorry, the director is currently unavailable.");
				addReply("curator","I'm very sorry, the curator is currently unavailable.");
				addGoodbye("Please honour us with your visit again.");
			}

			//Doesn't let the NPC face DOWN when it ends the conversation by itself by going IDLE but this would be preferred
			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}

			@Override
			public void onRejectedAttackStart(RPEntity attacker) {
				say("!me screams.");
				say("Dad! Dad! Help! I am attacked by " + attacker.getName() + "!");
			}

		};

		// I took this as a placeholder, it's the ghost in Ados. A boy clad in something more formal would be better.
		museumcashier.setEntityClass("kid7npc");
		museumcashier.setDescription("You see Iker. He's a clever and business-minded young man.");
		museumcashier.setPosition(24, 47);
		museumcashier.setDirection(Direction.DOWN);
		museumcashier.initHP(100);

		zone.add(museumcashier);
	}
}
