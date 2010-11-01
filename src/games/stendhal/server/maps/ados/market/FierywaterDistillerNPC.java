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

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides Uncle Dag NPC, in Ados Market.
 * He will produce fierywater bottles if he is given canes (from cane fields)
 * 
 * @author omero
 */
public class FierywaterDistillerNPC implements ZoneConfigurator {

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
                addJob("I can #brew #fierywater for you, if you bring to me enough #canes!");
                addOffer("If you need #fierywater, just ask me to #brew some for you!");
                addReply("fierywater",
                    "That's my specialty! Mix that with other ingredients and you'll have an execellent beverage."
                    +   " Gulp that down 100% pure and most likely you will not survive to tell the experience!");
                addReply(Arrays.asList("cane", "canes"),
                    "I get all my #canes imported directly from Athor island.");
                addGoodbye("Enjoy the market!");
            }

            @Override
            protected void onGoodbye(Player player) {
                setDirection(Direction.DOWN);
            }
        };

        final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
        requiredResources.put("cane", 5);
        requiredResources.put("wood", 1);

        final ProducerBehaviour behaviour = new ProducerBehaviour("uncle_dag_brew_fierywater",
            "brew", "fierywater", requiredResources, 20 * 60);
        new ProducerAdder().addProducer(npc, behaviour,
            "Yo! I'm Uncle Dag, the distillery man! If you bring me #canes, I could #brew #fierywater for you.");

        npc.setDescription("You see Uncle Dag. He runs the distillery stand in Ados market.");
        npc.setEntityClass("fierywaterdistillernpc");
        npc.setPosition(35, 30);
        
        npc.initHP(100);
        zone.add(npc);
    }
}
