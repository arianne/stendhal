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
public class ProducerBehaviour {
	
	/**
	 * To store the current status of a production order, each
	 * ProducerBehaviour needs to have an exclusive quest slot.
	 * 
	 * This slot can have three states:
	 * <ul>
	 *   <li>unset: if the player has never asked the NPC to produce
	 *       anything.</li>  
	 *   <li>done: if the player's last order has been processed.</li>  
	 *   <li>number;time: if the player has given an order and has not
	 *       yet retrieved the product. number is the amount of products
	 *       that the player will get, and time is the time when the order
	 *       was given, in milliseconds since the epoch.</li>
	 * </ul>
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
	private String productUnit;
	
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
	private Map<String, Integer> requiredResources;
	
	/**
	 * The number of seconds required to produce one unit of the product.
	 */
	private int productionTimePerItem;
	
	/**
	 * Creates a new ProducerBehaviour.
	 * @param questSlot the slot that is used to store the status 
	 * @param productionActivity the name of the activity, e.g. "build",
	 *        "forge", "bake"
	 * @param productUnit the unit in which the product is counted, e.g.
	 *                    "bags", "pieces", "pounds"
	 * @param productName the name of the product, e.g. "plate_armor". It
	 *                    must be a valid item name.
	 * @param requiredResources a mapping which maps the name of each required
	 *                          resource (e.g. "iron_ore") to the amount of
	 *                          this resource that is required for one
	 *                          unit of the product.
	 * @param productionTimePerItem the number of seconds required to produce
	 *                              one unit of the product.
	 */
	public ProducerBehaviour(String questSlot, String productionActivity,
					String productUnit, String productName,
					Map<String, Integer> requiredResources,
					int productionTimePerItem) {
		this.questSlot = questSlot;
		this.productionActivity = productionActivity;
		this.productUnit = productUnit;
		this.productName = productName;
		this.requiredResources = requiredResources;
		this.productionTimePerItem = productionTimePerItem;
	}
	
	protected Map<String, Integer> getRequiredResources() {
		return requiredResources;
	}
	
	protected String getProductionActivity() {
		return productionActivity;
	}
	
	protected String getProductUnit() {
		return productUnit;
	}

	protected String getProductName() {
		return productName;
	}
	
	protected int getProductionTimePerItem() {
		return productionTimePerItem;
	}
	
	/**
	 * Gets a nicely formulated string that describes the amounts and names
	 * of the resources that are required to produce one unit of the product,
	 * with hashes before the resource names in order to highlight them,
	 * e.g. "4 #wood, 1 #iron, and 2 #leather". 
	 * @return A string describing the required resources.
	 */
	private String getRequiredResourceNamesWithHashes() {
		Set<String> requiredResourcesWithHashes = new HashSet<String>();
		for (Map.Entry<String, Integer> entry: getRequiredResources().entrySet()) {
			requiredResourcesWithHashes.add(entry.getValue() + " #" + entry.getKey());	
		}
		return SpeakerNPC.enumerateCollection(requiredResourcesWithHashes);
	}
	
	/**
	 * 
	 * @param player
	 * @param npc
	 * @param maxAmount
	 */
	public void giveResources(Player player, SpeakerNPC npc, int maxAmount) {
		int numberOfProductItems = maxAmount;
		for (Map.Entry<String, Integer> entry: getRequiredResources().entrySet()) {
			int limitationByThisResource = player.getNumberOfEquipped(entry.getKey()) / entry.getValue();
			numberOfProductItems = Math.min(numberOfProductItems, limitationByThisResource);
		}
		if (numberOfProductItems == 0) {
			npc.say("I can only "
					+ getProductionActivity()
					+ " "
					+ getProductName()
					+ " if you bring me at least "
					+ getRequiredResourceNamesWithHashes()
					+ ".");
		} else {
			Set<String> droppedResources = new HashSet<String>();
			for (Map.Entry<String, Integer> entry: getRequiredResources().entrySet()) {
				int amountToDrop = numberOfProductItems * entry.getValue();
				player.drop(entry.getKey(), amountToDrop);
				droppedResources.add(amountToDrop + " " + entry.getKey());
			}
			long timeNow = new Date().getTime();
			player.setQuest(questSlot, numberOfProductItems + ";" + timeNow);
			npc.say("OK, I will "
					+ getProductionActivity()
					+ " "
					+ numberOfProductItems
					+ " "
					+ getProductName()
					+ " from the "
					+ SpeakerNPC.enumerateCollection(droppedResources)
					+ " that you gave me, but that will take some time. Come back later.");
		}
	}
	
	/**
	 * This method is called when the player returns to pick up the finished
	 * product. It checks if the NPC is already done with the order. If that
	 * is the case, the player is given the product. Otherwise, the NPC
	 * asks the player to come back later. 
	 * @param player The player who wants to fetch the product
	 * @param npc The producing NPC
	 */
	public void fetchProduct(Player player, SpeakerNPC npc) {
		String orderString = player.getQuest(questSlot);
		String[] order = orderString.split(";");
		int numberOfProductItems = Integer.parseInt(order[0]);
		long orderTime = Long.parseLong(order[1]);
		long timeNow = new Date().getTime();
		if (timeNow - orderTime < numberOfProductItems
				* getProductionTimePerItem() * 1000) {
			npc.say("Welcome back! I'm still busy with your order to "
					+ getProductionActivity()
					+ " "
					+ getProductName()
					+ " for you. Come back later to get it.");
		} else {
			StackableItem products = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(getProductName());            
			products.setQuantity(numberOfProductItems);
			player.equip(products, true);
			npc.say("Welcome back! I'm done with your order. Here you have "
					+ numberOfProductItems
					+ " "
					+ getProductUnit()
					+ " of "
					+ getProductName()
					+ ".");
			player.setQuest(questSlot, "done");
		}
	}
}
