package games.stendhal.server.core.rule.defaultruleset.creator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

public abstract class AbstractCreator<T> {
	
	private static final Logger logger = Logger.getLogger(AbstractCreator.class);

	protected final Constructor< ? > construct;
	
	private final String creatorFor;

	/**
	 * @param construct
	 */
	public AbstractCreator(Constructor<?> construct, String creatorFor) {
		super();
		this.construct = construct;
		this.creatorFor = creatorFor;
	}

	protected abstract T createObject() throws IllegalAccessException,
			InstantiationException, InvocationTargetException;

	public T create() {
		try {
			return createObject();
		} catch (final IllegalAccessException ex) {
			logger.error("Error creating object: Used constructor is not accessible." , ex);
		} catch (final InstantiationException ex) {
			logger.error("Error creating object: Object cannot be instantiated (i.e. class may be abstract)", ex);
		} catch (final InvocationTargetException ex) {
			logger.error("Error creating object: Exception thrown during constructor call.", ex.getCause());
		} catch (final ClassCastException ex) {
			/*
			 * Wrong type (i.e. not [subclass of])
			 */
			logger.error("Implementation for is no an subclass of "+creatorFor );
		}
	
		return null;
	}

}
