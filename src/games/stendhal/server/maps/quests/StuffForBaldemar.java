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
 * QUEST: The mithril shield forging.
 * 
 * PARTICIPANTS:
 * <ul>
 * <li> Baldemar, mithrilbourgh elite wizard, will forge a mithril shield.
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li> Baldemar tells you about shield.
 * <li> He offers to forge a mithril shield for you if you bring him what he
 * needs.
 * <li> You give him all he asks for.
 * <li> Baldemar forges the shield for you
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li> mithril shield
 * <li>95000 XP
 * </ul>
 * 
 * 
 * REPETITIONS:
 * <ul>
 * <li> None.
 * </ul>
 */
public class StuffForBaldemar extends AbstractQuest {
	private static final int REQUIRED_MITHRIL_BAR = 20;

	private static final int REQUIRED_OBSIDIAN = 1;

	private static final int REQUIRED_DIAMOND = 1;

	private static final int REQUIRED_EMERALD = 5;
	
	private static final int REQUIRED_CARBUNCLE = 10;

	private static final int REQUIRED_SAPPHIRE = 10;

	private static final int REQUIRED_BLACK_SHIELD = 1;

	private static final int REQUIRED_MAGIC_PLATE_SHIELD = 1;

	private static final int REQUIRED_GOLD_BAR = 10;

	private static final int REQUIRED_IRON = 20;

	private static final int REQUIRED_BLACK_PEARL = 10;

	private static final int REQUIRED_SHURIKEN = 20;
	
	private static final int REQUIRED_MARBLES = 15;

	private static final int REQUIRED_SNOWGLOBE = 1;
	
	private static final int REQUIRED_MINUTES = 10;

	private static final String QUEST_SLOT = "mithrilshield_quest";

