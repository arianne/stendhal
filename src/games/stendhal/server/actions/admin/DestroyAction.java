package games.stendhal.server.actions.admin;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class DestroyAction extends AdministrationAction {

	private static final String _TARGETID = "targetid";
	private static final String _ATTR_NAME = "name";

	public static void register() {
		CommandCenter.register("destroy", new DestroyAction(), 700);

	}

	@Override
	public void perform(Player player, RPAction action) {

		Entity inspected = getTarget(player, action);

		if (inspected == null) {
			String text = "Entity not found";

			player.sendPrivateText(text);
			return;
		}

		if (inspected instanceof Player) {
			String text = "You can't remove players";
			player.sendPrivateText(text);
			return;
		}

		if (inspected instanceof SpeakerNPC) {
			String text = "You can't remove SpeakerNPCs";
			player.sendPrivateText(text);
			return;
		}

		StendhalRPZone zone = inspected.getZone();

		if (inspected instanceof RPEntity) {
			((RPEntity) inspected).onDead(player);
		} else if ((inspected instanceof Item) || (inspected instanceof Portal)) {
			zone.remove(inspected);
		} else {
			player.sendPrivateText("You can't remove this type of entity");
			return;
		}

		String name = inspected.getRPClass().getName();
		if (inspected.has(_ATTR_NAME)) {
			name = inspected.get(_ATTR_NAME);
		}

		SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), "removed",
				name, zone.getName(), Integer.toString(inspected.getX()),
				Integer.toString(inspected.getY()));

		player.sendPrivateText("Removed entity " + action.get(_TARGETID));
	}

}
