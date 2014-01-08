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
package games.stendhal.server.entity.npc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Singleton class that contains inventory and prices of NPC stores.
 */
public final class ShopList {

	static {
		final ShopList shops = ShopList.get();

		shops.add("food&drinks", "beer", 10);
		shops.add("food&drinks", "wine", 15);
		shops.add("food&drinks", "flask", 5);
		shops.add("food&drinks", "cheese", 20);
		shops.add("food&drinks", "apple", 10);
		shops.add("food&drinks", "carrot", 10);
		shops.add("food&drinks", "meat", 40);
		shops.add("food&drinks", "ham", 80);

		shops.add("adosfoodseller", "apple", 50);
		shops.add("adosfoodseller", "carrot", 50);

		shops.add("buyfood", "cheese", 5);
		shops.add("buyfood", "meat", 10);
		shops.add("buyfood", "spinach", 15);
		shops.add("buyfood", "ham", 20);
		shops.add("buyfood", "flour", 25);
		shops.add("buyfood", "porcini", 30);

		shops.add("healing", "antidote", 50);
		shops.add("healing", "minor potion", 100);
		shops.add("healing", "potion", 250);
		shops.add("healing", "greater potion", 500);

		shops.add("superhealing", "antidote", 50);
		shops.add("superhealing", "greater antidote", 100);
		shops.add("superhealing", "potion", 250);
		shops.add("superhealing", "greater potion", 500);
		shops.add("superhealing", "mega potion", 1500);

		shops.add("scrolls", "home scroll", 250);
		shops.add("scrolls", "summon scroll", 200);
		shops.add("scrolls", "empty scroll", 2000);

		shops.add("fadoscrolls", "fado city scroll", 600);
		shops.add("fadoscrolls", "empty scroll", 2200);

		shops.add("nalworscrolls", "nalwor city scroll", 400);
		shops.add("nalworscrolls", "empty scroll", 2000);

		shops.add("adosscrolls", "ados city scroll", 400);
		shops.add("adosscrolls", "empty scroll", 2000);

		shops.add("kirdnehscrolls", "kirdneh city scroll", 400);
		shops.add("kirdnehscrolls", "home scroll", 400);
		shops.add("kirdnehscrolls", "empty scroll", 2000);

		shops.add("allscrolls", "home scroll", 250);
		shops.add("allscrolls", "summon scroll", 200);
		shops.add("allscrolls", "empty scroll", 2000);
		shops.add("allscrolls", "ados city scroll", 400);
		shops.add("allscrolls", "nalwor city scroll", 400);
		shops.add("allscrolls", "fado city scroll", 600);
		shops.add("allscrolls", "kirdneh city scroll", 600);

		shops.add("sellstuff", "knife", 15);
		shops.add("sellstuff", "club", 10);
		shops.add("sellstuff", "dagger", 25);
		shops.add("sellstuff", "wooden shield", 25);
		shops.add("sellstuff", "dress", 25);
		shops.add("sellstuff", "leather helmet", 25);
		shops.add("sellstuff", "cloak", 30);
		shops.add("sellstuff", "leather legs", 35);

		shops.add("sellbetterstuff1", "blue armor", 14000);
		shops.add("sellbetterstuff1", "blue boots", 3000);
		shops.add("sellbetterstuff1", "blue striped cloak", 5000);
		shops.add("sellbetterstuff1", "blue helmet", 6000);
		shops.add("sellbetterstuff1", "blue legs", 6000);
		shops.add("sellbetterstuff1", "blue shield", 20000);
		shops.add("sellbetterstuff1", "assassin dagger", 12000);

		shops.add("sellbetterstuff2", "shadow armor", 18000);
		shops.add("sellbetterstuff2", "shadow boots", 4000);
		shops.add("sellbetterstuff2", "shadow cloak", 7000);
		shops.add("sellbetterstuff2", "shadow helmet", 8000);
		shops.add("sellbetterstuff2", "shadow legs", 10000);
		shops.add("sellbetterstuff2", "shadow shield", 30000);
		shops.add("sellbetterstuff2", "hell dagger", 20000);

		shops.add("sellrangedstuff", "wooden bow", 300);
		shops.add("sellrangedstuff", "wooden arrow", 2);

		shops.add("buystuff", "short sword", 15);
		shops.add("buystuff", "sword", 60);
		shops.add("buystuff", "studded shield", 20);
		shops.add("buystuff", "studded armor", 22);
		shops.add("buystuff", "studded helmet", 17);
		shops.add("buystuff", "studded legs", 20);
		shops.add("buystuff", "chain armor", 29);
		shops.add("buystuff", "chain helmet", 25);
		shops.add("buystuff", "chain legs", 27);

		shops.add("selltools", "small axe", 15);
		shops.add("selltools", "hand axe", 25);
		shops.add("selltools", "axe", 40);
		// enable these if you need them for a quest or something
		// shops.add("selltools", "pick", 50);
		// shops.add("selltools", "shovel", 50);
		shops.add("selltools", "hammer", 60);
		// used for harvest grain.
		shops.add("selltools", "old scythe", 120);
        // for harvesting cane fields
		shops.add("selltools", "sickle", 80);
		shops.add("selltools", "gold pan", 230);

		shops.add("buyiron", "iron", 75);

		shops.add("buygrain", "grain", 1);

		shops.add("sellrings", "engagement ring", 5000);
		// gold and gemstones
		shops.add("buyprecious", "gold bar", 250);
		shops.add("buyprecious", "emerald", 200);
		shops.add("buyprecious", "sapphire", 400);
		shops.add("buyprecious", "carbuncle", 600);
		shops.add("buyprecious", "diamond", 800);
		shops.add("buyprecious", "obsidian", 1000);
		shops.add("buyprecious", "mithril bar", 2500);

		// rare weapons shop
		shops.add("buyrare", "scimitar", 65);
		shops.add("buyrare", "katana", 70);
		shops.add("buyrare", "bardiche", 75);
		shops.add("buyrare", "golden hammer", 80);

		// rare armor shop
		shops.add("buyrare", "enhanced chainmail", 32);
		shops.add("buyrare", "golden chainmail", 52);
		shops.add("buyrare", "plate armor", 62);
		shops.add("buyrare", "plate shield", 40);
		shops.add("buyrare", "lion shield", 50);

		// rare elf weapons buyer
		shops.add("elfbuyrare", "battle axe", 70);
		shops.add("elfbuyrare", "twoside axe", 80);
		shops.add("elfbuyrare", "claymore", 90);
		shops.add("elfbuyrare", "broadsword", 100);
		shops.add("elfbuyrare", "staff", 75);
		shops.add("elfbuyrare", "enhanced lion shield", 100);
		shops.add("elfbuyrare", "crown shield", 120);

		// more rare weapons shop (fado)
		shops.add("buyrare2", "war hammer", 120);
		shops.add("buyrare2", "biting sword", 150);
		shops.add("buyrare2", "crossbow", 175);
		shops.add("buyrare2", "great sword", 250);
		shops.add("buyrare2", "fire sword", 2000);
		shops.add("buyrare2", "ice sword", 5000);
        shops.add("buyrare2", "hell dagger", 8000);

		// very rare armor shop (ados)
		shops.add("buyrare3", "golden legs", 3000);
		shops.add("buyrare3", "shadow legs", 5000);
		shops.add("buyrare3", "golden armor", 7000);
		shops.add("buyrare3", "shadow armor", 9000);
		shops.add("buyrare3", "golden shield", 10000);
		shops.add("buyrare3", "shadow shield", 15000);

		// less rare armor shop (kobold city - kobolds drop some of these
		// things)
		shops.add("buystuff2", "leather scale armor", 65);
		shops.add("buystuff2", "studded legs", 70);
		shops.add("buystuff2", "studded boots", 75);
		shops.add("buystuff2", "chain boots", 100);
		shops.add("buystuff2", "skull shield", 100);
		shops.add("buystuff2", "unicorn shield", 125);
		shops.add("buystuff2", "viking helmet", 250);

		shops.add("sellstuff2", "leather boots", 50);
		shops.add("sellstuff2", "studded helmet", 60);
		shops.add("sellstuff2", "studded shield", 80);
		shops.add("sellstuff2", "sword", 90);
		shops.add("sellstuff2", "dwarf cloak", 230);

		// cloaks shop
		shops.add("buycloaks", "blue elf cloak", 300);
		shops.add("buycloaks", "green dragon cloak", 400);
		shops.add("buycloaks", "blue dragon cloak", 2000);
		shops.add("buycloaks", "shadow cloak", 3000);
		shops.add("buycloaks", "black dragon cloak", 4000);
		shops.add("buycloaks", "golden cloak", 5000);
		shops.add("buycloaks", "chaos cloak", 10000);
		shops.add("buycloaks", "black cloak", 20000);

		// boots shop (mithrilbourgh)
		// Note the shop sign is done by hand in
		// games.stendhal.server.maps.mithrilbourgh.stores
		// Because I wanted to split boots and helmets
		// Please if you change anything, change also the sign (by hand)
		shops.add("boots&helm", "steel boots", 1000);
		shops.add("boots&helm", "golden boots", 1500);
		shops.add("boots&helm", "shadow boots", 2000);
		shops.add("boots&helm", "stone boots", 2500);
		shops.add("boots&helm", "chaos boots", 4000);
        shops.add("boots&helm", "green thing boots", 6000);
        shops.add("boots&helm", "xeno boots", 8000);
        shops.add("boots&helm", "xeno helmet", 8000);

		// helmet shop (mithrilbourgh)
		// Note the shop sign is done by hand in
		// games.stendhal.server.maps.mithrilbourgh.stores
		shops.add("boots&helm", "golden helmet", 3000);
		shops.add("boots&helm", "shadow helmet", 4000);
		shops.add("boots&helm", "horned golden helmet", 5000);
		shops.add("boots&helm", "chaos helmet", 6000);
		shops.add("boots&helm", "magic chain helmet", 8000);
		shops.add("boots&helm", "black helmet", 10000);

		// buy axes (woodcutter)
		shops.add("buyaxe", "halberd", 2000);
		shops.add("buyaxe", "golden twoside axe", 4000);
		shops.add("buyaxe", "magic twoside axe", 6000);
		shops.add("buyaxe", "durin axe", 8000);
		shops.add("buyaxe", "black scythe", 9000);
		shops.add("buyaxe", "chaos axe", 10000);
		shops.add("buyaxe", "black halberd", 12000);

		// buy chaos items (scared dwarf, after quest)
		shops.add("buychaos", "chaos legs", 8000);
		shops.add("buychaos", "chaos sword", 12000);
		shops.add("buychaos", "chaos shield", 18000);
		shops.add("buychaos", "chaos armor", 20000);

		// buy elvish items (albino elf, after quest)
		shops.add("buyelvish", "elvish boots", 300);
		shops.add("buyelvish", "elvish legs", 300);
		shops.add("buyelvish", "elvish sword", 800);
		shops.add("buyelvish", "elvish shield", 1000);
		shops.add("buyelvish", "drow sword", 1200);
		shops.add("buyelvish", "elvish cloak", 400);
		shops.add("buyelvish", "elvish armor", 400);

		// magic items or 'relics' (witch in magic city)
		shops.add("buymagic", "demon sword", 4000);
		shops.add("buymagic", "dark dagger", 8000);
		shops.add("buymagic", "liberty helmet", 8000);
		shops.add("buymagic", "immortal sword", 10000);
		shops.add("buymagic", "jewelled legs", 12000);
		shops.add("buymagic", "magic plate shield", 16000);
		shops.add("buymagic", "magic plate armor", 20000);

		// red items (supplier in sedah city)
		shops.add("buyred", "red armor", 300);
		shops.add("buyred", "red boots", 200);
		shops.add("buyred", "red cloak", 250);
		shops.add("buyred", "red helmet", 200);
		shops.add("buyred", "red legs", 200);
		shops.add("buyred", "red shield", 750);

		// mainio items (despot in mithrilbourgh throne room)
		shops.add("buymainio", "mainio armor", 22000);
		shops.add("buymainio", "mainio boots", 4000);
		shops.add("buymainio", "mainio cloak", 12000);
		shops.add("buymainio", "mainio helmet", 8000);
		shops.add("buymainio", "mainio legs", 7000);
		shops.add("buymainio", "mainio shield", 16000);

		// assassinhq principal Femme Fatale)
		shops.add("buy4assassins", "buckler", 20);
		shops.add("buy4assassins", "aventail", 25);
		shops.add("buy4assassins", "robins hat", 30);
		shops.add("buy4assassins", "leather boots", 30);
		shops.add("buy4assassins", "dwarf cloak", 60);
		shops.add("buy4assassins", "dwarvish armor", 17000);
		shops.add("buy4assassins", "dwarvish legs", 15000);
		shops.add("buy4assassins", "assassin dagger", 7000);

		// mountain dwarf buyer of odds and ends -3 ados abandoned keep)
		shops.add("buyoddsandends", "shuriken", 20);
		shops.add("buyoddsandends", "amulet", 800);
		shops.add("buyoddsandends", "black pearl", 100);
		shops.add("buyoddsandends", "lucky charm", 60);
		shops.add("buyoddsandends", "knife", 5);
		shops.add("buyoddsandends", "dagger", 20);
		shops.add("buyoddsandends", "skull ring", 250);
		shops.add("buyoddsandends", "greater antidote", 80);
		shops.add("buyoddsandends", "marbles", 80);
		shops.add("buyoddsandends", "magical needle", 1500);
		shops.add("buyoddsandends", "snowglobe", 150);
		shops.add("buyoddsandends", "silk gland", 500);

		// archery shop in nalwor)
		shops.add("buyarcherstuff", "wooden arrow", 1);
		shops.add("buyarcherstuff", "steel arrow", 5);
		shops.add("buyarcherstuff", "golden arrow", 10);
		shops.add("buyarcherstuff", "power arrow", 35);
		shops.add("buyarcherstuff", "wooden bow", 250);
		shops.add("buyarcherstuff", "crossbow", 400);
		shops.add("buyarcherstuff", "longbow", 300);
		shops.add("buyarcherstuff", "composite bow", 350);
		shops.add("buyarcherstuff", "hunter crossbow", 800);
		shops.add("buyarcherstuff", "mithril bow", 2000);

		// selling arrows
		shops.add("sellarrows", "wooden arrow", 2);
		shops.add("sellarrows", "steel arrow", 7);
		shops.add("sellarrows", "golden arrow", 25);
		shops.add("sellarrows", "power arrow", 45);

		// assassinhq chief falatheen the dishwasher and veggie buyer)
		// sign is hard coded so if you change this change the sign
		shops.add("buyveggiesandherbs", "carrot", 5);
		shops.add("buyveggiesandherbs", "salad", 10);
		shops.add("buyveggiesandherbs", "leek", 25);
		shops.add("buyveggiesandherbs", "broccoli", 30);
		shops.add("buyveggiesandherbs", "courgette", 10);
		shops.add("buyveggiesandherbs", "cauliflower", 30);
		shops.add("buyveggiesandherbs", "tomato", 20);
		shops.add("buyveggiesandherbs", "onion", 20);
		shops.add("buyveggiesandherbs", "arandula", 10);
		shops.add("buyveggiesandherbs", "kokuda", 200);
		shops.add("buyveggiesandherbs", "kekik", 25);
		shops.add("buyveggiesandherbs", "sclaria", 25);

		// gnome village buyer in 0 ados mountain n2 w2)
		shops.add("buy4gnomes", "leather armor", 25);
		shops.add("buy4gnomes", "club", 3);
		shops.add("buy4gnomes", "leather helmet", 15);
		shops.add("buy4gnomes", "cloak", 25);
		shops.add("buy4gnomes", "apple", 5);
		shops.add("buy4gnomes", "marbles", 50);
		shops.add("buy4gnomes", "wooden shield", 20);

		// hotdog lady in athor)
		shops.add("buy4hotdogs", "sausage", 30);
		shops.add("buy4hotdogs", "cheese sausage", 25);
		shops.add("buy4hotdogs", "bread", 15);
		shops.add("buy4hotdogs", "onion", 20);
		shops.add("buy4hotdogs", "canned tuna", 15);
		shops.add("buy4hotdogs", "ham", 15);
		shops.add("buy4hotdogs", "cheese", 5);

		shops.add("sellhotdogs", "hotdog", 160);
		shops.add("sellhotdogs", "cheeseydog", 180);
		shops.add("sellhotdogs", "tuna sandwich", 130);
		shops.add("sellhotdogs", "sandwich", 120);
		shops.add("sellhotdogs", "vanilla shake", 110);
		shops.add("sellhotdogs", "chocolate shake", 110);
		shops.add("sellhotdogs", "chocolate bar", 100);
		shops.add("sellhotdogs", "snowglobe", 200);

		// magic city barmaid)
		shops.add("sellmagic", "hotdog", 160);
		shops.add("sellmagic", "cheeseydog", 180);
		shops.add("sellmagic", "tuna sandwich", 130);
		shops.add("sellmagic", "sandwich", 120);
		shops.add("sellmagic", "vanilla shake", 110);
		shops.add("sellmagic", "chocolate shake", 110);
		shops.add("sellmagic", "chocolate bar", 100);
		shops.add("sellmagic", "licorice", 100);

		// kirdneh city armor)
		shops.add("buykirdneharmor", "blue armor", 13000);
		shops.add("buykirdneharmor", "stone armor", 18000);
		shops.add("buykirdneharmor", "ice armor", 19000);
		shops.add("buykirdneharmor", "xeno armor", 21000);
		shops.add("buykirdneharmor", "barbarian armor", 5000);
		shops.add("buykirdneharmor", "green dragon shield", 13000);
		shops.add("buykirdneharmor", "xeno shield", 20000);


		// amazon cloaks shop
		shops.add("buyamazoncloaks", "vampire cloak", 14000);
		shops.add("buyamazoncloaks", "xeno cloak", 18000);
		shops.add("buyamazoncloaks", "elf cloak", 50);
		shops.add("buyamazoncloaks", "lich cloak", 10000);
		shops.add("buyamazoncloaks", "stone cloak", 350);
		shops.add("buyamazoncloaks", "blue striped cloak", 280);
		shops.add("buyamazoncloaks", "red dragon cloak", 4000);
		shops.add("buyamazoncloaks", "bone dragon cloak", 1500);

		// kirdneh city fishy market)
		shops.add("buyfishes", "perch", 22);
		shops.add("buyfishes", "mackerel", 20);
		shops.add("buyfishes", "roach", 10);
		shops.add("buyfishes", "char", 30);
		shops.add("buyfishes", "clownfish", 30);
		shops.add("buyfishes", "surgeonfish", 15);
		shops.add("buyfishes", "trout", 45);
		shops.add("buyfishes", "cod", 10);

		// semos trading - swords)
		shops.add("tradeswords", "dagger", 10);

		// party time! For maria for example. Bit more expensive than normal
		shops.add("sellparty", "pina colada", 100);
		shops.add("sellparty", "chocolate bar", 100);
		shops.add("sellparty", "beer", 10);
		shops.add("sellparty", "wine", 15);
		shops.add("sellparty", "vanilla shake", 150);
		shops.add("sellparty", "icecream", 50);
		shops.add("sellparty", "hotdog", 180);
		shops.add("sellparty", "sandwich", 140);


		// black items (balduin, when ultimate collector quest completed)
		shops.add("buyblack", "black armor", 60000);
		shops.add("buyblack", "black boots", 10000);
		shops.add("buyblack", "black cloak", 20000);
		shops.add("buyblack", "black helmet", 15000);
		shops.add("buyblack", "black legs", 40000);
		shops.add("buyblack", "black shield", 75000);
		shops.add("buyblack", "black sword", 20000);
		shops.add("buyblack", "black scythe", 40000);
		shops.add("buyblack", "black halberd", 30000);

		// ados market
		shops.add("buyadosarmors", "blue shield", 900);

		// Athor ferry
		shops.add("buypoisons", "poison", 40);
		shops.add("buypoisons", "toadstool", 60);
		shops.add("buypoisons", "greater poison", 60);
		shops.add("buypoisons", "red lionfish", 50);
		shops.add("buypoisons", "deadly poison", 100);
		shops.add("buypoisons", "mega poison", 500);
		shops.add("buypoisons", "disease poison", 2000);

		// Should have its own shop (buytraps)
		shops.add("buypoisons", "rodent trap", 50);
		
		// Mine Town Revival Weeks Caroline
		shops.add("sellrevivalweeks", "cherry pie", 195);
		shops.add("sellrevivalweeks", "apple pie", 195);
		shops.add("sellrevivalweeks", "vanilla shake", 120);
		shops.add("sellrevivalweeks", "chocolate shake", 120);
		shops.add("sellrevivalweeks", "icecream", 60);
		shops.add("sellrevivalweeks", "chocolate bar", 100);
		shops.add("sellrevivalweeks", "grilled steak", 250);
		shops.add("sellrevivalweeks", "hotdog", 170);
		shops.add("sellrevivalweeks", "cheeseydog", 175);
		shops.add("sellrevivalweeks", "tuna sandwich", 140);
		shops.add("sellrevivalweeks", "sandwich", 130);
		shops.add("sellrevivalweeks", "wine", 25);
		shops.add("sellrevivalweeks", "beer", 20);
		shops.add("sellrevivalweeks", "water", 15);

		// for ados botanical gardens or if you like, other cafes. 
		// expensive prices to make sure that the npc production of these items isn't compromised
		shops.add("cafe", "tea", 80);
		shops.add("cafe", "water", 50);
		shops.add("cafe", "chocolate shake", 150);
		shops.add("cafe", "sandwich", 170);
		shops.add("cafe", "tuna sandwich", 180);
		shops.add("cafe", "apple pie", 250);
		
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

	private final Map<String, Map<String, Integer>> contents;

	private ShopList() {
		contents = new HashMap<String, Map<String, Integer>>();
	}

	/**
	 * gets the items offered by a shop with their prices
	 *
	 * @param name name of shop
	 * @return items and prices
	 */
	public Map<String, Integer> get(final String name) {
		return contents.get(name);
	}

	/**
	 * gets a set of all shops
	 *
	 * @return set of shops
	 */
	public Set<String> getShops() {
		return contents.keySet();
	}

	/**
	 * converts a shop into a human readable form
	 *
	 * @param name   name of shop
	 * @param header prefix
	 * @return human readable description
	 */
	public String toString(final String name, final String header) {
		final Map<String, Integer> items = contents.get(name);

		final StringBuilder sb = new StringBuilder(header + "\n");

		for (final Entry<String, Integer> entry : items.entrySet()) {
			sb.append(entry.getKey() + " \t" + entry.getValue() + "\n");
		}

		return sb.toString();
	}

	/**
	 * Add an item to a shop
	 *
	 * @param name the shop name
	 * @param item the item to add
	 * @param price the price for the item
	 */
	public void add(final String name, final String item, final int price) {
		Map<String, Integer> shop;

		if (contents.containsKey(name)) {
			shop = contents.get(name);
		} else {
			shop = new LinkedHashMap<String, Integer>();
			contents.put(name, shop);
		}

		shop.put(item, Integer.valueOf(price));
	}
}
