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
 * Malleus, the fire wizard of the Wizards Tower
 *
 * @see games.stendhal.server.maps.quests.RedFireWizardsPlainQuest
 */
public class RedFireWizardNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildMalleus(zone);
	}

	private void buildMalleus(final StendhalRPZone zone) {
		final SpeakerNPC malleus = new SpeakerNPC("Malleus") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(5, 43));
				nodes.add(new Node(5, 42));
				nodes.add(new Node(7, 42));
				nodes.add(new Node(11, 42));
				nodes.add(new Node(9, 42));
				nodes.add(new Node(9, 33));
				nodes.add(new Node(11, 33));
				nodes.add(new Node(9, 33));
				nodes.add(new Node(9, 37));
				nodes.add(new Node(11, 37));
				nodes.add(new Node(9, 37));
				nodes.add(new Node(9, 33));
				nodes.add(new Node(11, 33));
				nodes.add(new Node(9, 33));
				nodes.add(new Node(9, 42));
				nodes.add(new Node(3, 42));
				nodes.add(new Node(3, 41));
				nodes.add(new Node(2, 41));
				nodes.add(new Node(2, 36));
				nodes.add(new Node(6, 36));
				nodes.add(new Node(4, 36));
				nodes.add(new Node(4, 33));
				nodes.add(new Node(2, 33));
				nodes.add(new Node(2, 41));
				nodes.add(new Node(3, 41));
				nodes.add(new Node(3, 43));
				nodes.add(new Node(2, 43));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings Stranger!");
				addHelp("");
				addReply("", "");
				addGoodbye("So long!");

			} //remaining behaviour defined in maps.quests.RedFireWizardsPlainQuest
		};

		malleus.setDescription("You see Malleus, the master of destructive magics.");
		malleus.setEntityClass("reddarkwizardnpc");
		malleus.setPosition(2, 43);
		malleus.initHP(100);
		zone.add(malleus);
	}
}
