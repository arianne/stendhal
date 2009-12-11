package utilities;

import marauroa.common.game.RPEvent;
import games.stendhal.common.constants.Events;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Provides convenience methods for SpeakerNPC creation. the Created NPC extends
 * <p>
 * SpeakerNPC and overrides <code>registerTheNewNameInTheConversationParserWordList</code> to
 * avoid database access
 * 
 */
public abstract class SpeakerNPCTestHelper {

	public static SpeakerNPC createSpeakerNPC() {
		return createSpeakerNPC("bob");
	}

	public static SpeakerNPC createSpeakerNPC(final String name) {
		PlayerTestHelper.generateNPCRPClasses();
		return new SpeakerNPC(name);
	}

	public static String getReply(SpeakerNPC npc) {
		String reply = null;
		
		for (RPEvent event : npc.events()) {
			if (event.getName().equals(Events.PUBLIC_TEXT)) {
				reply = event.get("text");
			}
		}
		
		npc.clearEvents();
		
		return reply;
	}
}
