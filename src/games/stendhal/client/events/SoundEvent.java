package games.stendhal.client.events;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.sound.SoundGroup;
import games.stendhal.client.sound.manager.AudibleCircleArea;
import games.stendhal.client.sound.manager.SoundFile.Type;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.math.Algebra;
import games.stendhal.common.math.Numeric;

/**
 * plays a sound
 *
 * @author hendrik
 */
public class SoundEvent extends Event<Entity> {

	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		SoundLayer layer = SoundLayer.AMBIENT_SOUND;
		int idx = event.getInt("layer");
		if (idx < SoundLayer.values().length) {
			layer = SoundLayer.values()[idx];
		}
		float volume = 1.0f;
		if (event.has("volume")) {
			volume = Numeric.intToFloat(event.getInt("volume"), 100.0f);
		}
		int radius = 100000;
		if (event.has("radius")) {
			radius = event.getInt("radius");
		}

		SoundGroup group = ClientSingletonRepository.getSound().getGroup(layer.groupName);
		String soundName = event.get("sound");
		AudibleCircleArea area = new AudibleCircleArea(Algebra.vecf((float)entity.getX(), (float)entity.getY()), radius/4.0f, radius);
		group.loadSound(soundName, "audio:/" + soundName + ".ogg", Type.OGG, false);
		group.play(soundName, volume, 0, area, null, false, true);
	}

}
