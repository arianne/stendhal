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
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
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

import marauroa.common.Pair;
import marauroa.common.game.IRPZone;
import org.apache.log4j.Logger;

/**
 * QUEST: Meal for Groongo, The Troublesome Customer
 * <p>
 * PARTICIPANTS:
 * <ul>
 *  <li> Groongo Rahnnt, The Troublesome Customer
 *  <li> Stefan, The Fado's Hotel Chef
 * </ul>
 *
 * STEPS:
 * <ul>
 *  <li> Groongo is hungry, asks the player to bring him something to eat,
 *  <li> The player checks with Stefan, he will tell him what he needs to fulfill Groongo's request,
 *  <li> The player has to fetch the required items/foodstuff,
 *  <li> The player talks again with Stefan, gives him the resources,
 *  <li> Stefan tells the player how much time he requires to prepare Groongo's order,
 *  <li> After enough time has elapsed, the player can collect Groongo's order from Stefan,
 *  <li> The player delivers the order to Grongo's,
 *  <li> Groongo is happy and gives the player a reward of some kind.   
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
     * 
     * FIXME omero: Quest states are not fully defined yet.
     * 
     * QUEST_SLOT will be used to hold the different states of the quest.
     *
     * QUEST_SLOT sub slot 0
     * will hold the main states, which can be:
     * - rejected, the player has refused to undertake the quest
     * - inprogress, 
     * - done, the player has completed the quest
     *
     * QUEST_SLOT sub slot 1
     * - a main dish short name and whether is ready or not 
     *   in the form [maindish]:[ready|notready], e.g.:
     * - paella:ready
     * 
     * QUEST_SLOT sub slot 2
     * - a dessert short name and whether is ready or not
     *   in the form [dessert]:[ready|notready], e.g.:
     * - slagroomtart:notready
     * 
     * NOTE:
     * We use two separate sub slot for for main dish and dessert
     * because we want full history
     * 
     * NOTE:
     * In a sub slot, ingredients cannot be stored as a collection of
     * key=value token pairs.
     * 
     * chicken=1:rice=1:tomato=1:garlic=1:trout=1:perch=1:onion=1:
     * chicken=1;rice=1;tomato=1;garlic=1;trout=1;perch=1;onion=1;
     * 
     * We cannot use directly:
     * final ItemCollection missingIngredients = new ItemCollection();
     * missingIngredients.addFromQuestStateString(player.getQuest(QUEST_SLOT, 2));
     *
     * When the quest is completed,
     * QUEST_SLOT sub slot 3 will be marked with a timestamp
     * QUEST_SLOT sub slot 4 will be incremented (number of times quest was completed)
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
            "Meal for Groongo Rahnnt",
            "Groongo is hungry and wants to have a meal at Fado's Restaurant.",
            true);

        // FIXME omero: this quest will require a yet unknown number of stages
        stageBeginQuest();
        stageCollectIngredientsForMainDish();
        stageCollectIngredientsForDessert();
        stageDeliverMeal();        
    }

    @Override
    public List<String> getHistory(final Player player) {

        final List<String> res = new ArrayList<String>();

        if (!player.hasQuest(QUEST_SLOT)) {
            return res;
        }

        res.add("I've met Groongo Rahnnt in Fado's restaurant.");
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
        	final ItemCollection missingIngredients = new ItemCollection();
        	String ingredients = "";        	
        	if ("fetch_maindish".equals(questState)) {
        		ingredients = getRequiredIngredientsForMainDish(player.getQuest(QUEST_SLOT,1));
        		missingIngredients.addFromQuestStateString(ingredients);
            	res.add("I've agreed to bring Groongo " +
            			Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 1))) +
            			" and I'm helping Chef Stefan finding the ingredients to prepare it: " +
            			Grammar.enumerateCollection(missingIngredients.toStringList()) + "."
            	);
        	} else {
        		ingredients = getRequiredIngredientsForDessert(player.getQuest(QUEST_SLOT, 2));
        		missingIngredients.addFromQuestStateString(ingredients);
            	res.add("I've already brought Chef Stefan the ingredients to prepare " +
            			Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 1)))
            	);
            	res.add("Along with that, Groongo now also wants " +
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

        return requiredMainDish.get(Rand.rand(requiredMainDish.size()));
    }

    private String getRequiredDessert() {
        // Desserts Groongo will ask for the quest
        // All desserts are temporary for developing purposes, subject to change 
        final List<String> requiredDesserts =
                Arrays.asList(
                        "macedonia",
                        "slagroomtart",
                        "brigadeiro",
                        "vatrushka"
                        );

                        //"tarte a la rhubarbe",
                        //"schwarzwalder kirschtorte",
                        //"ngat biang",
                        //"gulab jamun"
        
        return requiredDesserts.get(Rand.rand(requiredDesserts.size()));
    }

    private String getRequiredMainDishFancyName(final String requiredMainDish) {
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
        
        return requiredMainDishFancyName.get(requiredMainDish);

    }

    private String getRequiredDessertFancyName(final String requiredDessert) {
        // used only to build sentences
        // to avoid requiring the player to type long and complicated fancy dessert names
        final Map<String, String> requiredDessertFancyName = new HashMap<String, String>();
        requiredDessertFancyName.put("brigadeiro", "brigadeiro");
        requiredDessertFancyName.put("macedonia", "macedonia di frutta");
        requiredDessertFancyName.put("slagroomtart", "slagroomtart");
        requiredDessertFancyName.put("vatrushka", "vatrushka");

        return requiredDessertFancyName.get(requiredDessert);
    }

    /**
     * Returns required ingredients and quantities to collect for preparing the main dish
     *
     * @param requiredMainDish
     * @return A string composed of semicolon separated key=value token pairs.
     */
    private String getRequiredIngredientsForMainDish(final String requiredMainDish) {

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
        requiredIngredients_paidakia.put("potato", 1);
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
        
        final HashMap<String, Integer> requiredIngredients_kuskus = new HashMap<String, Integer>();
        requiredIngredients_kuskus.put("flour", 1);
        requiredIngredients_kuskus.put("water", 1);
        requiredIngredients_kuskus.put("courgette", 1);
        requiredIngredients_kuskus.put("onion", 1);
        requiredIngredients_kuskus.put("garlic", 1);
        //requiredIngredients_kuskus.put("salt", 1);
        //requiredIngredients_kuskus.put("pepper", 1);

        final HashMap<String, HashMap<String, Integer>> requiredIngredientsForMainDish = new HashMap<String, HashMap<String, Integer>>();
        requiredIngredientsForMainDish.put("paella", requiredIngredients_paella);
        requiredIngredientsForMainDish.put("ciorba", requiredIngredients_ciorba);
        requiredIngredientsForMainDish.put("lasagne", requiredIngredients_lasagne);
        requiredIngredientsForMainDish.put("schnitzel", requiredIngredients_schnitzel);
        requiredIngredientsForMainDish.put("consomme", requiredIngredients_consomme);
        requiredIngredientsForMainDish.put("paidakia", requiredIngredients_paidakia);
        requiredIngredientsForMainDish.put("kuskus", requiredIngredients_kuskus);
        requiredIngredientsForMainDish.put("kushari", requiredIngredients_kushari);


        String ingredients = "";
        final HashMap<String, Integer>  requiredIngredients = requiredIngredientsForMainDish.get(requiredMainDish);
        for (final Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
            ingredients = ingredients + entry.getKey() + "=" + entry.getValue() + ";";
        }

        //return requiredIngredientsForMainDish.get(requiredMainDish);
        return ingredients;
    }

    /**
     * Returns required ingredients and quantities to collect for preparing the dessert
     *
     * @param requiredDessert
     * @return A string composed of semicolon separated key=value token pairs.
     */
    private String getRequiredIngredientsForDessert(final String requiredDessert) {

        // All ingredients are temporary for developing purposes, subject to change
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

        //return requiredIngredientsForDessert.get(requiredDessert);
        return ingredients;

    }

    class chooseMainDishAction implements ChatAction {
        public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {

            final String requiredMainDish = getRequiredMainDish();

            player.setQuest(QUEST_SLOT, 0, "fetch_maindish");
            player.setQuest(QUEST_SLOT, 1, requiredMainDish);
            SpeakerNPC.say(
                    "Bah! I'm really in the mood of trying " +
                    Grammar.a_noun(getRequiredMainDishFancyName(requiredMainDish)) +
                    ". Now go ask Chef Stefan to prepare my #meal, at once!"
            );
            logger.info("Quest state <" + player.getQuest(QUEST_SLOT) + ">");
        }
    }

    class chooseDessertAction implements ChatAction {
        public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {

            final String requiredDessert = getRequiredDessert();

            player.setQuest(QUEST_SLOT, 0, "fetch_dessert");
            player.setQuest(QUEST_SLOT, 2, requiredDessert);
            SpeakerNPC.say(
                    "Bah! I'm really in the mood of trying " +
                    Grammar.a_noun(getRequiredDessertFancyName(requiredDessert)) +
                    ". Now go ask Chef Stefan to prepare my #dessert, at once!"
            );
            logger.info("Quest state <" + player.getQuest(QUEST_SLOT) + ">");
        }
    }
    
    // Groongo uses this to remind the player of what he has to bring him currently
    // FIXME omero: checkQuestInProgressAction needs to discriminate in which stage the quest currently is
    class checkQuestInProgressAction implements ChatAction {
        public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {
            SpeakerNPC.say(
                    "Bah! I've asked for " +
                    Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT,1))) +
                    ". Did you bring that to me?"
            );
            logger.info("Quest state <" + player.getQuest(QUEST_SLOT) + ">");
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
                    "Oh! So you've been asked for " +
                    Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT,1))) +
                    ". For that I'll need some ingredients that at the moment I'm missing: " +
                    Grammar.enumerateCollection(missingIngredients.toStringListWithHash()) +
                    ". Do you happen to have all of them already?"
            );
            logger.info("Quest state <" + player.getQuest(QUEST_SLOT) + ">");
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
                    "Oh! So you've been asked for " +
                    Grammar.a_noun(getRequiredDessertFancyName(player.getQuest(QUEST_SLOT,1))) +
                    ". For that I'll need some ingredients that at the moment I'm missing: " +
                    Grammar.enumerateCollection(missingIngredients.toStringListWithHash()) +
                    ". Do you happen to have any of those already?"
            );
            logger.info("Quest state <" + player.getQuest(QUEST_SLOT) + ">");
        }
    }
    
    // Stefan uses this when the player has said he has all the ingredients
    // for preparing something
    class collectAllRequestedIngredientsAtOnceAction implements ChatAction {

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
    						" you have brought! I said I need " +
    						ingredient.getValue() + " of " +
    						Grammar.thatthose(ingredient.getValue()));
    				playerHasAllIngredients = false;
    				break;
    			}
    		}
    		
    		if (playerHasAllIngredients) {
    			// we're sure we can take all the ingredients
    			for (final Map.Entry<String, Integer> ingredient : missingIngredients.entrySet()) {
    				player.drop(ingredient.getKey(), ingredient.getValue());
    			}
    			raiser.say(
    					"Excellent job " + player.getName() + "!"
    			);
    			
    			if ("fetch_maindish".equals(player.getQuest(QUEST_SLOT, 0))) {
    				// send player to ask Groongo for a dessert of his choice
    				player.setQuest(QUEST_SLOT, 0, "askfordessert");
    				raiser.say(
    						"But our troublesome customer has forgot to tell you what #dessert he would like along " +
    						Grammar.a_noun(getRequiredMainDishFancyName(player.getQuest(QUEST_SLOT, 1))) +
    						" Quick! While I'm preparing it, go back to him and ask which one he prefers!"   						
    				);
    			} else if ("fetch_dessert".equals(player.getQuest(QUEST_SLOT, 0))) {
    				// send player to bring Groongo his decent meal
    				player.setQuest(QUEST_SLOT, 0, "delivermeal");
    			}
    			
    		} else {
    			
    			raiser.say(
    				"I cannot start preparing [requested thing] if I'm missing some of the ingredients!"
    			);
    		}
    		
            logger.info("Quest state <" + player.getQuest(QUEST_SLOT) + ">");
            
    	}

   		ItemCollection getMissingIngredients(final Player player) {

        	final ItemCollection missingIngredients = new ItemCollection();
        	final String questState = player.getQuest(QUEST_SLOT, 0);
        	String ingredients = "";        	
        	if ("fetch_maindish".equals(questState)) {
        		ingredients = getRequiredIngredientsForMainDish(player.getQuest(QUEST_SLOT,1));
        		missingIngredients.addFromQuestStateString(ingredients);
        	} else {
        		ingredients = getRequiredIngredientsForDessert(player.getQuest(QUEST_SLOT, 2));
        		missingIngredients.addFromQuestStateString(ingredients);
        	}
   			return missingIngredients;

    	}
    }

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
        // FIXME omero: sub slot to use for timestamp?
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
        // set the quest slot (still have to decide how)
        // switch to stageCollectIngredients, stageQuestRunning...?
        npc.add(ConversationStates.QUEST_OFFERED,
            ConversationPhrases.YES_MESSAGES,
            new QuestNotStartedCondition(QUEST_SLOT),
            // here we want to transition to the collect ingredients stage
            // turning idle for the time being
            ConversationStates.IDLE, null,
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

    public void stageCollectIngredientsForMainDish() {

        final SpeakerNPC npc = npcs.get("Stefan");

        npc.add(ConversationStates.IDLE,
                ConversationPhrases.GREETING_MESSAGES,
                new AndCondition(
                        new GreetingMatchesNameCondition(npc.getName()),
                        new QuestInStateCondition(QUEST_SLOT, 0, "fetch_maindish")),
                ConversationStates.QUESTION_1,
                null,
                new checkIngredientsForMainDishAction()
        );

        // Player says no, he doesn't have any ingredients yet for main dish
        npc.add(ConversationStates.QUESTION_1,
                ConversationPhrases.NO_MESSAGES,
                null,
                ConversationStates.IDLE,
                "Be sure to bring me those ingredients all at once!",
                null
        );

        // Player says yes
        npc.add(ConversationStates.QUESTION_1,
                ConversationPhrases.YES_MESSAGES,
                null,
                ConversationStates.IDLE,
                null,
                new collectAllRequestedIngredientsAtOnceAction()
        );

        // Player collected all the ingredients
        /*
        ChatAction completeAction = new MultipleActions(
            new ChatAction() {
                public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {
                	logger.info("bravo1");
                	SpeakerNPC.say("bravo1");
                }
            },
            new ChatAction() {
                public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {
                	
                	logger.info("bravo2 <" + player.getQuest(QUEST_SLOT) + ">");
                	SpeakerNPC.say("bravo2 <" + player.getQuest(QUEST_SLOT) + ">");
                }
            }
        );
        */
    }
    
    public void stageCollectIngredientsForDessert() {

        final SpeakerNPC npc = npcs.get("Stefan");

        npc.add(ConversationStates.IDLE,
                ConversationPhrases.GREETING_MESSAGES,
                new AndCondition(
                        new GreetingMatchesNameCondition(npc.getName()),
                        new QuestInStateCondition(QUEST_SLOT, 0, "fetch_dessert")),
                ConversationStates.QUESTION_1,
                null,
                new checkIngredientsForDessertAction()
        );

        // Player says no, he doesn't have any ingredients yet for main dish
        npc.add(ConversationStates.QUESTION_1,
                ConversationPhrases.NO_MESSAGES,
                null,
                ConversationStates.IDLE,
                "Be sure to bring me those ingredients all at once!",
                null
        );
        // Player says no, he doesn't have any ingredients yet for dessert
        npc.add(ConversationStates.QUESTION_1,
                ConversationPhrases.NO_MESSAGES,
                null,
                ConversationStates.IDLE,
                "Be sure to bring me those ingredients all at once!",
                null
        );

        // Player says yes
        npc.add(ConversationStates.QUESTION_1,
                ConversationPhrases.YES_MESSAGES,
                null,
                ConversationStates.IDLE,
                null,
                new collectAllRequestedIngredientsAtOnceAction()
        );

    }

    public void stageDeliverMeal() {

        final SpeakerNPC npc = npcs.get("Groongo Rahnnt");
        
        // Player says his greetings to Groongo,
        // the quest is running
        npc.add(ConversationStates.IDLE,
            ConversationPhrases.GREETING_MESSAGES,
            new AndCondition(
                    new GreetingMatchesNameCondition(npc.getName()),
                    new QuestActiveCondition(QUEST_SLOT)),
            ConversationStates.QUESTION_1,
            "Finally! Do you have my #meal already?",
            null
        );

        // Player says meal to be reminded of what is still missing
        // quest is running
        npc.add(ConversationStates.QUESTION_1,
            "meal", null,
            ConversationStates.QUESTION_1,
            null,
            new checkQuestInProgressAction()
        );

        // Player answers no
        npc.add(ConversationStates.QUESTION_1,
            ConversationPhrases.NO_MESSAGES,
            new QuestNotCompletedCondition(QUEST_SLOT),
            ConversationStates.IDLE,
            "Then hurry up, go and fetch it!",
            null);

        // Player answers yes  
        npc.add(ConversationStates.QUESTION_1,
            ConversationPhrases.YES_MESSAGES, null,
            ConversationStates.IDLE,
            "Excellent! Let's see...",
            // here we will trigger the action that checks
            // that the player has indeed completed stage2 (meal+dessert)
            null
        );
        
        ChatAction addRewardAction = new ChatAction() {
        	public void fire(final Player player, final Sentence sentence, final EventRaiser SpeakerNPC) {
        		SpeakerNPC.say("bravo3");
        	}
        };

    }
}