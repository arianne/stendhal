package games.stendhal.server.entity.npc.action;

import static com.google.common.base.Preconditions.checkNotNull;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Action to disable a feature
 *
 * @author madmetzger
 */
@Dev(category=Category.OTHER, label="Feature-")
public class DisableFeatureAction implements ChatAction {

	private final String feature;

	/**
	 * Creates a new DisableFeatureAction.
	 *
	 * @param feature name of feature
	 */
	public DisableFeatureAction(final String feature) {
		this.feature = checkNotNull(feature);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		player.unsetFeature(this.feature);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("disable feature <");
		sb.append(this.feature);
		sb.append(">");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return 5107 * feature.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DisableFeatureAction)) {
			return false;
		}
		DisableFeatureAction other = (DisableFeatureAction) obj;
		return feature.equals(other.feature);
	}

}
