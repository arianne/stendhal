// $Id$
package games.stendhal.server.script;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Fire;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.StandardInteraction;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.List;


/**
 * end of the world
 */
public class Cataclysm extends ScriptImpl {

	@Override
    public void execute(Player admin, List<String> args) {
	    super.execute(admin, args);
	    
	    reduceNPCsHP();
	    addGreeting();
	    setOnFire();
    }

	private void reduceNPCsHP() {
	    StendhalRPZone semos = (StendhalRPZone) StendhalRPWorld.get().getRPZone("0_semos_city");
	    
	    for (NPC npc : semos.getNPCList()) {
	    	npc.setHP(Rand.rand(30) + 10);
	    }
    }
	
	private void addGreeting() {
	    StendhalRPZone semos = (StendhalRPZone) StendhalRPWorld.get().getRPZone("0_semos_city");
	    
	    for (NPC npc : semos.getNPCList()) {
	    	if (npc instanceof SpeakerNPC) {
	    		SpeakerNPC speakerNPC = (SpeakerNPC) npc;
	    		speakerNPC.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
	    				new StandardInteraction.AllwaysTrue(), ConversationStates.ATTENDING, "Help, Help", null);
	    		
	    	}
	    }
    }

	private void createFire(StendhalRPZone zone, int x, int y, int width, int height) {
		Fire fire = new Fire(width, height);
		zone.assignRPObjectID(fire);
		fire.set(x, y);
		zone.add(fire);
	}
	
	private void setOnFire() {
	    StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("0_semos_city");
	    createFire(zone, 10, 10, 1, 1);
	    createFire(zone, 15, 15, 2, 1);
	    createFire(zone, 20, 20, 3, 1);
	    createFire(zone, 25, 25, 1, 2);
	    createFire(zone, 30, 30, 1, 3);
	    createFire(zone, 35, 35, 2, 2);
	    createFire(zone, 40, 40, 3, 3);
	    createFire(zone, 45, 45, 4, 4);
	}
	
	// TODO: remove HealerBehaviour from Carmen
	// TODO: Change Greetings of the SpeakerNPCs in Semos
	// TODO: Try to use a animated entity for the fire instead of adding it to the map.
	
}
