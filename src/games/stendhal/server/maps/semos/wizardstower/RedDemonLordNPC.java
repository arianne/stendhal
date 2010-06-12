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
 * Xaruhwaiyz, the demon lord
 *
 * @see games.stendhal.server.maps.quests.WizardMalleusPlainQuest
 */
public class RedDemonLordNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildDemonlord(zone);
	}

	private void buildDemonlord(final StendhalRPZone zone) {
		final SpeakerNPC demonlord = new SpeakerNPC("Xaruhwaiyz") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("HUMAN! Who dare to break in my throne room?!");
				addHelp("");
				addReply("", "");
				addGoodbye("Flee, human!");

			} //remaining behaviour defined in maps.quests.WizardMalleusPlainQuest
		};

		demonlord.setDescription("You see Xaruhwaiyz the demon lord");
		demonlord.setEntityClass("reddemonnpc");
		demonlord.setPosition(15, 4);
		demonlord.initHP(100);
		zone.add(demonlord);
	}
}
