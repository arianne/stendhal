package games.stendhal.server.maps.mithrilbourgh.stores;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds an NPC to buy previously un bought weapons. He is the QM of the
 * Mithrilbourgh Army, who are short of boots and helmets
 * 
 * @author kymara
 */
public class BuyerNPC implements ZoneConfigurator {
	private ShopList shops = ShopList.get();

	/**
	 * Configure a zone.
	 * 
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Diehelm Brui") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(10, 4));
				nodes.add(new Node(10, 8));
				nodes.add(new Node(15, 8));
				nodes.add(new Node(15, 2));
				nodes.add(new Node(3, 2));
				nodes.add(new Node(3, 7));
				nodes.add(new Node(10, 7));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to the supply stores for the Mithrilbourgh Army.");
				addJob("I proud to be the Quartermaster of the Mithrilbourgh Army. However, we are lacking in #boots and #helmets.");
				addReply(
						"boots",
						"I seem to hand out stone boots very regularly, but our careless soldiers  always lose them. Thus, I buy any good boots that you can #offer, see the blue book for a price list.");
				addReply(
						"helmets",
						"I do not have a good source of helmets. Any you can #trade with me would be appreciated, at the moment we only have enough for the lieutenants, and none for the soldiers. The red book has details.");
				addHelp("As Quartermaster, I take #offers for supplies which we are short of.");
				addOffer("I buy #boots and #helmets on behalf of the Mithrilbourgh Army.");
				addQuest("The Mithrilbourgh Army is not in need your services at present.");
				new BuyerAdder().add(this, new BuyerBehaviour(
						shops.get("boots&helm")), false);
				addGoodbye("Bye.");
			}
		};
		npc.setDescription("You see Diehelm Brui, the Quartermaster.");
		npc.setEntityClass("recruiter3npc");
		npc.setPosition(10, 4);
		npc.initHP(100);
		zone.add(npc);

		// Add a book with the shop offers
		Sign book = new Sign();
		book.setPosition(12, 3);
		book.setText(" -- Buying -- \n steel_boots\t 1000\n golden_boots\t 1500\n shadow_boots\t 2000\n stone_boots\t 2500\n chaos_boots\t 4000");
		book.setEntityClass("book_blue");
		book.setResistance(10);
		zone.add(book);

		Sign book2 = new Sign();
		book2.setPosition(13, 4);
		book2.setText(" -- Buying -- \n golden_helmet\t 3000\n shadow_helmet\t 4000\n horned_golden_helmet 5000\n chaos_helmet\t 6000\n magic_chain_helmet\t 8000\n black_helmet\t 10000");
		book2.setEntityClass("book_red");
		book2.setResistance(10);
		zone.add(book2);
	}
}
