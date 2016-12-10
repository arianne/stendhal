package games.stendhal.server.actions;

import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.rp.pvp.PlayerVsPlayerChallengeAcceptedTurnListener;
import games.stendhal.server.core.rp.pvp.PlayerVsPlayerChallengeCreatorTurnListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;
/**
 * Handles challenge send from a player's client and creates the server side challenge
 *  
 * @author markus
 */
public class ChallengePlayerAction implements ActionListener {
	
	/**
	 * registers the ChallengePlayerAction action
	 */
	public static void register() {
		CommandCenter.register("challenge", new ChallengePlayerAction());
	}

	@Override
	public void onAction(Player player, RPAction action) {
		
		Entity targetEntity = EntityHelper.entityFromTargetName("target", player);
		String type = action.get("type");
		if(!(targetEntity instanceof Player)){
			return;
		}
		
		Player targetPlayer = (Player) targetEntity;
		
		if("open".equals(type)) {
			TurnNotifier.get().notifyInTurns(0, new PlayerVsPlayerChallengeCreatorTurnListener(player, targetPlayer));
			return;
		}
		
		if("accept".equals(type)) {
			TurnNotifier.get().notifyInTurns(0, new PlayerVsPlayerChallengeAcceptedTurnListener(player, targetPlayer));
			return;
		}
	}

}
