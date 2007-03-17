package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPRuleProcessor;
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
 * QUEST: Meet the Easter Bunny anywhere around the World.
 *
 * PARTICIPANTS:
 * - Easter Bunny
 *
 * STEPS:
 * - Find Bunny
 * - Say hi
 * - Get reward
 *
 * REWARD:
 * - a basket which can be opend to obtain a random good reward:
 *   food, money, potions, items, etc...
 *
 * REPETITIONS:
 * - None
 */
public class MeetBunny extends AbstractQuest implements TurnListener {
	private static final String QUEST_SLOT = "meet_bunny_07";
	private static Logger logger = Logger.getLogger(MeetBunny.class);
	/** the Bunny NPC */
	protected SpeakerNPC bunny = null;
	private StendhalRPZone zone = null;
	private ArrayList<StendhalRPZone> zones = null;

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	private SpeakerNPC createbunny() {
		bunny = new SpeakerNPC("Easter Bunny") {
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
					ConversationStates.ATTENDING, "Hi again!", null);

				add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
					new StandardInteraction.QuestNotCompletedCondition(QUEST_SLOT),
					ConversationStates.ATTENDING, null,
					new SpeakerNPC.ChatAction() {

						@Override
						public void fire(Player player, String text, SpeakerNPC engine) {
							Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("basket");
							engine.say("Happy Easter! I have an easter basket for you.");
							player.equip(item, true);
							player.setQuest(QUEST_SLOT, "done");
						}
				});

				addJob("I am the Easter Bunny!");
				addGoodbye("Don't eat too much this Easter! Bye!");
			}
		};
		npcs.add(bunny);
		bunny.put("class", "easterbunnynpc");
		bunny.initHP(100);

		// start in int_admin_playground
		zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("int_admin_playground");
		zone.assignRPObjectID(bunny);
		bunny.set(17, 12);
		zone.add(bunny);

		return bunny;
	}

	/**
	 * Creates an ArrayList of "outside" zones for bunny.
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
		bunny.say("Bye.");

		// We make bunny to stop speaking to anyone.
		bunny.setCurrentState(ConversationStates.IDLE);

		// remove bunny from old zone
		zone.remove(bunny);

		// Teleport to another random place
		boolean found = false;
		int x = -1;
		int y = -1;
		while (!found) {
			zone = zones.get(Rand.rand(zones.size()));
			x = Rand.rand(zone.getWidth() - 4) + 2;
			y = Rand.rand(zone.getHeight() - 5) + 2;
			if (!zone.collides(x, y) && !zone.collides(x, y + 1)) {
				zone.assignRPObjectID(bunny);
				bunny.set(x, y);
				bunny.setDirection(Direction.RIGHT);

				zone.add(bunny);
				StendhalRPRuleProcessor.get().addNPC(bunny);
				found = true;
				logger.info("Placing bunny at " + zone.getID().getID() + " " + x + " " + y);
			} else {
				logger.warn("Cannot place bunny at " + zone.getID().getID() + " " + x + " " + y);
			}
		}

		// try to build a path (but give up after 10 successless tries)
		for (int i = 0; i < 10; i++) {
			int tx = Rand.rand(zone.getWidth() - 4) + 2;
			int ty = Rand.rand(zone.getHeight() - 5) + 2;
			List<Path.Node> path = Path.searchPath(bunny, tx, ty);
			int size = path.size();
			if ((path != null) && (size > 1)) {
				// create path back
				for (int j = size - 1; j > 0; j--) {
					path.add(path.get(j));
				}
				logger.info(path);
				bunny.setPath(path, true);
				break;
			}
		}

		// Schedule so we are notified again in 5 minutes
		TurnNotifier.get().notifyInTurns(5 * 60 * 3, this, null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		createbunny();
		listZones();
		TurnNotifier.get().notifyInTurns(60, this, null);

		// say something every minute so that can be noticed more easily
		TurnNotifier.get().notifyInTurns(60, new TurnListener() {
			public void onTurnReached(int currentTurn, String message) {
				bunny.say("*hop* *hop* *hop* Happy Easter!");
				TurnNotifier.get().notifyInTurns(60 * 3, this, null);
			}
		}, null);
	}

}
