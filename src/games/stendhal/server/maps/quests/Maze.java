package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.maze.MazeGenerator;
import games.stendhal.server.maps.quests.maze.MazeSign;

public class Maze extends AbstractQuest {
	/** Minimum time between repeats. */
	private static final int COOLING_TIME = MathHelper.MINUTES_IN_ONE_HOUR * 24;
	private MazeSign sign;
	
	@Override
	public void addToWorld() {
		super.addToWorld();
		addMazeSign();
		setupConversation();
	}
	
	@Override
	public String getName() {
		return "Maze";
	}

	@Override
	public String getSlotName() {
		return "maze";
	}
	
	@Override
	public boolean isRepeatable(Player player) {
		return true;
	}
	
	private SpeakerNPC getNPC() {
		return npcs.get("Haizen");
	}
	
	private void addMazeSign() {
		sign = new MazeSign();
		sign.setPosition(10, 7);
		getNPC().getZone().add(sign);
	}
	
	private void setupConversation() {
		SpeakerNPC npc = getNPC();
		
		npc.addQuest("I can send you to a #maze you need to find your way out. I keep the a list of the fast and frequent maze solvers in that blue book on the table.");
	
		npc.add(ConversationStates.ATTENDING,
				"maze",
				new TimePassedCondition(getSlotName(), COOLING_TIME, 0),
				ConversationStates.QUEST_OFFERED,
				"There will be a portal out in the opposite corner of the maze. I'll also add scrolls to the two other corners you can try to get if you are fast enough. Do you want to try?",
				null);
		
		npc.add(ConversationStates.ATTENDING,
				"maze",
				new NotCondition(new TimePassedCondition(getSlotName(), COOLING_TIME, 0)),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(getSlotName(), 
						"I can send you to the maze only once in a day. You can go there again in", COOLING_TIME));
		
		
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				null,
				new SendToMazeChatAction());
		
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"OK. You look like you'd only get lost anyway.",
				null);
	}
	
	private class SendToMazeChatAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
			MazeGenerator maze = new MazeGenerator(player.getName() + "_maze", 128, 128);
			maze.setReturnLocation(player.getZone().getName(), player.getX(), player.getY());
			maze.setSign(sign);
			StendhalRPZone zone = maze.getZone();
			SingletonRepository.getRPWorld().addRPZone(zone);
			player.setQuest(getSlotName(), Long.toString(System.currentTimeMillis()));
			maze.startTiming();
			player.teleport(zone, maze.getStartPosition().x, maze.getStartPosition().y, Direction.DOWN, player);
		}
	}
}
