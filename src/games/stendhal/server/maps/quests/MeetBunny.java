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

/**
 * QUEST: Meet the Easter Bunny anywhere around the World.
 * 
 * PARTICIPANTS: - Easter Bunny
 * 
 * STEPS: - Find Bunny - Say hi - Get reward
 * 
 * REWARD: - a basket which can be opend to obtain a random good reward: food,
 * money, potions, items, etc...
 * 
 * REPETITIONS: - None
 */
public class MeetBunny extends AbstractQuest {
	private static final String QUEST_SLOT = "meet_bunny_07";

	/** the Bunny NPC */
	protected SpeakerNPC bunny;

	private StendhalRPZone zone;

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private SpeakerNPC createbunny() {
		bunny = new SpeakerNPC("Easter Bunny") {
			@Override
			protected void createPath() {
				// npc does not move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new StandardInteraction.QuestCompletedCondition(
								QUEST_SLOT), ConversationStates.ATTENDING,
						"Hi again!", null);

				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new StandardInteraction.QuestNotCompletedCondition(
								QUEST_SLOT), ConversationStates.ATTENDING,
						null, new SpeakerNPC.ChatAction() {

							@Override
							public void fire(Player player, String text,
									SpeakerNPC engine) {
								Item item = StendhalRPWorld.get()
										.getRuleManager().getEntityManager()
										.getItem("basket");
								engine
										.say("Happy Easter! I have an easter basket for you.");
								player.equip(item, true);
								player.setQuest(QUEST_SLOT, "done");
							}
						});

				addJob("I am the Easter Bunny!");
				addGoodbye("Don't eat too much this Easter! Bye!");
			}
		};
		npcs.add(bunny);
		bunny.setEntityClass("easterbunnynpc");
		bunny.initHP(100);

		// start in int_admin_playground
		zone = StendhalRPWorld.get().getZone("int_admin_playground");
		zone.assignRPObjectID(bunny);
		bunny.setPosition(17, 13);
		zone.add(bunny);

		return bunny;
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		createbunny();
		new TeleporterBehaviour(bunny, "*hop* *hop* *hop* Happy Easter!");
	}
}
