package games.stendhal.server.maps.quests;

import games.stendhal.common.Level;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Sheep Growing for Nishiya
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Nishiya (the sheep seller in Semos village)</li>
 * <li>Sato (the sheep buyer in Semos city)</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li>Nishiya asks you to grow a sheep.</li>
 * <li>Sheep grows to weight 100.</li>
 * <li>Sheep is handed over to Sato.</li>
 * <li>Nishiya thanks you.</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li>XP to level 2</li>
 * <li>Karma: 10</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class SheepGrowing extends AbstractQuest {

	private final String QUEST_SLOT = "sheep_growing";
	private final String title = "Sheep Growing for Nishiya";

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				title,
				"Nishiya, the sheep seller, promised Sato a sheep. " +
					"Because he is very busy he needs somebody to take care of " +
					"one of his sheep and hand it over to Sato.",
				true);
		preparePlayerGetsSheepStep();
		preparePlayerHandsOverSheepStep();
		preparePlayerReturnsStep();
	}
	
	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new LinkedList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("Nishiya asked me if I could grow a sheep for him.");
		
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("I told Nishiya that I have to do other things now... maybe I have time for the task later.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "handed_over", "done")) {
			res.add("I promised to take care of one of his sheep.");
		}
		if (player.isQuestInState(QUEST_SLOT, "handed_over", "done")) {
			res.add("I handed over the grown sheep to Sato. I should return to Nishiya now.");
		}
		if(questState.equals("done")) {
			res.add("I returned to Nishiya. He was very happy I helped him.");
		}
		return res;
	}

	@Override
	public String getName() {
		return title;
	}
	/**
	 * The step where the player speaks with Nishiya about quests and gets the sheep.
	 */
	private void preparePlayerGetsSheepStep() {
		final SpeakerNPC npc = npcs.get("Nishiya");
		
		// If quest is not done or started yet ask player for help
		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotActiveCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"Lately I am very busy with all my sheep. " +
				"Would you be willing to take care of one of my sheep and hand it over to #Sato? " +
				"You only have to let it eat some red berries until it reaches a weight of " + Sheep.MAX_WEIGHT + "." +
				"Would you do that?",
				new SetQuestAction(QUEST_SLOT, "asked"));
		
		// If quest is offered and player says no reject the quest
		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				ConversationStates.IDLE,
				"Ok... then I have to work twice as hard these days...",
				new SetQuestAction(QUEST_SLOT, "rejected"));
		
		// If quest is still active but not handed over do not give an other sheep to the player
		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
					new QuestActiveCondition(QUEST_SLOT),
					new NotCondition(new QuestInStateCondition(QUEST_SLOT, "handed_over"))),
				ConversationStates.ATTENDING,
				"I already gave you one of my sheep. " +
				"If you left it on its own I can sell you a new one. Just say #buy #sheep.",
				null);
		
		// If quest is offered and player says yes give a sheep to him.
		List<ChatAction> sheepActions = new LinkedList<ChatAction>();
		sheepActions.add(new SetQuestAction(QUEST_SLOT, "start"));
		sheepActions.add(new ChatAction() {
			public void fire(Player player, Sentence sentence, EventRaiser npc) {
				final Sheep sheep = new Sheep(player);
				StendhalRPAction.placeat(npc.getZone(), sheep, npc.getX(), npc.getY() + 1);
			}
		});
		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				ConversationStates.IDLE,
				"Thanks! *smiles* Here is your fluffy fosterling. Be careful with her. " +
				"If she dies or if you leave her behind you have to #buy the next sheep on your own." +
				"Oh... and don't accidentially sell the sheep to Sato. Just talk to him when the sheep has grown up.",
				new MultipleActions(sheepActions));
	}
	/**
	 * The step where the player goes to Sato to give him the grown up sheep.
	 */
	private void preparePlayerHandsOverSheepStep() {
		// Remove action
		final List<ChatAction> removeSheepAction = new LinkedList<ChatAction>();
		removeSheepAction.add(new ChatAction() {
			public void fire(Player player, Sentence sentence, EventRaiser npc) {
				// remove sheep
				final Sheep sheep = player.getSheep();
				// TODO Here the moveSheep method should be called if possible.
				if(sheep != null) {
					player.removeSheep(sheep);
					player.notifyWorldAboutChanges();
					sheep.getZone().remove(sheep);
				} else {
					// should not happen
					npc.say("What? What sheep? Missed I something?");
					npc.setCurrentState(ConversationStates.IDLE);
					return;
				}
			}
		});
		removeSheepAction.add(new SetQuestAction(QUEST_SLOT, "handed_over"));
		
		// Hand-Over condition
		ChatCondition playerHasFullWeightSheep = new ChatCondition() {
			public boolean fire(Player player, Sentence sentence, Entity npc) {
				return player.hasSheep()
					&& player.getSheep().getWeight() >= Sheep.MAX_WEIGHT;
			}
		};
		ChatCondition sheepIsNearEnough = new ChatCondition() {
			public boolean fire(Player player, Sentence sentence, Entity npc) {
				return player.hasSheep() 
					&& npc.squaredDistance(player.getSheep()) <= 5 * 5;
			}
		};
		
		// Sato asks for sheep
		final SpeakerNPC npc = npcs.get("Sato");
		npc.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(playerHasFullWeightSheep,sheepIsNearEnough),
				ConversationStates.QUEST_ITEM_BROUGHT,
				"Hello. What a nice and healthy sheep is following you there! Is that one for me?",
				null);
		
		// Player answers yes - Sheep is given to Sato
		npc.add(
				ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES,
				playerHasFullWeightSheep,
				ConversationStates.IDLE,
				"I knew it! It is Nishiya's, right? I was already waiting for it. " +
				"It is a gift for a friend of mine and it would be a shame if I had no birthday present. " +
				"Give thanks to Nishiya.",
				new MultipleActions(removeSheepAction));
		
		// Player answers no - Sheep stays at player
		npc.add(
				ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.NO_MESSAGES,
				playerHasFullWeightSheep,
				ConversationStates.IDLE,
				"Oh... hmm... ok. Well, I buy sheeps you know? And I am waiting for one from Nishiya. " +
				"He wanted to send me one a while ago...",
				null);
	}
	
	/**
	 * The step where the player returns to Nishiya to get his reward.
	 */
	private void preparePlayerReturnsStep() {
		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new ChatAction() {
			public void fire(Player player, Sentence sentence, EventRaiser npc) {
				// give XP to level 2
				int reward = Level.getXP( 2 ) - player.getXP();
				if(reward > 0) player.addXP(reward);
				player.notifyWorldAboutChanges();
			}
		});
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));
		reward.add(new IncreaseKarmaAction( 10 ));
		
		final SpeakerNPC npc = npcs.get("Nishiya");
		// Asks player if he handed over the sheep
		npc.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "handed_over"),
				ConversationStates.QUEST_ITEM_QUESTION,
				"Did you already give the sheep to Sato?",
				null);
		// Player answers yes - give reward
		npc.add(
				ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.YES_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "handed_over"),
				ConversationStates.IDLE,
				"Thank you! You doesn't know how much I have to do these days. " +
				"You really helped me out.",
				new MultipleActions(reward));
		// Player answers no - 
		npc.add(
				ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.NO_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "handed_over"),
				ConversationStates.IDLE,
				"Well... ok. But don't forget it. Sato needs the sheep very soon.",
				null);
	}
}
