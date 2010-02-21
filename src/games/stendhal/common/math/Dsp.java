/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.common.math;

import games.stendhal.common.memory.Field;

/**
 *
 * @author silvio
 */
public class Dsp
{
	public static byte[] convertUniformPCM(byte[] pcmBuffer, float[] samples, int numSamples, int numBytesPerSample)
    {
        int  numBitsPerSample = numBytesPerSample * 8;
		long maxValue         = (long)((Math.pow(2, numBitsPerSample) - 1.0) / 2.0);

		pcmBuffer = Field.expand(pcmBuffer, (numBytesPerSample * numSamples), false);

        for(int i=0; i<numSamples; ++i)
        {
            long value = (long)(samples[i] * maxValue);
            int  index = i * numBytesPerSample;

            value = (value >  maxValue) ? ( maxValue) : (value);
            value = (value < -maxValue) ? (-maxValue) : (value);

            for(int n=0; n<numBytesPerSample; ++n)
                pcmBuffer[index + n] = (byte)(value >>> (n*8));
        }

        return pcmBuffer;
	}

    public static void mixUniformPCM(float[] result, int rOffset, float[] samples, int sOffset, int numSamples)
    {
		int rEnd = rOffset + numSamples;

		while(rOffset < rEnd)
		{
			float A = result[rOffset];
            float B = samples[sOffset];

            result[rOffset] = A + B - A * B;

			++rOffset;
			++sOffset;
		}
		/*
        for(int i=0; i<numSamples; ++i)
        {
            float A = result[rOffset + i];
            float B = samples[sOffset + i];

            result[rOffset + i] = A + B - A * B;
        }//*/
    }

	public static float[] convertChannels(float[] samples, int numFrames, int numChannels, int numRequiredChannels)
    {
        /* Assignments for audio channels
         * channel 0: Front left
         * channel 1: Front right
         * channel 2: Center
         * channel 3: Low frequency (subwoofer)
         * channel 4: Surround left       - ## NEEDS CONFIRMATION ##
         * channel 5: Surround right      - ## NEEDS CONFIRMATION ##
         * channel 6: Surround back left  - ## NEEDS CONFIRMATION ##
         * channel 7: Surround back right - ## NEEDS CONFIRMATION ##
         */

        if(numChannels == numRequiredChannels)
            return samples;

        // we have to reduce the number of channels
        if(numChannels > numRequiredChannels)
        {
            // stereo/multichannel to mono - maybe this won't work properly for more than 2 channels
            //                             - ## NEEDS CONFIRMATION ##
            if(numRequiredChannels == 1)
            {
                for(int i=0; i<numFrames; ++i)
                {
                    int index   = i * numChannels;
                    float value = 0.0f;

                    for(int c=0; c<numChannels; ++c)
                        value += samples[index + c];

                    samples[i] = value / (float)numChannels;
                }
            }
            else
            {
                // not implemented yet
                samples = null;
            }
        }
        else // we have to increase the number of channels
        {
            // mono to stereo/multichannel
            if(numChannels == 1)
            {
                float[] newUniformPCM = new float[numFrames * numRequiredChannels];

                for(int i=0; i<numFrames; ++i)
                {
                    int index = i * numRequiredChannels;

                    for(int c=0; c<numRequiredChannels; ++c)
                        newUniformPCM[index + c] = samples[i];
                }

                samples = newUniformPCM;
            }
            else // stereo/multichannel to multichannel
            {
                /* Stereo widening (from wikipedia):
                 * Widening of the stereo image can be achieved by manipulating the
                 * relationship of the side signal S and the center signal C
                 * C = (L + R) / 2; S = (L - R) / 2
                 * A positive part of the side signal S is now fed into the left channel
                 * and a part with its phase inverted to the right channel.
                 */

                // not implemented yet
                samples = null;
            }
        }

        return samples;
    }

	public static float[] convertSampleRate(float[] samples, int numSamples, int numChannels, int sampleRate, int targetSampleRate)
    {
        if(sampleRate == targetSampleRate)
            return samples;

        // not implemented yet
        return null;
    }
}
