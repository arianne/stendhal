package games.stendhal.server.core.rule.defaultruleset;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Create an item class via the default constructor.
 */
class DefaultItemCreator extends AbstractItemCreator {

	public DefaultItemCreator(DefaultItem defaultItem, final Constructor< ? > construct) {
		super(defaultItem, construct);
	}

	@Override
	protected Object createObject() throws IllegalAccessException,
			InstantiationException, InvocationTargetException {
		return construct.newInstance(new Object[] {});
	}
}