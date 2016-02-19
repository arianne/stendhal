/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
 * <li> Baldemar checks if you have ever killed a black giant alone, or not
 * <li> Baldemar forges the shield for you
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li> mithril shield
 * <li> 95000 XP
 * <li> some karma (25)
 * </ul>
 * 
 * 
 * REPETITIONS:
 * <ul>
 * <li> None.
 * </ul>
 */
public class StuffForBaldemar extends AbstractQuest {

	static final String TALK_NEED_KILL_GIANT = "This shield can only be given to those who have killed a black giant, and without the help of others.";

	private static Map<Integer, ItemData> neededItems = initNeededItems();
	private static Map<Integer, ItemData> initNeededItems() {
		neededItems = new TreeMap<Integer, ItemData>();
		ItemData data;

		data = new ItemData("mithril bar", REQUIRED_MITHRIL_BAR,
				"I cannot #forge it without the missing ",
				". After all, this IS a mithril shield.");
		neededItems.put(1, data);

		data = new ItemData("obsidian",	REQUIRED_OBSIDIAN,
				"I need several gems to grind into dust to mix with the mithril. I need ",
				" still.");
		neededItems.put(2, data);

		data = new ItemData("diamond", REQUIRED_DIAMOND,
				"I need several gems to grind into dust to mix with the mithril. I need ",
				" still.");
		neededItems.put(3, data);
		
		data = new ItemData("emerald", REQUIRED_EMERALD,
				"I need several gems to grind into dust to mix with the mithril. I need ",
				" still.");
		neededItems.put(4, data);
		
		data = new ItemData("carbuncle", REQUIRED_CARBUNCLE,
				"I need several gems to grind into dust to mix with the mithril. I need ",
				" still.");
		neededItems.put(5, data);
		
		data = new ItemData("sapphire",	REQUIRED_SAPPHIRE,
				"I need several gems to grind into dust to mix with the mithril. I need ",
				" still.");
		neededItems.put(6, data);

		data = new ItemData("black shield",	REQUIRED_BLACK_SHIELD,
				"I need ",
				" to form the framework for your new shield.");
		neededItems.put(7, data);
		
		data = new ItemData("magic plate shield", REQUIRED_MAGIC_PLATE_SHIELD,
				"I need ",
				" for the pieces and parts for your new shield.");
		neededItems.put(8, data);
	
		data = new ItemData("gold bar",	REQUIRED_GOLD_BAR,
				"I need ",
				" to melt down with the mithril and iron.");
		neededItems.put(9, data);

		data = new ItemData("iron",	REQUIRED_IRON,
				"I need ",
				" to melt down with the mithril and gold.");
		neededItems.put(10, data);

		data = new ItemData("black pearl", REQUIRED_BLACK_PEARL,
				"I need ",
				" to crush into fine powder to sprinkle onto shield to give it a nice sheen.");
		neededItems.put(11, data);

		data = new ItemData("shuriken",	REQUIRED_SHURIKEN,
				"I need ",
				" to melt down with the mithril, gold and iron. It is a 'secret' ingredient that only you and I know about. ;)");
		neededItems.put(12, data);

		data = new ItemData("marbles", REQUIRED_MARBLES,
				"My son wants some new toys. I need ",
				" still.");
		neededItems.put(13, data);

		data = new ItemData("snowglobe", REQUIRED_SNOWGLOBE,
				"I just LOVE those trinkets from athor. I need ",
				" still.");
		neededItems.put(14, data);
		return neededItems;
	}

	protected static final class ItemData {

		private int neededAmount;
		private final String itemName;
		private final String itemPrefix;
		private final String itemSuffix;
		private final int requiredAmount;

		public ItemData(final String name, final int needed, final String prefix, final String suffix) {
			this.requiredAmount = needed;
			this.neededAmount = needed;
			this.itemName = name;
			this.itemPrefix = prefix;
			this.itemSuffix = suffix;
		}

		public int getStillNeeded() {
			return neededAmount;
		}

		public void setAmount(final int needed) {
			neededAmount = needed;
		}

		public String getName() {
			return itemName;
		}

		public void subAmount(final String string) {
			subAmount(Integer.parseInt(string));
		}

		public String getAnswer() {
			return itemPrefix
				+ Grammar.quantityplnoun(
						neededAmount, itemName, "a")
				+ itemSuffix;
		}

