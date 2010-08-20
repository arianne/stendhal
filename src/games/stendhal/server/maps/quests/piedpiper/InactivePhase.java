package games.stendhal.server.maps.quests.piedpiper;

import java.util.Map;


public class InactivePhase extends TPPQuest {
	
	private int minPhaseChangeTime;
	private int maxPhaseChangeTime;
	
	public InactivePhase(Map<String, Integer> timings) {
		super(timings);
		minPhaseChangeTime=timings.get(INACTIVE_TIME_MIN);
		maxPhaseChangeTime=timings.get(INACTIVE_TIME_MAX);		
	}


	public int getMinTimeOut() {
		return minPhaseChangeTime;
	}
	

	public int getMaxTimeOut() {
		return maxPhaseChangeTime;
	}
	

	

	public void phaseToDefaultPhase() {
		// not used		
	}


	public void phaseToNextPhase(ITPPQuest nextPhase) {
		super.phaseToNextPhase(nextPhase);
	}
	

	public void prepare() {

	}
	

	public TPP_Phase getPhase() {
		return TPP_Phase.TPP_INACTIVE;
	}
	
}
