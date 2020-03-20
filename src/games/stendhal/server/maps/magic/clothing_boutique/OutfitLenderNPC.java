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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.constants.Occasion;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.OutfitChangerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ShowOutfitListEvent;
import marauroa.common.Pair;

public class OutfitLenderNPC implements ZoneConfigurator {

	// outfits to last for 10 hours normally
	public static final int endurance = 10 * 60;

	// this constant is to vary the price. N=1 normally but could be a lot smaller on special occasions
	private static final double N = 1;

	private final static HashMap<String, Pair<Outfit, Boolean>> outfitTypes = new HashMap<String, Pair<Outfit, Boolean>>();
	private final static Map<String, Integer> priceList = new HashMap<String, Integer>();

	// for the client to know which bases should not be hidden in the preview
	private final static List<String> hideBaseOverrides = Arrays.asList("horse", "girl horse", "alien");

	private SpeakerNPC npc;

	private String jobReply;

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		if (Occasion.MINETOWN) {
			jobReply = "I work in clothes boutique located in Magic City. It's no ordinary shop, we use magic to put our clients into fantastic outfits. Ask about the #offer.";
		} else {
			jobReply= "I work in this clothes boutique. It's no ordinary shop, we use magic to put our clients into fantastic outfits. Ask about the #offer.";
		}

