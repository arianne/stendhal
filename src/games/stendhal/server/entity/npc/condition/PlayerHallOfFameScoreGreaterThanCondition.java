package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.core.engine.dbcommand.ReadHallOfFamePointsCommand;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

import org.apache.log4j.Logger;
/**
 * Check if a player has a minimum hall of fame score for a given fametype
 * 
 * @author madmetzger
 */
public class PlayerHallOfFameScoreGreaterThanCondition implements ChatCondition {
	
	private final String fametype;
	
	private final int score;

	private ResultHandle handle;
	
	/**
	 * Create a new PlayerHallOfFameScoreGreaterThanCondition
	 * 
	 * @param fametype
	 * @param score
	 */
	public PlayerHallOfFameScoreGreaterThanCondition(String fametype, int score) {
		this.fametype = fametype;
		this.score = score;
	}

	public boolean fire(Player player, Sentence sentence, Entity npc) {
		handle = new ResultHandle();
		DBCommandQueue.get().enqueueAndAwaitResult(new ReadHallOfFamePointsCommand(player.getName(), fametype), handle);
		ReadHallOfFamePointsCommand command = null;
		while(command == null) {
			command = DBCommandQueue.get().getOneResult(ReadHallOfFamePointsCommand.class, handle);
		}
		int result = command.getPoints();
		return result > score;
	}

}
