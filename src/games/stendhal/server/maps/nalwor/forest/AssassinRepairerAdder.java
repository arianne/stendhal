/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.nalwor.forest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.SoundID;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.BreakableItem;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;
import marauroa.common.Pair;


public class AssassinRepairerAdder {

	private static final Logger logger = Logger.getLogger(AssassinRepairerAdder.class);

	//private SpeakerNPC repairer;
	private Map<String, Integer> priceList;

	private String currentRepairItem = null;
	private Integer currentRepairCount = null;
	private Integer currentRepairFee = null;

	private static final List<String> repairPhrases = Arrays.asList("repair", "fix");

	// reply IDs
	public final static String ID_DENIED = "denied";
	public final static String ID_UNDECLARED = "undeclared";
	public final static String ID_NO_REPAIR = "cannot_repair";
	public final static String ID_NO_CARRY = "not_carrying";
	public final static String ID_NO_AFFORD = "cannot_afford";
	public final static String ID_SAY_COUNT = "say_count";
	public final static String ID_REJECT_REPAIR = "reject_repair";
	public final static String ID_DROPPED = "dropped";
	public final static String ID_REPAIR_DONE = "repair_done";
	public final static String ID_ERROR = "error";

	final Map<String, Object> replies = new HashMap<String, Object>() {{
		put(ID_DENIED, "Only members of the assassins guild can have items repaired.");
		put(ID_UNDECLARED, "Please tell me what you would repaired.");
		put(ID_NO_REPAIR, null);
		put(ID_NO_CARRY, null);
		put(ID_NO_AFFORD, "You don't have enough money.");
		put(ID_SAY_COUNT, null);
		put(ID_REJECT_REPAIR, "Good luck then. Remember, once they break, they can't be repaired.");
		put(ID_DROPPED, "Did you drop the item?");
		put(ID_REPAIR_DONE, null);
		put(ID_ERROR, "It appears I am unable to process the transaction. I'm sorry.");
	}};


	public void add(final AssassinRepairer repairer, final Map<String, Integer> priceList) {
		//this.repairer = repairer;
		this.priceList = priceList;


		repairer.add(ConversationStates.ATTENDING,
				repairPhrases,
				new NotCondition(new PlayerHasItemWithHimCondition("assassins id")),
				ConversationStates.ATTENDING,
				//"Only members of the assassins guild can have their #'auto crossbows' repaired.",
				getReply("denied"),
				null);

		repairer.add(ConversationStates.ATTENDING,
				repairPhrases,
				new PlayerHasItemWithHimCondition("assassins id"),
				ConversationStates.QUESTION_2,
				null,
				requestRepairAction(repairer));

		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				getReply(ID_REJECT_REPAIR),
				null);

