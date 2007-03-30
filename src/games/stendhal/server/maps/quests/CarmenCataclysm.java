package games.stendhal.server.maps.quests;

import java.util.Arrays;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.StandardInteraction;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Carmen senses a Cataclysm
 *
 * PARTICIPANTS:
 * - Carmen
 *
 * STEPS:
 * - Carmen tells you that she can sense big changes
 *
 * REPETITIONS:
 * - Always
 */
public class CarmenCataclysm extends AbstractQuest {

	private void step_1() {
		SpeakerNPC npc = npcs.get("Carmen");

		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES, new
			StandardInteraction.AllwaysTrue(),
			ConversationStates.ATTENDING, "Hello. I can #heal you in these #troubled #times.", null);

		npc.addReply( Arrays.asList("troubled", "times"), "I sense many changes approaching. I believe that a #Cataclysm is coming."); 
		npc.addReply("Cataclysm", "Yes, some upheaval, maybe a rebirth of old spirits. The lands could change and new ways begin.");

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
