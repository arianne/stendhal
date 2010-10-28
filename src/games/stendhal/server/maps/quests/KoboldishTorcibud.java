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
 
import games.stendhal.common.Rand;
import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.entity.npc.action.*;
import games.stendhal.server.entity.npc.condition.*;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.*;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.util.ItemCollection;

import java.util.*;

public class KoboldishTorcibud extends AbstractQuest {
 
    public static final String QUEST_SLOT = "koboldish_torcibud";

    // the torcibud quest may be repeated after some delay
    private static final int BASE_DELAY_DAYS = 5;

    // how much XP is given as the reward
    private static final int XP_REWARD = 100;

    // a template of the items that wrviliza will ask for the quest,
    // it is only used to initialize the triggers in phase_2.
    private static final String REQUIRED_ITEMS_TEMPLATE =
        "eared bottle=0;" +
        "slim bottle=0;" +
        "artichoke=0;" +
        "arandula=0;" +
        "sclaria=0;" +
        "kekik=0;" +
        "fierywater=0";

    @Override
    public void addToWorld() {
        super.addToWorld();
        phase_1();
        phase_2();
    }
 
    @Override
    public String getSlotName() {
        return QUEST_SLOT;
    }
 
    @Override
        public String getName() {
        return "KoboldishTorcibud";
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

    /**
     * Returns the items to collect and required quantity
     *
     * @param pLevel The level of the player undertaking the quest, affects the required quantity of some items.
     * @return A string composed of semi-colon separated key=value token pairs.
     */
    private String getRequiredItemsCollection(int pLevel) {

        int FACTOR_BOTTLE_EARED = 80;
        int FACTOR_BOTTLE_SLIM = 60;

        int required_bottle_eared = Rand.roll1D6() + (pLevel / FACTOR_BOTTLE_EARED);
        int required_bottle_slim = Rand.roll1D6() + (pLevel / FACTOR_BOTTLE_SLIM);

        return
            "eared bottle=" + required_bottle_eared + ";" +
            "slim bottle=" + required_bottle_slim + ";" +
            "artichoke=" + Rand.roll1D6() + ";" +
            "arandula=" + Rand.roll1D6() + ";" +
            "sclaria=" + Rand.roll1D6() + ";" +
            "kekik=" + Rand.roll1D6() + ";" +
            "fierywater=" + Rand.roll1D6() + ";";
    }

    private String getRandomDelayTimestamp(int base_delay_days) {
        String tstamp = Long.toString(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * ( BASE_DELAY_DAYS + Rand.roll1D6())); 
        return tstamp;
    }

    /**
     * The player meets the Kobold Barmaid Wrviliza and possibly gets a quest from her
     */
    public void phase_1() {
     
        final SpeakerNPC npc = npcs.get("Wrviliza");

        // player greeting NPC and has rejected or never took the quest in the past
        npc.add(ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new OrCondition(new QuestNotStartedCondition(QUEST_SLOT), new QuestInStateCondition(QUEST_SLOT, 0, "rejected")),
            ConversationStates.ATTENDING,
            "Wroff! Welcome into the Kobold's Den bar wanderer! I'm Wrviliza, wife of #Wrvil. May I #offer you something?",
            null);

        // player asks the quest and has rejected or never took the quest in the past
        npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.QUEST_MESSAGES,
            new OrCondition(new QuestNotStartedCondition(QUEST_SLOT), new QuestInStateCondition(QUEST_SLOT, 0, "rejected")),
            ConversationStates.QUEST_OFFERED,
            "Wrof! My stock of supplies for preparing koboldish #torcibud is running thin. Would you help me getting some #stuff?",
            null
        );

        // player asks the quest and has already done the quest
        npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.QUEST_MESSAGES,
            new QuestCompletedCondition(QUEST_SLOT),
            ConversationStates.ATTENDING,
            "Wrof! My stock of supplies needs not to be refurbished since you last helped me, thanks!",
            null
        );

        npc.add(ConversationStates.QUEST_OFFERED,
            "torcibud",
            new QuestNotStartedCondition(QUEST_SLOT),
            ConversationStates.QUEST_OFFERED,
            "Wruff. I will make more when I have enough #stuff!",
            null);

        npc.add(ConversationStates.QUEST_OFFERED,
            Arrays.asList("stuff","ingredients","supplies"),
            new OrCondition(new QuestNotStartedCondition(QUEST_SLOT), new QuestInStateCondition(QUEST_SLOT, 0, "rejected")),
            ConversationStates.QUEST_OFFERED,
            "Wrof! Some bottles, artichokes, a few herbs and fierywater... Things like that. So, will you help?",
            null);

