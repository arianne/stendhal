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
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

import games.stendhal.client.sound.Dsp;
import games.stendhal.client.sound.Field;
import games.stendhal.client.sound.facade.Time;

/**
 * Thread to manage sound output.
 * @author silvio
 */
public class SoundSystem extends Thread
{
	public static abstract class Output extends SignalProcessor { }

	private static class DummyOutput extends Output { }

	private static class SystemOutput extends Output
	{
		final SourceDataLine mLine;                   // the line we will write the PCM data to
		final AudioFormat    mFormat;
        float[]              mAudioBuffer     = null; // the interleaved uniform PCM data
		int                  mNumSamples      = 0;    //
        byte[]               mPCMBuffer       = null; // the uniform PCM data converted to the format defined in "mAudioFormat"
		int                  mPCMBufferSize   = 0;    //
		int                  mNumBytesWritten = 0;    // number of bytes from "mPCMBuffer" we have written to "mLine"
		int                  mNumBytesToWrite = 0;

		SystemOutput(SourceDataLine line)
		{
			assert line != null;
			assert line.isOpen();
			mLine   = line;
			mFormat = line.getFormat();
			mLine.start();
		}

		boolean     isOpen              () { return mLine.isOpen();                    }
        boolean     receivedData        () { return mNumSamples    > 0;                }
        boolean     isConverted         () { return mPCMBufferSize > 0;                }
		AudioFormat getAudioFormat      () { return mFormat;                           }
//		float[]     getBuffer           () { return mAudioBuffer;                      }
//		int         getNumSamples       () { return mNumSamples;                       }
		int         getNumChannels      () { return mFormat.getChannels();             }
		int         getSampleRate       () { return (int)mFormat.getSampleRate();      }
		int         getNumBytesPerSample() { return mFormat.getSampleSizeInBits() / 8; }
		int         available           () { return mLine.available();                 }
		int         getNumBytesToWrite  () { return mNumBytesToWrite;                  }

		void close()
		{
			mLine.close();
			mAudioBuffer = null;
			mPCMBuffer   = null;
			reset();
		}

		void reset()
		{
			mNumBytesWritten = 0;
			mNumSamples      = 0;
			mPCMBufferSize   = 0;
		}

		void setBuffer(float[] buffer, int numSamples)
		{
			assert (buffer == null) || (numSamples <= buffer.length);
			assert (numSamples % getNumChannels()) == 0;

			mAudioBuffer = buffer;
			mNumSamples  = numSamples;
		}

