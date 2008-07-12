package games.stendhal.client.soundreview;

import java.util.LinkedList;
import java.util.List;

public class Background {

	private static final String INT_SEMOS_BLACKSMITH = "int_semos_blacksmith";

	private static final String ZERO_SEMOS_ROAD_E = "0_semos_road_e";

	private static final String ZERO_SEMOS_CITY = "0_semos_city";

	private static final String ZERO_SEMOS_VILLAGE_W = "0_semos_village_w";

	private final LinkedList<Sound> sounds;

	public Background(final String name) {
		this.clips = new LinkedList<AudioClip>();
		this.sounds = new LinkedList<Sound>();
		if (INT_SEMOS_BLACKSMITH.equals(name)) {
			initSemosBlacksmith();
		} else if (ZERO_SEMOS_ROAD_E.equals(name)) {
			initSemosRoad();
		} else if (ZERO_SEMOS_CITY.equals(name)) {
			initSemosCity();
		} else if (ZERO_SEMOS_VILLAGE_W.equals(name)) {
			initSemosVillage();
		} 
			// TODO handle "no Background for zone:"+ name);
	

	}

	private void initSemosVillage() {

	}

	private void initSemosCity() {

	}

	private void initSemosRoad() {

	}

	private void initSemosBlacksmith() {
		addSound("firesparks-1", 11, 3);
		addSound("forgefire-1", 11, 3, true);

		addSound("forgefire-2", 3, 3, true);
		addSound("forgefire-3", 3, 3, true);
	

	}

	private void addSound(final String string, final int i, final int j, final boolean b) {
		sounds.add(new Sound(string, i, j, b));

	}

	private final List<AudioClip> clips;

	public void addSound(final String soundFileName, final int x, final int y) {

		sounds.add(new Sound(soundFileName, x, y));
	}

	public void run() {
		for (final Sound sound : sounds) {

			clips.add(sound.play());

		}
	}

	public void stop() {
		for (final AudioClip ac : clips) {

			ac.stop();

		}
	}

}
