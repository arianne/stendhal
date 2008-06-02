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

package games.stendhal.client.sound;

import games.stendhal.client.soundreview.Sound;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.Rand;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.sound.sampled.DataLine;

public class SoundObject  {

	private String name;

	private int volume;

	Map<String, String[]> soundArray;

	private double x;

	private double y;

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	private void soundInit() {
		soundArray = Sound.soundArray;
	}

	public SoundObject() {
	}
	/**
	 * Defines the distance in which the entity is heard by Player.
	 */
	protected double audibleRange = Double.POSITIVE_INFINITY;
	/**
	 * Sets the audible range as radius distance from this entity's position,
	 * expressed in coordinate units. This reflects an abstract capacity of this
	 * unit to emit sounds and influences the result of
	 * <code>getAudibleArea()</code>.
	 * 
	 * @param range
	 *            double audibility area radius in coordinate units
	 */
	public void setAudibleRange(final double range) {
		audibleRange = range;
	}
	
	/**
	 * @return the absolute world area (coordinates) to which audibility of
	 * entity sounds is confined. Returns <b>null</b> if confines do not exist
	 * (audible everywhere).
	 */
	public Rectangle2D getAudibleArea() {
		if (audibleRange == Double.POSITIVE_INFINITY) {
			return null;
		}

		double tempWidth = audibleRange * 2;
		return new Rectangle2D.Double(getX() - audibleRange, getY()
				- audibleRange, tempWidth, tempWidth);
	}
	public void setLocation(final Point2D position) {
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
	public void setName(final String name) {
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
	public void setVolume(final int volume) {
		this.volume = volume;
	}

	public DataLine playSound(String token, int volBot, int volTop, int chance) {
		if (soundArray == null) {
			soundInit();
		}
		if (Rand.rand(100) < chance) {
			if (soundArray.containsKey(token)) {

				SoundMaster.play(
						soundArray.get(token)[Rand.rand(soundArray.get(token).length)],
						x, y);

			}
		}
		return null;

	}
}
