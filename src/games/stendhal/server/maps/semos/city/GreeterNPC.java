package games.stendhal.server.maps.semos.city;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

/**
 * An old man (original name: Monogenes) who stands around and gives directions
 * to newcomers. He's the brother of RetireeNPC (original name: Diogenes).
 *
 * @see games.stendhal.server.maps.quests.MeetMonogenes
 * @see games.stendhal.server.maps.quests.HatForMonogenes
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class GreeterNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addJob("I'm Diogenes' older brother and I don't actually remember what I used to do... I'm retired now.");
		npc.addOffer("I give directions to #buildings in Semos, to newcomers settle in. When I'm in a bad mood I sometimes give misleading directions to amuse myself... hee hee hee! Of course, sometimes I get my wrong directions wrong and they end up being right after all! Ha ha!");
		// All further behaviour is defined in quest classes.
	}
}
