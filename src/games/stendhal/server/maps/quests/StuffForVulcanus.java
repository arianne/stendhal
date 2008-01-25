package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * QUEST: The immortal sword forging.
 * 
 * PARTICIPANTS:
 * <ul>
 * <li> Vulcanus, son of Zeus itself, will forge for you the god's sword.
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li> Vulcanus tells you about the sword.
 * <li> He offers to forge a immortal sword for you if you bring him what it
 * needs.
 * <li> You give him all what he ask you.
 * <li> Vulcanus forges the immortal sword for you
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li> immortal sword
 * <li>15000 XP
 * </ul>
 * 
 * 
 * REPETITIONS:
 * <ul>
 * <li> None.
 * </ul>
 */
public class StuffForVulcanus extends AbstractQuest {
	private static final int REQUIRED_IRON = 15;

	private static final int REQUIRED_GOLD_BAR = 12;

	private static final int REQUIRED_WOOD = 26;

	private static final int REQUIRED_GIANT_HEART = 6;

	private static final int REQUIRED_MINUTES = 10;

	private static final String QUEST_SLOT = "immortalsword_quest";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Vulcanus");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.QUEST_OFFERED, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
					if (!player.hasQuest(QUEST_SLOT) || "rejected".equals(player.getQuest(QUEST_SLOT))) {
						engine.say("I once forged the most powerful of swords. I can do it again for you. Are you interested?");
					} else if (player.isQuestCompleted(QUEST_SLOT)) {
						engine.say("Oh! I am so tired. Look for me later. I need a few years of relaxing.");
						engine.setCurrentState(ConversationStates.ATTENDING);
					} else {
						engine.say("Why are you bothering me when you haven't completed your quest yet?");
						engine.setCurrentState(ConversationStates.ATTENDING);
					}
				}
			});

		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
					engine.say("I will need several things: "
						+ REQUIRED_IRON
						+ " iron, "
						+ REQUIRED_WOOD
						+ " wood logs, "
						+ REQUIRED_GOLD_BAR
						+ " gold bars and "
						+ REQUIRED_GIANT_HEART
						+ " giant hearts. Come back when you have them in the same #exact order!");
					player.setQuest(QUEST_SLOT, "start;0;0;0;0");
					player.addKarma(10);

				}
			});

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.IDLE,
			"Oh, well forget it then, if you don't want an immortal sword...",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));

		npc.addReply("exact",
			"This archaic magic requires that the ingredients are added on a exact order.");
	}

	private void step_2() {
		/* Get the stuff. */
	}

	private void step_3() {

		SpeakerNPC npc = npcs.get("Vulcanus");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new QuestStateStartsWithCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
					String[] tokens = player.getQuest(QUEST_SLOT).split(";");

					int neededIron = REQUIRED_IRON
							- Integer.parseInt(tokens[1]);
					int neededWoodLogs = REQUIRED_WOOD
							- Integer.parseInt(tokens[2]);
					int neededGoldBars = REQUIRED_GOLD_BAR
							- Integer.parseInt(tokens[3]);
					int neededGiantHearts = REQUIRED_GIANT_HEART
							- Integer.parseInt(tokens[4]);
					boolean missingSomething = false;

					if (!missingSomething && neededIron > 0) {
						if (!player.isEquipped("iron", neededIron)) {
							int amount = player.getNumberOfEquipped("iron");
							if (amount > 0) {
								player.drop("iron", amount);
								neededIron -= amount;
							}

							engine.say("I cannot #forge it without the missing "
								+ Grammar.quantityplnoun(
										neededIron, "iron")
								+ ".");
							missingSomething = true;
						} else {
							player.drop("iron", neededIron);
							neededIron = 0;
						}
					}

					if (!missingSomething && neededWoodLogs > 0) {
						if (!player.isEquipped("wood", neededWoodLogs)) {
							int amount = player.getNumberOfEquipped("wood");
							if (amount > 0) {
								player.drop("wood", amount);
								neededWoodLogs -= amount;
							}

							engine.say("How do you expect me to #forge it without missing "
								+ Grammar.quantityplnoun(neededWoodLogs, "wood log")
								+ " for the fire?");
							missingSomething = true;
						} else {
							player.drop("wood", neededWoodLogs);
							neededWoodLogs = 0;
						}
					}

					if (!missingSomething && neededGoldBars > 0) {
						if (!player.isEquipped("gold bar", neededGoldBars)) {
							int amount = player.getNumberOfEquipped("gold bar");
							if (amount > 0) {
								player.drop("gold bar", amount);
								neededGoldBars -= amount;
							}
							engine.say("I must pay a bill to spirits in order to cast the enchantment over the sword. I need "
									+ Grammar.quantityplnoun(neededGoldBars, "gold bar") + " more.");
							missingSomething = true;
						} else {
							player.drop("gold bar", neededGoldBars);
							neededGoldBars = 0;
						}
					}

					if (!missingSomething && neededGiantHearts > 0) {
						if (!player.isEquipped("giant heart", neededGiantHearts)) {
							int amount = player.getNumberOfEquipped("giant heart");
							if (amount > 0) {
								player.drop("giant heart", amount);
								neededGiantHearts -= amount;
							}
							engine.say("It is the base element of the enchantment. I need "
								+ Grammar.quantityplnoun(neededGiantHearts, "giant heart") + " still.");
							missingSomething = true;
						} else {
							player.drop("giant heart", neededGiantHearts);
							neededGiantHearts = 0;
						}
					}

					if (player.hasKilled("giant") && !missingSomething) {
						engine.say("You've brought everything I need to make the immortal sword, and what is more, you are strong enough to handle it. Come back in "
							+ REQUIRED_MINUTES
							+ " minutes and it will be ready.");
						player.setQuest(QUEST_SLOT, "forging;" + System.currentTimeMillis());
					} else {
						if (!player.hasKilled("giant") && !missingSomething) {
							engine.say("Did you really get those giant hearts yourself? I don't think so! This powerful sword can only be given to those that are strong enough to kill a #giant.");
						}

						player.setQuest(QUEST_SLOT,
							"start;"
							+ (REQUIRED_IRON - neededIron)
							+ ";"
							+ (REQUIRED_WOOD - neededWoodLogs)
							+ ";"
							+ (REQUIRED_GOLD_BAR - neededGoldBars)
							+ ";"
							+ (REQUIRED_GIANT_HEART - neededGiantHearts));
					}
				}
			});

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
					return player.hasQuest(QUEST_SLOT)
							&& player.getQuest(QUEST_SLOT).startsWith(
									"forging;");
				}
			}, ConversationStates.IDLE, null, new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC engine) {

					String[] tokens = player.getQuest(QUEST_SLOT).split(";");
					
					long delay = REQUIRED_MINUTES * MathHelper.MILLISENCONDS_IN_ONE_MINUTE; 
					long timeRemaining = (Long.parseLong(tokens[1]) + delay)
							- System.currentTimeMillis();

					if (timeRemaining > 0L) {
						engine.say("I haven't finished forging the sword. Please check back in "
							+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
							+ ".");
						return;
					}

					engine.say("I have finished forging the mighty immortal sword. You deserve this. Now I'm going to have a long rest, so, goodbye!");
					player.addXP(15000);
					player.addKarma(25);
					Item magicSword = SingletonRepository.getEntityManager().getItem("immortal sword");
					magicSword.setBoundTo(player.getName());
					player.equip(magicSword, true);
					player.notifyWorldAboutChanges();
					player.setQuest(QUEST_SLOT, "done");
				}
			});

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("forge", "missing"), 
			new QuestStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
					String[] tokens = player.getQuest(QUEST_SLOT).split(";");

					int neededIron = REQUIRED_IRON
							- Integer.parseInt(tokens[1]);
					int neededWoodLogs = REQUIRED_WOOD
							- Integer.parseInt(tokens[2]);
					int neededGoldBars = REQUIRED_GOLD_BAR
							- Integer.parseInt(tokens[3]);
					int neededGiantHearts = REQUIRED_GIANT_HEART
							- Integer.parseInt(tokens[4]);

					engine.say("I will need " + neededIron + " #iron, "
							+ neededWoodLogs + " #wood logs, "
							+ neededGoldBars + " #gold bars and "
							+ neededGiantHearts + " #giant hearts.");
				}
			});

		npc.add(
			ConversationStates.ANY,
			"iron",
			null,
			ConversationStates.ATTENDING,
			"Collect some iron ore from the mines which are rich in minerals.",
			null);

		npc.add(ConversationStates.ANY, "wood", null,
				ConversationStates.ATTENDING,
				"The forest is full of wood logs.", null);
		npc.add(ConversationStates.ANY, "gold", null,
				ConversationStates.ATTENDING,
				"A smith in Ados can forge the gold into gold bars for you.",
				null);
		npc.add(
			ConversationStates.ANY,
			"giant",
			null,
			ConversationStates.ATTENDING,
			"There are ancient stories of giants living in the mountains at the north of Semos and Ados.",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}

	@Override
	public List<String> getHistory(Player player) {
		List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		String questState = player.getQuest(QUEST_SLOT);
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
}
