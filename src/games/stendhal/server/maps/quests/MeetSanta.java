package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.LoginNotifier;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.TeleporterBehaviour;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Meet Santa anywhere around the World.
 *
 * PARTICIPANTS: - Santa Claus
 *
 * STEPS: - Find Santa - Say hi - Get reward - Get hat
 *
 * REWARD: - a stocking which can be opened to obtain a random good reward: food,
 * money, potions, items, etc...
 *
 * REPETITIONS: - None
 */
public class MeetSanta extends AbstractQuest implements LoginListener {
	private static final String QUEST_SLOT = "meet_santa_08";

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
					"Hi again!",
				    new SpeakerNPC.ChatAction() {
					    @Override
					    public void fire(Player player, Sentence sentence, SpeakerNPC engine) { 
						addHat(player);	    
					    }
					}
				    );

				List<SpeakerNPC.ChatAction> reward = new LinkedList<SpeakerNPC.ChatAction>();
				reward.add(new EquipItemAction("stocking"));
				reward.add(new SetQuestAction(QUEST_SLOT, "done"));
				reward.add(new ChatAction() {
				      	@Override
				        public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					    addHat(player);
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

	private void addHat(Player player) {
		// fetch old outfit as we want to know the current hair
		Outfit oldoutfit = player.getOutfit();
		// all santa hat sprites are at 50 + current hair
		if (oldoutfit.getHair() < 50) {
			int hatnumber = oldoutfit.getHair() + 50;
			// the new outfit only changes the hair, rest is null
			Outfit newOutfit = new Outfit(hatnumber, null, null, null);
			//put it on, and store old outfit.
			player.setOutfit(newOutfit.putOver(oldoutfit), true);
		}
	}


	public void onLoggedIn(Player player) {
		// is it Christmas?
		Outfit outfit = player.getOutfit();
		int hairnumber = outfit.getHair();
		if (hairnumber >= 50 && hairnumber < 90) {
			Date now = new Date();
			GregorianCalendar notXmas = new GregorianCalendar(2008, Calendar.JANUARY, 6);
			Date dateNotXmas = notXmas.getTime();
			if (now.after(dateNotXmas)) {
				int newhair = hairnumber - 50;
				Outfit newOutfit = new Outfit(newhair, null, null, null);
				player.setOutfit(newOutfit.putOver(outfit), false);
			}
		}
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		LoginNotifier.get().addListener(this);
		/* activate santa here in 2008
		createSanta();
		new TeleporterBehaviour(santa, "Ho, ho, ho! Merry Christmas!", false);
		*/
	}
}
