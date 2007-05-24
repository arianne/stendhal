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

		shops.add("superhealing", "antidote", 50);
		shops.add("superhealing", "greater_antidote", 100);
		shops.add("superhealing", "potion", 250);
		shops.add("superhealing", "greater_potion", 500);
		//	shops.add("superhealing", "mega_potion", 1500); don't want giantheart market starting again

		shops.add("scrolls", "home_scroll", 250);
		shops.add("scrolls", "summon_scroll", 200);
		shops.add("scrolls", "empty_scroll", 1200);

		shops.add("fadoscrolls", "fado_city_scroll", 600);
		shops.add("fadoscrolls", "empty_scroll", 1200);

		shops.add("nalworscrolls", "nalwor_city_scroll", 400);
		shops.add("nalworscrolls", "empty_scroll", 1200);

		shops.add("adosscrolls", "ados_city_scroll", 400);
		shops.add("adosscrolls", "empty_scroll", 1200);

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
		
		shops.add("sellrings", "engagement_ring", 5000);
		// gold and gemstones
		shops.add("buyprecious", "gold_bar", 250);
		shops.add("buyprecious", "emerald", 200);
		shops.add("buyprecious", "sapphire", 400);
		shops.add("buyprecious", "carbuncle", 600);
		shops.add("buyprecious", "obsidian", 1000);

		// rare weapons shop
		shops.add("buyrare", "scimitar", 65);
		shops.add("buyrare", "katana", 70);
		shops.add("buyrare", "bardiche", 75);
		shops.add("buyrare", "golden_hammer", 80);

		// rare armor shop
		shops.add("buyrare", "enhanced_chainmail", 32);
		shops.add("buyrare", "golden_chainmail", 52);
		shops.add("buyrare", "plate_armor", 62);
		shops.add("buyrare", "plate_shield", 40);
		shops.add("buyrare", "lion_shield", 50);

		//rare elf weapons buyer
		shops.add("elfbuyrare", "battle_axe", 70);
		shops.add("elfbuyrare", "twoside_axe", 80);
		shops.add("elfbuyrare", "claymore", 90);
		shops.add("elfbuyrare", "broadsword", 100);
		shops.add("elfbuyrare", "staff", 75);
		shops.add("elfbuyrare", "enhanced_lion_shield", 100);
		shops.add("elfbuyrare", "crown_shield", 120);

		// more rare weapons shop (fado)
		shops.add("buyrare2", "war_hammer", 120);
		shops.add("buyrare2", "biting_sword", 150);
		shops.add("buyrare2", "crossbow", 175);
		shops.add("buyrare2", "great_sword", 250);
		shops.add("buyrare2", "fire_sword", 2000);
		shops.add("buyrare2", "ice_sword", 5000);

		// more rare armor shop (ados?)
		shops.add("buyrare3", "studded_boots", 75);
		shops.add("buyrare3", "chain_boots", 90);
		shops.add("buyrare3", "steel_boots", 1000);
		shops.add("buyrare3", "skull_shield", 100);
		shops.add("buyrare3", "unicorn_shield", 125);
		shops.add("buyrare3", "viking_helmet", 250);
		shops.add("buyrare3", "golden_legs", 3000);
		shops.add("buyrare3", "golden_shield", 5000);

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
