package games.stendhal.server.entity.npc.action;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

public final class NPCSetDirection implements ChatAction {
	public NPCSetDirection(Direction direction) {
		super();
		this.direction = direction;
	}

	private Direction direction;

	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		((ActiveEntity) npc.getEntity()).setDirection(direction);
	}
}