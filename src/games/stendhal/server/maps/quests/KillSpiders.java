package games.stendhal.server.maps.quests;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Kill Spiders
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li> Morgrin
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li> Groundskeeper Morgrin ask you to clean up the school basement
 * <li> You go kill the spiders in the basement and you get the reward from Morgrin
 * </ul>
 * <p>
 * REWARD:
 * <ul>
 * <li> magical egg
 * <li> 5000 XP
 * <li> 10 karma in total
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li> after 7 days.
 * </ul>
 */

public class KillSpiders extends AbstractQuest {

	private static final String QUEST_SLOT = "kill_all_spiders";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	private void step_1() {
		final SpeakerNPC npc = npcs.get("Morgrin");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				null,
				ConversationStates.ATTENDING, 
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						if (!player.hasQuest(QUEST_SLOT) || player.getQuest(QUEST_SLOT).equals("rejected")) {
							engine.say("Have you ever been to the basement of the school? The room is full of spiders and some could be dangerous, since the students do experiments! Would you like to help me with this 'little' problem?");
							engine.setCurrentState(ConversationStates.QUEST_OFFERED);
						}  else if (player.isQuestCompleted(QUEST_SLOT)) {
							engine.say("I already asked you to kill all creatures in the basement!");
						}  else if (player.getQuest(QUEST_SLOT).startsWith("killed;")) {
							final String[] tokens = player.getQuest(QUEST_SLOT).split(";");
							final long delay = MathHelper.MILLISECONDS_IN_ONE_WEEK;
							final long timeRemaining = (Long.parseLong(tokens[1]) + delay) - System.currentTimeMillis();
							if (timeRemaining > 0) {
								engine.say("Sorry there is nothing to do for you yet. But maybe you could come back later. I have to clean the school once a week.");
								return;
							}
							engine.say("Would you like to help me again?");
							engine.setCurrentState(ConversationStates.QUEST_OFFERED);
						} else {
							engine.say("Thanks for your help. Now I'm sleeping well again.");
						}
					}
				});

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new StartRecordingKillsAction("spider", "poisonous spider", "giant spider"));
		actions.add(new IncreaseKarmaAction(5.0));
		actions.add(new SetQuestAction(QUEST_SLOT, "start"));
		
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Fine. Go down to the basement and kill all the creatures there!",
				new MultipleActions(actions));

		npc.add(ConversationStates.QUEST_OFFERED, 
				ConversationPhrases.NO_MESSAGES, 
				null,
				ConversationStates.ATTENDING,
				"Ok, I have to find someone else to do this 'little' job!",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}

	private void step_2() {
		/* Player has to kill the creatures*/
	}

	private void step_3() {

		final SpeakerNPC npc = npcs.get("Morgrin");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING, 
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						if (player.hasKilled("spider")
								&& player.hasKilled("poisonous spider")
								&& player.hasKilled("giant spider")) {
							engine.say("Oh thank you my friend. Here you have something special, I got it from a Magican. Who he was I do not know. What the egg's good for, I do not know. I only know, it could be useful for you.");
							final Item mythegg = SingletonRepository.getEntityManager()
									.getItem("mythical egg");
							mythegg.setBoundTo(player.getName());
							player.equipOrPutOnGround(mythegg);
							player.addKarma(5.0);
							player.addXP(5000);
							player.setQuest(QUEST_SLOT, "killed;" + System.currentTimeMillis());
						} else {
							engine.say("Go down and kill the creatures, no time left.");
						}
		 			}
				});
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}

	@Override
	public String getName() {
		return "KillSpiders";
	}
}