	@Override
	public void init(final String name) {
		super.init(name, QUEST_SLOT);
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Baldemar");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.QUEST_OFFERED, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
					if (!player.hasQuest(QUEST_SLOT) || "rejected".equals(player.getQuest(QUEST_SLOT))) {
						engine.say("I can forge a shield made from mithril along with several other items. Would you like me to do that?");
					} else if (player.isQuestCompleted(QUEST_SLOT)) {
						engine.say("I would prefer you left me to my entertainment.");
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
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
					engine.say("I will need many, many things: "
						+ REQUIRED_MITHRIL_BAR
						+ " mithril bars, "
						+ REQUIRED_OBSIDIAN
						+ " obsidian, "
						+ REQUIRED_DIAMOND
						+ " diamond, "
						+ REQUIRED_EMERALD
						+ " emeralds," 
						+ REQUIRED_CARBUNCLE
						+ " carbuncles, "
						+ REQUIRED_SAPPHIRE
						+ " sapphires, "
						+ REQUIRED_BLACK_SHIELD
						+ " black shield, "
						+ REQUIRED_MAGIC_PLATE_SHIELD
						+ " magic plate shield, "
						+ REQUIRED_GOLD_BAR
						+ " gold bars, "
						+ REQUIRED_IRON
						+ " iron bars, "
						+ REQUIRED_BLACK_PEARL
						+ " black pearls," 
						+ REQUIRED_SHURIKEN
						+ " shuriken, "
						+ REQUIRED_MARBLES
						+ " marbles and "
						+ REQUIRED_SNOWGLOBE
						+ " snowglobe. Come back when you have them in the same #exact order!");
					player.setQuest(QUEST_SLOT, "start;0;0;0;0;0;0;0;0;0;0;0;0;0;0");
					player.addKarma(10);

				}
			});

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.IDLE,
			"I can't believe you are going to pass up this opportunity! You must be daft!!!",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));

		npc.addReply("exact",
			"As I have listed them here, you must provide them in that order.");
	}

	private void step_2() {
		/* Get the stuff. */
	}

	private void step_3() {

		final SpeakerNPC npc = npcs.get("Baldemar");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new QuestStateStartsWithCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
					final String[] tokens = player.getQuest(QUEST_SLOT).split(";");

					int neededMithrilBar = REQUIRED_MITHRIL_BAR
							- Integer.parseInt(tokens[1]);
					int neededObsidian = REQUIRED_OBSIDIAN
							- Integer.parseInt(tokens[2]);
					int neededDiamond = REQUIRED_DIAMOND
							- Integer.parseInt(tokens[3]);
					int neededEmerald = REQUIRED_EMERALD
							- Integer.parseInt(tokens[4]);
					int neededCarbuncle = REQUIRED_CARBUNCLE
							- Integer.parseInt(tokens[5]);
					int neededSapphire = REQUIRED_SAPPHIRE
							- Integer.parseInt(tokens[6]);
					int neededBlackShield = REQUIRED_BLACK_SHIELD
							- Integer.parseInt(tokens[7]);
					int neededMagicPlateShield = REQUIRED_MAGIC_PLATE_SHIELD
							- Integer.parseInt(tokens[8]);	
					int neededGoldBars = REQUIRED_GOLD_BAR
							- Integer.parseInt(tokens[9]);
					int neededIron = REQUIRED_IRON
							- Integer.parseInt(tokens[10]);
					int neededBlackPearl = REQUIRED_BLACK_PEARL
							- Integer.parseInt(tokens[11]);
					int neededShuriken = REQUIRED_SHURIKEN
							- Integer.parseInt(tokens[12]);
					int neededMarbles = REQUIRED_MARBLES
							- Integer.parseInt(tokens[13]);
					int neededSnowglobe = REQUIRED_SNOWGLOBE
							- Integer.parseInt(tokens[14]);
					boolean missingSomething = false;

					if (!missingSomething && (neededMithrilBar > 0)) {
						if (player.isEquipped("mithril bar", neededMithrilBar)) {
							player.drop("mithril bar", neededMithrilBar);
							neededMithrilBar = 0;
						} else {
							final int amount = player.getNumberOfEquipped("mithril bar");
							if (amount > 0) {
								player.drop("mithril bar", amount);
								neededMithrilBar -= amount;
							}

							engine.say("I cannot #forge it without the missing "
								+ Grammar.quantityplnoun(
										neededMithrilBar, "mithril bar")
								+ ". After all, this IS a mithril shield.");
							missingSomething = true;
						}
					}

					if (!missingSomething && (neededObsidian > 0)) {
						if (player.isEquipped("obsidian", neededObsidian)) {
							player.drop("obsidian", neededObsidian);
							neededObsidian = 0;
						} else {
							final int amount = player.getNumberOfEquipped("obsidian");
							if (amount > 0) {
								player.drop("obsidian", amount);
								neededObsidian -= amount;
							}

							engine.say("I need several gems to grind into dust to mix with the mithril. I need "
								+ Grammar.quantityplnoun(neededObsidian, "obsidian")
								+ " still.");
							missingSomething = true;
						}
					}

					if (!missingSomething && (neededDiamond > 0)) {
						if (player.isEquipped("diamond", neededDiamond)) {
							player.drop("diamond", neededDiamond);
							neededDiamond = 0;
						} else {
							final int amount = player.getNumberOfEquipped("diamond");
							if (amount > 0) {
								player.drop("diamond", amount);
								neededDiamond -= amount;
							}
							engine.say("I need several gems to grind into dust to mix with the mithril. I need "
									+ Grammar.quantityplnoun(neededDiamond, "diamond") + " still.");
							missingSomething = true;
						}
					}

					if (!missingSomething && (neededEmerald > 0)) {
						if (player.isEquipped("emerald", neededEmerald)) {
							player.drop("emerald", neededEmerald);
							neededEmerald = 0;
						} else {
							final int amount = player.getNumberOfEquipped("emerald");
							if (amount > 0) {
								player.drop("emerald", amount);
								neededEmerald -= amount;
							}
							engine.say("I need several gems to grind into dust to mix with the mithril. I need "
								+ Grammar.quantityplnoun(neededEmerald, "emerald") + " still.");
							missingSomething = true;
						}
					}

					if (!missingSomething && (neededCarbuncle > 0)) {
						if (player.isEquipped("carbuncle", neededCarbuncle)) {
							player.drop("carbuncle", neededCarbuncle);
							neededCarbuncle = 0;
						} else {
							final int amount = player.getNumberOfEquipped("carbuncle");
							if (amount > 0) {
								player.drop("carbuncle", amount);
								neededCarbuncle -= amount;
							}
							engine.say("I need several gems to grind into dust to mix with the mithril. I need "
								+ Grammar.quantityplnoun(neededCarbuncle, "carbuncle") + " still.");
							missingSomething = true;
						}
					}					

					if (!missingSomething && (neededSapphire > 0)) {
						if (player.isEquipped("sapphire", neededSapphire)) {
							player.drop("sapphire", neededSapphire);
							neededSapphire = 0;
						} else {
							final int amount = player.getNumberOfEquipped("sapphire");
							if (amount > 0) {
								player.drop("sapphire", amount);
								neededSapphire -= amount;
							}
							engine.say("I need several gems to grind into dust to mix with the mithril. I need "
								+ Grammar.quantityplnoun(neededSapphire, "sapphire") + " still.");
							missingSomething = true;
						}
					}					

					if (!missingSomething && (neededBlackShield > 0)) {
						if (player.isEquipped("black shield", neededBlackShield)) {
							player.drop("black shield", neededBlackShield);
							neededBlackShield = 0;
						} else {
							final int amount = player.getNumberOfEquipped("black shield");
							if (amount > 0) {
								player.drop("black shield", amount);
								neededBlackShield -= amount;
							}
							engine.say("I need "
								+ Grammar.quantityplnoun(neededBlackShield, "black shield") + " to form the framework for your new shield.");
							missingSomething = true;
						}
					}					

					if (!missingSomething && (neededMagicPlateShield > 0)) {
						if (player.isEquipped("magic plate shield", neededMagicPlateShield)) {
							player.drop("magic plate shield", neededMagicPlateShield);
							neededMagicPlateShield = 0;
						} else {
							final int amount = player.getNumberOfEquipped("magic plate shield");
							if (amount > 0) {
								player.drop("magic plate shield", amount);
								neededMagicPlateShield -= amount;
							}
							engine.say("I need "
								+ Grammar.quantityplnoun(neededMagicPlateShield, "magic plate shield") + " for the pieces and parts for your new shield.");
							missingSomething = true;
						}
					}					

					if (!missingSomething && (neededGoldBars > 0)) {
						if (player.isEquipped("gold bar", neededGoldBars)) {
							player.drop("gold bar", neededGoldBars);
							neededGoldBars = 0;
						} else {
							final int amount = player.getNumberOfEquipped("gold bar");
							if (amount > 0) {
								player.drop("gold bar", amount);
								neededGoldBars -= amount;
							}
							engine.say("I need "
								+ Grammar.quantityplnoun(neededGoldBars, "gold bar") + " to melt down with the mithril and iron.");
							missingSomething = true;
						}
					}					

					if (!missingSomething && (neededIron > 0)) {
						if (player.isEquipped("iron", neededIron)) {
							player.drop("iron", neededIron);
							neededIron = 0;
						} else {
							final int amount = player.getNumberOfEquipped("iron");
							if (amount > 0) {
								player.drop("iron", amount);
								neededIron -= amount;
							}
							engine.say("I need "
								+ Grammar.quantityplnoun(neededIron, "iron") + " to melt down with the mithril and gold.");
							missingSomething = true;
						}
					}						

					if (!missingSomething && (neededBlackPearl > 0)) {
						if (player.isEquipped("black pearl", neededBlackPearl)) {
							player.drop("black pearl", neededBlackPearl);
							neededBlackPearl = 0;
						} else {
							final int amount = player.getNumberOfEquipped("black pearl");
							if (amount > 0) {
								player.drop("black pearl", amount);
								neededBlackPearl -= amount;
							}
							engine.say("I need "
								+ Grammar.quantityplnoun(neededBlackPearl, "black pearl") + " to crush into fine powder to sprinkle onto shield to give it a nice sheen.");
							missingSomething = true;
						}
					}					

					if (!missingSomething && (neededShuriken > 0)) {
						if (player.isEquipped("shuriken", neededShuriken)) {
							player.drop("shuriken", neededShuriken);
							neededShuriken = 0;
						} else {
							final int amount = player.getNumberOfEquipped("shuriken");
							if (amount > 0) {
								player.drop("shuriken", amount);
								neededShuriken -= amount;
							}
							engine.say("I need "
								+ Grammar.quantityplnoun(neededShuriken, "shuriken") + " to melt down with the mithril, gold and iron. It is a 'secret' ingredient that only you and I know about. ;)");
							missingSomething = true;
						}
					}					

					if (!missingSomething && (neededMarbles > 0)) {
						if (player.isEquipped("marbles", neededMarbles)) {
							player.drop("marbles", neededMarbles);
							neededMarbles = 0;
						} else {
							final int amount = player.getNumberOfEquipped("marbles");
							if (amount > 0) {
								player.drop("marbles", amount);
								neededMarbles -= amount;
							}
							engine.say("My son wants some new toys. I need "
								+ Grammar.quantityplnoun(neededMarbles, "marbles") + " still.");
							missingSomething = true;
						}
					}						

					if (!missingSomething && (neededSnowglobe > 0)) {
						if (player.isEquipped("snowglobe", neededSnowglobe)) {
							player.drop("snowglobe", neededSnowglobe);
							neededSnowglobe = 0;
						} else {
							final int amount = player.getNumberOfEquipped("snowglobe");
							if (amount > 0) {
								player.drop("snowglobe", amount);
								neededSnowglobe -= amount;
							}
							engine.say("I just LOVE those trinkets from athor. I need "
								+ Grammar.quantityplnoun(neededSnowglobe, "snowglobe") + " still.");
							missingSomething = true;
						}
					}					
					
					if (player.hasKilled("black giant") && !missingSomething) {
						engine.say("You've brought everything I need to forge the shield. Come back in "
							+ REQUIRED_MINUTES
							+ " minutes and it will be ready.");
						player.setQuest(QUEST_SLOT, "forging;" + System.currentTimeMillis());
					} else {
						if (!player.hasKilled("black giant") && !missingSomething) {
							engine.say("This shield can only be given to those who have killed a black giant.");
						}

						player.setQuest(QUEST_SLOT,
							"start;"
							+ (REQUIRED_MITHRIL_BAR - neededMithrilBar)
							+ ";"
							+ (REQUIRED_OBSIDIAN - neededObsidian)
							+ ";"
							+ (REQUIRED_DIAMOND - neededDiamond)
							+ ";"
							+ (REQUIRED_EMERALD - neededEmerald)
							+ ";"
							+ (REQUIRED_CARBUNCLE - neededCarbuncle)
							+ ";"
							+ (REQUIRED_SAPPHIRE - neededSapphire)
							+ ";"
							+ (REQUIRED_BLACK_SHIELD - neededBlackShield)
							+ ";"
							+ (REQUIRED_MAGIC_PLATE_SHIELD - neededMagicPlateShield)
							+ ";"
							+ (REQUIRED_GOLD_BAR - neededGoldBars)
							+ ";"	
							+ (REQUIRED_IRON - neededIron)
							+ ";"
							+ (REQUIRED_BLACK_PEARL - neededBlackPearl)
							+ ";"
							+ (REQUIRED_SHURIKEN - neededShuriken)
							+ ";"
							+ (REQUIRED_MARBLES - neededMarbles)
							+ ";"						
							+ (REQUIRED_SNOWGLOBE - neededSnowglobe));
					}
				}
			});

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
					return player.hasQuest(QUEST_SLOT)
							&& player.getQuest(QUEST_SLOT).startsWith(
									"forging;");
				}
			}, ConversationStates.IDLE, null, new SpeakerNPC.ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {

					final String[] tokens = player.getQuest(QUEST_SLOT).split(";");
					
					final long delay = REQUIRED_MINUTES * MathHelper.MILLISECONDS_IN_ONE_MINUTE; 
					final long timeRemaining = (Long.parseLong(tokens[1]) + delay)
							- System.currentTimeMillis();

					if (timeRemaining > 0L) {
						engine.say("I haven't finished forging your shield. Please check back in "
							+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
							+ ".");
						return;
					}

					engine.say("I have finished forging your new mithril shield. Enjoy. Now I will see what Trillium has stored behind the counter for me. ;)");
					player.addXP(95000);
					player.addKarma(25);
					final Item mithrilshield = SingletonRepository.getEntityManager().getItem("mithril shield");
					mithrilshield.setBoundTo(player.getName());
					player.equip(mithrilshield, true);
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
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
					final String[] tokens = player.getQuest(QUEST_SLOT).split(";");

					final int neededMithrilBar = REQUIRED_MITHRIL_BAR
							- Integer.parseInt(tokens[1]);
					final int neededObsidian = REQUIRED_OBSIDIAN
							- Integer.parseInt(tokens[2]);
					final int neededDiamond = REQUIRED_DIAMOND
							- Integer.parseInt(tokens[3]);
					final int neededEmerald = REQUIRED_EMERALD
							- Integer.parseInt(tokens[4]);
					final int neededCarbuncle = REQUIRED_CARBUNCLE
							- Integer.parseInt(tokens[5]);
					final int neededSapphire = REQUIRED_SAPPHIRE
							- Integer.parseInt(tokens[6]);
					final int neededBlackShield = REQUIRED_BLACK_SHIELD
							- Integer.parseInt(tokens[7]);
					final int neededMagicPlateShield = REQUIRED_MAGIC_PLATE_SHIELD
							- Integer.parseInt(tokens[8]);
					final int neededGoldBars = REQUIRED_GOLD_BAR
							- Integer.parseInt(tokens[9]);
					final int neededIron = REQUIRED_IRON
							- Integer.parseInt(tokens[10]);
					final int neededBlackPearl = REQUIRED_BLACK_PEARL
							- Integer.parseInt(tokens[11]);
					final int neededShuriken = REQUIRED_SHURIKEN
							- Integer.parseInt(tokens[12]);
					final int neededMarbles = REQUIRED_MARBLES
							- Integer.parseInt(tokens[13]);
					final int neededSnowglobe = REQUIRED_SNOWGLOBE
							- Integer.parseInt(tokens[14]);

					
					engine.say("I will need " + neededMithrilBar + " mithril bars, "
							+ neededObsidian + " obsidian, "
							+ neededDiamond + " diamond, "
							+ neededEmerald + " emeralds, "
							+ neededCarbuncle + " carbuncles, "
							+ neededSapphire + " sapphires, "
							+ neededBlackShield + " black shield, "
							+ neededMagicPlateShield + " magic plate shield, "
							+ neededGoldBars + " gold bars, "
							+ neededIron + " iron bars, "
							+ neededBlackPearl + " black pearls, "
							+ neededShuriken + " shuriken, "
							+ neededMarbles + " marbles and "
							+ neededSnowglobe + " snowglobe");
				}
			});

	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
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
}
