package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.Spell;

import java.util.List;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.RPObject.ID;
/**
 * Summon a spell into the spells slot for the given player.
 * 
 * @author madmetzger
 *
 */
public class SummonSpell  extends ScriptImpl{

	@Override
	public void execute(Player admin, List<String> args) {
		if(args.size() != 2) {
			admin.sendPrivateText("Usage: [character] [spell name].");
			return;
		}
		EntityManager em = SingletonRepository.getEntityManager();
		Spell spell = em.getSpell(args.get(1));
		String name = args.get(0);
		Player player = SingletonRepository.getRuleProcessor().getPlayer(name);
		RPSlot slot = player.getSlot("spells");
		ID id = null;
		for(RPObject o : slot) {
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
