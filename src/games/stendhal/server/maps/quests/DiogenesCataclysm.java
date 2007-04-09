package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.StandardInteraction;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Diogenes and the Cataclysm
 *
 * PARTICIPANTS:
 * - Diogenes
 *
 * STEPS:
 * - Diogenes tells you to ask Carmen what's happening
 *
 * REPETITIONS:
 * - Always
 */
public class DiogenesCataclysm extends AbstractQuest {

	private void step_1() {
		SpeakerNPC npc = npcs.get("Diogenes");

		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES, new
			StandardInteraction.AllwaysTrue(),
			ConversationStates.ATTENDING, "Greetings. I expect you are wondering what strange things are happening here?", null);

		npc.addReply("yes", "So am I, my friend. I expect young Carmen will tell you something."); 
		npc.addReply("no", "Ah, the folly of youth! You do not look around you with open eyes until it is too late.");

	}
	private void step_2() {
	}

	private void step_3() {

	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}

}
