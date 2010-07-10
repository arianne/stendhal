package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Jailed Dwarf
 * 
 * PARTICIPANTS: - Hunel, the guard of the Dwarf Kingdom's Prison
 * 
 * STEPS: - You see Hunel locked in the cell - You get the key by killing the
 * Duergar King - You speak to Hunel when you have the key. - Hunel wants to
 * stay in, he is afraid. - You can then sell chaos equipment to Hunel.
 * 
 * REWARD: - 2000 XP - everlasting place to sell chaos equipment
 * 
 * REPETITIONS: - None.
 */
public class JailedDwarf extends AbstractQuest {

	private static final String QUEST_SLOT = "jailed_dwarf";



	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void step_1() {
		final SpeakerNPC npc = npcs.get("Hunel");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				null, ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						if (player.isQuestCompleted(QUEST_SLOT)) {
							raiser.say("Hi. As you see, I am still to nervous to leave ...");
						} else {
							if (player.isEquipped("kanmararn prison key")) {
								player.setQuest(QUEST_SLOT, "done");
								player.addXP(2000);
								raiser.say("You got the key to unlock me! *mumble*  Errrr ... it doesn't look too safe out there for me ... I think I'll just stay here ... perhaps someone could #offer me some good equipment ... ");
							} else {
								raiser.say("Help! The duergars have raided the prison and locked me up! I'm supposed to be the Guard! It's a shambles.");
								player.setQuest(QUEST_SLOT, "start");
								raiser.setCurrentState(ConversationStates.IDLE);
							}
						}
					}
				});

	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
	}
	@Override
	public String getName() {
		return "JailedDwarf";
	}
	
	@Override
	public int getMinLevel() {
		return 60;
	}
}
