package games.stendhal.server.maps.fado.great_cave;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Map;

/**
 * Builds a Banker NPC who controls access to the magic bank  
 *
 * @author kymara
 */
public class BankerNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Javier X") {

			@Override
			protected void createPath() {
				// doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
			        addGreeting("Greetings.");
				addJob("I control access to the bank. My spells ensure people cannot simply come and go as they please. You will be allowed access when kymara has coded it.");
				addReply("magic","Have you not heard of magic? It is what makes the grass grow here. Perhaps in time your kind will learn how to use this fine art.");
				addReply("offer","I would have thought that the offer of these #fiscal services is enough for you.");
				addReply("fiscal","You do not understand the meaning of the word? You should spend more time in libraries, I hear the one in Ados is excellent.");
				addHelp("This bank is suffused with #magic, and as such you may access any vault you own. There will be a fee to pay for this privilege, as we are not a charity.");
				addQuest("You may not use this bank if you have not gained the right to use the chests at Nalwor, nor if you have not earned the trust of a young woman.");
 				addGoodbye("Goodbye.");
			}
		};
		npc.setDescription("You see a wizard who you should be afraid to mess with.");
		zone.assignRPObjectID(npc);
		npc.setEntityClass("brownwizardnpc");
		npc.setPosition(15, 10);
		npc.initHP(100);
		zone.add(npc);
	}
}
