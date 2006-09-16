package games.stendhal.server.maps.semos;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Blackboard;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.CroupierNPC;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.pathfinder.Path;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

public class SemosCityInsideTavern {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		buildSemosTavernArea();
	}

	private void buildSemosTavernArea() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"int_semos_tavern_1"));
		
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.set(4, 4);
		portal.setNumber(0);
		portal.setDestination("int_semos_tavern_0", 1);
		zone.addPortal(portal);
		
		SpeakerNPC ouchit = new SpeakerNPC("Ouchit") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(21, 2));
				nodes.add(new Path.Node(25, 2));
				nodes.add(new Path.Node(25, 4));
				nodes.add(new Path.Node(29, 4));
				nodes.add(new Path.Node(25, 4));
				nodes.add(new Path.Node(25, 2));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I sell bows and arrows.");
				addHelp("I sell several items, ask me for my #offer.");
				addSeller(new SellerBehaviour(shops.get("sellrangedstuff")));
				addGoodbye();
			}
		};
		npcs.add(ouchit);
		zone.assignRPObjectID(ouchit);
		ouchit.put("class", "weaponsellernpc");
		ouchit.set(21, 2);
		ouchit.initHP(100);
		zone.addNPC(ouchit);

		zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"int_semos_tavern_0"));
		
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(22);
		portal.setY(17);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 0);
		zone.addPortal(portal);
		
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.set(4, 4);
		portal.setNumber(1);
		portal.setDestination("int_semos_tavern_1", 0);
		zone.addPortal(portal);
		
		SpeakerNPC margaret = new SpeakerNPC("Margaret") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(17, 12));
				nodes.add(new Path.Node(17, 13));
				nodes.add(new Path.Node(16, 8));
				nodes.add(new Path.Node(13, 8));
				nodes.add(new Path.Node(13, 6));
				nodes.add(new Path.Node(13, 10));
				nodes.add(new Path.Node(23, 10));
				nodes.add(new Path.Node(23, 10));
				nodes.add(new Path.Node(23, 13));
				nodes.add(new Path.Node(23, 10));
				nodes.add(new Path.Node(17, 10));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am the bar maid for this fair tavern. We sell fine beers and food.");
				addHelp("At this tavern you can get an #offer of drinks and take a break to meet new people!");
				addSeller(new SellerBehaviour(shops.get("food&drinks")));
				addGoodbye();
			}
		};
		npcs.add(margaret);
		zone.assignRPObjectID(margaret);
		margaret.put("class", "tavernbarmaidnpc");
		margaret.set(17, 12);
		margaret.initHP(100);
		zone.addNPC(margaret);
		
		SpeakerNPC xinBlanca = new SpeakerNPC("Xin Blanca") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(2, 14));
				nodes.add(new Path.Node(2, 15));
				nodes.add(new Path.Node(5, 15));
				nodes.add(new Path.Node(5, 14));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("Shhh! I sell stuff to adventurers.");
				addHelp("I buy and sell several items, ask me for my #offer!");
				addSeller(new SellerBehaviour(shops.get("sellstuff")), false);
				addBuyer(new BuyerBehaviour(shops.get("buystuff")), false);
				add(ConversationStates.ATTENDING,
					"offer",
					null,
					ConversationStates.ATTENDING,
					"Have a look at the blackboards on the wall to see my offers",
					null);
				addGoodbye();
				StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get()
						.getRPZone(new IRPZone.ID("int_semos_tavern_0"));
				Blackboard board = new Blackboard(false);
				zone.assignRPObjectID(board);
				board.set(2, 11);
				board.setText(shops.toString("sellstuff", "-- Selling --"));
				zone.add(board);
				board = new Blackboard(false);
				zone.assignRPObjectID(board);
				board.set(3, 11);
				board.setText(shops.toString("buystuff", "-- Buying --"));
				zone.add(board);
			}
		};
		npcs.add(xinBlanca);
		
		zone.assignRPObjectID(xinBlanca);
		xinBlanca.put("class", "weaponsellernpc");
		xinBlanca.setX(2);
		xinBlanca.setY(14);
		xinBlanca.setBaseHP(100);
		xinBlanca.setHP(xinBlanca.getBaseHP());
		zone.addNPC(xinBlanca);
		
		Rectangle tableArea = new Rectangle(25, 4, 2, 3);
		SpeakerNPC ricardo = new CroupierNPC("Ricardo", tableArea) {
			@Override
			protected void createPath() {
				// Ricardo doesn't move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				
				addGreeting("Welcome to the #gambling table, where dreams can come true.");
				addJob("I'm the only person in Semos who is licensed to offer gambling activities.");
				addReply("gambling", "The rules are simple: just tell me if you want to #play, pay the stake, and throw the dice on the table. The higher the sum of the upper faces is, the nicer will be your prize. Take a look at the blackboards on the wall!");
				addHelp("If you are looking for Ouchit: he's upstairs.");
				addGoodbye();
			}
		};
		
		npcs.add(ricardo);
		
		zone.assignRPObjectID(ricardo);
		ricardo.put("class", "naughtyteen2npc");
		ricardo.setX(28);
		ricardo.setY(4);
		ricardo.setDirection(Direction.LEFT);
		ricardo.setBaseHP(100);
		ricardo.setHP(ricardo.getBaseHP());
		zone.addNPC(ricardo);		
	}

}
