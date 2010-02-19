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

import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.client.sound.manager.AudibleCircleArea;
import games.stendhal.client.sound.manager.SoundFile;
import games.stendhal.client.sound.manager.SoundManager.Sound;
import games.stendhal.client.sound.system.Time;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.math.Algebra;
import games.stendhal.common.math.Numeric;
import marauroa.common.game.RPObject;


public class LoopedSoundSource extends InvisibleEntity {

	private Sound sound;
	private int radius;
	private int volume;
	private SoundLayer layer = SoundLayer.AMBIENT_SOUND;

	@Override
	public void onChangedAdded(RPObject object, RPObject changes) {
		// stop the current sound
		SoundSystemFacade.get().stop(sound, new Time(3, Time.Unit.SEC));

		// udpate
		super.onChangedAdded(object, changes);
		
		update(changes);
		play();
	}

	/**
	 * updates the attributes based on the RPObject values sent from the server.
	 *
	 * @param object object to read from
	 */
	private void update(RPObject object) {
		if (object.has("sound")) {
			String soundName = object.get("sound");
			sound = SoundSystemFacade.get().loadSound(soundName, "audio:/" + soundName + ".ogg", SoundFile.Type.OGG, true);
		}
		if (object.has("radius")) {
			radius = object.getInt("radius");
		}
		if (object.has("volume")) {
			volume = object.getInt("volume");
		}
		if (object.has("layer")) {
			int idx = object.getInt("layer");
			if (idx < SoundLayer.values().length) {
				layer = SoundLayer.values()[idx];
			}
		}

		if (object.has("sound") || object.has("layer")) {
			if (layer != SoundLayer.BACKGROUND_MUSIC) {
				sound = sound.clone();
			}
		}
	}

	/**
	 * plays the sound
	 */
	private void play() {
		AudibleCircleArea area = new AudibleCircleArea(Algebra.vecf((float)x, (float)y), radius / 2.0f, radius);
		float             vol  = Numeric.intToFloat(volume, 100.0f);
		SoundSystemFacade.get().play(sound, vol, 0, area, true, new Time(3, Time.Unit.SEC));
	}

	/**
	 * Release this entity. This should clean anything that isn't automatically
	 * released (such as unregister callbacks, cancel external operations, etc).
	 * 
	 * @see #initialize(RPObject)
	 */
	@Override
	public void release() {
		super.release();
		SoundSystemFacade.get().stop(sound, new Time(3, Time.Unit.SEC));
	}
}
