/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.ados.tavern;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;


public class BarmanNPCTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		new DatabaseFactory().initializeDatabase();
		MockStendlRPWorld.get();
		PlayerTestHelper.generatePlayerRPClasses();
		ItemTestHelper.generateRPClasses();
		PlayerTestHelper.generateNPCRPClasses();

		MockStendhalRPRuleProcessor.get();
		// load item configurations to handle money and other items
		SingletonRepository.getEntityManager();

		SingletonRepository.getNPCList().clear();
	}


	@Test
	public void testChoclateBar() throws Exception {
		BarmanNPC zoneconfig = new BarmanNPC();
		StendhalRPZone testzone = new StendhalRPZone("dalesTestZone");
		zoneconfig.configureZone(testzone , null);
		SpeakerNPC dale = SingletonRepository.getNPCList().get("dale");
		// configure Dale's shop
		SingletonRepository.getShopList().configureNPC("Dale", "adostavernbarman", true);

		Engine engine = dale.getEngine();
		Player player = PlayerTestHelper.createPlayer("monsterdhal");

		engine.step(player,"hi");
		assertThat(getReply(dale),is("Hey, good looking ..."));

		engine.step(player,"buy 500 chocolate bar");
		assertThat(getReply(dale),is("500 chocolate bars will cost 50000. Do you want to buy them?"));
		final Item item = ItemTestHelper.createItem("money", 50000);
		player.getSlot("bag").add(item);

		engine.step(player,"yes");
		assertThat(getReply(dale),is("Congratulations! Here are your chocolate bars!"));

		engine.step(player,"bye");
		assertThat(getReply(dale),is("See you around, sweetcheeks."));



		engine.step(player,"hi");
		assertThat(getReply(dale),is("Hey, good looking ..."));

		engine.step(player,"offer");
		assertThat(getReply(dale), anyOf(equalTo("I sell wine, pina colada, and chocolate bar."),
				equalTo("I sell wine, chocolate bar, and pina colada."),
				equalTo("I sell chocolate bar, wine, and pina colada."),
				equalTo("I sell chocolate bar, pina colada, and wine."),
				equalTo("I sell pina colada, chocolate bar, and wine."),
				equalTo("I sell pina colada, wine, and chocolate bar.")));

		engine.step(player,"buy 100 chocolate");
		assertThat(getReply(dale),is("Please specify which sort of chocolate you want to buy."));

		engine.step(player,"buy 100 chocolates");
		assertThat(getReply(dale),is("Please specify which sort of chocolate you want to buy."));
	}

}
