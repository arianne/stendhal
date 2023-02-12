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
import java.util.HashMap;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.common.constants.SkinColor;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.mapstuff.sign.OutfitShopSign;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.shop.OutfitShopInventory;


/**
 * An NPC that sells special accessories for player outfits that do not expire.
 */
public class DealerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		zone.add(buildNPC());
		zone.add(buildSign());
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
		gwen.addOffer("Please see the catalog on the chair for a list of accessories that I offer.");

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

	private OutfitShopSign buildSign() {
		// TODO: move to XML
		final OutfitShopInventory inventory = SingletonRepository.getOutfitShopsList().get("deniran_accessories");
		final Map<String, String> addLayers = new HashMap<>();
		if (inventory != null) {
			for (final String name: inventory.keySet()) {
				addLayers.put(name, "body=0,head=0,eyes=0,dress=11,hair=11");
			}
		}
		final OutfitShopSign sign = new OutfitShopSign("deniran_accessories", "Deniran Accessories",
				"Gwen sells the following accessories", addLayers, true);
		sign.setPosition(12, 2);
		sign.setEntityClass("book_purple");
		return sign;
	}
}
