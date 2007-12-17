package games.stendhal.common.filter;

/**
 * <p>Title: FilterCriteria</p>
 * <p>Description: A FilterCriteria is added to the CollectionFilter to filter
 * all the objects in the collection. </p>
 * @author David Rappoport
 * @version 1.0
 */

public interface FilterCriteria {

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
     * @param o
     * @return
     */
    public boolean passes(Object o);
}