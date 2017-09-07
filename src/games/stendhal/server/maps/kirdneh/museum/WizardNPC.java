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
package games.stendhal.server.maps.kirdneh.museum;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a wizard npc, an expert in textiles.
 *
 * @author kymara
 */
public class WizardNPC implements ZoneConfigurator {

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
		final SpeakerNPC npc = new SpeakerNPC("Kampusch") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(23, 3));
				nodes.add(new Node(23, 44));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			    protected void createDialog() {
				addHelp("Sorry, I am not the curator of this museum, I am only looking around here like you.");
				addOffer("I will teach you about #thread, and #fabric, and how wizards can fuse #mithril onto textiles.");
				addJob("I'm a wizard, I specialise in magical textiles. I can tell you anything you want to know about #thread and #fabric.");
				addReply("thread", "The best thread of all is light and strong, it is called #silk and it comes from the silk glands of spiders. Making the thread from the glands is a job which is messy. Wizards will not stoop so low. #Scientists are most likely to make thread if you need it.");
				addReply("fabric", "Cloth has different standards, which I'm sure you'll notice in your own cloaks. #Mithril fabric is the very finest and strongest of all. But then, I would say that, being from Mithrilbourgh... So, you need to find plenty of silk glands, then take them to a #scientist to make the thread. Once you have silk thread bring it to me to #fuse mithril into it. Finally, you will need to take the mithril thread to #Whiggins to get the fabric woven.");
				addReply("mithril", "Should you need it, I can #fuse mithril nuggets and silk thread together. But I don't perform this magic for just anyone... Once you have the mithril thread, it can be woven into fabric by #Whiggins.");
				addGoodbye("Farewell.");
				// remaining behaviour defined in maps.quests.MithrilCloak
	 	     }

		};

		npc.setDescription("You see one of the mithrilbourgh wizards, taking in the artwork at the museum");
		npc.setEntityClass("mithrilforgernpc");
		npc.setPosition(23, 3);
		npc.initHP(100);
		zone.add(npc);
	}
}
