/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.manager;

import games.stendhal.common.math.Algebra;

/**
 *
 * @author silvio
 */
public class InfiniteAudibleArea implements AudibleArea
{
    public float getHearingIntensity(float[] hearerPos)                 { return 1.0f;                         }
    public void  getClosestPoint    (float[] result, float[] hearerPos) { Algebra.mov_Vecf(result, hearerPos); }
}
