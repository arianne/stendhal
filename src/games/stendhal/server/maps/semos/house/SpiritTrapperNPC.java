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
package games.stendhal.server.maps.semos.house;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.MultiProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.MultiProducerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.TeleporterBehaviour;
import games.stendhal.server.entity.player.Player;



/**
 * Builds a shady Spirit Trapper NPC for the Empty Bottle quest.
 *
 * @author soniccuz based on FlowerSellerNPC by kymara and fishermanNPC by dine.
 */
public class SpiritTrapperNPC implements ZoneConfigurator {



	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {

			final List<String> setZones = new ArrayList<String>();
			setZones.add("0_ados_swamp");
			setZones.add("0_ados_outside_w");
			setZones.add("0_ados_wall_n2");
			setZones.add("0_ados_wall_s");
			setZones.add("0_ados_city_s");
        	new TeleporterBehaviour(buildSemosHouseArea(), setZones, "0_ados", "..., ....");
	}

	private SpeakerNPC buildSemosHouseArea() {

	    final SpeakerNPC mizuno = new SpeakerNPC("Mizuno") {
	                @Override
			protected void createPath() {
				// npc does not move
				setPath(null);
			}
	        @Override
			protected void createDialog() {
	        	addGreeting("What do you want?");
			    addJob("I prefer to keep that to myself.");
			    addHelp("Look I must move on soon but, quickly, if you have any #black #pearls I will trade some of my magic #arrows for them. Care to #buy for some #arrows?");
			    addOffer("Look I must move on soon but, quickly, if you have any #black #pearls I will trade some of my magic #arrows for them. Care to #buy for some #arrows?");
			    addGoodbye("Then get going... I can't get any work done with you mucking around.");

			    addReply("arrows","I enchant arrows with elemental power. I have #ice, #fire, and #light.");
			    addReply(Arrays.asList("ice", "ice arrow", "fire", "fire arrow"),
	                    "I can spare 1 of those arrows for every each black pearl.");
			    addReply(Arrays.asList("light", "light arrow"),
	                    "Light arrows are tricky, I can only spare 1 for every 2 black pearls.");
			    addReply(Arrays.asList("black pearls", "pearls"),
	                    "To me they make for decent talismans. I often find them on those assassin punks.");
			    // the rest is in the MessageInABottle quest




			 // Mizuno exchanges elemental arrows for black pearls.
				// (uses sorted TreeMap instead of HashMap)
			    final HashSet<String> productsNames = new HashSet<String>();
                productsNames.add("ice arrow");
                productsNames.add("fire arrow");
                productsNames.add("light arrow");

                final Map<String, Integer> reqRes_iceArrow = new TreeMap<String, Integer>();
                reqRes_iceArrow.put("black pearl", 1);

                final Map<String, Integer> reqRes_fireArrow = new TreeMap<String, Integer>();
                reqRes_fireArrow.put("black pearl", 1);

                final Map<String, Integer> reqRes_lightArrow = new TreeMap<String, Integer>();
                reqRes_lightArrow.put("black pearl", 2);


                final HashMap<String, Map<String, Integer>> requiredResourcesPerProduct = new HashMap<String, Map<String, Integer>>();
                requiredResourcesPerProduct.put("ice arrow", reqRes_iceArrow);
                requiredResourcesPerProduct.put("fire arrow", reqRes_fireArrow);
                requiredResourcesPerProduct.put("light arrow", reqRes_lightArrow);

                final HashMap<String, Integer> productionTimesPerProduct = new HashMap<String, Integer>();
                productionTimesPerProduct.put("ice arrow", 0 * 60);
                productionTimesPerProduct.put("fire arrow", 0 * 60);
                productionTimesPerProduct.put("light arrow", 0 * 60);

                final HashMap<String, Boolean> productsBound = new HashMap<String, Boolean>();
                productsBound.put("ice arrow", false);
                productsBound.put("fire arrow", false);
                productsBound.put("light arrow", false);

                class SpecialTraderBehaviour extends MultiProducerBehaviour {

					public SpecialTraderBehaviour(String questSlot, String productionActivity,
							HashSet<String> productsNames,
							HashMap<String, Map<String, Integer>> requiredResourcesPerProduct,
							HashMap<String, Integer> productionTimesPerProduct,
							HashMap<String, Boolean> productsBound) {
						super(questSlot, productionActivity, productsNames, requiredResourcesPerProduct, productionTimesPerProduct,
								productsBound);
						// TODO Auto-generated constructor stub
					}

					@Override
					public boolean askForResources(final ItemParserResult res, final EventRaiser npc, final Player player) {
						int amount = res.getAmount();
				        String productName = res.getChosenItemName();

				        if (getMaximalAmount(productName, player) < amount) {
				            npc.say("I can only " + getProductionActivity() + " "
				                    + Grammar.quantityplnoun(amount, productName, "a")
				                    + " if you bring me "
				                    + getRequiredResourceNamesWithHashes(productName, amount) + ".");
				            return false;
				        } else {
							res.setAmount(amount);
							npc.say(Grammar.quantityplnoun(amount, productName, "a")
									+ " for "
									+ getRequiredResourceNamesWithHashes(productName, amount) + ". "
									+ " Correct?");


				            return true;
				        }
				    }


					@Override
					public boolean transactAgreedDeal(ItemParserResult res, final EventRaiser npc, final Player player) {
				    	int amount = res.getAmount();
				        String productName = res.getChosenItemName();


				        if (getMaximalAmount(productName, player) < amount) {
				            // The player tried to cheat us by placing the resource
				            // onto the ground after saying "yes"
				            npc.say("Hey! I'm over here! You'd better not be trying to trick me...");
				            return false;
				        } else {
				            for (final Map.Entry<String, Integer> entry : getRequiredResourcesPerProduct(productName).entrySet()) {
				                final int amountToDrop = amount * entry.getValue();
				                player.drop(entry.getKey(), amountToDrop);
				            }
				            final long timeNow = new Date().getTime();
				            player.setQuest(getQuestSlot(), amount + ";" + productName + ";" + timeNow);

				            if (getProductionTime(productName, amount) == 0) {

				            	//If production time is 0 just give player the product
				            	final int numberOfProductItems = amount;
				            	final StackableItem products = (StackableItem) SingletonRepository.getEntityManager().getItem(productName);
				    			products.setQuantity(numberOfProductItems);

				    			if (isProductBound(productName)) {
				    				products.setBoundTo(player.getName());
				    			}

				    			if (player.equipToInventoryOnly(products)) {
				    				npc.say("Here " + Grammar.isare(numberOfProductItems)
									+ " your " + Grammar.quantityplnoun(numberOfProductItems,
											productName, "") + ".");

				    				player.setQuest(getQuestSlot(), "done");
				    				player.notifyWorldAboutChanges();
				    				player.incProducedForItem(productName, products.getQuantity());
				    			} else {
				    				npc.say("Welcome back! I'm done with your order. But right now you cannot take the "
				    						+ Grammar.plnoun(numberOfProductItems, productName)
				    						+ ". Come back when you have space.");
				    			}

				            	return true;
				            } else {
				            		npc.say("OK, I will "
				                    + getProductionActivity()
				                    + " "
				                    + Grammar.quantityplnoun(amount, productName, "a")
				                    + " for you, but that will take some time. Please come back in "
				                    + getApproximateRemainingTime(player) + ".");
				            		return true;
				            	}
				        }
				    }


                }

                final MultiProducerBehaviour behaviour = new SpecialTraderBehaviour(
                        "arrow_trader",
                        "buy",
                        productsNames,
                        requiredResourcesPerProduct,
                        productionTimesPerProduct,
                        productsBound);

                    new MultiProducerAdder().addMultiProducer(this, behaviour,
                        "What do you want?");
			}
		};

		mizuno.setEntityClass("man_001_npc");
		mizuno.initHP(100);
		mizuno.setHP(80);
		mizuno.setCollisionAction(CollisionAction.REVERSE);
		mizuno.setDescription("You see Mizuno. Like a spirit he wanders the vacant haunts of Ados doing who knows what.");

		// start in int_semos_house
		final StendhalRPZone	zone = SingletonRepository.getRPWorld().getZone("int_semos_house");
		mizuno.setPosition(5, 6);
		zone.add(mizuno);

		return mizuno;
	}
}
