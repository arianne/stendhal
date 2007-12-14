package games.stendhal.server.entity.npc.fsm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * easy access to a list of transitions for debugging
 * 
 * @author hendrik
 */
public class TransitionList {
	private List<Transition> transitions;

	/**
	 * Creates a new TransitionList
	 * 
	 * @param transitions
	 *            list of transitions
	 */
	public TransitionList(List<Transition> transitions) {
		this.transitions = transitions;
	}

	/**
	 * gets all source states
	 * 
	 * @return Set of source states
	 */
	public Set<Integer> getSourceStates() {
		Set<Integer> res = new HashSet<Integer>();
		for (Transition transition : transitions) {
			res.add(Integer.valueOf(transition.getState()));
		}
		return res;
	}

	/**
	 * returns a set of triggers for a given source state
	 * 
	 * @param state
	 *            source state
	 * @return set of triggers
	 */
	public Set<String> getTriggersForState(int state) {
		Set<String> res = new HashSet<String>();
		for (Transition transition : transitions) {
			if (transition.getState() == state) {
				res.add(transition.getTrigger());
			}
		}
		return res;
	}

	/**
	 * returns a list of transitions for this state-trigger pair.
	 * 
	 * @param state
	 *            source state
	 * @param trigger
	 *            trigger
	 * @return list of transitions
	 */
	public List<Transition> getTransitionsForStateAndTrigger(int state,
			String trigger) {
		List<Transition> res = new LinkedList<Transition>();
		for (Transition transition : transitions) {
			if ((transition.getState() == state)
					&& (transition.getTrigger().equals(trigger))) {
				res.add(transition);
			}
		}
		return res;
	}
}
