package utilities;

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

	public static SpeakerNPC createSpeakerNPC(String name) {
		PlayerTestHelper.generateNPCRPClasses();
		return new SpeakerNPC(name);
	}

}
