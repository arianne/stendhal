package games.stendhal.client.events;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.client.sound.manager.AudibleCircleArea;
import games.stendhal.client.sound.manager.SoundFile.Type;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.math.Algebra;

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
		int volume = 100;
		if (event.has("volume")) {
			volume = event.getInt("volume");
		}
		int radius = 100000;
		if (event.has("radius")) {
			radius = event.getInt("radius");
		}

		SoundSystemFacade.Group group = null;

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

		String soundName = event.get("sound");
		AudibleCircleArea area = new AudibleCircleArea(Algebra.vecf((float)entity.getX(), (float)entity.getY()), radius/4.0f, radius);
		group.loadSound(soundName, "audio:/" + soundName + ".ogg", Type.OGG, false);
		group.play(soundName, 0, area, null, false, true);
	}

}
