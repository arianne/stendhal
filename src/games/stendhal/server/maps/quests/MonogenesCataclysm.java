package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.StandardInteraction;

/**
 * QUEST: Monogenes and the Cataclysm
 * 
 * PARTICIPANTS: - Monogenes
 * 
 * STEPS: - Monogenes speaks of the fire and Cataclysm
 * 
 * REPETITIONS: - Always
 */
public class MonogenesCataclysm extends AbstractQuest {

	private void step_1() {
		SpeakerNPC npc = npcs.get("Monogenes");

		npc
				.add(
						ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new StandardInteraction.AllwaysTrue(),
						ConversationStates.ATTENDING,
						"Hi. *cough* *splutter* The smoke is getting into my lungs. The #fire is spreading.",
						null);

		npc
				.addReply(
						"fire",
						"It started overnight and now Semos is lit up like a torch. They say a #Cataclysm is coming.");
		npc
				.addReply(
						"Cataclysm",
						"I've never seen the like, but my great grandfather spoke of such a thing. Some see it as a disaster. Others say that the rebuilding after such an event allows for new life and new ways.");

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
