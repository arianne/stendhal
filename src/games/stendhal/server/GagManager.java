package games.stendhal.server;

import games.stendhal.server.actions.ChatAction;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.LoginListener;
import games.stendhal.server.events.LoginNotifier;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 * Manages gags
 */
public class GagManager implements LoginListener {

	private static final Logger logger = Log4J.getLogger(GagManager.class);

	/** The Singleton instance */
	private static GagManager instance;
	
	// TODO: cleanup/properly-handle Notifiers in special cases: change gag time, server restart, player login.

	/**
	 * returns the GagManager object (Singleton Pattern)
	 *
	 * @return GagManager
	 */
	public static GagManager get() {
		if (instance == null) {
			instance = new GagManager();
		}
		return instance;
	}
	
	// singleton
	private GagManager() {
		LoginNotifier.get().addListener(this);
	}

	/**
	 * @param criminalName The name of the player who should be gagged
	 * @param policeman The name of the admin who wants to gag the criminal
	 * @param minutes The duration of the sentence
	 */
	public void gag(final String criminalName, Player policeman, int minutes, String reason) {
		final Player criminal = StendhalRPRuleProcessor.get().getPlayer(criminalName);

		if (criminal == null) {
			String text = "Player " + criminalName + " not found";
			policeman.sendPrivateText(text);
			logger.debug(text);
			return;
		}
		
		//no -1
		if(minutes < 0) {
			policeman.sendPrivateText("Infinity (negative numbers) is not supported.");
			return;
		}
		
		//Set the gag
		long expireDate = System.currentTimeMillis() + (1000L * 60L * minutes); // Miliseconds
		criminal.setQuest("gag", "" + expireDate);

		//Send messages
		policeman.sendPrivateText("You have gagged " + criminalName
				+ " for " + minutes + " minutes. Reason: " + reason + ".");
		criminal.sendPrivateText("You have been gagged by " + policeman.getName()
				+ " for " + minutes + " minutes. Reason: " + reason + ".");
		ChatAction.sendMessageToSupporters("GagManager", policeman.getName()
				+ " gagged " + criminalName
				+ " for " + minutes + " minutes. Reason: " + reason + ".");

		SetupNotifier(criminal);

	}

	/**
	 * Removes a gag
	 *
	 * @param inmate player who should be released
	 */
	public void release(Player inmate) {
		
		if (isGagged(inmate)) {
			inmate.removeQuest("gag");
			inmate.sendPrivateText("Your gag sentence is over.");
			logger.debug("Player " + inmate.getName() + "released from gag.");
		}

	}

	/**
	 * Is player gagged?
	 *
	 * @param inmate player to check
	 * @return true, if it is gagged, false otherwise.
	 */
	public static boolean isGagged(Player player) {
		if(player.hasQuest("gag"))
			return true;
		return false;
	}
	
	/*
	 * If the players' gag has expired remove it
	 * 
	 * @param inmate player to check
	 * @return true, if the gag expired and was removed or was already removed. false, if the player still has time to serve.
	 */
	private boolean tryExpire(Player player) {
		if(!isGagged(player))
			return true;
		
		// allow for an error of 10 seconds
		if(getTimeRemaining(player) < (10L * 1000L)) {
			release(player);
			return true;
		}
		
		return false;
	}

	public void onLoggedIn(Player player) {
		if(!isGagged(player))
			return;
		
		if (!tryExpire(player)) {
			SetupNotifier(player);
		}
	}
	
	private void SetupNotifier(Player criminal) {
	
		final String criminalName = criminal.getName();
		
		// Set a timer so that the inmate is automatically released after
		// serving his sentence. We're using the TurnNotifier; we use
		TurnNotifier.get().notifyInSeconds((int)(getTimeRemaining(criminal) / 1000),
				new TurnListener (){
					public void onTurnReached(int currentTurn, String message) {
						
						Player criminal2 = StendhalRPRuleProcessor.get().getPlayer(criminalName);
						if (criminal2 == null) {
							logger.debug("Gagged player " + criminalName + "has logged out.");
							return;
						}
						
						tryExpire(criminal2);
						
					}});
	}
	
	/*
	 * gets time remaining in miliseconds
	 * 
	 * @param inmate player to check
	 * @return time remaining in miliseconds
	 */
	public long getTimeRemaining(Player criminal) {
		if(!isGagged(criminal))
			return 0L;
		long expireDate = Long.parseLong(criminal.getQuest("gag"));
		long timeRemaining = expireDate - System.currentTimeMillis();
		return timeRemaining;
	}
}