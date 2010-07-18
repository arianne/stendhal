package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Enables a client side feature.
 */
public class EnableFeatureAction implements ChatAction {

	private final String feature;
	private final String value;

	/**
	 * Creates a new EnableFeatureAction.
	 * 
	 * @param feature name of feature
	 */
	public EnableFeatureAction(final String feature) {
		this(feature, "");
	}

	/**
	 * Creates a new EnableFeatureAction.
	 * 
	 * @param feature name of feature
	 * @param value value of feature (<code>null</code> means disabled, "" means enabled without value)
	 */
	public EnableFeatureAction(final String feature, final String value) {
		this.feature = feature;
		this.value = value;
	}


	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		player.setFeature(feature, value);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("enable feature <");
		sb.append(feature);
		sb.append("m ");
		sb.append(value);
		sb.append(">");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				EnableFeatureAction.class);
	}

}
