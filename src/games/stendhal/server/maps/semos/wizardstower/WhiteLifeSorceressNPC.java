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
 * Elana, the life wizard of the Wizards Tower
 *
 * @see games.stendhal.server.maps.quests.WizardElanaPlainQuest
 */
public class WhiteLifeSorceressNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildElana(zone);
	}

	private void buildElana(final StendhalRPZone zone) {
		final SpeakerNPC elana = new SpeakerNPC("Elana") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(39, 18));
				nodes.add(new Node(39, 17));
				nodes.add(new Node(35, 17));
				nodes.add(new Node(35, 15));
				nodes.add(new Node(34, 15));
				nodes.add(new Node(35, 15));
				nodes.add(new Node(35, 20));
				nodes.add(new Node(33, 20));
				nodes.add(new Node(33, 25));
				nodes.add(new Node(32, 25));
				nodes.add(new Node(32, 27));
				nodes.add(new Node(32, 25));
				nodes.add(new Node(30, 25));
				nodes.add(new Node(32, 25));
				nodes.add(new Node(32, 27));
				nodes.add(new Node(32, 25));
				nodes.add(new Node(33, 25));
				nodes.add(new Node(33, 21));
				nodes.add(new Node(30, 21));
				nodes.add(new Node(40, 21));
				nodes.add(new Node(40, 29));
				nodes.add(new Node(39, 29));
				nodes.add(new Node(39, 27));
				nodes.add(new Node(36, 27));
				nodes.add(new Node(38, 27));
				nodes.add(new Node(38, 25));
				nodes.add(new Node(40, 25));
				nodes.add(new Node(40, 20));
				nodes.add(new Node(39, 20));
				nodes.add(new Node(39, 18));
				nodes.add(new Node(40, 18));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings Stranger!");
				addHelp("");
				addReply("", "");
				addGoodbye("So long!");

			} //remaining behaviour defined in maps.quests.WizardElanaPlainQuest
		};

		elana.setDescription("You see Elana, the devinely enchantress of life.");
		elana.setEntityClass("whitesorceressnpc");
		elana.setPosition(40, 18);
		elana.initHP(100);
		zone.add(elana);
	}
}
