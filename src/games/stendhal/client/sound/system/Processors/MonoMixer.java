/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.system.Processors;

import games.stendhal.client.sound.system.SignalProcessor;

/**
 *
 * @author silvio
 */
public class MonoMixer extends SignalProcessor
{
    @Override
    protected void modify(float[] data, int samples, int channels, int rate)
    {
        assert data.length >= (samples * channels);

        for(int i=0; i<samples; ++i)
        {
            float value = 0;

            for(int c=0; c<channels; ++c)
                value += data[i * channels + c];

            value  /= channels;
            data[i] = value;
        }

        super.propagate(data, samples, 1, rate);
        /*
        for(int i=0; i<samples; ++i)
        {
            int    index    = i * channels;
            double sum      = data[index];
            double product  = data[index];
            double combined = data[index] * data[index + 1];
            int    ch       = 1;

            while(ch < channels-1)
            {
                sum      += data[index + ch];
                product  *= data[index + ch];
                combined -= data[index + ch] * data[index + ch+1];
                ++ch;
            }

            sum      += data[index + ch];
            product  *= data[index + ch];
            combined -= data[index + ch] * data[index + 0];

            data[i] = (float)(sum - combined + product);
        }

        super.propagate(data, samples, 1, rate);//*/
    }
}
