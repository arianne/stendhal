package games.stendhal.server.maps.athor.holiday_area;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

public class WifeNPC extends SpeakerNPCFactory {

	@Override
	protected SpeakerNPC instantiate(String name) {
		SpeakerNPC npc = new SpeakerNPC(name) {
			@Override
			public void say(String text) {
				// She doesn't move around because she's "lying" on her towel.
				say(text, false);
			}
		};
		return npc;
	}

	@Override
	public void createDialog(SpeakerNPC npc) {
		npc.addGreeting("Hi!");
		npc.addGoodbye("Bye!");
	}
}
