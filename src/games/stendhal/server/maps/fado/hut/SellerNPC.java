package games.stendhal.server.maps.fado.hut;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

/**
 * A lady wizard who sells potions and antidotes. Original name: Sarzina
 */
public class SellerNPC implements ZoneConfigurator {

	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}
	
	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Sarzina") {
			
			@Override
			public void createDialog() {
				addGreeting();
				addJob("I make potions and antidotes, to #offer to warriors.");
				addHelp("You can take one of my prepared medicines with you on your travels; just ask for an #offer.");
				addGoodbye();
			}

			/* (non-Javadoc)
			 * @see games.stendhal.server.entity.npc.SpeakerNPC#onGoodbye(games.stendhal.server.entity.player.Player)
			 */
			@Override
			protected void onGoodbye(Player player) {
				setDirection(Direction.DOWN);
			}
			
		};
		new SellerAdder().addSeller(npc, new SellerBehaviour(SingletonRepository.getShopList().get("superhealing")));
		npc.setPosition(3, 5);
		npc.setEntityClass("wizardwomannpc");
		npc.setDirection(Direction.DOWN);
		zone.add(npc);
	}

}