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
import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.CollectRequestedItemsAction;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingUntilTimeReachedAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToFutureRandomTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimeReachedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.ItemCollection;
import games.stendhal.server.util.TimeUtil;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import marauroa.common.game.IRPZone;
import org.apache.log4j.Logger;

/**
 * QUEST: Meal for Groongo, The Troublesome Customer
 * <p>
 * PARTICIPANTS:
 * <ul>
 * 	<li> Groongo Rahnnt, The Troublesome Customer
 *	<li> Stefan, The Fado's Hotel Chef
 * </ul>
 * <p>
 * STEPS:
 * <ul>
 * 	<li> Groongo is hungry, asks the player to bring him something to eat,
 *	<li> The player checks with Stefan, he will tell him what he needs to fulfill Groongo's request,
 *	<li> The player has to fetch the required items/foodstuff,
 *	<li> The player talks again with Stefan, gives him the resources,
 *	<li> Stefan tells the player how much time he requires to prepare Groongo's order,
 *	<li> After enough time has elapsed, the player can collect Groongo's order from Stefan,
 *	<li> The player delivers the order to Grongo's,
 *	<li> Groongo is happy and gives the player a reward of some kind.   
 * </ul>
 * <p>
 * REWARD:
 * <ul>
 *	<li> none defined yet
 * </ul>
 * <p>
 * REPETITIONS:
 * <ul>
 *	<li>unlimited
 *	<li>once or twice a day?
 * </ul>
 * <p>
 * @author omero
 */
public class MealForGroongo extends AbstractQuest {
 
    private static Logger logger = Logger.getLogger(MealForGroongo.class);

    /**
     * QUEST_SLOT will be used to hold the different states of the quest.
     *
     * QUEST_SLOT sub slot 0 will hold the main states, which can be:
     * - rejected, the player has refused to undertake the quest
     * - a list of semicolon separated key=value token pairs.
     * - done, the player has completed the quest
     *
     * When the quest is completed,
     * QUEST_SLOT sub slot 1 will be marked with a timestamp 1-2 days in the future
     * 
     */
    public static final String QUEST_SLOT = "meal_for_groongo";

    // quest cannot be repeated before 1-2 days (random)
    private static final int MIN_DELAY = 1 * MathHelper.MINUTES_IN_ONE_DAY;
    private static final int MAX_DELAY = 2 * MathHelper.MINUTES_IN_ONE_DAY;

    // how much XP is given as the reward
    // TODO omero: XP_REWARD needs to be adjusted
    private static final int XP_REWARD = 1000;
    
    @Override
    public void addToWorld() {
        super.addToWorld();
        fillQuestInfo(
            "Meal for Groongo Rahnnt, the troublesome customer",
            "Groongo is hungry and wants to have a meal at Fado's Restaurant.",
            true);
        
        // FIXME omero: this quest will require a yet unknown number of stages
        stageBeginQuest();
        stageCollectIngredients();
        stageDeliverMeal();        
    }

