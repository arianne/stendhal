package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.StandardInteraction;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
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
 * - a present which can be opend to obtain a random good reward:
 *   food, money, potions, items, etc...
 *
 * REPETITIONS:
 * - None
 */
public class MeetSanta extends AbstractQuest implements TurnListener {
	private static final String QUEST_SLOT = "meet_santa_06";
	private SpeakerNPC santa = null;

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
				add(ConversationStates.IDLE, SpeakerNPC.GREETING_MESSAGES,
					new StandardInteraction.QuestCompletedCondition(QUEST_SLOT),
					ConversationStates.ATTENDING, "Hi again.", null);

				add(ConversationStates.IDLE, SpeakerNPC.GREETING_MESSAGES,
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
		santa.set(17, 12);
		santa.initHP(100);

		// start in int_admin_playground
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("int_admin_playground");
		zone.assignRPObjectID(santa);
		zone.addNPC(santa);

		return santa;
	}

	public void onTurnReached(int currentTurn, String message) {
		// Say bye
		santa.say("Bye.");

		// Teleport to another random place


		// Schedule so we are notified again in 5 minutes
		TurnNotifier.get().notifyInTurns(5*60*3, this, null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		createSanta();
		TurnNotifier.get().notifyInTurns(6, this, null);
	}

}
