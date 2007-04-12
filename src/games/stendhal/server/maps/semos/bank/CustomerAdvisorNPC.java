package games.stendhal.server.maps.semos.bank;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

public class CustomerAdvisorNPC extends SpeakerNPCFactory {

	protected void createDialog(SpeakerNPC npc) {
		npc.addGreeting("Welcome to the bank of Semos! Do you need #help on your personal chest?");
		npc.addHelp("Follow the corridor to the right, and you will find the magic chests. You can store your belongings in any of them, and nobody else will be able to touch them!");
		npc.addJob("I'm the Customer Advisor here at Semos Bank.");
		npc.addGoodbye("It was a pleasure to serve you.");
	}
}
