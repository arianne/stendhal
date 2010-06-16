package games.stendhal.server.maps.semos.city;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Map;

/**
 * An old man (original name: Monogenes) who stands around and gives directions
 * to newcomers. He's the brother of RetireeNPC (original name: Diogenes).
 *
 * @see games.stendhal.server.maps.quests.MeetMonogenes
 * @see games.stendhal.server.maps.quests.HatForMonogenes
 */
public class GreeterNPC implements ZoneConfigurator {

	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}
	
	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Monogenes") {
			@Override
			public void createDialog() {
				addJob("I'm Diogenes' older brother and I don't actually remember what I used to do... I'm retired now.");
				addOffer("I give directions to #buildings in Semos, to newcomers settle in. When I'm in a bad mood I sometimes give misleading directions to amuse myself... hee hee hee! Of course, sometimes I get my wrong directions wrong and they end up being right after all! Ha ha!");
				// All further behaviour is defined in quest classes.
			}
		};
		npc.setPosition(26, 22);
		npc.setEntityClass("oldmannpc");
		npc.setDirection(Direction.LEFT);
		zone.add(npc);
	}

}