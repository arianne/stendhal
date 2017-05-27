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
package games.stendhal.server.maps.ados.market;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

/**
 * Provides Uncle Dag NPC, in Ados Market.
 * He will produce fierywater bottles if he is given sugar canes (from cane fields)
 *
 * @author omero
 */
public class FierywaterDistillerNPC implements ZoneConfigurator {

    @Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
        buildNPC(zone);
    }

    private void buildNPC(final StendhalRPZone zone) {
        final SpeakerNPC npc = new SpeakerNPC("Uncle Dag") {

            @Override
            protected void createPath() {
                setPath(null);
            }

            @Override
            protected void createDialog() {
                addGreeting("Yo!");
                addHelp("I'm quite new to this area myself. I cannot help you much, sorry!");
                addQuest("Oh, well... I'm not much into those kind of things... I'm a simple man with simple needs, but thank you for asking.");
                addJob("I can #brew #fierywater for you, if you bring to me enough #sugar #canes and #wood!");
                addOffer("If you need #fierywater, just ask me to #brew some for you!");
                addReply("fierywater",
                    "That's my speciality! Mix that with other ingredients and you'll have an excellent beverage."
                    +   " Gulp that down 100% pure and most likely you will not survive to tell the experience!");
                addReply(Arrays.asList("sugar", "cane", "canes", "sugar cane"),
                    "I get all my sugar canes imported directly from Athor island.");
                addReply("wood",
                    "You find plenty of wood logs near trees, and forests are the best place to look for those!");
                addGoodbye("Enjoy the market!");
            }

            @Override
            protected void onGoodbye(RPEntity player) {
                setDirection(Direction.DOWN);
            }
        };

        final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
        requiredResources.put("sugar cane", 5);
        requiredResources.put("wood", 1);

        final ProducerBehaviour behaviour = new ProducerBehaviour("uncle_dag_brew_fierywater",
            "brew", "fierywater", requiredResources, 20 * 60);
        new ProducerAdder().addProducer(npc, behaviour,
            "Yo! I'm Uncle Dag, the distillery man! If you bring me #sugar #canes, I could #brew #fierywater for you.");

        npc.setDescription("You see Uncle Dag. He runs the distillery stand in Ados market.");
        npc.setEntityClass("fierywaterdistillernpc");
        npc.setPosition(35, 30);
        npc.setDirection(Direction.DOWN);
        npc.initHP(100);
        zone.add(npc);
    }
}
