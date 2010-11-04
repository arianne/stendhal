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
package games.stendhal.server.maps.wofol.bar;

import games.stendhal.common.Grammar;
import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
  * Provides Ortiz, a regular customer of Kobold's Den bar in Wo'fol.
  *
  * @author omero
  */
public class KoboldBarRegularNPC implements ZoneConfigurator {

    /**
     * Configure a zone.
     *
     * @param   zone        The zone to be configured.
     * @param   attributes  Configuration attributes.
     */
    public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
        buildNPC(zone);
    }

    private void buildNPC(final StendhalRPZone zone) {

        final SpeakerNPC npc = new SpeakerNPC("Ortiz") {

            @Override
            public void say(final String text) {
               
                    // doesn't turn
                    say(text, false);
            }

            @Override
            protected void createPath() {
                setPath(null);
            }

            @Override

            protected void createDialog() {

                addGreeting("*BuRP*");
                addHelp("*GrOaN*");
                addQuest("*mOaN*");
                addGoodbye("*BUrP!*");
                addReply(Arrays.asList("wine","beer","mild","strong","torcibud"),
                        "*Wrof!*");
            }
        };

        npc.setEntityClass("koboldbarregularnpc");
        npc.setPosition(6, 5);
        npc.initHP(100);
        npc.setDirection(Direction.UP);
        npc.setDescription("You see Ortiz, a regular here in Kobold's Den bar.");
        zone.add(npc);

    }
}
