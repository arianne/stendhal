package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The behaviour of an NPC who is able to produce something for a player if the
 * player brings the required resources. Production takes time, depending on the
 * amount of ordered products.
 * 
 * @author daniel
 */
public class ProducerBehaviour extends Behaviour {

	/**
	 * To store the current status of a production order, each ProducerBehaviour
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
	 * 
	 * Note: The product name is stored although each ProductBehaviour only
	 * allows one type of product at the moment. We store it to make the system
	 * extensible.
	 */
	private String questSlot;

	/**
	 * The name of the activity, e.g. "build", "forge", "bake"
	 */
	private String productionActivity;

	/**
	 * The unit in which the product is counted, e.g. "bags", "pieces", "pounds"
	 */
	// private String productUnit;
	/**
	 * The name of the product, e.g. "plate armor". It must be a valid item
	 * name.
	 */
	private String productName;

	/**
	 * Whether the produced item should be player bound.
	 */
	private boolean productBound;

	/**
	 * A mapping which maps the name of each required resource (e.g. "iron ore")
	 * to the amount of this resource that is required for one unit of the
	 * product.
	 */
	private Map<String, Integer> requiredResourcesPerItem;

	/**
	 * The number of seconds required to produce one unit of the product.
	 */
	private int productionTimePerItem;

	private int amount;

	/**
	 * Creates a new ProducerBehaviour.
	 * 
	 * @param questSlot
	 *            the slot that is used to store the status
	 * @param productionActivity
	 *            the name of the activity, e.g. "build", "forge", "bake"
	 * @param productName
	 *            the name of the product, e.g. "plate armor". It must be a
	 *            valid item name.
	 * @param requiredResourcesPerItem
	 *            a mapping which maps the name of each required resource (e.g.
	 *            "iron ore") to the amount of this resource that is required
	 *            for one unit of the product.
	 * @param productionTimePerItem
	 *            the number of seconds required to produce one unit of the
	 *            product.
	 */
	public ProducerBehaviour(String questSlot, String productionActivity,
			String productName, Map<String, Integer> requiredResourcesPerItem,
			int productionTimePerItem) {
		this(questSlot, productionActivity, productName,
				requiredResourcesPerItem, productionTimePerItem, false);
	}

	/**
	 * Creates a new ProducerBehaviour.
	 * 
	 * @param questSlot
	 *            the slot that is used to store the status
	 * @param productionActivity
	 *            the name of the activity, e.g. "build", "forge", "bake"
	 * @param productName
	 *            the name of the product, e.g. "plate armor". It must be a
	 *            valid item name.
	 * @param requiredResourcesPerItem
	 *            a mapping which maps the name of each required resource (e.g.
	 *            "iron ore") to the amount of this resource that is required
	 *            for one unit of the product.
	 * @param productionTimePerItem
	 *            the number of seconds required to produce one unit of the
	 *            product.
	 * @param productBound
	 *            Whether the produced item should be player bound. Use only for
	 *            special one-time items.
	 */
	public ProducerBehaviour(String questSlot, String productionActivity,
			String productName, Map<String, Integer> requiredResourcesPerItem,
			int productionTimePerItem, boolean productBound) {
		this.questSlot = questSlot;
		this.productionActivity = productionActivity;
		// this.productUnit = productUnit;
		this.productName = productName;
		this.requiredResourcesPerItem = requiredResourcesPerItem;
		this.productionTimePerItem = productionTimePerItem;
		this.productBound = productBound;
	}

	public String getQuestSlot() {
		return questSlot;
	}

	protected Map<String, Integer> getRequiredResourcesPerItem() {
		return requiredResourcesPerItem;
	}

	public String getProductionActivity() {
		return productionActivity;
	}

	// protected String getProductUnit() {
	// return productUnit;
	// }

	public String getProductName() {
		return productName;
	}

	public int getProductionTime(int amount) {
		return productionTimePerItem * amount;
	}

	/*
	 * Determine whether the produced item should be player bound.
	 * 
	 * @return <code>true</code> if the product should be bound.
	 */
	public boolean isProductBound() {
		return productBound;
	}

	/**
	 * Gets a nicely formulated string that describes the amounts and names of
	 * the resources that are required to produce <i>amount</i> units of the
	 * product, with hashes before the resource names in order to highlight
	 * them, e.g. "4 #wood, 2 #iron, and 6 #leather".
	 * 
	 * @param amount
	 *            The amount of products that were requested
	 * @return A string describing the required resources.
	 */
	private String getRequiredResourceNamesWithHashes(int amount) {
		Set<String> requiredResourcesWithHashes = new TreeSet<String>(); // use
																			// sorted
																			// TreeSet
																			// instead
																			// of
																			// HashSet
		for (Map.Entry<String, Integer> entry : getRequiredResourcesPerItem().entrySet()) {
			requiredResourcesWithHashes.add(Grammar.quantityplnoun(amount
					* entry.getValue(), "#" + entry.getKey()));
		}
		return Grammar.enumerateCollection(requiredResourcesWithHashes);
	}

