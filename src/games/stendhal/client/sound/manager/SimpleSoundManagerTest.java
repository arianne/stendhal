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
		if (!manager.hasSoundName("coins-1")) {
			manager.openSoundFile("data/sounds/coins-1.ogg", "coins-1");
		}

		System.out.println("Playing sound: harp");
		manager.play("harp-1", 0, SoundManager.INFINITE_AUDIBLE_AREA, false, null);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Playing sound: coins");
		manager.play("coins-1", 0, SoundManager.INFINITE_AUDIBLE_AREA, false, null);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Closing Soundsystem");
		manager.close();

		System.out.println("exiting");
	}
}
