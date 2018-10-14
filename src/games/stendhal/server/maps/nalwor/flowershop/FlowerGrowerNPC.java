/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.nalwor.flowershop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class FlowerGrowerNPC implements ZoneConfigurator {

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
    	final SpeakerNPC npc = new SpeakerNPC("Seremela") {

    		@Override
    		protected void createPath() {
    			List<Node> nodes = new ArrayList<Node>();
    			nodes.add(new Node(4, 3));
    			nodes.add(new Node(3, 3));
    			nodes.add(new Node(3, 6));
    			nodes.add(new Node(4, 6));
    			nodes.add(new Node(4, 8));
    			nodes.add(new Node(2, 8));
    			nodes.add(new Node(2, 9));
    			nodes.add(new Node(6, 9));
    			nodes.add(new Node(6, 8));
    			nodes.add(new Node(4, 8));
    			nodes.add(new Node(4, 6));
    			nodes.add(new Node(10, 6));
    			nodes.add(new Node(10, 3));
    			nodes.add(new Node(10, 4));
    			nodes.add(new Node(11, 4));
    			nodes.add(new Node(10, 4));
    			nodes.add(new Node(10, 6));
    			nodes.add(new Node(3, 6));
    			nodes.add(new Node(3, 3));
    			setPath(new FixedPath(nodes, true));
    		}

			@Override
			public void createDialog() {
    			addGreeting("Hello.");
    			addGoodbye("Goodbye!");
    			//addHelp("Hmmm, I don't think there is anything I can help with.");
    			addJob("I take care of our city's beautiful flowers.");
    			addOffer("I don't have anything to offer.");
    			//addReply(Arrays.asList("flower", "flowers"), "Aren't flowers beautiful?");
    			//addEmotionReply(ConversationPhrases.GOODBYE_MESSAGES, "winks");
    		}
    	};

    	npc.setPosition(4, 3);
    	npc.setCollisionAction(CollisionAction.REVERSE);
    	npc.setDescription("You see a beautiful elf girl that loves flowers.");
    	npc.setEntityClass("elfflowergrowernpc");
    	zone.add(npc);
    }
}
