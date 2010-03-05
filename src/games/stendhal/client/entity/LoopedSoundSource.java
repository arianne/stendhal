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
import games.stendhal.client.sound.manager.SoundFile.Type;
import games.stendhal.client.sound.manager.SoundManagerNG.Sound;
import games.stendhal.client.sound.system.Time;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.math.Algebra;
import games.stendhal.common.math.Numeric;
import marauroa.common.game.RPObject;

public class LoopedSoundSource extends InvisibleEntity {

	private String                  soundName      = null;
	private Sound                   sound          = null;
	private SoundSystemFacade.Group group          = null;
	private Time                    fadingDuration = new Time();
	private int radius;
	private float volume;

	@Override
	public void onChangedAdded(RPObject object, RPObject changes) {
		// stop the current sound
		SoundSystemFacade.get().stop(sound, fadingDuration);

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

		boolean streaming = false;

		if (object.has("radius")) {
			radius = object.getInt("radius");
		}
		if (object.has("volume")) {
			volume = Numeric.intToFloat(object.getInt("volume"), 100.0f);
		}
		if (object.has("layer")) {
			int idx = object.getInt("layer");
			SoundLayer layer = null;

			if (idx < SoundLayer.values().length) {
				layer = SoundLayer.values()[idx];
			}

			switch(layer)
			{
			case AMBIENT_SOUND:
				group = SoundSystemFacade.get().getGroup("ambient");
				break;
			case BACKGROUND_MUSIC:
				group = SoundSystemFacade.get().getGroup("music");
				break;
			case CREATURE_NOISE:
				group = SoundSystemFacade.get().getGroup("creature");
				break;
			case FIGHTING_NOISE:
				group = SoundSystemFacade.get().getGroup("sfx");
				break;
			case USER_INTERFACE:
				group = SoundSystemFacade.get().getGroup("gui");
				break;
			}

			fadingDuration.set(100, Time.Unit.MILLI);

			if (layer == SoundLayer.BACKGROUND_MUSIC) {
				streaming = true;
				fadingDuration.set(3, Time.Unit.SEC);
			}
		}
		if (object.has("sound")) {
			soundName = object.get("sound");
			group.loadSound(soundName, "audio:/" + soundName + ".ogg", Type.OGG, streaming);
		}
	}

	/**
	 * plays the sound
	 */
	private void play() {
		AudibleCircleArea area = new AudibleCircleArea(Algebra.vecf((float) x, (float) y), radius / 2.0f, radius);
		boolean cloneSound = group != SoundSystemFacade.get().getGroup("music");
		sound = group.play(soundName, volume, 0, area, fadingDuration, true, cloneSound);
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
		SoundSystemFacade.get().stop(sound, fadingDuration);
	}
}
