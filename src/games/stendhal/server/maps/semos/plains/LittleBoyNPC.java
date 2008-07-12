package games.stendhal.server.maps.semos.plains;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

/**
 * A little boy who lives at a farm.
 * 
 * @see games.stendhal.server.maps.quests.PlinksToy
 */
public class LittleBoyNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		// NOTE: These texts are only available after finishing the quest.
		npc.addGreeting();
		npc.addJob("I play all day.");
		npc.addHelp("Be careful out east, there are wolves about!");
		npc.addGoodbye();
	}
}
