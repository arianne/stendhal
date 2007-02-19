package games.stendhal.client.sound;

/**
 * is a smelly lazy class just keeping the precalculated dbValues
 * which represent loudness(?) 
 * 
 * TODO: remove the magic 100 , 101 numbers from DBValues
 * 
 * @author astridemma 19.02.2007 
 * 
 */
public class DBValues {
	
	/**
	 *  dbValue[0] is mute and dbValue[100] is loudest
	 */
	public static final float[] dBValues = new float[101];

	static {
		// init our volume -> decibel map
		for (int i = 0; i < 101; i++) {
			double level = ((double) i) / 100;
			dBValues[i] = (float) (Math.log(level) / Math.log(10.0) * 20.0);
		}
	}
}
