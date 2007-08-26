package games.stendhal.server.maps.ados.sewingroom;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Ados City, house with a woman who makes sails for the ships
 */
public class SeamstressNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	private ShopList shops = ShopList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSeamstress(zone);
	}

	private void buildSeamstress(final StendhalRPZone zone) {
		SpeakerNPC seamstress = new SpeakerNPC("Ida") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(7, 7));
				nodes.add(new Node(7, 14));
				nodes.add(new Node(12, 14));
				nodes.add(new Node(12, 7));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello there.");
				addJob("I'm a seamstress. I make sails for ships, like the Athor ferry.");
				addHelp("If you want to go to the island Athor on the ferry, just go south once you've departed from Ados, and look for the pier.");
				addQuest("If you could #offer me material I'd be grateful.");
				addBuyer(new BuyerBehaviour(shops.get("buycloaks")), false);
				add(ConversationStates.ATTENDING, "offer", null, ConversationStates.ATTENDING,
				        "I buy cloaks, because we are short of material to make sails with. The better the material, the more I pay. My notebook on the table has the price list.", null);
				addGoodbye("Bye, thanks for stepping in.");

				Sign board = new Sign();
				zone.assignRPObjectID(board);
				board.setPosition(16, 8);
				board.setEntityClass("book_blue");
				board.setResistance(10);
				board.setText(shops.toString("buycloaks", "-- Buying --"));
				zone.add(board);
			}
		};
		npcs.add(seamstress);

		zone.assignRPObjectID(seamstress);
		seamstress.setEntityClass("woman_002_npc");
		seamstress.setPosition(7, 7);
		seamstress.initHP(100);
		zone.add(seamstress);
	}
}
