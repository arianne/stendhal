package games.stendhal.server.entity.npc.action;

import games.stendhal.server.core.engine.db.StendhalHallOfFameDAO;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.sql.SQLException;

import marauroa.server.game.db.DAORegister;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

/**
 * Sets the current state of a quest.
 */
public class SetHallOfFameToAgeDiffAction implements ChatAction {
	private static final Logger logger = Logger.getLogger(SetHallOfFameToAgeDiffAction.class);

	private final String questname;
	private final int index;
	private final String fametype;

	/**
	 * Creates a new SetHallOfFameToAgeDiffAction.
	 * 
	 * @param questname
	 *            name of quest-slot to read the original age from
	 * @param fametype
	 * 			  the type in the hall of fame
	 */
	public SetHallOfFameToAgeDiffAction(final String questname, String fametype) {
		this.questname = questname;
		this.index = -1;
		this.fametype = fametype;
	}

	/**
	 * Creates a new SetHallOfFameToAgeDiffAction.
	 * 
	 * @param questname
	 *            name of quest-slot to read the original age from
	 * @param index
	 *            index of sub state containing the age at start of the quest
	 * @param fametype
	 * 			  the type in the hall of fame
	 */
	public SetHallOfFameToAgeDiffAction(final String questname, final int index, String fametype) {
		this.questname = questname;
		this.index = index;
		this.fametype = fametype;
	}

	public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		String orgAge = null;
		if (index > -1) {
			orgAge = player.getQuest(questname, index);
		} else {
			orgAge = player.getQuest(questname);
		}

		int diff = player.getAge() - Integer.parseInt(orgAge);
		try {
			DAORegister.get().get(StendhalHallOfFameDAO.class).setHallOfFamePoints(player.getName(), fametype, diff);
		} catch (SQLException e) {
			logger.error(e, e);
		}
	}

	@Override
	public String toString() {
		return "SetHallOfFameToAgeDiffAction<" + questname + "[" + index + "]," + fametype + ">";
	}


	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				SetHallOfFameToAgeDiffAction.class);
	}
}