        //player accepts the quest and gets to know the required items
        npc.add(ConversationStates.QUEST_OFFERED,
            ConversationPhrases.YES_MESSAGES, null,
            ConversationStates.QUESTION_1, null,
            new ChatAction() {
                public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
                    int pLevel = player.getLevel();

                    player.setQuest(QUEST_SLOT, 0, getRequiredItemsCollection(pLevel));
                    player.addKarma(20);

                    raiser.say("Wroff! Right now, I need "
                        + Grammar.enumerateCollection(getMissingItems(player).toStringListWithHash())
                        + ". Do you by chance have anything of that with you already?");
                }
            }
        );

        //player is not inclined to comply with the request (looses some karma)
        npc.add(ConversationStates.QUEST_OFFERED,
            ConversationPhrases.NO_MESSAGES, null,
            ConversationStates.IDLE,
            "Wruff... I guess I'll have to ask someone with a better attitude!",
            new MultipleActions(
            new SetQuestAction(QUEST_SLOT, 0, "rejected"),
        new  DecreaseKarmaAction(20.0)));

    }

    /**
     * The player meets the Kobold Barmaid Wrviliza after he has started the quest
     */
    public void phase_2() {

        final SpeakerNPC npc = npcs.get("Wrviliza");

        // player greeting NPC with the quest active
        npc.add(ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new ChatCondition() {
                public boolean fire(final Player player, final Sentence sentence,   final Entity entity) {
                    return player.hasQuest(QUEST_SLOT)
                        && !player.isQuestCompleted(QUEST_SLOT)
                        && !"reward".equals(player.getQuest(QUEST_SLOT));
                }
            },
            ConversationStates.QUESTION_1,
            "Wrof! Welcome back. Did you gather #stuff for me?",
            null);

        // player says 'stuff'
        npc.add(ConversationStates.QUESTION_1, "stuff", null,
            ConversationStates.QUESTION_1,
            null,
            new ChatAction() {
                public void fire(final Player player, final Sentence sentence, final EventRaiser entity) {
                    entity.say(
                        "Wrof! I still need "
                            + Grammar.enumerateCollection(getMissingItems(player).toStringListWithHash())
                            + ". Did you bring anything of that sort?");
                }
            });

        // player answers yes
        npc.add(ConversationStates.QUESTION_1,
            ConversationPhrases.YES_MESSAGES, null,
            ConversationStates.QUESTION_1,
            "Fine, what did you bring?",
            null);
    
        // player answers no 
        npc.add(ConversationStates.QUESTION_1,
            ConversationPhrases.NO_MESSAGES,
            new ChatCondition() {
                public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
                    return player.hasQuest(QUEST_SLOT)
                        && !player.isQuestCompleted(QUEST_SLOT)
                        && !"reward".equals(player.getQuest(QUEST_SLOT));
                }
            },
            ConversationStates.ATTENDING,
            "Wruf! Take your time... no hurry!",
            null);

        // player answers no to 'offer' question
        npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.NO_MESSAGES,
            new ChatCondition() {
                public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
                    return !player.isQuestCompleted(QUEST_SLOT)
                        && !"reward".equals(player.getQuest(QUEST_SLOT));
                }
            },
            ConversationStates.ATTENDING,
            "Wruf! Wruf!",
            null);

        // create the ChatAction to reward the player
        ChatAction addRewardAction = new ChatAction() {
            public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
                final StackableItem koboldish_torcibud_vsop = (StackableItem) SingletonRepository.getEntityManager().getItem("vsop koboldish torcibud");
                final int torcibud_bottles = 1 + 1 * Rand.roll1D6();
                koboldish_torcibud_vsop.setQuantity(torcibud_bottles);
                player.equipOrPutOnGround(koboldish_torcibud_vsop);
                npc.say(
                    "Wrof! Here take "
                    + Integer.toString(torcibud_bottles)
                    + " bottles of my V.S.O.P. Koboldish Torcibud with my best wishes for you!"
                    + " [0]<" + player.getQuest(QUEST_SLOT, 0)
                    + "> [1]<" + player.getQuest(QUEST_SLOT, 1) + ">");
            }
        };

        // player collected all the items
        ChatAction completeAction = new MultipleActions(
            new SetQuestAction(QUEST_SLOT, 0, "done"),
            new SetQuestAction(QUEST_SLOT, 1, getRandomDelayTimestamp(BASE_DELAY_DAYS)),
            addRewardAction,
            new IncreaseXPAction(XP_REWARD));

        // create the ChatAction used for item triggers
        final ChatAction itemsChatAction = new CollectRequestedItemsAction(
            QUEST_SLOT,
            "Wroff! do you have anything else?",
            "Wruff! You have already brought that to me!",
            completeAction,
            ConversationStates.ATTENDING);
        
        // add triggers for the item names
        final ItemCollection items = new ItemCollection();
        items.addFromQuestStateString(REQUIRED_ITEMS_TEMPLATE);
        for (final Map.Entry<String, Integer> item : items.entrySet()) {
            npc.add(ConversationStates.QUESTION_1,
                item.getKey(), null,
                ConversationStates.QUESTION_1, null,
                itemsChatAction);
        }
    }
}
