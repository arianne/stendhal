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
package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.ExpressionType;
import games.stendhal.common.parser.WordList;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * The behaviour of an NPC who is able to produce one or more things for a player,
 * given that the player brings the necessary resources. 
 * Production takes time, depending on the amount of the product requested.
 *
 * @author omero
 */
public class MultiProducerBehaviour extends TransactionBehaviour {

	/**
	 * To store the current status of a production order, each MultiProducerBehaviour
     * needs to have an exclusive quest slot.
	 *
	 * This slot can have three states:
	 * <ul>
	 * <li>unset: if the player has never asked the NPC to produce anything.</li>
	 * <li>done: if the player's last order has been processed.</li>
	 * <li>number;product;time: if the player has given an order and has not
	 * yet retrieved the product. number is the amount of products that the
	 * player will get, product is the name of the ordered product, and time is
	 * the time when the order was given, in milliseconds since the epoch.</li>
	 * </ul>
	 */
    private final String questSlot;

	/**
	 * The name of the activity, e.g. "build", "forge", "bake"
	 */
    private final String productionActivity;

	/**
	 * TODO: For each of the products, in which units the product is counted, e.g. "bags", "pieces", "pounds"
     * This is not used at the moment.
	 */
    //private final HashMap<String, Map<String, String>> productsUnits;

    /**
     * TODO: Use this to return a 'default' product among the ones the NPC is able to produce?
     * This is not used at the moment.
     */
	//private final String productName;

	/**
	 * The set of products the NPC is providing
	 */
    private final HashSet<String> productsNames;

	/**
	 * For each of the products, whether the product will be bound to the player or not
	 */
    private final HashMap<String, Boolean> productsBound;

	/**
	 * Maps each of the products to which resources are required for producing one unit of it
	 */
    private final HashMap<String, Map<String, Integer>> requiredResourcesPerProduct;

	/**
	 * Maps each of the products to how many seconds it will take to produce one unit of the product.
	 */
    private final HashMap<String, Integer> productionTimesPerProduct;

    private static Logger logger = Logger.getLogger(MultiProducerBehaviour.class);

    /**
     * Creates a new MultiProducerBehaviour.
     * 
     * @param questSlot
     *            The slot that is used to store the status of the production.
     * @param productionActivity
     *            The name of the activity, e.g. "build", "brew", "bake"
	 * @param productsNames
	 *            The set of items that the NPC is able to produce.
     *            All must be valid items names.
     * @param requiredResourcesPerProduct
     *            The mapping which maps the name of each product to 
     *            the mapping of resources required for producing that product, stated as <item, quantity> 
     * @param productionTimesPerProduct
     *            The mapping of the name of a product to
     *            the amount of time required to produce it
     * @param productsBound
     *            The mapping of the name of a product to
     *            whether or not that product will be bound to the player.
     */
    public MultiProducerBehaviour(
            final String questSlot,
            final String productionActivity,
            final HashSet<String> productsNames,
            final HashMap<String, Map<String, Integer>> requiredResourcesPerProduct,
            final HashMap<String, Integer> productionTimesPerProduct,
            final HashMap<String, Boolean> productsBound) {

        super(productsNames);

        this.questSlot = questSlot;
        this.productionActivity = productionActivity;
		// this.productsUnits = productsUnits;
        this.productsNames = productsNames;
        this.requiredResourcesPerProduct = requiredResourcesPerProduct;
        this.productionTimesPerProduct = productionTimesPerProduct;
        this.productsBound = productsBound;

		// add the activity word as verb to the word list in case it is still missing there
        logger.debug("Registering VERB [" + productionActivity + "] in the word list");
		WordList.getInstance().registerVerb(productionActivity);

        // iterate through each of the required resource items and add them as a object to the word list
        String requiredResourceName;
        Iterator<String> i = requiredResourcesPerProduct.keySet().iterator();
        while (i.hasNext()) {
            requiredResourceName = i.next();
            logger.debug("Registering OBJECT [" + requiredResourceName + "] in the word list");
			WordList.getInstance().registerName(requiredResourceName, ExpressionType.OBJECT);
		}
    }

    public String getQuestSlot() {
        return questSlot;
    }

    protected Map<String, Integer> getRequiredResourcesPerProduct(final String productName) {
        return requiredResourcesPerProduct.get(productName);
    }

    public String getProductionActivity() {
        return productionActivity;
    }

//	protected String getProductUnit(final String productName) {
//	return productsUnits.get(productName);
//	}

	/**
	 * Return what products the NPC is able to produce
	 *
	 * @return products names
	 */
    public HashSet<String> getProductsNames() {
        return productsNames;
    }

