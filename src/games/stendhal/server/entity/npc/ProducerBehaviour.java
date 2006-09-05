package games.stendhal.server.entity.npc;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.StackableItem;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The behaviour of an NPC who is able to produce something for a player
 * if the player brings the required resources. Production takes time,
 * depending on the amount of ordered products.
 * 
 * @author daniel
 */
public class ProducerBehaviour extends Behaviour {
	
	/**
	 * Given a number of seconds, returns a corresponding string like
	 * "about 5 hours" or "about 27 minutes".
	 * @param seconds
	 * @return An approximate description of the given timespan
	 */
	public static String roundTimespan(int seconds) {
		int d = seconds / (60 * 60 * 24);
		int h = seconds / (60 * 60) - d * 24;
		int min = seconds / 60 - h * 60;
		// int s = seconds - min * 60;
		if (d > 0) {
			if (h >= 12) {
				return "about " + (d + 1) + " days";
			} else {
				return "about " + d + " days";
			}
		} else if (h > 0) {
			if (min >= 30) {
				return "about " + (h + 1) + " hours";
			} else {
				return "about " + h + " hours";
			}
		} else { // if (min > 0) {
			if (min >= 30) {
				return "about " + (min + 1) + " minutes";
			} else {
				return "about " + min + " minutes";
			}
		} // else {
		// 	return s + " seconds";
		// }
	}
	
	/**
	 * To store the current status of a production order, each
	 * ProducerBehaviour needs to have an exclusive quest slot.
	 * 
	 * This slot can have three states:
	 * <ul>
	 *   <li>unset: if the player has never asked the NPC to produce
	 *       anything.</li>  
	 *   <li>done: if the player's last order has been processed.</li>  
	 *   <li>number;product;time: if the player has given an order and has not
	 *       yet retrieved the product. number is the amount of products
	 *       that the player will get, product is the name of the ordered
	 *       product, and time is the time when the order
	 *       was given, in milliseconds since the epoch.</li>
	 * </ul>
	 * 
	 * Note: The product name is stored although each ProductBehaviour only
	 * allows one type of product at the moment. We store it to make the
	 * system extensible.
	 */
	private String questSlot;
	
	/**
	 * The name of the activity, e.g. "build", "forge", "bake"
	 */
	private String productionActivity;
	
	/**
	 * The unit in which the product is counted, e.g. "bags", "pieces",
	 * "pounds"
	 */
	// private String productUnit;
	
	/**
	 * The name of the product, e.g. "plate_armor". It must be a valid item
	 * name.
	 */
	private String productName;
	
	/**
	 * A mapping which maps the name of each required resource
	 * (e.g. "iron_ore") to the amount of this resource that is
	 * required for one unit of the product.
	 */
	private Map<String, Integer> requiredResourcesPerItem;
	
	/**
	 * The number of seconds required to produce one unit of the product.
	 */
	private int productionTimePerItem;
	
	protected int amount;
	
	/**
	 * Creates a new ProducerBehaviour.
	 * @param questSlot the slot that is used to store the status 
	 * @param productionActivity the name of the activity, e.g. "build",
	 *        "forge", "bake"
	 * @param productUnit the unit in which the product is counted, e.g.
	 *                    "bags", "pieces", "pounds"
	 * @param productName the name of the product, e.g. "plate_armor". It
	 *                    must be a valid item name.
	 * @param requiredResourcesPerItem a mapping which maps the name of each
	 *                          required resource (e.g. "iron_ore") to the
	 *                          amount of this resource that is required for
	 *                          one unit of the product.
	 * @param productionTimePerItem the number of seconds required to produce
	 *                              one unit of the product.
	 */
	public ProducerBehaviour(String questSlot, String productionActivity,
					String productName,
					Map<String, Integer> requiredResourcesPerItem,
					int productionTimePerItem) {
		this.questSlot = questSlot;
		this.productionActivity = productionActivity;
		// this.productUnit = productUnit;
		this.productName = productName;
		this.requiredResourcesPerItem = requiredResourcesPerItem;
		this.productionTimePerItem = productionTimePerItem;
	}
	
	protected String getQuestSlot() {
		return questSlot;
	}
	
	protected Map<String, Integer> getRequiredResourcesPerItem() {
		return requiredResourcesPerItem;
	}
	
	protected String getProductionActivity() {
		return productionActivity;
	}
	
//	protected String getProductUnit() {
//		return productUnit;
//	}

	protected String getProductName() {
		return productName;
	}
	
	protected int getProductionTime(int amount) {
		return productionTimePerItem * amount;
	}
	
