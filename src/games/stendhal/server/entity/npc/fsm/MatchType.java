package games.stendhal.server.entity.npc.fsm;

/**
 * How strong possible transitions should be matched.
 */
public enum MatchType {

	/** a transition that can happen from any state */
	ABSOLUTE_JUMP,

	/** a transition whose exspected input matches excatly to the actual input */
	EXACT_MATCH,

	/** a transition whose exspected input is very similary to the actual input */
	SIMILAR_MATCH;
}
