package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.KilledCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
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
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"I already asked you to kill every dark elf in the tunnel below the secret room. And bring me the amulet from the thing.",
				null);

		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"Thanks for your help. I am relieved to have the amulet back.",
				null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, 
				"I have a problem with some dark elves. I used to be in league with them... now they are too strong. There is access to their lair from a #secret #room in this hall.",
				null);

		List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new StartRecordingKillsAction("dark elf archer", "dark elf captain", "thing"));
		actions.add(new IncreaseKarmaAction(5.0));
		actions.add(new SetQuestAction(QUEST_SLOT, "start"));
		
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Good. Please kill every dark elf down there and get the amulet from the mutant thing.",
			new MultipleActions(actions));

		npc.add(ConversationStates.QUEST_OFFERED, 
			ConversationPhrases.NO_MESSAGES, 
			null,
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
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start")
				   , new NotCondition(new KilledCondition("dark elf archer", "dark elf captain", "thing"))),
				ConversationStates.QUEST_STARTED, 
				"Don't you remember promising to sort out my dark elf problem? You need to go to the #secret #room below. Kill every dark elf.",
				null);
		
		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start")
								   , new KilledCondition("dark elf archer", "dark elf captain", "thing")
								   , new NotCondition(new PlayerHasItemWithHimCondition("amulet")))
				, ConversationStates.QUEST_STARTED
				, "What happened to the amulet? Remember I need it back!"
				, null);
				
		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start")
								   , new KilledCondition("dark elf archer", "dark elf captain", "thing")
								   , new PlayerHasItemWithHimCondition("amulet"))
				, ConversationStates.QUEST_STARTED
				, "Many, many thanks. I am relieved to have that back. Here, take this ring. It can revive the powers of the dead.",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {

						player.drop("amulet");
						Item emeraldring = SingletonRepository.getEntityManager()
								.getItem("emerald ring");
						emeraldring.setBoundTo(player.getName());
						player.equip(emeraldring, true);
						player.addKarma(5.0);
						player.addXP(10000);
						player.setQuest(QUEST_SLOT, "done");
						engine.setCurrentState(ConversationStates.ATTENDING);
					
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
