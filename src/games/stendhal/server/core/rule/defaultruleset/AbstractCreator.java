package games.stendhal.server.core.rule.defaultruleset;

import games.stendhal.server.entity.item.Item;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

public abstract class AbstractCreator<T> {
	
	private static final Logger logger = Logger.getLogger(AbstractCreator.class);

	protected final Constructor< ? > construct;

	/**
	 * @param construct
	 */
	public AbstractCreator(Constructor<?> construct) {
		super();
		this.construct = construct;
	}

	protected abstract Object createObject() throws IllegalAccessException,
			InstantiationException, InvocationTargetException;

	public T create() {
		try {
			return (T) createObject();
		} catch (final IllegalAccessException ex) {
			logger.error("Error creating object" , ex);
		} catch (final InstantiationException ex) {
			logger.error("Error creating object", ex);
		} catch (final InvocationTargetException ex) {
			logger.error("Error creating object", ex);
		} catch (final ClassCastException ex) {
			/*
			 * Wrong type (i.e. not [subclass of] Item)
			 */
			logger.error("Implementation for is not an applicable class");
		}
	
		return null;
	}

}