		public int getRequired() {
			return requiredAmount;
		}

		public int getAlreadyBrought() {
			return requiredAmount - neededAmount;
		}

		public void subAmount(final int amount) {
			neededAmount -= amount;
		}

		public void resetAmount() {
			neededAmount = requiredAmount;
		}
	}

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

	private static final String I_WILL_NEED_MANY_THINGS = "I will need many, many things: ";

	private static final String IN_EXACT_ORDER = "Come back when you have them in the same #exact order!";

	private static final int REQUIRED_MINUTES = 10;

	private static final String QUEST_SLOT = "mithrilshield_quest";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Baldemar");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.QUEST_OFFERED, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					if (!player.hasQuest(QUEST_SLOT) || "rejected".equals(player.getQuest(QUEST_SLOT))) {
						raiser.say("I can forge a shield made from mithril along with several other items. Would you like me to do that?");
					} else if (player.isQuestCompleted(QUEST_SLOT)) {
						raiser.say("I would prefer you left me to my entertainment.");
						raiser.setCurrentState(ConversationStates.ATTENDING);
					} else {
						raiser.say("Why are you bothering me when you haven't completed your quest yet?");
						raiser.setCurrentState(ConversationStates.ATTENDING);
					}
				}
			});

		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					String need = I_WILL_NEED_MANY_THINGS + itemsStillNeeded(player) + ". " + IN_EXACT_ORDER;
					raiser.say(need);
					player.setQuest(QUEST_SLOT, "start;0;0;0;0;0;0;0;0;0;0;0;0;0;0");

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
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestStateStartsWithCondition(QUEST_SLOT, "start")),
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					final String[] tokens = player.getQuest(QUEST_SLOT).split(";");
					
					int idx1 = 1;
					for (ItemData itemdata : neededItems.values()) {
							itemdata.resetAmount();
							itemdata.subAmount(tokens[idx1]);
							idx1++;
					}

					boolean missingSomething = false;

					int size = neededItems.size();
					for (int idx = 1; !missingSomething && idx <= size; idx++) {
						ItemData itemData = neededItems.get(idx);
						missingSomething = proceedItem(player, raiser,
								itemData);
					}
					
					if (player.hasKilledSolo("black giant") && !missingSomething) {
						raiser.say("You've brought everything I need to forge the shield. Come back in "
							+ REQUIRED_MINUTES
							+ " minutes and it will be ready.");
						player.setQuest(QUEST_SLOT, "forging;" + System.currentTimeMillis());
					} else {
						if (!player.hasKilledSolo("black giant") && !missingSomething) {
							raiser.say(TALK_NEED_KILL_GIANT);
						}

						StringBuilder sb = new StringBuilder(30);
						sb.append("start");
						for (ItemData id : neededItems.values()) {
							sb.append(";");
							sb.append(id.getAlreadyBrought());
						}
						player.setQuest(QUEST_SLOT, sb.toString());
					}
				}

				private boolean proceedItem(final Player player,
						final EventRaiser engine, final ItemData itemData) {
					if (itemData.getStillNeeded() > 0) {
						
						if (player.isEquipped(itemData.getName(), itemData.getStillNeeded())) {
							player.drop(itemData.getName(), itemData.getStillNeeded());
							itemData.setAmount(0);
						} else {
							final int amount = player.getNumberOfEquipped(itemData.getName());
							if (amount > 0) {
								player.drop(itemData.getName(), amount);
								itemData.subAmount(amount);
							}

							engine.say(itemData.getAnswer());
							return true;
						}
					}
					return false;
				}
			});

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStateStartsWithCondition(QUEST_SLOT, "forging;")),
				ConversationStates.IDLE, null, new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {

					final String[] tokens = player.getQuest(QUEST_SLOT).split(";");
					
					final long delay = REQUIRED_MINUTES * MathHelper.MILLISECONDS_IN_ONE_MINUTE; 
					final long timeRemaining = Long.parseLong(tokens[1]) + delay
							- System.currentTimeMillis();

					if (timeRemaining > 0L) {
						raiser.say("I haven't finished forging your shield. Please check back in "
							+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
							+ ".");
						return;
					}

					raiser.say("I have finished forging your new mithril shield. Enjoy. Now I will see what Trillium has stored behind the counter for me. ;)");
					player.addXP(95000);
					player.addKarma(25);
					final Item mithrilshield = SingletonRepository.getEntityManager().getItem("mithril shield");
					mithrilshield.setBoundTo(player.getName());
					player.equipOrPutOnGround(mithrilshield);
					player.notifyWorldAboutChanges();
					player.setQuest(QUEST_SLOT, "done");
				}
			});

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("forge", "missing"), 
			new QuestStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					final String questState = player.getQuest(QUEST_SLOT);
					if (!broughtAllItems(questState)) {
						raiser.say("I need " + itemsStillNeeded(player) + ".");
					} else {
						if(!player.hasKilledSolo("black giant")) {
							raiser.say(TALK_NEED_KILL_GIANT);
						}
					}
				}
			});
	}

	private String itemsStillNeeded(final Player player) {
		List<String> neededItemsWithAmounts = neededItemsWithAmounts(player);

		StringBuilder all = new StringBuilder();
		for (int i = 0; i < neededItemsWithAmounts.size(); i++) {
			if (i != 0 && i == neededItemsWithAmounts.size() - 1) {
				all.append(" and ");
			}
			all.append(neededItemsWithAmounts.get(i));
			if (i < neededItemsWithAmounts.size() - 2) {
				all.append(", ");
			}
		}
		return all.toString();
	}

	private List<String> neededItemsWithAmounts(final Player player) {
		int[] broughtItems = broughtItems(player);
		List<String> neededItemsWithAmounts = new LinkedList<>();
		for (int i = 1; i <= neededItems.size(); i++) {
			ItemData item = neededItems.get(i);
			int required = item.requiredAmount;
			int brought = broughtItems[i - 1];
			int neededAmount = required - brought;
			if (neededAmount > 0) {
				neededItemsWithAmounts.add(neededItem(neededAmount, item.itemName));
			}
		}

		return neededItemsWithAmounts;
	}

	private int[] broughtItems(final Player player) {
		int[] brought = new int[neededItems.size()];
		if (player.getQuest(QUEST_SLOT) != null) {
			String[] broughtTokens = player.getQuest(QUEST_SLOT).split(";");
			for (int i = 1; i < broughtTokens.length; i++) {
				brought[i - 1] = Integer.parseInt(broughtTokens[i]);
			}
		} else {
			Arrays.fill(brought, 0);
		}
		return brought;
	}

	private String neededItem(int neededAmount, String itemName) {
		return Grammar.quantityplnoun(neededAmount, itemName, "a");
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Stuff for Baldemar",
				"Baldemar, a friendly mithrilbourgh elite wizard, will forge a special shield.",
				false);
		step_1();
		step_2();
		step_3();
	}

	@Override
	public String getName() {
		return "StuffForBaldemar";
	}

	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}			
			final String questState = player.getQuest(QUEST_SLOT);
			res.add("I met Baldemar in the magic theater.");
			if (questState.equals("rejected")) {
				res.add("I'm not interested in his ideas about shields made from mithril.");
				return res;
			} 
			res.add("Baldemar asked me to bring him many things.");
			if(questState.startsWith("start") && !broughtAllItems(questState)){
				res.add("I still need to bring " + itemsStillNeeded(player) + ", in this order.");
			} else if (broughtAllItems(questState) || !questState.startsWith("start")) {
				res.add("I took all the special items to Baldemar.");
			}
			if(broughtAllItems(questState) && !player.hasKilledSolo("black giant")){
				res.add("I will need to bravely face a black giant alone, before I am worthy of this shield.");
			}
			if (questState.startsWith("forging")) {
				res.add("Baldemar is forging my mithril shield!");
			} 
			if (isCompleted(player)) {
				res.add("I brought Baldemar many items, killed a black giant solo, and he forged me a mithril shield.");
			}
			return res;
	}

	private boolean broughtAllItems(final String questState) {
		return "start;20;1;1;5;10;10;1;1;10;20;10;20;15;1".equals(questState);
	}

	@Override
	public int getMinLevel() {
		return 100;
	}

	@Override
	public String getNPCName() {
		return "Baldemar";
	}

	@Override
	public String getRegion() {
		return Region.FADO_CAVES;
	}
}
