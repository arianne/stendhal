package games.stendhal.server.actions.equip;

import games.stendhal.common.EquipActionConsts;
import games.stendhal.common.Grammar;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class EquipAction extends EquipmentAction {

	/**
	 * registers "equip" action processor.
	 */
	public static void register() {
		CommandCenter.register("equip", new EquipAction());
	}
	@Override
	protected void execute(final Player player, final RPAction action, final SourceObject source) {
		// get source and check it
	
	
		logger.debug("Getting entity name");
		// is the entity unbound or bound to the right player?
		final Entity entity = source.getEntity();
		final String itemName = source.getEntityName();
		logger.debug("Checking minimum level");
		// check minimum level
		if (entity.has("min_level")
				&& (player.getLevel() < entity.getInt("min_level"))) {
			player.sendPrivateText("You are not experienced enough to use this "
					+ itemName);
			return;
		}
	
		logger.debug("Checking if entity is bound");
		if (entity instanceof Item) {
			final Item item = (Item) entity;
			if (item.isBound() && !item.isBoundTo(player)) {
				player.sendPrivateText("This " + itemName
						+ " is a special reward for " + item.getBoundTo()
						+ ". You do not deserve to use it.");
				return;
			}
			
		}
	
		logger.debug("Checking destination");
		// get destination and check it
		final DestinationObject dest = new DestinationObject(action, player);
		if (dest.isInvalidMoveable(player, EquipActionConsts.MAXDISTANCE, validContainerClassesList)) {
			// destination is not valid
			logger.debug("Destination is not valid");
			return;
		}
	
		logger.debug("Equip action agreed");
	
		// looks good
		if (source.moveTo(dest, player)) {
			int amount = 1;
			if (entity instanceof StackableItem) {
				amount = ((StackableItem) entity).getQuantity();
			}

			// players sometimes accidentally drop items into corpses, so inform about all drops into a corpse 
			// which aren't just a movement from one corpse to another.
			// we could of course specifically preclude dropping into corpses, but that is undesirable.
			if (dest.isContainerCorpse() && !source.isContainerCorpse()) {
					player.sendPrivateText("For your information, you just dropped " 
							+ Grammar.quantityplnounWithHash(amount,entity.getTitle()) + " into a corpse you are stood next to.");
			}
			
			new GameEvent(player.getName(), "equip", itemName, source.getSlot(), dest.getSlot(), Integer.toString(amount)).raise();
	
			player.updateItemAtkDef();
		}
	}

}
