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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

import games.stendhal.client.sound.Dsp;
import games.stendhal.client.sound.Field;
import games.stendhal.client.sound.facade.Time;
import games.stendhal.common.math.Numeric;

/**
 * Thread to manage sound output.
 * @author silvio
 */
public class SoundSystemNG extends Thread
{
	private static class SystemOutput
	{
		final SourceDataLine mLine;                   // the line we will write the PCM data to
		final AudioFormat    mFormat;
        float[]              mAudioBuffer     = null; // the interleaved normalized audio data
		int                  mNumSamples      = 0;    //
        byte[]               mPCMBuffer       = null; // the uniform PCM data converted to the format defined in "mAudioFormat"
		int                  mPCMBufferSize   = 0;    //
		int                  mNumBytesWritten = 0;    // number of bytes from "mPCMBuffer" we have written to "mLine"
		int                  mNumBytesToWrite = 0;

		SystemOutput(SourceDataLine line)
		{
			assert line != null;
			mLine   = line;
			mFormat = line.getFormat();
		}

		boolean isReady             () { return mLine.isOpen() && mLine.isRunning();       }
//		int     getNumSamples       () { return mNumSamples;                               }
		int     getNumChannels      () { return mFormat.getChannels();                     }
		int     getSampleRate       () { return (int)mFormat.getSampleRate();              }
		int     getNumBytesPerSample() { return mFormat.getSampleSizeInBits() / 8;         }
		int     available           () { return mLine.available();                         }
		int     getNumBytesToWrite  () { return mNumBytesToWrite;                          }
		int     getFrameSize        () { return getNumBytesPerSample() * getNumChannels(); }

		void close()
		{
			if(mLine.isOpen()) {
				mLine.close();
			}
		}

		boolean start()
		{
			if(!mLine.isOpen())
			{
				try
				{
					mLine.open();
				}
				catch(LineUnavailableException e) { return false; }
				catch(SecurityException        e) { return false; }
			}

			if(!mLine.isRunning()) {
				mLine.start();
			}

			return true;
		}

		void pause()
		{
			if(mLine != null && mLine.isOpen() && mLine.isRunning())
			{
				mLine.stop();
				mLine.flush();
			}
		}

		void reset()
		{
			mNumBytesWritten = 0;
			mNumSamples      = 0;
			mPCMBufferSize   = 0;
		}

		void setBuffer(float[] buffer, int numSamples)
		{
			assert numSamples <= buffer.length;
			assert (numSamples % getNumChannels()) == 0;

			mAudioBuffer = buffer;
			mNumSamples  = numSamples;
		}

		void setNumBytesToWrite(int numBytesToWrite)
		{
			int frameSize = getNumBytesPerSample() * getNumChannels();

			mNumBytesToWrite  = Math.min(numBytesToWrite, mLine.getBufferSize());
			mNumBytesToWrite /= frameSize;
			mNumBytesToWrite *= frameSize;
		}

		void convert()
        {
            assert (mLine.getFormat().getSampleSizeInBits() % 8) == 0;

            int numBytesPerSample = getNumBytesPerSample();
			int numBytes          = numBytesPerSample * mNumSamples;

			mPCMBuffer     = Field.expand(mPCMBuffer, numBytes, false);
            mPCMBuffer     = Dsp.convertUniformPCM(mPCMBuffer, mAudioBuffer, mNumSamples, numBytesPerSample);
			mPCMBufferSize = numBytes;
        }

		boolean write(int numBytes)
        {
			int frameSize                 = getNumBytesPerSample() * getNumChannels();
			int numRemainingBytesInBuffer = mPCMBufferSize - mNumBytesWritten;
			int available                 = mLine.available();

			// ATTENTION: this should never happen but if it happens, it is likely
			//            that the mLine.available() method returns an "unsigned int"
			//            from its native C method and thus will be handled as an
			//            "signed int" in java. for a fix we simply set the value
			//            to the highest possible signed interger value
			if(available < 0) {
				available = Integer.MAX_VALUE;
			}

			numBytes  = Math.min(numBytes, numRemainingBytesInBuffer);
			numBytes  = Math.min(numBytes, mNumBytesToWrite         );
			numBytes  = Math.min(numBytes, available                );
			numBytes /= frameSize;
			numBytes *= frameSize;
			numBytes  = mLine.write(mPCMBuffer, mNumBytesWritten, numBytes);

			// ATTENTION: if no audio data was written to the output line even though
			//            the line is not full (mLine.available() is not 0)
			//            the line blocks for some (unknown) reason
			//            the simplest workaround here is to simply discard the remaining
			//            audio data and pretend everything was written (return false)
			if(available > 0 && numBytes == 0) {
				return false;
			}

			mNumBytesWritten          += numBytes;
			mNumBytesToWrite          -= numBytes;
			numRemainingBytesInBuffer -= numBytes;

			if(numRemainingBytesInBuffer < frameSize) {
				reset();
			}

			return (mNumBytesToWrite >= frameSize);
        }
	}

