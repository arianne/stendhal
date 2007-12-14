package games.stendhal.server.maps.nalwor.weaponshop;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
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
 * Builds an NPC to buy previously un bought weapons.
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
		SpeakerNPC npc = new SpeakerNPC("Elodrin") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(4, 5));
				nodes.add(new Node(7, 5));
				nodes.add(new Node(7, 3));
				nodes.add(new Node(7, 5));
				nodes.add(new Node(4, 5));
				nodes.add(new Node(4, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("*grrr* You dare come in my shop?");
				addJob("I buy weapons. I pay more to elves. Ha!");
				addHelp("I #trade rare weapons.");
				addOffer("Look at the blackboard on the wall to see what I will buy.");
				addQuest("You think I'd trust a human with anything important? You're wrong!");
				new BuyerAdder().add(this, new BuyerBehaviour(
						shops.get("elfbuyrare")), false);
				addGoodbye("Bye - be careful not to annoy the other elves as much.");
			}
		};

		npc.setDescription("You see Elodrin, a mean looking elf.");
		npc.setEntityClass("elfbuyernpc");
		npc.setPosition(4, 5);
		npc.initHP(100);
		zone.add(npc);
	}
}
