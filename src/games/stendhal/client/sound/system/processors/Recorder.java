/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.system.processors;

import games.stendhal.client.sound.system.SignalProcessor;

/**
 *
 * @author silvio
 */
public class Recorder extends SignalProcessor
{
    private float[] mData               = null;
    private float[] mOutputBuffer       = null;
    private int     mNumChannels        = 1;
    private int     mSampleRate         = 0;
    private int     mNumSamplesBuffered = 0;
    private int     mNumSamplesRead     = 0;
    private int     mOutputNumSamples;

    public Recorder(int outputNumSamplesPerChannel)
    {
        mOutputNumSamples = outputNumSamplesPerChannel;
    }

    public synchronized void clear()
    {
        mNumSamplesBuffered = 0;
        mNumSamplesRead     = 0;
        mData               = null;
        mOutputBuffer       = null;
    }

    public synchronized void    restart           () { mNumSamplesRead = 0;                           }
    public synchronized boolean reachedEndOfStream() { return mNumSamplesRead >= mNumSamplesBuffered; }
    public synchronized int     getNumChannels    () { return mNumChannels;                           }
    public synchronized int     getSampleRate     () { return mSampleRate;                            }
    public synchronized int     getNumSamples     () { return mNumSamplesBuffered / mNumChannels;     }


    public synchronized void trim()
    {
        if(mData.length != mNumSamplesBuffered)
        {
            float[] temp = new float[mNumSamplesBuffered];
            System.arraycopy(mData, 0, temp, 0, mNumSamplesBuffered);
            mData = temp;
        }
    }

    @Override
    protected void finished()
    {
        trim();
        super.quit();
    }

    @Override
    protected synchronized void modify(float[] data, int samples, int channels, int rate)
    {
        if(data == null || samples == 0)
            return;
        
        if(mData == null)
        {
            mData        = new float[samples * channels * 3];
            mNumChannels = channels;
            mSampleRate  = rate;
        }

        assert mNumChannels == channels && mSampleRate == rate;

        int remainingBufferSpace = mData.length - mNumSamplesBuffered;
        int requiredBufferSpace  = samples * channels;

        // if the buffer is to small to hold the whole data, we expand it
        if(remainingBufferSpace < requiredBufferSpace)
        {
            float[] newData = new float[(mNumSamplesBuffered + requiredBufferSpace) * 2];
            System.arraycopy(mData, 0, newData, 0, mNumSamplesBuffered);
            mData = newData;
        }

        System.arraycopy(data, 0, mData, mNumSamplesBuffered, requiredBufferSpace);
        mNumSamplesBuffered += requiredBufferSpace;

        super.propagate(data, samples, channels, rate);
    }

    @Override
    protected boolean generate()
    {
        if(reachedEndOfStream())
        {
            super.quit();
            return false;
        }

        int numSamplesToRead    = mOutputNumSamples * mNumChannels;
        int numSamplesAvailable = mNumSamplesBuffered - mNumSamplesRead;
        int numSamples          = Math.min(numSamplesToRead, numSamplesAvailable);

        if(mOutputBuffer == null)
            mOutputBuffer = new float[numSamplesToRead];

        System.arraycopy(mData, mNumSamplesRead, mOutputBuffer, 0, numSamples);
        mNumSamplesRead += numSamples;

        super.propagate(mOutputBuffer, (numSamples / mNumChannels), mNumChannels, mSampleRate);
        return true;
    }
}
