package games.stendhal.server.entity.npc.action;

import games.stendhal.server.core.engine.db.StendhalHallOfFameDAO;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import marauroa.server.game.db.DAORegister;


/**
 * Displays text from the hall of fame on a sign.
 *
 * @author hendrik
 */
public class LoadSignFromHallOfFame implements ChatAction {

	private Sign sign;
	private String introduction;
	private String fametype;
	private int max;
	private boolean ascending;

	/**
	 * creates a new LoadSignFromHallOfFame
	 *
	 * @param sign the sign to modify
	 * @param introduction introduction for the sign
	 * @param fametype type of fame
	 * @param max maximum number of returned characters
	 * @param ascending sort ascending or descending
	 */
	public LoadSignFromHallOfFame(Sign sign, String introduction, String fametype, int max, boolean ascending) {
		this.sign = sign;
		this.introduction = introduction;
		this.fametype = fametype;
		this.max = max;
		this.ascending = ascending;
	}


	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		List<String> players = DAORegister.get().get(StendhalHallOfFameDAO.class).getCharactersByFametype(fametype, max, ascending);
		sign.setText(introduction + players);
		sign.notifyWorldAboutChanges();
	}

}
