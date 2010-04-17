package games.stendhal.server.core.rule.defaultruleset;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Create an item class via the full arguments (<em>name, clazz,
 * subclazz, attributes</em>)
 * constructor.
 */
class FullItemCreator extends AbstractItemCreator {

	public FullItemCreator(DefaultItem defaultItem, final Constructor< ? > construct) {
		super(defaultItem, construct);
	}

	@Override
	protected Object createObject() throws IllegalAccessException,
			InstantiationException, InvocationTargetException {
		return construct.newInstance(new Object[] { this.defaultItem.getItemName(), this.defaultItem.getItemClass(), this.defaultItem.getItemSubClass(),
				this.defaultItem.attributes });
	}
}