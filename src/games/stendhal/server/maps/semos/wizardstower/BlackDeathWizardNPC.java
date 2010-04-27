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
 * Ravashack, the death wizard of the Wizards Tower
 *
 * @see games.stendhal.server.maps.quests.WizardRavashackPlainQuest
 */
public class BlackDeathWizardNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildRavashack(zone);
	}

	private void buildRavashack(final StendhalRPZone zone) {
		final SpeakerNPC ravashack = new SpeakerNPC("Ravashack") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(5, 18));
				nodes.add(new Node(7, 18));
				nodes.add(new Node(7, 15));
				nodes.add(new Node(8, 15));
				nodes.add(new Node(7, 15));
				nodes.add(new Node(7, 20));
				nodes.add(new Node(12, 20));
				nodes.add(new Node(12, 21));
				nodes.add(new Node(9, 21));
				nodes.add(new Node(9, 25));
				nodes.add(new Node(12, 25));
				nodes.add(new Node(9, 25));
				nodes.add(new Node(9, 26));
				nodes.add(new Node(9, 21));
				nodes.add(new Node(2, 21));
				nodes.add(new Node(2, 25));
				nodes.add(new Node(4, 25));
				nodes.add(new Node(4, 27));
				nodes.add(new Node(6, 27));
				nodes.add(new Node(4, 27));
				nodes.add(new Node(4, 28));
				nodes.add(new Node(2, 28));
				nodes.add(new Node(2, 18));
				nodes.add(new Node(7, 18));
				nodes.add(new Node(7, 15));
				nodes.add(new Node(7, 18));
				nodes.add(new Node(5, 18));
				nodes.add(new Node(5, 17));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings Stranger!");
				addHelp("");
				addReply("", "");
				addGoodbye("So long!");

			} //remaining behaviour defined in maps.quests.WizardRavashackPlainQuest
		};

		ravashack.setDescription("You see Ravashack, the mighty and mystical Necromancer.");
		ravashack.setEntityClass("largeblackwizardnpc");
		ravashack.setPosition(5, 17);
		ravashack.initHP(100);
		zone.add(ravashack);
	}
}
