package games.stendhal.server.maps.quests.piedpiper;

import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.maps.quests.ThePiedPiper;

public class TPPQuest implements ITPPQuest {
	
	protected static Logger logger = Logger.getLogger(ThePiedPiper.class);	
	protected Map<String, Integer> timings;
	
	public String getSwitchingToNextPhaseMessage() {
		return null;
	}
	
	public String getSwitchingToDefPhaseMessage() {
		return null;
	}
	
	public TPP_Phase getPhase() {
		return null;
	}
	
	public TPPQuest(Map<String, Integer> timings) {
		this.timings=timings;
	}
	
	/**
	 * wrapper for shout to all function
	 * @param msg
	 */
	public void shoutMessage(final String msg) {
		if(msg!=null) {
			SingletonRepository.getRuleProcessor().tellAllPlayers(msg);
		}
	}

	
	/**
	 * timer for npc's shouts to player.
	 */
	class ShouterTimer implements TurnListener {
		private String shoutMsg;
		private int shoutTime;
		
		public void start() {
			shoutMessage(shoutMsg);
			TurnNotifier.get().dontNotify(this);
			TurnNotifier.get().notifyInSeconds(shoutTime, this);
		}
		
		public void stop() {
			TurnNotifier.get().dontNotify(this);
		}
		
		public void onTurnReached(int currentTurn) {			
			start();
		}
		
		public void setShouts(final String msg) {
			shoutMsg = msg;
		}
		
		public void setTime(int time) {
			shoutTime = time;
		}

		public ShouterTimer(final int time, final String msg) {
			setTime(time);
			setShouts(msg);
		}

	}
	
	private final ShouterTimer shouterTimer = new ShouterTimer(-1, null);
	
	protected void changeShouts(final int time, final String msg) {
		shouterTimer.setTime(time);
		shouterTimer.setShouts(msg);
	}
	
	protected void startShouts(int time, String msg) {
		shouterTimer.setTime(time);
		shouterTimer.setShouts(msg);
		shouterTimer.start();
	}
	
	protected void stopShouts() {
		shouterTimer.stop();
	}
	

		
	
	public void phaseToDefaultPhase() {
		shoutMessage(getSwitchingToDefPhaseMessage());
		ThePiedPiper.setPhase(ThePiedPiper.getDefaultPhaseClass().getPhase());
		logger.info("ThePiedPiper quest: switch phase ("+
				ThePiedPiper.getPhase().name()+
				") to ("+
				ThePiedPiper.getPhases().get(0).getPhase().name()+
				").");
		stopShouts();
		ThePiedPiper.setNewNotificationTime(ThePiedPiper.getPhases().get(0).getMinTimeOut(),
				ThePiedPiper.getPhases().get(0).getMaxTimeOut());	
		ThePiedPiper.getPhases().get(0).prepare();
	}

	public void phaseToNextPhase(ITPPQuest nextPhase) {
		shoutMessage(getSwitchingToNextPhaseMessage());
		logger.info("ThePiedPiper quest: switch phase to ("+nextPhase.getPhase().name()+").");
		ThePiedPiper.setPhase(nextPhase.getPhase());
		stopShouts();
		ThePiedPiper.setNewNotificationTime(
				nextPhase.getMinTimeOut(), 
				nextPhase.getMaxTimeOut());
		nextPhase.prepare();
	}
	
	public void prepare() {
		
	}
	
	public int getMaxTimeOut() {
		return 0;
	}

	public int getMinTimeOut() {
		return 0;
	}
	
}

