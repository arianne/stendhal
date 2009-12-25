/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.system.Processors;

import games.stendhal.client.sound.system.SignalProcessor;
import java.util.ArrayList;

/**
 *
 * @author silvio
 */
public class ToneGenerator extends SignalProcessor
{
    public static class Tone
    {
        private float mVolume;
        private float mFrequency;
        private long  mPosition = 0;

        public Tone(float volume, float frequency)
        {
            mVolume    = volume;
            mFrequency = frequency;
        }
    }

    private int                   mSampleRate;
    private int                   mNumChannels;
    private float[]               mSamples;
    private final ArrayList<Tone> mTones = new ArrayList<Tone>();

    public ToneGenerator(int channels, int rate, int numSamplesToBuffer)
    {
        mNumChannels = channels;
        mSampleRate  = rate;
        mSamples     = new float[numSamplesToBuffer * mNumChannels];
    }

    public synchronized void addTone(Tone tone)
    {
        mTones.add(tone);
    }

    @Override
    protected boolean generate()
    {
        int index = 0;

        for(Tone tone: mTones)
        {
            makeTone(tone, mSamples, (mSamples.length / mNumChannels), mNumChannels, (index != 0));
            ++index;
        }

        super.propagate(mSamples, (mSamples.length / mNumChannels), mNumChannels, mSampleRate);
        return true;
    }

    @Override
    protected synchronized void modify(float[] data, int samples, int channels, int rate)
    {
        for(Tone tone: mTones)
            makeTone(tone, data, samples, channels, true);

        super.propagate(data, samples, channels, rate);
    }

    private void makeTone(Tone tone, float[] data, int samples, int channels, boolean mixSound)
    {
        final double RAD                      = 2.0 * Math.PI;
        final float  frequencySampleRateRatio = tone.mFrequency / (float)mSampleRate;

        if(mixSound)
        {
            for(int i=0; i<samples; ++i)
            {
                int   index = i * channels;
                float value = (float)Math.sin(RAD * frequencySampleRateRatio * (double)tone.mPosition);

                value *= tone.mVolume;

                for(int c=0; c<channels; ++c)
                    data[index + c] = data[index + c] + value - data[index + c] * value;
                
                ++tone.mPosition;
            }
        }
        else
        {
            for(int i=0; i<samples; ++i)
            {
                int   index = i * channels;
                float value = (float)Math.sin(RAD * frequencySampleRateRatio * (double)tone.mPosition);

                value *= tone.mVolume;

                for(int c=0; c<channels; ++c)
                    data[index + c] = value;

                ++tone.mPosition;
            }
        }
    }
}
