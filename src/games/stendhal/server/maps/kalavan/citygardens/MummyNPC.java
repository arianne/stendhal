package games.stendhal.server.maps.kalavan.citygardens;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds the mother of Annie Jones
 *
 * @author kymara
 */
public class MummyNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}


	private void buildNPC(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC mummyNPC = new SpeakerNPC("Mrs Jones") {
			@Override
			protected void createPath() {
			    // does not move
				setPath(null);
			}
			@Override
			protected void createDialog() {

				// greeting in maps/quests/IcecreamForAnnie.java
				addOffer("I can't, I'm busy watching my daughter.");
				addQuest("Nothing, thank you.");
				addJob("I'm a mother.");
				addHelp("I'll help if I can, but I have to watch my daughter.");
				addGoodbye("Bye for now.");
			}
		};

		mummyNPC.setDescription("You see a woman, resting on a bench.");
		mummyNPC.setEntityClass("woman_000_npc");
		mummyNPC.setPosition(53, 88);
		mummyNPC.initHP(100);
		zone.add(mummyNPC);
	}
}