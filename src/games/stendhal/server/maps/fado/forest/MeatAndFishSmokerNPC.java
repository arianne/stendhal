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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.MultiProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.MultiProducerBehaviour;

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
                addJob(
                    "I can #smoke some " +
                    "#smoked #meat," + " " +
                    "#smoked #ham," + " " +
                    "#smoked #trout," + " " +
                    "#smoked #cod." + " " +
                    "To #smoke is my job."
                );

                addOffer(
                    "I will #smoke " +
                    "#smoked #meat," + " " +
                    "#smoked #ham," + " " +
                    "#smoked #trout," + " " +
                    "#smoked #cod." +  " " +
                    "You'll have to bring me what is needed."
                );

                addHelp(
                    "Ask me to #smoke for you any" + " " +
                    "#smoked #meat,"               + " " +
                    "#smoked #ham,"                + " " +
                    "#smoked #trout,"              + " " +
                    "#smoked #cod,"                + " " +
                    "that's what I'm good at when you bring me all that is needed."
                );

                addReply(
                    Arrays.asList("smoked", "smoked meat", "smoked ham", "smoked trout", "smoked cod"),
                    "The true secret are which herbs for the aroma along with enough wood for the perfect #smoke." + " " +
                    "Maybe you care to hear what I have to #offer."
                );

                addReply(Arrays.asList("arandula","sclaria", "kekik"),
                    "It grows in many places, at the edges or in the depths of a forest.");

                addReply(Arrays.asList("meat","ham"),
                    "I don't care if that comes from a lion, a bear or even an elephant... I can #smoke that for you!");

                addReply(Arrays.asList("trout", "cod"),
                    "I will not reveal my favorite fishing spots..." + " " +
                    "Go find some books on the subject in one of those scholarly places not many are familiar with anymore..."
                );

                addGoodbye("S' vegum...");

                final HashSet<String> productsNames = new HashSet<String>();
                productsNames.add("smoked meat");
                productsNames.add("smoked ham");
                productsNames.add("smoked trout");
                productsNames.add("smoked cod");

                //smoked meat: wood+arandula
                final Map<String, Integer> reqRes_smokedMeat = new TreeMap<String, Integer>();
                reqRes_smokedMeat.put("wood",      2);
                reqRes_smokedMeat.put("meat",      1);
                reqRes_smokedMeat.put("arandula",  1);

                //smoked ham: wood+kekik
                final Map<String, Integer> reqRes_smokedHam = new TreeMap<String, Integer>();
                reqRes_smokedHam.put("wood",       1);
                reqRes_smokedHam.put("ham",        1);
                reqRes_smokedHam.put("kekik",      1);

                //smoked trout: wood+arandula
                final Map<String, Integer> reqRes_smokedTrout = new TreeMap<String, Integer>();
                reqRes_smokedTrout.put("wood",     1);
                reqRes_smokedTrout.put("trout",    1);
                reqRes_smokedTrout.put("sclaria",  1);

                //smoked cod: wood+arandula
                final Map<String, Integer> reqRes_smokedCod = new TreeMap<String, Integer>();
                reqRes_smokedCod.put("wood",       1);
                reqRes_smokedCod.put("cod",        1);
                reqRes_smokedCod.put("arandula",   2);

                final HashMap<String, Map<String, Integer>> requiredResourcesPerProduct = new HashMap<String, Map<String, Integer>>();
                requiredResourcesPerProduct.put("smoked meat", reqRes_smokedMeat);
                requiredResourcesPerProduct.put("smoked ham", reqRes_smokedHam);
                requiredResourcesPerProduct.put("smoked trout", reqRes_smokedTrout);
                requiredResourcesPerProduct.put("smoked cod", reqRes_smokedCod);

                final HashMap<String, Integer> productionTimesPerProduct = new HashMap<String, Integer>();
                productionTimesPerProduct.put("smoked meat",  1 * 120);
                productionTimesPerProduct.put("smoked ham",   1 * 140);
                productionTimesPerProduct.put("smoked trout", 1 * 140);
                productionTimesPerProduct.put("smoked cod",   1 * 120);

                final HashMap<String, Boolean> productsBound = new HashMap<String, Boolean>();
                productsBound.put("smoked meat",  false);
                productsBound.put("smoked ham",   false);
                productsBound.put("smoked trout", false);
                productsBound.put("smoked cod",   false);

                final MultiProducerBehaviour behaviour = new MultiProducerBehaviour(
                    "olmo_smoke_meatandfish",
                    "smoke",
                    productsNames,
                    requiredResourcesPerProduct,
                    productionTimesPerProduct,
                    productsBound);

                new MultiProducerAdder().addMultiProducer(this, behaviour,
                        "Hi there! Sure you were attracted here by the aroma emanating from my tasty #smoked products!");
            }
        };

        npc.setEntityClass("meatandfishsmokernpc");
        npc.setDirection(Direction.DOWN);
        npc.setPosition(26, 53);
        npc.initHP(100);
        npc.setDescription("You see Olmo. He seems quite busy smoking meat and fish.");
        zone.add(npc);
    }
}
