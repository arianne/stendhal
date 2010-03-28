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
import games.stendhal.server.util.TimeUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Kill Dhohr Nuggetcutter
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li> Zogfang
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li> Zogfang asks you to kill remainging dwarves from area
 * <li> You go kill Dhohr Nuggetcutter and you get the reward from zogfang
 * </ul>
 * <p>
 * REWARD:
 * <ul>
 * <li> mithril nugget
 * <li> 4000 XP
 * <li>10 karma in total
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li> after 14 days.
 * </ul>
 */

public class KillDhohrNuggetcutter extends AbstractQuest {

	private static final String QUEST_SLOT = "kill_dhohr_nuggetcutter";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	private void step_1() {
		final SpeakerNPC npc = npcs.get("Zogfang");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				null,
				ConversationStates.QUEST_OFFERED, 
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						if (!player.hasQuest(QUEST_SLOT) || player.getQuest(QUEST_SLOT).equals("rejected")) {
							engine.say("We are unable to rid our area of dwarves. Especially one mighty one named Dhohr Nuggetcutter. Would you please kill them?");
						}  else if (player.getQuest(QUEST_SLOT).equals("start")) {
							engine.say("I already asked you to kill Dhohr Nuggetcutter!");
							engine.setCurrentState(ConversationStates.ATTENDING);
						}  else if (player.getQuest(QUEST_SLOT).startsWith("killed;")) {
							final String[] tokens = player.getQuest(QUEST_SLOT).split(";");
							final long delay = 2 * MathHelper.MILLISECONDS_IN_ONE_WEEK;
							final long timeRemaining = (Long.parseLong(tokens[1]) + delay) - System.currentTimeMillis();
							if (timeRemaining > 0) {
								engine.say("Thank you for helping us. Maybe you could come back later. The dwarves might return. Try back in " + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)) + ".");
								engine.setCurrentState(ConversationStates.ATTENDING);
								return;
							}
							engine.say("Would you like to help us again?");
						} else {
							engine.say("Thank you for your help in our time of need. Now we feel much safer.");
							engine.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new StartRecordingKillsAction("Dhohr Nuggetcutter", "mountain dwarf", "mountain elder dwarf", "mountain hero dwarf", "mountain leader dwarf"));
		actions.add(new IncreaseKarmaAction(5.0));
		actions.add(new SetQuestAction(QUEST_SLOT, "start"));
		
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Great! Please find them somewhere in this level of the keep and make them pay for their tresspassing!",
				new MultipleActions(actions));

		npc.add(ConversationStates.QUEST_OFFERED, 
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Ok, I will await someone with enough backbone to do the job.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}

	private void step_2() {
		/* Player has to kill the dwarves*/
	}

	private void step_3() {

		final SpeakerNPC npc = npcs.get("Zogfang");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING, 
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						if (player.hasKilled("mountain leader dwarf")
								&& player.hasKilled("Dhohr Nuggetcutter")
								&& player.hasKilled("mountain elder dwarf")
								&& player.hasKilled("mountain hero dwarf")
								&& player.hasKilled("mountain dwarf")) {
							engine.say("Thank you so much. You are a warrior, indeed! Here, have one of these. We have found them scattered about. We have no idea what they are.");
							final Item mithrilnug = SingletonRepository.getEntityManager()
									.getItem("mithril nugget");
							player.equipOrPutOnGround(mithrilnug);
							player.addKarma(5.0);
							player.addXP(4000);
							player.setQuest(QUEST_SLOT, "killed;" + System.currentTimeMillis());
						} else {
							engine.say("Just go kill Dhohr Nuggetcutter and his minions; the mountain leader, hero and elder dwarves. Even the simple mountain dwarves are a danger to us, kill them too.");
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
		return "KillDhohrNuggetcutter";
	}
	
	// The kill requirements and surviving in the zone requires at least this level
	@Override
	public int getMinLevel() {
		return 70;
	}
}
