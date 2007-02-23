package games.stendhal.client.sound;

import java.util.Hashtable;

public class SoundEffectMap {
	private static final SoundEffectMap _instance=new SoundEffectMap();
	
	static SoundEffectMap getInstance(){
		return _instance;
	}

	/**
	 * stores the named sound effects 
	 */
	 private Hashtable<String, Object> sfxmap = new Hashtable<String, Object>(256);
	 private Hashtable<String, String> pathMap = new Hashtable<String, String>(256);
	 private Hashtable<String, ClipRunner> clipRunnerMap = new Hashtable<String, ClipRunner >(256);

	Object getByName(String name){
		 return sfxmap.get(name);
	 }
	 
	 boolean containsKey(String key){
		 return sfxmap.containsKey(key);
		 
	 }
	 

	 Object put(String key, String value){
		 pathMap.put(key, value);
		 return sfxmap.put(key, value);
	 }
	 Object put(String key, ClipRunner value){
		 clipRunnerMap.put(key, value);
			 
		 return sfxmap.put(key, value);
		
	 }
	 int size(){
		 return sfxmap.size();
	 }

}
