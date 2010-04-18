package games.stendhal.server.core.rule.defaultruleset.creator;

import games.stendhal.server.core.rule.defaultruleset.DefaultSpell;
import games.stendhal.server.entity.spell.Spell;

import java.lang.reflect.Constructor;

abstract class AbstractSpellCreator extends AbstractCreator<Spell> {
	
	protected final DefaultSpell defaultSpell;

	public AbstractSpellCreator(DefaultSpell defaultSpell, Constructor<?> construct) {
		super(construct, "Spell");
		this.defaultSpell = defaultSpell;
	}

}
