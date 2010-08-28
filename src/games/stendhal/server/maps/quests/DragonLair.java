package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Dragon Lair Access
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li> Wishman, storm trooper extraordinaire from Blordrough's dark legion, guards the remaining dragons
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li> Wishman 
 * </ul>
 * <p>
 * REWARD:
 * <ul>
 * <li> admittance to dragon lair
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li> after 1 week.
 * </ul>
 */

public class DragonLair extends AbstractQuest {

	private static final String QUEST_SLOT = "dragon_lair";
	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void step_1() {
		final SpeakerNPC npc = npcs.get("Wishman");
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				null,
				ConversationStates.QUEST_OFFERED, 
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						if (!player.hasQuest(QUEST_SLOT) || player.getQuest(QUEST_SLOT).equals("rejected")) {
							raiser.say("Would you like to visit our dragon lair?");
						}  else if (player.getQuest(QUEST_SLOT).startsWith("done;")) {
							final String[] tokens = player.getQuest(QUEST_SLOT).split(";");
							final long delay = 1 * MathHelper.MILLISECONDS_IN_ONE_WEEK;
							final long timeRemaining = (Long.parseLong(tokens[1]) + delay) - System.currentTimeMillis();
							if (timeRemaining > 0) {
								raiser.say("I think they've had enough excitement for a while.  Come back in " + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".");
								raiser.setCurrentState(ConversationStates.ATTENDING);
								return;
							}
							raiser.say("Would you like to visit our dragons again?");
						} else {
							raiser.say("Thanks for stopping by. Come again.");
							raiser.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new SetQuestAction(QUEST_SLOT, "start"));
		
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Great! Enjoy your visit. I know THEY will. Oh, watch out, we have a couple chaos dragonriders exercising our dragons. Don't get in their way!",
				new MultipleActions(new TeleportAction("-1_ados_outside_w", 25, 28, Direction.DOWN),
						new ChatAction() {
							public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
								player.setQuest(QUEST_SLOT, "done" + ";" + System.currentTimeMillis());
							}
				}
						));

		npc.add(ConversationStates.QUEST_OFFERED, 
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Ok, but our dragons will be sorry you didn't stop in for a visit.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();

	}
	@Override
	public String getName() {
		return "DragonLair";
	}
	
	// getting past the assassins to this location needs a higher level; the lair itself is dangerous too
	@Override
	public int getMinLevel() {
		return 100;
	}
	
	@Override
	public boolean isRepeatable(final Player player) {
		return	new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
						 new TimePassedCondition(QUEST_SLOT,1,MathHelper.MINUTES_IN_ONE_WEEK)).fire(player, null, null);
	}
}
