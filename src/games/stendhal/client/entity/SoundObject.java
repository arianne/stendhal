/*
 *  SoundObject in games.stendhal.client.entity
 *  file: SoundObject.java
 * 
 *  Project stendhal
 *  @author Jane Hunt
 *  Created 21.01.2006
 *  Version
 * 
 This program is free software. You can use, redistribute and/or modify it under 
 the terms of the GNU General Public License as published by the Free Software 
 Foundation, version 2 of the License.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 Place - Suite 330, Boston, MA 02111-1307, USA, or go to
 http://www.gnu.org/copyleft/gpl.html.
 */

package games.stendhal.client.entity;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

public class SoundObject extends Entity {

	private String name;

	private int volume;

	public SoundObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SoundObject(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public SoundObject(RPObject object) throws AttributeNotFoundException {
		super(object);
		// TODO Auto-generated constructor stub
	}

	public SoundObject(Point2D soundPos, int radius) {
		x = soundPos.getX();
		y = soundPos.getY();
		audibleRange = radius;
    }

	@Override
	public Rectangle2D getArea() {
		return null;
	}

	@Override
	public Rectangle2D getDrawedArea() {
		return null;
	}

	// TODO: remove Soundobject from hierarchie
	@Override
	public ActionType defaultAction() {
		return null;
	}

	public void setLocation(Point2D position) {
		x = position.getX();
		y = position.getY();
	}

	/**
	 * @return returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name -
	 *            the new value for name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return returns the volume.
	 */
	public int getVolume() {
		return volume;
	}

	/**
	 * @param volume -
	 *            the new value for volume to set.
	 */
	public void setVolume(int volume) {
		this.volume = volume;
	}

	@Override
	public int getZIndex() {
		return 1000;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see games.stendhal.client.entity.Entity#buildOfferedActions(java.util.List)
	 */
	@Override
	protected void buildOfferedActions(List<String> list) {
		list.clear();
	}

}
