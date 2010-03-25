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
	
	// The default is 90 so make ours half this
	private static final int TIME_OUT = 45;

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
				// Greet players who have a basket but go straight back to idle to give others a chance
				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new QuestCompletedCondition(QUEST_SLOT),
						ConversationStates.IDLE,
						"Hi again! Don't eat too much this Easter!", null);

				final List<ChatAction> reward = new LinkedList<ChatAction>();
				reward.add(new EquipItemAction("basket"));
				reward.add(new SetQuestAction(QUEST_SLOT, "done"));

				// Give unmet players a basket
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
		
		bunny.setEntityClass("easterbunnynpc");
		bunny.initHP(100);
		// times out twice as fast as normal NPCs
		bunny.setPlayerChatTimeout(TIME_OUT); 
		
		// start in int_admin_playground
		zone = SingletonRepository.getRPWorld().getZone("int_admin_playground");
		bunny.setPosition(17, 13);
		zone.add(bunny);
		// Do not add bunny to NPC list until all is known to be OK
		npcs.add(bunny);

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
