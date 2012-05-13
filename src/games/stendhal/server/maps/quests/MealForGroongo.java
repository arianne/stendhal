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
     * Each sub slot will only always serve one purpose as stated below. 
     *
     * QUEST_SLOT sub slot 0
     * will hold the main states, which can be:
     * - rejected, the player has refused to undertake the quest
     * - fetch_maindish, the player is collecting ingredients for that
     * - check_dessert, the player needs to ask Groongo which dessert he wants
     * - tell_dessert, the player has to tell Stefan about Groongo's choice
     * - fetch_dessert, the player is collecting the ingredients for that
     * - prepare_decentmeal, the player has to wait a little before the meal is ready
     * - deliver_decentmeal, meal for Groongo is ready 
     * - done, the player has completed the quest
     *
     * QUEST_SLOT sub slot 1
     * will hold how the quest was finally ended:
     * - incomplete
     * - complete
     * 
     * After the troublesome customer gets delivered his decent meal,
     * the player may check back with Stefan once more and
     * say the trigger word 'thanks' before BRINGTHANKS_DELAY has elapsed.
     * 
     * Since we don't want to reuse any of the sub slots,
     * when BRINGTHANKS_DELAY hasn't expired yet, checking QUEST_SLOT sub slot 0 (done)
     * and QUEST_SLOT sub slot 4 (timestamp) or any other combination of sub slots 
     * would allow the player to say 'thanks' repeatedly 
     * and get 'better reward' multiple times.
     * 
     * QUEST_SLOT sub slot 0 gets marked as 'done'
     * as soon as player delivers meal to customer and
     * QUEST_SLOT sub slot 1 gets marked 'incomplete'.
     * 
     * If the player checks back with Stefan in time,
     * the QUEST_SLOT sub slot 1 gets marked 'complete'
     * and the quest is finally ended.
     *  
     * If the player doesn't check back with Stefan within the 'expire time',
     * the 'better reward' from Stefan is lost.
     * 
     * QUEST_SLOT sub slot 2
     * - a main dish short name
     * 
     * QUEST_SLOT sub slot 3
     * - a dessert short name
     *
     * QUEST_SLOT sub slot 4
     * - when quest is running, holds a timestamp for waiting the preparation of decent meal
     * - when quest is done, holds the timestamp of when last decent meal was delivered
     *
     * QUEST_SLOT sub slot 5
     * - counts how many times a decent meal has been delivered
     */
    public static final String QUEST_SLOT = "meal_for_groongo";

    //How long it takes Chef Stefan to prepare a decent meal (main dish and dessert)
    private static final int MEALREADY_DELAY = 1;
    
    //How much time the player has to get a better reward
    private static final int BRINGTHANKS_DELAY = 10;
    
    //Every when the quest can be repeated
    //private static final int REPEATQUEST_DELAY = 1 * MathHelper.MINUTES_IN_ONE_DAY;

    // FIXME omero: for testing only
    private static final int REPEATQUEST_DELAY = 1;
        
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
                "brigadeiro",
                "vatrushka",
                "cake",
                "tarte",
                "slagroomtart",
                "kirschtorte",
                "gulab"
            );


    @Override
    public void addToWorld() {
        super.addToWorld();
        fillQuestInfo(
            "Meal for Groongo Rahnnt",
            "Groongo is hungry and wants to have a decent meal at Fado's Hotel Restaurant.",
            true);

        // FIXME omero: this quest will require a yet unknown number of stages
        stageBeginQuest();
        stageCollectIngredientsForMainDish();
        stageCheckForDessert();
        stageCollectIngredientsForDessert();
        stageWaitForMeal();
        stageDeliverMeal();        
    }

    @Override
    public List<String> getHistory(final Player player) {
    	// FIXME omero: the quest states are not fully defined yet
        final List<String> res = new ArrayList<String>();
        if (!player.hasQuest(QUEST_SLOT)) {
            return res;
        }
        final String questState = player.getQuest(QUEST_SLOT, 0);
        logger.warn("Quest state: <" + player.getQuest(QUEST_SLOT) + ">");
        
        res.add("I've met Groongo Rahnnt in Fado's Hotel Restaurant.");
        
        if ("rejected".equals(questState)) {
            res.add("He asked me to bring him a meal of his desire, "
                + " but I had no interest in such an errand.");
        } else if ("done".equals(questState)) {
            res.add(
        		"I did bring to him " +
    			Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 2))) +
    			" as the main dish and " +
    			Grammar.a_noun(getRequiredDessertFancyName(player.getQuest(QUEST_SLOT, 3))) +
    			" for dessert."            		
    		);
            if (isRepeatable(player)) {
                // enough time has passed, inform that the quest is available to be taken.
                res.add("I might ask him again if he wants to have another decent meal.");
            } else {
                // inform about how much time has to pass before the quest can be taken again.
                long timestamp;
                try {
                    timestamp = Long.parseLong(player.getQuest(QUEST_SLOT, 4));
                } catch (final NumberFormatException e) {
                    timestamp = 0;
                }
                final long timeBeforeRepeatable = (
            		timestamp
            		+ REPEATQUEST_DELAY * MathHelper.MILLISECONDS_IN_ONE_MINUTE
            		- System.currentTimeMillis());
                res.add(
            		"He will be fine for at least " +
    				TimeUtil.approxTimeUntil((int) (timeBeforeRepeatable / 1000L)) + ".");
            }
        } else {
        	final ItemCollection missingIngredients = new ItemCollection();
        	String ingredients = "";        	
        	if ("fetch_maindish".equals(questState)) {
        		ingredients = getRequiredIngredientsForMainDish(player.getQuest(QUEST_SLOT,2));
        		missingIngredients.addFromQuestStateString(ingredients);
            	res.add("Groongo wants to have " +
        			Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 2))) +
        			" as the main dish and I'm helping Chef Stefan finding the ingredients to prepare it." +
        			" I still have to bring " +
        			Grammar.enumerateCollection(missingIngredients.toStringList()) + "."
            	);
        	} else if ("check_dessert".equals(questState)) {
            	res.add("Groongo wants to have " +
        			Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 2))) +
        			" as the main dish and Chef Stefan is already preparing it."
	        	);
            	res.add("I now have to ask Groongo" +
        			" which dessert he'd like to have along with that."
	        	);
        	} else if ("tell_dessert".equals(questState)) {
            	res.add("Groongo wants to have " +
        			Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 2))) +
        			" as the main dish."
	        	);
            	res.add("I'm now going to tell Chef Stefan to also prepare " +
        			Grammar.a_noun(getRequiredDessertFancyName(player.getQuest(QUEST_SLOT, 3))) +
        			" for dessert."
    			);
        	} else if ("fetch_dessert".equals(questState)) {
	    		ingredients = getRequiredIngredientsForDessert(player.getQuest(QUEST_SLOT, 3));
	    		missingIngredients.addFromQuestStateString(ingredients);
            	res.add("Groongo wants to have " +
        			Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 2))) +
        			" as the main dish and " +
        			Grammar.a_noun(getRequiredDessertFancyName(player.getQuest(QUEST_SLOT, 3))) +
        			" for dessert and I'm helping Chef Stefan finding the ingredients to prepare it." +
        			" I still have to bring " +
        			Grammar.enumerateCollection(missingIngredients.toStringList()) + "."
	        	);
        	} else if ("prepare_decentmeal".equals(questState)) {
            	res.add("Groongo wants to have " +
        			Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 2))) +
        			" as the main dish and " +
        			Grammar.a_noun(getRequiredDessertFancyName(player.getQuest(QUEST_SLOT, 3))) +
        			" for dessert and I'm waiting that Chef Stefan finishes to prepare them."
    			);
        	} else if ("deliver_decentmeal".equals(questState)) {
            	res.add("Groongo wants to have " +
        			Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 2))) +
        			" as the main dish and " +
        			Grammar.a_noun(getRequiredDessertFancyName(player.getQuest(QUEST_SLOT, 3))) +
        			" for dessert and I'm going to bring him his decent meal."
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
            new TimePassedCondition(QUEST_SLOT, 4, REPEATQUEST_DELAY)).fire(player,null, null);
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
        requiredDessertFancyName.put("brigadeiro", "brigadeiro a la amparo");
        requiredDessertFancyName.put("macedonia", "macedonia di frutta con panna");
        requiredDessertFancyName.put("vatrushka", "old-fashioned vatrushka with cottage cheese");
        requiredDessertFancyName.put("cake", "classic carrot cake with fluffy cream cheese frosting");
        requiredDessertFancyName.put("tarte", "tarte avec la rhubarbe");
        requiredDessertFancyName.put("slagroomtart", "slagroomtart van der boer");
        requiredDessertFancyName.put("kirschtorte", "schwarzwalder kirschtorte");
        requiredDessertFancyName.put("gulab", "gulab jamun");
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
        requiredIngredients_vatrushka.put("cherry", 6);
        
        final HashMap<String, Integer> requiredIngredients_cake = new HashMap<String, Integer>();
        requiredIngredients_cake.put("flour", 4);
        requiredIngredients_cake.put("sugar", 3);
        requiredIngredients_cake.put("cheese", 2);
        requiredIngredients_cake.put("carrot", 1);
        
        final HashMap<String, Integer> requiredIngredients_tarte = new HashMap<String, Integer>();
        requiredIngredients_tarte.put("flour", 1);
        requiredIngredients_tarte.put("sugar", 2);
        requiredIngredients_tarte.put("chocolate", 2);
        requiredIngredients_tarte.put("milk", 1);

        final HashMap<String, Integer> requiredIngredients_kirschtorte = new HashMap<String, Integer>();
        requiredIngredients_kirschtorte.put("flour", 4);
        requiredIngredients_kirschtorte.put("sugar", 3);
        requiredIngredients_kirschtorte.put("butter", 3);
        requiredIngredients_kirschtorte.put("milk", 4);
        
        final HashMap<String, Integer> requiredIngredients_gulab = new HashMap<String, Integer>();
        requiredIngredients_gulab.put("flour", 1);
        requiredIngredients_gulab.put("fierywater", 2);
        requiredIngredients_gulab.put("sugar", 3);
        requiredIngredients_gulab.put("honey", 4);
       
        final HashMap<String, HashMap<String, Integer>> requiredIngredientsForDessert = new HashMap<String, HashMap<String, Integer>>();
        requiredIngredientsForDessert.put("brigadeiro", requiredIngredients_brigadeiro);
        requiredIngredientsForDessert.put("macedonia", requiredIngredients_macedonia);
        requiredIngredientsForDessert.put("vatrushka", requiredIngredients_vatrushka);
        requiredIngredientsForDessert.put("cake", requiredIngredients_cake);
        requiredIngredientsForDessert.put("tarte", requiredIngredients_tarte);
        requiredIngredientsForDessert.put("slagroomtart", requiredIngredients_slagroomtart);
        requiredIngredientsForDessert.put("kirschtorte", requiredIngredients_kirschtorte);
        requiredIngredientsForDessert.put("gulab", requiredIngredients_gulab);

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
            	player.setQuest(QUEST_SLOT, 0, "check_dessert");
            	SpeakerNPC.say(
        			"Excellent! I'll start preparing " +
        			Grammar.article_noun(
        					getRequiredMainDishFancyName(
        							player.getQuest(QUEST_SLOT, 2)), true) + " immediately." +
        			" Meanwhile, please go ask our troublesome customer" +
        			" which #dessert he'd like to have along with it!");
            } else if ("tell_dessert".equals(player.getQuest(QUEST_SLOT, 0))) {
            	player.setQuest(QUEST_SLOT, 0, "fetch_dessert");
            	SpeakerNPC.say("Oh! A delicious choice indeed!");
            } else if ("fetch_dessert".equals(player.getQuest(QUEST_SLOT, 0))) {
                player.setQuest(QUEST_SLOT, 0, "prepare_decentmeal");
                SpeakerNPC.say("FIXME omero: the meal will be ready soon");
            } else if ("prepare_decentmeal".equals(player.getQuest(QUEST_SLOT, 0))) {
            	final Item decentMeal = SingletonRepository.getEntityManager().getItem("decent meal");
            	final String decentMealDescription =
            			Grammar.a_noun(
        					getRequiredMainDishFancyName(
    							player.getQuest(QUEST_SLOT, 2))) +
            			" as the main dish and " +
            			Grammar.a_noun(
        					getRequiredDessertFancyName(
    							player.getQuest(QUEST_SLOT, 3))) +
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
            String requiredMainDish = getRequiredMainDish();

            int attempts = 0;
            String requiredOldMainDish;
            try {
            	requiredOldMainDish = player.getQuest(QUEST_SLOT, 2);
            } catch (final NullPointerException e) {
            	requiredOldMainDish = "none";
            }
            while (requiredMainDish.equals(requiredOldMainDish) & attempts <= 5 ) {
            	requiredMainDish = getRequiredMainDish();
            	attempts++;
            }
            logger.warn("Attempts for new main dish <" + attempts + ">");

            player.setQuest(QUEST_SLOT, 0, "fetch_maindish");
            player.setQuest(QUEST_SLOT, 1, "inprogress");
            player.setQuest(QUEST_SLOT, 2, requiredMainDish);
            
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
            final String requiredMainDish = player.getQuest(QUEST_SLOT, 2);
            String requiredDessert = getRequiredDessert();

            int attempts = 0;
            String requiredOldDessert;
            try {
            	requiredOldDessert = player.getQuest(QUEST_SLOT, 3);
            } catch (final NullPointerException e) {
            	requiredOldDessert = "none";
            }
            while (requiredDessert.equals(requiredOldDessert) & attempts <= 5 ) {
            	requiredDessert = getRequiredDessert();
            	attempts++;
            }
            logger.warn("Attempts for new dessert <" + attempts + ">");

            /**
             * this transition is needed just to have Stefan
             * react appropriately when the player gets back to him
             * after checking which dessert the customer wants.
             */
            player.setQuest(QUEST_SLOT, 0, "tell_dessert");
            player.setQuest(QUEST_SLOT, 3, requiredDessert);
            
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
    
    // Groongo uses this to remind the player of what he has to bring him currently
    // depending on which stage the quest currently is
    class checkQuestInProgressAction implements ChatAction {
        public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {
        	final String questState = player.getQuest(QUEST_SLOT, 0);
        	String meal = "";
        	String question = "";
        	if  (
        			"fetch_maindish".equals(questState)) {
        		meal = Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 2)));
        		question = "Did you bring that for me?";
        	} else if (        			
        			"check_dessert".equals(questState)) {
        		meal = Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 2)));
        		//question = " Should I also choose some #dessert to go with that?";
        		// not a question but a way to give the player a hint about how to 'ask' for which dessert
        		question = " Maybe I should also choose some #dessert to go with that...";
        	} else if (        			
        			"tell_dessert".equals(questState) ||
        			"fetch_dessert".equals(questState) ||
        			"prepare_decentmeal".equals(questState) ||
        			"deliver_decentmeal".equals(questState)) {
        		meal = 
        			Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 2))) +
        			" as the main dish and " +
        			Grammar.a_noun(getRequiredDessertFancyName(player.getQuest(QUEST_SLOT, 3))) +
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
            		getRequiredIngredientsForMainDish(player.getQuest(QUEST_SLOT, 2)));

            SpeakerNPC.say(
                    "Ah! Our troublesome customer has asked for " +
                    Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 2))) +
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
            		getRequiredIngredientsForDessert(player.getQuest(QUEST_SLOT, 3)));

            SpeakerNPC.say(
                    "Our troublesome customer decided to have " +
                    Grammar.a_noun(getRequiredDessertFancyName(player.getQuest(QUEST_SLOT, 3))) +
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
        	final String questState = player.getQuest(QUEST_SLOT, 0);
    		ItemCollection missingIngredients = getMissingIngredients(player, questState);
    		ItemCollection missingIngredientsToFetch = getMissingIngredients(player, questState);
			boolean playerHasAllIngredients = true;
			// preliminary check. we don't take anything from the player, yet
    		for (final Map.Entry<String, Integer> ingredient : missingIngredients.entrySet()) {
    			final int amount = player.getNumberOfEquipped(ingredient.getKey());
    			if ( amount < ingredient.getValue()) {
    				playerHasAllIngredients = false;
    			} else {
    				missingIngredientsToFetch.removeItem(ingredient.getKey(), ingredient.getValue());
    			}
    		}
    		
    		if (playerHasAllIngredients) {
    			for (final Map.Entry<String, Integer> ingredient : missingIngredients.entrySet()) {
    				player.drop(ingredient.getKey(), ingredient.getValue());
    			}
    			triggerActionOnCompletion.fire(player, sentence, raiser);
    			raiser.setCurrentState(this.stateAfterCompletion);
    		} else {
    			raiser.say(
					"Oh my... For preparing " + 
					Grammar.article_noun(
						getWhatToPrepare(player, questState), true) + 
					" I need all the ingredients at once and it seems you did not bring " + 
					Grammar.enumerateCollection(
						missingIngredientsToFetch.toStringListWithHash()) +
					" as I've asked."
				);
    		}
   		
            logger.warn("Quest state <" + player.getQuest(QUEST_SLOT) + ">");

    	}

    	String getWhatToPrepare(final Player player, final String questState) {
 
    		String res = "";
        	if  ("fetch_maindish".equals(questState)) {
        		res =  getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 2));
        	} else if ("fetch_dessert".equals(questState)) {
        		res =  getRequiredDessertFancyName(player.getQuest(QUEST_SLOT, 3));
        	}
    		
        	return res;
    	}
    	
   		ItemCollection getMissingIngredients(final Player player, final String questState) {
        	final ItemCollection missingIngredients = new ItemCollection();
        	String requiredIngredients = "";
        	if  ("fetch_maindish".equals(questState)) {
        		requiredIngredients = getRequiredIngredientsForMainDish(player.getQuest(QUEST_SLOT, 2));
        		missingIngredients.addFromQuestStateString(requiredIngredients);
        	} else if ("fetch_dessert".equals(questState)) {
        		requiredIngredients = getRequiredIngredientsForDessert(player.getQuest(QUEST_SLOT, 3));
        		missingIngredients.addFromQuestStateString(requiredIngredients);
        	}
   			return missingIngredients;
    	}
    }

    // The quest is started or rejected by first interacting with Groongo Rahnnt
    public void stageBeginQuest() {
     
        final SpeakerNPC npc = npcs.get("Groongo Rahnnt");

        // Player greets Groongo,
        // quest has been rejected in the past
        npc.add(ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new AndCondition(
                    new GreetingMatchesNameCondition(npc.getName()),
                    new QuestInStateCondition(QUEST_SLOT, 0, "rejected")),
            ConversationStates.QUEST_OFFERED,
            "Gah! [insults player]" +
            " I'm all covered with dust after waiting this much..." +
            " Will you bring me a decent #meal now?",
            null
        );

        // Player has done the quest in the past,
        // time enough has elapsed to take the quest again
        npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.QUEST_MESSAGES,
            new AndCondition(
                new QuestCompletedCondition(QUEST_SLOT),
                new TimePassedCondition(QUEST_SLOT, 4, REPEATQUEST_DELAY)),
            ConversationStates.QUEST_OFFERED,
            "Oh, you're still around, I see! Would you bring me another decent #meal?",
            null
        );

        // Player has done the quest in the past,
        // not enough time has elapsed to take the quest again
        npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.QUEST_MESSAGES,
            new AndCondition(
                new QuestCompletedCondition(QUEST_SLOT),
                new NotCondition(new TimePassedCondition(QUEST_SLOT, 4, REPEATQUEST_DELAY))),
            ConversationStates.ATTENDING,
            null,
            new SayTimeRemainingAction(QUEST_SLOT, 4, REPEATQUEST_DELAY, 
                "I'm not so hungry now... I will be fine for")
        );

        // Player asks Groongo for a quest, 1st time!
        // quest is not running
        npc.add(ConversationStates.ATTENDING,
            ConversationPhrases.QUEST_MESSAGES,
            new AndCondition(
        		new QuestNotStartedCondition(QUEST_SLOT),
        		new NotCondition(
    				new QuestCompletedCondition(QUEST_SLOT))),
            ConversationStates.QUEST_OFFERED,
            "Yeah! [insults player]" +
            " I've been waiting here for so long that I've now got cobwebs under my armpits..." +
            " Are you going to bring me a decent #meal now?",
            null
        );

        // Player is curious about meal when offered the quest
        // quest not running yet
        npc.add(ConversationStates.QUEST_OFFERED,
            "meal",
            new OrCondition(
        		new QuestNotStartedCondition(QUEST_SLOT),
        		new QuestCompletedCondition(QUEST_SLOT)),
            ConversationStates.QUEST_OFFERED,
            "Bah! I want to have a decent meal and try something different than soups or pies!" +
            " Will you bring me what I will ask?",
            null
        );

        // Player has just accepted the quest,
        // Player says 'meal' again,
        // give some hints, quest is running
        npc.add(ConversationStates.QUEST_STARTED,
                "meal",
                new OrCondition(
            		new QuestNotStartedCondition(QUEST_SLOT),
            		new QuestCompletedCondition(QUEST_SLOT)),
                ConversationStates.IDLE,
                "I've just told you what I want! Now go, and tell Chef Stefan to prepare it at once!",
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
        // Player accepts the quest,
        // quest is started
        npc.add(ConversationStates.QUEST_OFFERED,
            ConversationPhrases.YES_MESSAGES,
            new OrCondition(
        		new QuestNotStartedCondition(QUEST_SLOT),
        		new QuestCompletedCondition(QUEST_SLOT)),
            ConversationStates.QUEST_STARTED,
            null,
            new chooseMainDishAction()
        );

        // Player rejects the quest,
        // has not rejected it last time
        // Groongo turns idle and some Karma is lost.
        npc.add(ConversationStates.QUEST_OFFERED,
            ConversationPhrases.NO_MESSAGES,
            new AndCondition(
                new QuestNotActiveCondition(QUEST_SLOT),
                new QuestNotInStateCondition(QUEST_SLOT, 0, "rejected")),
            ConversationStates.IDLE,
            "Stop pestering me and get lost in a dungeon then!",
            new MultipleActions(
                    new SetQuestAction(QUEST_SLOT, 0, "rejected"),
                    new DecreaseKarmaAction(20.0))
        );

        // Player rejects the quest,
        // Player has rejected the quest last time,
        // Groongo turns idle and some (more) Karma is lost.
        npc.add(ConversationStates.QUEST_OFFERED,
            ConversationPhrases.NO_MESSAGES,
            new QuestInStateCondition(QUEST_SLOT, 0, "rejected"),
            ConversationStates.IDLE,
            "Stat away from me and get lost in a forest then!",
            new MultipleActions(
                    new SetQuestAction(QUEST_SLOT, 0, "rejected"),
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
                "Hello!" +
                " I'm so busy that I never get to leave this kitchen..." +
                " Don't tell me I now have to prepare another #meal!",
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
                "Too bad! Be sure to bring me those ingredients all at once!",
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
                				new SetQuestToTimeStampAction(QUEST_SLOT, 4),
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
                        new QuestInStateCondition(QUEST_SLOT, 0, "check_dessert")),
                ConversationStates.ATTENDING,
                "Oh, you're back so soon..." +
                " And you haven't checked which #dessert" +
        		" our troublesome customer would like to have!",
                null
        );

        // give some hints about what to do next
        // when the player says dessert
        npc_chef.add(ConversationStates.ATTENDING,
                "dessert",
                new QuestInStateCondition(QUEST_SLOT, 0, "check_dessert"),
                ConversationStates.IDLE,
                "I know how to prepare several kind of desserts..." +
                " You better check with our troublesome customer to know which one he prefers!",
                null
        );

        // give some hints about what to do next
        // when the player says one of the main dishes
        Iterator<String> i = REQUIRED_MAIN_DISHES.iterator();
        while (i.hasNext()) {
            npc_chef.add(ConversationStates.ATTENDING,
                    i.next(),
                    new QuestInStateCondition(QUEST_SLOT, 0, "check_dessert"),
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
                        new QuestInStateCondition(QUEST_SLOT, 0, "tell_dessert")),
                ConversationStates.ATTENDING,
                "Here you are... I still wonder what #dessert our troublesome customer wants...",
                null
        );

    	// Player knows which dessert Groongo wants,
    	// Advance the quest
        npc_chef.add(ConversationStates.ATTENDING,
                "dessert",
                new QuestInStateCondition(QUEST_SLOT, 0, "tell_dessert"),
                ConversationStates.QUESTION_1,
                null,
                new MultipleActions (
                		new SetQuestToTimeStampAction(QUEST_SLOT, 4),
                		new advanceQuestInProgressAction(),
                		new checkIngredientsForDessertAction()
                )
        );
    	// Player knows which dessert Groongo wants,
        // Add all desserts as triggers
    	// Advance the quest
        Iterator<String> j = REQUIRED_DESSERTS.iterator();
        while (j.hasNext()) {
            npc_chef.add(ConversationStates.ATTENDING,
                j.next(),
                new QuestInStateCondition(QUEST_SLOT, 0, "tell_dessert"),
                ConversationStates.QUESTION_1,
                null,
                new MultipleActions (
                		new SetQuestToTimeStampAction(QUEST_SLOT, 4),
                		new advanceQuestInProgressAction(),
                		new checkIngredientsForDessertAction()
                )
            );
        }

        // Player checks back with Groongo,
        // quest is running
        npc_customer.add(ConversationStates.IDLE,
        		ConversationPhrases.GREETING_MESSAGES,
                new AndCondition(
                        new GreetingMatchesNameCondition(npc_customer.getName()),
                        new QuestInStateCondition(QUEST_SLOT, 0, "check_dessert")),
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
	            new QuestInStateCondition(QUEST_SLOT, 0, "check_dessert")),
	        ConversationStates.ATTENDING,
            null,
            new checkQuestInProgressAction()
        );

        // Player says dessert to ask Groongo which one he'd like
        // Advance the quest
        npc_customer.add(
        	ConversationStates.ATTENDING,
            "dessert",
            new AndCondition(
            		new QuestActiveCondition(QUEST_SLOT),
            		new QuestInStateCondition(QUEST_SLOT, 0, "check_dessert")),
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
                    " I now miss some ingredients for preparing the #dessert for our troublesome customer!",
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
                new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert"),
                ConversationStates.IDLE,
                null,
                new collectAllRequestedIngredientsAtOnceAction(
            		new MultipleActions(
            				// meal will not be ready before MEALREADY_DELAY from now
            				new SetQuestToTimeStampAction(QUEST_SLOT, 4),
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

        // Player checks back with Stefan when the decent meal is being prepared
        npc_chef.add(
        	ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new AndCondition(
                new GreetingMatchesNameCondition(npc_chef.getName()),
                new AndCondition(
            		new QuestInStateCondition(QUEST_SLOT, 0, "prepare_decentmeal"),
            		new NotCondition(
           				new TimePassedCondition(QUEST_SLOT, 4, MEALREADY_DELAY)))),
            ConversationStates.IDLE,
            null,
            new SayTimeRemainingAction(QUEST_SLOT, 4, MEALREADY_DELAY,        		
        		"The meal for our troublesome customer won't be ready before")
        );
        
        // Player checks back with Stefan when the decent meal is ready
        npc_chef.add(
        	ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new AndCondition(
                new GreetingMatchesNameCondition(npc_chef.getName()),
                new AndCondition(
            		new QuestInStateCondition(QUEST_SLOT, 0, "prepare_decentmeal"),
       				new TimePassedCondition(QUEST_SLOT, 4, MEALREADY_DELAY))),
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
            		new QuestInStateCondition(QUEST_SLOT, 0, "tell_dessert"),
            		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert"),
            		new QuestInStateCondition(QUEST_SLOT, 0, "prepare_decentmeal"))),
            ConversationStates.QUESTION_1,
            "Here you are! Is my #meal finally ready yet?",
            null
        );

        // Player wants to be reminded
        // add trigger words for both 'meal' and 'dessert'
        npc_customer.add(ConversationStates.QUESTION_1,
            Arrays.asList("meal", "dessert"),
            new OrCondition(
        		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_maindish"),
        		new QuestInStateCondition(QUEST_SLOT, 0, "tell_dessert"),
        		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert"),
        		new QuestInStateCondition(QUEST_SLOT, 0, "prepare_decentmeal")),
            ConversationStates.QUESTION_1,
            null,
            new checkQuestInProgressAction()
        );
        
        // Player wants to be reminded
        // add trigger words for each of the short names of a main dish
        Iterator<String> i = REQUIRED_MAIN_DISHES.iterator();
        while (i.hasNext()) {
            npc_customer.add(ConversationStates.QUESTION_1,
                i.next(),
                new OrCondition(
            		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_maindish"),
            		new QuestInStateCondition(QUEST_SLOT, 0, "tell_dessert"),
            		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert"),
            		new QuestInStateCondition(QUEST_SLOT, 0, "prepare_decentmeal")),
                ConversationStates.QUESTION_1,
                null,
                new checkQuestInProgressAction()
            );
        }

        // Player wants to be reminded
        // add trigger words for each of the short names of a dessert
        Iterator<String> j = REQUIRED_DESSERTS.iterator();
        while (j.hasNext()) {
            npc_customer.add(ConversationStates.QUESTION_1,
                j.next(),
                new OrCondition(
            		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_maindish"),
            		new QuestInStateCondition(QUEST_SLOT, 0, "tell_dessert"),
            		new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert"),
            		new QuestInStateCondition(QUEST_SLOT, 0, "prepare_decentmeal")),
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

        // Player answers yes (when not having the decent meal with him)
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

    	final SpeakerNPC npc_chef = npcs.get("Stefan");
        final SpeakerNPC npc_customer = npcs.get("Groongo Rahnnt");

        /**
         * This is intended to be the better end of the quest.
         * After getting his decent meal, Groongo hints the player 
         * to bring his #thanks to Chef Stefan.
         * 
         * FIXME omero: setup the better reward.
         */
		final List<ChatAction> betterEndQuestActions = new LinkedList<ChatAction>();
		betterEndQuestActions.add(new SetQuestAction(QUEST_SLOT, 0, "done"));
		betterEndQuestActions.add(new SetQuestAction(QUEST_SLOT, 1, "complete"));
		betterEndQuestActions.add(new IncreaseKarmaAction(10.0));
		betterEndQuestActions.add(
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					int amountOfPies = Rand.randUniform(10, 15);
					new EquipItemAction("pie", amountOfPies, true).fire(player, null, null);
					npc.say(" FIXME omero: Here, take " +
						Grammar.thisthese(amountOfPies) + " " +
						Grammar.quantityNumberStrNoun(amountOfPies, "pie") +
						" and some money as my better reward!"
					);

					logger.warn("Quest state <" + player.getQuest(QUEST_SLOT) + ">");

				}
			}
		);

        npc_chef.add(ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new AndCondition(
        		new QuestCompletedCondition(QUEST_SLOT),
        		new QuestInStateCondition(QUEST_SLOT, 1, "incomplete"),
        		new NotCondition(
        				new TimePassedCondition(QUEST_SLOT, 4, BRINGTHANKS_DELAY))
    		),
    		ConversationStates.QUESTION_1,
    		"So... What did our troublesome customer say about the meal?",
    		null
		);

        npc_chef.add(ConversationStates.QUESTION_1,
                "thanks",
                new AndCondition(
            		new QuestCompletedCondition(QUEST_SLOT),
            		new QuestInStateCondition(QUEST_SLOT, 1, "incomplete"),
            		new NotCondition(
            				new TimePassedCondition(QUEST_SLOT, 4, BRINGTHANKS_DELAY))
        		),
        		ConversationStates.QUESTION_1,
        		" FIXME omero: player gets better reward.",
        		new MultipleActions(betterEndQuestActions)
    		);
        
        // Player says his greetings to Groongo,
        // the quest is running
        npc_customer.add(ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new AndCondition(
                new GreetingMatchesNameCondition(npc_customer.getName()),
                new QuestActiveCondition(QUEST_SLOT),
                new QuestInStateCondition(QUEST_SLOT, 0, "deliver_decentmeal")),
            ConversationStates.QUESTION_1,
            "Oh, you're back! Do you finally have my #meal?",
            null
        );

        // Player says meal to be reminded of what is still missing
        // quest is running
        npc_customer.add(ConversationStates.QUESTION_1,
        	Arrays.asList("meal", "dessert"),
            new AndCondition(
                new GreetingMatchesNameCondition(npc_customer.getName()),
                new QuestActiveCondition(QUEST_SLOT),
                new QuestInStateCondition(QUEST_SLOT, 0, "deliver_decentmeal")),
            ConversationStates.QUESTION_1,
            null,
            new checkQuestInProgressAction()
        );

        // Player answers no
        // waiting for Stefan?
        npc_customer.add(ConversationStates.QUESTION_1,
            ConversationPhrases.NO_MESSAGES,
            new AndCondition(
                new GreetingMatchesNameCondition(npc_customer.getName()),
                new QuestActiveCondition(QUEST_SLOT),
                new QuestInStateCondition(QUEST_SLOT, 0, "deliver_decentmeal")),
            ConversationStates.IDLE,
            "Then hurry up, go and fetch it!",
            null);

        /**
         * This is intended to be the deceiving end of the quest.
         * After getting his decent meal, Groongo hints the player 
         * to bring his #thanks to Chef Stefan.
         * 
         * FIXME omero: setup the normal reward.
         */
		final List<ChatAction> deceivingEndQuestActions = new LinkedList<ChatAction>();
		deceivingEndQuestActions.add(new DropInfostringItemAction("decent meal","Decent Meal for Groongo"));
		deceivingEndQuestActions.add(new SetQuestAction(QUEST_SLOT, 0, "done"));
		deceivingEndQuestActions.add(new SetQuestAction(QUEST_SLOT, 1, "incomplete"));
		deceivingEndQuestActions.add(new SetQuestToTimeStampAction(QUEST_SLOT, 4));
		deceivingEndQuestActions.add(new IncrementQuestAction(QUEST_SLOT, 5, 1));
		deceivingEndQuestActions.add(new IncreaseXPAction(XP_REWARD));
		deceivingEndQuestActions.add(new IncreaseKarmaAction(50.0));
		deceivingEndQuestActions.add(
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					int amountOfPies = Rand.randUniform(10, 15);
					new EquipItemAction("pie", amountOfPies, true).fire(player, null, null);
					npc.say("Here, take " +
						Grammar.thisthese(amountOfPies) + " " +
						Grammar.quantityNumberStrNoun(amountOfPies, "pie") +
						" and some money as my reward! Please bring my very deserved #thanks to" + 
						" Chef Stefan for preparing such a decent meal!"
					);
				}
			}
		);

		// Player answers yes and he indeed has the meal for Groongo  
        npc_customer.add(ConversationStates.QUESTION_1,
            ConversationPhrases.YES_MESSAGES,
            new AndCondition(
                new GreetingMatchesNameCondition(npc_customer.getName()),
                new QuestActiveCondition(QUEST_SLOT),
                new QuestInStateCondition(QUEST_SLOT, 0, "deliver_decentmeal"),
                new PlayerHasInfostringItemWithHimCondition("decent meal", "Decent Meal for Groongo")),
            ConversationStates.IDLE,
            " FIXME omero: say the player something about giving #thanks to Stefan (and get the better reward)",
            new MultipleActions(deceivingEndQuestActions)
        );
    }
}