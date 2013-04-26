package games.stendhal.server.maps.quests.piedpiper;

import games.stendhal.server.maps.quests.ThePiedPiper;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

/**
 * helper class for normal switching phase to next phase, 
 * wrapper of observer around a function.
 * 
 * @author yoriy
 */
public final class PhaseSwitcher implements Observer {

	private ITPPQuest myphase; 
	
	@Override
	public void update(Observable arg0, Object arg1) {
		myphase.phaseToNextPhase(
				ThePiedPiper.getNextPhaseClass(ThePiedPiper.getPhase()), 
				Arrays.asList("normal switching"));			
	}
	
	public PhaseSwitcher(ITPPQuest phase) {
		myphase = phase;
	}
	
}
