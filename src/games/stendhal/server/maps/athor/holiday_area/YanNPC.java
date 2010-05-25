package games.stendhal.server.maps.athor.holiday_area;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
//TODO: take NPC definition elements which are currently in XML and include here
public class YanNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Hello stranger!");
		npc.addQuest("I don't have a task for you.");
		npc.addJob("Sorry, but on holiday I don't want to talk about work.");
		npc.addHelp("A cocktail bar is open! Look for a hut with a straw roof.");
		npc.addGoodbye("See you later!");
	}
}
