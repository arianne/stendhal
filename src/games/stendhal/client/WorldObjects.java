/*
 *  WorldObjects in games.stendhal.client
 *  file: WorldObjects.java
 *
 *  Project stendhal
 *  @author Jane Hunt
 *  Created 23.01.2006
 *  Version
 *
 This program is free software. You can use, redistribute and/or modify it under
 the termsof the GNU General Public License as published by the Free Software
 Foundation, version 2 of the License.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 Place - Suite 330, Boston, MA 02111-1307, USA, or go to
 http://www.gnu.org/copyleft/gpl.html.
 */

package games.stendhal.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Class meant to work as a global event multiplexer for world objects and
 * zones. Events shall be reported dependent on succession of game flow and in
 * particular free from events caused by perceptions and sync operations of the
 * lower client layers.
 * 
 * Currently works for zone events.
 */
public class WorldObjects {

	private static List<WorldListener> worldListeners = new ArrayList<WorldListener>();

	public static interface WorldListener {

		/** Called when a world zone has been loaded. */
		void zoneEntered(String zoneName);

		/** Called when a world zone has been unloaded. */
		void zoneLeft(String zoneName);

		/** Called when the player arrives at a map location. */
		void playerMoved();
	}

	/** Adds a WorldListener to this event distributor. */
	public static void addWorldListener(WorldListener a) {
		synchronized (worldListeners) {
			if (!worldListeners.contains(a)) {
				worldListeners.add(a);
			}
		}
	}

	/** Removes a WorldListener from this event distributor. */
	public static void removeWorldListener(WorldListener a) {
		synchronized (worldListeners) {
			worldListeners.remove(a);
		}
	}

	/** Create a zone-entered event. */
	public static void fireZoneEntered(String zoneName) {
		synchronized (worldListeners) {
			for (WorldListener wl : worldListeners) {
				wl.zoneEntered(zoneName);
			}
		}
	} // fireZoneEntered

	/** Create a zone-left event. */
	public static void fireZoneLeft(String zoneName) {
		synchronized (worldListeners) {
			for (WorldListener wl : worldListeners) {
				wl.zoneLeft(zoneName);
			}
		}
	} // fireZoneLeft

	/** Create a player-moved event. */
	public static void firePlayerMoved() {

		synchronized (worldListeners) {
			for (WorldListener wl : worldListeners) {
				wl.playerMoved();
			}
		}
	} // firePlayerMoved

}
