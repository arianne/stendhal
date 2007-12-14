package games.stendhal.server.maps.fado.weaponshop;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds an NPC to buy gems and gold and sell engagement ring
 * <p>
 * He is also the NPC who can fix a broken emerald ring
 * (../../quests/Ringmaker.java)
 * <p>
 * He is also the NPC who casts the wedding ring (../../quests/Marriage.java)
 *
 * @author kymara
 */
public class RingSmithNPC implements ZoneConfigurator {
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
				addGreeting("Hi, can I help you?");
				addJob("I work with #gold, to fix and make jewellery.");
				addOffer("I sell diamond engagement rings which I make myself. I also buy gems and gold, see the red catalogue on the table.");
				addReply("request",
						"Just ask about the #task if you want me to make a wedding ring for someone.");
				addReply(
						"gold",
						"It's cast from gold nuggets which you can pan for on Or'ril river. I don't cast it myself, but a smith in Ados does.");
				addHelp("You can sell weapons to Yorphin Baos over there. I #trade in precious items and I can also make a wedding ring as a special #request.");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("sellrings")), false);
				new BuyerAdder().add(this, new BuyerBehaviour(shops.get("buyprecious")), false);
				addGoodbye("Bye, my friend.");
			}
		};

		npc.setDescription("You see Ognir, a friendly bearded chap.");
		npc.setEntityClass("ringsmithnpc");
		npc.setPosition(18, 8);
		npc.initHP(100);
		zone.add(npc);
	}
}
