package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Meet Santa anywhere around the World.
 * PARTICIPANTS:
 * - Santa Claus
 *
 * STEPS:
 * - Find Santa
 * - Say hi
 * - Get reward
 *
 * REWARD:
 * - Any random good reward: food, money, potions, items, etc...
 * 
 * REPETITIONS:
 * - None
 */

public class MeetSanta extends AbstractQuest implements TurnListener {
	private static final String QUEST_SLOT = "meet_santa_06";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	private SpeakerNPC createSanta() {
		SpeakerNPC npc = npcs.get("Santa");
		if(npc!=null) {
			return npc;
		}
		
		SpeakerNPC santa = new SpeakerNPC("Santa") {
			@Override
			protected void createPath() {
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am Santa Claus! Where have you been in these years?");
				addGoodbye();
			}
		};
		npcs.add(santa);
		santa.put("class", "santaclausnpc");
		santa.set(17, 12);
		santa.initHP(100);
		
		return santa;
	}

	public void onTurnReached(int currentTurn, String message) {
		/* Say bye */
		/* Teleport to another random place */ 
		/* Schedule so we are notified again in X turns */
	}


}
