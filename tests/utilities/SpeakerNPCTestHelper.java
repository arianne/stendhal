package utilities;

import games.stendhal.server.entity.npc.SpeakerNPC;

public class SpeakerNPCTestHelper  {

	public static SpeakerNPC createSpeakerNPC() {
		return createSpeakerNPC("bob");
	}

	public static SpeakerNPC createSpeakerNPC(String name) {
		PlayerHelper.generateNPCRPClasses();
		return new SpeakerNPC(name);

	}

}
