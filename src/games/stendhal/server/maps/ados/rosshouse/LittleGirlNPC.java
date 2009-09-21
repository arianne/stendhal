package games.stendhal.server.maps.ados.rosshouse;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Creates a normal version of Susi in the ross house.
 */
public class LittleGirlNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		createGirlNPC(zone);
	}

	private void createGirlNPC(final StendhalRPZone zone) {
		
		if (System.getProperty("stendhal.minetown") != null) {
			return;
		}

		final SpeakerNPC npc = new SpeakerNPC("Susi") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 7));
				nodes.add(new Node(5, 7));
				nodes.add(new Node(5, 3));
				nodes.add(new Node(5, 8));
				nodes.add(new Node(10, 8));
				nodes.add(new Node(10, 12));
				nodes.add(new Node(12, 12));
				nodes.add(new Node(9, 12));
				nodes.add(new Node(9, 11));
				nodes.add(new Node(7, 11));
				nodes.add(new Node(7, 7));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Hello. Daddy must have left the house door open again. He's always doing that.");
				addJob("I am just a little girl.");
				addGoodbye("Have fun!");
				addReply("debuggera", "She is my crazy twin sister.");
				addQuest("I might see you some time at the #Semos #Mine #Town #Revival #Weeks.");
				addOffer("I can offer you my #friendship.");

				// Revival Weeks
				add(
					ConversationStates.ATTENDING,
					Arrays.asList("Semos", "Mine", "Town", "Revival", "Weeks"),
					ConversationStates.ATTENDING,
					"During the Revival Weeks we #celebrate the old and now mostly dead Semos Mine Town. "
					+ "The party was cancelled one year because the people of Ados were searching for me after I got lost. "
					+ "I was found and now I am safe.",
					null);
				add(
					ConversationStates.ATTENDING,
					Arrays.asList("celebrate", "celebration", "party"),
					null,
					ConversationStates.ATTENDING,
					"You can get a costume or try to solve a difficult #puzzle in one of the houses in the mine town. It's great!",
					null);
				
				addReply("puzzle", "Gamblos has nine tokens on the floor arranged into a triangle with the point downwards."
						 + " The puzzle is to try to make it into a triangle pointing upwards by moving only three tokens!"
						 + " Just remember, you must stand next to the token to move it.");
				
				// friends
				add(ConversationStates.ATTENDING, Arrays.asList("friend", "friends", "friendship"),
					new QuestInStateCondition("susi", "friends"), 
					ConversationStates.ATTENDING,
					"We are friends.", null);

				add(ConversationStates.ATTENDING,
				    Arrays.asList("friend", "friends", "friendship"),
					new QuestNotInStateCondition("susi", "friends"),
					ConversationStates.INFORMATION_1,
					"Please repeat:\r\n                        \"A circle is round,\"",
					null);
				add(ConversationStates.INFORMATION_1, Arrays.asList(
					"A circle is round,", "A circle is round"), null,
					ConversationStates.INFORMATION_2, "\"it has no end.\"",
					null);
				add(ConversationStates.INFORMATION_2, Arrays.asList(
					"it has no end.", "it has no end"), null,
					ConversationStates.INFORMATION_3,
					"\"That's how long,\"", null);
				add(ConversationStates.INFORMATION_3, Arrays.asList(
					"That's how long,", "That's how long",
					"Thats how long,", "Thats how long"), null,
					ConversationStates.INFORMATION_4,
					"\"I will be your friend.\"", null);
				add(ConversationStates.INFORMATION_4, Arrays.asList(
					"I will be your friend.", "I will be your friend"),
					null, ConversationStates.ATTENDING,
					"Yay! We are friends now.",
					new SetQuestAction("susi", "friends"));

				// help
				add(
					ConversationStates.ATTENDING,
					ConversationPhrases.HELP_MESSAGES,
					new QuestInStateCondition("susi", "friends"),
					ConversationStates.ATTENDING,
					"I have made a lot of friends during the #Semos #Mine #Town #Revival #Weeks.",
					null);
				add(ConversationStates.ATTENDING,
					ConversationPhrases.HELP_MESSAGES,
					new QuestNotInStateCondition("susi", "friends"),
					ConversationStates.ATTENDING, "I need a #friend.", null);
			}
		};

		npc.setOutfit(new Outfit(04, 07, 32, 13));
		npc.setPosition(3, 7);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}

}
