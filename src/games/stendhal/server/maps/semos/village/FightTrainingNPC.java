/* $Id$ */
package games.stendhal.server.maps.semos.village;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;


import java.util.Map;



public class FightTrainingNPC implements ZoneConfigurator {

		/**
		 * Configure a zone.
		 *
		 * @param	zone		The zone to be configured.
		 * @param	attributes	Configuration attributes.
		 */
		@Override
		public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
			buildNPC(zone);
		}

		 private void buildNPC(final StendhalRPZone zone) {
	    	    final SpeakerNPC npc = new SpeakerNPC("Rochar-Zith") {
	            @Override
				protected void createPath() {
	                setPath(null);
	            }

	            @Override
				protected void createDialog() {
	                addGreeting("Hello there! how can my rusty sword-arm be of service?");
	                addJob("I am a combat trainer that can show you how to easily defeat your enemies.");
	                addHelp("Ask me to #train you and i will show you some secrets in regards to combat.");
	                addReply("train","During #combat, you and your opponent keep attacking each other until one dies.");
	                addReply("combat","In combat, 2 basic stats apply, #rate and #damage.");
	                addReply("damage","The higher the DAMAGE of your weapon, the more HP your opponent will lose with every hit.");
	                addReply("rate","The higher the RATE of your weapon, the faster you will attack."); 
	                addGoodbye("Until we meet again!");
	            }
	    	  };
	    	  
	        npc.setEntityClass("jailedbarbariannpc");
	        npc.setDescription("You see Rochar-Zith, firmly standing there, gazing at the horizon.");
	        npc.setPosition(23, 30);
	        npc.initHP(100);
	        zone.add(npc);   
	    }
	}
