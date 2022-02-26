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

import games.stendhal.client.sound.system.SignalProcessor;

/**
 *
 * @author silvio
 */
public class MonoMixer extends SignalProcessor
{
    @Override
    protected void modify(float[] data, int frames, int channels, int rate)
    {
        assert data.length >= (frames * channels);

        for(int i=0; i<frames; ++i)
        {
            float value = 0;

            for(int c=0; c<channels; ++c) {
				value += data[i * channels + c];
			}

            value  /= channels;
            data[i] = value;
        }

        super.propagate(data, frames, 1, rate);
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

        super.propagate(data, samples, 1, rate);
		*/
    }
}
