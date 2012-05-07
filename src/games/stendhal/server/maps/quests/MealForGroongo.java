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
import games.stendhal.server.entity.item.Item;
// TODO omero: do we need this for rewarding the player?
//import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.DropInfostringItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasInfostringItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.condition.TimeReachedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.ItemCollection;
import games.stendhal.server.util.TimeUtil;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// TODO omero: do we want to place the decent meal on Groongo's table?
//import marauroa.common.game.IRPZone;
import org.apache.log4j.Logger;

/**
 * FIXME omero: investigate 'ambiguous state transition'
 * WARN  [marauroad ] Engine                   (249 ) - Stefan: Adding ambiguous state transition: [QUESTION_1,yes|ok|yep|sure,IDLE,null] existingAction='games.stendhal.server.maps.quests.MealForGroongo$collectAllRequestedIngredientsAtOnceAction@1c52f9e' newAction='games.stendhal.server.maps.quests.MealForGroongo$collectAllRequestedIngredientsAtOnceAction@b94ec7'
 * WARN  [marauroad ] Engine                   (249 ) - Stefan: Adding ambiguous state transition: [QUESTION_1,yes|ok|yep|sure,IDLE,null] existingAction='games.stendhal.server.maps.quests.MealForGroongo$collectAllRequestedIngredientsAtOnceAction@1c52f9e' newAction='games.stendhal.server.maps.quests.MealForGroongo$collectAllRequestedIngredientsAtOnceAction@b94ec7'
 * WARN  [marauroad ] Engine                   (249 ) - Stefan: Adding ambiguous state transition: [QUESTION_1,yes|ok|yep|sure,IDLE,null] existingAction='games.stendhal.server.maps.quests.MealForGroongo$collectAllRequestedIngredientsAtOnceAction@1c52f9e' newAction='games.stendhal.server.maps.quests.MealForGroongo$collectAllRequestedIngredientsAtOnceAction@b94ec7'
 * WARN  [marauroad ] Engine                   (249 ) - Stefan: Adding ambiguous state transition: [QUESTION_1,yes|ok|yep|sure,IDLE,null] existingAction='games.stendhal.server.maps.quests.MealForGroongo$collectAllRequestedIngredientsAtOnceAction@1c52f9e' newAction='games.stendhal.server.maps.quests.MealForGroongo$collectAllRequestedIngredientsAtOnceAction@b94ec7'
 *
 * FIXME omero: investigate 'stackable item'
 * 
 * NOTE:
 * ---------------------------
 * We use a separate sub slot for for main dish and dessert
 * because we want to keep full history of what's going on
 *
 * use this quest slot template for testing
 * player.setQuest(QUEST_SLOT, "prepare_decentmeal;ciorba;slagroomtart;1336246794962");
 * ---------------------------
 */

/**
 * QUEST: Meal for Groongo, The Troublesome Customer
 * <p>
 * PARTICIPANTS:
 * <ul>
 *  <li> Groongo Rahnnt, The Troublesome Customer
 *  <li> Stefan, The Fado's Hotel Restaurant Chef
 * </ul>
 *
 * FIXME omero: Quest steps are not fully defined yet.
 * STEPS:
 * <ul>
 *  <li> Groongo is hungry, asks the player to bring him a decent meal,
 *  <li> The player talks to Stefan and he will tell him what he needs to fulfill Groongo's request,
 *  <li> The player goes fetching the ingredients for the main dish,
 *  <li> The player brings Stefan the ingredients he needs,
 *  <li> Stefan tells the player to ask Groongo which dessert he would like along the main dish,
 *  <li> The player checks back with Groongo to ask for a dessert of his choice,
 *  <li> The player tells Stefan which dessert Groongo wants along with the main dish,
 *  <li> Stefan tells the player which ingredients he's missing for preparing the dessert,
 *  <li> The player goes fetching the ingredients for the main dish and brings them to Stefan,   
 *  <li> Stefan tells the player how much time (10-15mins) he requires to prepare Groongo's order,
 *  <li> After enough time has elapsed, the player can collect Groongo's order from Stefan,
 *  <li> The player may deliver the decent meal to Grongo
 *  <li> Groongo is finaly happy and gives the player a reward of some kind.   
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> none defined yet
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 *  <li>unlimited
 *  <li>once or twice a day?
 * </ul>
 *
 * @author omero
 */
public class MealForGroongo extends AbstractQuest {
 
    private static Logger logger = Logger.getLogger(MealForGroongo.class);

    /**
     * FIXME omero: Quest states are not fully defined yet.
     *
     * QUEST_SLOT will be used to hold the different states of the quest.
     *
     * QUEST_SLOT sub slot 0
     * will hold the main states, which can be:
     * - rejected, the player has refused to undertake the quest
     * - fetch_maindish, the player is collecting ingredients for that
     * - fetch_dessert, the player is collecting the ingredients for that
     * - choose_dessert, the player needs to ask Groongo which dessert he wants
     * - deliver_decentmeal, meal for Groongo is ready 
     * - done, the player has completed the quest
     *
     *
     * QUEST_SLOT sub slot 1
     * - a main dish short name
     *
     * QUEST_SLOT sub slot 2
     * - a dessert short name
     *
     * QUEST_SLOT sub slot 3
     * - when quest is running, holds a timestamp for waiting before decent meal is ready
     * - when quest is done, holds the timestamp the quest was last completed
     *
     * QUEST_SLOT sub slot 4
     * - when quest is done, gets incremented
     *
     * When the quest is completed,
     * QUEST_SLOT sub slot 3 will be marked with a timestamp
     * QUEST_SLOT sub slot 4 will be incremented (number of times quest was completed)
     */
    public static final String QUEST_SLOT = "meal_for_groongo";

    //How long it takes Chef Stefan to prepare a decent meal (main dish and dessert)
    private static final int MEALREADY_DELAY = 5;
    
    //Every when the quest can be repeated
    private static final int REPEATQUEST_DELAY = 1 * MathHelper.MINUTES_IN_ONE_DAY;
        
    // how much XP is given as the reward
    // FIXME omero: XP_REWARD needs to be adjusted
    private static final int XP_REWARD = 1000;

    // which main dishes Groongo will ask for the quest
    // FIXME omero: REQUIRED_MAIN_DISHES is still subject to changes
    private static final List<String> REQUIRED_MAIN_DISHES =
            Arrays.asList(
                "paella",
                "ciorba",
                "lasagne",
                "schnitzel",
                "consomme",
                "paidakia",
                "couscous",
                "kushari"
            );

    // which desserts Groongo will ask for the quest
    // FIXME omero: REQUIRED_DESSERTS is still subject to changes

    private static final List<String> REQUIRED_DESSERTS =
            Arrays.asList(
                "macedonia",
                "slagroomtart",
                "brigadeiro",
                "vatrushka"
                //"tarte a la rhubarbe",
                //"schwarzwalder kirschtorte",
                //"ngat biang",
                //"gulab jamun"
            );


    @Override
    public void addToWorld() {
        super.addToWorld();
        fillQuestInfo(
            "Meal for Groongo Rahnnt",
            "Groongo is hungry and wants to have a meal at Fado's Hotel Restaurant.",
            true);

        // FIXME omero: this quest will require a yet unknown number of stages
        stageBeginQuest();
        stageWaitForMeal();
        stageCollectIngredientsForMainDish();
        stageCheckForDessert();
        stageCollectIngredientsForDessert();
        stageDeliverMeal();        
    }