    /**
     * Return how much time is required to make the requested amount of products
     * 
     * @param productName
     * @param amount
     * @return amount of time to produce amount of product
     */
    protected int getProductionTime(final String productName, final int amount) {
        return productionTimesPerProduct.get(productName) * amount;
    }

	/**
	 * Return whether the requested product will be bound to the player.
	 *
	 * @param productName
	 * @return <code>true</code> if the product should be bound.
	 */
	public boolean isProductBound(final String productName) {
		return productsBound.get(productName);
	}

    /**
     * Create a text representing a saying of approximate time until
     * the order being produced is ready
     * 
     * @param player
     * @return A string describing the remaining time.
     */
    public String getApproximateRemainingTime(final Player player) {
        final String orderString = player.getQuest(questSlot);
        final String[] order = orderString.split(";");
        final long orderTime = Long.parseLong(order[2]);
        final long timeNow = new Date().getTime();
        final int numberOfProductItems = Integer.parseInt(order[0]);
        final String productName = order[1];

        final long finishTime = orderTime
                + (getProductionTime(productName, numberOfProductItems) * 1000L);
        final int remainingSeconds = (int) ((finishTime - timeNow) / 1000);
        return TimeUtil.approxTimeUntil(remainingSeconds);
    }

    /**
     * Is the order ready for this player?
     * 
     * @param player
     * @return true if the order is ready.
     */
    public boolean isOrderReady(final Player player) {
        final String orderString = player.getQuest(questSlot);
        final String[] order = orderString.split(";");
        final int numberOfProductItems = Integer.parseInt(order[0]);
        final String productName = order[1];
        final long orderTime = Long.parseLong(order[2]);
        final long timeNow = new Date().getTime();
        return timeNow - orderTime >= getProductionTime(productName, numberOfProductItems) * 1000L; 
    }

    /**
     * Checks how many items are being produced for this particular order
     * 
     * @param player
     * @return number of items
     */
    public int getNumberOfProductItems(final Player player) {
        final String orderString = player.getQuest(questSlot);
        final String[] order = orderString.split(";");

        return Integer.parseInt(order[0]);
    }

    /**
     * Return a nicely formulated string that describes the amounts and names of
     * the resources that are required to produce <i>amount</i> units of the
     * choosen product, with hashes prepended to the resource names in order to
     * highlight them, e.g. "4 #wood, 2 #iron, and 6 #leather".
     * 
     * @param productName
     *            The requested product name
	 * @param amount
	 *            The amount of products that were requested
     * @return A string describing the required resources with hashes.
     */
    public String getRequiredResourceNamesWithHashes(final String productName, final int amount) {
        // use sorted TreeSet instead of HashSet
        final Set<String> requiredResourcesWithHashes = new TreeSet<String>();
        for (final Map.Entry<String, Integer> entry : getRequiredResourcesPerProduct(productName).entrySet()) {
            requiredResourcesWithHashes.add(Grammar.quantityplnounWithHash(amount
                    * entry.getValue(), entry.getKey()));
        }
        return Grammar.enumerateCollection(requiredResourcesWithHashes);
    }

	/**
	 * Return a nicely formulated string that describes the amounts and names of
	 * the resources that are required to produce <i>amount</i> units of the
	 * chosen product.
	 *
     * @param productName
     *            The requested product name
	 * @param amount
	 *            The amount of products that were requested
	 * @return A string describing the required resources.
	 */
    public String getRequiredResourceNames(final String productName, final int amount) {
		// use sorted TreeSet instead of HashSet
		final Set<String> requiredResources = new TreeSet<String>();
		for (final Map.Entry<String, Integer> entry : getRequiredResourcesPerProduct(productName).entrySet()) {
			requiredResources.add(Grammar.quantityplnoun(amount * entry.getValue(), entry.getKey()));
		}
		return Grammar.enumerateCollection(requiredResources);
	}

    /**
     * At the time the order is made,
     * Checks how many items the NPC can offer to produce based on what the player is carrying
     * 
     * @param productName
     *            The requested product name
     * @param player
     * @return maximum number of items
     */
    protected int getMaximalAmount(final String productName, final Player player) {
        int maxAmount = Integer.MAX_VALUE;

        for (final Map.Entry<String, Integer> entry : getRequiredResourcesPerProduct(productName).entrySet()) {
            final int limitationByThisResource = player.getNumberOfEquipped(entry.getKey()) / entry.getValue();
            maxAmount = Math.min(maxAmount, limitationByThisResource);
        }

        return maxAmount;
    }

