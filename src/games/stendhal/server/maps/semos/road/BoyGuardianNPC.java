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
package games.stendhal.server.maps.semos.road;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.Map;

public class BoyGuardianNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildMineArea(zone, attributes);
	}

	private void buildMineArea(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Will") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting(null, new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						String reply = "Hey you! Take care, you are leaving the city now! ";
						if (player.getLevel() < 60) {
							reply += "Always watch out for animals who might attack you and other evil guys who walk around...";
						} else {
							reply += "Better prepare yourself with food or drinks and be careful!";
						}
						raiser.say(reply);
					}
				});
				addJob("Ohh my job is it to watch out for baaad creatures! I got that special #duty by my parents!");
				addReply("duty", "Yes, a really special and important one!");
				addHelp("My daddy always tells me to #sneak around through forests which aren't known to me... And he said that I should always take something to #eat #and #drink with me for being save!");
				addReply("sneak", "Yes if you want to be a warrior like I want to be, you have to work quiet and undetected!");
				addReply(Arrays.asList("eat","drink","eat and drink"), "Leander, the Semos baker, makes some really tasty sandwiches, mom always buys them there and his bread is yummi, too! Not sure about drinks, maybe you can ask #Carmen or #Margaret?");
				addReply("Carmen", "She is a famous healer in Semos city. Maybe you saw her on your way from the village into the city :)");
				addReply("Margaret", "She works in the tavern but I am not allowed to go there without my parents...");
				addQuest("I'm on a mission :) Watching out for bad bad guys and warn people! But I have nothing to do for you...");
				addGoodbye("Shhhhh don't talk too loud! Bye and take care!");
			}
			
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}
		};


		npc.addInitChatMessage(null, new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				if (!player.hasQuest("WillFirstChat")) {
					player.setQuest("WillFirstChat", "done");
					((SpeakerNPC) raiser.getEntity()).listenTo(player, "hi");
				}
			}
		});

		npc.setEntityClass("boyguardnpc");
		npc.setDescription("You see Will. He wants to be a mighty guardian in his future.");
		npc.setPosition(6, 43);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}
}
