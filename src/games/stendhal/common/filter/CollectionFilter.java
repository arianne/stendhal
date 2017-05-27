/* $Id$ */
/* found on javaworld.com */
package games.stendhal.common.filter;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * <p>
 * Title: CollectionFilter.
 * </p>
 * <p>
 * Description:
 * </p>
 *
 * @author David Rappoport
 * @version 1.0
 * @param <T> generic type of collection elements
 */

public class CollectionFilter<T> {
	private static Logger logger = Logger.getLogger(CollectionFilter.class);

	private final ArrayList<FilterCriteria<T>> allFilterCriteria = new ArrayList<FilterCriteria<T>>();

	/**
	 * Adds a FilterCriteria to be used by the filter.
	 *
	 * @param filterCriteria criteria for the filter
	 */
	public void addFilterCriteria(final FilterCriteria<T> filterCriteria) {
		allFilterCriteria.add(filterCriteria);
	}

	/**
	 * Starts the filtering process. For each object in the collection, all
	 * FilterCriteria are called. Only if the object passes all FilterCriteria
	 * it remains in the collection. Otherwise, it is removed.
	 *
	 * @param collection collection to filter
	 */
	public void filter(final Collection<T> collection) {

		if (collection != null) {
			final Iterator<T> iter = collection.iterator();
			while (iter.hasNext()) {
				final T o = iter.next();
				if (!passesAllCriteria(o)) {
					iter.remove();
				}
			}
		}
	}

	/**
	 * This method does the same as the filter method. However, a copy of the
	 * original collection is created and filtered. The original collection
	 * remains unchanged and the copy is returned. Only use this method for
	 * collection classes that define a default constructor
	 *
	 * @param inputCollection collection to copy
	 * @return a filtered copy of the input collection
	 */
	@SuppressWarnings("unchecked")
    public Collection< ? extends T> filterCopy(final Collection< ? extends T> inputCollection) {

		Collection<T> outputCollection = null;

		if (inputCollection != null) {

			outputCollection = (Collection<T>) createObjectSameClass(inputCollection);

			final Iterator< ? extends T> iter = inputCollection.iterator();
			while (iter.hasNext()) {
				final T o = iter.next();
				if (passesAllCriteria(o)) {
					outputCollection.add(o);
				}
			}
		}
		return outputCollection;
	}

	/**
	 * Makes sure the specified object passes all FilterCriteria's passes
	 * method.
	 *
	 * @param o
	 * @return true if all criteria are passed
	 */
	private boolean passesAllCriteria(final T o) {
		for (int i = 0; i < allFilterCriteria.size(); i++) {
			final FilterCriteria<T> filterCriteria = allFilterCriteria.get(i);
			if (!filterCriteria.passes(o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Call the no arguments constructor of the object passed.
	 *
	 * @param object object to use as template
	 * @return a new Instance of the same type as object passed
	 */
	private Object createObjectSameClass(final Object object) {

		final Class<?>[] NO_ARGS = new Class[0];
		Object sameClassObject = null;
		try {
			if (object != null) {
				final Constructor<?> constructor = object.getClass().getConstructor(NO_ARGS);
				sameClassObject = constructor.newInstance();
			}
		} catch (final IllegalAccessException e) {
			logger.error(e, e);
		} catch (final NoSuchMethodException e) {
			logger.error(e, e);
		} catch (final InstantiationException e) {
			logger.error(e, e);
		} catch (final Exception e) {
			logger.error(e, e);
		}
		return sameClassObject;
	}

}