    /**
     * At the time the order is made,
	 * Tries to take all the resources required to produce <i>amount</i> units
	 * of the product from the player. If this is possible, asks the user if the
	 * order should be initiated.
     * 
	 * @param res
     * @param npc
     * @param player
     * @return true if all resources can be taken
     */
    public boolean askForResources(final ItemParserResult res, final EventRaiser npc, final Player player) {
		int amount = res.getAmount();
        String productName = res.getChosenItemName();

        if (getMaximalAmount(productName, player) < amount) {
            npc.say("I can only " + getProductionActivity() + " "
                    + Grammar.quantityplnoun(amount, productName, "a")
                    + " if you bring me "
                    + getRequiredResourceNamesWithHashes(productName, amount) + ".");
            return false;
        } else {
			res.setAmount(amount);
            npc.say("I need you to fetch me "
                    + getRequiredResourceNamesWithHashes(productName, amount)
					+ " for this job, which will take "
                    + TimeUtil.approxTimeUntil(getProductionTime(productName, amount))
                    + ". Do you have what I need?");
                   
            return true;
        }
    }

    /**
     * At the time the order is made,
     * tries to take all the resources required to produce the agreed amount of
     * the chosen product from the player. If this is possible, initiates an order.
     * 
     * @param npc
     *            the involved NPC
     * @param player
     *            the involved player
     */
    @Override
    public boolean transactAgreedDeal(ItemParserResult res, final EventRaiser npc, final Player player) {
		int amount = res.getAmount();
        String productName = res.getChosenItemName();

        if (getMaximalAmount(productName, player) < amount) {
            // The player tried to cheat us by placing the resource
            // onto the ground after saying "yes"
            npc.say("Hey! I'm over here! You'd better not be trying to trick me...");
            return false;
        } else {
            for (final Map.Entry<String, Integer> entry : getRequiredResourcesPerProduct(productName).entrySet()) {
                final int amountToDrop = amount * entry.getValue();
                player.drop(entry.getKey(), amountToDrop);
            }
            final long timeNow = new Date().getTime();
            player.setQuest(questSlot, amount + ";" + productName + ";" + timeNow);
            npc.say("OK, I will "
                    + getProductionActivity()
                    + " "
                    + Grammar.quantityplnoun(amount, productName, "a")
                    + " for you, but that will take some time. Please come back in "
                    + getApproximateRemainingTime(player) + ".");
            return true;
        }
    }

	/**
	 * At the time the player returns to pick up the finished product,
     * check that the NPC is done with the order.
     * If that is the case, the player is given the product,
     * otherwise the NPC tells to the player to come back later.
	 * 
	 * @param npc
	 *            The producing NPC
	 * @param player
	 *            The player who wants to fetch the product
	 */
	public void giveProduct(final EventRaiser npc, final Player player) {
        final String orderString = player.getQuest(questSlot);
        final String[] order = orderString.split(";");
        final int numberOfProductItems = Integer.parseInt(order[0]);
        final String productName = order[1];

		if (!isOrderReady(player)) {
			npc.say("Welcome back! I'm still busy with your order to "
					+ getProductionActivity() + " " + Grammar.quantityplnoun(numberOfProductItems, productName, "a")
					+ " for you. Come back in "
					+ getApproximateRemainingTime(player) + " to get it.");
		} else {
			final StackableItem products = (StackableItem) SingletonRepository.getEntityManager().getItem(productName);
			products.setQuantity(numberOfProductItems);

			if (isProductBound(productName)) {
				products.setBoundTo(player.getName());
			}

			if (player.equipToInventoryOnly(products)) {					
				npc.say("Welcome back! I'm done with your order. Here you have "
					+ Grammar.quantityplnoun(numberOfProductItems, productName, "the") + ".");
				player.setQuest(questSlot, "done");
				// give some XP as a little bonus for industrious workers
				player.addXP(numberOfProductItems);
				player.notifyWorldAboutChanges();
				player.incProducedCountForItem(productName, products.getQuantity());
				SingletonRepository.getAchievementNotifier().onProduction(player);
			} else {
				npc.say("Welcome back! I'm done with your order. But right now you cannot take the "
						+ Grammar.plnoun(numberOfProductItems, productName) 
						+ ". Come back when you have space.");
			}
		}
	}

	/**
	 * Answer with an error message in case the request could not be fulfilled.
	 *
	 * @param res
	 * @param npcAction
	 * @return error message
	 */
	public String getErrormessage(final ItemParserResult res, final String npcAction) {
		return getErrormessage(res, getProductionActivity(), npcAction);
	}

}
