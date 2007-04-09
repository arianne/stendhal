package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.StandardInteraction;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Sato in hard times
 *
 * PARTICIPANTS:
 * - Sato
 *
 * STEPS:
 * - Sato tells you that Carmen can sense big changes
 *
 * REPETITIONS:
 * - Always
 */
public class SatoCataclysm extends AbstractQuest {

	private void step_1() {
		SpeakerNPC npc = npcs.get("Sato");

		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES, new
			StandardInteraction.AllwaysTrue(),
			ConversationStates.ATTENDING, "Hi. We've fallen on hard #times.", null);

		npc.addReply("times", "All I know is, my sheep are getting sick. Maybe #Carmen can sense what is happening here."); 
		npc.addReply("Carmen", "She's a summon healer, she can sense anything strange with her powers. Me, I'm just a simple sheep dealer.");

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
