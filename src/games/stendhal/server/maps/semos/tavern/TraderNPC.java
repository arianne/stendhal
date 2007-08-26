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
import games.stendhal.server.pathfinder.Node;

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
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(2, 14));
				nodes.add(new Node(2, 15));
				nodes.add(new Node(3, 15));
				nodes.add(new Node(3, 16));
				nodes.add(new Node(5, 16));
				nodes.add(new Node(5, 14));
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
			}
		};
		npcs.add(xinBlanca);

		zone.assignRPObjectID(xinBlanca);
		xinBlanca.setEntityClass("weaponsellernpc");
		xinBlanca.setPosition(2, 15);
		xinBlanca.initHP(100);
		zone.add(xinBlanca);
	}
}
