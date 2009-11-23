package games.stendhal.server.actions.equip;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class DropAction extends EquipmentAction {
	public static void register() {
		CommandCenter.register("drop", new DropAction());
	}
	
	@Override
	protected void execute(final Player player, final RPAction action, final SourceObject source) {
	
		// get destination and check it
		final DestinationObject dest = new DestinationObject(action, player);
		if (!dest.checkDistance(player, EquipUtil.MAX_THROWING_DISTANCE)) {
		    player.sendPrivateText("You cannot throw that far.");
		    return;
		}
		
		if (!dest.isValid() || !dest.checkClass(validContainerClassesList)) {
			logger.warn("destination is invalid. action is: " + action);
			// destination is not valid
			return;
		}
	
		final Entity entity = source.getEntity();
		final String itemName = source.getEntityName();
		if (entity instanceof Item) {
			final Item itemobject = (Item) entity;
				if (itemobject.isBound() && dest.isTradingTable(entity, player)) {
					player.sendPrivateText("You cannot trade items which are player bound to yourself as the other player cannot use them.");
					logger.warn(player.getName() + " was trying to place a bound object on the semos bank trading table.");
					return;
				}
		}
	
		if (source.moveTo(dest, player)) {
			if (entity instanceof Item) {
				final Item item = (Item) entity;
				if (item.isBound()) {
	
					player.sendPrivateText("You put a valuable item on the ground. Please note that it will expire in "
							+ (Item.DEGRADATION_TIMEOUT / 60)
							+ " minutes, as all items do. But in this case there is no way to restore it.");
				}
			}
			
			final int amount = source.getQuantity();
			new GameEvent(player.getName(), "drop", itemName, source.getSlot(), dest.getSlot(), Integer.toString(amount)).raise();
			player.updateItemAtkDef();
		}
	}

	

}
