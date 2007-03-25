package games.stendhal.client.soundreview;

import games.stendhal.client.SpriteStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class Schnick {

	public static InputStream getResourceStream(String name) throws IOException {
		URL url = SpriteStore.get().getResourceURL(name);
		if (url == null) {
			throw new FileNotFoundException(name);
		}
		return url.openStream();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	
	   
        
		// TODO Auto-generated method stub
		SoundMaster sm = new SoundMaster();
		sm.run();
		SoundMaster.play("hammer-1.wav");
	}

	private static void loadFromPropertiesintoXML() {
	    Properties prop=new Properties();
	    try {
	        prop.load(getResourceStream("data/sounds/stensounds.properties"));
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    try {
	        prop.storeToXML(new FileOutputStream(new File("data/sounds/stensounds.xml")),"autmatic");
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
    }

	private static void loadPropertiesFromXML() {
	    Properties prop=new Properties();
		try {
	        prop.loadFromXML(new FileInputStream(new File("data/sounds/stensounds.xml")));
        } catch (InvalidPropertiesFormatException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
    }

	
}

