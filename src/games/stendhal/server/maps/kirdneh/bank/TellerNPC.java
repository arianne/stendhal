/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.kirdneh.bank;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BankTellerAdder;


public class TellerNPC implements ZoneConfigurator {

  @Override
  public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
    zone.add(buildNPC());
  }

  private SpeakerNPC buildNPC() {
    final SpeakerNPC npc = new SpeakerNPC("Ivonne");

    npc.setOutfit("body=1,head=0,eyes=1,hair=15,dress=53");
    npc.setOutfitColor("hair", 0x891310);
    npc.setPosition(8, 13);
    npc.setIdleDirection(Direction.DOWN);

    npc.addGreeting("Hello, welcome to Kirdneh bank.");
    npc.addGoodbye();
    npc.addQuest("There is nothing I need help with.");

    // manage bank account balance
    BankTellerAdder.addTeller(npc);
    npc.addOffer(npc.getJob());

    return npc;
  }
}
