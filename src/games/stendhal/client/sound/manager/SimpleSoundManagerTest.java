package games.stendhal.client.sound.manager;



public class SimpleSoundManagerTest {


	public static void main(String[] args) throws InterruptedException {
		games.stendhal.client.sound.manager.SimpleSoundManagerTest.play();
	}

	public static void play() {

		System.out.println("Starting Soundsystem");
		SoundManager manager = SoundManager.get();
		if (!manager.hasSoundName("harp-1")) {
			manager.openSoundFile("data/sounds/harp-1.ogg", "harp-1");
		}

		System.out.println("Playing sound");
		manager.play("harp-1", 0, null, false, null);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Finished waiting");
	}
}
