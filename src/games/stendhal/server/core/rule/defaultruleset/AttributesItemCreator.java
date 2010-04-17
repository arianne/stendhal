package games.stendhal.server.core.rule.defaultruleset;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Create an item class via the <em>attributes</em> constructor.
 */
class AttributesItemCreator extends AbstractItemCreator {

	public AttributesItemCreator(DefaultItem defaultItem, final Constructor< ? > construct) {
		super(defaultItem, construct);
	}

	@Override
	protected Object createObject() throws IllegalAccessException,
			InstantiationException, InvocationTargetException {
		return construct.newInstance(new Object[] { this.defaultItem.attributes });
	}
}