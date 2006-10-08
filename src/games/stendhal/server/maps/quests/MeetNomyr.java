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
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.INFORMATION_1,
				"The young people have joined the Imperial Deniran Army to fight in the south, so the city has been left almost unprotected against the hordes of monsters coming from the dungeons. Can you help us?",
				null);

		npc.add(ConversationStates.ATTENDING,
				"no",
				null,
				ConversationStates.IDLE,
				"Huh. Well, you could help me by taking a peek through that other window, if you're not busy... I'm trying to figure out what's going on inside.",
				null);

		npc.add(ConversationStates.INFORMATION_1,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				"First of all, you should go talk to Hayunn Naratha. He's an great old hero, and he's also pretty much our only defender here... I'm sure he will gladly give you some advice! Good luck.",
				null);

		npc.add(ConversationStates.INFORMATION_1,
				"no",
				null,
				ConversationStates.IDLE,
				"Awww... so you're a coward, then? Huh.",
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
	}
}
