package games.stendhal.client.sound;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SoundEffectMap {
	private static final SoundEffectMap _instance=new SoundEffectMap();
	
	static SoundEffectMap getInstance(){
		return _instance;
	}

	/**
	 * stores the named sound effects 
	 */
	 private Map<String, Object> sfxmap = Collections.synchronizedMap(new HashMap<String, Object>(256));
	 private Map<String, String> pathMap = Collections.synchronizedMap(new HashMap<String, String>(256));
	 private Map<String, ClipRunner> clipRunnerMap = Collections.synchronizedMap(new HashMap<String, ClipRunner >(256));

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
