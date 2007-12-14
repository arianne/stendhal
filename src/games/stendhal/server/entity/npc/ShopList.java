package games.stendhal.server.entity.npc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

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

		shops.add("buyfood", "cheese", 5);
		shops.add("buyfood", "meat", 10);
		shops.add("buyfood", "spinach", 15);
		shops.add("buyfood", "ham", 20);
		shops.add("buyfood", "flour", 25);
		shops.add("buyfood", "porcini", 30);

		shops.add("healing", "antidote", 50);
		shops.add("healing", "minor_potion", 100);
		shops.add("healing", "potion", 250);
		shops.add("healing", "greater_potion", 500);

		shops.add("superhealing", "antidote", 50);
		shops.add("superhealing", "greater_antidote", 100);
		shops.add("superhealing", "potion", 250);
		shops.add("superhealing", "greater_potion", 500);
		// shops.add("superhealing", "mega_potion", 1500); don't want giantheart
		// market starting again

		shops.add("scrolls", "home_scroll", 250);
		shops.add("scrolls", "summon_scroll", 200);
		shops.add("scrolls", "empty_scroll", 2000);

		shops.add("fadoscrolls", "fado_city_scroll", 600);
		shops.add("fadoscrolls", "empty_scroll", 2200);

		shops.add("nalworscrolls", "nalwor_city_scroll", 400);
		shops.add("nalworscrolls", "empty_scroll", 2000);

		shops.add("adosscrolls", "ados_city_scroll", 400);
		shops.add("adosscrolls", "empty_scroll", 2000);

		shops.add("allscrolls", "home_scroll", 250);
		shops.add("allscrolls", "summon_scroll", 200);
		shops.add("allscrolls", "empty_scroll", 2000);
		shops.add("allscrolls", "ados_city_scroll", 400);
		shops.add("allscrolls", "nalwor_city_scroll", 400);
		shops.add("allscrolls", "fado_city_scroll", 600);

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
		shops.add("buyprecious", "diamond", 800);
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

		// rare elf weapons buyer
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

		// very rare armor shop (ados)
		shops.add("buyrare3", "golden_legs", 3000);
		shops.add("buyrare3", "shadow_legs", 5000);
		shops.add("buyrare3", "golden_armor", 7000);
		shops.add("buyrare3", "shadow_armor", 9000);
		shops.add("buyrare3", "golden_shield", 10000);
		shops.add("buyrare3", "shadow_shield", 15000);

		// less rare armor shop (kobold city - kobolds drop some of these
		// things)
		shops.add("buystuff2", "leather_scale_armor", 65);
		shops.add("buystuff2", "studded_legs", 70);
		shops.add("buystuff2", "studded_boots", 75);
		shops.add("buystuff2", "chain_boots", 100);
		shops.add("buystuff2", "skull_shield", 100);
		shops.add("buystuff2", "unicorn_shield", 125);
		shops.add("buystuff2", "viking_helmet", 250);

		shops.add("sellstuff2", "leather_boots", 50);
		shops.add("sellstuff2", "studded_helmet", 60);
		shops.add("sellstuff2", "studded_shield", 80);
		shops.add("sellstuff2", "sword", 90);
		shops.add("sellstuff2", "dwarf_cloak", 230);

		// cloaks shop
		shops.add("buycloaks", "blue_elf_cloak", 300);
		shops.add("buycloaks", "green_dragon_cloak", 400);
		shops.add("buycloaks", "blue_dragon_cloak", 2000);
		shops.add("buycloaks", "shadow_cloak", 3000);
		shops.add("buycloaks", "black_dragon_cloak", 4000);
		shops.add("buycloaks", "golden_cloak", 5000);
		shops.add("buycloaks", "chaos_cloak", 10000);
		shops.add("buycloaks", "black_cloak", 20000);

		// boots shop (mithrilbourgh)
		// Note the shop sign is done by hand in
		// games.stendhal.server.maps.mithrilbourgh.stores
		// Because I wanted to split boots and helmets
		// Please if you change anything, change also the sign (by hand)
		shops.add("boots&helm", "steel_boots", 1000);
		shops.add("boots&helm", "golden_boots", 1500);
		shops.add("boots&helm", "shadow_boots", 2000);
		shops.add("boots&helm", "stone_boots", 2500);
		shops.add("boots&helm", "chaos_boots", 4000);

		// helmet shop (mithrilbourgh)
		// Note the shop sign is done by hand in
		// games.stendhal.server.maps.mithrilbourgh.stores
		shops.add("boots&helm", "golden_helmet", 3000);
		shops.add("boots&helm", "shadow_helmet", 4000);
		shops.add("boots&helm", "horned_golden_helmet", 5000);
		shops.add("boots&helm", "chaos_helmet", 6000);
		shops.add("boots&helm", "magic_chain_helmet", 8000);
		shops.add("boots&helm", "black_helmet", 10000);

		// buy axes (woodcutter)
		shops.add("buyaxe", "halberd", 2000);
		shops.add("buyaxe", "golden_twoside_axe", 4000);
		shops.add("buyaxe", "magic_twoside_axe", 6000);
		shops.add("buyaxe", "durin_axe", 8000);
		shops.add("buyaxe", "black_scythe", 9000);
		shops.add("buyaxe", "chaos_axe", 10000);
		shops.add("buyaxe", "black_halberd", 12000);

		// buy chaos items (scared dwarf, after quest)
		shops.add("buychaos", "chaos_legs", 8000);
		shops.add("buychaos", "chaos_sword", 12000);
		shops.add("buychaos", "chaos_shield", 15000);
		shops.add("buychaos", "chaos_armor", 20000);

		// buy elvish items (albino elf, after quest)
		shops.add("buyelvish", "elvish_boots", 300);
		shops.add("buyelvish", "elvish_legs", 300);
		shops.add("buyelvish", "elvish_sword", 800);
		shops.add("buyelvish", "elvish_shield", 1000);
		shops.add("buyelvish", "drow_sword", 1200);
		shops.add("buyelvish", "elvish_cloak", 400);
		shops.add("buyelvish", "elvish_armor", 400);

		// magic items or 'relics' (witch in magic city)
		shops.add("buymagic", "demon_sword", 4000);
		shops.add("buymagic", "dark_dagger", 6000);
		shops.add("buymagic", "liberty_helmet", 8000);
		shops.add("buymagic", "immortal_sword", 10000);
		shops.add("buymagic", "jewelled_legs", 12000);
		shops.add("buymagic", "magic_plate_shield", 16000);
		shops.add("buymagic", "magic_plate_armor", 20000);

		// red items (supplier in sedah city)
		shops.add("buyred", "red_armor", 300);
		shops.add("buyred", "red_boots", 200);
		shops.add("buyred", "red_cloak", 250);
		shops.add("buyred", "red_helmet", 200);
		shops.add("buyred", "red_legs", 200);
		shops.add("buyred", "red_shield", 750);

	}

	private static ShopList instance;

	/**
	 * Returns the Singleton instance.
	 * 
	 * @return The instance
	 */
	public static ShopList get() {
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

		for (Entry<String, Integer> entry : items.entrySet()) {
			sb.append(entry.getKey() + " \t" + entry.getValue() + "\n");
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