	/**
	 * Gets a nicely formulated string that describes the amounts and names
	 * of the resources that are required to produce <i>amount</i> units of the product,
	 * with hashes before the resource names in order to highlight them,
	 * e.g. "4 #wood, 2 #iron, and 6 #leather". 
	 * @param amount The amount of products that were requested
	 * @return A string describing the required resources.
	 */
	private String getRequiredResourceNamesWithHashes(int amount) {
		Set<String> requiredResourcesWithHashes = new HashSet<String>();
		for (Map.Entry<String, Integer> entry: getRequiredResourcesPerItem().entrySet()) {
			requiredResourcesWithHashes.add(amount * entry.getValue() + " #" + entry.getKey());	
		}
		return SpeakerNPC.enumerateCollection(requiredResourcesWithHashes);
	}
	
	public String getApproximateRemainingTime(Player player) {
		String orderString = player.getQuest(questSlot);
		String[] order = orderString.split(";");
		long orderTime = Long.parseLong(order[2]);
		long timeNow = new Date().getTime();
		int numberOfProductItems = Integer.parseInt(order[0]);
		// String productName = order[1];

		long finishTime = orderTime + (getProductionTime(numberOfProductItems) * 1000);
		int remainingSeconds = (int) ((finishTime - timeNow) / 1000);
		return roundTimespan(remainingSeconds);

	}
	
	private int getMaximalAmount(Player player) {
		int maxAmount = Integer.MAX_VALUE;
		for (Map.Entry<String, Integer> entry: getRequiredResourcesPerItem().entrySet()) {
			int limitationByThisResource = player.getNumberOfEquipped(entry.getKey()) / entry.getValue();
			maxAmount = Math.min(maxAmount, limitationByThisResource);
		}
		return maxAmount;
	}
	
	/**
	 * Tries to take all the resources required to produce <i>amount</i>
	 * units of the product from the player. If this is possible, asks the
	 * user if the order should be initiated.
	 * 
	 * @param npc
	 * @param player
	 * @param amount
	 */
	public boolean askForResources(SpeakerNPC npc, Player player, int amount) {
		if (getMaximalAmount(player) < amount) {
			npc.say("I can only "
					+ getProductionActivity()
					+ " "
					+ amount
					+ " "
					+ getProductName()
					+ " if you bring me "
					+ getRequiredResourceNamesWithHashes(amount)
					+ ".");
			return false;
		} else {
			this.amount = amount;
			npc.say("I need "
					+ getRequiredResourceNamesWithHashes(amount)
					+ " for this job. Will you give it to me?");
			return true;
		}
	}
	
	/**
	 * Tries to take all the resources required to produce the agreed amount
	 * of the product from the player. If this is possible, initiates
	 * an order.
	 * 
	 * @param npc
	 * @param player
	 * @param amount
	 */
	public boolean transactAgreedDeal(SpeakerNPC npc, Player player) {
		if (getMaximalAmount(player) < amount) {
			// The player tried to cheat us by placing the resource
			// onto the ground after saying "yes"
			npc.say("Hey! Don't try to trick me!");
			return false;
		} else {
			for (Map.Entry<String, Integer> entry: getRequiredResourcesPerItem().entrySet()) {
				int amountToDrop = amount * entry.getValue();
				player.drop(entry.getKey(), amountToDrop);
			}
			long timeNow = new Date().getTime();
			player.setQuest(questSlot, amount + ";" + getProductName() + ";" + timeNow);
			npc.say("OK, I will "
					+ getProductionActivity()
					+ " "
					+ amount
					+ " "
					+ getProductName()
					+ " for you, but that will take some time. Come back in " + getApproximateRemainingTime(player) + ".");
			return true;
		}
	}
	
	/**
	 * This method is called when the player returns to pick up the finished
	 * product. It checks if the NPC is already done with the order. If that
	 * is the case, the player is given the product. Otherwise, the NPC
	 * asks the player to come back later. 
	 * @param npc The producing NPC
	 * @param player The player who wants to fetch the product
	 */
	public void giveProduct(SpeakerNPC npc, Player player) {
		String orderString = player.getQuest(questSlot);
		String[] order = orderString.split(";");
		int numberOfProductItems = Integer.parseInt(order[0]);
		// String productName = order[1];
		long orderTime = Long.parseLong(order[2]);
		long timeNow = new Date().getTime();
		if (timeNow - orderTime < getProductionTime(numberOfProductItems)
								  * 1000) {
			npc.say("Welcome back! I'm still busy with your order to "
					+ getProductionActivity()
					+ " "
					+ getProductName()
					+ " for you. Come back in "
					+ getApproximateRemainingTime(player)
					+ " to get it.");
		} else {
			StackableItem products = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(getProductName());            
			products.setQuantity(numberOfProductItems);
			player.equip(products, true);
			npc.say("Welcome back! I'm done with your order. Here you have "
					+ numberOfProductItems
					+ " "
					// + getProductUnit()
					// + " of "
					+ getProductName()
					+ ".");
			player.setQuest(questSlot, "done");
			// give some XP as a little bonus for industrious workers
			player.addXP(numberOfProductItems);
			player.notifyWorldAboutChanges();
		}
	}
}
