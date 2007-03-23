package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.StandardInteraction;
import games.stendhal.server.entity.npc.TeleporterBehaviour;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;

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
public class MeetSanta extends AbstractQuest {

	private static final String QUEST_SLOT = "meet_santa_07";

	/** the Santa NPC */
	protected SpeakerNPC santa = null;

	private StendhalRPZone zone = null;

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
				        new StandardInteraction.QuestCompletedCondition(QUEST_SLOT), ConversationStates.ATTENDING,
				        "Hi again.", null);

				add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				        new StandardInteraction.QuestNotCompletedCondition(QUEST_SLOT), ConversationStates.ATTENDING,
				        null, new SpeakerNPC.ChatAction() {

					        @Override
					        public void fire(Player player, String text, SpeakerNPC engine) {
						        Item item = StendhalRPWorld.get().getRuleManager().getEntityManager()
						                .getItem("present");
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
		zone.add(santa);

		return santa;
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		createSanta();
		new TeleporterBehaviour(santa, "Ho, ho, ho! Merry Christmas!");
	}

}
