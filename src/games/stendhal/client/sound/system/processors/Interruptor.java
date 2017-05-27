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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import games.stendhal.client.sound.facade.Time;
import games.stendhal.client.sound.system.SignalProcessor;

/**
 * Signal processor stage to pause sound signals.
 * @author silvio
 */
public class Interruptor extends SignalProcessor
{
    private static final int PLAY  = 0;
    private static final int STOP  = 1;
    private static final int PAUSE = 2;

    private AtomicInteger mState = new AtomicInteger(PLAY);
    private AtomicLong    mDelay = new AtomicLong(0);

    public void play()
    {
        mState.set(PLAY);
        mDelay.set(0);
    }

    public void pause(Time delay)
    {
        mState.set(PAUSE);
        mDelay.set(delay.getInNanoSeconds());
    }

    public void stop(Time delay)
    {
        mState.set(STOP);
        mDelay.set(delay.getInNanoSeconds());
    }

    @Override
    public synchronized boolean request()
    {
        if(mDelay.get() <= 0)
        {
            switch(mState.get())
            {
            case PAUSE:
                return true;

            case STOP:
                quit();
                return false;
            }
        }

        return super.request();
    }

    @Override
    protected void modify(float[] data, int frames, int channels, int rate)
    {
        if(mState.get() != PLAY)
        {
            //Time delay        = new Time(mDelay.get(), Time.Unit.NANO);
            Time delaySegment = new Time(frames, rate);
            mDelay.addAndGet(-delaySegment.getInNanoSeconds());
        }

        super.propagate(data, frames, channels, rate);
    }
}
