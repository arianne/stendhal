package games.stendhal.server.core.rp.pvp;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
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
	
	private static final Logger logger = Logger.getLogger(PlayerVsPlayerChallengeManager.class);
	
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
	
	/**
	 * Create a new challenge between two players if not yet existing.
	 * 
	 * @param challenger
	 * @param challenged
	 * @param currentTurn
	 */
	public void createChallenge(Player challenger, Player challenged, int currentTurn) {
		PlayerVsPlayerChallenge newChallenge = new PlayerVsPlayerChallenge(currentTurn, challenger, challenged);
		if(this.currentChallenges.contains(newChallenge)) {
			challenger.sendPrivateText(String.format("You alread have a challenge with %s", challenged.getName()));
			return;
		}
		this.currentChallenges.add(newChallenge);
		// TODO game event
		challenger.sendPrivateText(String.format("You successfully challenged %s!", challenged.getName()));
		challenged.sendPrivateText(String.format("%s send you a challenge. If you accept you can fight a duel.", challenger.getName()));
	}
	
	/**
	 * Mark the challenge between challenger and challenged as accepted
	 * 
	 * @param challenger
	 * @param challenged
	 * @param currentTurn
	 */
	public void accpetChallenge(Player challenger, Player challenged, int currentTurn) {
		PlayerVsPlayerChallenge openChallenge = this.getOpenChallengeForPlayers(challenger, challenged);
		if(openChallenge != null) {
			openChallenge.accept(currentTurn, challenged);
			// TODO game event
			// logger.debug
		} else {
			logger.debug(String.format("%s is trying to accept a challenge with %s but no such challenge exists.", challenged.getName(), challenger.getName()));
		}
	}
	
	private PlayerVsPlayerChallenge getOpenChallengeForPlayers(Player challenger, Player challenged) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onTurnReached(int currentTurn) {
		this.timeOutCurrentChallenges(currentTurn);
	}
	
	/**
	 * Filters out challenges that have to time out at the given turn
	 * @param currentTurn
	 */
	public void timeOutCurrentChallenges(int currentTurn) {
		Collection<PlayerVsPlayerChallenge> timeouts = Collections2.filter(this.currentChallenges, new Predicate<PlayerVsPlayerChallenge>() {
			@Override
			public boolean apply(PlayerVsPlayerChallenge challenge) {
				boolean timeout = currentTurn - challenge.getOpened() > TIMEOUT_FOR_ACCEPTANCE;
				boolean open = !challenge.isAccepted();
				boolean apply = timeout && open;
				return apply;
			}
		});
		logger.debug(String.format("Challenges ran into time out: %s", timeouts.toString()));
		this.currentChallenges.removeAll(timeouts);
		//TODO game event
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
				// TODO game event
				// logger.debug
			}
		}
		this.currentChallenges.removeAll(removals);
	}

}
