package games.stendhal.server.entity.npc.fsm;

/**
 * How strong possible transitions should be matched.
 */
public enum MatchType {

	/** a transition whose expected input matches exactly to the actual input, used to
	 * distinguish between words in singular and plural form, e.g. "cloak" and "cloaks" */
	EXACT_MATCH,

	/** a transition whose normalized expected input matches the normalized input. */
	NORMALIZED_MATCH,

	/** a transition whose expected input is very similar to the actual input. */
	SIMILAR_MATCH,

	/** a transition that can happen from any state with exact text match. */
	ABSOLUTE_JUMP,

	/** a transition that can happen from any state with normalized text match. */
	NORMALIZED_JUMP,

	/** a transition that can happen from any state with similar text match. */
	SIMILAR_JUMP

}