	public static class Output extends SignalProcessor
	{
		AudioFormat         mAudioFormat;
		float[]             mAudioBuffer     = null; // the interleaved normalized PCM data
		float[]             mMixingBuffer    = null;
		int                 mNumSamples      = 0;    // the number of samples received
		int                 mNumSamplesMixed = 0;
		final AtomicInteger mLevel           = new AtomicInteger(0);
		final AtomicInteger mIntensity       = new AtomicInteger(PRECISION);

		Output(AudioFormat format, int level)
		{
			assert format != null;
			mAudioFormat = format;
			mLevel.set(level);
		}

		public void  setIntensity(float intensity) { mIntensity.set(Numeric.floatToInt(intensity, PRECISION)); }
		public float getIntensity()                { return Numeric.intToFloat(mIntensity.get(), PRECISION);   }

		boolean receivedData   () { return mNumSamples > 0;                   }
		int     getNumChannels () { return mAudioFormat.getChannels();        }
		int     getSampleRate  () { return (int)mAudioFormat.getSampleRate(); }
		int     getLayer       () { return mLevel.get();                      }
		float[] getMixingBuffer() { return mMixingBuffer;                     }

		void reset()
		{
			mNumSamples      = 0;
			mNumSamplesMixed = 0;
		}

		void clearMixingBuffer(int bufferSize)
		{
			mMixingBuffer = Field.expand(mMixingBuffer, bufferSize, false);
			Arrays.fill(mMixingBuffer, 0.0f);
		}

		void setBuffer(float[] buffer, int numSamples)
		{
			assert (buffer == null) || (numSamples <= buffer.length);
			assert (numSamples % getNumChannels()) == 0;

			mAudioBuffer = buffer;
			mNumSamples  = numSamples;
		}

		boolean mix(float[] buffer, int size)
		{
			int offset          = 0;
			int numSamplesToMix = size;

			while(numSamplesToMix > 0)
			{
				if(!receivedData()) {
					request();
				}

				if(!receivedData()) {
					return false;
				}

				int numSamples = mNumSamples - mNumSamplesMixed;
				numSamples = Math.min(numSamples, numSamplesToMix);

				Dsp.mixAudioData(buffer, offset, mAudioBuffer, mNumSamplesMixed, numSamples, Numeric.intToFloat(mIntensity.get(), PRECISION));

				offset           += numSamples;
				mNumSamplesMixed += numSamples;
				numSamplesToMix  -= numSamples;

				if(mNumSamples == mNumSamplesMixed) {
					reset();
				}
			}

			return true;
		}

		@Override
        protected void modify(float[] buffer, int frames, int channels, int rate)
        {
            if (buffer != null && frames > 0 && channels > 0 && rate > 0)
            {
                assert (frames * channels) <= buffer.length;
				buffer = Dsp.convertChannels(buffer, frames, channels, getNumChannels());

				setBuffer(buffer, (frames * getNumChannels()));

				buffer = Dsp.convertSampleRate(buffer, (frames * channels), channels, rate, getSampleRate());

				float ratio = (float)frames / (float)rate;
				setBuffer(buffer, (int)(ratio * getSampleRate() * channels));
			}
			else
			{
				setBuffer(null, 0);
				assert false: "could not convert sample rate";
			}
        }
	}

	private final static int    STATE_EXITING   = 0;
	private final static int    STATE_RUNNING   = 1;
	private final static int    STATE_PAUSING   = 2;
	private final static int    STATE_SUSPENDED = 3;
	private final static Time   ZERO_DURATION   = new Time();
	private final static Time   SLEEP_DURATION  = new Time(100, Time.Unit.MILLI);
	private final static int    PRECISION       = 1000000;
	private final static Logger logger          = Logger.getLogger(SoundSystemNG.class);

