/**
 *
 * This package handles status effects such as eating, poisoned, confused.
 *
 * <h2>General structure</h2>
 *
 * <p>The class <tt>StatusList</tt> keeps track of all statuses of an rpentity.</p>
 *
 * <p>The <tt>*Status</tt>classes store the information of each status effect. This may be as
 * simple as "this status is active" or as complex as consumable statuses such as eating or poison.</p>
 *
 * <p>The logic of whether statuses of the same type stack or not is quite different. The
 * <tt>*StatusHandler</tt> take care of that.</p>
 *
 * <p>Some statuses need to do something periodically, such as modifying hp. This is done in
 * <tt>*StatusTurnListener</tt>.</p>
 *
 * <h2>Things to keep in mind</h2>
 *
 * <ul>
 * <li>The name of the attribute for the client does not start with status_ for poison, eating and choking for compatiblity reasons</li>
 * <li>The attributes for poison and eating are removed by the *StatusTurnListener in the following turn
 *     because this attribute carries the information about the hp-impact of the last event.</li>
 * </ul>
 */
package games.stendhal.server.entity.status;
