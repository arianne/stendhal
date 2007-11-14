package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.TeleporterBehaviour;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Meet Santa anywhere around the World.
 *
 * PARTICIPANTS: - Santa Claus
 *
 * STEPS: - Find Santa - Say hi - Get reward - Get hat
 *
 * REWARD: - a present which can be opened to obtain a random good reward: food,
 * money, potions, items, etc...
 *
 * REPETITIONS: - None
 */
public class MeetSanta extends AbstractQuest {
	private static final String QUEST_SLOT = "meet_santa_07";

	/** the Santa NPC */
	protected SpeakerNPC santa;

	private StendhalRPZone zone;

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private SpeakerNPC createSanta() {
		santa = new SpeakerNPC("Santa") {
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
					"Hi again.", null);

				List<SpeakerNPC.ChatAction> reward = new LinkedList<SpeakerNPC.ChatAction>();
				reward.add(new EquipItemAction("present"));
				reward.add(new SetQuestAction(QUEST_SLOT, "done"));
				reward.add(new ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
					    /*sorry but I (kymara) don't know how to make a proper thing
					     *out of this like for the other rewards. Hope this is ok*/

				        // fetch old outfit as we want to know the current hair
				        Outfit oldoutfit = player.getOutfit();
					// all santa hat sprites are at 50 + current hair
					int hatnumber = oldoutfit.getHair() + 50;
					// the new outfit only changes the hair, rest is null
				        Outfit newOutfit = new Outfit(hatnumber, null, null, null);
					    //put it on, and store old outfit.
				        player.setOutfit(newOutfit.putOver(oldoutfit), true);
					}
				}
					   );
				add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new QuestNotCompletedCondition(QUEST_SLOT),
					ConversationStates.ATTENDING,
					"Merry Christmas! I have a present and a hat for you.",
					new MultipleActions(reward));

				addJob("I am Santa Claus! Where have you been in these years?");
				addGoodbye("Good bye, and remember to behave if you want a present next year!");
			}
		};
		santa.setEntityClass("santaclausnpc");
		santa.initHP(100);

		// start in int_admin_playground
		zone = StendhalRPWorld.get().getZone("int_admin_playground");
		santa.setPosition(17, 13);
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
