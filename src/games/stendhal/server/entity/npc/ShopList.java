package games.stendhal.server.entity.npc;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Singleton class that contains inventory and prices of NPC stores.
 *
 */
public class ShopList {
	static {
		ShopList shops = get();

		shops.add("food&drinks", "beer", 10);
		shops.add("food&drinks", "wine", 15);
		shops.add("food&drinks", "flask", 5);
		shops.add("food&drinks", "cheese", 20);
		shops.add("food&drinks", "apple", 10);
		shops.add("food&drinks", "carrot", 10);
		shops.add("food&drinks", "meat", 40);
		shops.add("food&drinks", "ham", 80);

		shops.add("healing", "antidote", 50);
		shops.add("healing", "minor_potion", 100);
		shops.add("healing", "potion", 250);
		shops.add("healing", "greater_potion", 500);

		shops.add("scrolls", "home_scroll", 250);
		shops.add("scrolls", "summon_scroll", 200);
		shops.add("scrolls", "empty_scroll", 1200);

		shops.add("sellstuff", "knife", 15);
		shops.add("sellstuff", "club", 10);
		shops.add("sellstuff", "dagger", 25);
		shops.add("sellstuff", "wooden_shield", 25);
		shops.add("sellstuff", "dress", 25);
		shops.add("sellstuff", "leather_helmet", 25);
		shops.add("sellstuff", "cloak", 30);
		shops.add("sellstuff", "leather_legs", 35);

		shops.add("sellrangedstuff", "wooden_bow", 300);
		shops.add("sellrangedstuff", "wooden_arrow", 2);

		shops.add("buystuff", "short_sword", 15);
		shops.add("buystuff", "sword", 60);
		shops.add("buystuff", "studded_shield", 20);
		shops.add("buystuff", "studded_armor", 22);
		shops.add("buystuff", "studded_helmet", 17);
		shops.add("buystuff", "studded_legs", 20);
		shops.add("buystuff", "chain_armor", 29);
		shops.add("buystuff", "chain_helmet", 25);
		shops.add("buystuff", "chain_legs", 27);
		
		shops.add("selltools", "small_axe", 15);
		shops.add("selltools", "hand_axe", 25);
		shops.add("selltools", "axe", 40);
		// enable these if you need them for a quest or something
		// shops.add("selltools", "pick", 50);
		// shops.add("selltools", "shovel", 50);
		shops.add("selltools", "hammer", 60);
		// used for harvest grain.
		shops.add("selltools", "old_scythe", 120);
		shops.add("selltools", "gold_pan", 230);

		shops.add("buyiron", "iron", 75);
		
		shops.add("buygrain", "grain", 1);

		// rare weapons shop
		shops.add("buyrare", "scimitar", 65);
		shops.add("buyrare", "katana", 70);
		shops.add("buyrare", "bardiche", 75);
		shops.add("buyrare", "hammer_+3", 80);

		// rare armor shop
		shops.add("buyrare", "chain_armor_+1", 32);
		shops.add("buyrare", "chain_armor_+2", 42);
		shops.add("buyrare", "chain_armor_+3", 52);
		shops.add("buyrare", "plate_armor", 62);
		shops.add("buyrare", "plate_shield", 40);
		shops.add("buyrare", "lion_shield", 50);

		//rare elf weapons buyer
		shops.add("elfbuyrare", "battle_axe", 50);
		shops.add("elfbuyrare", "twoside_axe", 60);
		shops.add("elfbuyrare", "claymore", 90);
		shops.add("elfbuyrare", "broadsword", 70);
		shops.add("elfbuyrare", "staff", 75);
		shops.add("elfbuyrare", "lion_shield_+1", 52);
		shops.add("elfbuyrare", "crown_shield", 80);


	}

	static private ShopList instance;

	/**
	 * Returns the Singleton instance.
	 * @return The instance
	 */
	static public ShopList get() {
		if (instance == null) {
			instance = new ShopList();
		}
		return instance;
	}

	private Map<String, Map<String, Integer>> contents;

	private ShopList() {
		contents = new HashMap<String, Map<String, Integer>>();
	}

	public Map<String, Integer> get(String name) {
		return contents.get(name);
	}

	public String toString(String name, String header) {
		Map<String, Integer> items = contents.get(name);

		StringBuffer sb = new StringBuffer(header + "\n");

		for (String item : items.keySet()) {
			sb.append(item + " \t" + items.get(item) + "\n");
		}

		return sb.toString();
	}

	public void add(String name, String item, int price) {
		Map<String, Integer> shop;

		if (!contents.containsKey(name)) {
			shop = new LinkedHashMap<String, Integer>();
			contents.put(name, shop);
		} else {
			shop = contents.get(name);
		}

		shop.put(item, price);
	}
}
