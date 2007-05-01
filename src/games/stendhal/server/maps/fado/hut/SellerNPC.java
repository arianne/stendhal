package games.stendhal.server.maps.fado.hut;

import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

/**
 * A lady wizard who sells potions and antidotes. Original name: Sarzina
 */
public class SellerNPC extends SpeakerNPCFactory {

	@Override
	protected void createDialog(SpeakerNPC npc) {
		npc.addGreeting();
		npc.addJob("I make potions and antidotes, to #offer to warriors.");
		npc.addHelp("You can take one of my prepared medicines with you on your travels; just ask for an #offer.");
		npc.addSeller(new SellerBehaviour(ShopList.get().get("superhealing")));
		npc.addGoodbye();
	}
}
