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
package games.stendhal.client.sound.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import games.stendhal.client.sound.system.processors.ToneGenerator;

/**
 * Tests for the ToneGenerator class.
 * @author Martin Fuchs
 */
public class ToneGeneratorTest {

	/**
	 * Test empty signal generation.
	 */
	@Test
	public final void testNoSignal() {
		// generate a signal without adding a Tone
		final ToneGenerator gen = new ToneGenerator(1, 44100, 10*44100);
		final Receiver rec = new Receiver(gen);
		rec.request();

		assertEquals(10*44100, rec._data.length);
		assertEquals(10*44100, rec._frames);
		assertEquals(1, rec._channels);
		assertEquals(44100, rec._rate);

		// The whole buffer has to be 0.
		for(float f : rec._data) {
			assertEquals(0.f, f, 0.f);
		}
	}

	/**
	 * Test 1 channel signal generation.
	 */
	@Test
	public final void test1Sine1kHz() {
		// generate a 1 kHz signal
		final ToneGenerator gen = new ToneGenerator(1, 44100, 10*44100);
		gen.addTone(new ToneGenerator.Tone(1.f, 1000.f));
		final Receiver rec = new Receiver(gen);
		rec.request();

		assertEquals(10*44100, rec._data.length);
		assertEquals(10*44100, rec._frames);
		assertEquals(1, rec._channels);
		assertEquals(44100, rec._rate);

		float max = 0.f;
		float sum = 0.f;
		for(float f : rec._data) {
			if (f > max) {max = f;}
			sum += f;
		}
		assertTrue(max > 0.f);
		assertTrue(max <= 1.f);
		assertEquals(0.f, sum, 0.01f);
	}

	/**
	 * Test 2 channel signal generation.
	 */
	@Test
	public final void test2Sine1kHz() {
		// generate a two channel 1 kHz signal
		final ToneGenerator gen = new ToneGenerator(2, 44100, 10*44100);
		gen.addTone(new ToneGenerator.Tone(1.f, 1000.f));
		final Receiver rec = new Receiver(gen);
		rec.request();

		assertEquals(2*10*44100, rec._data.length);
		assertEquals(10*44100, rec._frames);
		assertEquals(2, rec._channels);
		assertEquals(44100, rec._rate);

		float max1 = 0.f, max2 = 0.f;
		float sum1 = 0.f, sum2 = 0.f;
		for(int i=0; i<rec._data.length; ) {
			float f = rec._data[i++];
			if (f > max1) {max1 = f;}
			sum1 += f;
			f = rec._data[i++];
			if (f > max2) {max2 = f;}
			sum2 += f;
		}
		assertTrue(max1 > 0.f);
		assertTrue(max1 <= 1.f);
		assertEquals(0.f, sum1, 0.01f);
		assertTrue(max2 > 0.f);
		assertTrue(max2 <= 1.f);
		assertEquals(0.f, sum2, 0.01f);
	}

	/**
	 * Sound receiver to store the processed sound signal.
	 */
	public static final class Receiver extends SignalProcessor
	{
		public float[] _data;
		public int _frames;
		public int _channels;
		public int _rate;

		public Receiver(final SignalProcessor sp)
		{
			// link with the sound source
			insert(sp, false);
		}

		@Override
	    protected void modify(float[] data, int frames, int channels, int rate)
	    {
			// store the received data
			_data = data;
			_frames = frames;
			_channels = channels;
			_rate = rate;
	    }
	}
}
