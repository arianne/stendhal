package games.stendhal.server.core.rp.pvp;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.player.Player;

public class PlayerVsPlayerChallengeCreatorTurnListener implements TurnListener {

	private final Player challenger;
	private final Player challenged;

	public PlayerVsPlayerChallengeCreatorTurnListener(Player challenger, Player challenged) {
		this.challenger = challenger;
		this.challenged = challenged;
	}

	@Override
	public void onTurnReached(int currentTurn) {
		SingletonRepository.getChallengeManager().createChallenge(challenger, challenged, currentTurn);
	}
}
