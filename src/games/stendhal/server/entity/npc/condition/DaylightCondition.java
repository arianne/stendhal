package games.stendhal.server.entity.npc.condition;

import java.util.Arrays;
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.rp.DaylightPhase;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Checks the current day light phase.
 *
 * @author hendrik
 */
@Dev(category=Category.ENVIRONMENT, label="Time?")
public class DaylightCondition implements ChatCondition {

	private final List<DaylightPhase> daylightPhases;

	/**
	 * creates a new DaytimeCondition
	 *
	 * @param daylightPhases one or more daylight phases
	 */
	public DaylightCondition(final DaylightPhase... daylightPhases) {
		super();
		this.daylightPhases = Arrays.asList(daylightPhases);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		return daylightPhases.contains(DaylightPhase.current());
	}

	@Override
	public int hashCode() {
		return 43633 * daylightPhases.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DaylightCondition)) {
			return false;
		}
		DaylightCondition other = (DaylightCondition) obj;
		return daylightPhases.equals(other.daylightPhases);
	}

}
