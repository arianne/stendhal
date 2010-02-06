package games.stendhal.client.sound;

import java.awt.geom.Point2D;

/**
 * creates ambient sound sources for the specified zone
 *
 * @author hendrik
 */
public class AmbientSoundFactory {

	private static final String INT_SEMOS_BLACKSMITH = "int_semos_blacksmith";

	private static final String ZERO_SEMOS_ROAD_E = "0_semos_road_e";

	private static final String ZERO_SEMOS_CITY = "0_semos_city";

	private static final String ZERO_SEMOS_VILLAGE_W = "0_semos_village_w";

	public void create(String zone) {

		if (zone.equals(AmbientSoundFactory.ZERO_SEMOS_VILLAGE_W)) {
			createForSemosVillageW();
		} else if (zone.equals(AmbientSoundFactory.ZERO_SEMOS_CITY)) {
			createForSemosCity();
		} else if (zone.equals(AmbientSoundFactory.INT_SEMOS_BLACKSMITH)) {
			createForSemosBlacksmith();
		} else if (zone.equals(AmbientSoundFactory.ZERO_SEMOS_ROAD_E)) {
			createForSemosRoeadE();
		}
	}

	private void createForSemosVillageW() {

	}

	private void createForSemosCity() {
		Point2D soundPos;
		// blackbirds
		AmbientSound baseAmb = AmbientStore.getAmbient("blackbirds-1");

		soundPos = new Point2D.Double(29, 8);
		AmbientSound ambient = new AmbientSound(baseAmb, "semos-city-blackbirds-1", soundPos, 30, 80);
		playAmbientSound(ambient);

		// chicken
		baseAmb = AmbientStore.getAmbient("chicken-1");

		soundPos = new Point2D.Double(8, 30);
		ambient = new AmbientSound(baseAmb, "semos-city-fowl-1", soundPos, 12, 50);
		playAmbientSound(ambient);

		soundPos = new Point2D.Double(47, 25);
		ambient = new AmbientSound(baseAmb, "semos-city-fowl-2", soundPos, 15, 50);
		playAmbientSound(ambient);

		// worksounds
		baseAmb = AmbientStore.getAmbient("build-works-1");

		soundPos = new Point2D.Double(12, 38);
		ambient = new AmbientSound(baseAmb, "semos-city-works-1", soundPos, 8, 25);
		playAmbientSound(ambient);

		// tavern noise
		baseAmb = AmbientStore.getAmbient("tavern-noise-1");

		soundPos = new Point2D.Double(45, 37);
		ambient = new AmbientSound(baseAmb, "semos-city-tavern-1", soundPos, 10, 40);
		playAmbientSound(ambient);
	}

	private void createForSemosBlacksmith() {
	}

	private void createForSemosRoeadE() {
		Point2D soundPos;
		// creaking tree and wind
		AmbientSound baseAmb = AmbientStore.getAmbient("wind-tree-1");

		soundPos = new Point2D.Double(10, 45);
		AmbientSound ambient = new AmbientSound(baseAmb, "road-ados-tree-1", soundPos, 30, 30);
		playAmbientSound(ambient);

		soundPos = new Point2D.Double(54, 59);
		ambient = new AmbientSound(baseAmb, "road-ados-tree-2", soundPos, 100, 50);
		playAmbientSound(ambient);

		soundPos = new Point2D.Double(65, 31);
		ambient = new AmbientSound(baseAmb, "road-ados-tree-3", soundPos, 100, 30);
		playAmbientSound(ambient);

		// beach water
		baseAmb = AmbientStore.getAmbient("water-beach-1");

		soundPos = new Point2D.Double(32, 46);
		ambient = new AmbientSound(baseAmb, "road-ados-beachwater-1", soundPos, 7, 25);
		playAmbientSound(ambient);

		soundPos = new Point2D.Double(43, 47);
		ambient = new AmbientSound(baseAmb, "road-ados-beachwater-2", soundPos, 7, 25);
		playAmbientSound(ambient);

		soundPos = new Point2D.Double(32, 55);
		ambient = new AmbientSound(baseAmb, "road-ados-beachwater-3", soundPos, 12, 35);
		playAmbientSound(ambient);

		// water at bridge
		baseAmb = AmbientStore.getAmbient("water-flow-1");

		soundPos = new Point2D.Double(47, 47);
		ambient = new AmbientSound(baseAmb, "road-ados-bridge-1", soundPos, 3, 50);
		playAmbientSound(ambient);

		// larks
		baseAmb = AmbientStore.getAmbient("meadow-larks-1");

		soundPos = new Point2D.Double(15, 15);
		ambient = new AmbientSound(baseAmb, "road-ados-larks-1", soundPos, 30, 50);
		playAmbientSound(ambient);

		soundPos = new Point2D.Double(32, 33);
		ambient = new AmbientSound(baseAmb, "road-ados-larks-2", soundPos, 30, 50);
		playAmbientSound(ambient);

		// bushbirds
		baseAmb = AmbientStore.getAmbient("bushbirds-1");

		soundPos = new Point2D.Double(83, 56);
		ambient = new AmbientSound(baseAmb, "road-ados-bushbirds-1", soundPos, 20, 80);
		playAmbientSound(ambient);

		soundPos = new Point2D.Double(118, 57);
		ambient = new AmbientSound(baseAmb, "road-ados-bushbirds-2", soundPos, 20, 90);
		playAmbientSound(ambient);
	}

	private void playAmbientSound(AmbientSound ambient) {
		// TODO Auto-generated method stub
		
	}

}