	private final LinkedList<Output>     mMixerOutputs          = new LinkedList<Output>();
	private volatile SystemOutput        mSystemOutput          = null;
    private volatile AudioFormat         mAudioFormat           = null;
    private final AtomicReference<Time>  mBufferDuration        = new AtomicReference<Time>(null);
    private final AtomicBoolean          mUseDynamicLoadScaling = new AtomicBoolean(false);
	private final AtomicReference<Time>  mStateChangeDelay      = new AtomicReference<Time>(ZERO_DURATION);
	private final AtomicInteger          mTargetSystemState     = new AtomicInteger(STATE_EXITING);
	private final AtomicInteger          mCurrentSystemState    = new AtomicInteger(STATE_EXITING);
	private float[]                      mMixBuffer             = null;

	public SoundSystemNG(AudioFormat audioFormat, Time bufferDuration)
	{
		init(null, audioFormat, bufferDuration);
	}

    public SoundSystemNG(SourceDataLine outputLine, Time bufferDuration)
    {
		if(outputLine == null) {
			throw new IllegalArgumentException("outputLine argument must not be null");
		}
		if(bufferDuration == null) {
			throw new IllegalArgumentException("bufferDuration argument must not be null");
		}

		init(outputLine, outputLine.getFormat(), bufferDuration);
    }

	public void suspend(Time delay, boolean closeSystemOutput)
	{
		if(closeSystemOutput) {
			changeSystemState(STATE_SUSPENDED, delay);
		} else {
			changeSystemState(STATE_PAUSING, delay);
		}
	}

	public void proceed(Time delay)
	{
		changeSystemState(STATE_RUNNING, delay);
	}

	public void proceed(Time delay, SourceDataLine outputLine)
    {
		if(outputLine != null && !outputLine.getFormat().matches(mAudioFormat)) {
			throw new IllegalArgumentException("outputLine has the wrong audio format");
		}

		init(outputLine, mAudioFormat, mBufferDuration.get());
		changeSystemState(STATE_RUNNING, delay);
	}

	public boolean isRunning()
	{
		return mCurrentSystemState.get() == STATE_RUNNING;
	}

	public Output openOutput(int layerID, SignalProcessor ...processorChain)
	{
		Output output = new Output(mAudioFormat, layerID);
		SignalProcessor.createChain(Field.append(processorChain, processorChain.length, output));

		synchronized(mMixerOutputs)
		{
			mMixerOutputs.add(output);
		}

		logger.debug("opening a mixer output (using manual mixing)");
		return output;
	}

    public void closeOutput(Output output)
    {
		if(output != null)
		{
			output.disconnect();

			synchronized(mMixerOutputs)
			{
				logger.debug("closing a mixer output");
				mMixerOutputs.remove(output);
			}
		}
    }

    public void closeAllOutputs()
    {
		logger.debug("closing all outputs excluding the output for manual mixing");

		synchronized(mMixerOutputs)
		{
			mMixerOutputs.clear();
		}
    }

    public void exit(Time delay)
    {
		logger.info("stopping sound system after " + ((delay != null) ? delay.getInMilliSeconds() : 0) + "ms");
		changeSystemState(STATE_EXITING, delay);
    }

