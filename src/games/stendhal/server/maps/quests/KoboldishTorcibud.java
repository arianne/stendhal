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
 
import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.ItemCollection;

public class KoboldishTorcibud extends AbstractQuest {
 
	public static final String QUEST_SLOT = "koboldish_torcibud";

	//the torcibud quest may be repeated after this amount of minutes has elapsed
	private static final int REPEAT_INTERVAL = 5;

	//the number of some of the items to fetch depends on the player level
	//private static Map<String,Integer> REQUIRED_ITEMS;

	@Override
	public void addToWorld() {
		super.addToWorld();
		phase_1();
	}
 
	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
 
	@Override
		public String getName() {
		return "KoboldishTorcibud";
	}

        public String askForItems() {
		return "Wrof! What #stuff did you gather?";
	}

        public String getRequiredItemsCollection(int pLevel) {

		int FACTOR_BOTTLE_EARED = 50;
		int FACTOR_BOTTLE_SLIM = 25;

		return
			"eared bottle=" + Rand.roll1D6() + pLevel / FACTOR_BOTTLE_EARED + ";" +
			"slim bottle=" + Rand.roll1D6() + pLevel / FACTOR_BOTTLE_SLIM + ";" +
			"artichoke=" + Rand.roll1D6() + ";" +
			"arandula=" + Rand.roll1D6() + ";" +
			"sclaria=" + Rand.roll1D6() + ";" +
			"kekik=" + Rand.roll1D6() + ";" +
			"fierywater=" + Rand.roll1D6() + ";";


	}

        public String askForMoreItems() {
                return "Wrof! Did you gather more #stuff?";
        }

	public String replyItemGiven() {
		return "Wrof! It is what I needed, many thanks!";
	}

	public String greetingsAfterQuestCompleted() {
		return "Wroff! Here you are again. Did you come for more of my famous koboldish #torcibud?";
	}

        /**
         * Returns all items that the given player still has to bring to complete the quest.
         *
         * @param player The player doing the quest
         * @return A list of item names
         */
        private ItemCollection getMissingItems(final Player player) {
                final ItemCollection missingItems = new ItemCollection();

                missingItems.addFromQuestStateString(player.getQuest(QUEST_SLOT));

                return missingItems;
        }

	public void phase_1() {
     
		SpeakerNPC npc = npcs.get("Wrviliza");

                // player has rejected or never took the quest in the past
                npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"Wrof! My stock of supplies for preparing koboldish #torcibud is running thin. Would you help me getting #stuff?",
			null
		);

                npc.add(ConversationStates.ATTENDING,
                                "stuff",
                                new QuestNotStartedCondition(QUEST_SLOT),
                                ConversationStates.QUEST_OFFERED,
                                "Wrof! Some bottles, artichokes, a few herbs and the fierywater... So, will you help?",
                                null);

                npc.add(ConversationStates.ATTENDING,
                                "torcibud",
                                new QuestNotStartedCondition(QUEST_SLOT),
                                ConversationStates.QUEST_OFFERED,
                                "Wrof! Good mild or strong koboldish torcibud requires some ingredients. Will you gather some for me?",
                                null);

                npc.add(ConversationStates.QUEST_OFFERED,
                        ConversationPhrases.YES_MESSAGES, null,
                        ConversationStates.ATTENDING, null,
                        new ChatAction() {
                                public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					int pLevel = player.getLevel();

                                        player.setQuest(QUEST_SLOT, getRequiredItemsCollection(pLevel));
                                        player.addKarma(10);

                                        raiser.say("Wroff! Right now, I need "
						+ Grammar.enumerateCollection(getMissingItems(player).toStringListWithHash())
						+ ". Do you by chance have anything of that with you already?");
                                }
                        }
		);

		//player accepts the quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Wroff! I'll be waiting for your return then.",
			null);
		
		//player rejects the quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Wruff... I guess I'll have to ask someone with a better attitude!",
			null);

	}
}
