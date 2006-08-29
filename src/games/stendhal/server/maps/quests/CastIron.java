package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * QUEST: Cast Iron. This is not really a quest, but a service offered
 * by the blacksmith. Because casting iron takes time, we abuse the
 * player's quest slot to store the time and amount of the player's order.
 * 
 * PARTICIPANTS:
 * - Xoderos, the blacksmith in Semos
 * 
 * STEPS:
 * - You bring wood and iron ore to Xoderos.
 * - You ask Xoderos to cast it for you.
 * - Xoderos starts to cast.
 * - You come back later and get the cast iron.
 * 
 * REWARD:
 * - none (except for the iron)
 * 
 * REPETITIONS:
 * - As much as you want.
 */
public class CastIron extends AbstractQuest {
	
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
	private static final String QUEST_SLOT = "cast_iron";

	/**
	 * The time it takes Xoderos to cast one piece of iron.  
	 */
	private static final int SECONDS_PER_IRON = 5 * 60; // 5 minutes
	
	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	protected Map<String, Integer> getRequiredResources() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("wood", new Integer(1));
		result.put("iron_ore", new Integer(1));
		return result;
	}
	
	protected String getProductionActivity() {
		return "cast";
	}
	
	protected String getProductName() {
		return "iron";
	}
	
	protected int getProductionTimePerItem() {
		return SECONDS_PER_IRON;
	}
	
	private String getRequiredResourceNamesWithHashes() {
		Set<String> requiredResources = getRequiredResources().keySet();
		Set<String> requiredResourcesWithHashes = new HashSet<String>();
		for (String resource: requiredResources) {
			requiredResourcesWithHashes.add("#" + resource);	
		}
		return SpeakerNPC.enumerateCollection(requiredResourcesWithHashes);
	}
	
	protected void giveResources(Player player, SpeakerNPC npc) {
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
			player.setQuest(QUEST_SLOT, numberOfProductItems + ";" + timeNow);
			npc.say("OK, but that will take some time. Come back later.");
		}
	}
	
	protected void takeProduct(Player player, SpeakerNPC npc) {
		String orderString = player.getQuest(QUEST_SLOT);
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
					+ " bars of "
					+ getProductName()
					+ ".");
			player.setQuest(QUEST_SLOT, "done");
		}
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		SpeakerNPC xoderos = npcs.get("Xoderos");
		
		xoderos.add(ConversationStates.IDLE,
					SpeakerNPC.GREETING_MESSAGES,
					new SpeakerNPC.ChatCondition() {
						@Override
						public boolean fire(Player player, SpeakerNPC engine) {
							return !player.hasQuest(QUEST_SLOT)
									|| player.isQuestCompleted(QUEST_SLOT);
						}
					},
					ConversationStates.ATTENDING,
					"Greetings. I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you. I can also #offer you tools.",
					null);

		xoderos.add(ConversationStates.ATTENDING,
				"cast",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return !player.hasQuest(QUEST_SLOT)
								|| player.isQuestCompleted(QUEST_SLOT);
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC npc) {
						giveResources(player, npc);
					}
				});

		xoderos.add(ConversationStates.ATTENDING,
				"cast",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest(QUEST_SLOT)
								&& !player.isQuestCompleted(QUEST_SLOT);
					}
				}, 
				ConversationStates.ATTENDING,
				"I still haven't finished your last order. Come back later!",
				null);

		xoderos.add(ConversationStates.IDLE,
				SpeakerNPC.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, SpeakerNPC engine) {
						return player.hasQuest(QUEST_SLOT)
								&& !player.isQuestCompleted(QUEST_SLOT);
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC npc) {
						takeProduct(player, npc);
					}
				});
	}
}