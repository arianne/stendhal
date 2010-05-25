package games.stendhal.server.maps.athor.holiday_area;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
//TODO: take NPC definition elements which are currently in XML and include here
public class TouristFromAdosNPC extends SpeakerNPCFactory {

	@Override
	protected SpeakerNPC instantiate(final String name) {
		final SpeakerNPC npc = new SpeakerNPC(name) {
			@Override
			public void say(final String text) {
				// She doesn't move around because she's "lying" on her towel.
				say(text, false);
			}
		};
		return npc;
	}
	
	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Nice to meet you!");
		npc.addJob("I'm on holiday! Let's talk about anything else!");
		npc.addHelp("Be careful! On this island is a desert where many adventurers found their death...");
		npc.addGoodbye("I hope to see you soon!");
		// more dialog is defined in the SuntanForZara quest.
	}
}
