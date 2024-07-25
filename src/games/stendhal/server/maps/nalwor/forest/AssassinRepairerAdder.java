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


	/**
	 * Adds repairer behavior to an NPC.
	 *
	 * @param repairer
	 *   Item repairing NPC.
	 */
	public void add(final AssassinRepairer repairer) {
		// requests repair but does not meet preconditions
		repairer.add(ConversationStates.ATTENDING,
				ConversationPhrases.REPAIR_MESSAGES,
				new NotCondition(new PlayerHasItemWithHimCondition("assassins id")),
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, EventRaiser npc) {
						repairer.say(repairer.getReply(ResponseId.DENIED));
					}
				});

		// meets requirements and does meet preconditions
		repairer.add(ConversationStates.ATTENDING,
				ConversationPhrases.REPAIR_MESSAGES,
				new PlayerHasItemWithHimCondition("assassins id"),
				null,
				null,
				requestRepairAction(repairer));

		// changes mind / does not want repair
		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, EventRaiser npc) {
						repairer.say(repairer.getReply(ResponseId.REJECT_REPAIR));
					}
				});

		// player dropped item before accepting
		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						needsRepairCondition(repairer),
						itemsDroppedCondition(repairer)),
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, EventRaiser npc) {
						repairer.say(repairer.getReply(ResponseId.DROPPED));
					}
				});

		// this should not happen
		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				feeNotSetCondition(repairer),
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, EventRaiser npc) {
						repairer.say(repairer.getReply(ResponseId.ERROR));
					}
				});

		// wants repair but doesn't have enough money
		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						needsRepairCondition(repairer),
						new NotCondition(canAffordCondition(repairer))),
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, EventRaiser npc) {
						repairer.say(repairer.getReply(ResponseId.NO_AFFORD));
					}
				});

		// wants repair and does have enough money
		repairer.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						needsRepairCondition(repairer),
						canAffordCondition(repairer)),
				ConversationStates.ATTENDING,
				null,
				repairAction(repairer));
	}

	/**
	 * Creates action for requesting item(s) repair.
	 *
	 * @param repairer
	 *   Item repairing NPC.
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
						repairer.say(repairer.getReply(ResponseId.UNDECLARED));
						repairer.setCurrentState(ConversationStates.ATTENDING);
						return;
					}
					// player does not need to specify item name if repairer only repairs one item
					request = request + " " + repairer.getFirstRepairable();
				}

				// compare with lower-case to make request line case-insensitive
				String requestL = request.toLowerCase(Locale.ENGLISH);
				for (final String rWord: ConversationPhrases.REPAIR_MESSAGES) {
					if (requestL.startsWith(rWord.toLowerCase(Locale.ENGLISH))) {
						// trim trigger word
						request = request.substring(rWord.length() + 1);
						break;
					}
				}

				repairer.setRepairItem(request);
				repairer.setRepairCount(player);

				if (!repairer.canRepair()) {
					String cannotRepairReply = repairer.getReply(ResponseId.NO_REPAIR);
					if (cannotRepairReply == null) {
						cannotRepairReply = "I do not repair " + Grammar.plural(repairer.currentRepairItem)
								+ ".";
					}

					repairer.say(cannotRepairReply);
					repairer.setCurrentState(ConversationStates.ATTENDING);
					return;
				} else if (repairer.currentRepairCount < 1) {
					String notCarryingReply = repairer.getReply(ResponseId.NO_CARRY);
					if (notCarryingReply == null) {
						notCarryingReply = "You don't have any #'" + Grammar.plural(repairer.currentRepairItem)
								+ "' that need repaired.";
					}

					repairer.say(notCarryingReply);
					repairer.setCurrentState(ConversationStates.ATTENDING);
					return;
				}

				repairer.calculateRepairFee();
				final boolean multiple = repairer.currentRepairCount > 1;

				String sayCountReply;
				if (multiple) {
					sayCountReply = repairer.getReply(ResponseId.SAY_COUNT, true);
				} else {
					sayCountReply = repairer.getReply(ResponseId.SAY_COUNT);
				}

				if (sayCountReply == null) {
					final StringBuilder sb = new StringBuilder("You have " + repairer.currentRepairCount
							+ " used " + repairer.currentRepairItem); //Grammar.plnoun(repairer.currentRepairCount, repairer.currentRepairItem)); // FIXME: formats name all lowercase
					if (multiple) {
						sb.append("s");
					}
					sb.append(". I can repair ");
					if (multiple) {
						sb.append("them all");
					} else {
						sb.append("it");
					}
					sb.append(" for " + repairer.currentRepairFee + " money. Would you like me to do so?");
					sayCountReply = sb.toString();
				}

				repairer.say(sayCountReply);
				repairer.setCurrentState(ConversationStates.QUESTION_2);
			}
		};
	}

	/**
	 * Creates action for executing item(s) repair.
	 *
	 * @param repairer
	 *   Item repairing NPC.
	 * @return
	 *   Action for executing repair.
	 */
	private ChatAction repairAction(AssassinRepairer repairer) {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				player.drop("money", repairer.currentRepairFee);

				for (final Item bow: player.getAllEquipped(repairer.currentRepairItem)) {
					final BreakableWeapon breakable = (BreakableWeapon) bow;
					if (breakable.isUsed()) {
						breakable.repair();
					}
				}

				final boolean multiple = repairer.currentRepairCount > 1;

				String doneReply;
				if (multiple) {
					doneReply = repairer.getReply(ResponseId.REPAIR_DONE, true);
				} else {
					doneReply = repairer.getReply(ResponseId.REPAIR_DONE);
				}

				if (doneReply == null) {
					doneReply = "Done! Your ";
					if (multiple) {
						doneReply += Grammar.plural(repairer.currentRepairItem) + " are ";
					} else {
						doneReply += repairer.currentRepairItem + " is ";
					}
					doneReply += "as good as new.";
				}

				repairer.say(doneReply);
				repairer.addEvent(new SoundEvent(SoundID.COMMERCE, SoundLayer.CREATURE_NOISE));

				// reset item name, count, & fee back to null
				repairer.reset();
			}
		};
	}

	/**
	 * Creates condition to check if fee has been determined.
	 *
	 * @param repairer
	 *   Item repairing NPC.
	 * @return
	 *   Condition.
	 */
	private ChatCondition feeNotSetCondition(AssassinRepairer repairer) {
		return new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity raiser) {
				if (repairer.currentRepairFee == null) {
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
	 * @param repairer
	 *   Item repairing NPC.
	 * @return
	 *   Condition.
	 */
	private ChatCondition canAffordCondition(AssassinRepairer repairer) {
		return new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity raiser) {
				return player.isEquipped("money", repairer.currentRepairFee);
			}
		};
	}

	/**
	 * Creates condition to check if player is carrying any "worn" items.
	 *
	 * @param repairer
	 *   Item repairing NPC.
	 * @return
	 *   Condition.
	 */
	private ChatCondition needsRepairCondition(AssassinRepairer repairer) {
		return new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, Entity entity) {
				return repairer.currentRepairCount != null && repairer.currentRepairCount > 0;
			}
		};
	}

	/**
	 * Creates condition to check if player dropped item after requesting repair.
	 *
	 * @param repairer
	 *   Item repairing NPC.
	 * @return
	 *   Condition.
	 */
	private ChatCondition itemsDroppedCondition(AssassinRepairer repairer) {
		return new ChatCondition() {
			@Override
			public boolean fire(Player player, Sentence sentence, Entity entity) {
				return repairer.getWornItemsCount(player) < repairer.currentRepairCount;
			}
		};
	}


	/**
	 * NPC entity that supports repairing items.
	 *
	 * TODO:
	 *   - move to separate class file in package {@code games.stendhal.server.entity.npc} or ...
	 *   - move members and methods to behavior class
	 */
	public static class AssassinRepairer extends SpeakerNPC {

		private static Logger logger = Logger.getLogger(AssassinRepairer.class);

		/** Prices for repairing items. */
		private final Map<String, Integer> repairList;

		/** Subject item name of current conversation. */
		private String currentRepairItem = null;
		/** Subject number of items to repair of current conversation. */
		private Integer currentRepairCount = null;
		/** Subject fee of current conversation. */
		private Integer currentRepairFee = null;

		/** Responses for repair requests conditions. */
		private final Map<ResponseId, Object> replies = new HashMap<ResponseId, Object>() {{
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
		 * Creates a new NPC.
		 *
		 * @param name
		 *   Name of NPC.
		 * @param repairList
		 *   List of item names and prices for repair.
		 */
		public AssassinRepairer(String name, final Map<String, Integer> repairList) {
			super(name);
			this.repairList = repairList;
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
		 * @param id
		 *   Condition ID.
		 * @param responses
		 *   Replies for condition.
		 */
		public void setReply(final ResponseId id, final String... responses) {
			if (replies.containsKey(id)) {
				if (responses.length == 1) {
					replies.put(id, responses[0]);
				} else {
					replies.put(id, new Pair<String, String>(responses[0], responses[1]));
					if (responses.length > 2) {
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
		 * @param id
		 *   Condition ID.
		 * @param plural
		 *   {@code true} if multiple items are to be repaired.
		 * @return
		 *   NPC response to condition.
		 */
		private String getReply(final ResponseId id, final boolean plural) {
			final Object reply = replies.get(id);
			if (reply instanceof Pair) {
				Pair<?, ?> p = (Pair<?, ?>) reply;
				if (plural) {
					return (String) p.second();
				} else {
					return (String) p.first();
				}
			}
			return (String) reply;
		}

		/**
		 * Retrieves reply for certain condition.
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
		private void setRepairItem(String itemName) {
			itemName = Grammar.singular(itemName);
			for (final String repairable: repairList.keySet()) {
				if (itemName.toLowerCase(Locale.ENGLISH).equals(repairable.toLowerCase(Locale.ENGLISH))) {
					// use name from repair list in case request does not match character case (e.g.
					// repairable item is "foo" but player requested "Foo")
					currentRepairItem = repairable;
					return;
				}
			}
			currentRepairItem = itemName;
		}

		/**
		 * Checks if current item subject is included in list of items that NPC can repair.
		 *
		 * @return
		 *   {@code true} if repairing item is supported.
		 */
		public boolean canRepair() {
			if (currentRepairItem == null) {
				return false;
			}
			String itemNameL = currentRepairItem.toLowerCase(Locale.ENGLISH);
			for (String repairable: repairList.keySet()) {
				if (repairable.toLowerCase(Locale.ENGLISH).equals(itemNameL)) {
					return true;
				}
			}
			return false;
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
		 * Retrieves number of items player is carrying that need repair.
		 *
		 * @param player
		 *   Player requesting repair.
		 * @return
		 *   Number of "worn" items player is carrying.
		 */
		private int getWornItemsCount(final Player player) {
			if (!canRepair()) {
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
		 * Retrieves price of single item repair.
		 *
		 * @return
		 *   Unit price.
		 */
		private Integer getUnitPrice() {
			if (currentRepairItem == null) {
				logger.warn("Repair item name not set, cannot get unit price.");
				return null;
			}
			String itemNameL = currentRepairItem.toLowerCase(Locale.ENGLISH);
			for (String repairable: repairList.keySet()) {
				if (repairable.toLowerCase(Locale.ENGLISH).equals(itemNameL)) {
					return repairList.get(repairable);
				}
			}
			logger.warn("Item " + currentRepairItem + " not found in repair list, cannot get unit price");
			return null;
		}

		/**
		 * Sets subject fee of current conversation.
		 */
		private void calculateRepairFee() {
			if (currentRepairCount == null) {
				logger.warn("Repair count not set, cannot calculate repair fee");
				return;
			}
			Integer unitPrice = getUnitPrice();
			if (unitPrice != null) {
				currentRepairFee = currentRepairCount * unitPrice;
			}
		}

		/**
		 * Retrieves the first item name from repair list.
		 *
		 * Used when player asks for repair without declaring item name.
		 *
		 * @return
		 *   Name of first item in list of items that can be repaired.
		 */
		public String getFirstRepairable() {
			return repairList.keySet().toArray(new String[] {})[0];
		}

		/**
		 * Overridden to reset subjects at end of conversation.
		 */
		@Override
		public void onGoodbye(final RPEntity attending) {
			reset();
		}

		/**
		 * Overridden to reset subjects at end of conversation.
		 */
		@Override
		public void setCurrentState(ConversationStates state) {
			super.setCurrentState(state);
			if (ConversationStates.IDLE.equals(state)) {
				reset();
			}
		}
	}
}
