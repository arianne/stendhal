/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.system.Processors;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author silvio
 */
public class VolumeGroups
{
    private final HashMap<String,ArrayList<VolumeAdjustor>> mAdjustorGroups =
            new HashMap<String,ArrayList<VolumeAdjustor>>();

    public VolumeAdjustor add(String group, float volume)
    {
        ArrayList<VolumeAdjustor> adjustorList = mAdjustorGroups.get(group);
        
        if(adjustorList == null)
        {
           adjustorList = new ArrayList<VolumeAdjustor>();
           mAdjustorGroups.put(group, adjustorList);
        }

        VolumeAdjustor adjustor = new VolumeAdjustor(volume);
        adjustorList.add(adjustor);

        return adjustor;
    }

    public void remove(String group)
    {
        ArrayList<VolumeAdjustor> adjustorList = mAdjustorGroups.get(group);

        if(adjustorList != null)
        {
            for(VolumeAdjustor adjustor: adjustorList)
                adjustor.disconnect();

            adjustorList.clear();
            mAdjustorGroups.remove(group);
        }
    }

    public void remove(String group, VolumeAdjustor adjustor)
    {
        ArrayList<VolumeAdjustor> adjustorList = mAdjustorGroups.get(group);

        if(adjustorList != null)
            adjustorList.remove(adjustor);
    }

    public void setVolume(String group, float volume)
    {
        ArrayList<VolumeAdjustor> adjustorList = mAdjustorGroups.get(group);

        if(adjustorList != null)
        {
            for(VolumeAdjustor adjustor: adjustorList)
                adjustor.setVolume(volume);
        }
    }

    public void setVolume(float volume)
    {
        for(ArrayList<VolumeAdjustor> adjustorList: mAdjustorGroups.values())
            for(VolumeAdjustor adjustor: adjustorList)
                adjustor.setVolume(volume);
    }
}
