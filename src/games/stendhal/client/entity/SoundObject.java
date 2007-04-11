/*
 *  SoundObject in games.stendhal.client.entity
 *  file: SoundObject.java
 * 
 *  Project stendhal
 *  @author Jane Hunt
 *  Created 21.01.2006
 *  Version
 * 
 This program is free software. You can use, redistribute and/or modify it under 
 the terms of the GNU General Public License as published by the Free Software 
 Foundation, version 2 of the License.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 Place - Suite 330, Boston, MA 02111-1307, USA, or go to
 http://www.gnu.org/copyleft/gpl.html.
 */

package games.stendhal.client.entity;

import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.Rand;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.DataLine;

public class SoundObject extends InvisibleEntity {

	private String name;

	private int volume;
	static Map<String, String[]> soundArray = null;
	
	void soundInit(){
		soundArray = new HashMap<String, String[]>();

		soundArray.put("smith-mix", new String[]{"hammer-1.wav","smith-1.wav"});
		soundArray.put("chicken-mix", new String[]{"chicken-1.wav"});
		soundArray.put("tavern-mix", new String[]{"kettle-1.wav",
				"bottle-2.wav","trash-1.wav","trash-21.wav","trash-3.wav"
				,"metal-can-1.wav","drain-water-11.wav","dishbreak-1.wav",
				"dishbreak-2.wav","dishes-1.wav","dishes-21.wav",
				"creaky-door-1.wav","creaky-door-2.wav","ice-cubes-1.wav",
				"window-close-1.wav"});
		soundArray.put("treecreak-1", new String[]{"treecreak-1.wav"});
		soundArray.put("blackbird-1", new String[]{"blackbird-11.wav"});
		soundArray.put("blackbird-mix",new String[]{"blackbird-6b.wav",
				"blackbird-7b.wav","blackbird-8b.wav"});
		soundArray.put("firesparks-1", new String[]{"fire-sparkes-1.wav"});
		soundArray.put("lark-1", new String[]{"lark-1.wav"});
		soundArray.put("lark-2", new String[]{"lark-2.wav"});
		soundArray.put("bushbird-mix-1", new String[]{"bird-1b.wav"});
		soundArray.put("water-splash-1", new String[]{"water-2.wav"});
		soundArray.put("water-splash-2", new String[]{"water-3.wav"});
		soundArray.put("water-wave-1", new String[]{"wave-11.wav"}); 
	}

	public SoundObject() {
	}


	public SoundObject(final int x, final int y) {
		super();
		this.x = x;
		this.y = y;
		
	}

	

	public SoundObject(final Point2D soundPos,final  int radius) {
		x = soundPos.getX();
		y = soundPos.getY();
		audibleRange = radius;
	}


	@Override
	public Rectangle2D getArea() {
		return null;
	}

	@Override
	public Rectangle2D getDrawedArea() {
		return null;
	}

	// TODO: remove Soundobject from hierarchie
	@Override
	public ActionType defaultAction() {
		return null;
	}

	public void setLocation(final Point2D position) {
		x = position.getX();
		y = position.getY();
	}

	/**
	 * @return returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name -
	 *            the new value for name to set.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return returns the volume.
	 */
	public int getVolume() {
		return volume;
	}

	/**
	 * @param volume -
	 *            the new value for volume to set.
	 */
	public void setVolume(final int volume) {
		this.volume = volume;
	}

	@Override
	public int getZIndex() {
		return 1000;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see games.stendhal.client.entity.Entity#buildOfferedActions(java.util.List)
	 */
	@Override
	protected void buildOfferedActions(List<String> list) {
		list.clear();
	}


	
	public DataLine playSound(String token, int volBot, int volTop, int chance) {
		if (soundArray==null) {
			soundInit();
		}else{
			
			
		}
		if (Rand.rand(100)<chance){
			if (soundArray.containsKey(token)){
				
				SoundMaster.play(soundArray.get(token)[Rand.rand(soundArray.get(token).length)],x,y);

				
			}else{
			   // 	SoundMaster.play("evillaugh-3.wav",x,y);
				     this.soundInit();
			        System.out.println("done");
			     	System.out.println("chance " +token + " chance "+ chance);
			     	
					}
			}
		return null;//SoundSystem.playMapSound(getX(),getY(), getAudibleArea(), token, volBot, volTop, chance);

	}

}