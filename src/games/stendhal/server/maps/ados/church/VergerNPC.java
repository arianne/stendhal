/**
 * 
 */
package games.stendhal.server.maps.ados.church;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * A praying NPC in ados church
 * 
 * @author madmetzger
 */
public class VergerNPC implements ZoneConfigurator {

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
				addOffer("For some people it is helpful to pray...");
				addHelp("shhhhh!");
				addJob("Be quiet!");
				addQuest("Pray or go away!");
			}

			@Override
			protected void onGoodbye(Player player) {
				setDirection(Direction.UP);
			}
			
			
			
		};
		npc.setEntityClass("vergernpc");
		npc.setPosition(29, 14);
		npc.setDirection(Direction.UP);
		npc.initHP(100);
		zone.add(npc);
	}

}
