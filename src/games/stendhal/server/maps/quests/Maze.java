package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.maze.MazeGenerator;
import games.stendhal.server.maps.quests.maze.MazeSign;

public class Maze extends AbstractQuest {
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
	
		npc.add(ConversationStates.ATTENDING,
				"maze",
				null,
				ConversationStates.ATTENDING,
				null,
				new SendToMazeChatAction());
	}
	
	private class SendToMazeChatAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
			MazeGenerator maze = new MazeGenerator(player.getName() + "_maze", 128, 128);
			maze.setReturnLocation(player.getZone().getName(), player.getX(), player.getY());
			maze.setSign(sign);
			StendhalRPZone zone = maze.getZone();
			SingletonRepository.getRPWorld().addRPZone(zone);
			maze.startTiming();
			player.teleport(zone, maze.getStartPosition().x, maze.getStartPosition().y, Direction.DOWN, player);
		}
	}
}
