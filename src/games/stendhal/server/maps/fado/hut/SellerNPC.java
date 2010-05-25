package games.stendhal.server.maps.fado.hut;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * A lady wizard who sells potions and antidotes. Original name: Sarzina
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class SellerNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting();
		npc.addJob("I make potions and antidotes, to #offer to warriors.");
		npc.addHelp("You can take one of my prepared medicines with you on your travels; just ask for an #offer.");
		new SellerAdder().addSeller(npc, new SellerBehaviour(SingletonRepository.getShopList().get("superhealing")));
		npc.addGoodbye();
	}
}
