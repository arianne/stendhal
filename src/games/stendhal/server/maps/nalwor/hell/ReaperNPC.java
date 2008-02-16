package games.stendhal.server.maps.nalwor.hell;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Map;

/**
 * Builds the reaper in hell.
 * Remaining behaviour will be in games.stendhal.server.maps.quests.SolveRiddles
 * @author kymara
 */
public class ReaperNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Grim Reaper") {

			@Override
			protected void createPath() {
				// doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("If you seek to #leave this place you must solve a #riddle");
				addReply("riddle", "I will pose a brain teaser for you, if you wish to #leave. Of course, you can rot in hell, if you so desire ... ");
				addReply("leave", "A pity for you, I do not have my book of riddles with me! You will have to ask my mirror to let you leave. But it will hurt...");
				// Remaining behaviour will be in games.stendhal.server.maps.quests.SolveRiddles
				addJob("I harvest the souls of the living.");
				addHelp("I hold the keys to the gates of hell, should you wish to #leave");
				addOffer("Unless you wish me to take your soul ... ");
				addGoodbye("The old order of things has passed away ... ");
			}
		};
		npc.setEntityClass("grim_reaper_npc");
		npc.setPosition(63, 76);
		npc.initHP(100);
		zone.add(npc);
	}
}
