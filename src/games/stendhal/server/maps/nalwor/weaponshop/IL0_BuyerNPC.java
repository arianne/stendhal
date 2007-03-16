package games.stendhal.server.maps.nalwor.weaponshop;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Blackboard;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class IL0_BuyerNPC implements ZoneConfigurator {
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
		buildNPC(zone);
	}


	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Elodrin") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(4, 4));
				nodes.add(new Path.Node(7, 4));
				nodes.add(new Path.Node(7, 2));
				nodes.add(new Path.Node(7, 4));
				nodes.add(new Path.Node(4, 4));
				nodes.add(new Path.Node(4, 2));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("*grrr* You dare come in my shop?");
				addJob("I buy weapons. I pay more to elves. Ha!");
				addHelp("I buy rare weapons, ask me for my #offer.");
				add(ConversationStates.ATTENDING,
					"offer",
					null,
					ConversationStates.ATTENDING,
					"Look at the blackboard on the wall to see what I will buy.",
					null);
				add(ConversationStates.ATTENDING,
					ConversationPhrases.QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"You think I'd trust a human with anything important? You're wrong!",
					null);
				addBuyer(new BuyerBehaviour(shops.get("elfbuyrare")),false); //why does this false go here? what is it?
				addGoodbye("Bye - be careful not to annoy the other elves as much");
			}
		};
		npc.setDescription("You see Elodrin, a mean looking elf.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "elfbuyernpc");
		npc.set(4, 4);
		npc.initHP(100);
		zone.addNPC(npc);

	

		// Add a blackboard with the shop offers
		Blackboard board = new Blackboard(false);
		zone.assignRPObjectID(board);
		board.set(3, 1);
		board.setText(shops.toString("elfbuyrare", "-- Buying --"));
		zone.add(board);
	}
}