    @Override
    public List<String> getHistory(final Player player) {

        final List<String> res = new ArrayList<String>();

        if (!player.hasQuest(QUEST_SLOT)) {
            return res;
        }

        res.add("I've met Groongo Rahnnt, the troublesome customer in Fado's restaurant.");

        /**
         * NOTE:
         * Retrieving QUEST_SLOT without sub slot index 0 will break the if/then logic below.
         */
        final String questState = player.getQuest(QUEST_SLOT, 0);
        logger.info("Quest state: <" + questState + ">");
        if ("rejected".equals(questState)) {
            res.add("He asked me to bring him a meal of his desire, "
                + " but I had no interest in such an errand.");
        } else if ("done".equals(questState)) {
            res.add("I did bring to him what he asked for.");
            if (isRepeatable(player)) {
                // enough time has passed, inform that the quest is available to be taken.
                res.add("I might ask him again if he wants to have another decent meal.");
            } else {
                // inform about how much time has to pass before the quest can be taken again.
                long timestamp;
                try {
                    timestamp = Long.parseLong(player.getQuest(QUEST_SLOT, 1));
                } catch (final NumberFormatException e) {
                    timestamp = 0;
                }
                final long timeRemaining = (timestamp - System.currentTimeMillis());
                res.add("He will be fine for " + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".");
            }
        } else {
            final ItemCollection missingItems = new ItemCollection();
            final ItemCollection missingIngredients = new ItemCollection();
            /**
             * 
             * NOTE:
             * The quest is running,
             * QUEST_SLOT sub slot 0 holds a semicolon separated list of key=value token pairs.
             * Do not use player.getQuest(QUEST_SLOT, 0) as that would only retrieve the first token pair.
             * 
             */
            missingItems.addFromQuestStateString(player.getQuest(QUEST_SLOT));
            res.add("I've agreed to bring Groongo a decent meal,"
                + " consisting of " + Grammar.enumerateCollection(missingItems.toStringList()));
        }
        return res;
    }
 
    @Override
    public String getSlotName() {
        return QUEST_SLOT;
    }
 
    @Override
        public String getName() {
        return "MealForGroongo";
    }

    @Override
        public int getMinLevel() {
    	// TODO omero: minlevel needs to be adjusted
        return 30;
    }
    
	@Override
	public String getRegion() {
		return Region.FADO_CITY;
	}

	@Override
	public String getNPCName() {
		return "Groongo Rahnnt";
	}

    @Override
    public boolean isRepeatable(final Player player) {
        return new AndCondition(
            new QuestCompletedCondition(QUEST_SLOT),
            new TimeReachedCondition(QUEST_SLOT, 1)).fire(player,null, null);
    }

    @Override
    public boolean isCompleted(final Player player) {
        return new QuestCompletedCondition(QUEST_SLOT).fire(player, null, null);
    }

    private String getRequiredMainDish() {
    	// Main dishes Groongo will require for the quest
    	final List<String> requiredMainDish =
    			Arrays.asList(
    					"paella",
    					"ciorba",
    					"lasagne",
    					"schnitzel",
    					"consomme",
    					"paidakia",
    					"kuskus",
    					"kushari"
    					);

    	return requiredMainDish.get(Rand.randUniform(0,requiredMainDish.size()));
    }
    
    private String getRequiredDessert() {
	    // Desserts Groongo will ask for the quest
	    final List<String> requiredDesserts =
	    		Arrays.asList(
	    				"brigadeiro",
	    				"macedonia",
	    				"slagroomtart",
	    				"vatrushka" //,
	    				//"tarte a la rhubarbe",
	    				//"schwarzwalder kirschtorte",
	    				//"ngat biang",
	    				//"gulab jamun"
	    				);

	    return requiredDesserts.get(Rand.randUniform(0,requiredDesserts.size()));
    }

    /*
	private String getRequiredMainDishFancyName() {
     	// used only to build sentences
     	// to avoid requiring the player to type long and complicated fancy dish names
    	final Map<String, String> requiredMainDishFancyName = new HashMap<String, String>();
    	requiredMainDishFancyName.put("paella", "paella de pescado");
    	requiredMainDishFancyName.put("ciorba", "ciorba de burta cu smantena");
    	requiredMainDishFancyName.put("lasagne", "lasagne alla bolognese");
    	requiredMainDishFancyName.put("jaegerschnitzel", "jaegerschnitzel");
    	requiredMainDishFancyName.put("consomme", "consomme du jour");
    	requiredMainDishFancyName.put("paidakia", "paidakia meh piperi");
    	requiredMainDishFancyName.put("kuskus", "couscous");
    	requiredMainDishFancyName.put("kushari", "kushari");

    }
     */

