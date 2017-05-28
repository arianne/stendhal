package games.stendhal.server.core.rp.pvp;

import games.stendhal.common.constants.Events;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

/**
 * This event is sent when a player send a PvP challenge to another player. 
 * @author markus
 */
public class PlayerVsPlayerNewChallengeEvent extends RPEvent {
	
	private static final String TURN_ATTR = "turn";
	private static final String CHALLENGER_ATTR = "challenger";

	public static void generateRPClass() {
		RPClass newClass = new RPClass(Events.PVP_NEW_CHALLENGE);
		newClass.addAttribute(CHALLENGER_ATTR, Type.STRING);
		newClass.addAttribute(TURN_ATTR, Type.INT);
	}

	public PlayerVsPlayerNewChallengeEvent(String challenger, int currentTurn) {
		super(Events.PVP_NEW_CHALLENGE);
		this.put(CHALLENGER_ATTR, challenger);
		this.put(TURN_ATTR, currentTurn);
	}

}
