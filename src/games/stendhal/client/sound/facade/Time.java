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
package games.stendhal.client.sound.facade;

/**
 * A time class that helps converting different time units
 * to and from samples per sample rate
 * <p>
 * internally this class will calculate in nanosecond resolution only
 *
 * @author silvio
 */
public class Time implements Cloneable
{
    /**
     * Enumeration of different time units
     */
    public static enum Unit
    {
        NANO  (1                    ),
        MILLI (NANO.mBase  * 1000000),
        SEC   (MILLI.mBase * 1000   ),
        MIN   (SEC.mBase   * 60     ),
        HOUR  (MIN.mBase   * 60     );

        private final long mBase;

        Unit(long base) { mBase = base; }

        /**
         * @return returns the specific time unit in nano seconds
         */
        public long getNanos() { return mBase; }
    }

    private long mNanoSeconds;

    public Time()
    {
        mNanoSeconds = 0;
    }

	public Time(long nanos)
    {
        mNanoSeconds = nanos;
    }

    /**
     * Creates an instance of the Time class
     * from a time value and a time unit
     *
     * @param value the time value
     * @param unit  the time unit (e.g. NANO, MILLI, SEC, ...)
     */
    public Time(long value, Unit unit)
    {
        set(value, unit);
    }

    /**
     * Creates an instance of the Time class
     * from a given number of samples and a sample rate
     *
     * @param samples
     * @param sampleRate
     */
    public Time(long samples, int sampleRate)
    {
        set(samples, sampleRate);
    }

    public final void set(long value, Unit unit)
    {
        mNanoSeconds = value * unit.getNanos();
    }

    public final void set(long samples, int sampleRate)
    {
        mNanoSeconds = (samples * Unit.SEC.getNanos()) / sampleRate;
    }

    public long   getInNanoSeconds () { return mNanoSeconds;                                         }
    public double getInMilliSeconds() { return (double)mNanoSeconds / (double)Unit.MILLI.getNanos(); }
    public double getInSeconds     () { return (double)mNanoSeconds / (double)Unit.SEC.getNanos();   }
    public double getInMinutes     () { return (double)mNanoSeconds / (double)Unit.MIN.getNanos();   }
    public double getInHours       () { return (double)mNanoSeconds / (double)Unit.HOUR.getNanos();  }

	void add(Time time ) { mNanoSeconds += time.mNanoSeconds; }
	void sub(Time time ) { mNanoSeconds -= time.mNanoSeconds; }
	void add(long nanos) { mNanoSeconds += nanos;             }
	void sub(long nanos) { mNanoSeconds -= nanos;             }

    /**
     * Calculates the number of samples for a given sample rate
     * that would fit into this time range
     *
     * @param sampleRate any sample rate
     * @return           number of samples
     */
    public double getInSamples(int sampleRate)
    {
        double seconds = (double)mNanoSeconds / (double)Unit.SEC.getNanos();
        return seconds * sampleRate;
    }

	@Override
	public Time clone()
	{
		return new Time(mNanoSeconds);
	}
}
