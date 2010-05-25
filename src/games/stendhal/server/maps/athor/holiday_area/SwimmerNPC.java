package games.stendhal.server.maps.athor.holiday_area;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

//TODO: take NPC definition elements which are currently in XML and include here
public class SwimmerNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Don't disturb me, I'm trying to establish a record!");
		npc.addQuest("I don't have a task for you, I'm too busy.");
		npc.addJob("I am a swimmer!");
		npc.addHelp("Try the diving board! It's fun!");
		npc.addGoodbye("Bye!");
	};
}
