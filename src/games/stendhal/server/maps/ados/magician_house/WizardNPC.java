package games.stendhal.server.maps.ados.magician_house;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WizardNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildMagicianHouseArea(zone, attributes);
	}

	private void buildMagicianHouseArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Haizen") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(7, 2));
				nodes.add(new Node(7, 4));
				nodes.add(new Node(13, 4));
				nodes.add(new Node(13, 9));
				nodes.add(new Node(9, 9));
				nodes.add(new Node(9, 8));
				nodes.add(new Node(9, 9));
				nodes.add(new Node(2, 9));
				nodes.add(new Node(2, 3));
				nodes.add(new Node(7, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am a wizard who sells #magic #scrolls. Just ask me for an #offer!");
				addHelp("You can take powerful magic with you on your adventures with the aid of my #magic #scrolls!");

				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("scrolls")));

				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING,
				        "I don't have any tasks for you right now. If you need anything from me, just ask.", null);
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("magic", "scroll", "scrolls"),
				        null,
				        ConversationStates.ATTENDING,
				        "I #offer scrolls that help you to travel faster: #'home scrolls' and the #markable #'empty scrolls'. For the more advanced customer, I also have #'summon scrolls'!",
				        null);
				add(ConversationStates.ATTENDING, Arrays.asList("home", "home scroll"), null,
				        ConversationStates.ATTENDING,
				        "Home scrolls take you home immediately, a good way to escape danger!", null);
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("empty", "marked", "empty scroll", "markable", "marked scroll"),
				        null,
				        ConversationStates.ATTENDING,
				        "Empty scrolls are used to mark a position. Those marked scrolls can take you back to that position. They are a little expensive, though.",
				        null);
				add(
				        ConversationStates.ATTENDING,
				        "summon",
				        null,
				        ConversationStates.ATTENDING,
				        "A summon scroll empowers you to summon animals to you; advanced magicians will be able to summon stronger monsters than others. Of course, these scrolls can be dangerous if misused.",
				        null);
				add(
				        ConversationStates.ATTENDING,
				        "maze",
				        null,
				        ConversationStates.ATTENDING,
				        null,
				        new SendToMazeChatAction());

				addGoodbye();
			}
		};

		npc.setEntityClass("wisemannpc");
		npc.setPosition(7, 2);
		npc.initHP(100);
		zone.add(npc);
	}
	
	private class SendToMazeChatAction implements ChatAction {
    	public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
    		Maze maze = new Maze(player.getName() + "_maze", 128, 128);
    		maze.setReturnLocation("int_ados_magician_house", player.getX(), player.getY());
    		StendhalRPZone zone = maze.getZone();
    		SingletonRepository.getRPWorld().addRPZone(zone);
    		player.teleport(zone, maze.getStartPosition().x, maze.getStartPosition().y, Direction.DOWN, player);
    	}
	}
}
