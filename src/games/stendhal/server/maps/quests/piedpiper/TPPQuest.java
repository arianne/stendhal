/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.piedpiper;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.maps.quests.ThePiedPiper;

public class TPPQuest implements ITPPQuest {

	protected static final Logger logger = Logger.getLogger(TPPQuest.class);
	protected Map<String, Integer> timings;

	@Override
	public String getSwitchingToNextPhaseMessage() {
		return null;
	}

	@Override
	public String getSwitchingToDefPhaseMessage() {
		return null;
	}

	@Override
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
			SingletonRepository.getRuleProcessor().tellAllPlayers(NotificationType.PRIVMSG, msg);
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

		@Override
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


	@Override
	public void phaseToDefaultPhase(List<String> comments) {
		shoutMessage(getSwitchingToDefPhaseMessage());
		ThePiedPiper.setPhase(ThePiedPiper.getDefaultPhaseClass().getPhase());
		logger.info("ThePiedPiper quest: switch phase ("+
				ThePiedPiper.getPhase().name()+
				") to ("+
				ThePiedPiper.getDefaultPhaseClass().getPhase().name());
		stopShouts();
		ThePiedPiper.setNewNotificationTime(
				ThePiedPiper.getDefaultPhaseClass().getMinTimeOut(),
				ThePiedPiper.getDefaultPhaseClass().getMaxTimeOut());
		ThePiedPiper.getDefaultPhaseClass().prepare();
		if(!comments.isEmpty()) {
			new GameEvent(null, "raid", comments).raise();
		}
	}

	@Override
	public void phaseToNextPhase(ITPPQuest nextPhase, List<String> comments) {
		shoutMessage(getSwitchingToNextPhaseMessage());
		if(!comments.isEmpty()) {
			new GameEvent(null, "raid", comments).raise();
		}
		logger.info("ThePiedPiper quest: switch phase to ("+nextPhase.getPhase().name()+").");
		ThePiedPiper.setPhase(nextPhase.getPhase());
		stopShouts();
		ThePiedPiper.setNewNotificationTime(
				nextPhase.getMinTimeOut(),
				nextPhase.getMaxTimeOut());
		nextPhase.prepare();
	}

	@Override
	public void prepare() {

	}

	@Override
	public int getMaxTimeOut() {
		return 0;
	}

	@Override
	public int getMinTimeOut() {
		return 0;
	}

}
