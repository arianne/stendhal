package games.stendhal.server.maps.semos;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.CroupierNPC;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Inside Semos Tavern - Level 0 (ground floor)
 */
public class SemosCityTavern0 implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildMargaret(zone);
		buildXinBlanca(zone);
		buildRicardo(zone);
	}


	private void buildMargaret(StendhalRPZone zone) {
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

	private void buildXinBlanca(final StendhalRPZone zone) {
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
				Sign board = new Sign();
				zone.assignRPObjectID(board);
				board.set(2, 11);
				board.setClass("blackboard");
				board.setText(shops.toString("sellstuff", "-- I sell --"));
				zone.add(board);

				board = new Sign();
				zone.assignRPObjectID(board);
				board.set(3, 11);
				board.setClass("blackboard");
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

	private void buildRicardo(StendhalRPZone zone) {
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
}
