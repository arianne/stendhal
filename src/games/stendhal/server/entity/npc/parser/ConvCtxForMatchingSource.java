package games.stendhal.server.entity.npc.parser;

/**
 * Create a ConversationContext with de-activated Expression merging and word ignoring.
 *
 * @author Martin Fuchs
 */
public final class ConvCtxForMatchingSource extends ConversationContext {

	public ConvCtxForMatchingSource() {
		mergeExpressions = false;
		ignoreIgnorable = false;
	}

}
