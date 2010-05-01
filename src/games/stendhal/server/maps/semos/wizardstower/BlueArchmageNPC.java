package games.stendhal.server.maps.semos.wizardstower;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Erastus, the archmage of the Wizards Tower
 *
 * @see games.stendhal.server.maps.quests.ArchmageErastusQuest
 */
public class BlueArchmageNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildErastus(zone);
	}

	private void buildErastus(final StendhalRPZone zone) {
		final SpeakerNPC erastus = new SpeakerNPC("Erastus") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(21, 37));
				nodes.add(new Node(13, 37));
				nodes.add(new Node(13, 32));
				nodes.add(new Node(22, 32));
				nodes.add(new Node(22, 25));
				nodes.add(new Node(24, 25));
				nodes.add(new Node(22, 25));
				nodes.add(new Node(22, 32));
				nodes.add(new Node(33, 32));
				nodes.add(new Node(32, 32));
				nodes.add(new Node(32, 33));
				nodes.add(new Node(32, 32));
				nodes.add(new Node(22, 32));
				nodes.add(new Node(22, 25));
				nodes.add(new Node(24, 25));
				nodes.add(new Node(20, 25));
				nodes.add(new Node(20, 32));
				nodes.add(new Node(8, 32));
				nodes.add(new Node(11, 32));
				nodes.add(new Node(11, 35));
				nodes.add(new Node(13, 35));
				nodes.add(new Node(13, 37));
				nodes.add(new Node(22, 37));
				nodes.add(new Node(22, 40));
				nodes.add(new Node(26, 40));
				nodes.add(new Node(26, 36));
				nodes.add(new Node(26, 37));
				nodes.add(new Node(28, 37));
				nodes.add(new Node(25, 37));
				nodes.add(new Node(25, 40));
				nodes.add(new Node(22, 40));
				nodes.add(new Node(22, 37));
				nodes.add(new Node(21, 37));
				nodes.add(new Node(21, 36));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings Stranger!");
				addHelp("");
				addReply("", "");
				addGoodbye("So long!");

			} //remaining behaviour defined in maps.quests.ArchmageErastusQuest
		};

		erastus.setDescription("You see Erastus, the grandmaster of all magics.");
		erastus.setEntityClass("blueoldwizardnpc");
		erastus.setPosition(21, 36);
		erastus.initHP(100);
		zone.add(erastus);
	}
}
