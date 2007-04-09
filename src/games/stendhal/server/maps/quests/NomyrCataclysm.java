package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.StandardInteraction;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Nomyr Ahba and the Cataclysm
 *
 * PARTICIPANTS:
 * - Nomyr Ahba
 *
 * STEPS:
 * - Nomyr Ahba tells you rumours of the Cataclysm
 *
 * REPETITIONS:
 * - Always
 */
public class NomyrCataclysm extends AbstractQuest {

	private void step_1() {
		SpeakerNPC npc = npcs.get("Nomyr Ahba");

		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES, new
			StandardInteraction.AllwaysTrue(),
			ConversationStates.ATTENDING, "Hi. I'm guessing you knew to come to an old gossip, for #information.", null);

		npc.addReply("information", "Well my friend, fire is spreading through Semos and we're all getting sick. People say that it's the start of a #Cataclysm..."); 
		npc.addReply("Cataclysm", "Don't ask me why, but I think the world will look very different in the near future. Lucky I haven't got a home to lose, really.");

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
