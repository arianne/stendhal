package games.stendhal.server.entity.npc;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.StackableItem;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProducerBehaviour {
	
	/**
	 * This slot can have three states:
	 * <ul>
	 *   <li>unset: if the player has never asked Xoderos to cast iron.</li>  
	 *   <li>done: if the player's last order has been processed.</li>  
	 *   <li>number;time: if the player has given an order and has not
	 *       yet retrieved the iron. number is
	 *       the number of iron that the player will get, and time is the
	 *       time when the order was given.</li>
	 * </ul>
	 */
	private String questSlot;
	
	private String productionActivity;
	
	private String productUnit;
	
	private String productName;
	
	private Map<String, Integer> requiredResources;
	
	private int productionTimePerItem;
	
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
	
	private String getRequiredResourceNamesWithHashes() {
		Set<String> requiredResources = getRequiredResources().keySet();
		Set<String> requiredResourcesWithHashes = new HashSet<String>();
		for (String resource: requiredResources) {
			requiredResourcesWithHashes.add("#" + resource);	
		}
		return SpeakerNPC.enumerateCollection(requiredResourcesWithHashes);
	}
	
	public void giveResources(Player player, SpeakerNPC npc) {
		int numberOfProductItems = Integer.MAX_VALUE;
		for (Map.Entry<String, Integer> entry: getRequiredResources().entrySet()) {
			int limitationByThisResource = player.getNumberOfEquipped(entry.getKey()) / entry.getValue();
			numberOfProductItems = Math.min(numberOfProductItems, limitationByThisResource);
		}
		if (numberOfProductItems == 0) {
			npc.say("I can only "
					+ getProductionActivity()
					+ " "
					+ getProductName()
					+ " if you bring me "
					+ getRequiredResourceNamesWithHashes()
					+ ".");
		} else {
			for (Map.Entry<String, Integer> entry: getRequiredResources().entrySet()) {
				player.drop(entry.getKey(), numberOfProductItems * entry.getValue());
			}
			long timeNow = new Date().getTime();
			player.setQuest(questSlot, numberOfProductItems + ";" + timeNow);
			npc.say("OK, but that will take some time. Come back later.");
		}
	}
	
	public void takeProduct(Player player, SpeakerNPC npc) {
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
