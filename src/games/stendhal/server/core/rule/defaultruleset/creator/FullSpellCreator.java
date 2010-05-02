package games.stendhal.server.core.rule.defaultruleset.creator;

import games.stendhal.server.core.rule.defaultruleset.DefaultSpell;
import games.stendhal.server.entity.spell.Spell;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class FullSpellCreator extends AbstractSpellCreator {

	public FullSpellCreator(DefaultSpell defaultSpell, Constructor<?> construct) {
		super(defaultSpell, construct);
	}

	@Override
	protected Spell createObject() throws IllegalAccessException,
			InstantiationException, InvocationTargetException {
		return (Spell) construct.newInstance(defaultSpell.getName(), defaultSpell.getNature(), (Object) defaultSpell.getAmount(),
									(Object) defaultSpell.getAtk(), (Object) defaultSpell.getCooldown(),
									(Object) defaultSpell.getDef(), (Object) defaultSpell.getLifesteal(),
									(Object) defaultSpell.getMana(),
									(Object) defaultSpell.getMinimumLevel(), (Object) defaultSpell.getRange(),
									(Object) defaultSpell.getRate(), (Object) defaultSpell.getRegen());
	}

}
