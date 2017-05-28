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
package games.stendhal.server.maps.semos.wizardstower;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Zekiel, the guardian statue of the Wizards Tower (Zekiel in the basement)
 *
 * @see games.stendhal.server.maps.quests.ZekielsPracticalTestQuest
 * @see games.stendhal.server.maps.semos.wizardstower.WizardsGuardStatueSpireNPC
 */
public class WizardsGuardStatueNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildZekiel(zone);
	}

	private void buildZekiel(final StendhalRPZone zone) {
		final SpeakerNPC zekiel = new SpeakerNPC("Zekiel the guardian") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings Stranger! I am Zekiel the #guardian.");
				addHelp("I guess you want to explore this #tower. I am not just the #guardian, I am also here to receive visitors and accompany them through the practical #test.");
				addJob("I am the #guardian of this #tower.");
				addReply("guardian", "I watch and guard this #tower, the residence of the #wizards circle.");
				addReply("tower", "If you want to reach the spire, you have to pass the practical #test.");
				addGoodbye("So long!");
				addReply("test", "The practical test will be your #quest from me.");
				addReply(Arrays.asList("wizard", "wizards"),
				        "Seven wizards form the wizards circle. These are #Erastus, #Elana, #Ravashack, #Jaer, #Cassandra, #Silvanus and #Malleus");
				addReply("erastus", "Erastus is the archmage of the wizards circle. He is the grandmaster of all magics and the wisest person that is known. He is the only one without a part in the practical test.");
				addReply("elana", "Elana is the warmest and friendliest enchantress. She is the protectress of all living creatures and uses divine magic to save and heal them.");
				addReply("ravashack", "Ravashack is a very mighty necromancer. He has studied the dark magic for ages. Ravashack is a mystery, using dark magic to gain the upper hand on his opponents, but fighting the evil liches, his arch enemies.");
				addReply("jaer", "Jaer is the master of illusion. Charming and flighty like a breeze on a hot summer day. His domain is Air and he has many allies in the plains of mythical ghosts.");
				addReply("cassandra", "Cassandra is a beautiful woman, but foremost a powerful sorceress. Cassandra's domain is Water and she can be cold like ice to achieve her aim.");
				addReply("silvanus", "Silvanus is a sage druid and perhaps the eldest of all elves. He is a friend of all animals, trees, fairy creatures and ents. His domain is Earth and Nature.");
				addReply("malleus", "Malleus is the powerful archetype of a magician and the master of destructive magics. His domain is Fire and he rambled the plains of demons for ages, to understand their ambitions.");

			} //remaining behaviour defined in maps.quests.ZekielsPracticalTestQuest
		};

		zekiel.setDescription("You see Zekiel, the guardian of this tower.");
		zekiel.setEntityClass("transparentnpc");
		zekiel.setAlternativeImage("zekiel");
		zekiel.setPosition(15, 15);
		zekiel.initHP(100);
		zone.add(zekiel);
	}
}
