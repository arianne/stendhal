package games.stendhal.server.maps.athor.holiday_area;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

public class YanNPC extends SpeakerNPCFactory {

	@Override
	protected void createDialog(SpeakerNPC npc) {
		npc.addGreeting("Hello stranger!");
		npc.addQuest("I don't have a task right now, but in the next release I will get one...");
		npc.addJob("Sorry, but on holiday I don't want to talk about work");
		npc.addHelp("A cocktail bar will open on this island soon.");
		npc.addGoodbye("See you later!");
	}
}
