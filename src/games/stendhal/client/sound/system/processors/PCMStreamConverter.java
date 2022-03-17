/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.sound.system.processors;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;

import games.stendhal.client.sound.Field;
import games.stendhal.client.sound.system.SignalProcessor;

/**
 *
 * @author silvio
 */
public class PCMStreamConverter extends SignalProcessor
{
    private InputStream mInputStream;
    private AudioFormat mAudioFormat;
    private float[]     mOutputBuffer = null;
    private byte[]      mInputBuffer  = null;
    private int         mNumSamplesToBuffer;
    private boolean     mEndOfStream;
    private boolean     mConverterIsOpened;

    public PCMStreamConverter(InputStream stream, AudioFormat format, int outputNumSamplesPerChannel)
    {
        init(stream, format, outputNumSamplesPerChannel);
    }

    public boolean reachedEndOfStream()
    {
        return mEndOfStream;
    }

    public void open(InputStream stream, AudioFormat format, int outputNumSamplesPerChannel)
    {
        if(!mConverterIsOpened) {
			init(stream, format, outputNumSamplesPerChannel);
		}
    }

    public void close()
    {
        mEndOfStream       = true;
        mConverterIsOpened = false;

		if(mInputStream != null)
		{
			try
			{
				 mInputStream.close();
			}
			catch(IOException exception)
			{
				assert false: exception.toString();
			}

			mInputStream = null;
		}
    }

    private void init(InputStream stream, AudioFormat format, int outputNumSamplesPerChannel)
    {
        assert stream != null && format != null;
        assert outputNumSamplesPerChannel > 0:          "given buffer size too small";
        assert (format.getSampleSizeInBits() % 8) == 0: "invalid sample size";
        assert !format.isBigEndian():                   "cannot convert fom big-endian TO little-endian (not implemented jet)";

        int outputBufferSize = outputNumSamplesPerChannel * format.getChannels();
        int inputBufferSize  = outputNumSamplesPerChannel * format.getFrameSize();

		mInputBuffer        = Field.expand(mInputBuffer , inputBufferSize , false);
		mOutputBuffer       = Field.expand(mOutputBuffer, outputBufferSize, false);
        mInputStream        = stream;
        mAudioFormat        = format;
        mNumSamplesToBuffer = outputNumSamplesPerChannel;
        mEndOfStream        = false;
        mConverterIsOpened  = true;
    }

    private void convertToUniformPCM(int numSamplesToConvert)
    {
        int  numBytesPerSample = mAudioFormat.getSampleSizeInBits() / 8;
        int  numChannels       = mAudioFormat.getChannels();
        int  numBytesPerFrame  = mAudioFormat.getFrameSize();
        long maxValue          = (long)(Math.pow(2, mAudioFormat.getSampleSizeInBits()));
        long maxValueHalf      = maxValue / 2;

        if((numChannels * numBytesPerSample) == numBytesPerFrame)
        {
            for(int i=0; i<(numSamplesToConvert * numChannels); ++i)
            {
                int  index = i * numBytesPerSample;
                long value  = 0;

                for(int b=0; b<numBytesPerSample; ++b) {
					value |= (mInputBuffer[index + b] & 0x00000000000000FF) << (b * 8);
				}

                if(value >= maxValueHalf) {
					value -= maxValue;
				}

                mOutputBuffer[i] = ((float)value / (float)maxValueHalf);
            }
        }
        else
        {
            for(int i=0; i<numSamplesToConvert; ++i)
            {
                int index = i * numChannels;
                int frame = i * numBytesPerFrame;

                for(int c=0; c<numChannels; ++c)
                {
                    long value = 0;

                    for(int b=0; b<numBytesPerSample; ++b) {
						value |= (mInputBuffer[frame + b] & 0x00000000000000FF) << (b * 8);
					}

                    if(value >= maxValueHalf) {
						value -= maxValue;
					}

                    mOutputBuffer[index + c] = (float)value / (float)maxValueHalf;
                    frame                   += numBytesPerSample;
                }
            }
        }
    }

    @Override
    protected boolean generate()
    {
        if(reachedEndOfStream())
        {
            super.quit();
            return false;
        }

        try
        {
            int numBytesToRead      = mNumSamplesToBuffer * mAudioFormat.getFrameSize();
            int numBytesRead        = mInputStream.read(mInputBuffer, 0, numBytesToRead);
            int numSamplesToConvert = numBytesRead / mAudioFormat.getFrameSize();

            convertToUniformPCM(numSamplesToConvert);
            super.propagate(mOutputBuffer, numSamplesToConvert, mAudioFormat.getChannels(), (int)mAudioFormat.getSampleRate());

            if(numBytesRead < numBytesToRead) {
				close();
			}
        }
        catch(IOException exception)
        {
            close();
        }

        return true;
    }
}
