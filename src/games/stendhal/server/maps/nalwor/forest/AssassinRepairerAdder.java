/***************************************************************************
 *                    Copyright © 2020-2024 - Stendhal                     *
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.SoundID;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.BreakableWeapon;
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


/**
 * Functions for repairing items that are "worn" or "broken".
 *
 * TODO:
 *   - merge functionality with games.stendhal.server.entity.npc.behaviour.adder.RepairerAdder
 *   - add behavior for repairing "broken" items (more expensive than just "worn")
 */
public class AssassinRepairerAdder {

	private static final Logger logger = Logger.getLogger(AssassinRepairerAdder.class);

	/**
	 * Prices for repairing items.
	 *
	 * TODO:
	 *   - move to behavior
	 */
	private Map<String, Integer> priceList;

	/** Subject item name of current conversation. */
	private String currentRepairItem = null;
	/** Subject number of items to repair of current conversation. */
	private Integer currentRepairCount = null;
	/** Subject fee of current conversation. */
	private Integer currentRepairFee = null;

	/**
	 * IDs for responses to repair requests.
	 *
	 * TODO:
	 *   - move to behavior
	 */
	public static enum ResponseId {
		DENIED, // player does not satisfy preconditions
		UNDECLARED, // player did not specify which item to repair
		NO_REPAIR, // NPC does not repair requested item
		NO_CARRY, // player is not carrying requested item
		NO_AFFORD, // player doesn't have enough money to pay for repair
		SAY_COUNT, // NPC should say how many items player is carrying that need repair
		REJECT_REPAIR, // player does not confirm repair after request
		DROPPED, // player dropped item after request
		REPAIR_DONE, // repair was completed
		ERROR; // an unknown error occurred
	}

	/** Responses for repair requests conditions. */
	final Map<ResponseId, Object> replies = new HashMap<ResponseId, Object>() {{
		put(ResponseId.DENIED, "Only members of the assassins guild can have items repaired.");
		put(ResponseId.UNDECLARED, "Please tell me what you would like repaired.");
		put(ResponseId.NO_REPAIR, null);
		put(ResponseId.NO_CARRY, null);
		put(ResponseId.NO_AFFORD, "You don't have enough money.");
		put(ResponseId.SAY_COUNT, null);
		put(ResponseId.REJECT_REPAIR, "Good luck then. Remember, once they break, they can't be repaired.");
		put(ResponseId.DROPPED, "Did you drop the item?");
		put(ResponseId.REPAIR_DONE, null);
		put(ResponseId.ERROR, "It appears I am unable to process the transaction. I'm sorry.");
	}};


	/**
	 * Adds repairer behavior to an NPC.
	 *
	 * @param repairer
	 *   NPC repairer.
	 * @param priceList
	 *   Prces for reparation.
	 */
	public void add(final AssassinRepairer repairer, final Map<String, Integer> priceList) {
		//this.repairer = repairer;
		this.priceList = priceList;


		// requests repair but does not meet preconditions
		repairer.add(ConversationStates.ATTENDING,
				ConversationPhrases.REPAIR_MESSAGES,
				new NotCondition(new PlayerHasItemWithHimCondition("assassins id")),
				ConversationStates.ATTENDING,
				//"Only members of the assassins guild can have their #'auto crossbows' repaired.",
				getReply(ResponseId.DENIED),
				null);

		// meets requirements and does meet preconditions
		repairer.add(ConversationStates.ATTENDING,
				ConversationPhrases.REPAIR_MESSAGES,
				new PlayerHasItemWithHimCondition("assassins id"),
				ConversationStates.QUESTION_2,
				null,
				requestRepairAction(repairer));

		// changes mind / does not want repair
		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				getReply(ResponseId.REJECT_REPAIR),
				null);

