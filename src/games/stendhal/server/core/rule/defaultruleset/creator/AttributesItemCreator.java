package games.stendhal.server.core.rule.defaultruleset.creator;

import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
import games.stendhal.server.entity.item.Item;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Create an item class via the <em>attributes</em> constructor.
 */
public class AttributesItemCreator extends AbstractItemCreator {

	public AttributesItemCreator(DefaultItem defaultItem, final Constructor< ? > construct) {
		super(defaultItem, construct);
	}

	@Override
	protected Item createObject() throws IllegalAccessException,
			InstantiationException, InvocationTargetException {
		return (Item) construct.newInstance(new Object[] { this.defaultItem.getAttributes() });
	}
}