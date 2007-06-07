package games.stendhal.server.maps.wofol.house4;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Inside the Kobold City, interior called house4
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
		buildTrader(zone);
	}

	private void buildTrader(final StendhalRPZone zone) {
		SpeakerNPC trader = new SpeakerNPC("Wrvil") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(4, 3));
				nodes.add(new Path.Node(4, 8));
				nodes.add(new Path.Node(12, 8));
				nodes.add(new Path.Node(12, 3));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to the Kobold City of Wofol. I hope you come in peace.");
				addJob("I run a buying and selling trade with kobolds - or whoever else passes by. I am one of the few Kobolds who can speak with non-Kobolds.");
				addHelp("I buy and sell all sorts of items. Ask me for my #offer.");
				addQuest("Try Alrak the mountain dwarf who lives here with the kobolds. He'd probably have more than one task to give you.");
				addSeller(new SellerBehaviour(shops.get("sellstuff2")), false);
				addBuyer(new BuyerBehaviour(shops.get("buystuff2")), false);
				add(ConversationStates.ATTENDING, "offer", null, ConversationStates.ATTENDING,
				        "Please look at the each blackboard on the wall to see what I buy and sell at the moment.", null);
				addGoodbye("Bye, and please don't attack too many of my friends.");
				Sign board = new Sign();
				zone.assignRPObjectID(board);
				board.set(4, 1);
				board.setClass("blackboard");
				board.setText(shops.toString("sellstuff2", "-- I sell --"));
				zone.add(board);

				board = new Sign();
				zone.assignRPObjectID(board);
				board.set(6, 1);
				board.setClass("blackboard");
				board.setText(shops.toString("buystuff2", "-- I buy --"));
				zone.add(board);
			}
		};
		npcs.add(trader);

		zone.assignRPObjectID(trader);
		trader.put("class", "koboldnpc");
		trader.setX(4);
		trader.setY(3);
		trader.initHP(100);
		zone.add(trader);

	}
}
