package games.stendhal.server.maps.semos.jail;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

/**
 * An elven inmate (original name: Conual). He's just decoration. 
 * 
 * @author hendrik
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class InmateNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Let me out!");
		npc.addGoodbye();
		
		npc.setDescription("You see Conual. He is jailed for ages now. Seems like he really did something badly wrong!");
	}
}
