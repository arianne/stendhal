package games.stendhal.server.entity.npc.fsm;

/**
 * How strong possible transitions should be matched.
 */
public enum MatchType {

	/** a transition that can happen from any state */
	ABSOLUTE_JUMP,

	/** a transition whose expected input matches exactly to the actual input */
	EXACT_MATCH,

	/** a transition whose expected input is very similar to the actual input */
	SIMILAR_MATCH;
}
