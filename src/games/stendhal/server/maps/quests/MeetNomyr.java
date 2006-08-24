package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Speak with Nomyr
 * 
 * PARTICIPANTS:
 * - Nomyr
 *
 * STEPS:
 * - Talk to Nomyr to activate the quest and keep speaking with Nomyr.
 *
 * REWARD:
 * - No XP
 * - No money
 * 
 * REPETITIONS:
 * - As much as wanted.
 */
public class MeetNomyr extends AbstractQuest {

	private void step_1() {
		SpeakerNPC npc = npcs.get("Nomyr Ahba");

		npc.add(ConversationStates.ATTENDING,
				"yes",
				null,
				ConversationStates.INFORMATION_1,
				"The young people have joined the Deniran army to fight in the South, so the city has been left unprotected from hordes of monsters coming from the dungeons. Can you help us?",
				null);

		npc.add(ConversationStates.ATTENDING,
				"no",
				null,
				ConversationStates.IDLE,
				"Oh, fine. Then help me at the other window... I'm trying to find out what's happening inside",
				null);

		npc.add(ConversationStates.INFORMATION_1,
				"yes",
				null,
				ConversationStates.IDLE,
				"First of all you should talk to Hayunn Naratha. He's a former great hero and our only defense here and he will gladly give you good advice. Good luck.",
				null);

		npc.add(ConversationStates.INFORMATION_1,
				"no",
				null,
				ConversationStates.IDLE,
				"Awww... I didn't know you were such a coward! What about me? Hey, I'm about to save a damsel in distress! Bye!",
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
	}
}
