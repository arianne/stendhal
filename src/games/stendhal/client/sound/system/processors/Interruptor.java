/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.system.processors;

import games.stendhal.client.sound.system.SignalProcessor;
import games.stendhal.client.sound.system.Time;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
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
    protected void modify(float[] data, int samples, int channels, int rate)
    {
        if(mState.get() != PLAY)
        {
            //Time delay        = new Time(mDelay.get(), Time.Unit.NANO);
            Time delaySegment = new Time(samples, rate);
            mDelay.addAndGet(-delaySegment.getInNanoSeconds());
        }

        super.propagate(data, samples, channels, rate);
    }
}
