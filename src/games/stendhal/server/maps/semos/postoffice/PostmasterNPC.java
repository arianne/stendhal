/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.postoffice;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.Mailbox;


/**
 * NPC that manages postal transactions.
 */
public class PostmasterNPC implements ZoneConfigurator {

	/** Amount NPC will charge to send an item. */
	private static final int SERVICE_FEE = 100;


	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		zone.add(configureNPC());
	}

	/**
	 * Configures NPC for managing player mailboxes.
	 *
	 * @return
	 *   Postmaster NPC.
	 */
	private SpeakerNPC configureNPC() {
		final SpeakerNPC npc = new SpeakerNPC("Postmaster Ellie");
		npc.setOutfit("body=1,head=0,mouth=2,eyes=24,dress=970,hair=50,mask=1,hat=990");
		npc.setPosition(9, 7);
		npc.setIdleDirection(Direction.DOWN);
		configurePostmaster(npc);

		// TODO:
		// - create interior post office zone
		// - create NPC dialogue

		return npc;
	}

	/**
	 * Adds postmaster dialogue and logic to NPC.
	 *
	 * @param npc
	 *   Postmaster NPC.
	 */
	private void configurePostmaster(final SpeakerNPC npc) {
		npc.addGreeting("Hello. Welcome to Semos Post Office. How can I #help you?");
		npc.addGoodbye();
		npc.addHelp("I can help manage your #mailbox for a #fee.");
		npc.addJob("I am postmaster of the Semos Post Office.");
		npc.addQuest("Oh, no thank you. I don't need any help at the moment.");
		npc.addOffer("The only thing I can offer is assistance with managing your #mailbox.");

		npc.add(
				ConversationStates.ATTENDING,
				"fee",
				null,
				ConversationStates.ATTENDING,
				"The fee to #send an item is " + SERVICE_FEE + " money.",
				null);

		npc.add(
				ConversationStates.ATTENDING,
				"mailbox",
				null,
				ConversationStates.ATTENDING,
				"I can help you #send items to your friends. You can also check the current #status of your"
						+ " mailbox.",
				null);

		npc.add(
				ConversationStates.ATTENDING,
				"status",
				null,
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, EventRaiser raiser) {
						reportStatus(player, npc);
					}
				});

		npc.add(
				ConversationStates.ATTENDING,
				"send",
				null,
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, EventRaiser raiser) {
						// TODO: get target name & item from conversation
						sendOutbox(player, npc, null, null);
					}
				});

		final ChatAction receiveInboxAction = new ChatAction() {
			@Override
			public void fire(Player player, Sentence sentence, EventRaiser raiser) {
				receiveInbox(player, npc);
			}
		};

		npc.add(
				ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Okay, let me know how else I can #help you.",
				null);

		npc.add(
				ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				receiveInboxAction);

		npc.add(
				ConversationStates.ATTENDING,
				"receive",
				null,
				ConversationStates.ATTENDING,
				null,
				receiveInboxAction);
	}

	/**
	 * Reports status of player's mailbox.
	 *
	 * @param player
	 *   Player requesting status info.
	 * @param npc
	 *   Postmaster NPC.
	 */
	private static void reportStatus(final Player player, final SpeakerNPC npc) {
		String inbox = getInboxStatus(player);
		String outbox = getOutboxStatus(player);
		boolean offerReceive = true;
		if (inbox == null && outbox == null) {
			npc.say("I'm sorry, but I'm unable to check the status of your mailbox at the current time."
					+ " Please come back after we have worked out the problem.");
			return;
		} else if (inbox == null) {
			offerReceive = false;
			inbox = "I'm sorry, but I'm unable to check the status of your inbox at the current time."
					+ " Please come back after we have worked out the problem.";
		} else if (outbox == null) {
			outbox = "I'm sorry, but I'm unable to check the status of your outbox at the current time."
					+ " Please come back after we have worked out the problem.";
		}
		String msg = inbox + " " + outbox;
		if (offerReceive) {
			msg += " Would you like to get the item from your inbox?";
			npc.setCurrentState(ConversationStates.QUESTION_1);
		} else {
			msg += " Let me know if there is anything else I can #help you with.";
		}
		npc.say(msg);
	}

	/**
	 * Gets message for player dependent on inbox status.
	 *
	 * @param player
	 *   Player requesting status info.
	 * @return
	 *   Inbox status message or {@code null}.
	 */
	private static String getInboxStatus(final Player player) {
		// TODO:
		return null;
	}

	/**
	 * Gets a message for player dependent on outbox status.
	 *
	 * @param player
	 *   Player requesting status info.
	 * @return
	 *   Outbox status message or {@code null}.
	 */
	private static String getOutboxStatus(final Player player) {
		final Mailbox mailbox = (Mailbox) player.getSlot("mailbox");
		if (mailbox == null) {
			return null;
		}
		final Item item = (Item) mailbox.getFirst();
		if (item == null) {
			return "Your outbox is empty.";
		} else {
			final String itemName = item.getName();
			final int count = item.getQuantity();
			final String target = mailbox.getTarget();
			if (target == null) {
				return "Uh oh! You have " + Grammar.quantityNumberStrNoun(count, itemName)
						+ " ready to be mailed without anyone to receive it. Let me know if you would like to"
						+ "#cancel.";
			} else {
				return "You have " + Grammar.quantityNumberStrNoun(count, itemName)
						+ " waiting to be picked up by " + target + ".";
			}
		}
	}

	/**
	 * Gives player item from inbox.
	 *
	 * @param player
	 *   Player receiving item.
	 * @param npc
	 *   Postmaster NPC.
	 */
	private static void receiveInbox(final Player player, final SpeakerNPC npc) {
		// TODO:
		npc.say("I'm sorry, the post office is still in its infancy and not fully functional. Please"
				+ " come back at another time to receive items.");
	}

	/**
	 * Sends item to a player's inbox.
	 *
	 * @param player
	 *   Player sending item.
	 * @param npc
	 *   Postmaster NPC.
	 * @param target
	 *   Name of player to receive item.
	 * @param item
	 *   Item to be sent.
	 */
	private static void sendOutbox(final Player player, final SpeakerNPC npc, final String target,
			final Item item) {
		// TODO:
		npc.say("I'm sorry, the post office is still in its infancy and not fully functional. Please"
				+ " come back at another time to send items.");
	}
}
