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
//TODO: delete me when new soundsystem works
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
		SoundMaster sm = new SoundMaster();
		sm.run();
		SoundMaster.play("hammer-1.wav");
	}

	 static void loadFromPropertiesintoXML() {
	    Properties prop=new Properties();
	    try {
	        prop.load(getResourceStream("data/sounds/stensounds.properties"));
	    } catch (IOException e) {
	       
	        e.printStackTrace();
	    }
	    try {
	        prop.storeToXML(new FileOutputStream(new File("data/sounds/stensounds.xml")),"autmatic");
	    } catch (FileNotFoundException e) {
	       
	        e.printStackTrace();
	    } catch (IOException e) {
	        
	        e.printStackTrace();
	    }
    }

	 static void loadPropertiesFromXML() {
	    Properties prop=new Properties();
		try {
	        prop.loadFromXML(new FileInputStream(new File("data/sounds/stensounds.xml")));
        } catch (InvalidPropertiesFormatException e) {
	        
	        e.printStackTrace();
        } catch (FileNotFoundException e) {
	        
	        e.printStackTrace();
        } catch (IOException e) {
	       
	        e.printStackTrace();
        }
    }

	
}

