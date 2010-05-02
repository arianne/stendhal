package games.stendhal.server.actions.spell;
import static games.stendhal.common.constants.Actions.CASTSPELL;
import static games.stendhal.common.constants.Actions.TARGET;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
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
	
	public static void register() {
		CommandCenter.register(CASTSPELL, new CastSpellAction());
	}

	public void onAction(Player player, RPAction action) {
		Entity target = EntityHelper.entityFromTargetName(action.get(TARGET), player);
		Spell spell = (Spell) EntityHelper.entityFromSlot(player, action);
		spell.cast(player, target);
	}

}
