package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.StandardInteraction;

/**
 * QUEST: Hayunn Naratha and the Cataclysm
 * 
 * PARTICIPANTS: - Hayunn Naratha
 * 
 * STEPS: - Hayunn refers to the Cataclysm
 * 
 * REPETITIONS: - Always
 */
public class HayunnCataclysm extends AbstractQuest {

	private void step_1() {
		SpeakerNPC npc = npcs.get("Hayunn Naratha");

		npc
				.add(
						ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new StandardInteraction.AllwaysTrue(),
						ConversationStates.ATTENDING,
						"Greetings. I'm ashamed to address you while I look #unwell. It's not fitting for my post.",
						null);

		npc
				.addReply(
						"unwell",
						"I imagine it is from the smoke. I hope it's nothing more ominous. In any case, let me know if I can help you at all.");

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
