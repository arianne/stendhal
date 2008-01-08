package games.stendhal.common.filter;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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

	private ArrayList<FilterCriteria<T>> allFilterCriteria = new ArrayList<FilterCriteria<T>>();

	/**
	 * Adds a FilterCriteria to be used by the filter.
	 * 
	 * @param filterCriteria
	 */
	public void addFilterCriteria(FilterCriteria<T> filterCriteria) {
		allFilterCriteria.add(filterCriteria);
	}

	/**
	 * Starts the filtering process. For each object in the collection, all
	 * FilterCriteria are called. Only if the object passes all FilterCriteria
	 * it remains in the collection. Otherwise, it is removed.
	 * 
	 * @param collection
	 */
	public void filter(Collection<T> collection) {

		if (collection != null) {
			Iterator<T> iter = collection.iterator();
			while (iter.hasNext()) {
				T o = iter.next();
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
	 * @param inputCollection
	 * @return a filtered copy of the input collection
	 */
	@SuppressWarnings("unchecked")
    public Collection<? extends T> filterCopy(Collection<? extends T> inputCollection) {

		Collection<T> outputCollection = null;

		if (inputCollection != null) {

			outputCollection = (Collection<T>) createObjectSameClass(inputCollection);

			Iterator<? extends T> iter = inputCollection.iterator();
			while (iter.hasNext()) {
				T o = iter.next();
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
	private boolean passesAllCriteria(T o) {
		for (int i = 0; i < allFilterCriteria.size(); i++) {
			FilterCriteria<T> filterCriteria = allFilterCriteria.get(i);
			if (!filterCriteria.passes(o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Call the no arguments constructor of the object passed.
	 * 
	 * @param object
	 * @return a new Instance of the same type as object passed
	 */
	@SuppressWarnings("unchecked")
	public Object createObjectSameClass(Object object) {

		Class[] NO_ARGS = new Class[0];
		Object sameClassObject = null;
		try {
			if (object != null) {
				Constructor constructor = object.getClass().getConstructor(NO_ARGS);
				sameClassObject = constructor.newInstance();
			}
		} catch (IllegalAccessException e) {
			// @todo do something
		} catch (NoSuchMethodException e) {
			// @todo do something
		} catch (InstantiationException e) {
			// @todo do something
		} catch (Exception e) {
			// @todo do something
		}
		return sameClassObject;
	}

}
