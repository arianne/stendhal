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
import java.util.Iterator;
import java.util.List;

/**
 * Class meant to work as a global event multiplexer for world objects and zones.
 * Events shall be reported dependent on succession of game flow and in
 * particular free from events caused by perceptions and sync operations
 * of the lower client layers.
 *  
 *  Currently works for zone events.  
 */
public class WorldObjects
{
   private static List<WorldListener> worldListeners = new ArrayList<WorldListener>();

   public static interface WorldListener
   {
      public void zoneEntered ( String zoneName );
      public void zoneLeft ( String zoneName );
   }

   
   public static void addWorldListener ( WorldListener a )
   {
      synchronized ( worldListeners )
      {
         if ( !worldListeners.contains( a ) )
            worldListeners.add( a );
      }
   }
   
   public static void removeWorldListener ( WorldListener a )
   {
      synchronized ( worldListeners )
      {
         worldListeners.remove( a );
      }
   }

   public static void fireZoneEntered ( String zoneName )
   {
      Iterator it;
      
      synchronized ( worldListeners )
      {
         for ( it = worldListeners.iterator(); it.hasNext(); )
         {
            ((WorldListener)it.next()).zoneEntered( zoneName );
         }
      }      
   }  // fireZoneEntered
   
   public static void fireZoneLeft ( String zoneName )
   {
      Iterator it;
      
      synchronized ( worldListeners )
      {
         for ( it = worldListeners.iterator(); it.hasNext(); )
         {
            ((WorldListener)it.next()).zoneLeft( zoneName );
         }
      }      
   }  // fireZoneLeft
}
