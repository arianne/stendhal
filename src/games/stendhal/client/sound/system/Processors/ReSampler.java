/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.system.Processors;

import games.stendhal.client.sound.system.SignalProcessor;
import games.stendhal.client.sound.system.Time;

/**
 *
 * @author silvio
 */
public class ReSampler extends SignalProcessor
{
    private int     mNumChannels;
    private int     mDelayInSamples;
    private float[] mBuffer;
    private boolean mStarted = false;

    ReSampler(Time delay, int channels, int sampleRate)
    {
        assert delay.getInSamples(sampleRate) < Integer.MAX_VALUE: "delay time is to long";

        mDelayInSamples = (int)delay.getInSamples(sampleRate);
        mBuffer         = new float[(int)(mDelayInSamples * channels)];
    }

    public synchronized void start()
    {
        mStarted = true;
    }
    
    @Override
    protected void modify(float[] data, int samples, int channels, int rate)
    {
        
        super.propagate(data, samples, channels, rate);
    }
}
