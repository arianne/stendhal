/* $Id$ */
/* found on javaworld.com */
package games.stendhal.common.filter;

/**
 * <p>Title: FilterCriteria</p>
 * <p>Description: A FilterCriteria is added to the CollectionFilter to filter
 * all the objects in the collection. </p>
 * @author David Rappoport
 * @version 1.0
 * @param <T> type of the item to check.
 */

public interface FilterCriteria<T> {

    /**
     * Implement this method to return true, if a given object in the collection
     * should pass this filter.
     * Example: Class Car has an attribute color (String). You only want Cars
     * whose color equals "red".
     * 1) Write the FilterCriteria implementation:
     * class RedColorFilterCriteria implements FilterCriteria{
     *     public boolean passes(Object o){
     *         return ((Car)o).getColor().equals("red");
     *     }
     * }
     * 2) Then add this FilterCriteria to a CollectionFilter:
     * CollectionFilter filter = new CollectionFilter();
     * filter.addFilterCriteria(new ColorFilterCriteria());
     * 3) Now filter:
     * filter.filter(carCollection);
     * @param o object
     * @return true, if a given object in the collection
     * passes this filter.
     */
    boolean passes(T o);
}
