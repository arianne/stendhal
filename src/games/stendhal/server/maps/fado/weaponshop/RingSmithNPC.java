package games.stendhal.server.maps.fado.weaponshop;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.BuyerBehaviour;
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

/**
 * Builds an NPC to buy gems and gold and sell engagement ring
 * He is also the NPC who can fix a broken emerald ring (../../quests/Ringmaker.java)
 * He is also the NPC who casts the wedding ring  (../../quests/Marriage.java)
 *
 * @author kymara
 */
public class RingSmithNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	private ShopList shops = ShopList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Ognir") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(18, 8));
				nodes.add(new Node(15, 8));
				nodes.add(new Node(15, 10));
				nodes.add(new Node(16, 10));
				nodes.add(new Node(16, 14));
				nodes.add(new Node(18, 14));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("I work with #gold, to fix and make jewellery.");
				addReply("offer","I sell diamond engagement rings which I make myself. I also buy gems and gold, see the red catalogue on the table.");
				addReply("request","Just ask about the #task if you want me to make a wedding ring for someone.");
				addReply("gold","It's cast from gold nuggets which you can pan for on Or'ril river. I don't cast it myself, but a smith in Ados does.");
				addHelp("You can sell weapons to Yorphin Baos over there. I buy and sell items and I can also make a wedding ring as a special #request.");
				addSeller(new SellerBehaviour(shops.get("sellrings")),false);
				addBuyer(new BuyerBehaviour(shops.get("buyprecious")),false);
				addGoodbye("Bye, my friend.");
			}
		};
		npc.setDescription("You see Ognir, a friendly bearded chap.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "ringsmithnpc");
		npc.set(18, 8);
		npc.initHP(100);
		zone.add(npc);

		// Add a red book with the things Ognir buys
		Sign book = new Sign();
		zone.assignRPObjectID(book);
		book.set(12, 14);
		book.setText(shops.toString("buyprecious", "-- Buying --"));
		book.setClass("book_red");
		zone.add(book);
	}
}
