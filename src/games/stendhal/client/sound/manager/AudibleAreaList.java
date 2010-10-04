/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.manager;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author silvio
 */
public class AudibleAreaList extends LinkedList<AudibleArea> implements AudibleArea
{
    /**
	 * serial version uid
	 */
	private static final long serialVersionUID = 8277563402183797791L;

	public float getHearingIntensity(float[] hearerPos)
    {
        if(isEmpty())
            return 0.0f;
        
        Iterator<AudibleArea> iterator  = super.iterator();
        AudibleArea           area      = iterator.next();
        float                 intensity = area.getHearingIntensity(hearerPos);

        while(iterator.hasNext())
        {
            area = iterator.next();
            intensity = Math.max(intensity, area.getHearingIntensity(hearerPos));
        }
        
        return intensity;
    }

    public void getClosestPoint(float[] result, float[] hearerPos)
    {
        if(isEmpty())
            return;

        Iterator<AudibleArea> iterator    = super.iterator();
        AudibleArea           closestArea = iterator.next();
        float                 intensity   = closestArea.getHearingIntensity(hearerPos);

        while(iterator.hasNext())
        {
            AudibleArea area   = iterator.next();
            float       intens = area.getHearingIntensity(hearerPos);

            if(intens > intensity)
            {
                intensity   = intens;
                closestArea = area;
            }
        }

        closestArea.getClosestPoint(result, hearerPos);
    }
}