		// player dropped item before accepting
		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						needsRepairCondition(),
						itemsDroppedCondition()),
				ConversationStates.ATTENDING,
				getReply(ResponseId.DROPPED),
				null);

		// this should not happen
		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				feeNotSetCondition(),
				ConversationStates.ATTENDING,
				getReply(ResponseId.ERROR),
				null);

		// wants repair but doesn't have enough money
		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						needsRepairCondition(),
						new NotCondition(canAffordCondition())),
				ConversationStates.ATTENDING,
				getReply(ResponseId.NO_AFFORD),
				null);

		// wants repair and does have enough money
		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						needsRepairCondition(),
						canAffordCondition()),
				ConversationStates.ATTENDING,
				null,
				repairAction());
	}

	/**
	 * Resets subjects of current conversation.
	 */
	private void reset() {
		currentRepairItem = null;
		currentRepairCount = null;
		currentRepairFee = null;
	}

	/**
	 * Adds/Changes a reply for a certain condition.
	 *
	 * TODO:
	 *   - move to behavior (maybe)
	 *
	 * @param id
	 *   Condition ID.
	 * @param phrases
	 *   Replies for condition.
	 */
	public void setReply(final ResponseId id, final String... phrases) {
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

	/**
	 * Retrieves reply for certain condition.
	 *
	 * TODO:
	 *   - move to behavior (maybe)
	 *
	 * @param id
	 *   Condition ID.
	 * @param plural
	 *   {@code true} if multiple items are to be repaired.
	 * @return
	 *   NPC response to condition.
	 */
	@SuppressWarnings("unchecked")
	private String getReply(final ResponseId id, final boolean plural) {
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

	/**
	 * Retrieves reply for certain condition.
	 *
	 * TODO:
	 *   - move to behavior (maybe)
	 *
	 * @param id
	 *   Condition ID.
	 * @return
	 *   NPC response to condition.
	 */
	private String getReply(final ResponseId id) {
		return getReply(id, false);
	}

	/**
	 * Sets subject item name of current conversation.
	 *
	 * @param itemName
	 *   Name of item.
	 */
	private void setRepairItem(final String itemName) {
		for (final String repairable: priceList.keySet()) {
			if (itemName.toLowerCase(Locale.ENGLISH).equals(repairable.toLowerCase(Locale.ENGLISH))) {
				currentRepairItem = repairable;
				return;
			}
		}

		currentRepairItem = itemName;
	}

	/**
	 * Checks if item is included in NPC's list of repairable items.
	 *
	 * @param item
	 *   Name of item.
	 * @return
	 *   {@code true} if repairing item is supported.
	 */
	private boolean canRepair(final String item) {
		for (String bow: priceList.keySet()) {
			if (item.toLowerCase(Locale.ENGLISH).equals(bow.toLowerCase(Locale.ENGLISH))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Retrieves number of items player is carrying that need repair.
	 *
	 * @param player
	 *   Player requesting repair.
	 * @return
	 *   Number of "worn" items player is carrying.
	 */
	private int getWornItemsCount(final Player player) {
		if (!canRepair(currentRepairItem)) {
			return 0;
		}
		int count = 0;
		for (final Item item: player.getAllEquipped(currentRepairItem)) {
			if (((BreakableWeapon) item).isUsed()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Sets subject number of items to repair of current conversation.
	 *
	 * @param player
	 *   Player requesting repair.
	 */
	private void setRepairCount(final Player player) {
		currentRepairCount = getWornItemsCount(player);
	}

	/**
	 * Sets subject fee of current conversation.
	 */
	private void calculateRepairFee() {
		for (final String item: priceList.keySet()) {
			if (currentRepairItem.toLowerCase(Locale.ENGLISH).equals(item.toLowerCase(Locale.ENGLISH))) {
				currentRepairFee = currentRepairCount * (priceList.get(currentRepairItem));
				return;
			}
		}
	}

	/**
	 * Creates action for requesting item(s) repair.
	 *
	 * @param repairer
	 *   NPC repairer.
	 * @return
	 *   Action for requesting repair.
	 */
	private ChatAction requestRepairAction(final AssassinRepairer repairer) {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				final int repairables = repairer.getNumberOfRepairables();

				String request = sentence.getTrimmedText();
				if (ConversationPhrases.REPAIR_MESSAGES.contains(request.toLowerCase(Locale.ENGLISH))) {
					if (repairables > 1) {
						repairer.say(getReply(ResponseId.UNDECLARED));
						repairer.setCurrentState(ConversationStates.ATTENDING);
						return;
					}

					// player does not need to specify item name if repairer only repairs one item
					request = request + " " + repairer.getFirstRepairable();
				}

				for (final String rWord: ConversationPhrases.REPAIR_MESSAGES) {
					if (request.startsWith(rWord)) {
						request = request.substring(rWord.length() + 1);
						break;
					}
				}

				setRepairItem(request);
				setRepairCount(player);

				if (currentRepairCount == null) {
					String cannotRepairReply = getReply(ResponseId.NO_REPAIR);
					if (cannotRepairReply == null) {
						cannotRepairReply = "I do not repair " + Grammar.plural(currentRepairItem) + ".";
					}

					repairer.say(cannotRepairReply);
					repairer.setCurrentState(ConversationStates.ATTENDING);
					return;
				} else if (currentRepairCount < 1) {
					String notCarryingReply = getReply(ResponseId.NO_CARRY);
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
					sayCountReply = getReply(ResponseId.SAY_COUNT, true);
				} else {
					sayCountReply = getReply(ResponseId.SAY_COUNT);
				}

				if (sayCountReply == null) {
					final StringBuilder sb = new StringBuilder("You have " + Integer.toString(currentRepairCount) + " used " + currentRepairItem); //Grammar.plnoun(usedBows, bowType)); // FIXME: formats name all lowercase
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

	/**
	 * Creates action for executing item(s) repair.
	 *
	 * @return
	 *   Action for executing repair.
	 */
	private ChatAction repairAction() {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser repairer) {
				//final int usedBows = getUsedBowsCount(player);
				player.drop("money", currentRepairFee);

				for (final Item bow: player.getAllEquipped(currentRepairItem)) {
					final BreakableWeapon breakable = (BreakableWeapon) bow;
					if (breakable.isUsed()) {
						breakable.repair();
					}
				}

				final boolean multiple = currentRepairCount > 1;

				String doneReply;
				if (multiple) {
					doneReply = getReply(ResponseId.REPAIR_DONE, true);
				} else {
					doneReply = getReply(ResponseId.REPAIR_DONE);
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

	/**
	 * Creates condition to check if fee has been determined.
	 *
	 * @return
	 *   Condition.
	 */
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

	/**
	 * Creates condition to check if player can afford fee.
	 *
	 * @return
	 *   Condition.
	 */
	private ChatCondition canAffordCondition() {
		return new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity repairer) {
				return player.isEquipped("money", currentRepairFee);
			}
		};
	}

	/**
	 * Creates condition to check if player is carrying any "worn" items.
	 *
	 * @return
	 *   Condition.
	 */
	private ChatCondition needsRepairCondition() {
		return new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, Entity repairer) {
				return currentRepairCount != null && currentRepairCount > 0;
			}
		};
	}

	/**
	 * Creates condition to check if player dropped item after requesting repair.
	 *
	 * @return
	 *   Condition.
	 */
	private ChatCondition itemsDroppedCondition() {
		return new ChatCondition() {
			@Override
			public boolean fire(Player player, Sentence sentence, Entity npc) {
				return getWornItemsCount(player) < currentRepairCount;
			}
		};
	}


	/**
	 * NPC entity that supports repairing items.
	 *
	 * TODO:
	 *   - move to separate class file in package {@code games.stendhal.server.entity.npc}.
	 */
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
		 *   Number of supported item types.
		 */
		public int getNumberOfRepairables() {
			return repairList.size();
		}

		/**
		 * Retrieves the first item name from repair list.
		 *
		 * FIXME: appears to be incomplete as only first item name in repair list can be retrieved
		 *
		 * @return
		 *   Name of first item in list of items that can be repaired.
		 */
		@SuppressWarnings("unchecked")
		public String getFirstRepairable() {
			return repairList.keySet().toArray(new String[] {})[0];
		}
	}
}
