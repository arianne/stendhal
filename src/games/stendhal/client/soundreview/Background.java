package games.stendhal.client.soundreview;



import java.util.LinkedList;
import java.util.List;

public class Background {
    
	private static final String INT_SEMOS_BLACKSMITH = "int_semos_blacksmith";

	private static final String _0_SEMOS_ROAD_E = "0_semos_road_e";

	private static final String _0_SEMOS_CITY = "0_semos_city";

	private static final String _0_SEMOS_VILLAGE_W = "0_semos_village_w";
	
	private String _name;
	private LinkedList<Sound> sounds;

	
	public Background(String name) {
	this.clips= new   LinkedList<AudioClip>();
	this.sounds= new   LinkedList<Sound>();
	_name=name;
	 if (INT_SEMOS_BLACKSMITH.equals(name)){
		 initSemosBlacksmith();
	 } else if (_0_SEMOS_ROAD_E.equals(name)){
		 initSemosRoad();
	 } else if (  _0_SEMOS_CITY.equals(name)){
		 initSemosCity();
	 } else if (  _0_SEMOS_VILLAGE_W.equals(name)){
		 initSemosVillage();
	 } else{
		// TODO handle System.out.println("no Background for zone:"+ name);
	 }
		
	}
	
	private void initSemosVillage() {
		// TODO Auto-generated method stub
		
	}

	private void initSemosCity() {
		// TODO Auto-generated method stub
		
	}

	private void initSemosRoad() {
		// TODO Auto-generated method stub
		
	}

	private void initSemosBlacksmith() {
//		ambient = new AmbientSound("blacksmith-overall-1", 20);
//		ambient.addCycle("hammer", 45000, 20, 40, 65);
//		playAmbientSound(ambient);

	addSound("firesparks-1", 11, 3);
	addSound("forgefire-1",11,3,true);
//		soundPos = new Point2D.Double(11, 3);
//		ambient = new AmbientSound("blacksmith-forgefire-main", soundPos, 30, 50);
//		ambient.addLoop("forgefire-1", 50, 0);
//		ambient.addCycle("firesparks-1", 60000, 10, 50, 80);
//		playAmbientSound(ambient);
	addSound("forgefire-2",3,3,true);
	addSound("forgefire-3",3,3,true);
//		soundPos = new Point2D.Double(3, 3);
//		ambient = new AmbientSound("blacksmith-forgefire-side", soundPos, 6, 50);
//		ambient.addLoop("forgefire-2", 50, 0);
//		ambient.addLoop("forgefire-3", 50, 0);
//		playAmbientSound(ambient);
		
	}

	private void addSound(String string, int i, int j, boolean b) {
		sounds.add(new Sound(string,i,j,b));
		
	}

	List<AudioClip> clips;	
	public void addSound(String soundFileName,int x , int y){
		
		sounds.add(new Sound(soundFileName,x,y));
	}
	
	public void run(){
		for (Sound sound  : sounds){
			
				clips.add(sound.play());
			
		}
	}
	
	public void stop(){
		for (AudioClip ac  : clips){
			
			ac.stop();
		
	    }
	}

}
