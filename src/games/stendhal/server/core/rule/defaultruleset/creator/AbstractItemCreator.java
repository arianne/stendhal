package games.stendhal.server.core.rule.defaultruleset.creator;

import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
import games.stendhal.server.entity.item.Item;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

/**
 * Base item creator (using a constructor).
 */
abstract class AbstractItemCreator extends AbstractCreator<Item>{
	
	static final Logger logger = Logger.getLogger(AbstractItemCreator.class);

	/**
	 * 
	 */
	final DefaultItem defaultItem;
	
	public AbstractItemCreator(DefaultItem defaultItem, final Constructor< ? > construct) {
		super(construct);
		this.defaultItem = defaultItem;
	}
}