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
package games.stendhal.server.maps.ados.bank;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Map;

/**
 * Builds the Ados bank npc.
 *
 * @author Vanessa Julius
 */
public class BankNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Rachel") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to Ados Bank!");
				addJob("I am the customer advisor of Ados Bank.");
				addHelp("Our rooms contain 4 #chests of our bank and 4 chests of our affiliate in #Semos. They are available for your full use.");
				addReply("chests", "You can find the chests in our two separated rooms. Two chests of our bank are accessible on the left side in these rooms and 2 of our affiliate in Semos at the right.");
				addReply("Semos", "Our main affiliate is in Semos City. Maybe you met my chief advisor #Dagobert already. He is my personal mentor.");
				addReply("Dagobert", "He can explain a lot about our banking system, but maybe I can explain #more to you as well if you want.");
				addReply("more", "Visit one of our two rooms for reaching our magic chests. You can store your belongings in any of them, and nobody will be able to reach them. A number of spells have been cast on the chest areas to ensure #safety.");
				addReply("safety", "When you are standing at a chest to organise your items, any other people or animals will not be able to come near you. A magical aura stops others from using scrolls to arrive near you. You will need to walk out. Lastly let me tell you about safe #trading.");
				addReply("trading","To start a trade with another player, right-click on them and select 'Trade'. If they also want to trade with you, you'll see a window pop up where you can drag items to offer, and see what is being offered to you. Both click Offer, and then you both need to Accept the offer to complete the trade.");
				addQuest("Sorry, I have no job for you at the moment.");
 				addGoodbye("Thank you for visiting our bank!");
			}
			
			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}
		};
		
		npc.setDescription("You see Rachel, a smart looking woman. She works in Ados bank.");
		npc.setEntityClass("adosbankassistantnpc");
		npc.setDirection(Direction.DOWN);
		npc.setPosition(9, 4);
		npc.initHP(100);
		zone.add(npc);
		
	}
}
