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
package games.stendhal.server.maps.fado.forest;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.MultiProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.MultiProducerBehaviour;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides a Meat and Fish professional smoker in Fado forest.
 *
 * @author omero 
 */
public class MeatAndFishSmokerNPC implements ZoneConfigurator {
    /**
     * Configure a zone.
     *
     * @param   zone        The zone to be configured.
     * @param   attributes  Configuration attributes.
     */
    @Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
        buildNPC(zone);
    }

    private void buildNPC(final StendhalRPZone zone) {
        final SpeakerNPC npc = new SpeakerNPC("Olmo") {

            @Override
            protected void createPath() {
                final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(26, 53));
                nodes.add(new Node(26, 58));
                nodes.add(new Node(22, 58));
                nodes.add(new Node(29, 58));
                nodes.add(new Node(29, 52));
                nodes.add(new Node(31, 52));
                nodes.add(new Node(27, 52));
                nodes.add(new Node(27, 53));
                nodes.add(new Node(25, 53));
                nodes.add(new Node(25, 52));
                nodes.add(new Node(22, 52));
                nodes.add(new Node(22, 50));
                nodes.add(new Node(22, 53));

                setPath(new FixedPath(nodes, true));
            }

            @Override
            protected void createDialog() {
                addJob("I can #smoke you #smoked #meat, #smoked #ham, #smoked #trout or #smoked #cod. Just ask me to!");
                addOffer("I will #smoke for you #smoked #meat, #smoked #ham, #smoked #trout or #smoked cod, but you'll have to bring me what is needed.");
                addHelp("Ask me to #smoke for you #smoked #meat, #smoked #ham, #smoked #trout or #smoked #cod, that's what I'm good at when you bring me what is needed.");

                addReply(Arrays.asList("smoked", "smoked meat", "smoked ham", "smoked trout", "smoked cod"),
                    "The true secret is which herbs and which wood will make the perfect #smoke.");
                addReply(Arrays.asList("sclaria", "kekik"),
                    "It grows in many places, at the edges or well in the depths of a forest.");
                addReply(Arrays.asList("trout", "cod"),
                    "I wouldn't reveal you where my favorite fishing spots are but I would suggest you go find some books on the subject in one of those scholarly places.");
                addReply(Arrays.asList("meat","ham"),
                    "I don't care if it comes from lion or elephant... I can #smoke that for you!"); 
                    
                addGoodbye("S' veg!");

                final HashSet<String> productsNames = new HashSet<String>();
                productsNames.add("smoked meat");
                productsNames.add("smoked ham");
                productsNames.add("smoked trout");
                productsNames.add("smoked cod");

                final Map<String, Integer> reqRes_smokedMeat = new TreeMap<String, Integer>();
                reqRes_smokedMeat.put("wood", 2);
                reqRes_smokedMeat.put("meat", 1);
                reqRes_smokedMeat.put("kekik", 1);

                final Map<String, Integer> reqRes_smokedHam = new TreeMap<String, Integer>();
                reqRes_smokedHam.put("wood", 3);
                reqRes_smokedHam.put("ham", 1);
                reqRes_smokedHam.put("kekik", 2);

                final Map<String, Integer> reqRes_smokedTrout = new TreeMap<String, Integer>();
                reqRes_smokedTrout.put("wood", 1);
                reqRes_smokedTrout.put("trout", 1);
                reqRes_smokedTrout.put("sclaria", 1);

                final Map<String, Integer> reqRes_smokedCod = new TreeMap<String, Integer>();
                reqRes_smokedCod.put("wood", 1);
                reqRes_smokedCod.put("cod", 1);
                reqRes_smokedCod.put("sclaria", 2);


                final HashMap<String, Map<String, Integer>> requiredResourcesPerProduct = new HashMap<String, Map<String, Integer>>();
                requiredResourcesPerProduct.put("smoked meat", reqRes_smokedMeat);
                requiredResourcesPerProduct.put("smoked ham", reqRes_smokedHam);
                requiredResourcesPerProduct.put("smoked trout", reqRes_smokedTrout);
                requiredResourcesPerProduct.put("smoked cod", reqRes_smokedCod);

                final HashMap<String, Integer> productionTimesPerProduct = new HashMap<String, Integer>();
                productionTimesPerProduct.put("smoked meat", 5 * 60);
                productionTimesPerProduct.put("smoked ham", 8 * 60);
                productionTimesPerProduct.put("smoked trout", 4 * 60);
                productionTimesPerProduct.put("smoked cod", 6 * 60);

                final HashMap<String, Boolean> productsBound = new HashMap<String, Boolean>();
                productsBound.put("smoked meat", false);
                productsBound.put("smoked ham", true);
                productsBound.put("smoked trout", true);
                productsBound.put("smoked cod", false);

                final MultiProducerBehaviour behaviour = new MultiProducerBehaviour(
                    "olmo_smoke_meatandfish",
                    "smoke",
                    productsNames,
                    requiredResourcesPerProduct,
                    productionTimesPerProduct,
                    productsBound);

                new MultiProducerAdder().addMultiProducer(this, behaviour,
                        "Hi there! Sure you smelled the aroma coming from  my #smoked products!");
            }
        };

        npc.setEntityClass("meatandfishsmokernpc");
        npc.setDirection(Direction.DOWN);
        npc.setPosition(26, 53);
        npc.initHP(100);
        npc.setDescription("You see Olmo. He seems busy smoking meat and fish.");
        zone.add(npc);
    }
}
