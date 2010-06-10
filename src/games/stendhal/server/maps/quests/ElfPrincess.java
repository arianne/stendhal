package games.stendhal.server.maps.quests;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

/**
 * QUEST: The Elf Princess
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Tywysoga, the Elf Princess in Nalwor Tower</li>
 * <li>Rose Leigh, the wandering flower seller.</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>The princess asks you for a rare flower</li>
 * <li>Find the wandering flower seller</li>
 * <li>You are given the flower, provided you've already been asked to fetch it</li>
 * <li>Take flower back to princess</li>
 * <li>Princess gives you a reward</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>5000 XP</li>
 * <li>Some gold bars, random between 5,10,15,20,25,30.</li>
 * <li>Karma: 15</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Unlimited, provided you've activated the quest by asking the princess
 * for a task again</li>
 * </ul>
 */
public class ElfPrincess extends AbstractQuest {

	private static final String QUEST_SLOT = "elf_princess";


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void offerQuestStep() {
		final SpeakerNPC npc = npcs.get("Tywysoga");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new OrCondition(new QuestNotStartedCondition(QUEST_SLOT), new QuestInStateCondition(QUEST_SLOT, "rejected")),
			ConversationStates.QUEST_OFFERED,
			"Will you find the wandering flower seller, Rose Leigh, and get from her my favourite flower, the Rhosyd?",
			null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition("QUEST_SLOT"),
			ConversationStates.ATTENDING,
			"I have plenty of blooms now thank you.", null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "flower_brought"),
			ConversationStates.QUEST_OFFERED,
			"The last Rhosyd you brought me was so lovely. Will you find me another from Rose Leigh?",
			null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new OrCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new QuestInStateCondition(QUEST_SLOT, "got_flower")),
			ConversationStates.ATTENDING,
			"I do so love those pretty flowers from Rose Leigh ...",
			null);

		// Player agrees to collect flower
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Thank you! Once you find it, say #flower to me so I know you have it. I'll be sure to give you a nice reward.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 10.0));

		// Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
			"Oh, never mind. Bye then.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));
	}

	private void getFlowerStep() {
		final SpeakerNPC rose = npcs.get("Rose Leigh");

		rose.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.IDLE, 
			"Hello dearie. My far sight tells me you need a pretty flower for some fair maiden. Here ye arr, bye now.",
			new MultipleActions(new EquipItemAction("rhosyd", 1, true), new SetQuestAction(QUEST_SLOT, "got_flower")));

	rose.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new QuestNotInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.IDLE,
			"I've got nothing for you today, sorry dearie. I'll be on my way now, bye.", 
			null);
	}

	private void bringFlowerStep() {
		final SpeakerNPC npc = npcs.get("Tywysoga");
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("flower", "Rhosyd"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "got_flower"), new PlayerHasItemWithHimCondition("rhosyd")),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						player.drop("rhosyd");
						player.addXP(5000);
						player.addKarma(15);
						final StackableItem goldbars = (StackableItem) SingletonRepository.getEntityManager()
								.getItem("gold bar");
						int goldamount;
						goldamount = 5 * Rand.roll1D6();
						goldbars.setQuantity(goldamount);
						// goldbars.setBoundTo(player.getName()); <- not sure
						// if these should get bound or not.
						player.equipOrPutOnGround(goldbars);
						npc.say("Thank you! Take these " + Integer.toString(goldamount) + " gold bars, I have plenty. And, listen: If you'd ever like to get me another, be sure to ask me first. Rose Leigh is superstitious, she won't give the bloom unless she senses you need it.");
						player.setQuest(QUEST_SLOT, "flower_brought");
					}
				});

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("flower", "Rhosyd"),
			new NotCondition(new PlayerHasItemWithHimCondition("rhosyd")),
			ConversationStates.ATTENDING,
			"You don't seem to have a rhosyd bloom with you. But Rose Leigh wanders all over the island, I'm sure you'll find her one day!",
			null);

	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		offerQuestStep();
		getFlowerStep();
		bringFlowerStep();
	}
	@Override
	public String getName() {
		return "ElfPrincess";
	}
	
	@Override
	public int getMinLevel() {
		return 60;
	}
	
	@Override
	public boolean isRepeatable(final Player player) {
		return new QuestInStateCondition(QUEST_SLOT,"flower_brought").fire(player,null, null);
	}
	
	@Override
	public boolean isCompleted(final Player player) {
		return new QuestInStateCondition(QUEST_SLOT,"flower_brought").fire(player,null, null);
	}
}
