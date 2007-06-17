package games.stendhal.server.maps.semos.tavern;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Inside Semos Tavern - Level 0 (ground floor)
 */
public class TraderNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	private ShopList shops = ShopList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildXinBlanca(zone);
	}

	private void buildXinBlanca(final StendhalRPZone zone) {
		SpeakerNPC xinBlanca = new SpeakerNPC("Xin Blanca") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(2, 13));
				nodes.add(new Path.Node(2, 14));
				nodes.add(new Path.Node(3, 14));
				nodes.add(new Path.Node(3, 15));
				nodes.add(new Path.Node(5, 15));
				nodes.add(new Path.Node(5, 13));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("Shhh! I sell stuff to adventurers.");
				addHelp("I buy and sell several items, ask me for my #offer.");
				addSeller(new SellerBehaviour(shops.get("sellstuff")), false);
				addBuyer(new BuyerBehaviour(shops.get("buystuff")), false);
				add(ConversationStates.ATTENDING, "offer", null, ConversationStates.ATTENDING,
				        "Have a look at the blackboards on the wall to see my offers.", null);
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
		xinBlanca.initHP(100);
		zone.add(xinBlanca);

	}
}
