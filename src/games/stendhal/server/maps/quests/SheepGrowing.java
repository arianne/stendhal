/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.Level;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.semos.city.SheepBuyerNPC.SheepBuyerSpeakerNPC;

/**
 * QUEST: Sheep Growing for Nishiya
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Nishiya (the sheep seller in Semos village)</li>
 * <li>Sato (the sheep buyer in Semos city)</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Nishiya asks you to grow a sheep.</li>
 * <li>Sheep grows to weight 100.</li>
 * <li>Sheep is handed over to Sato.</li>
 * <li>Nishiya thanks you.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>Maximum of (XP to level 2) or (30XP)</li>
 * <li>Karma: 10</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class SheepGrowing extends AbstractQuest {

    private static final String QUEST_SLOT = "sheep_growing";
    private static final String TITLE = "Sheep Growing for Nishiya";
    private static final int MIN_XP_GAIN = 30;

    @Override
    public void addToWorld() {
        fillQuestInfo(
                TITLE,
                "Nishiya, the sheep seller, promised Sato a sheep. " +
                    "Because he is very busy he needs somebody to take care of " +
                    "one of his sheep and hand it over to Sato.",
                true);
        generalInformationDialogs();
        preparePlayerGetsSheepStep();
        preparePlayerHandsOverSheepStep();
        preparePlayerReturnsStep();
    }

    @Override
    public String getSlotName() {
        return QUEST_SLOT;
    }

    @Override
    public List<String> getHistory(final Player player) {
        final List<String> res = new LinkedList<String>();
        if (!player.hasQuest(QUEST_SLOT)) {
            return res;
        }
        res.add("Nishiya asked me if I could raise a sheep for him.");

        final String questState = player.getQuest(QUEST_SLOT);
        if (questState.equals("rejected")) {
            res.add("I told Nishiya that I have to do other things now... maybe I have time for the task later.");
        }
        if (player.isQuestInState(QUEST_SLOT, "start", "handed_over", "done")) {
            res.add("I promised to take care of one of his sheep.");
        }
        if (player.isQuestInState(QUEST_SLOT, "handed_over", "done")) {
            res.add("I handed over the grown sheep to Sato. I should return to Nishiya now.");
        }
        if(questState.equals("done")) {
            res.add("I returned to Nishiya. He was very happy I helped him.");
        }
        return res;
    }

    @Override
    public String getName() {
        return TITLE;
    }

    /**
     * General information for the player related to the quest.
     */
    private void generalInformationDialogs() {
        final SpeakerNPC npc = npcs.get("Nishiya");

        npc.add(ConversationStates.ATTENDING, "Sato", null, ConversationStates.ATTENDING, "Sato is the sheep buyer of Semos city. " +
                "You will find him if you follow the path to the east.", null);
        npc.add(ConversationStates.QUEST_OFFERED, "Sato", null, ConversationStates.QUEST_OFFERED, "Sato is the sheep buyer of Semos city. " +
                "You will find him if you follow the path to the east.", null);

        List<String> berryStrings = new ArrayList<String>();
        berryStrings.add("red berries");
        berryStrings.add("berries");
        berryStrings.add("sheepfood");
        berryStrings.add("sheep food");
        npc.addReply(berryStrings, "Sheep like to eat the red berries from the aeryberry bushes.");

        npc.addReply("sheep", "I sell fluffy sheep, it's my #job.");
    }
    /**
     * The step where the player speaks with Nishiya about quests and gets the sheep.
     */
    private void preparePlayerGetsSheepStep() {
        final SpeakerNPC npc = npcs.get("Nishiya");

        // If quest is not done or started yet ask player for help (if he does not have a sheep already)
        ChatCondition playerHasNoSheep = new ChatCondition() {
            @Override
			public boolean fire(Player player, Sentence sentence, Entity npc) {
                return !player.hasSheep();
            }
        };
        npc.add(
                ConversationStates.ATTENDING,
                ConversationPhrases.QUEST_MESSAGES,
                new AndCondition(playerHasNoSheep,
                        new QuestNotInStateCondition(QUEST_SLOT, "start"),
                        new QuestNotInStateCondition(QUEST_SLOT, "handed_over"),
                        new QuestNotInStateCondition(QUEST_SLOT, "done")),
                ConversationStates.QUEST_OFFERED,
                "Lately I am very busy with all my sheep. " +
                "Would you be willing to take care of one of my sheep and hand it over to #Sato? " +
                "You only have to let it eat some red berries until it reaches a weight of " + Sheep.MAX_WEIGHT + ". " +
                "Would you do that?",
                new SetQuestAction(QUEST_SLOT, "asked"));

        // If quest is offered and player says no reject the quest
        npc.add(
                ConversationStates.QUEST_OFFERED,
                ConversationPhrases.NO_MESSAGES,
                new AndCondition(playerHasNoSheep,
                        new QuestInStateCondition(QUEST_SLOT, "asked")),
                ConversationStates.IDLE,
                "Ok... then I have to work twice as hard these days...",
                new SetQuestAction(QUEST_SLOT, "rejected"));

        // If quest is still active but not handed over do not give an other sheep to the player
        npc.add(
                ConversationStates.ATTENDING,
                ConversationPhrases.QUEST_MESSAGES,
                new AndCondition(
                    new QuestActiveCondition(QUEST_SLOT),
                    new NotCondition(new QuestInStateCondition(QUEST_SLOT, "asked")),
                    new NotCondition(new QuestInStateCondition(QUEST_SLOT, "handed_over"))),
                ConversationStates.ATTENDING,
                "I already gave you one of my sheep. " +
                "If you left it on its own I can sell you a new one. Just say #buy #sheep.",
                null);

        // If quest is offered and player says yes, give a sheep to him.
        List<ChatAction> sheepActions = new LinkedList<ChatAction>();
        sheepActions.add(new SetQuestAction(QUEST_SLOT, "start"));
        sheepActions.add(new ChatAction() {
            @Override
			public void fire(Player player, Sentence sentence, EventRaiser npc) {
                final Sheep sheep = new Sheep(player);
                StendhalRPAction.placeat(npc.getZone(), sheep, npc.getX(), npc.getY() + 1);
            }
        });
        npc.add(
                ConversationStates.QUEST_OFFERED,
                ConversationPhrases.YES_MESSAGES,
                new AndCondition(playerHasNoSheep,
                        new QuestInStateCondition(QUEST_SLOT, "asked")),
                ConversationStates.IDLE,
                "Thanks! *smiles* Here is your fluffy fosterling. Be careful with her. " +
                "If she dies or if you leave her behind you have to buy the next sheep on your own. " +
                "Oh... and don't accidentally sell the sheep to Sato. Just talk to him when the sheep has grown up.",
                new MultipleActions(sheepActions));
    }
    /**
     * The step where the player goes to Sato to give him the grown up sheep.
     */
    private void preparePlayerHandsOverSheepStep() {
        // Remove action
        final List<ChatAction> removeSheepAction = new LinkedList<ChatAction>();
        removeSheepAction.add(new ChatAction() {
            @Override
			public void fire(Player player, Sentence sentence, EventRaiser npc) {
                // remove sheep
                final Sheep sheep = player.getSheep();
                if(sheep != null) {
                    player.removeSheep(sheep);
                    player.notifyWorldAboutChanges();
                    if(npc.getEntity() instanceof SheepBuyerSpeakerNPC) {
                        ((SheepBuyerSpeakerNPC)npc.getEntity()).moveSheep(sheep);
                    } else {
                        // only to prevent that an error occurs and the sheep does not disappear
                        sheep.getZone().remove(sheep);
                    }
                } else {
                    // should not happen
                    npc.say("What? What sheep? Did I miss something?");
                    npc.setCurrentState(ConversationStates.IDLE);
                    return;
                }
            }
        });
        removeSheepAction.add(new SetQuestAction(QUEST_SLOT, "handed_over"));

        // Hand-Over condition
        ChatCondition playerHasFullWeightSheep = new ChatCondition() {
            @Override
			public boolean fire(Player player, Sentence sentence, Entity npc) {
                return player.hasSheep()
                    && player.getSheep().getWeight() >= Sheep.MAX_WEIGHT;
            }
        };

        // Sato asks for sheep
        final SpeakerNPC npc = npcs.get("Sato");
        npc.add(
                ConversationStates.IDLE,
                ConversationPhrases.GREETING_MESSAGES,
                new AndCondition(
                        new QuestInStateCondition(QUEST_SLOT,"start"),
                        playerHasFullWeightSheep),
                ConversationStates.QUEST_ITEM_BROUGHT,
                "Hello. What a nice and healthy sheep is following you there! Is that one for me?",
                null);

        npc.add(
                ConversationStates.IDLE,
                ConversationPhrases.GREETING_MESSAGES,
                new AndCondition(
                        new QuestInStateCondition(QUEST_SLOT,"start"),
                        new NotCondition(playerHasFullWeightSheep)),
                ConversationStates.IDLE,
                "Hello. You should have a sheep from Nishiya for me, he owes me one! But I want a full weight one, so come back when you have one. Bye!",
                null);

        // Player answers yes - Sheep is given to Sato
        npc.add(
                ConversationStates.QUEST_ITEM_BROUGHT,
                ConversationPhrases.YES_MESSAGES,
                new AndCondition(
                        new QuestInStateCondition(QUEST_SLOT,"start"),
                        playerHasFullWeightSheep),
                ConversationStates.IDLE,
                "I knew it! It is Nishiya's, right? I was already waiting for it. " +
                "It is a gift for a friend of mine and it would be a shame if I had no birthday present. " +
                "Give thanks to Nishiya.",
                new MultipleActions(removeSheepAction));

        // Player answers no - Sheep stays at player
        npc.add(
                ConversationStates.QUEST_ITEM_BROUGHT,
                ConversationPhrases.NO_MESSAGES,
                new AndCondition(
                        new QuestInStateCondition(QUEST_SLOT,"start"),
                        playerHasFullWeightSheep),
                ConversationStates.IDLE,
                "He wanted to send me one a while ago...",
                null);


		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
						new QuestInStateCondition(QUEST_SLOT, "handed_over"),
				ConversationStates.ATTENDING,
				"Thank you for bringing me Nishiyas sheep! My friend was really happy about it.", null);
    }

    /**
     * The step where the player returns to Nishiya to get his reward.
     */
    private void preparePlayerReturnsStep() {
        final List<ChatAction> reward = new LinkedList<ChatAction>();
        reward.add(new ChatAction() {
            @Override
			public void fire(Player player, Sentence sentence, EventRaiser npc) {
                // give XP to level 2
                int reward = Level.getXP( 2 ) - player.getXP();
                if(reward > MIN_XP_GAIN) {
                    player.addXP(reward);
                } else {
                    player.addXP(MIN_XP_GAIN);
                }
                player.notifyWorldAboutChanges();
            }
        });
        reward.add(new SetQuestAction(QUEST_SLOT, "done"));
        reward.add(new IncreaseKarmaAction( 10 ));

        final SpeakerNPC npc = npcs.get("Nishiya");
        // Asks player if he handed over the sheep
        npc.add(
                ConversationStates.IDLE,
                ConversationPhrases.GREETING_MESSAGES,
                new QuestInStateCondition(QUEST_SLOT, "handed_over"),
                ConversationStates.QUEST_ITEM_QUESTION,
                "Did you already give the sheep to Sato?",
                null);
        // Player answers yes - give reward
        npc.add(
                ConversationStates.QUEST_ITEM_QUESTION,
                ConversationPhrases.YES_MESSAGES,
                new QuestInStateCondition(QUEST_SLOT, "handed_over"),
                ConversationStates.IDLE,
                "Thank you! You don't know how much I have to do these days. " +
                "You really helped me out.",
                new MultipleActions(reward));
        // Player answers no -
        npc.add(
                ConversationStates.QUEST_ITEM_QUESTION,
                ConversationPhrases.NO_MESSAGES,
                new QuestInStateCondition(QUEST_SLOT, "handed_over"),
                ConversationStates.IDLE,
                "Well... ok. But don't forget it. Sato needs the sheep very soon.",
                null);

        // Player asks for quest after solving the quest
        npc.add(ConversationStates.ATTENDING,
                ConversationPhrases.QUEST_MESSAGES,
                new QuestCompletedCondition(QUEST_SLOT),
                ConversationStates.ATTENDING,
                "Sorry. I have nothing to do for you at the moment. But thank you again for your help.",
                null);
    }

    @Override
    public String getRegion() {
        return Region.SEMOS_CITY;
    }

	@Override
	public String getNPCName() {
		return "Nishiya";
	}
}
