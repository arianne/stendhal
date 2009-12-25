/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.manager;

/**
 *
 * @author silvio
 */
public interface AudibleArea
{
    public float getHearingIntensity(float[] hearerPos);
    public void  getClosestPoint    (float[] result, float[] hearerPos);
}