		// player dropped item before accepting
		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(needsRepairCondition()),
				ConversationStates.ATTENDING,
				getReply(ID_DROPPED),
				null);

		// this should not happen
		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				feeNotSetCondition(),
				ConversationStates.ATTENDING,
				getReply(ID_ERROR),
				null);

		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						needsRepairCondition(),
						new NotCondition(canAffordCondition())),
				ConversationStates.ATTENDING,
				getReply(ID_NO_AFFORD),
				null);

		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						needsRepairCondition(),
						canAffordCondition()),
				ConversationStates.ATTENDING,
				null,
				repairAction());
	}

	private void reset() {
		currentRepairItem = null;
		currentRepairCount = null;
		currentRepairFee = null;
	}

	public void setReply(final String id, final String... phrases) {
		/*
		if (phrases.length > 2) {
			logger.warn("Maximum of 2 phrases allowed: singular & plural forms");
		}
		*/

		if (replies.containsKey(id)) {
			if (phrases.length == 1) {
				replies.put(id, phrases[0]);
			} else {
				replies.put(id, new Pair<String, String>(phrases[0], phrases[1]));

				if (phrases.length > 2) {
					logger.warn("Maximum of 2 phrases allowed: singular & plural forms");
				}
			}

			return;
		}

		logger.warn("Tried to add unused reply: " + id);
	}

	@SuppressWarnings("unchecked")
	private String getReply(final String id, final boolean plural) {
		final Object reply = replies.get(id);
		if (reply instanceof Pair) {
			if (plural) {
				return ((Pair<String, String>) reply).second();
			} else {
				return ((Pair<String, String>) reply).first();
			}
		}

		return (String) reply;
	}

	private String getReply(final String id) {
		return getReply(id, false);
	}

	private void setRepairItem(final String itemName) {
		for (final String repairable: priceList.keySet()) {
			if (itemName.toLowerCase().equals(repairable.toLowerCase())) {
				currentRepairItem = repairable;
				return;
			}
		}

		currentRepairItem = itemName;
	}

	private boolean canRepair(final String item) {
		for (String bow: priceList.keySet()) {
			if (item.toLowerCase().equals(bow.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	private void setRepairCount(final Player player) {
		if (!canRepair(currentRepairItem)) {
			return;
		}

		int count = 0;
		for (final Item item: player.getAllEquipped(currentRepairItem)) {
			if (((BreakableItem) item).isUsed()) {
				count++;
			}
		}

		currentRepairCount = count;
	}

	private void calculateRepairFee() {
		for (final String item: priceList.keySet()) {
			if (currentRepairItem.toLowerCase().equals(item.toLowerCase())) {
				currentRepairFee = currentRepairCount * (priceList.get(currentRepairItem));
				return;
			}
		}
	}

	private ChatAction requestRepairAction(final AssassinRepairer repairer) {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				final int repairables = repairer.getNumberOfRepairables();

				String request = sentence.getTrimmedText();
				if (repairPhrases.contains(request.toLowerCase())) {
					if (repairables > 1) {
						repairer.say(getReply(ID_UNDECLARED));
						repairer.setCurrentState(ConversationStates.ATTENDING);
						return;
					}

					// player does not need to specify item name if repairer only repairs one item
					request = request + " " + repairer.getFirstRepairable();
				}

				for (final String rWord: repairPhrases) {
					if (request.startsWith(rWord)) {
						request = request.substring(rWord.length() + 1);
						break;
					}
				}

				setRepairItem(request);
				setRepairCount(player);

				if (currentRepairCount == null) {
					String cannotRepairReply = getReply(ID_NO_REPAIR);
					if (cannotRepairReply == null) {
						cannotRepairReply = "I do not repair " + Grammar.plural(currentRepairItem) + ".";
					}

					repairer.say(cannotRepairReply);
					repairer.setCurrentState(ConversationStates.ATTENDING);
					return;
				} else if (currentRepairCount < 1) {
					String notCarryingReply = getReply(ID_NO_CARRY);
					if (notCarryingReply == null) {
						notCarryingReply = "You don't have any #'" + Grammar.plural(currentRepairItem) + "' that need repaired.";
					}

					repairer.say(notCarryingReply);
					repairer.setCurrentState(ConversationStates.ATTENDING);
					return;
				}

				calculateRepairFee();
				final boolean multiple = currentRepairCount > 1;

				String sayCountReply;
				if (multiple) {
					sayCountReply = getReply(ID_SAY_COUNT, true);
				} else {
					sayCountReply = getReply(ID_SAY_COUNT);
				}

				if (sayCountReply == null) {
					final StringBuilder sb = new StringBuilder("You have " + Integer.toString(currentRepairCount) + " used " + currentRepairItem); //Grammar.plnoun(usedBows, bowType)); // formats name all lowercase
					if (multiple) {
						sb.append("s");
					}
					sb.append(". I can repair ");
					if (multiple) {
						sb.append("them all");
					} else {
						sb.append("it");
					}
					sb.append(" for " + Integer.toString(currentRepairFee) + " money. Would you like me to do so?");

					sayCountReply = sb.toString();
				}

				repairer.say(sayCountReply);
			}
		};
	}

	private ChatAction repairAction() {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser repairer) {
				//final int usedBows = getUsedBowsCount(player);
				player.drop("money", currentRepairFee);

				for (final Item bow: player.getAllEquipped(currentRepairItem)) {
					final BreakableItem breakable = (BreakableItem) bow;
					if (breakable.isUsed()) {
						breakable.repair();
					}
				}

				final boolean multiple = currentRepairCount > 1;

				String doneReply;
				if (multiple) {
					doneReply = getReply(ID_REPAIR_DONE, true);
				} else {
					doneReply = getReply(ID_REPAIR_DONE);
				}

				if (doneReply == null) {
					doneReply = "Done! Your ";
					if (multiple) {
						doneReply += Grammar.plural(currentRepairItem) + " are ";
					} else {
						doneReply += currentRepairItem + " is ";
					}
					doneReply += "as good as new.";
				}

				/*
				if (currentRepairCount > 1) {
					ranger.say("Done! Your " + Grammar.plural(bowType) + " are as good as new.");
				} else {
					ranger.say("Done! Your " + bowType + " is as good as new.");
				}
				*/

				repairer.say(doneReply);
				repairer.addEvent(new SoundEvent(SoundID.COMMERCE, SoundLayer.CREATURE_NOISE));

				// reset item name, count, & fee back to null
				/*
				reset();
				*/
			}
		};
	}

	private ChatCondition feeNotSetCondition() {
		return new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity repairer) {
				if (currentRepairFee == null) {
					logger.error("Cannot create transaction, repair fee not set");
					return true;
				}

				return false;
			}
		};
	}

	private ChatCondition canAffordCondition() {
		return new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity repairer) {
				return player.isEquipped("money", currentRepairFee);
			}
		};
	}

	private ChatCondition needsRepairCondition() {
		return new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, Entity repairer) {
				return currentRepairCount != null && currentRepairCount > 0;
			}
		};
	}


	public class AssassinRepairer extends SpeakerNPC {

		private final Map<String, Integer> repairList;

		public AssassinRepairer(String name, final Map<String, Integer> repairList) {
			super(name);

			this.repairList = repairList;
		}

		@Override
		public void onGoodbye(final RPEntity attending) {
			// reset item name, count, & fee to null
			reset();
		}

		/**
		 * Retrieves number of item types that can be repaired by this NPC.
		 *
		 * @return
		 * 		Number of repairable item types.
		 */
		public int getNumberOfRepairables() {
			return repairList.size();
		}

		/**
		 * Retrieves the first item name from repair list.
		 *
		 * @return
		 * 		First item.
		 */
		@SuppressWarnings("unchecked")
		public String getFirstRepairable() {
			return repairList.keySet().toArray(new String[] {})[0];
		}
	}
}