	public String getApproximateRemainingTime(Player player) {
		String orderString = player.getQuest(questSlot);
		String[] order = orderString.split(";");
		long orderTime = Long.parseLong(order[2]);
		long timeNow = new Date().getTime();
		int numberOfProductItems = Integer.parseInt(order[0]);
		// String productName = order[1];

		long finishTime = orderTime
				+ (getProductionTime(numberOfProductItems) * 1000);
		int remainingSeconds = (int) ((finishTime - timeNow) / 1000);
		return TimeUtil.approxTimeUntil(remainingSeconds);

	}

	private int getMaximalAmount(Player player) {
		int maxAmount = Integer.MAX_VALUE;
		for (Map.Entry<String, Integer> entry : getRequiredResourcesPerItem().entrySet()) {
			int limitationByThisResource = player.getNumberOfEquipped(entry.getKey())
					/ entry.getValue();
			maxAmount = Math.min(maxAmount, limitationByThisResource);
		}
		return maxAmount;
	}

	/**
	 * Tries to take all the resources required to produce <i>amount</i> units
	 * of the product from the player. If this is possible, asks the user if the
	 * order should be initiated.
	 * 
	 * @param npc
	 * @param player
	 * @param amount
	 */
	public boolean askForResources(SpeakerNPC npc, Player player, int amount) {
		if (getMaximalAmount(player) < amount) {
			npc.say("I can only " + getProductionActivity() + " "
					+ Grammar.quantityplnoun(amount, getProductName())
					+ " if you bring me "
					+ getRequiredResourceNamesWithHashes(amount) + ".");
			return false;
		} else {
			setAmount(amount);
			npc.say("I need you to fetch me "
					+ getRequiredResourceNamesWithHashes(amount)
					+ " for this job. Do you have it?");
			return true;
		}
	}

	/**
	 * Tries to take all the resources required to produce the agreed amount of
	 * the product from the player. If this is possible, initiates an order.
	 * 
	 * @param npc
	 *            the involved NPC
	 * @param player
	 *            the involved player
	 */
	@Override
	public boolean transactAgreedDeal(SpeakerNPC npc, Player player) {
		if (getMaximalAmount(player) < amount) {
			// The player tried to cheat us by placing the resource
			// onto the ground after saying "yes"
			npc.say("Hey! I'm over here! You'd better not be trying to trick me...");
			return false;
		} else {
			for (Map.Entry<String, Integer> entry : getRequiredResourcesPerItem().entrySet()) {
				int amountToDrop = amount * entry.getValue();
				player.drop(entry.getKey(), amountToDrop);
			}
			long timeNow = new Date().getTime();
			player.setQuest(questSlot, amount + ";" + getProductName() + ";"
					+ timeNow);
			npc.say("OK, I will "
					+ getProductionActivity()
					+ " "
					+ amount
					+ " "
					+ getProductName()
					+ " for you, but that will take some time. Please come back in "
					+ getApproximateRemainingTime(player) + ".");
			return true;
		}
	}

	/**
	 * This method is called when the player returns to pick up the finished
	 * product. It checks if the NPC is already done with the order. If that is
	 * the case, the player is given the product. Otherwise, the NPC asks the
	 * player to come back later.
	 * 
	 * @param npc
	 *            The producing NPC
	 * @param player
	 *            The player who wants to fetch the product
	 */
	public void giveProduct(SpeakerNPC npc, Player player) {
		String orderString = player.getQuest(questSlot);
		String[] order = orderString.split(";");
		int numberOfProductItems = Integer.parseInt(order[0]);
		// String productName = order[1];
		long orderTime = Long.parseLong(order[2]);
		long timeNow = new Date().getTime();
		if (timeNow - orderTime < getProductionTime(numberOfProductItems) * 1000) {
			npc.say("Welcome back! I'm still busy with your order to "
					+ getProductionActivity() + " " + getProductName()
					+ " for you. Come back in "
					+ getApproximateRemainingTime(player) + " to get it.");
		} else {
			StackableItem products = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
					getProductName());
			products.setQuantity(numberOfProductItems);

			if (isProductBound()) {
				products.setBoundTo(player.getName());
			}

			player.equip(products, true);
			npc.say("Welcome back! I'm done with your order. Here you have "
					+ Grammar.quantityplnoun(numberOfProductItems,
							getProductName()) + ".");
			player.setQuest(questSlot, "done");
			// give some XP as a little bonus for industrious workers
			player.addXP(numberOfProductItems);
			player.notifyWorldAboutChanges();
		}
	}

	/**
	 * Sets the amount that the player wants to buy from the NPC.
	 * 
	 * @param amount
	 *            amount
	 */
	public void setAmount(int amount) {
		if (amount < 1) {
			amount = 1;
		}
		if (amount > 1000) {
			amount = 1;
		}
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}
}