    @Override
    public void run()
    {
		class StateChanger
		{
			long    mSystemTime  = 0;
			boolean mChangeState = false;

			void processStateChange()
			{
				if(!mChangeState)
				{
					if(mCurrentSystemState.get() != mTargetSystemState.get())
					{
						logger.debug("change state");

						if(mStateChangeDelay.get().getInNanoSeconds() <= 0)
						{
							mCurrentSystemState.set(mTargetSystemState.get());
							return;
						}

						mSystemTime  = System.nanoTime();
						mChangeState = true;
					}
				}
				else
				{
					if((System.nanoTime() - mSystemTime) >= mStateChangeDelay.get().getInNanoSeconds())
					{
						mCurrentSystemState.set(mTargetSystemState.get());
						mChangeState = false;
					}
				}
			}
		}

        double       averageTimeToProcessSound = 0;
        double       multiplicator             = 0.995;
		StateChanger stateChanger              = new StateChanger();

		mCurrentSystemState.set(STATE_RUNNING);
		mTargetSystemState.set(STATE_RUNNING);
		mStateChangeDelay.set(ZERO_DURATION);

        while(mCurrentSystemState.get() != STATE_EXITING)
        {
			stateChanger.processStateChange();

			switch(mCurrentSystemState.get())
			{
			case STATE_RUNNING:
				if(mSystemOutput == null)
				{
					changeSystemState(STATE_SUSPENDED, null);
					logger.warn("no output line was opened. sound system will be suspended");
				}
				else if(!mSystemOutput.isReady() && !mSystemOutput.start())
				{
					changeSystemState(STATE_SUSPENDED, null);
					logger.warn("suspend sound system due to unavailability of an output line");
				}
				else
				{
					long duration       = System.nanoTime();
					long bufferDuration = mBufferDuration.get().getInNanoSeconds();

					processOutputs();

					if(mUseDynamicLoadScaling.get())
					{
						duration                  = System.nanoTime() - duration;
						averageTimeToProcessSound = (averageTimeToProcessSound + duration) / 2.0;

						if(averageTimeToProcessSound > bufferDuration) {
							averageTimeToProcessSound = bufferDuration;
						}

						long nanos = (long)((bufferDuration - averageTimeToProcessSound) * multiplicator);
						threadSleep(nanos);
					}
				}
				break;

			case STATE_PAUSING:
				mSystemOutput.pause();
				threadSleep(SLEEP_DURATION.getInNanoSeconds());
				break;

			case STATE_SUSPENDED:
				if(mSystemOutput != null) {
					mSystemOutput.close();
				}
				threadSleep(SLEEP_DURATION.getInNanoSeconds());
				break;
			}
        }

        closeAllOutputs();

		if(mSystemOutput != null) {
			mSystemOutput.close();
		}
    }

	private void changeSystemState(int state, Time delay)
	{
		if(delay == null) {
			delay = ZERO_DURATION;
		} else {
			delay = delay.clone();
		}

		mStateChangeDelay.set(delay);
		mTargetSystemState.set(state);
	}

	@SuppressWarnings("unchecked")
	private void processOutputs()
	{
		LinkedList<Output> mixerOutputs;
		SystemOutput       systemOutput = mSystemOutput;

		synchronized(mMixerOutputs)
		{
			mixerOutputs = (LinkedList<Output>)mMixerOutputs.clone();
		}

		int sampleRate        = systemOutput.getSampleRate();
		int numBytesPerSample = systemOutput.getNumBytesPerSample();
		int numChannels       = systemOutput.getNumChannels();
		int numBytesToWrite   = (int)(mBufferDuration.get().getInSamples(sampleRate) * numChannels * numBytesPerSample);

		systemOutput.setNumBytesToWrite(numBytesToWrite);
		numBytesToWrite = systemOutput.getNumBytesToWrite();

		int numSamples = numBytesToWrite / numBytesPerSample;

		mMixBuffer = Field.expand(mMixBuffer, numSamples, false);
		Arrays.fill(mMixBuffer, 0, numSamples, 0.0f);

		for(Output output: mixerOutputs) {
			output.mix(mMixBuffer, numSamples);
		}

		systemOutput.setBuffer(mMixBuffer, numSamples);
		systemOutput.convert();

		boolean lineIsBlocked = false;

		while(systemOutput.write(Integer.MAX_VALUE))
		{
			if(systemOutput.available() < systemOutput.getFrameSize())
			{
				// if the line is full even though we waited a few milliseconds
				// we will asume that the line is blocked for some (unknown) reason
				// so we will discard the rest of the non written audio data and return
				if(lineIsBlocked) {
					return;
				}

				// if the buffer of the output line is full we will wait a few milliseconds
				// to let the system mixer process the audio data
				threadSleep(SLEEP_DURATION.getInNanoSeconds());
				lineIsBlocked = true;
			}
			else
			{
				lineIsBlocked = false;
			}
		}
	}

	private void init(SourceDataLine line, AudioFormat audioFormat, Time bufferDuration)
	{
		mBufferDuration.set(bufferDuration);
        mAudioFormat = audioFormat;

		if(line != null)
		{
			logger.info("opening sound system with line \"" + line + "\" and audio format \"" + audioFormat + "\"");
			mSystemOutput = new SystemOutput(line);
		}
		else
		{
			mSystemOutput = null;
		}
	}

    private void threadSleep(long nanos)
    {
        try
        {
            long millis = nanos / Time.Unit.MILLI.getNanos();

            nanos %= Time.Unit.MILLI.getNanos();

            sleep(millis, (int)nanos);
        }
        catch(InterruptedException ex) { }
    }
}
