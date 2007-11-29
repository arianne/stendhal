package games.stendhal.server.entity.npc.action;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Teleports the player to the specified location
 */
public class TeleportAction extends SpeakerNPC.ChatAction {

	private String zonename;
	private int x;
	private int y;
	private Direction direction;

	/**
	 * Creates a new TeleportAction
	 *
	 * @param zonename name of destination zone
	 * @param x x-position
	 * @param y y-position
	 * @param direction facing into this direction
	 */
	public TeleportAction(String zonename, int x, int y, Direction direction) {
		this.zonename = zonename; 
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	@Override
	public void fire(Player player, String text, SpeakerNPC engine) {
		StendhalRPZone zone = StendhalRPWorld.get().getZone(zonename);
        player.teleport(zone, x, y, direction, player);
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "Teleport<" + zonename + ", " + x + ", " + y + ", " + direction +">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false, TeleportAction.class);
	}

}