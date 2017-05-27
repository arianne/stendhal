package games.stendhal.server.script;

import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.Spell;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;
import marauroa.common.game.RPSlot;
/**
 * Summon a spell into the spells slot for the given player.
 *
 * @author madmetzger
 *
 */
public class SummonSpell  extends ScriptImpl{

	@Override
	public void execute(final Player admin, final List<String> args) {
		if(args.size() != 2) {
			admin.sendPrivateText("Usage: [character] [spell name].");
			return;
		}
		final EntityManager em = SingletonRepository.getEntityManager();
		final Spell spell = em.getSpell(args.get(1));
		if(spell == null) {
			admin.sendPrivateText("The spell "+ args.get(1) +" was not found.");
			return;
		}
		final String name = args.get(0);
		final Player player = SingletonRepository.getRuleProcessor().getPlayer(name);
		final RPSlot slot = player.getSlot("spells");
		ID id = null;
		for(final RPObject o : slot) {
			if(spell.getName().equalsIgnoreCase(o.get("name"))) {
				id = o.getID();
			}
		}
		if (id != null) {
			slot.remove(id);
		}
		slot.add(spell);
	}

}
