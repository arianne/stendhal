/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityinterior.dressshop;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.common.constants.SkinColor;
import games.stendhal.common.constants.SoundID;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.sign.ShopSign;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.OutfitChangerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ShowOutfitListEvent;
import games.stendhal.server.events.SoundEvent;
import games.stendhal.server.util.TimeUtil;


public class OutfitLenderNPC implements ZoneConfigurator {

	private static final Logger logger = Logger.getLogger(OutfitLenderNPC.class);

	private SpeakerNPC lender;

	// how long player can wear outfit (10 hours)
	private static final int endurance = 10 * MathHelper.MINUTES_IN_ONE_HOUR;


	private enum OutfitType {
		// set hair to -1 to not be drawn
		BEAR_BLUE("dress=0,hat=993"),
		BEAR_BROWN("dress=0,hat=994"),
		SUPERSTENDHAL("dress=973,hat=992");

		String outfit_str;

		OutfitType(final String outfit) {
			outfit_str = outfit;
		}

		@Override
		public String toString() {
			return outfit_str;
		}
	};


	private class DeniranOutfit {
		private final String label;
		private final OutfitType outfitType;
		private final int price;

		public DeniranOutfit(final String label, final OutfitType outfitType, final int price) {
			this.label = label;
			this.outfitType = outfitType;
			this.price = price;
		}

		public String getLabel() {
			return label;
		}

		public int getPrice() {
			return price;
		}

		public Outfit getOutfit() {
			return new Outfit(outfitType.toString());
		}

		public String getOutfitString() {
			return outfitType.toString();
		}
	}


	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		initNPC(zone);
		initShop(zone);
	}

	private void initNPC(final StendhalRPZone zone) {
		lender = new SpeakerNPC("Pierre");
		lender.setOutfit("body=0,head=0,eyes=9,dress=35,hat=9");
		lender.setOutfitColor("skin", SkinColor.LIGHT);
		lender.setOutfitColor("dress", 0x008080); // teal
		lender.setOutfitColor("hat", Color.BLUE);
		lender.setDescription("You see " + lender.getName() + ", a very fashionable young man.");

		lender.addGreeting();
		lender.addGoodbye();
		final String helpReply = "Please see our catalog on the desk for the outfits that I #rent out.";
		lender.addHelp(helpReply);
		lender.addOffer(helpReply);
		lender.addJob("I run the Deniran Dress Shop. Let me know if you want to #rent an outfit.");

		final List<Node> nodes = new LinkedList<Node>() {{
			add(new Node(9, 2));
			add(new Node(12, 2));
			add(new Node(12, 15));
			add(new Node(26, 15));
			add(new Node(26, 6));
			add(new Node(21, 6));
		}};

		lender.setPathAndPosition(new FixedPath(nodes, true));
		lender.retracePath();
		lender.addSuspend(MathHelper.TURNS_IN_ONE_MINUTE / 4, Direction.DOWN, 0);

		zone.add(lender);
	}

	private void initShop(final StendhalRPZone zone) {
		final List<DeniranOutfit> outfitList = new LinkedList<DeniranOutfit>() {{
			add(new DeniranOutfit("blue bear", OutfitType.BEAR_BLUE, 2500));
			add(new DeniranOutfit("brown bear", OutfitType.BEAR_BROWN, 2500));
			add(new DeniranOutfit("superstendhal", OutfitType.SUPERSTENDHAL, 5000));
		}};

		// TODO: add special outfit during Mine Town
		/*
		if (Occasion.MINETOWN) {

		}
		*/

		final Map<String, Integer> prices = new LinkedHashMap<>();
		for (final DeniranOutfit outfit: outfitList) {
			prices.put(outfit.getLabel(), outfit.getPrice());
		}

		final OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(prices, endurance, "Your outfit has worn off.", true) {
			@Override
			public boolean transactAgreedDeal(final ItemParserResult res, final EventRaiser seller, final Player player) {
				final String outfitName = res.getChosenItemName();
				final int price = getCharge(res, player);

				if (!player.isEquipped("money", price)) {
					seller.say("Sorry, you don't have enough money!");
					return false;
				}

				DeniranOutfit selected = null;
				for (final DeniranOutfit current: outfitList) {
					if (current.getLabel().equals(outfitName)) {
						selected = current;
						break;
					}
				}

				if (selected == null) {
					logger.error("Could not determine outfit");
					return false;
				}

				player.drop("money", price);
				seller.addEvent(new SoundEvent(SoundID.COMMERCE, SoundLayer.CREATURE_NOISE));

				if (resetBeforeChange) {
					// remove temp outfit before changing
					player.returnToOriginalOutfit();
				}
				player.setOutfit(selected.getOutfit().putOver(player.getOutfit()), true);
				player.registerOutfitExpireTime(endurance);

				return true;
			}

			@Override
			public boolean wearsOutfitFromHere(final Player player) {
				final Outfit currentOutfit = player.getOutfit();

				for (final DeniranOutfit possibleOutfit: outfitList) {
					if (possibleOutfit.getOutfit().isPartOf(currentOutfit)) {
						return true;
					}
				}

				return false;
			}
		};

		new OutfitChangerAdder() {
			@Override
			protected String getReturnPhrase() {
				return "This outfit can be worn for " +  TimeUtil.timeUntil(60 * endurance)
				+ ". But you can #return it before it expires if you like.";
			};
		}.addOutfitChanger(lender, behaviour, "rent", false, true);


		// a catalog for players to browse
		final ShopSign catalog = new ShopSign(null, null, null, true) {
			@Override
			public boolean onUsed(final RPEntity user) {
				if (user instanceof Player) {
					final Player player = (Player) user;

					final StringBuilder toSend = new StringBuilder();
					final int outfitCount = outfitList.size();

					for (int idx = 0; idx < outfitCount; idx++) {
						final DeniranOutfit of = outfitList.get(idx);
						toSend.append(of.getLabel() + ";" + of.getOutfitString() + ";" + of.getPrice());
						if (idx < outfitCount - 1) {
							toSend.append(":");
						}
					}

					player.addEvent(new ShowOutfitListEvent("Deniran Dress Shop", lender.getName() + " rents out the following outfits", toSend.toString()));
					player.notifyWorldAboutChanges();

					return true;
				}

				return false;
			}
		};
		catalog.setEntityClass("book_turquoise");
		catalog.setPosition(9, 4);

		zone.add(catalog);
	}
}
