package games.stendhal.server.actions.spell;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.Spell;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;
/**
 * Triggered from casting a spell client side
 * 
 * @author madmetzger
 */
public class CastSpellAction implements ActionListener {

	public void onAction(Player player, RPAction action) {
		Entity target = EntityHelper.entityFromTargetName(action.get("target"), player);
		Spell spell = (Spell) EntityHelper.entityFromSlot(player, action);
		spell.cast(player, target);
	}

}
