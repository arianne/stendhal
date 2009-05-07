package games.stendhal.server.maps.quests;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * QUEST: The Vampire Sword
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li>Hogart, a retired master dwarf smith, forgotten below the dwarf mines in
 * Orril.</li>
 * <li>Markovich, a sick vampire who will fill the goblet.</li>
 * </ul>
 * <p>
 * STEPS:
 * <ul>
 * <li>Hogart tells you the story of the Vampire Lord.</li>
 * <li>He offers to forge a Vampire Sword for you if you bring him what it
 * needs.</li>
 * <li>Go to the catacombs, kill 7 vampirettes to get to the 3rd level, kill 7
 * killer bats and the vampire lord to get the required items to fill the
 * goblet.</li>
 * <li>Fill the goblet and come back.</li>
 * <li>You get some items from the Catacombs and kill the Vampire Lord.</li>
 * <li>You get the iron needed in the usual way by collecting iron ore and
 * casting in Semos.</li>
 * <li>Hogart forges the Vampire Sword for you.</li>
 * </ul>
 * <p>
 * REWARD:
 * <ul>
 * <li>Vampire Sword</li>
 * <li>5000 XP</li>
 * </ul>
 * <p>
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class VampireSword extends AbstractQuest {

	private static final int REQUIRED_IRON = 10;

	private static final int REQUIRED_MINUTES = 10;

	private static final String QUEST_SLOT = "vs_quest";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void prepareQuestOfferingStep() {
		final SpeakerNPC npc = npcs.get("Hogart");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, 
			new ChatCondition() {
				public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					String quest = player.getQuest(QUEST_SLOT); 
					return ((quest == null) || quest.equals("rejected")); 
				}
			},
			ConversationStates.QUEST_OFFERED, 
			"I can forge a powerful life stealing sword for you. You will need to go to the Catacombs below Semos Graveyard and fight the Vampire Lord. Are you interested?",
			null);
		
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, 
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, 
			"What are you bothering me for now? You've got your sword, go and use it!",
			null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"Why are you bothering me when you haven't completed your quest yet?",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					npc.say("Then you need this #goblet. Take it to the Semos #Catacombs.");
					final Item emptygoblet = SingletonRepository.getEntityManager().getItem("empty goblet");
					player.equipOrPutOnGround(emptygoblet);
					player.setQuest(QUEST_SLOT, "start");
					player.addKarma(5);
				}
			});

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.IDLE,
			"Oh, well forget it then. You must have a better sword than I can forge, huh? Bye.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		npc.addReply("catacombs", "The Catacombs of north Semos of the ancient #stories.");

		npc.addReply("goblet", "Go fill it with the blood of the enemies you meet in the #Catacombs.");
	}

	private void prepareGobletFillingStep() {

		final SpeakerNPC npc = npcs.get("Markovich");

		npc.addGoodbye("*cough* ... farewell ... *cough*");
		npc.addReply(
			Arrays.asList("blood", "vampirette entrails", "bat entrails"),
			"I need blood. I can take it from the entrails of the alive and undead. I will mix the bloods together for you and #fill your #goblet, if you let me drink some too. But I'm afraid of the powerful #lord.");

		npc.addReply(Arrays.asList("lord", "vampire", "skull ring"),
			"The Vampire Lord rules these Catacombs! And I'm afraid of him. I can only help you if you kill him and bring me his skull ring with the #goblet.");

		npc.addReply(
			Arrays.asList("empty goblet", "goblet"),
			"Only a powerful talisman like this cauldron or a special goblet should contain blood.");

		final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();	
		requiredResources.put("vampirette entrails", 7);
		requiredResources.put("bat entrails", 7);
		requiredResources.put("skull ring", 1);
		requiredResources.put("empty goblet", 1);
		final ProducerBehaviour behaviour = new ProducerBehaviour(
				"sicky_fill_goblet", "fill", "goblet", requiredResources,
				5 * 60, true);
		new ProducerAdder().addProducer(npc, behaviour,
			"Please don't try to kill me...I'm just a sick old #vampire. Do you have any #blood I could drink? If you have an #empty goblet I will #fill it with blood for you in my cauldron.");

	}

	private void prepareForgingStep() {

		final SpeakerNPC npc = npcs.get("Hogart");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new ChatCondition() {
				public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					return player.hasQuest(QUEST_SLOT)
							&& player.getQuest(QUEST_SLOT).equals("start")
							&& player.isEquipped("goblet");
				}
			}, ConversationStates.QUEST_ITEM_BROUGHT, null,
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					if (player.isEquipped("iron", REQUIRED_IRON)) {
						player.drop("goblet");
						player.drop("iron", REQUIRED_IRON);
						npc.say("You've brought everything I need to make the vampire sword. Come back in "
							+ REQUIRED_MINUTES
							+ " minutes and it will be ready");
						player.setQuest(QUEST_SLOT, "forging;"
								+ System.currentTimeMillis());
						npc.setCurrentState(ConversationStates.IDLE);
					} else {
						npc.say("You have battled hard to bring that goblet. I will use it to #forge the vampire sword");
					}
				}
			});

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new ChatCondition() {
				public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					return player.hasQuest(QUEST_SLOT)
							&& player.getQuest(QUEST_SLOT).equals("start")
							&& !player.isEquipped("goblet")
							&& player.isEquipped("empty goblet");
				}
			}, ConversationStates.IDLE, null, new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					npc.say("Did you lose your way? The Catacombs are in North Semos. Don't come back without a full goblet! Bye!");
				}
			});

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new ChatCondition() {
				public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					return player.hasQuest(QUEST_SLOT)
							&& player.getQuest(QUEST_SLOT).equals("start")
							&& !player.isEquipped("goblet")
							&& !player.isEquipped("empty goblet");
				}
			}, ConversationStates.QUESTION_1, 
			"I hope you didn't lose your goblet! Do you need another?", null);

		npc.add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.IDLE, null, new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					npc.say("You stupid ..... Be more careful next time. Bye!");
					final Item emptygoblet = SingletonRepository.getEntityManager().getItem("empty goblet");
					player.equipOrPutOnGround(emptygoblet);
				}
			});

		npc.add(
			ConversationStates.QUESTION_1,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.IDLE,
			"Then why are you back here? Go slay some vampires! Bye!",
			null);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new QuestStateStartsWithCondition(QUEST_SLOT, "forging;"),
			ConversationStates.IDLE, null, new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					final String[] tokens = player.getQuest(QUEST_SLOT).split(";");
					// minutes -> milliseconds
					final long delay = REQUIRED_MINUTES * MathHelper.MILLISECONDS_IN_ONE_MINUTE;
					final long timeRemaining = (Long.parseLong(tokens[1]) + delay)
							- System.currentTimeMillis();
					if (timeRemaining > 0L) {
						npc.say("I haven't finished forging the sword. Please check back in "
							+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".");
						return;
					}
					npc.say("I have finished forging the mighty Vampire Sword. You deserve this. Now i'm going back to work, goodbye!");
					player.addXP(5000);
					player.addKarma(15);
					final Item vampireSword = SingletonRepository.getEntityManager().getItem(
									"vampire sword");
					vampireSword.setBoundTo(player.getName());
					player.equipOrPutOnGround(vampireSword);
					player.setQuest(QUEST_SLOT, "done");
					player.notifyWorldAboutChanges();
				}
			});

		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			"forge",
			null,
			ConversationStates.QUEST_ITEM_BROUGHT,
				"Bring me "
				+ REQUIRED_IRON
				+ " #iron bars to forge the sword with. Don't forget to bring the goblet too.",
			null);

		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			"iron",
			null,
			ConversationStates.IDLE,
			"You know, collect the iron ore lying around and get it cast! Bye!",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		prepareQuestOfferingStep();
		prepareGobletFillingStep();
		prepareForgingStep();
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("QUEST_REJECTED");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "done")) {
			res.add("QUEST_ACCEPTED");
		}
		if ((questState.equals("start") && player.isEquipped("goblet"))
				|| questState.equals("done")) {
			res.add("FOUND_ITEM");
		}
		if (player.getQuest(QUEST_SLOT).startsWith("forging;")) {
			res.add("FORGING");
		}
		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}

	@Override
	public String getName() {
		return "VampireSword";
	}
}
