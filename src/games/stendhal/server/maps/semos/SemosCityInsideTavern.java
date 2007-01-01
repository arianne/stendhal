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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

public class SemosCityInsideTavern {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();

	public void build() {
		buildSemosTavernPortals();
		buildSemosTavernLevel0Margaret();
		buildSemosTavernLevel0XinBlanca();
		buildSemosTavernLevel0Ricardo();
		buildSemosTavernLevel1Ouchit();
		buildSemosTavernLevel1McPegleg();
	}

	private void buildSemosTavernPortals() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
		"int_semos_tavern_0"));

		Portal portal = new Portal();
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
				
		zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"int_semos_tavern_1"));

		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.set(4, 4);
		portal.setNumber(0);
		portal.setDestination("int_semos_tavern_0", 1);
		zone.addPortal(portal);
	}

	private void buildSemosTavernLevel0Margaret() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"int_semos_tavern_0"));
		
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
				addJob("I am the bar maid for this fair tavern. We sell both imported and local beers, and fine food.");
				addHelp("This tavern is a great place to take a break and meet new people! Just ask if you want me to #offer you a drink.");
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
	}		

	private void buildSemosTavernLevel0XinBlanca() {
			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
					"int_semos_tavern_0"));
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
				addHelp("I buy and sell several items, ask me for my #offer.");
				addSeller(new SellerBehaviour(shops.get("sellstuff")), false);
				addBuyer(new BuyerBehaviour(shops.get("buystuff")), false);
				add(ConversationStates.ATTENDING,
					"offer",
					null,
					ConversationStates.ATTENDING,
					"Have a look at the blackboards on the wall to see my offers.",
					null);
				addGoodbye();
				StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get()
						.getRPZone(new IRPZone.ID("int_semos_tavern_0"));
				Blackboard board = new Blackboard(false);
				zone.assignRPObjectID(board);
				board.set(2, 11);
				board.setText(shops.toString("sellstuff", "-- I sell --"));
				zone.add(board);
				board = new Blackboard(false);
				zone.assignRPObjectID(board);
				board.set(3, 11);
				board.setText(shops.toString("buystuff", "-- I buy --"));
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
	}

	private void buildSemosTavernLevel0Ricardo() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"int_semos_tavern_0"));

		CroupierNPC ricardo = new CroupierNPC("Ricardo") {
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
		Rectangle tableArea = new Rectangle(25, 4, 2, 3);
		ricardo.setTableArea(tableArea);
		zone.addNPC(ricardo);		
	}

	private void buildSemosTavernLevel1Ouchit() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"int_semos_tavern_1"));
		
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
	}

	private void buildSemosTavernLevel1McPegleg() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("int_semos_tavern_1"));

		// Adding a new NPC that buys some of the stuff that Xin doesn't
		SpeakerNPC mcpegleg = new SpeakerNPC("McPegleg") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(16, 2));
				nodes.add(new Path.Node(13, 2));
				nodes.add(new Path.Node(13, 1));
				nodes.add(new Path.Node(13, 2));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Yo matey! You look like you need #help.");
				addJob("I'm a trader of ... let's say ... #rare things.");
				addHelp("Not sure if I can trust you ....");
				addQuest("Perhaps if you find some #rare #armor or #weapon ...");
				addGoodbye("I see you!");
				add(ConversationStates.ATTENDING, Arrays.asList("weapon", "armor", "rare"),
					ConversationStates.ATTENDING,
					"Ssshh! I'm occasionally buying rare weapons and armor. Got any? Ask for my #offer",
					null);

				add(ConversationStates.ATTENDING, Arrays.asList("eye","leg","wood","patch"),
						ConversationStates.ATTENDING,
						"Not every day is a lucky day ...",
						null);
				add(ConversationStates.ATTENDING, "pirate", 
						null, ConversationStates.ATTENDING, 
						"That's none of you business!",
						null);
				addBuyer(new BuyerBehaviour(shops.get("buyrare")), false);
			}
		};

		// Add some atmosphere
		mcpegleg.setDescription("You see a dubious man with a patched eye and a wooden leg.");  
		  
		// Add our new NPC to the game world
		npcs.add(mcpegleg);
		zone.assignRPObjectID(mcpegleg);
		mcpegleg.put("class", "pirate_sailornpc");
		mcpegleg.set(16, 2);
		mcpegleg.initHP(100);
		zone.addNPC(mcpegleg);

		// Add a blackboard with the shop offers
		Blackboard board = new Blackboard(false);
		zone.assignRPObjectID(board);
		board.set(11, 4);
		board.setText(shops.toString("buyrare", "-- Buying --"));
		zone.add(board);
	}
}
