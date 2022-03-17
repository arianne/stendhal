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
package games.stendhal.client.sound;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import games.stendhal.client.sound.system.ToneGeneratorTest;
import games.stendhal.client.sound.system.processors.ToneGenerator;

/**
 * Tests for DSP algorithms.
 * @author Martin Fuchs
 */
public class DspTest {

	private final static float[] inputData = new float[] {
		-0.5f, -0.2f,
		 0.0f,  0.0f,
		+0.5f, +0.2f
	};

	/**
	 * Call resampling function with unchanged sample rate.
	 */
	@Test
	public final void test0() {
		// keep sampling rate
		final float[] buffer = Dsp.convertSampleRate(inputData, inputData.length/2, 2, 44100, 44100);

		assertEquals("check output buffer size", inputData.length, buffer.length);

		for(int i=0; i<buffer.length; ++i) {
			assertEquals(inputData[i], buffer[i], .0);
		}
	}

	/**
	 * Test simple signal resampling.
	 */
	@Test
	public final void test1() {
		// convert to a rate of 44100 samples/sec
		final float[] buffer = Dsp.convertSampleRate(inputData, inputData.length/2, 2, 11025, 44100);

		assertEquals("check output buffer size", inputData.length*4, buffer.length);
		assertEquals(-0.5, buffer[0], .0001);
		assertEquals(-0.2, buffer[1], .0001);
		assertEquals( 0.0, buffer[8], .0001);
		assertEquals( 0.0, buffer[9], .0001);
		assertEquals(0.5, buffer[22], .0001);
		assertEquals(0.2, buffer[23], .0001);
	}

	/**
	 * Test signal resampling using the sound generator.
	 */
	@Test
	public final void testGenerated() {
		// generate a 1 kHz signal
		final ToneGenerator gen = new ToneGenerator(1, 44100, 10*44100);
		gen.addTone(new ToneGenerator.Tone(1.f, 1000.f));
		final ToneGeneratorTest.Receiver rec = new ToneGeneratorTest.Receiver(gen);
		rec.request();

		assertEquals("check output buffer size", 10*44100, rec._data.length);

		// sample down to 22050 samples/s
		final float[] buffer = Dsp.convertSampleRate(rec._data, rec._frames, rec._channels, rec._rate, 22050);

		assertEquals("check output buffer size", 220500, buffer.length);
	}
}