		void setNumBytesToWrite(int numBytesToWrite)
		{
			int frameSize = getNumBytesPerSample() * getNumChannels();

			mNumBytesToWrite  = numBytesToWrite;
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

			numBytes  = Math.min(numBytes, numRemainingBytesInBuffer);
			numBytes  = Math.min(numBytes, mNumBytesToWrite         );
			numBytes  = Math.min(numBytes, mLine.available()        );
			numBytes /= frameSize;
			numBytes *= frameSize;
			numBytes  = mLine.write(mPCMBuffer, mNumBytesWritten, numBytes);

			mNumBytesWritten          += numBytes;
			mNumBytesToWrite          -= numBytes;
			numRemainingBytesInBuffer -= numBytes;

			if(numRemainingBytesInBuffer < frameSize) {
				reset();
			}

			return (mNumBytesToWrite >= frameSize);
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
				//assert false: "could not convert sample rate";
			}
        }
	}

	private static class MixerOutput extends Output
	{
		AudioFormat  mAudioFormat;
		float[]      mAudioBuffer     = null; // the interleaved uniform PCM data
		int          mNumSamples      = 0;    //
		int          mNumSamplesMixed = 0;

		MixerOutput(AudioFormat format)
		{
			assert format != null;
			mAudioFormat = format;
		}

		boolean receivedData  () { return mNumSamples > 0;                   }
		int     getNumChannels() { return mAudioFormat.getChannels();        }
		int     getSampleRate () { return (int)mAudioFormat.getSampleRate(); }

		void reset()
		{
			mNumSamples      = 0;
			mNumSamplesMixed = 0;
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

				Dsp.mixAudioData(buffer, offset, mAudioBuffer, mNumSamplesMixed, numSamples, 1.0f);

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
				//assert false: "could not convert sample rate";
			}
        }
	}

	private final static int    STATE_EXITING = 0;
	private final static int    STATE_RUNNING = 1;
	private final static int    STATE_PAUSING = 2;
	private final static Time   ZERO_DURATION = new Time();
	private final static Logger logger        = Logger.getLogger(SoundSystem.class);

	private final LinkedList<SystemOutput> mSystemOutputs         = new LinkedList<SystemOutput>();
	private final LinkedList<MixerOutput>  mMixerOutputs          = new LinkedList<MixerOutput>();
	private SystemOutput                   mMixSystemOutput       = null;
    private Mixer                          mSystemMixer           = null;
    private Time                           mBufferDuration        = null;
    private final AtomicBoolean            mUseDynamicLoadScaling = new AtomicBoolean(false);
	private final AtomicReference<Time>    mStateChangeDelay      = new AtomicReference<Time>(ZERO_DURATION);
	private final AtomicInteger            mTargetSystemState     = new AtomicInteger(0);
	private int                            mCurrentSystemState    = 0;
	private int                            mMaxNumLines           = 0;
	private float[]                        mMixBuffer             = null;

    public SoundSystem(Mixer mixer, AudioFormat audioFormat, Time bufferDuration, int useMaxMixerLines)
    {
		if(audioFormat == null) {
			throw new IllegalArgumentException("audioFormat argument must not be null");
		}
		if(bufferDuration == null) {
			throw new IllegalArgumentException("bufferDuration argument must not be null");
		}

		if(mixer == null)
		{
			logger.info("opening sound system and trying to find optimal system mixer device / output line");
			mixer = tryToFindMixer(audioFormat);
		}
		else
		{
			logger.info("opening sound system using specified system mixer device");
		}

		init(mixer, audioFormat, bufferDuration, useMaxMixerLines);
    }

    public SoundSystem(SourceDataLine outputLine, Time bufferDuration)
    {
		if(outputLine == null) {
			throw new IllegalArgumentException("outputLine argument must not be null");
		}
		if(bufferDuration == null) {
			throw new IllegalArgumentException("bufferDuration argument must not be null");
		}

		logger.info("opening sound system with only manual mixing enabled");

        mBufferDuration = bufferDuration;
		mMaxNumLines    = 0;

        if(!outputLine.isOpen())
        {
            try
            {
                outputLine.open();
            }
            catch(LineUnavailableException e)
            {
				logger.warn("cannot open output line for manual mixing of audio data: \"" + e + "\"");
				return;
            }
			catch(SecurityException e)
			{
				logger.warn("cannot open output line for manual mixing of audio data: \"" + e + "\"");
				return;
            }
        }

		mMixSystemOutput = new SystemOutput(outputLine);
    }

	public void suspend(Time delay)
	{
		changeSystemState(STATE_PAUSING, delay);
	}

	public void proceed(Time delay)
	{
		changeSystemState(STATE_RUNNING, delay);
	}

	public Output openOutput(AudioFormat audioFormat)
	{
		if(audioFormat == null) {
			throw new IllegalArgumentException("audioFormat argument must not be null");
		}

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);

        if(mSystemMixer != null && mSystemMixer.isLineSupported(info) && mSystemOutputs.size() < mMaxNumLines)
        {
            try
            {
                SourceDataLine line = (SourceDataLine)mSystemMixer.getLine(info);
                line.open();

				SystemOutput output = new SystemOutput(line);

				synchronized(mSystemOutputs)
				{
					mSystemOutputs.add(output);
				}

				logger.debug("opening a system output (using a system mixer device)");
				return output;
            }
            catch(LineUnavailableException e) { }
        }

		if(mMixSystemOutput != null)
		{
			MixerOutput output = new MixerOutput(mMixSystemOutput.getAudioFormat());

			synchronized(mMixerOutputs)
			{
				mMixerOutputs.add(output);
			}

			logger.debug("opening a mixer output (using manual mixing)");
			return output;
		}

		logger.debug("opening a dummy output (no output line could be received)");
		return new DummyOutput();
	}

    public void closeOutput(Output output)
    {
		if(output != null)
		{
			output.disconnect();

			synchronized(mSystemOutputs)
			{
				if(output instanceof SystemOutput)
				{
					logger.debug("closing a system output");

					SystemOutput systemOutput = (SystemOutput)output;
					systemOutput.close();
					mSystemOutputs.remove(systemOutput);
				}
			}

			synchronized(mMixerOutputs)
			{
				if(output instanceof MixerOutput)
				{
					logger.debug("closing a mixer output");

					MixerOutput mixerOutput = (MixerOutput)output;
					mMixerOutputs.remove(mixerOutput);
				}
			}
		}
    }

    public void closeAllOutputs()
    {
		logger.debug("closing all outputs excluding the output for manual mixing");

		synchronized(mSystemOutputs)
		{
			for(SystemOutput output: mSystemOutputs) {
				output.close();
			}

			mSystemOutputs.clear();
		}

		synchronized(mMixerOutputs)
		{
			mMixerOutputs.clear();
		}
    }

    public void exit(Time delay)
    {
		logger.info("stopping sound system");
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
					if(mCurrentSystemState != mTargetSystemState.get())
					{
						if(mStateChangeDelay.get().getInNanoSeconds() <= 0)
						{
							mCurrentSystemState = mTargetSystemState.get();
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
						mCurrentSystemState = mTargetSystemState.get();
						mChangeState        = false;
					}
				}
			}
		}

        double       averageTimeToProcessSound = 0;//mBufferDuration.getInNanoSeconds();
        double       multiplicator             = 0.995;
		StateChanger stateChanger              = new StateChanger();

		mCurrentSystemState = STATE_RUNNING;
		mTargetSystemState.set(STATE_RUNNING);
		mStateChangeDelay.set(ZERO_DURATION);

        while(mCurrentSystemState != STATE_EXITING)
        {
			stateChanger.processStateChange();

			switch(mCurrentSystemState)
			{
			case STATE_RUNNING:
				{
					long duration = System.nanoTime();

					processOutputs();

					if(mUseDynamicLoadScaling.get())
					{
						duration                  = System.nanoTime() - duration;
						averageTimeToProcessSound = (averageTimeToProcessSound + duration) / 2.0;

						if(averageTimeToProcessSound > mBufferDuration.getInNanoSeconds()) {
							averageTimeToProcessSound = mBufferDuration.getInNanoSeconds();
						}

						long nanos = (long)((mBufferDuration.getInNanoSeconds() - averageTimeToProcessSound) * multiplicator);
						suspendThread(nanos);
					}
				}
				break;

			case STATE_PAUSING:
				suspendThread(50);
				break;
			}
        }

        closeAllOutputs();
		closeOutput(mMixSystemOutput);
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
		LinkedList<SystemOutput> sysOutputs;
		LinkedList<MixerOutput>  mixOutputs;

		synchronized(mSystemOutputs)
		{
			sysOutputs = (LinkedList<SystemOutput>)mSystemOutputs.clone();
		}

		synchronized(mMixerOutputs)
		{
			mixOutputs = (LinkedList<MixerOutput>)mMixerOutputs.clone();
		}

		if(mMixSystemOutput != null) {
			sysOutputs.add(mMixSystemOutput);
		}

		for(SystemOutput output: sysOutputs)
		{
			int sampleRate        = output.getSampleRate();
			int numBytesPerSample = output.getAudioFormat().getSampleSizeInBits() / 8;
			int numChannels       = output.getNumChannels();
			int numBytesToWrite   = (int)(mBufferDuration.getInSamples(sampleRate) * numChannels * numBytesPerSample);

			output.setNumBytesToWrite(numBytesToWrite);
		}

		if(mMixSystemOutput != null)
		{
			int numSamples = mMixSystemOutput.getNumBytesToWrite() / mMixSystemOutput.getNumBytesPerSample();
			mMixBuffer = Field.expand(mMixBuffer, numSamples, false);
			Arrays.fill(mMixBuffer, 0, numSamples, 0.0f);

			for(MixerOutput output: mixOutputs) {
				output.mix(mMixBuffer, numSamples);
			}

			mMixSystemOutput.setBuffer(mMixBuffer, numSamples);
		}

		while(!sysOutputs.isEmpty())
		{
			boolean buffersAreFull = true;

			for(Iterator<SystemOutput> iOutput = sysOutputs.iterator(); iOutput.hasNext(); )
			{
				SystemOutput output = iOutput.next();

				if(output.isOpen())
				{
					if(!output.receivedData())
					 {
						output.request(); // if we got no sound data we request it
					}

					if(output.receivedData())
					{
						if(!output.isConverted()) {
							output.convert();
						}

						buffersAreFull = buffersAreFull && (output.available() == 0);

						if(output.write(1000)) {
							continue;
						}
					}
				}

				iOutput.remove();
			}

			if(buffersAreFull && !mUseDynamicLoadScaling.get()) {
				suspendThread(50);
			}
		}
	}

	private void init(Mixer mixer, AudioFormat audioFormat, Time bufferDuration, int useMaxMixerLines)
	{
        mSystemMixer     = mixer;
        mBufferDuration  = bufferDuration;
		mMixSystemOutput = null;

		if(mSystemMixer != null)
		{
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);

			try
            {
                SourceDataLine line = (SourceDataLine)mSystemMixer.getLine(info);
                line.open();

				mMixSystemOutput = new SystemOutput(line);
				mMaxNumLines     = mSystemMixer.getMaxLines(info);
				mMaxNumLines     = (mMaxNumLines == AudioSystem.NOT_SPECIFIED) ? (Integer.MAX_VALUE) : (mMaxNumLines);
				mMaxNumLines     = Math.min(useMaxMixerLines, (mMaxNumLines - 1));
            }
            catch(LineUnavailableException e)
			{
				logger.warn("cannot open output line of system mixer device: \"" + e + "\"");
			}
			catch(IllegalArgumentException e)
			{
				logger.warn("cannot open output line of system mixer device: \"" + e + "\"");
			}
			catch(SecurityException e)
			{
				logger.warn("cannot open output line of system mixer device: \"" + e + "\"");
			}
		}

        if(mSystemMixer == null)
        {
			logger.info("try to open a common output line");

            DataLine.Info datalineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);

            try
            {
                SourceDataLine line = (SourceDataLine)AudioSystem.getLine(datalineInfo);
                line.open();

				mMixSystemOutput = new SystemOutput(line);
				mMaxNumLines     = 0;
            }
            catch(LineUnavailableException e)
            {
				logger.warn("cannot open common output line for manual mixing of audio data: \"" + e + "\"");
            }
			catch(IllegalArgumentException e)
			{
				logger.warn("cannot open common output line for manual mixing of audio data: \"" + e + "\"");
			}
			catch(SecurityException e)
			{
				logger.warn("cannot open common output line for manual mixing of audio data: \"" + e + "\"");
			}
        }

		if(mSystemMixer == null && mMixSystemOutput == null) {
			mMaxNumLines = 0;
		}
	}

    private void suspendThread(long nanos)
    {
        try
        {
            long millis = nanos / Time.Unit.MILLI.getNanos();

            nanos %= Time.Unit.MILLI.getNanos();

            sleep(millis, (int)nanos);
        }
        catch(InterruptedException ex) { }
    }

    public static Mixer tryToFindMixer(AudioFormat audioFormat)
    {
        Mixer.Info[]        mixerInfos   = AudioSystem.getMixerInfo();
        Mixer[]             mixers       = new Mixer[mixerInfos.length];
        final DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);

        if(mixers.length == 0) {
			return null;
		}

        for(int i=0; i<mixerInfos.length; ++i) {
			mixers[i] = AudioSystem.getMixer(mixerInfos[i]);
		}

        Arrays.sort(mixers, new Comparator<Mixer>()
        {
            @Override
			public int compare(Mixer mixer1, Mixer mixer2)
            {
                int numLines1 = mixer1.getMaxLines(dataLineInfo);
                int numLines2 = mixer2.getMaxLines(dataLineInfo);

                if(numLines1 == AudioSystem.NOT_SPECIFIED || numLines1 > numLines2) {
					return -1;
				}

                return 1;
            }
        });

        if(mixers[0].getMaxLines(dataLineInfo) == 0) {
			return null;
		}

        return mixers[0];
    }
}
