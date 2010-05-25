package games.stendhal.server.maps.athor.holiday_area;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
//TODO: take NPC definition elements which are currently in XML and include here
public class DiverNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Hallo, my friend!");
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
		        ConversationStates.ATTENDING, "No, thank you, I do not need help!", null);
		npc.addJob("I am a diver, but I cannot see a single fish at the moment!");
		npc.addHelp("I like the swimsuits which you can get in the dressing rooms at the beach.");
		npc.addGoodbye("Bye!");
	}
}
