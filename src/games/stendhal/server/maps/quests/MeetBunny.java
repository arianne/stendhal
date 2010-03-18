package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.TeleporterBehaviour;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;

import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Meet the Easter Bunny anywhere around the World.
 * <p>
 *
 * PARTICIPANTS:<ul><li> Easter Bunny</ul>
 *
 * STEPS: <ul><li> Find Bunny <li> Say hi <li> Get reward </ul>
 *
 * REWARD: <ul><li> a basket which can be opened to obtain a random good reward: food,
 * money, potions, items, etc...</ul>
 *
 * REPETITIONS: None
 */
public class MeetBunny extends AbstractQuest {
	private static final String QUEST_SLOT = "meet_bunny_10";

	/** the Bunny NPC. */
	protected SpeakerNPC bunny;

	private StendhalRPZone zone;


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
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
						new QuestCompletedCondition(QUEST_SLOT),
						ConversationStates.ATTENDING,
						"Hi again!", null);

				final List<ChatAction> reward = new LinkedList<ChatAction>();
				reward.add(new EquipItemAction("basket"));
				reward.add(new SetQuestAction(QUEST_SLOT, "done"));

				add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new QuestNotCompletedCondition(QUEST_SLOT),
					ConversationStates.ATTENDING,
					"Happy Easter! I have an easter basket for you.",
					new MultipleActions(reward));
				addQuest("Just be kind and loving this Easter!");
				addOffer("I give easter baskets to my new friends, every Easter!");
				addHelp("You can meet me every Easter!");
				addJob("I am the Easter Bunny!");
				addGoodbye("Don't eat too much this Easter! Bye!");
			}
		};
		npcs.add(bunny);
		bunny.setEntityClass("easterbunnynpc");
		bunny.initHP(100);

		// start in int_admin_playground
		zone = SingletonRepository.getRPWorld().getZone("int_admin_playground");
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

	@Override
	public String getName() {
		return "MeetBunny";
	}
}
