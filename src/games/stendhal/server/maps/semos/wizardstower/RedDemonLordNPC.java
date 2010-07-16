package games.stendhal.server.maps.semos.wizardstower;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

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
				addGreeting("HUMAN! Who dares to enter my throne room?!");
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
