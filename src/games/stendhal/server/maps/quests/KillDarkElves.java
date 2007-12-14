package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Kill Dark Elves
 *
 * PARTICIPANTS: - Maerion
 *
 * STEPS: - Maerion asks you fix his dark elf problem - You go kill at least a
 * dark elf archer, captain, and thing - The thing drops an amulet - Maerion
 * checks your kills, takes the amulet and gives you a ring of life as reward
 *
 * REWARD: - emerald ring - 10000 XP
 *
 * REPETITIONS: - None.
 */
public class KillDarkElves extends AbstractQuest {
	private static final String QUEST_SLOT = "kill_dark_elves";

	private void step_1() {
		SpeakerNPC npc = npcs.get("Maerion");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, null,
				ConversationStates.QUEST_OFFERED, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						if (!player.hasQuest(QUEST_SLOT)
								|| player.getQuest(QUEST_SLOT).equals("rejected")) {
							engine.say("I have a problem with some dark elves. I used to be in league with them... now they are too strong. There is access to their lair from a #secret #room in this hall.");
						} else if (!player.isQuestCompleted(QUEST_SLOT)) {
							engine.say("I already asked you to kill every dark elf in the tunnel below the secret room. And bring me the amulet from the thing.");
							engine.setCurrentState(ConversationStates.ATTENDING);
						} else {
							engine.say("Thanks for your help. I am relieved to have the amulet back.");
							engine.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new StartRecordingKillsAction("dark_elf_archer", "dark_elf_captain", "thing"));
		actions.add(new IncreaseKarmaAction(5.0));
		actions.add(new SetQuestAction(QUEST_SLOT, "start"));
		
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Good. Please kill every dark elf down there and get the amulet from the mutant thing.",
			new MultipleActions(actions));

		npc.add(ConversationStates.QUEST_OFFERED, "no", null,
			ConversationStates.ATTENDING,
			"Then I fear for the safety of the Nalwor elves...",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("secret", "room"),
			null,
			ConversationStates.QUEST_OFFERED,
			"It's that room downstairs with a grey roof and the evil face on the door. Inside you'll find what the dark elves were making, a mutant thing. Will you help?",
			null);
	}

	private void step_2() {
		// Go kill the dark elves and get the amulet from the thing
	}

	private void step_3() {

		SpeakerNPC npc = npcs.get("Maerion");

		// the player returns to Maerion after having started the quest.
		// Maerion checks if the player has killed one of enough dark elf types
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.QUEST_STARTED, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						if (player.hasKilled("dark_elf_archer")
								&& player.hasKilled("dark_elf_captain")
								&& player.hasKilled("thing")) {
							// must have amulet from Thing to complete quest
							if (player.drop("amulet")) {
								engine.say("Many, many thanks. I am relieved to have that back. Here, take this ring. It can revive the powers of the dead.");
								Item emeraldring = StendhalRPWorld.get()
										.getRuleManager().getEntityManager()
										.getItem("emerald_ring");
								emeraldring.setBoundTo(player.getName());
								player.equip(emeraldring, true);
								player.addKarma(5.0);
								player.addXP(10000);
								player.setQuest(QUEST_SLOT, "done");
								engine.setCurrentState(ConversationStates.ATTENDING);
							} else {
								// 	this happens if player has killed the thing but
								// left the amulet somewhere else
								engine.say("What happened to the amulet? Remember I need it back!");
							}
						} else {
							engine.say("Don't you remember promising to sort out my dark elf problem? You need to go to the #secret #room below. Kill every dark elf.");
						}
					}
				});

		npc.add(
			ConversationStates.QUEST_STARTED,
			Arrays.asList("secret", "room"),
			null,
			ConversationStates.ATTENDING,
			"The room is below us. It has a grey roof and a evil face for a door. I need you to kill all the dark elves and bring me the amulet from the mutant thing.",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}
}
