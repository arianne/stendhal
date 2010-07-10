package games.stendhal.server.maps.quests;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;


/**
 * Quest to buy icecream for a little girl.
 * You have to get approval from her mother before giving it to her
 *
 * @author kymara
 */

public class IcecreamForAnnie extends AbstractQuest {

	// constants
	private static final String QUEST_SLOT = "icecream_for_annie";

	
	


	/** The delay between repeating quests. */
	private static final int REQUIRED_MINUTES = 30;

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void icecreamStep() {
		final SpeakerNPC npc = npcs.get("Annie Jones");

		npc.addGreeting(null, new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				if (!player.hasQuest(QUEST_SLOT)) {
					npc.say("Hello, my name is Annie. I am five years old.");
				} else if (player.getQuest(QUEST_SLOT).equals("start")) {
					if (player.isEquipped("icecream")) {
						npc.say("Mummy says I mustn't talk to you any more. You're a stranger.");
						npc.setCurrentState(ConversationStates.IDLE);
					} else {
						npc.say("Hello. I'm hungry.");
					}
				} else if (player.getQuest(QUEST_SLOT).equals("mummy")) {
							if (player.isEquipped("icecream")) {
								npc.say("Yummy! Is that icecream for me?");
									npc.setCurrentState(ConversationStates.QUESTION_1);
							} else {	
								npc.say("Hello. I'm hungry.");
							}	
				} else { 
					//any other options (like rejected quest slot)
					npc.say("Hello.");
				}
			}
		});
			    
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				null,
				ConversationStates.ATTENDING, 
				null,
				new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				if (!player.hasQuest(QUEST_SLOT) || player.getQuest(QUEST_SLOT).equals("rejected")) {
					npc.say("I'm hungry! I'd like an icecream, please. Vanilla, with a chocolate flake. Will you get me one?");
					npc.setCurrentState(ConversationStates.QUEST_OFFERED);
				} else if (player.isQuestCompleted(QUEST_SLOT)) { 
					// shouldn't happen
					npc.say("I'm full up now thank you!");
				} else if (player.getQuest(QUEST_SLOT).startsWith("eating;")) {
					// She is still full from her previous icecream,
					// she doesn't want another yet

					// Split the time from the word eating 
					// tokens now is like an array with 'eating' in tokens[0] and
					// the time is in tokens[1]. so we use just tokens[1]
					final String[] tokens = player.getQuest(QUEST_SLOT).split(";"); 
					final long delayInMilliseconds = REQUIRED_MINUTES * MathHelper.MILLISECONDS_IN_ONE_MINUTE; 
					
					// timeRemaining is ''time when quest was done +
					// delay - time now''
					// if this is > 0, she's still full
					final long timeRemaining = (Long.parseLong(tokens[1]) + delayInMilliseconds)
						- System.currentTimeMillis();
					if (timeRemaining > 0L) {
						npc.say("I've had too much icecream. I feel sick.");
						return;
						// note: it is also possible to make the npc
						// say an approx time but this sounded wrong
						// with the 'at least'
					}
					// She has recovered and is ready for another
					npc.say("I hope another icecream wouldn't be greedy. Can you get me one?");
					npc.setCurrentState(ConversationStates.QUEST_OFFERED);
				} else {
					npc.say("Waaaaaaaa! Where is my icecream ....");
				}
			}
		});
		// Player agrees to get the icecream
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, 
				null,
				ConversationStates.ATTENDING, 
				"Thank you!",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 10.0));
		
		// Player says no, they've lost karma
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, 
				null, 
				ConversationStates.IDLE,
				"Ok, I'll ask my mummy instead.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
		
		// Player has got icecream and spoken to mummy
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES, 
				null,
				ConversationStates.ATTENDING, 
				null,
				new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				if (player.drop("icecream")) {
					npc.say("Thank you EVER so much! You are very kind. Here, take this present.");
					player.setQuest(QUEST_SLOT, "eating;"
							+ System.currentTimeMillis());
					player.addKarma(10.0);
					player.addXP(500);
					final Item item = SingletonRepository.getEntityManager().getItem("present");
					player.equipOrPutOnGround(item);
				} else {
					npc.say("Hey, where's my icecream gone?!");
				}
			}
		});
		// Player says no, they've lost karma
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES, 
				null, 
				ConversationStates.IDLE,
				"Waaaaaa! You're a big fat meanie.",
				new DecreaseKarmaAction(5.0));
	}
	
	private void meetMummyStep() {
		final SpeakerNPC mummyNPC = npcs.get("Mrs Jones");

		mummyNPC.add(ConversationStates.IDLE, 
					ConversationPhrases.GREETING_MESSAGES,
					new QuestNotStartedCondition(QUEST_SLOT),
					ConversationStates.ATTENDING, "Hello, nice to meet you.",
					null);

		mummyNPC.add(ConversationStates.IDLE, 
					ConversationPhrases.GREETING_MESSAGES, 
					new QuestInStateCondition(QUEST_SLOT, "start"),
					ConversationStates.ATTENDING, 
					"Hello, I see you've met my daughter Annie. I hope she wasn't too demanding. You seem like a nice person.",
					new SetQuestAction(QUEST_SLOT, "mummy"));

		mummyNPC.add(ConversationStates.IDLE, 
					ConversationPhrases.GREETING_MESSAGES, null,
					ConversationStates.ATTENDING, "Hello again.", null);
	}
	@Override
	public void addToWorld() {
		super.addToWorld();
		icecreamStep();
		meetMummyStep();
	}
	@Override
	public String getName() {
		return "IcecreamForAnnie";
	}
	
	// Getting to Kalavan is not too feasible till this level
	@Override
	public int getMinLevel() {
		return 10;
	}
	
	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT,"eating;"),
				 new TimePassedCondition(QUEST_SLOT, REQUIRED_MINUTES, 1)).fire(player,null, null);
	}
	
	@Override
	public boolean isCompleted(final Player player) {
		return new QuestStateStartsWithCondition(QUEST_SLOT,"eating;").fire(player, null, null);
	}
}
