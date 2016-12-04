package games.stendhal.server.core.rp.pvp;

import java.util.Collection;

import com.google.common.collect.Sets;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.LogoutListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.player.Player;
/**
 * The PlayerVsPlayerChallengeManager stores, expires and creates PvP duels to allow
 * two players to fight with each other
 *  
 * @author markus
 */
public class PlayerVsPlayerChallengeManager  implements TurnListener, LogoutListener {
	
	private static final long TIMEOUT_FOR_ACCEPTANCE = 60 * 1000 / 300; 
	
	private final Collection<PlayerVsPlayerChallenge> currentChallenges = Sets.newHashSet();
	
	public static PlayerVsPlayerChallengeManager create() {
		PlayerVsPlayerChallengeManager challengeManager = new PlayerVsPlayerChallengeManager();
		TurnNotifier.get().notifyInSeconds(60, challengeManager);
		SingletonRepository.getLogoutNotifier().addListener(challengeManager);
		return challengeManager;
	}
	
	private PlayerVsPlayerChallengeManager() {
		//do nothing
	}
	
	@Override
	public void onTurnReached(int currentTurn) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Remove player's current challenges on log out
	 */
	@Override
	public void onLoggedOut(Player player) {
		Collection<PlayerVsPlayerChallenge> removals = Sets.newHashSet();
		for (PlayerVsPlayerChallenge playerVsPlayerChallenge : currentChallenges) {
			if(playerVsPlayerChallenge.isInvolved(player)) {
				removals.add(playerVsPlayerChallenge);
			}
		}
		this.currentChallenges.removeAll(removals);
	}

}
