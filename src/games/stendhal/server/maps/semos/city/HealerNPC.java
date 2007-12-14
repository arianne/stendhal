package games.stendhal.server.maps.semos.city;

import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.HealerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * A young lady (original name: Carmen) who heals players without charge.
 */
public class HealerNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(SpeakerNPC npc) {
		npc.addGreeting();
		npc.addJob("My special powers help me to heal wounded people. I also sell potions and antidotes.");
		npc.addHelp("I can #heal you here for free, or you can take one of my prepared medicines with you on your travels; just ask for an #offer.");
		new SellerAdder().addSeller(npc, new SellerBehaviour(
				ShopList.get().get("healing")));
		new HealerAdder().addHealer(npc, 0);
		npc.addGoodbye();
	}
}
