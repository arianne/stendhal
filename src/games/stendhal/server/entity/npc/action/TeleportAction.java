package games.stendhal.server.entity.npc.action;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Teleports the player to the specified location.
 */
public class TeleportAction implements ChatAction {

	private final String zonename;
	private final int x;
	private final int y;
	private final Direction direction;

	/**
	 * Creates a new TeleportAction.
	 * 
	 * @param zonename
	 *            name of destination zone
	 * @param x
	 *            x-position
	 * @param y
	 *            y-position
	 * @param direction
	 *            facing into this direction
	 */
	public TeleportAction(final String zonename, final int x, final int y, final Direction direction) {
		this.zonename = zonename;
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(zonename);
		player.teleport(zone, x, y, direction, player);
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "Teleport<" + zonename + ", " + x + ", " + y + ", " + direction
				+ ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				TeleportAction.class);
	}

}