		initOutfits();
		buildBoutiqueArea(zone);
	}

	private void initOutfits() {
		// these outfits must be put on over existing outfit
		// (what's null doesn't change that part of the outfit)
		// so true means we put on over
		// NOTE: use negative numbers for layers that should not be drawn (use 998 for body layer)
		final Pair<Outfit, Boolean> JUMPSUIT = new Pair<Outfit, Boolean>(new Outfit(null, 983, null, null, null, null, null, null, null), true);
		final Pair<Outfit, Boolean> DUNGAREES = new Pair<Outfit, Boolean>(new Outfit(null, 984, null, null, null, null, null, null, null), true);
		final Pair<Outfit, Boolean> GREEN_DRESS = new Pair<Outfit, Boolean>(new Outfit(null, 978, null, null, null, null, null, null, null), true);

		final Pair<Outfit, Boolean> GOWN = new Pair<Outfit, Boolean>(new Outfit(null, 982, null, null, null, null, null, null, null), true);
		final Pair<Outfit, Boolean> NOOB = new Pair<Outfit, Boolean>(new Outfit(null, 980, null, null, null, null, null, null, null), true);
		final Pair<Outfit, Boolean> JESTER = new Pair<Outfit, Boolean>(new Outfit(null, 976, null, null, null, null, -1, 995, null), true);

		// these outfits must replace the current outfit (what's -1 simply isn't there)
		final Pair<Outfit, Boolean> BUNNY = new Pair<Outfit, Boolean>(new Outfit(998, 981, -1, -1, -1, -1, -1, -1, null), false);
		final Pair<Outfit, Boolean> HORSE = new Pair<Outfit, Boolean>(new Outfit(997, -1, -1, -1, -1, -1, -1, -1, null), false);
		final Pair<Outfit, Boolean> GIRL_HORSE = new Pair<Outfit, Boolean>(new Outfit(996, -1, -1, -1, -1, -1, -1, -1, null), false);
		final Pair<Outfit, Boolean> ALIEN = new Pair<Outfit, Boolean>(new Outfit(995, -1, -1, -1, -1, -1, -1, -1, null), false);

		outfitTypes.put("jumpsuit", JUMPSUIT);
		outfitTypes.put("dungarees", DUNGAREES);
		outfitTypes.put("green dress", GREEN_DRESS);
		outfitTypes.put("gown", GOWN);
		outfitTypes.put("orange", NOOB);
		outfitTypes.put("bunny", BUNNY);
		outfitTypes.put("jester", JESTER);
		outfitTypes.put("horse", HORSE);
		outfitTypes.put("girl horse", GIRL_HORSE);
		outfitTypes.put("alien", ALIEN);
	}

	private void buildBoutiqueArea(final StendhalRPZone zone) {
		npc = new SpeakerNPC("Liliana") {
			@Override
			protected void createPath() {
			    final List<Node> nodes = new LinkedList<Node>();
			    nodes.add(new Node(16, 5));
			    nodes.add(new Node(16, 16));
			    nodes.add(new Node(26, 16));
			    nodes.add(new Node(26, 5));
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

						// remove temporary outfits to avoid visual conflicts
						player.returnToOriginalOutfit();

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
							if (player.getOutfit().getLayer("body") > 80
									&& player.getOutfit().getLayer("body") < 99) {
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

				priceList.put("jumpsuit", (int) (N * 500));
				priceList.put("dungarees", (int) (N * 500));
				priceList.put("green dress", (int) (N * 500));
				priceList.put("gown", (int) (N * 750));
				priceList.put("orange", (int) (N * 500));
				priceList.put("bunny", (int) (N * 800));
				priceList.put("jester", (int) (N * 400));
				priceList.put("horse", (int) (N * 1200));
				priceList.put("girl horse", (int) (N * 1200));
				priceList.put("alien", (int) (N * 1200));

				addGreeting("Hi! How may I help you?");
				addQuest("I can't think of anything for you, sorry.");
				add(
					ConversationStates.ATTENDING,
					ConversationPhrases.OFFER_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"Just tell me if you want to #hire a #gown, #hire a #green #dress, #hire a #jester suit, #hire an #alien suit, #hire a #horse outfit, #hire a #girl #horse outfit, #hire a #jumpsuit, #hire #dungarees, #hire a #bunny suit or #hire an #orange outfit.",
					createPreviewAction());

				addJob(jobReply);
				// addJob("I normally work in a clothes boutique, we use magic to put our clients into fantastic outfits. I'm here for Mine Town Revival Weeks, where we #offer our outfits at greatly reduced prices, but they last for less time!");
				addHelp("Our hired outfits wear off after some time, but you can always come back for more!");
				addGoodbye("Bye!");
				final OutfitChangerBehaviour behaviour = new SpecialOutfitChangerBehaviour(priceList, endurance, "Your magical outfit has worn off.");
				new OutfitChangerAdder().addOutfitChanger(this, behaviour, "hire", false, false);
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				if (Occasion.MINETOWN) {
					setDirection(Direction.DOWN);
				}
			}
		};

		npc.setEntityClass("slim_woman_npc");
		npc.initHP(100);
		npc.setDescription("You see Liliana. She works in the Magic City clothes boutique.");

		if (Occasion.MINETOWN) {
			npc.clearPath();
			npc.stop();
			npc.setDirection(Direction.DOWN);
			npc.setPosition(53, 9);
		} else {
			npc.setPosition(16, 5);
		}

		zone.add(npc);
	}


	private ChatAction createPreviewAction() {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				final StringBuilder outfitString = new StringBuilder();
				final int outfitCount = outfitTypes.size();

				int idx = 0;
				for (final String outfitName: outfitTypes.keySet()) {
					outfitString.append(outfitName + ";" + outfitTypes.get(outfitName).first().toString() + ";" + priceList.get(outfitName));
					if (hideBaseOverrides.contains(outfitName)) {
						outfitString.append(";showall");
					} else {
						outfitString.append(";"); // avoid index out of range exception
					}

					if (outfitName.equals("horse") || outfitName.equals("girl horse")) {
						// show side-facing frame for horses
						outfitString.append(";1;3");
					}

					if (idx < outfitCount - 1) {
						outfitString.append(":");
					}
					idx++;
				}

				player.addEvent(new ShowOutfitListEvent(npc.getName() + "s Shop", "Outfits rented here", outfitString.toString()));
				player.notifyWorldAboutChanges();
			}
		};
	}
}
