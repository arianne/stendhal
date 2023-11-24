/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
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

import java.util.Arrays;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ConditionalAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerCanEquipItemCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemdataItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestRegisteredCondition;
import games.stendhal.server.entity.npc.quest.BringItemQuestBuilder;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Traps for Klaas
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Klaas (the Seaman that takes care of Athor's ferry's cargo)</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Klaas asks you to bring him rodent traps.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>1000 XP</li>
 * <li>5 greater antidote
 * <li>note to apothecary (if Antivenom Ring quest not started)
 * <li>Karma: 10</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Every 24 hours</li>
 * </ul>
 */
public class TrapsForKlaas implements QuestManuscript {
	private final static String QUEST_SLOT = "traps_for_klaas";

	@Override
	public BringItemQuestBuilder story() {
		BringItemQuestBuilder quest = new BringItemQuestBuilder();

		quest.info()
			.name("Traps for Klaas")
			.description("Klaas, the cargo caretaker on the Athor ferry, is in need of some rodent traps.")
			.internalName(QUEST_SLOT)
			.repeatableAfterMinutes(24 * 60)
			.minLevel(0)
			.region(Region.ATHOR_ISLAND)
			.questGiverNpc("Klaas");

		quest.history()
			.whenNpcWasMet("I have talked to Klaas on the ship to Athor.")
			.whenQuestWasRejected("I do not care about dealing with rodents.")
			.whenQuestWasAccepted("I promised to gather 20 rodent traps and bring them to Klaas.")
			.whenTaskWasCompleted("I got enough traps.")
			.whenQuestWasCompleted("I gave the rodent traps to Klaas. I got some experience and antidotes.")
			.whenQuestCanBeRepeated("I should check if Klaas needs my help again.");

		quest.offer()
			.respondToRequest("The rats down here have been getting into the food storage. Would you help me rid us of the varmints?")
			.respondToUnrepeatableRequest("Thanks for the traps. Now the food will be safe. But I may need your help again soon.")
			.respondToRepeatedRequest("The rats down here have been getting into the food storage. Would you help me rid us of the varmints?")
			.respondToAccept("Thanks, I need you to bring me bring me 20 #rodent #traps. Please hurry! We can't afford to lose anymore food.")
			.respondToReject("Don't waste my time. I've got to protect the cargo.")
			.rejectionKarmaPenalty(5.0)
			.remind("I believe, I already asked you to get me 20 rodent traps.");

		final SpeakerNPC npc = NPCList.get().get("Klaas");
		npc.add(
				ConversationStates.ATTENDING,
				Arrays.asList("rodent trap", "trap", "rodent traps", "traps"),
				new QuestActiveCondition("traps_for_klaas"),
				ConversationStates.ATTENDING,
				"I don't know of anyone who sells 'em. But I did hear a story once about a fella who killed a large rat and discovered a trap snapped shut on its foot.",
				null);


		quest.task()
			.requestItem(20, "rodent trap");

		quest.complete()
			.greet("Did you bring any traps?")
			.respondToReject("Please hurry! I just found another box of food that's been chewed through.")
			.respondToAccept("Thanks! I've got to get these set up as quickly as possible. Take these antidotes as a reward.")
			.rewardWith(new IncreaseXPAction(1000))
			.rewardWith(new IncreaseKarmaAction(10))
			.rewardWith(new EquipItemAction("greater antidote", 5))
			.rewardWith(new ConditionalAction(
					giveNoteRewardCondition,
					new MultipleActions(
							equipNoteAction,
							new SayTextAction("I used to know an old #apothecary. Take this note to him. Maybe he can help you out with something."))));

		// Player has lost note
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new QuestRegisteredCondition("antivenom_ring"),
						new NotCondition(new PlayerHasItemdataItemWithHimCondition("note", info_string)),
						new PlayerCanEquipItemCondition("note"),
						new QuestCompletedCondition(QUEST_SLOT),
						new QuestNotStartedCondition("antivenom_ring")),
				ConversationStates.ATTENDING,
				"You lost the note? Well, I guess I can write you up another, but be careful this time."
				+ " Remember to ask around about an #apothecary.",
				equipNoteAction);


		// player lost note, but doesn't have room in inventory
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new NotCondition(new PlayerHasItemdataItemWithHimCondition("note", info_string)),
						new NotCondition(new PlayerCanEquipItemCondition("note")),
						new QuestCompletedCondition(QUEST_SLOT),
						new QuestNotStartedCondition("antivenom_ring")),
				ConversationStates.ATTENDING,
				"You lost the note? Well, I could write another one. But it doesn't look like you have room to carry it.",
				null);

		return quest;
	}


	// action that gives player note to apothecary
	private static final String info_string = "note to apothecary";
	final ChatAction equipNoteAction = new ChatAction() {
		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
			final Item note = SingletonRepository.getEntityManager().getItem("note");
			note.setItemData(info_string);
			note.setDescription("You see a note written to an apothecary. It is a recommendation from Klaas.");
			note.setBoundTo(player.getName());
			player.equipOrPutOnGround(note);
		}
	};

	final ChatCondition giveNoteRewardCondition = new ChatCondition() {
		private final String avrQuestSlot = "antivenom_ring";

		@Override
		public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
			if (!new QuestRegisteredCondition(avrQuestSlot).fire(player, sentence, npc)) {
				return false;
			}

			// note has already been given to Jameson & Antivenom Ring quest has already been started or completed
			if (player.getQuest(avrQuestSlot) != null) {
				return false;
			}

			// player already has a note
			// FIXME: PlayerOwnsItemIncludingBankCondition currently doesn't support itemdata items
			if (player.isEquippedWithItemdata("note", info_string)) {
				return false;
			}

			return true;
		}
	};

}
