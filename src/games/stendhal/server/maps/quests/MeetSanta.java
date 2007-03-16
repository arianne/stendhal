package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.StandardInteraction;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.pathfinder.Path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * QUEST: Meet Santa anywhere around the World.
 *
 * PARTICIPANTS:
 * - Santa Claus
 *
 * STEPS:
 * - Find Santa
 * - Say hi
 * - Get reward
 *
 * REWARD:
 * - a present which can be opend to obtain a random good reward:
 *   food, money, potions, items, etc...
 *
 * REPETITIONS:
 * - None
 */
public class MeetSanta extends AbstractQuest implements TurnListener {
	private static final String QUEST_SLOT = "meet_santa_07";
	private static Logger logger = Logger.getLogger(MeetSanta.class);
	/** the Santa NPC */
	protected SpeakerNPC santa = null;
	private StendhalRPZone zone = null;
	private ArrayList<StendhalRPZone> zones = null;

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	private SpeakerNPC createSanta() {
		santa = new SpeakerNPC("Santa") {
			@Override
			protected void createPath() {
				// npc does not move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
					new StandardInteraction.QuestCompletedCondition(QUEST_SLOT),
					ConversationStates.ATTENDING, "Hi again.", null);

				add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
					new StandardInteraction.QuestNotCompletedCondition(QUEST_SLOT),
					ConversationStates.ATTENDING, null,
					new SpeakerNPC.ChatAction() {

						@Override
						public void fire(Player player, String text, SpeakerNPC engine) {
							Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("present");
							engine.say("Merry Christmas! I have a present for you.");
							player.equip(item, true);
							player.setQuest(QUEST_SLOT, "done");
						}
				});

				addJob("I am Santa Claus! Where have you been in these years?");
				addGoodbye();
			}
		};
		npcs.add(santa);
		santa.put("class", "santaclausnpc");
		santa.initHP(100);

		// start in int_admin_playground
		zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("int_admin_playground");
		zone.assignRPObjectID(santa);
		santa.set(17, 12);
		zone.addNPC(santa);

		return santa;
	}

	/**
	 * Creates an ArrayList of "outside" zones for Santa.
	 */
	private void listZones() {
		Iterator itr = StendhalRPWorld.get().iterator();
		zones = new ArrayList<StendhalRPZone>();
        while (itr.hasNext()) {
        	StendhalRPZone aZone = (StendhalRPZone) itr.next();
        	String zoneName = aZone.getID().getID();
        	if (zoneName.startsWith("0") && !zoneName.equals("0_nalwor_city") 
        		&& !zoneName.equals("0_orril_castle") 
        		&& !zoneName.equals("0_ados_swamp")
        		&& !zoneName.equals("0_ados_outside_w")
        		&& !zoneName.equals("0_ados_wall_n")) {
        		zones.add(aZone);
        	}
        }
	}

	public void onTurnReached(int currentTurn, String message) {
		// Say bye
		santa.say("Bye.");

		// We make santa to stop speaking to anyone.
		santa.setCurrentState(ConversationStates.IDLE);

		// remove santa from old zone
		zone.remove(santa);

		// Teleport to another random place
		boolean found = false;
		int x = -1;
		int y = -1;
		while (!found) {
			zone = zones.get(Rand.rand(zones.size()));
			x = Rand.rand(zone.getWidth() - 4) + 2;
			y = Rand.rand(zone.getHeight() - 5) + 2;
			if (!zone.collides(x, y) && !zone.collides(x, y + 1)) {
				zone.assignRPObjectID(santa);
				santa.set(x, y);
				santa.setDirection(Direction.RIGHT);

				zone.add(santa);
				found = true;
				logger.info("Placing Santa at " + zone.getID().getID() + " " + x + " " + y);
			} else {
				logger.warn("Cannot place Santa at " + zone.getID().getID() + " " + x + " " + y);
			}
		}

		// try to build a path (but give up after 10 successless tries)
		for (int i = 0; i < 10; i++) {
			int tx = Rand.rand(zone.getWidth() - 4) + 2;
			int ty = Rand.rand(zone.getHeight() - 5) + 2;
			List<Path.Node> path = Path.searchPath(santa, tx, ty);
			int size = path.size();
			if ((path != null) && (size > 1)) {
				// create path back
				for (int j = size - 1; j > 0; j--) {
					path.add(path.get(j));
				}
				logger.info(path);
				santa.setPath(path, true);
				break;
			}
		}

		// Schedule so we are notified again in 5 minutes
		TurnNotifier.get().notifyInTurns(5 * 60 * 3, this, null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		createSanta();
		listZones();
		TurnNotifier.get().notifyInTurns(60, this, null);

		// say something every minute so that can be noticed more easily
		TurnNotifier.get().notifyInTurns(60, new TurnListener() {
			public void onTurnReached(int currentTurn, String message) {
				santa.say("Ho, ho, ho! Merry Christmas!");
				TurnNotifier.get().notifyInTurns(60 * 3, this, null);
			}
		}, null);
	}

}
