package games.stendhal.server.actions.admin;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Blood;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.mapstuff.spawner.FlowerGrower;
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
	public void perform(final Player player, final RPAction action) {

		final Entity inspected = getTarget(player, action);

		if (inspected == null) {
			final String text = "Entity not found";

			player.sendPrivateText(text);
			return;
		}

		if (inspected instanceof Player) {
			final String text = "You can't remove players";
			player.sendPrivateText(text);
			return;
		}

		if (inspected instanceof SpeakerNPC) {
			final String text = "You can't remove SpeakerNPCs";
			player.sendPrivateText(text);
			return;
		}

        if (inspected instanceof Portal) {
            final String text = "You can't remove portals. Try blocking it with a few of /script AdminSign.class.";
            player.sendPrivateText(text);
            return;
		}

		final StendhalRPZone zone = inspected.getZone();

		if (inspected instanceof RPEntity) {
			if (inspected instanceof Creature) {
				// *destroyed creatures should not drop items
				((Creature) inspected).clearDropItemList();
			} 
			((RPEntity) inspected).onDead(player);
		} else if ((inspected instanceof Item) || (inspected instanceof FlowerGrower) || (inspected instanceof Corpse) || (inspected instanceof Blood)) {
			zone.remove(inspected);
		} else {
			player.sendPrivateText("You can't remove this type of entity");
			return;
		}

		String clazz = inspected.getRPClass().getName();
		String name = "";
		 
		if (inspected.has(_ATTR_NAME)) {
                      name = inspected.get(_ATTR_NAME);
		} 

		SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), "removed",
				name + " " + clazz, zone.getName(), Integer.toString(inspected.getX()),
				Integer.toString(inspected.getY()));

		player.sendPrivateText("Removed " + name + " " + clazz + " with ID " + action.get(_TARGETID));
	}

}
