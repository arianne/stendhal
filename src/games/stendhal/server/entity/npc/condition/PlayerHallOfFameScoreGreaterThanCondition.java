package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.core.engine.dbcommand.ReadHallOfFamePointsCommand;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;

import org.apache.log4j.Logger;
/**
 * Check if a player has a minimum hall of fame score for a given fametype
 * 
 * @author madmetzger
 */
public class PlayerHallOfFameScoreGreaterThanCondition implements ChatCondition {
	
	private static final Logger logger = Logger.getLogger(PlayerHallOfFameScoreGreaterThanCondition.class);

	private final String fametype;
	
	private final int score;
	
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
		ReadHallOfFamePointsCommand command = new ReadHallOfFamePointsCommand(player.getName(), fametype);
		try {
			DBTransaction transaction = TransactionPool.get().beginWork();
			command.execute(transaction);
			TransactionPool.get().commit(transaction);
		} catch (SQLException e) {
			logger.error("Error during read of hall of fame points for chat condition", e);
		}
		int result = command.getPoints();
		return result > score;
	}

}
