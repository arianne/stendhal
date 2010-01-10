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

import games.stendhal.client.sound.SoundLayer;
import games.stendhal.client.soundreview.Sound;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.Rand;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.sound.sampled.DataLine;

public class SoundObject extends InvisibleEntity {

	private String name;

	private int volume;

	Map<String, String[]> soundArray;

	private void soundInit() {
		soundArray = Sound.soundArray;
	}

	public SoundObject() {
	}

	public SoundObject(final int x, final int y) {
		super();
		this.x = x;
		this.y = y;

	}

	public SoundObject(final Point2D soundPos, final int radius) {
		x = soundPos.getX();
		y = soundPos.getY();
		audibleRange = radius;
	}

	@Override
	public Rectangle2D getArea() {
		return null;
	}

	public void setLocation(final Point2D position) {
		x = position.getX();
		y = position.getY();
	}

	/**
	 * @return returns the name.
	 */
	@Override
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

	public DataLine playSound(final String token, final int volBot, final int volTop, final int chance) {
		if (soundArray == null) {
			soundInit();
		}
		if (Rand.rand(100) < chance) {
			if (soundArray.containsKey(token)) {

				SoundMaster.play(SoundLayer.AMBIENT_SOUND, 
						soundArray.get(token)[Rand.rand(soundArray.get(token).length)],
						x, y);

			}
		}
		return null;

	}
}
