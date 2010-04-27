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
				nodes.add(new Node(20, 25));
				nodes.add(new Node(20, 32));
				nodes.add(new Node(22, 32));
				nodes.add(new Node(22, 27));
				nodes.add(new Node(25, 27));
				nodes.add(new Node(19, 27));
				nodes.add(new Node(19, 25));
				nodes.add(new Node(25, 25));
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
		erastus.setPosition(25, 25);
		erastus.initHP(100);
		zone.add(erastus);
	}
}
