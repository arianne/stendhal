/**
 * 
 */
package games.stendhal.server.maps.ados.church;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * A praying NPC in ados church
 * 
 * @author madmetzger
 */
public class PrayingNPC implements ZoneConfigurator {

	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Simon") {

			@Override
			protected void createDialog() {
				addGreeting("*whispering* Hello");
				addGoodbye("*whispering* Amen");
				addOffer("For some peoble it is helpful to pray...");
				addHelp("shhhhh!");
				addJob("Be quiet!");
				addQuest("Pray or go away!");
			}
			
		};
		npc.setEntityClass("prayernpc");
		npc.setPosition(29, 14);
		npc.initHP(100);
		zone.add(npc);
	}

}
