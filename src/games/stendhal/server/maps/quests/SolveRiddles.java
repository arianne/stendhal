fpackage games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.common.NotificationType;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.parser.ConvCtxForMatchingSource;
import games.stendhal.server.entity.npc.parser.ConversationContext;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.npc.parser.SimilarExprMatcher;
import games.stendhal.server.entity.player.Player;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * QUEST: Quest to solve a riddle to leave hell
 * <p>
 * 
 * PARTICIPANTS: <ul><li>Grim Reaper</ul>
 * 
 * 
 * STEPS: <ul><li> Reaper sets you a riddle 
 * <li> Player tries to answer
 * <li> Reaper compares answer to configuration file on server 
 * </ul>
 * 
 * 
 * REWARD: <ul><li>100 XP - Leaving hell</ul>
 * 
 * REPETITIONS: <ul><li>Any time you wish to leave hell, but if you ask for a riddle when you didn't solve the previous
 *  one yet, nor asked the other NPC to let you leave with karma loss, then you have to solve same one still</ul>
 * 
 * @author kymara
 */

public class SolveRiddles extends AbstractQuest {
	private static final String QUEST_SLOT = "solve_riddles";
	private static final int xpreward = 100;
    private static final String RIDDLES_XML = "data/conf/riddles.xml";
	private final Properties riddles = new Properties();

	public SolveRiddles() {
		try {
			FileInputStream fis = new FileInputStream(RIDDLES_XML);
			if (fis==null) { 
				System.out.println("**************cant find riddles file**********");
			} else {
				this.riddles.loadFromXML(fis);
			}
			// TODO: load a riddles-example.xml if other doesn't exist
		} catch (final Exception e) {
			System.out.println("******error loading riddles file*******");
		}
	}


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void setRiddle() {
		final SpeakerNPC reaper = npcs.get("Grim Reaper");
		
		// player has no unsolved riddle active
		reaper.add(ConversationStates.ATTENDING,
				"leave", 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1,
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						// randomly choose from available riddles
						final String riddle = Rand.rand(riddles.keySet()).toString();
						npc.say("Try this riddle: " + riddle);
						player.setQuest(QUEST_SLOT, riddle);
					}
				});
		
		// player already was set a riddle he couldn't solve
		reaper.add(ConversationStates.ATTENDING,
				"leave", 
				new QuestStartedCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1,
				null,
				new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					final String riddle = player.getQuest(QUEST_SLOT);
					npc.say("You must solve the riddle which I previously set you: " + riddle);
				}
		});
		
		reaper.add(ConversationStates.QUESTION_1, "", null,
			ConversationStates.QUESTION_1, null,
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					final String riddle = player.getQuest(QUEST_SLOT);
					final String solution = riddles.get(riddle).toString();

					final ConversationContext ctx = new ConvCtxForMatchingSource();
					final Sentence answer = ConversationParser.parse(sentence.getOriginalText(), ctx);
					final Sentence expected = ConversationParser.parse(solution, new SimilarExprMatcher());

					if (answer.matchesFull(expected)) {
						final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("int_afterlife");
						player.teleport(zone, 31, 23, Direction.UP, player);
						// clear quest slot so riddle is chosen randomly for player next time
						player.removeQuest(QUEST_SLOT);
						player.sendPrivateText(NotificationType.POSITIVE, "You solved the riddle correctly and earned " + xpreward + " XP.");
						player.addXP(xpreward);
						player.notifyWorldAboutChanges();
					} else if (sentence.getTriggerExpression().getNormalized().equals("bye")) {
						npc.say("The old order of things has passed away ... ");
						npc.setCurrentState(ConversationStates.IDLE);
					} else if (sentence.getTriggerExpression().getNormalized().equals("leave") || sentence.getTriggerExpression().getNormalized().equals("riddle")) {
						// player didn't answer riddle but tried saying riddle/leave again (to get another maybe?)
						npc.say("You can ask my mirror to let you leave, or you must solve the riddle which I previously set you: " + riddle);
					} else {
						npc.say("Incorrect! Try again, or ask my mirror to let you leave.");
						player.subXP(10 * xpreward);
					}
				}
			});
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		setRiddle();
	}

	@Override
	public String getName() {
		return "SolveRiddles";
	}
}