    @Override
    public List<String> getHistory(final Player player) {

        final List<String> res = new ArrayList<String>();

        if (!player.hasQuest(QUEST_SLOT)) {
            return res;
        }

        res.add("I've met Groongo Rahnnt in Fado's Hotel Restaurant.");
        final String questState = player.getQuest(QUEST_SLOT, 0);
        
        logger.warn("Quest state: <" + questState + ">");
        
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

        	// FIXME omero: the quest states are not fully defined yet
        	
        	final ItemCollection missingIngredients = new ItemCollection();
        	String ingredients = "";        	
        	if ("fetch_maindish".equals(questState)) {
        		ingredients = getRequiredIngredientsForMainDish(player.getQuest(QUEST_SLOT,1));
        		missingIngredients.addFromQuestStateString(ingredients);
            	res.add("Groongo wants to try " +
            			Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 1))) +
            			" and I'm helping Chef Stefan finding the ingredients to prepare it: " +
            			Grammar.enumerateCollection(missingIngredients.toStringList()) + "."
            	);
        	} else if ("fetch_dessert".equals(questState)) {
	    		ingredients = getRequiredIngredientsForDessert(player.getQuest(QUEST_SLOT, 2));
	    		missingIngredients.addFromQuestStateString(ingredients);
	        	res.add("Groongo also wants " +
	        			Grammar.a_noun(getRequiredDessertFancyName(player.getQuest(QUEST_SLOT, 2))) +
	        			" and I'm helping Chef Stefan finding the ingredients to prepare it: " +
	        			Grammar.enumerateCollection(missingIngredients.toStringList()) + "."
	        	);
        	}

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

    // Groongo uses this to select a random main dish for the quest
    // All main dishes are temporary for developing purposes, subject to change
    private String getRequiredMainDish() {
        return REQUIRED_MAIN_DISHES.get(Rand.rand(REQUIRED_MAIN_DISHES.size()));
    }

    // Groongo uses this to select a random dessert for the quest
    // All desserts are temporary for developing purposes, subject to change 
    private String getRequiredDessert() {
        return REQUIRED_DESSERTS.get(Rand.rand(REQUIRED_DESSERTS.size()));
    }

    // used by both Groongo and Stefan only to build sentences
    // to avoid requiring the player to type long and complicated fancy dish names
    private String getRequiredMainDishFancyName(final String requiredMainDish) {
        final Map<String, String> requiredMainDishFancyName = new HashMap<String, String>();
        requiredMainDishFancyName.put("paella", "paella de pescado");
        requiredMainDishFancyName.put("ciorba", "ciorba de burta cu smantena");
        requiredMainDishFancyName.put("lasagne", "lasagne alla bolognese");
        requiredMainDishFancyName.put("schnitzel", "jaegerschnitzel mit pilzen");
        requiredMainDishFancyName.put("consomme", "consomme du jour");
        requiredMainDishFancyName.put("paidakia", "paidakia meh piperi");
        requiredMainDishFancyName.put("couscous", "couscous");
        requiredMainDishFancyName.put("kushari", "kushari");
        
        return requiredMainDishFancyName.get(requiredMainDish);

    }

    // used by both Groongo and Stefan only to build sentences
    // to avoid requiring the player to type long and complicated fancy dessert names
    private String getRequiredDessertFancyName(final String requiredDessert) {
        final Map<String, String> requiredDessertFancyName = new HashMap<String, String>();
        requiredDessertFancyName.put("brigadeiro", "brigadeiro");
        requiredDessertFancyName.put("macedonia", "macedonia di frutta");
        requiredDessertFancyName.put("slagroomtart", "slagroomtart");
        requiredDessertFancyName.put("vatrushka", "vatrushka");

        return requiredDessertFancyName.get(requiredDessert);
    }

    // used by Stefan
    /**
     * Returns required ingredients and quantities to collect for preparing the main dish
     *
     * @param requiredMainDish
     * @return A string composed of semicolon separated key=value token pairs.
     */
    private String getRequiredIngredientsForMainDish(final String requiredMainDish) {

    	// All not-yet-existing ingredients commented out for testing purposes
        // All ingredients are temporary for developing purposes, subject to change
    	
        final HashMap<String, Integer> requiredIngredients_paella = new HashMap<String, Integer>();
        //requiredIngredients_paella.put("rice", 1);
        requiredIngredients_paella.put("onion", 1);
        requiredIngredients_paella.put("garlic", 1);
        requiredIngredients_paella.put("tomato", 1);        
        requiredIngredients_paella.put("chicken", 1);
        requiredIngredients_paella.put("perch", 1);
        requiredIngredients_paella.put("trout", 1);

        final HashMap<String, Integer> requiredIngredients_ciorba = new HashMap<String, Integer>();
        //requiredIngredients_ciorba.put("cow entrails", 1);
        //requiredIngredients_ciorba.put("pinto bean", 1);
        requiredIngredients_ciorba.put("onion", 1);
        requiredIngredients_ciorba.put("garlic", 1);
        requiredIngredients_ciorba.put("milk", 1);
        //requiredIngredients_ciorba.put("salt", 1);
        //requiredIngredients_ciorba.put("pepper", 1);

        final HashMap<String, Integer> requiredIngredients_lasagne = new HashMap<String, Integer>();
        requiredIngredients_lasagne.put("meat", 1);
        requiredIngredients_lasagne.put("tomato", 1);
        requiredIngredients_lasagne.put("carrot", 1);
        requiredIngredients_lasagne.put("cheese", 1);
        requiredIngredients_lasagne.put("flour", 1);
        requiredIngredients_lasagne.put("egg", 1);
        //requiredIngredients_lasagne.put("olive oil", 1);
        
        final HashMap<String, Integer> requiredIngredients_schnitzel = new HashMap<String, Integer>();
        //requiredIngredients_schnitzel.put("potato", 1);
        requiredIngredients_schnitzel.put("porcini", 1);
        requiredIngredients_schnitzel.put("button mushroom", 1);
        requiredIngredients_schnitzel.put("ham", 1);
        requiredIngredients_schnitzel.put("meat", 1);
        requiredIngredients_schnitzel.put("milk", 1);
        requiredIngredients_schnitzel.put("cheese", 1);

        final HashMap<String, Integer> requiredIngredients_consomme = new HashMap<String, Integer>();
        requiredIngredients_consomme.put("onion", 1);
        requiredIngredients_consomme.put("garlic", 1);
        requiredIngredients_consomme.put("carrot", 1);
        requiredIngredients_consomme.put("chicken", 1);
        requiredIngredients_consomme.put("meat", 1);
        requiredIngredients_consomme.put("sclaria", 1);
        requiredIngredients_consomme.put("kekik", 1);
        
        final HashMap<String, Integer> requiredIngredients_paidakia = new HashMap<String, Integer>();
        requiredIngredients_paidakia.put("meat", 1);
        //requiredIngredients_paidakia.put("pepper", 1);
        //requiredIngredients_paidakia.put("salt", 1);
        //requiredIngredients_paidakia.put("olive oil", 1);
        //requiredIngredients_paidakia.put("potato", 1);
        requiredIngredients_paidakia.put("kekik", 1);
        //requiredIngredients_paidakia.put("lemon", 1);

        final HashMap<String, Integer> requiredIngredients_kushari = new HashMap<String, Integer>();
        //requiredIngredients_kushari.put("rice", 1);
        //requiredIngredients_kushari.put("lentils", 1);
        requiredIngredients_kushari.put("onion", 1);
        requiredIngredients_kushari.put("garlic", 1);
        requiredIngredients_kushari.put("tomato", 1);
        //requiredIngredients_kushari.put("jalapeno", 1);
        //requiredIngredients_kushari.put("olive oil", 1);
        
        final HashMap<String, Integer> requiredIngredients_couscous = new HashMap<String, Integer>();
        requiredIngredients_couscous.put("flour", 1);
        requiredIngredients_couscous.put("water", 1);
        requiredIngredients_couscous.put("courgette", 1);
        requiredIngredients_couscous.put("onion", 1);
        requiredIngredients_couscous.put("garlic", 1);
        //requiredIngredients_couscous.put("salt", 1);
        //requiredIngredients_couscous.put("pepper", 1);

        final HashMap<String, HashMap<String, Integer>> requiredIngredientsForMainDish = new HashMap<String, HashMap<String, Integer>>();
        requiredIngredientsForMainDish.put("paella", requiredIngredients_paella);
        requiredIngredientsForMainDish.put("ciorba", requiredIngredients_ciorba);
        requiredIngredientsForMainDish.put("lasagne", requiredIngredients_lasagne);
        requiredIngredientsForMainDish.put("schnitzel", requiredIngredients_schnitzel);
        requiredIngredientsForMainDish.put("consomme", requiredIngredients_consomme);
        requiredIngredientsForMainDish.put("paidakia", requiredIngredients_paidakia);
        requiredIngredientsForMainDish.put("couscous", requiredIngredients_couscous);
        requiredIngredientsForMainDish.put("kushari", requiredIngredients_kushari);


        String ingredients = "";
        final HashMap<String, Integer>  requiredIngredients = requiredIngredientsForMainDish.get(requiredMainDish);
        for (final Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
            ingredients = ingredients + entry.getKey() + "=" + entry.getValue() + ";";
        }

        //return requiredIngredientsForMainDish.get(requiredMainDish);
        return ingredients;
        
    }

    // used by Stefan
    /**
     * Returns required ingredients and quantities to collect for preparing the dessert
     *
     * @param requiredDessert
     * @return A string composed of semicolon separated key=value token pairs.
     */
    private String getRequiredIngredientsForDessert(final String requiredDessert) {

        // All ingredients are temporary for developing purposes, subject to change
    	// All not-yet-existing ingredients commented out for testing purposes
    	
        final HashMap<String, Integer> requiredIngredients_brigadeiro = new HashMap<String, Integer>();
        requiredIngredients_brigadeiro.put("milk", 1);
        requiredIngredients_brigadeiro.put("sugar", 2);
        requiredIngredients_brigadeiro.put("butter", 4);        
        //requiredIngredients_brigadeiro.put("coconut", 3); // will be cacao pod... monkeys?

        final HashMap<String, Integer> requiredIngredients_macedonia = new HashMap<String, Integer>();
        requiredIngredients_macedonia.put("banana", 5);
        requiredIngredients_macedonia.put("apple", 7);
        requiredIngredients_macedonia.put("pear", 9);
        requiredIngredients_macedonia.put("watermelon", 4);

        final HashMap<String, Integer> requiredIngredients_slagroomtart = new HashMap<String, Integer>();
        requiredIngredients_slagroomtart.put("milk", 13);
        requiredIngredients_slagroomtart.put("sugar", 14);
        requiredIngredients_slagroomtart.put("egg", 15);
        //requiredIngredients_slagroomtart.put("pineapple", 16);

        final HashMap<String, Integer> requiredIngredients_vatrushka = new HashMap<String, Integer>();
        requiredIngredients_vatrushka.put("flour", 2);
        requiredIngredients_vatrushka.put("sugar", 4);
        requiredIngredients_vatrushka.put("cheese", 8);
        requiredIngredients_vatrushka.put("cherry", 16);
        
        final HashMap<String, HashMap<String, Integer>> requiredIngredientsForDessert = new HashMap<String, HashMap<String, Integer>>();
        requiredIngredientsForDessert.put("brigadeiro", requiredIngredients_brigadeiro);
        requiredIngredientsForDessert.put("macedonia", requiredIngredients_macedonia);
        requiredIngredientsForDessert.put("slagroomtart", requiredIngredients_slagroomtart);
        requiredIngredientsForDessert.put("vatrushka", requiredIngredients_vatrushka);

        String ingredients = "";
        final HashMap<String, Integer>  requiredIngredients = requiredIngredientsForDessert.get(requiredDessert);
        for (final Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
            ingredients = ingredients + entry.getKey() + "=" + entry.getValue() + ";";
        }

        // FIXME omero: should we just return the HashMap thing 
        // and build the semicolon separated string of ingredient=quantity token pairs elsewhere?
        //return requiredIngredientsForDessert.get(requiredDessert);

        return ingredients;

    }

    // Stefan uses this to advance the quest: 
    // - after the player has gathered all of required ingredients for the main dish
    // - after the player has asked Groongo which dessert he'd like along the main dish
    // - after the player has gathered all of required ingredients for the dessert
    class advanceQuestInProgressAction implements ChatAction {
        public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {

        	if ("fetch_maindish".equals(player.getQuest(QUEST_SLOT, 0))) {
            	player.setQuest(QUEST_SLOT, 0, "choose_dessert");
            	SpeakerNPC.say(
        			"Excellent! I'll start preparing " +
        			Grammar.article_noun(
        					getRequiredMainDishFancyName(
        							player.getQuest(QUEST_SLOT, 1)), true) + " immediately." +
        			" Meanwhile, please go ask our troublesome customer" +
        			" which #dessert he'd like to have along with it!");
            } else if ("checked_dessert".equals(player.getQuest(QUEST_SLOT, 0))) {
            	player.setQuest(QUEST_SLOT, 0, "fetch_dessert");
            	SpeakerNPC.say("A delicious choice indeed!");
            } else if ("fetch_dessert".equals(player.getQuest(QUEST_SLOT, 0))) {
                player.setQuest(QUEST_SLOT, 0, "prepare_decentmeal");
                SpeakerNPC.say("FIXME omero: the meal will be ready soon");
            } else if ("prepare_decentmeal".equals(player.getQuest(QUEST_SLOT, 0))) {
            	final Item decentMeal = SingletonRepository.getEntityManager().getItem("decent meal");
            	final String decentMealDescription =
            			Grammar.a_noun(
        					getRequiredMainDishFancyName(
    							player.getQuest(QUEST_SLOT, 1))) +
            			" as the main dish and " +
            			Grammar.a_noun(
        					getRequiredDessertFancyName(
    							player.getQuest(QUEST_SLOT, 2))) +
						" for dessert.";
            	decentMeal.setInfoString("Decent Meal for Groongo");
            	decentMeal.setBoundTo("Groongo Rahnnt");
            	decentMeal.setDescription(
        			"You see a dome-covered decent meal which consists of " + 
					decentMealDescription);
        		if (player.equipToInventoryOnly(decentMeal)) {
                	player.setQuest(QUEST_SLOT, 0, "deliver_decentmeal");
            		SpeakerNPC.say(
        				"Here you are! I've just finished preparing " + decentMealDescription +
            		    " You should now bring this decent meal to our troublesome customer at once." +
        				" And be very careful not to spoil it or drop it along your way!"
        			);
        		} else {
        			SpeakerNPC.say(
        				"The meal for our troublesome customer is ready," +
        				" please come back when you can bring it to him!"	
        			);
        		}
            }

            logger.warn("Quest state <" + player.getQuest(QUEST_SLOT) + ">");

        }
    }

    // Groongo uses this to select one main dish among the defined ones
    // the quest is initiated
    class chooseMainDishAction implements ChatAction {
        public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {
            final String requiredMainDish = getRequiredMainDish();

            player.setQuest(QUEST_SLOT, 0, "fetch_maindish");
            player.setQuest(QUEST_SLOT, 1, requiredMainDish);
            
            SpeakerNPC.say(
                    "Today I really feel like trying " +
                    Grammar.a_noun(getRequiredMainDishFancyName(requiredMainDish)) +
                    ". Now go ask Chef Stefan to prepare my #" + requiredMainDish + ", at once!"
            );
            
            logger.warn("Quest state <" + player.getQuest(QUEST_SLOT) + ">");

        }
    }

    // Groongo uses this to tell the player which dessert he'd like along with the main dish
    // the quest is advanced further
    class chooseDessertAction implements ChatAction {
        public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {
            final String requiredMainDish = player.getQuest(QUEST_SLOT, 1);
            final String requiredDessert = getRequiredDessert();

            //player.setQuest(QUEST_SLOT, 0, "fetch_dessert");
            player.setQuest(QUEST_SLOT, 0, "checked_dessert");
            player.setQuest(QUEST_SLOT, 2, requiredDessert);
            
            SpeakerNPC.say(
                    "Indeed, I shouldn't have forgot that! With " +
                    Grammar.article_noun(getRequiredMainDishFancyName(requiredMainDish), true) +
                    " I will try " +
                    Grammar.a_noun(getRequiredDessertFancyName(requiredDessert)) +
                    ". Now go ask Chef Stefan to prepare my #dessert, at once!"
            );

            logger.warn("Quest state <" + player.getQuest(QUEST_SLOT) + ">");
        }
    }

    /*
    class checkDecentMealAction implements ChatAction {
        public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {
        	if ( player.isEquipped("decent meal")) {
            	for ( final Item decentMeal : player.getAllEquipped("decent meal")) {
            		final String decentMealInfoString = decentMeal.getInfoString();
            		if ("Decent Meal for Groongo".equals(decentMealInfoString)) {
            			String timestamp = Long.toString(System.currentTimeMillis());
            			player.setQuest(QUEST_SLOT, 3, timestamp);
            			player.setQuest(QUEST_SLOT, 0, "done");
            			player.drop(decentMeal);
            		}
            	}
        	} else {
        		SpeakerNPC.say("GAH! Liar... you didn't bring me the meal I asked for!");
        	}
            logger.warn("Quest state <" + player.getQuest(QUEST_SLOT) + ">");

        }
    }
    */
    
    // Groongo uses this to remind the player of what he has to bring him currently
    // FIXME omero: checkQuestInProgressAction needs to discriminate in which stage the quest currently is
    class checkQuestInProgressAction implements ChatAction {
        public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {
        	final String questState = player.getQuest(QUEST_SLOT, 0);
        	String meal = "";
        	String question = "";
        	if  (
        			"fetch_maindish".equals(questState)) {
        		meal = Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT,1)));
        		question = "Did you bring that for me?";
        	} else if (        			
        			"choose_dessert".equals(questState)) {
        		meal = Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT,1)));
        		//question = " Should I also choose some #dessert to go with that?";
        		// not a question but a way to give the player a hint about how to 'ask' for which dessert
        		question = " Maybe I should also choose some #dessert to go with that...";
        	} else if (        			
        			"fetch_dessert".equals(questState) ||
        			"deliver_decentmeal".equals(questState)) {
        		meal = 
        			Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT,1))) +
        			" as the main dish and " +
        			Grammar.a_noun(getRequiredDessertFancyName(player.getQuest(QUEST_SLOT,2))) +
        			" for dessert";
        		question = "Did you bring those for me?";
        	}

            SpeakerNPC.say(
                    "Bah! I'm still waiting for " + meal + ". That's what I call a decent meal! " + question
            );

            logger.warn("Quest state <" + player.getQuest(QUEST_SLOT) + ">");

        }
    }

    // Stefan uses this to tell the player what ingredients he needs
    // for preparing the main dish
    class checkIngredientsForMainDishAction implements ChatAction {
        public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {

        	final ItemCollection missingIngredients = new ItemCollection();
            missingIngredients.addFromQuestStateString(
            		getRequiredIngredientsForMainDish(player.getQuest(QUEST_SLOT, 1)));

            SpeakerNPC.say(
                    "Ah! Our troublesome customer has asked for " +
                    Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT,1))) +
                    " this time. For that I'll need some ingredients that at the moment I'm missing: " +
                    Grammar.enumerateCollection(missingIngredients.toStringListWithHash()) +
                    ". Do you happen to have them all with you already?"
            );
            
            logger.warn("Quest state <" + player.getQuest(QUEST_SLOT) + ">");
            
        }
    }

    // Stefan uses this to tell the player what ingredients he needs
    // for preparing the dessert
    class checkIngredientsForDessertAction implements ChatAction {
        public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {

        	final ItemCollection missingIngredients = new ItemCollection();
            missingIngredients.addFromQuestStateString(
            		getRequiredIngredientsForDessert(player.getQuest(QUEST_SLOT, 2)));

            SpeakerNPC.say(
                    "Oh! So our troublesome customer decided to have " +
                    Grammar.a_noun(getRequiredDessertFancyName(player.getQuest(QUEST_SLOT, 2))) +
                    " for dessert. For that I'll need some other ingredients that I'm missing: " +
                    Grammar.enumerateCollection(missingIngredients.toStringListWithHash()) +
                    ". Do you happen to have any of those already with you?"
            );
            logger.warn("Quest state <" + player.getQuest(QUEST_SLOT) + ">");
        }
    }
    
    // Stefan uses this
    // when the player has said he has all the ingredients
    // for preparing either the main dish or the dessert
    class collectAllRequestedIngredientsAtOnceAction implements ChatAction {

    	private final ChatAction triggerActionOnCompletion;
    	private final ConversationStates stateAfterCompletion;
    	
    	public collectAllRequestedIngredientsAtOnceAction (ChatAction completionAction, ConversationStates stateAfterCompletion) {

    		this.triggerActionOnCompletion = completionAction;
    		this.stateAfterCompletion = stateAfterCompletion;
    		
    	}

    	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
    		ItemCollection missingIngredients = getMissingIngredients(player);
			boolean playerHasAllIngredients = true;
			// preliminary check. we don't take anything from the player, yet
    		for (final Map.Entry<String, Integer> ingredient : missingIngredients.entrySet()) {
    			final int amount = player.getNumberOfEquipped(ingredient.getKey());
    			if ( amount < ingredient.getValue()) {
    				raiser.say(
    						"Not enough " + 
    						Grammar.plnoun(ingredient.getValue(), ingredient.getKey()) +
    						" you have brought me! I said I need " +
    						ingredient.getValue() + " of " +
    						Grammar.thatthose(ingredient.getValue()) + "...");
    				playerHasAllIngredients = false;
    				//don't bother checking further after first not met requirement 
    				break;
    			}
    		}
    		
    		if (playerHasAllIngredients) {
    			// take all the ingredients
    			for (final Map.Entry<String, Integer> ingredient : missingIngredients.entrySet()) {
    				player.drop(ingredient.getKey(), ingredient.getValue());
    			}
    			triggerActionOnCompletion.fire(player, sentence, raiser);
    			raiser.setCurrentState(this.stateAfterCompletion);
    		}
    		
            logger.warn("Quest state <" + player.getQuest(QUEST_SLOT) + ">");

    	}

   		ItemCollection getMissingIngredients(final Player player) {

        	final ItemCollection missingIngredients = new ItemCollection();
        	final String questState = player.getQuest(QUEST_SLOT, 0);
        	String ingredients = "";
        	if  ("fetch_maindish".equals(questState)) {

        		ingredients = getRequiredIngredientsForMainDish(player.getQuest(QUEST_SLOT,1));
        		missingIngredients.addFromQuestStateString(ingredients);

        	} else if ("fetch_dessert".equals(questState)) {

        		ingredients = getRequiredIngredientsForDessert(player.getQuest(QUEST_SLOT, 2));
        		missingIngredients.addFromQuestStateString(ingredients);

        	}
   			return missingIngredients;
    	}
    }

    // The quest is started or rejected by first interacting with Groongo Rahnnt
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
            " I've been waiting so long that I've now got cobwebs under my armpits..." +
            " Are you going to bring me a decent #meal now?",
            null
        );

        // Player has done the quest in the past,
        // time enough has elapsed to take the quest again
        // FIXME omero: sub slot to use for timestamp?
        /*
        npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.QUEST_MESSAGES,
            new AndCondition(
                new QuestCompletedCondition(QUEST_SLOT),
                new TimeReachedCondition(QUEST_SLOT, 3)),
            ConversationStates.QUEST_OFFERED,
            "Ah, here you are! Will you now bring me another decent #meal?",
            null
        );
        */

        // Player has done the quest in the past,
        // not enough time has elapsed to take the quest again
        // FIXME omero: sub slot to use for timestamp?
        npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.QUEST_MESSAGES,
            new AndCondition(
                new QuestCompletedCondition(QUEST_SLOT),
                new NotCondition(new TimeReachedCondition(QUEST_SLOT, 3))),
            ConversationStates.ATTENDING, null,
            new SayTimeRemainingAction(QUEST_SLOT, 3, REPEATQUEST_DELAY, 
                "I'm not so hungry now... I will be fine for")
        );

        // Player is curious about meal when offered the quest
        // quest not running yet
        npc.add(ConversationStates.QUEST_OFFERED,
            "meal",
            new QuestNotStartedCondition(QUEST_SLOT),
            ConversationStates.QUEST_OFFERED,
            "I just want to have a decent meal and try something different than soups or pies!" +
            " Will you bring me what I will ask?",
            null
        );

        // Player has just accepted the quest,
        // Player says short name of a main dish,
        // give final hints, quest is running
        Iterator<String> i = REQUIRED_MAIN_DISHES.iterator();
        while (i.hasNext()) {
            npc.add(ConversationStates.QUEST_STARTED,
                    i.next(),
                    new QuestInStateCondition(QUEST_SLOT, 0, "fetch_maindish"),
                    ConversationStates.IDLE,
                    "I'm sure Chef Stefan knows how to prepare that, you'll find him in the kitchen. Now go!",
                    null
            );
        }

        // Player has just accepted the quest,
        // Player says 'meal' again,
        // give some hints, quest is running
        npc.add(ConversationStates.QUEST_STARTED,
                "meal",
                new QuestNotStartedCondition(QUEST_SLOT),
                ConversationStates.QUEST_STARTED,
                "I've just told you what I want! Now go, and tell Chef Stefan to prepare it at once!",
                null
            );
        
        // Player accepts the quest and gets to know what Groongo wants
        // quest is started
        npc.add(ConversationStates.QUEST_OFFERED,
            ConversationPhrases.YES_MESSAGES,
            new QuestNotStartedCondition(QUEST_SLOT),
            ConversationStates.QUEST_STARTED,
            null,
            new chooseMainDishAction()
            /*
             * TODO omero: delete what follows once quest is working as expected.
             * All of the following code has been moved into initQuestAction,
             * it is kept here as a reminder.
             * 
            new MultipleActions(
                    new ChatAction() {
                        public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {

                            final String requiredMainDish = getRequiredMainDish();

							//ATTEMPT 1)
                            //String requiredIngredients = "";
                            final Map<String, Integer> requiredIngredientsForMainDish = getRequiredIngredientsForMainDish(requiredMainDish); 
                            for (final Map.Entry<String, Integer> entry : requiredIngredientsForMainDish.entrySet()) {
                                requiredIngredients = requiredIngredients + entry.getKey() + "=" + entry.getValue() + ";";
                            }
                            final Map<String, Integer> requiredIngredientsForDessert = getRequiredIngredientsForDessert(requiredDessert); 
                            for (final Map.Entry<String, Integer> entry : requiredIngredientsForDessert.entrySet()) {
                                requiredIngredients = requiredIngredients + entry.getKey() + "=" + entry.getValue() + ";";
                            }
                            //player.setQuest(QUEST_SLOT, "inprogress" + ";" + requiredMainDish + ";" + requiredIngredients);
                            //
                            //  trying to retrieve quest slot whith index 2 will result in getting "carrot=1",
                            //  and not "carrot=1;cheese=1;egg=1;flour=1;meat=1;olive oil=1;tomato=1"
                            //
							//ATTEMPT 2) (have the getRequiredIngredientsForMainDish() return an HashMap)
                            final HashMap<String, Integer> requiredIngredientsForMainDish = getRequiredIngredientsForMainDish(requiredMainDish);
                            
                            // If the HashMap is stored directly into the QUEST_SLOT sub slot 2, it will look like:
                            // inprogress;lasagne;{carrot=1;cheese=1;egg=1;flour=1;meat=1;olive oil=1;tomato=1}

							//ATTEMPT 3)
                            for (final Map.Entry<String, Integer> entry : requiredIngredientsForMainDish.entrySet()) {
                            	requiredIngredients = requiredIngredients + entry.getKey() + "=" + entry.getValue() + ":";
                            }
							


                            //player.setQuest(QUEST_SLOT, "inprogress" + ";" + requiredMainDish + ";" + requiredIngredientsForMainDish + ";");

                        }
                    },
                    //new IncreaseKarmaAction(20),
                    new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "I really want to try [items]")
            )
            */
        );

        // Player rejects the quest,
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

        // Player rejects the quest again,
        // Player has rejected the quest in the past,
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

    // the quest is advanced by next interacting with Stefan
    public void stageCollectIngredientsForMainDish() {

        final SpeakerNPC npc = npcs.get("Stefan");

        npc.add(ConversationStates.IDLE,
                ConversationPhrases.GREETING_MESSAGES,
                new AndCondition(
                        new GreetingMatchesNameCondition(npc.getName()),
                        new QuestInStateCondition(QUEST_SLOT, 0, "fetch_maindish")),
                ConversationStates.ATTENDING,
                // FIXME omero: greetings line should reflect the quest has started
                "Hello! I'm so busy that I never get to leave this kitchen... " +
                "Don't tell me I now have to prepare another #meal!",
                null
        );

        // Player remembers generic instructions from Groongo,
        // Player says 'meal'
        // Ask if he has the required ingredients
        npc.add(ConversationStates.ATTENDING,
                "meal",
                new AndCondition(
                        new GreetingMatchesNameCondition(npc.getName()),
                        new QuestInStateCondition(QUEST_SLOT, 0, "fetch_maindish")),
                ConversationStates.QUESTION_1,
                null,
                new checkIngredientsForMainDishAction()
        );

        // Player remembers Groongo asked for a specific main dish
        // Player says one of the known REQUIRED_MAIN_DISHES
        // Add all the main dishes trigger words
        Iterator<String> i = REQUIRED_MAIN_DISHES.iterator();
        while (i.hasNext()) {
            npc.add(ConversationStates.ATTENDING,
                    i.next(),
                    new QuestInStateCondition(QUEST_SLOT, 0, "fetch_maindish"),
                    ConversationStates.QUESTION_1,
                    null,
                    new checkIngredientsForMainDishAction()
            );
        }

        // Player has been asked if he has the ingredients for main dish,
        // Player answers negatively
        npc.add(ConversationStates.QUESTION_1,
                ConversationPhrases.NO_MESSAGES,
                new QuestInStateCondition(QUEST_SLOT, 0, "fetch_maindish"),
                ConversationStates.ATTENDING,
                "Be sure to bring me those ingredients all at once!",
                null
        );

        // Player has been asked if he has the ingredients for main dish,
        // Player answers affirmatively,
        // the quest is possibly advanced to the next step
        npc.add(ConversationStates.QUESTION_1,
                ConversationPhrases.YES_MESSAGES,
                null,
                ConversationStates.IDLE,
                null,
                new collectAllRequestedIngredientsAtOnceAction(
                		new MultipleActions(
                				new SetQuestToTimeStampAction(QUEST_SLOT, 3),
                				new advanceQuestInProgressAction()
                		),
                		ConversationStates.IDLE)
        );
    }

    // the quest is advanced further by interacting with both Groongo and Stefan again
    public void stageCheckForDessert() {
    	
    	final SpeakerNPC npc_chef = npcs.get("Stefan");
    	final SpeakerNPC npc_customer = npcs.get("Groongo Rahnnt");
    	
        // Player checks back with Stefan
        // Player doesn't yet know which dessert Groongo would like
        npc_chef.add(ConversationStates.IDLE,
        		ConversationPhrases.GREETING_MESSAGES,
                new AndCondition(
                        new GreetingMatchesNameCondition(npc_chef.getName()),
                        new QuestInStateCondition(QUEST_SLOT, 0, "choose_dessert")),
                ConversationStates.ATTENDING,
                "Oh, you're back so soon..." +
                " And you haven't checked which #dessert" +
        		" our troublesome customer would like to have!",
                null
        );

        // give some hints about what to do next
        npc_chef.add(ConversationStates.ATTENDING,
                "dessert",
                new QuestInStateCondition(QUEST_SLOT, 0, "choose_dessert"),
                ConversationStates.IDLE,
                "I know how to prepare several kind of desserts..." +
                " You better check with our troublesome customer to know which one he prefers!",
                null
        );

        // give some hints about what to do next
        // Add all trigger words of main dishes
        Iterator<String> i = REQUIRED_MAIN_DISHES.iterator();
        while (i.hasNext()) {
            npc_chef.add(ConversationStates.ATTENDING,
                    i.next(),
                    new QuestInStateCondition(QUEST_SLOT, 0, "choose_dessert"),
                    ConversationStates.ATTENDING,
                    "I'm preparing that already..." +
                    " You should now check with our troublesome castomer" +
            		" what #dessert he'd like to have with that.",
                    null
            );
        }

    	// Player knows which dessert Groongo wants
        // quest is running
    	npc_chef.add(ConversationStates.IDLE,
                ConversationPhrases.GREETING_MESSAGES,
                new AndCondition(
                        new GreetingMatchesNameCondition(npc_chef.getName()),
                        new QuestInStateCondition(QUEST_SLOT, 0, "checked_dessert")),
                ConversationStates.ATTENDING,
                "Here you are... I still wonder what #dessert our troublesome customer wants...",
                null
        );

    	// Player knows which dessert Groongo wants,
    	// Advance the quest
    	// Ask if he has the required ingredients
    	// quest is running
        npc_chef.add(ConversationStates.ATTENDING,
                "dessert",
                new AndCondition(
                        new GreetingMatchesNameCondition(npc_chef.getName()),
                        new QuestInStateCondition(QUEST_SLOT, 0, "checked_dessert")),
                ConversationStates.QUESTION_1,
                null,
                new MultipleActions (
                		new SetQuestToTimeStampAction(QUEST_SLOT, 3),
                		new advanceQuestInProgressAction(),
                		new checkIngredientsForDessertAction()
                )
        );

        // Player checks back with Groongo,
        // quest is running
        npc_customer.add(ConversationStates.IDLE,
        		ConversationPhrases.GREETING_MESSAGES,
                new AndCondition(
                        new GreetingMatchesNameCondition(npc_customer.getName()),
                        new QuestInStateCondition(QUEST_SLOT, 0, "choose_dessert")),
                ConversationStates.ATTENDING,
                "Ah, you're back already..." +
                " And I still don't see the #meal I've asked!",                		
                null
        );
        
        // Player says meal to be reminded,
        // will trigger checkQuestInProgressAction() 
        // that will give hints about dessert
        // quest is running
        npc_customer.add(
        	ConversationStates.ATTENDING,
        	"meal",
        	new AndCondition(
	            new QuestActiveCondition(QUEST_SLOT),
	            new QuestInStateCondition(QUEST_SLOT, 0, "choose_dessert")),
	        ConversationStates.ATTENDING,
            null,
            new checkQuestInProgressAction()
        );

        // Player says dessert to ask Groongo which one he'd like
        // quest is running
        npc_customer.add(
        	ConversationStates.ATTENDING,
            "dessert",
            new AndCondition(
            		new QuestActiveCondition(QUEST_SLOT),
            		new QuestInStateCondition(QUEST_SLOT, 0, "choose_dessert")),
            ConversationStates.IDLE,
            null,
            new chooseDessertAction()
        );
    }
    
    // the quest is advanced further again by interacting with Stefan
    public void stageCollectIngredientsForDessert() {

        final SpeakerNPC npc = npcs.get("Stefan");

        npc.add(ConversationStates.IDLE,
                ConversationPhrases.GREETING_MESSAGES,
                new AndCondition(
                        new GreetingMatchesNameCondition(npc.getName()),
                        new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert")),
                ConversationStates.ATTENDING,
                // FIXME omero: greetings line should reflect the quest has advanced
                "Ah, you're back! I'm afraid that I'm still missing some ingredients for preparing a good #dessert...",
                null
        );


        // Player remembers generic instructions from Groongo,
        // Player says 'dessert'
        npc.add(ConversationStates.ATTENDING,
                "dessert",
                new AndCondition(
                        new GreetingMatchesNameCondition(npc.getName()),
                        new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert")),
                ConversationStates.QUESTION_1,
                null,
                new checkIngredientsForDessertAction()
        );

        // Player says one of the defined REQUIRED_MAIN_DISHES
        // Add all the main dishes trigger words
        Iterator<String> i = REQUIRED_MAIN_DISHES.iterator();
        while (i.hasNext()) {
            npc.add(ConversationStates.ATTENDING,
                    i.next(),
                    new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert"),
                    ConversationStates.ATTENDING,
                    "I'm preparing that already..." +
                    " I now miss some ingredients for preparing a #dessert for our troublesome customer!",
                    null
            );
        }

        // Player says one of the defined REQUIRED_DESSERTS
        // Add all the desserts trigger words
        Iterator<String> j = REQUIRED_DESSERTS.iterator();
        while (j.hasNext()) {
            npc.add(ConversationStates.ATTENDING,
                j.next(),
                new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert"),
                ConversationStates.QUESTION_1,
                null,
                new checkIngredientsForDessertAction()
            );
        }

        // Player has been asked if he has the ingredients for dessert,
        // Player answers negatively
        npc.add(ConversationStates.QUESTION_1,
            ConversationPhrases.NO_MESSAGES,
            new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert"),
            ConversationStates.IDLE,
            "Oh, fetch them quickly then! And be sure to bring them to me all at the same time!",
            null
        );

        // Player has been asked if he has the ingredients for dessert,
        // Player answers affirmatively,
        // the quest is possibly advanced to the next step
        npc.add(ConversationStates.QUESTION_1,
                ConversationPhrases.YES_MESSAGES,
                null,
                ConversationStates.IDLE,
                null,
                new collectAllRequestedIngredientsAtOnceAction(
            		new MultipleActions(
            				// meal will not be ready before MEALREADY_DELAY from now
            				new SetQuestToTimeStampAction(QUEST_SLOT, 3),
            				new advanceQuestInProgressAction()
            		),
            		ConversationStates.IDLE)
        );
        
    }

    // the states for interacting with both Groongo and Stefan
    // when the quest has reached its almost final stage
    public void stageWaitForMeal() {
    	
    	final SpeakerNPC npc_chef = npcs.get("Stefan");
        final SpeakerNPC npc_customer = npcs.get("Groongo Rahnnt");

        /**
         * FIXME omero: Groongo does not react appropriately when the 'checked_dessert' stage has been reached:
         * 
         * [01:40] <Groongo Rahnnt> Ah, you're back already... And I still don't see the meal I've asked!
		 * [01:41] <omero> meal
		 * [01:41] <Groongo Rahnnt> Bah! I'm still waiting for a couscous. That's what I call a decent meal!  Maybe I should also choose some dessert to go with that...
		 * [01:41] <omero> dessert
		 * [01:41] <Groongo Rahnnt> Indeed, I shouldn't have forgot that! With the couscous I will try a vatrushka. Now go ask Chef Stefan to prepare my dessert, at once!
		 * [01:41] <omero> dessert
		 * ...
		 * [01:41] <omero> hello
		 * [01:41] <Groongo Rahnnt> Gah! Outrageous Place! Been waiting forever for someone to show up!
		 * [01:41] <omero> task
		 * [01:41] <omero> help
		 * [01:41] <Groongo Rahnnt> HELP?! You want ME to ...help... YOU?! Ask me for a task and I'll give you one at once!
		 * [01:41] <omero> task
		 * [01:41] <omero> dessert
		 * [01:41] <omero> meal
		 * [01:41] <Groongo Rahnnt> Do a task for me and you get a generous tip from me!
		 * Open Quests: 
		 * 	MealForGroongo (meal_for_groongo): checked_dessert;couscous;vatrushka;1336347641236
         */

        /**
         * FIXME omero: Stefan does not react appropriately to short name of dessert when 'checked_dessert' reached:
         * [01:53] <omero> hola
		 * [01:53] <Stefan> Here you are... I still wonder what dessert our troublesome customer wants...
		 * [01:53] <omero> vatrushka
		 * ... nothing works here
		 * [01:54] <Stefan> Goodbye! Have a nice stay in Fado!
		 * 
		 * this works:
		 * [01:56] <omero> hello
		 * [01:56] <Stefan> Here you are... I still wonder what dessert our troublesome customer wants...
		 * [01:57] <omero> dessert
		 * [01:57] <Stefan> A delicious choice indeed!
		 * [01:57] <Stefan> Oh! So our troublesome customer decided to have a vatrushka for dessert. For that I'll need some other ingredients that I'm missing: 8 pieces of cheese, 16 cherries, 2 sacks of flour, and 4 sacks of sugar. Do you happen to have any of those already with you?
		 * [01:57] <Stefan> Goodbye! Have a nice stay in Fado!
         */
        
        /**
         *  [02:02] <omero> hello
			[02:02] <Stefan> Ah, you're back! I'm afraid that I'm still missing some ingredients for preparing a good dessert...
			[02:02] <omero> dessert
			[02:02] <Stefan> Oh! So our troublesome customer decided to have a vatrushka for dessert. For that I'll need some other ingredients that I'm missing: 8 pieces of cheese, 16 cherries, 2 sacks of flour, and 4 sacks of sugar. Do you happen to have any of those already with you?
			[02:02] <omero> yes
			[02:02] <Stefan> FIXME omero: the meal will be ready soon
			[02:02] <omero> hello
			[02:02] <Stefan> The meal for our troublesome customer won't be ready before about 4 and a half minutes.
			[02:06] <omero> hello
			[02:06] <Stefan> The meal for our troublesome customer won't be ready before just over 1 minute.
			[02:07] <omero> hello
			[02:07] <Stefan> The meal for our troublesome customer won't be ready before less than a minute.
			[02:08] <omero> hello
			[02:08] <Stefan> Here you are! I've just finished preparing a couscous as the main dish and a vatrushka for dessert. You should now bring this decent meal to our troublesome customer at once. And be very careful not to spoil it or drop it along your way!
			[02:08] <omero> hello
			[02:08] <Groongo Rahnnt> Oh, you're back! Do you finally have my meal?
			[02:09] <omero> meal
			[02:09] <Groongo Rahnnt> Bah! I'm still waiting for a couscous as the main dish and a vatrushka for dessert. That's what I call a decent meal! Did you bring those for me?
			[02:09] <omero> yes
			[02:09] <Groongo Rahnnt> FIXME omero: say the player something giving thanks to Stefan (and get the real reward)
			[02:09] <Groongo Rahnnt> FIXME omero: Good job. Take theseten pies
			[02:09] omero earns 1000 experience points.
         */

        npc_chef.add(
        	ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new AndCondition(
                new GreetingMatchesNameCondition(npc_chef.getName()),
                new AndCondition(
            		new QuestInStateCondition(QUEST_SLOT, 0, "prepare_decentmeal"),
            		new NotCondition(
           				new TimePassedCondition(QUEST_SLOT, 3, MEALREADY_DELAY)))),
            ConversationStates.IDLE,
            null,
            new SayTimeRemainingAction(QUEST_SLOT, 3, MEALREADY_DELAY,        		
        		"The meal for our troublesome customer won't be ready before")
        );
        
        npc_chef.add(
        	ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new AndCondition(
                new GreetingMatchesNameCondition(npc_chef.getName()),
                new AndCondition(
            		new QuestInStateCondition(QUEST_SLOT, 0, "prepare_decentmeal"),
       				new TimePassedCondition(QUEST_SLOT, 3, MEALREADY_DELAY))),
            ConversationStates.IDLE,
            null,
            new advanceQuestInProgressAction()
        );
        
    	// Player says his greetings to Groongo,
        // the quest is running
        npc_customer.add(
        	ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new AndCondition(
                new GreetingMatchesNameCondition(npc_customer.getName()),
                new OrCondition(
            		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_maindish"),
            		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert"))),
            ConversationStates.QUESTION_1,
            "Here you are! Is my #meal finally ready yet?",
            null
        );

        // Player needs to be reminded
        // add trigger words for both 'meal' and 'dessert'
        npc_customer.add(ConversationStates.QUESTION_1,
            Arrays.asList("meal", "dessert"),
            new OrCondition(
        		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_maindish"),
        		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert")),
            ConversationStates.QUESTION_1,
            null,
            new checkQuestInProgressAction()
        );
        
        // Player needs to be reminded
        // add trigger words for each of the short names of a main dish
        Iterator<String> i = REQUIRED_MAIN_DISHES.iterator();
        while (i.hasNext()) {
            npc_customer.add(ConversationStates.QUESTION_1,
                i.next(),
                new OrCondition(
            		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_maindish"),
            		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert")),
                ConversationStates.QUESTION_1,
                null,
                new checkQuestInProgressAction()
            );
        }

        // Player needs to be reminded
        // add trigger words for each of the short names of a dessert
        Iterator<String> j = REQUIRED_DESSERTS.iterator();
        while (j.hasNext()) {
            npc_customer.add(ConversationStates.QUESTION_1,
                j.next(),
                new OrCondition(
            		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_maindish"),
            		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert")),
                ConversationStates.QUESTION_1,
                null,
                new checkQuestInProgressAction()
            );
        }
        
        // Player answers no
        // quest running, not completed yet
        npc_customer.add(ConversationStates.QUESTION_1,
            ConversationPhrases.NO_MESSAGES,
            new QuestNotCompletedCondition(QUEST_SLOT),
            ConversationStates.IDLE,
            "GAH! [insults player] Why did you come back then!",
            null
        );

        // Player answers yes
        // quest running
        npc_customer.add(ConversationStates.QUESTION_1,
            ConversationPhrases.YES_MESSAGES,
            new AndCondition(
        		new QuestNotCompletedCondition(QUEST_SLOT),
        		new NotCondition(new PlayerHasItemWithHimCondition("decent meal"))),
            ConversationStates.IDLE,
            "GAAAH! [instults player] Who are you trying to fool?! Go in that kitchen and come back with my meal, NOOOOOW!",
            null
		);
    }
    
    // the states for interacting with both Groongo and Stefan
    // when the quest is in its final stage
    public void stageDeliverMeal() {

        final SpeakerNPC npc = npcs.get("Groongo Rahnnt");
        
        // Player says his greetings to Groongo,
        // the quest is running
        npc.add(ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new AndCondition(
                new GreetingMatchesNameCondition(npc.getName()),
                new QuestActiveCondition(QUEST_SLOT),
                new QuestInStateCondition(QUEST_SLOT, 0, "deliver_decentmeal")),
            ConversationStates.QUESTION_1,
            "Oh, you're back! Do you finally have my #meal?",
            null
        );

        // Player says meal to be reminded of what is still missing
        // quest is running
        npc.add(ConversationStates.QUESTION_1,
        	Arrays.asList("meal", "dessert"),
            new AndCondition(
                new GreetingMatchesNameCondition(npc.getName()),
                new QuestActiveCondition(QUEST_SLOT),
                new QuestInStateCondition(QUEST_SLOT, 0, "deliver_decentmeal")),
            ConversationStates.QUESTION_1,
            null,
            new checkQuestInProgressAction()
        );

        // Player answers no
        // waiting for Stefan?
        npc.add(ConversationStates.QUESTION_1,
            ConversationPhrases.NO_MESSAGES,
            new AndCondition(
                new GreetingMatchesNameCondition(npc.getName()),
                new QuestActiveCondition(QUEST_SLOT),
                new QuestInStateCondition(QUEST_SLOT, 0, "deliver_decentmeal")),
            ConversationStates.IDLE,
            "Then hurry up, go and fetch it!",
            null);

        /**
         * TODO omero: this is intended to look like the end of the quest,
         * but Groongo should hint the player to send his #thanks to Chef Stefan.
         * The reward here should be something ok, but kind of not covering the effort.
         * In order to effectively end the quest, the player should check back with Stefan
         * one more time and say the trigger word 'thanks'.
         *  
         * It would be nice to have a sort of 'expire time'.
         * If the player doesn't check back with Stefan within the 'expire time',
         * the quest gets marked as 'done' nevertheless but the player doesn't get
         * the 'real reward' from Stefan.
         */
		final List<ChatAction> deceivingEndQuestActions = new LinkedList<ChatAction>();
		deceivingEndQuestActions.add(new DropInfostringItemAction("decent meal","Decent Meal for Groongo"));
		deceivingEndQuestActions.add(new SetQuestAction(QUEST_SLOT, 0, "deceivinglydone"));
		deceivingEndQuestActions.add(new SetQuestToTimeStampAction(QUEST_SLOT, 3));
		// FIXME omero: bug? logic hole? last parameter 'increment' should be 1 by default!
		deceivingEndQuestActions.add(new IncrementQuestAction(QUEST_SLOT, 4, 1));
		deceivingEndQuestActions.add(new IncreaseXPAction(XP_REWARD));
		deceivingEndQuestActions.add(new IncreaseKarmaAction(50.0));
		deceivingEndQuestActions.add(
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					int amount = 10;
					new EquipItemAction("pie", amount, true).fire(player, null, null);
					npc.say("FIXME omero: Good job. Take " +
						Grammar.thisthese(amount) + " " +
						Grammar.quantityNumberStrNoun(amount, "pie") +
						" as my reward! Please bring my very deserved #thanks to" + 
						" Chef Stefan for preparing such a decent meal!"
					);
				}
			}
		);

		// Player answers yes and he indeed has the meal for Groongo  
        npc.add(ConversationStates.QUESTION_1,
            ConversationPhrases.YES_MESSAGES,
            new AndCondition(
                new GreetingMatchesNameCondition(npc.getName()),
                new QuestActiveCondition(QUEST_SLOT),
                new QuestInStateCondition(QUEST_SLOT, 0, "deliver_decentmeal"),
                new PlayerHasInfostringItemWithHimCondition("decent meal", "Decent Meal for Groongo")),
            ConversationStates.IDLE,
            "FIXME omero: say the player something giving #thanks to Stefan (and get the real reward)",
            new MultipleActions(deceivingEndQuestActions)
        );
    }
}