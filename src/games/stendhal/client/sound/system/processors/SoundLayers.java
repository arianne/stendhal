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

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import games.stendhal.client.sound.system.SignalProcessor;
import games.stendhal.common.math.Algebra;
import games.stendhal.common.math.Numeric;

/**
 *
 * @author silvio
 */
public class SoundLayers
{
    public final class VolumeAdjustor extends SignalProcessor
    {
        //private float mIntensity;
        private AtomicInteger mIntensity = new AtomicInteger(floatToInt(1.0f));
        private Layer         mLayer;

        private VolumeAdjustor(Layer layer)
        {
            mLayer = layer;
        }

        public void setLayer(int level)
        {
            if(mLayer.level == level) {
				return;
			}

            synchronized(SoundLayers.this)
            {
                mLayer.adjustors.remove(this);
                mLayer = getLayer(level);
                mLayer.adjustors.add(this);
                mStateChanged = true;
            }
        }

        public void setIntensity(float intensity)
        {
            if(Algebra.isEqual_Scalf(intensity, intToFloat(mIntensity.get()))) {
				return;
			}

            synchronized(SoundLayers.this)
            {
                mIntensity.set(floatToInt(intensity));
                mStateChanged = true;
            }
        }

        @Override
        protected void modify(float[] data, int frames, int channels, int rate)
        {
            synchronized(SoundLayers.this)
            {
                if(mStateChanged) {
					computeVolumes();
				}
            }

            float volume = intToFloat(Math.min(mLayer.volume.get(), mIntensity.get()));

            // if volume is zero we return without processing the audio data
            if(Algebra.isEqual_Scalf(volume, 0.0f)) {
				return;
			}

            // if volume is 1 we propagate the unmodified audio data
            if(!Algebra.isEqual_Scalf(volume, 1.0f))
            {
                for(int i=0; i<(frames*channels); ++i) {
					data[i] *= volume;
				}
            }

            super.propagate(data, frames, channels, rate);
        }
    }

    private static class Layer
    {
        int                        level;
        LinkedList<VolumeAdjustor> adjustors = new LinkedList<VolumeAdjustor>();
        AtomicInteger              volume    = new AtomicInteger(floatToInt(1.0f));

        Layer(int lvl) { level = lvl; }
    }

    private boolean                mStateChanged = true;
    private TreeMap<Integer,Layer> mLayers       = new TreeMap<Integer, Layer>(new Comparator<Integer>()
    {
        @Override
		public int compare(Integer a, Integer b)
        {
            return b.compareTo(a);
        }
    });

    public synchronized VolumeAdjustor createVolumeAdjustor(int layerLevel)
    {
        Layer          layer    = getLayer(layerLevel);
        VolumeAdjustor adjustor = new VolumeAdjustor(layer);
        layer.adjustors.add(adjustor);
        return adjustor;
    }

    public synchronized void removeVolumeAdjustor(VolumeAdjustor adjustor)
    {
        adjustor.mLayer.adjustors.remove(adjustor);
    }

    private void computeVolumes()
    {
        int summedIntensity = 0;

        for(Layer layer: mLayers.values())
        {
            int currIntensity = 0;

            for(VolumeAdjustor adjustor: layer.adjustors) {
				currIntensity = Math.max(currIntensity, adjustor.mIntensity.get());
			}

            currIntensity    = Math.min(currIntensity, floatToInt(1.0f) - summedIntensity);
            currIntensity    = Math.max(currIntensity, 0);
            summedIntensity += currIntensity;

            layer.volume.set(currIntensity);
        }

        mStateChanged = false;
    }

    private Layer getLayer(int layerLevel)
    {
        Layer layer = mLayers.get(layerLevel);

        if(layer == null)
        {
            layer = new Layer(layerLevel);
            mLayers.put(layerLevel, layer);
        }

        return layer;
    }

    private static int   floatToInt(float v) { return Numeric.floatToInt(v, 10000000.0f); }
    private static float intToFloat(int   v) { return Numeric.intToFloat(v, 10000000.0f); }
}
