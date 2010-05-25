package games.stendhal.server.maps.semos.dungeon;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

/**
 * An army sergeant who lost his company.
 * 
 * @see games.stendhal.server.maps.quests.KanmararnSoldiers
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class SergeantNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Good day, adventurer!");
		npc.addJob("I'm a Sergeant in the army.");
		npc.addGoodbye("Good luck and better watch your back with all those dwarves around!");
		// all other behaviour is defined in the quest.
	}
}