    /**
     * Returns required ingredients and quantities to collect for preparing the main dish
     *
     * @param mainDish
     * @return A string composed of semicolon separated key=value token pairs.
     */
    /*
    private Map<String, Integer> getRequiredIngredientsForMainDish(final String mainDish) {

    	final Map<String, Integer> requiredIngredients_paella = new TreeMap<String, Integer>();
	    requiredIngredients_paella.put("rice", 1);
	    requiredIngredients_paella.put("onion", 1);
	    requiredIngredients_paella.put("garlic", 1);
	    requiredIngredients_paella.put("tomato", 1);	    
	    requiredIngredients_paella.put("chicken", 1);
	    requiredIngredients_paella.put("perch", 1);
	    requiredIngredients_paella.put("trout", 1);

	    final Map<String, Integer> requiredIngredients_ciorba = new TreeMap<String, Integer>();
	    requiredIngredients_ciorba.put("cow entrails", 1);
	    requiredIngredients_ciorba.put("pinto bean", 1);
	    requiredIngredients_ciorba.put("onion", 1);
	    requiredIngredients_ciorba.put("garlic", 1);
	    requiredIngredients_ciorba.put("milk", 1);
	    requiredIngredients_ciorba.put("salt", 1);
	    requiredIngredients_ciorba.put("pepper", 1);

	    final Map<String, Integer> requiredIngredients_lasagne = new TreeMap<String, Integer>();
	    requiredIngredients_lasagne.put("meat", 1);
	    requiredIngredients_lasagne.put("tomato", 1);
	    requiredIngredients_lasagne.put("carrot", 1);
	    requiredIngredients_lasagne.put("cheese", 1);
	    requiredIngredients_lasagne.put("flour", 1);
	    requiredIngredients_lasagne.put("egg", 1);
	    requiredIngredients_lasagne.put("olive oil", 1);
	    
	    final Map<String, Integer> requiredIngredients_schnitzel = new TreeMap<String, Integer>();
	    requiredIngredients_schnitzel.put("potato", 1);
	    requiredIngredients_schnitzel.put("porcini", 1);
	    requiredIngredients_schnitzel.put("button mushroom", 1);
	    requiredIngredients_schnitzel.put("ham", 1);
	    requiredIngredients_schnitzel.put("meat", 1);
	    requiredIngredients_schnitzel.put("milk", 1);
	    requiredIngredients_schnitzel.put("cheese", 1);

	    final Map<String, Integer> requiredIngredients_consomme = new TreeMap<String, Integer>();
	    requiredIngredients_consomme.put("onion", 1);
	    requiredIngredients_consomme.put("garlic", 1);
	    requiredIngredients_consomme.put("carrot", 1);
	    requiredIngredients_consomme.put("chicken", 1);
	    requiredIngredients_consomme.put("meat", 1);
	    requiredIngredients_consomme.put("sclaria", 1);
	    requiredIngredients_consomme.put("kekik", 1);
	    
	    final Map<String, Integer> requiredIngredients_paidakia = new TreeMap<String, Integer>();
	    requiredIngredients_paidakia.put("meat", 1);
	    requiredIngredients_paidakia.put("pepper", 1);
	    requiredIngredients_paidakia.put("salt", 1);
	    requiredIngredients_paidakia.put("olive oil", 1);
	    requiredIngredients_paidakia.put("potato", 1);
	    requiredIngredients_paidakia.put("kekik", 1);
	    requiredIngredients_paidakia.put("lemon", 1);

	    final Map<String, Integer> requiredIngredients_kushari = new TreeMap<String, Integer>();
	    requiredIngredients_kushari.put("rice", 1);
	    requiredIngredients_kushari.put("lentils", 1);
	    requiredIngredients_kushari.put("onion", 1);
	    requiredIngredients_kushari.put("garlic", 1);
	    requiredIngredients_kushari.put("tomato", 1);
	    requiredIngredients_kushari.put("jalapeno", 1);
	    requiredIngredients_kushari.put("olive oil", 1);
	    
	    final Map<String, Integer> requiredIngredients_kuskus = new TreeMap<String, Integer>();
	    requiredIngredients_kuskus.put("flour", 1);
	    requiredIngredients_kuskus.put("water", 1);
	    requiredIngredients_kuskus.put("courgette", 1);
	    requiredIngredients_kuskus.put("onion", 1);
	    requiredIngredients_kuskus.put("garlic", 1);
	    requiredIngredients_kuskus.put("salt", 1);
	    requiredIngredients_kuskus.put("pepper", 1);
	
	    final HashMap<String, Map<String, Integer>> requiredIngredientsPerMainDish = new HashMap<String, Map<String, Integer>>();
	    requiredIngredientsPerMainDish.put("paella", requiredIngredients_paella);
	    requiredIngredientsPerMainDish.put("ciorba", requiredIngredients_ciorba);
	    requiredIngredientsPerMainDish.put("lasagne", requiredIngredients_lasagne);
	    requiredIngredientsPerMainDish.put("schnitzel", requiredIngredients_schnitzel);
	    requiredIngredientsPerMainDish.put("consomme", requiredIngredients_consomme);
	    requiredIngredientsPerMainDish.put("paidakia", requiredIngredients_paidakia);
	    requiredIngredientsPerMainDish.put("kuskus", requiredIngredients_kuskus);
	    requiredIngredientsPerMainDish.put("kushari", requiredIngredients_kushari);
	    
	    return requiredIngredientsPerMainDish.get(mainDish);
    }
     */
    
