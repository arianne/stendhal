/***************************************************************************
 *                      (C) Copyright 2023 - Stendhal                      *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityinterior.accessoryshop;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.common.constants.SkinColor;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
//~ import games.stendhal.server.entity.npc.OutfitShopsList;
//~ import games.stendhal.server.entity.npc.OutfitShopInventory;
import games.stendhal.server.entity.npc.SpeakerNPC;


/**
 * An NPC that sells special accessories for player outfits that do not expire.
 */
public class DealerNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC gwen = buildNPC();
		buildShop(gwen);

		zone.add(gwen);
	}

	private SpeakerNPC buildNPC() {
		final SpeakerNPC gwen = new SpeakerNPC("Gwen");
		gwen.setOutfit("body=1,head=0,eyes=3,dress=63,hair=56,mask=8");
		gwen.setOutfitColor("skin", SkinColor.LIGHT);
		gwen.setOutfitColor("eyes", 0x921adf);
		gwen.setOutfitColor("dress", 0x169073);
		gwen.setOutfitColor("hair", 0x905816);
		gwen.setOutfitColor("mask", 0xff0000);

		gwen.setDescription("You see Gwen. She deals in fashionable accessories.");
		gwen.addGreeting();
		gwen.addGoodbye();

		gwen.setPathAndPosition(new FixedPath(Arrays.asList(
				new Node(14, 4),
				new Node(14, 7),
				new Node(11, 7),
				new Node(11, 10),
				new Node(6, 10),
				new Node(6, 6),
				new Node(9, 6),
				new Node(9, 4)
			), true));
		gwen.addSuspend(MathHelper.TURNS_IN_ONE_MINUTE / 2, Direction.DOWN, 4);

		return gwen;
	}

	private void buildShop(final SpeakerNPC gwen) {
		/* TODO:
		 * - configure shop
		 * - add shop sign
		 */
		gwen.addOffer("I'm sorry but my shop is not quite set up yet. Come back in the future and I"
				+ " will have some accessories for you.");
		//~ final OutfitShopsList shops = OutfitShopsList.get();
		//~ final OutfitShopInventory inventory = new OutfitShopInventory();
		//~ inventory.put("sword", "detail=sword", 100000);
		//~ inventory.put("knife", "detail=9", 100000);
		//~ inventory.put("spear", "detail=10", 100000);
		//~ inventory.put("bow", "detail=11", 100000);
		//~ inventory.put("axe1", "detail=12", 100000);
		//~ inventory.put("axe2", "detail=13", 100000);
		//~ inventory.put("shield", "detail=14", 100000);
		//~ inventory.put("bone", "detail=15", 100000);
		//~ shops.register("deniran_accessories", inventory);
		//~ shops.configureSeller(gwen, "deniran_accessories", "buy", true, false);
	}
}
