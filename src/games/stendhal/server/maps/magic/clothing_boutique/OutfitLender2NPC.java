/***************************************************************************
 *                   (C) Copyright 2003-2018 - Stendhal                    *
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

public class OutfitLender2NPC implements ZoneConfigurator {

	// outfits to last for 10 hours normally
	public static final int endurance = 10 * 60;

	// this constant is to vary the price. N=1 normally but could be a lot smaller on special occasions
	private static final double N = 1;

	private static HashMap<String, Pair<Outfit, Boolean>> outfitTypes = new HashMap<String, Pair<Outfit, Boolean>>();
	private final static Map<String, Integer> priceList = new HashMap<String, Integer>();

	// for the client to know which bases should not be hidden in the preview
	private final static Map<String, String> hideBaseOverrides = new HashMap<String, String>() {{
		put("gingerbread man", "showbody");
		put("black cat", "showbody");
		put("white cat", "showbody");
		put("green slime", "showbody");
		put("red slime", "showbody");
		put("purple slime", "showbody");
		put("blue slime", "showbody");
		put("goblin face", "showhead");
		put("thing face", "showhead");
	}};


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
		// NOTE: use negative numbers for layers that should not be drawn (use 998 for body layer)
		final Pair<Outfit, Boolean> GOBLIN_FACE = new Pair<Outfit, Boolean>(new Outfit(null, null, 988, -1, -1, -1, -1, -1, null), true);
		final Pair<Outfit, Boolean> THING_FACE = new Pair<Outfit, Boolean>(new Outfit(null, null, 987, -1, -1, -1, -1, -1, null), true);
		final Pair<Outfit, Boolean> Umbrella = new Pair<Outfit, Boolean>(new Outfit(null, null, null, null, null, null, null, null, 7), true);

		// these outfits must replace the current outfit (what's null simply isn't there)
		final Pair<Outfit, Boolean> PURPLE_SLIME = new Pair<Outfit, Boolean>(new Outfit(993, -1, -1, -1, -1, -1, -1, -1, null), false);
		final Pair<Outfit, Boolean> GREEN_SLIME = new Pair<Outfit, Boolean>(new Outfit(989, -1, -1, -1, -1, -1, -1, -1, null), false);
		final Pair<Outfit, Boolean> RED_SLIME = new Pair<Outfit, Boolean>(new Outfit(988, -1, -1, -1, -1, -1, -1, -1, null), false);
		final Pair<Outfit, Boolean> BLUE_SLIME = new Pair<Outfit, Boolean>(new Outfit(991, -1, -1, -1, -1, -1, -1, -1, null), false);
		final Pair<Outfit, Boolean> GINGERBREAD_MAN = new Pair<Outfit, Boolean>(new Outfit(992, -1, -1, -1, -1, -1, -1, -1, null), false);
		final Pair<Outfit, Boolean> WHITE_CAT = new Pair<Outfit, Boolean>(new Outfit(978, -1, -1, -1, -1, -1, -1, -1, null), false);
		final Pair<Outfit, Boolean> BLACK_CAT = new Pair<Outfit, Boolean>(new Outfit(979, -1, -1, -1, -1, -1, -1, -1, null), false);

		outfitTypes.put("goblin face", GOBLIN_FACE);
		outfitTypes.put("thing face", THING_FACE);
		outfitTypes.put("umbrella", Umbrella);
		outfitTypes.put("purple slime", PURPLE_SLIME);
		outfitTypes.put("green slime", GREEN_SLIME);
		outfitTypes.put("red slime", RED_SLIME);
		outfitTypes.put("blue slime", BLUE_SLIME);
		outfitTypes.put("gingerbread man", GINGERBREAD_MAN);
		outfitTypes.put("white cat", WHITE_CAT);
		outfitTypes.put("black cat", BLACK_CAT);
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

				priceList.put("goblin face", (int) (N * 500));
				priceList.put("thing face", (int) (N * 500));
				priceList.put("purple slime", (int) (N * 3000));
				priceList.put("red slime", (int) (N * 3000));
				priceList.put("blue slime", (int) (N * 3000));
				priceList.put("green slime", (int) (N * 3000));
				priceList.put("gingerbread man", (int) (N * 1200));
				priceList.put("umbrella", (int) (N * 300));
				priceList.put("black cat", (int) (N * 4500));
				priceList.put("white cat", (int) (N * 4500));

			    addGreeting("Hello, I hope you are enjoying looking around our gorgeous boutique.");
				addQuest("Just look fabulous!");
				add(
					ConversationStates.ATTENDING,
					ConversationPhrases.OFFER_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"Please tell me which outfit you would like,"
					+ " ask to #hire a #goblin #face,"
					+ " #hire a #thing #face, #hire a #umbrella,"
					+ " #hire a #purple #slime outfit, #hire a #green #slime,"
					+ " #hire a #red #slime, #hire a #blue #slime,"
					+ " #hire a #gingerbread #man outfit,"
					+ " #hire a #white #cat, or #hire a #black #cat.",
					createPreviewAction());

				addJob("I work with magic in a fun way! Ask about the #offer.");
				addHelp("I can cast a spell to dress you in a magical outfit. They wear off after some time. I hope I can #offer you something you like. If not Liliana also rents out from a different range.");
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

		npc.setEntityClass("wizardwomannpc");
		npc.initHP(100);
		npc.setDescription("You see Saskia. She works in the Magic City boutique.");

		if (Occasion.MINETOWN) {
			npc.clearPath();
			npc.stop();
			npc.setDirection(Direction.DOWN);
			npc.setPosition(42, 9);
		} else {
			npc.setPosition(5, 7);
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
					if (hideBaseOverrides.containsKey(outfitName)) {
						outfitString.append(";" + hideBaseOverrides.get(outfitName));
					} else {
						outfitString.append(";"); // avoid index out of range exception
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
