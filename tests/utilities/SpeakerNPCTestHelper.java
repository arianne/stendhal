package utilities;



import games.stendhal.server.entity.npc.SpeakerNPC;

public class SpeakerNPCTestHelper  {

	public static SpeakerNPC createSpeakerNPC() {
		PlayerHelper.generateNPCRPClasses();
		return new SpeakerNPC("bob");
	}



}
