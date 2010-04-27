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
 * Jaer, the air wizard of the Wizards Tower
 *
 * @see games.stendhal.server.maps.quests.WizardJaerPlainQuest
 */
public class OrientalAirWizardNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildJaer(zone);
	}

	private void buildJaer(final StendhalRPZone zone) {
		final SpeakerNPC jaer = new SpeakerNPC("Jaer") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(40, 44));
				nodes.add(new Node(37, 44));
				nodes.add(new Node(37, 42));
				nodes.add(new Node(33, 42));
				nodes.add(new Node(33, 37));
				nodes.add(new Node(31, 37));
				nodes.add(new Node(31, 36));
				nodes.add(new Node(33, 36));
				nodes.add(new Node(33, 33));
				nodes.add(new Node(31, 33));
				nodes.add(new Node(33, 33));
				nodes.add(new Node(33, 36));
				nodes.add(new Node(31, 36));
				nodes.add(new Node(31, 37));
				nodes.add(new Node(31, 35));
				nodes.add(new Node(33, 35));
				nodes.add(new Node(33, 33));
				nodes.add(new Node(31, 33));
				nodes.add(new Node(33, 33));
				nodes.add(new Node(33, 42));
				nodes.add(new Node(31, 42));
				nodes.add(new Node(39, 42));
				nodes.add(new Node(39, 41));
				nodes.add(new Node(40, 41));
				nodes.add(new Node(40, 34));
				nodes.add(new Node(38, 34));
				nodes.add(new Node(38, 33));
				nodes.add(new Node(40, 33));
				nodes.add(new Node(40, 41));
				nodes.add(new Node(41, 41));
				nodes.add(new Node(41, 43));
				nodes.add(new Node(40, 43));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings Stranger!");
				addHelp("");
				addReply("", "");
				addGoodbye("So long!");

			} //remaining behaviour defined in maps.quests.WizardJaerPlainQuest
		};

		jaer.setDescription("You see Jaer, the master of illusion.");
		jaer.setEntityClass("orientalwizardnpc");
		jaer.setPosition(40, 43);
		jaer.initHP(100);
		zone.add(jaer);
	}
}
