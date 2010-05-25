package games.stendhal.server.maps.athor.holiday_area;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
//TODO: take NPC definition elements which are currently in XML and include here
public class WifeNPC extends SpeakerNPCFactory {

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
		npc.addGreeting("Hi!");
		npc.addGoodbye("Bye!");
	}
}