    // quest started or rejected
    public void stageBeginQuest() {
     
        final SpeakerNPC npc = npcs.get("Groongo Rahnnt");

        // Player greets Groongo and never asked for a quest is handled in NPC class

        // Player greets Groongo,
        // quest has been rejected in the past
        npc.add(ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new AndCondition(
            		new GreetingMatchesNameCondition(npc.getName()),
            		new QuestInStateCondition(QUEST_SLOT, "rejected")),
            ConversationStates.QUEST_OFFERED,
            "Gah! [insults player]" +
            " I'm all covered with dust after waiting this much..." +
            " Will you bring me a decent #meal now?",
            null
        );

        // Player asks Groongo for a quest,
        // quest is not running
        npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.QUEST_MESSAGES,
            new QuestNotStartedCondition(QUEST_SLOT),
            ConversationStates.QUEST_OFFERED,
            "Bah! [insults player]" +
            " I've been waiting here for so long I'm covered in cobwebs..." +
            " Are you going to bring me a decent #meal now?",
            null
        );

        // Player has done the quest in the past,
        // time enough has elapsed to take the quest again
        npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.QUEST_MESSAGES,
            new AndCondition(
                new QuestCompletedCondition(QUEST_SLOT),
                new TimeReachedCondition(QUEST_SLOT, 1)),
            ConversationStates.QUEST_OFFERED,
            "Ah, here you are! Will you now bring me another decent #meal?",
            null
        );
        
        // Player has done the quest in the past,
        // not enough time has elapsed to take the quest again
        npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.QUEST_MESSAGES,
            new AndCondition(
                new QuestCompletedCondition(QUEST_SLOT),
                new NotCondition(new TimeReachedCondition(QUEST_SLOT, 1))),
            ConversationStates.ATTENDING, null,
            new SayTimeRemainingUntilTimeReachedAction(QUEST_SLOT, 1,
                "I'm not so hungry now... I will be fine for")
        );
        
        // Player is curious about meal when offered the quest
        npc.add(ConversationStates.QUEST_OFFERED,
            "meal",
            new QuestNotStartedCondition(QUEST_SLOT),
            ConversationStates.QUEST_OFFERED,
            "I just want to try something different than soups or pies!" +
            " Are you ready to help?",
            null
        );

        // Player accepts the quest and gets to know what Groongo wants
        // set the quest slot (still have to decide which)
        // switch to stageCollectIngredients, stageQuestRunning...?
        npc.add(ConversationStates.QUEST_OFFERED,
            ConversationPhrases.YES_MESSAGES, null,
            // here we want to transition to the collect ingredients stage
            // turning idle for the time being
            // ConversationStates.QUESTION_1, null,
            ConversationStates.IDLE, null,
            new MultipleActions(
            		new ChatAction() {
            			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
            				// compose a meal with a main dish and a dessert
            				final String decentMeal = getRequiredMainDish() + "=1;" + getRequiredDessert() + "=1;";
            				player.setQuest(QUEST_SLOT, decentMeal);
            			}
            		},
            		// FIXME omero: are we sure we want to award karma here?
            		//new IncreaseKarmaAction(20),
                    // FIXME omero: we don't deal with 'real' ingame items...
                    // new appropriate action is required to say fancy dishes/desserts names
            		// It is important that we say what Groongo wants AFTER we've set the QUEST_SLOT
            		new SayRequiredItemsFromCollectionAction(
                        QUEST_SLOT,
                        "I really want to try some [items]"))
        );

        // Player is not inclined to comply with the request.
        // Player has never rejected quest before,
        // Groongo turns idle and some Karma is lost.
        npc.add(ConversationStates.QUEST_OFFERED,
            ConversationPhrases.NO_MESSAGES,
            new AndCondition(
                new QuestNotActiveCondition(QUEST_SLOT),
                new QuestNotInStateCondition(QUEST_SLOT, "rejected")),
            ConversationStates.IDLE,
            "Stop pestering me and get lost in a dungeon then!",
            new MultipleActions(
            		new SetQuestAction(QUEST_SLOT, "rejected"),
            		new DecreaseKarmaAction(20.0))
		);

        // Player has refused the quest in the past,
        // Player is still not inclined to comply with the request,
        // Groongo turns idle and some (more) Karma is lost.
        npc.add(ConversationStates.QUEST_OFFERED,
            ConversationPhrases.NO_MESSAGES,
            new QuestInStateCondition(QUEST_SLOT, "rejected"),
            ConversationStates.IDLE,
            "Stat away from me and get lost in a forest then!",
            new MultipleActions(
            		new SetQuestAction(QUEST_SLOT, "rejected"),
            		new DecreaseKarmaAction(100.0))
        );
    }

    public void stageCollectIngredients() {

        final SpeakerNPC npc = npcs.get("Stefan");

    }
    
    public void stageDeliverMeal() {

        final SpeakerNPC npc = npcs.get("Groongo Rahnnt");

        // Player says his greetings to Groongo and the quest is running
        npc.add(ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new AndCondition(
            		new GreetingMatchesNameCondition(npc.getName()),
            		new QuestActiveCondition(QUEST_SLOT)),
            ConversationStates.QUESTION_1,
            "Finally! Do you have my #meal already?",
            null);

        // Player says meal to be reminded of what is still missing
        npc.add(ConversationStates.QUESTION_1,
            "meal", null,
            ConversationStates.QUESTION_1,
            null,
            // FIXME omero: we don't deal with 'real' ingame items...
            // new appropriate action is required to say fancy dishes/desserts names            
            new SayRequiredItemsFromCollectionAction(
                QUEST_SLOT,
                "Hey! I'm still waiting for my [items]. Do you have some?"));

        // Player answers no when asked if he has brought any items
        npc.add(ConversationStates.QUESTION_1,
            ConversationPhrases.NO_MESSAGES,
            new QuestNotCompletedCondition(QUEST_SLOT),
            ConversationStates.IDLE,
            "Then hurry up, go and fetch it!",
            null);

        /*	
        npc.add(ConversationStates.QUESTION_1,
            ConversationPhrases.YES_MESSAGES, null,
            ConversationStates.QUESTION_1,
            "Fine, what did you bring?",
            null
        );
    	 */

    }
}