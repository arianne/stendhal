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
package games.stendhal.server.maps.magic.clothing_boutique;

import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ExamineChatAction;
import games.stendhal.server.entity.npc.behaviour.adder.OutfitChangerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.Pair;

public class OutfitLender2NPC implements ZoneConfigurator {
	
	// outfits to last for 10 hours normally 
	public static final int endurance = 10 * 60;

	// this constant is to vary the price. N=1 normally but could be a lot smaller on special occasions 
	private static final double N = 1;

	private static HashMap<String, Pair<Outfit, Boolean>> outfitTypes = new HashMap<String, Pair<Outfit, Boolean>>();
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		initOutfits();
		buildBoutiqueArea(zone);
	}

	private void initOutfits() {
		// these outfits must be put on over existing outfit 
		// (what's null doesn't change that part of the outfit)	
		// so true means we put on over
		// FIXME: Use new outfit system
		  final Pair<Outfit, Boolean> GLASSES = new Pair<Outfit, Boolean>(new Outfit(null, null, Integer.valueOf(86), null, null), true);
		  final Pair<Outfit, Boolean> GOBLIN_FACE = new Pair<Outfit, Boolean>(new Outfit(null, null, Integer.valueOf(88), null, null), true);
		  final Pair<Outfit, Boolean> THING_FACE = new Pair<Outfit, Boolean>(new Outfit(null, null, Integer.valueOf(87), null, null), true);

		// these outfits must replace the current outfit (what's null simply isn't there)
		  final Pair<Outfit, Boolean> PURPLE_SLIME = new Pair<Outfit, Boolean>(new Outfit(null, Integer.valueOf(00), Integer.valueOf(98), Integer.valueOf(00), Integer.valueOf(93)), false);
		  final Pair<Outfit, Boolean> GREEN_SLIME = new Pair<Outfit, Boolean>(new Outfit(null, Integer.valueOf(00), Integer.valueOf(98), Integer.valueOf(00), Integer.valueOf(89)), false);
		  final Pair<Outfit, Boolean> RED_SLIME = new Pair<Outfit, Boolean>(new Outfit(null, Integer.valueOf(00), Integer.valueOf(98), Integer.valueOf(00), Integer.valueOf(88)), false);
		  final Pair<Outfit, Boolean> BLUE_SLIME = new Pair<Outfit, Boolean>(new Outfit(null, Integer.valueOf(00), Integer.valueOf(98), Integer.valueOf(00), Integer.valueOf(91)), false);
		  final Pair<Outfit, Boolean> GINGERBREAD_MAN = new Pair<Outfit, Boolean>(new Outfit(null, Integer.valueOf(00), Integer.valueOf(98), Integer.valueOf(00), Integer.valueOf(92)), false);
		 
		
			outfitTypes.put("glasses", GLASSES);
			outfitTypes.put("goblin face", GOBLIN_FACE);
			outfitTypes.put("thing face", THING_FACE);
			outfitTypes.put("purple slime", PURPLE_SLIME);
			outfitTypes.put("green slime", GREEN_SLIME);
			outfitTypes.put("red slime", RED_SLIME);
			outfitTypes.put("blue slime", BLUE_SLIME);
			outfitTypes.put("gingerbread man", GINGERBREAD_MAN);
	}
		
	

	private void buildBoutiqueArea(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Saskia") {
			@Override
			protected void createPath() {
			    final List<Node> nodes = new LinkedList<Node>();
			    nodes.add(new Node(5, 7));
			    nodes.add(new Node(5, 20));
			    nodes.add(new Node(9, 20));
			    nodes.add(new Node(9, 7));
			    setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				class SpecialOutfitChangerBehaviour extends OutfitChangerBehaviour {
					SpecialOutfitChangerBehaviour(final Map<String, Integer> priceList, final int endurance, final String wearOffMessage) {
						super(priceList, endurance, wearOffMessage);
					}

					@Override
					public void putOnOutfit(final Player player, final String outfitType) {
						
						final Pair<Outfit, Boolean> outfitPair = outfitTypes.get(outfitType);
						final Outfit outfit = outfitPair.first();
						final boolean type = outfitPair.second();
						if (type) {
							player.setOutfit(outfit.putOver(player.getOutfit()), true);
						} else {
							player.setOutfit(outfit, true);
						}
						player.registerOutfitExpireTime(endurance);
					}
					// override transact agreed deal to only make the player rest to a normal outfit if they want a put on over type.
					@Override
					public boolean transactAgreedDeal(ItemParserResult res, final EventRaiser seller, final Player player) {
						final String outfitType = res.getChosenItemName();
						final Pair<Outfit, Boolean> outfitPair = outfitTypes.get(outfitType);
						final boolean type = outfitPair.second();

						if (type) {
							if (player.getOutfit().getBody() > 80
									&& player.getOutfit().getBody() < 99) {
								seller.say("You already have a magic outfit on which just wouldn't look good with another - could you please put yourself in something more conventional and ask again? Thanks!");
								return false;
							}
						}

						int charge = getCharge(res, player);

						if (player.isEquipped("money", charge)) {
							player.drop("money", charge);
							putOnOutfit(player, outfitType);
							return true;
						} else {
							seller.say("Sorry, you don't have enough money!");
							return false;
						}
					}
					
					// These outfits are not on the usual OutfitChangerBehaviour's
					// list, so they need special care when looking for them
					@Override
					public boolean wearsOutfitFromHere(final Player player) {
						final Outfit currentOutfit = player.getOutfit();

						for (final Pair<Outfit, Boolean> possiblePair : outfitTypes.values()) {
							if (possiblePair.first().isPartOf(currentOutfit)) {
								return true;
							}
						}
						return false;
					}
				}
				final Map<String, Integer> priceList = new HashMap<String, Integer>();
				priceList.put("glasses", (int) (N * 400));
				priceList.put("goblin face", (int) (N * 500));
				priceList.put("thing face", (int) (N * 500));
				priceList.put("purple slime", (int) (N * 3000));
				priceList.put("red slime", (int) (N * 3000));
				priceList.put("blue slime", (int) (N * 3000));
				priceList.put("green slime", (int) (N * 3000));
				priceList.put("gingerbread man", (int) (N * 1200));
			    addGreeting("Hello, I hope you are enjoying looking around our gorgeous boutique.");
				addQuest("Just look fabulous!");
				add(
					ConversationStates.ATTENDING,
					ConversationPhrases.OFFER_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"Please tell me which outfit you would like, ask to #hire #glasses, #hire a #goblin #face, #hire a #thing #face, #hire a #purple #slime outfit, #hire a #green #slime, #hire a #red #slime, #hire a #blue #slime, or #hire a #gingerbread #man outfit.",
					new ExamineChatAction("outfits2.png", "Outfits", "Price varies"));
				addJob("I work with magic in a fun way! Ask about the #offer.");
				addHelp("I can cast a spell to dress you in a magical outfit. They wear off after some time. I hope I can #offer you something you like. If not Liliana also rents out from a different range.");
				addGoodbye("Bye!");
				final OutfitChangerBehaviour behaviour = new SpecialOutfitChangerBehaviour(priceList, endurance, "Your magical outfit has worn off.");
				new OutfitChangerAdder().addOutfitChanger(this, behaviour, "hire", false, false);
			}
		};

		npc.setEntityClass("wizardwomannpc");
		npc.setPosition(5, 7);
		npc.initHP(100);
		npc.setDescription("You see Saskia. She works in the Magic City boutique.");
		zone.add(npc);
	}
}

