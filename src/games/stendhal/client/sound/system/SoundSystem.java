/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.system;

import games.stendhal.common.memory.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author silvio
 */
public class SoundSystem extends Thread
{
	public static abstract class Output extends SignalProcessor { }

	private static class SystemOutput extends Output
	{
		final SourceDataLine mLine;                   // the line we will write the PCM data to
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
			mLine = line;
			mLine.start();
		}

		boolean     isOpen              () { return mLine.isOpen();                              }
        boolean     receivedData        () { return mNumSamples    > 0;                          }
        boolean     isConverted         () { return mPCMBufferSize > 0;                          }
		AudioFormat getAudioFormat      () { return mLine.getFormat();                           }
		float[]     getBuffer           () { return mAudioBuffer;                                }
		int         getNumSamples       () { return mNumSamples;                                 }
		int         getNumChannels      () { return mLine.getFormat().getChannels();             }
		int         getSampleRate       () { return (int)mLine.getFormat().getSampleRate();      }
		int         getNumBytesPerSample() { return mLine.getFormat().getSampleSizeInBits() / 8; }
		int         available           () { return mLine.available();                           }
		int         getNumBytesToWrite  () { return mNumBytesToWrite;                            }

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
			assert numSamples <= buffer.length;
			assert (numSamples % getNumChannels()) == 0;

			mAudioBuffer = buffer;
			mNumSamples  = numSamples;
		}

		void setNumBytesToWrite(int numBytesToWrite)
		{
			int frameSize = getNumBytesPerSample() * getNumChannels();
			
			numBytesToWrite /= frameSize;
			numBytesToWrite *= frameSize;
			mNumBytesToWrite = numBytesToWrite;
		}

		void convert()
        {
            assert (mLine.getFormat().getSampleSizeInBits() % 8) == 0;

            int numBytesPerSample = getNumBytesPerSample();
			int numBytes          = numBytesPerSample * mNumSamples;

			mPCMBuffer     = Field.expand(mPCMBuffer, numBytes, false);
            mPCMBuffer     = SoundSystem.convertUniformPCM(mPCMBuffer, mAudioBuffer, mNumSamples, numBytesPerSample);
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
			
			numBytes = mLine.write(mPCMBuffer, mNumBytesWritten, numBytes);

			mNumBytesWritten          += numBytes;
			mNumBytesToWrite          -= numBytes;
			numRemainingBytesInBuffer -= numBytes;
			
			if(numRemainingBytesInBuffer < frameSize)
				reset();

			if(mNumBytesToWrite < frameSize)
				return false;

			return true;
        }

		@Override
        protected void modify(float[] buffer, int frames, int channels, int rate)
        {
            if(buffer != null && frames > 0 && channels > 0 && rate > 0)
            {
                assert frames <= buffer.length;
				buffer = SoundSystem.convertChannels(buffer, frames, channels, getNumChannels());

                if(buffer != null)
                {
					setBuffer(buffer, (frames * getNumChannels()));
                }
				else assert false: "could not convert channels";

				buffer = SoundSystem.convertSampleRate(buffer, (frames * channels), channels, rate, getSampleRate());

				if(buffer != null)
				{
					float ratio = (float)frames / (float)rate;
					setBuffer(buffer, (int)(ratio * getSampleRate() * channels));
				}
				else assert false: "could not convert sample rate";
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
			mAudioFormat  = format;
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
			assert numSamples <= buffer.length;
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
				if(!receivedData())
					request();

				if(!receivedData())
					return false;

				int numSamples = mNumSamples - mNumSamplesMixed;
				numSamples = Math.min(numSamples, numSamplesToMix);

				SoundSystem.mixUniformPCM(buffer, offset, mAudioBuffer, mNumSamplesMixed, numSamples);

				offset           += numSamples;
				mNumSamplesMixed += numSamples;
				numSamplesToMix  -= numSamples;

				if(mNumSamples == mNumSamplesMixed)
					reset();
			}

			return true;
		}

		@Override
        protected void modify(float[] buffer, int frames, int channels, int rate)
        {
            if(buffer != null && frames > 0 && channels > 0 && rate > 0)
            {
                assert frames <= buffer.length;
				buffer = SoundSystem.convertChannels(buffer, frames, channels, getNumChannels());

                if(buffer != null)
                {
					setBuffer(buffer, (frames * getNumChannels()));
                }
				else assert false: "could not convert channels";

				buffer = SoundSystem.convertSampleRate(buffer, (frames * channels), channels, rate, getSampleRate());

				if(buffer != null)
				{
					float ratio = (float)frames / (float)rate;
					setBuffer(buffer, (int)(ratio * getSampleRate() * channels));
				}
				else assert false: "could not convert sample rate";
            }
        }
	}

	private final LinkedList<SystemOutput> mSystemOutputs         = new LinkedList<SystemOutput>();
	private final LinkedList<MixerOutput>  mMixerOutputs          = new LinkedList<MixerOutput>();
	private SystemOutput                   mMixSystemOutput       = null;
    private Mixer                          mSystemMixer           = null;
    private Time                           mBufferDuration        = null;
    private final AtomicBoolean            mSystemIsRunning       = new AtomicBoolean(false);
    private final AtomicBoolean            mUseDynamicLoadScaling = new AtomicBoolean(false);
	float[]                                mMixBuffer             = null;
	private final int                      mMaxNumLines;
    
    public SoundSystem(AudioFormat audioFormat, Time bufferDuration, int useMaxMixerLines) throws SoundSystemException
    {
        assert audioFormat    != null;
        assert bufferDuration != null;

        mSystemMixer    = tryToFindMixer(audioFormat);
        mBufferDuration = bufferDuration;
		mMaxNumLines    = useMaxMixerLines;

        if(mSystemMixer == null)
        {
            DataLine.Info datalineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);

            try
            {
                SourceDataLine line = (SourceDataLine)AudioSystem.getLine(datalineInfo);
                line.open();
				
				mMixSystemOutput = new SystemOutput(line);
				mSystemOutputs.add(mMixSystemOutput);
            }
            catch(LineUnavailableException exception)
            {
                throw new SoundSystemException("line is unavailable - " + exception.toString());
            }
        }
		else
		{
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);

			try
            {
                SourceDataLine line = (SourceDataLine)mSystemMixer.getLine(info);
                line.open();

				mMixSystemOutput = new SystemOutput(line);
				mSystemOutputs.add(mMixSystemOutput);
            }
            catch(LineUnavailableException e) { }
		}
    }
//*
    public SoundSystem(Mixer mixer, AudioFormat audioFormat, Time bufferDuration, int useMaxMixerLines)
    {
		assert audioFormat    != null;
        assert mixer          != null;
        assert bufferDuration != null;

        mSystemMixer    = mixer;
        mBufferDuration = bufferDuration;
		mMaxNumLines    = useMaxMixerLines;

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);

		try
		{
			SourceDataLine line = (SourceDataLine)mSystemMixer.getLine(info);
			line.open();

			mMixSystemOutput = new SystemOutput(line);
			mSystemOutputs.add(mMixSystemOutput);
		}
		catch(LineUnavailableException e) { }
    }

    public SoundSystem(SourceDataLine outputLine, Time bufferDuration) throws SoundSystemException
    {
        assert outputLine     != null;
        assert bufferDuration != null;

        if(!outputLine.isOpen())
        {
            try
            {
                outputLine.open();
            }
            catch(LineUnavailableException e)
            {
                throw new SoundSystemException(e.toString());
            }
        }
		
		mMaxNumLines     = 0;
        mBufferDuration  = bufferDuration;
		mMixSystemOutput = new SystemOutput(outputLine);
		mSystemOutputs.add(mMixSystemOutput);
    }

	public Output openOutput(AudioFormat audioFormat)
	{
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

			return output;
		}
		
		return null;
	}

    public synchronized void closeOutput(Output output)
    {
		output.disconnect();

		if(output instanceof SystemOutput)
		{
			SystemOutput systemOutput = (SystemOutput)output;
			systemOutput.close();
			mSystemOutputs.remove(systemOutput);
		}

		if(output instanceof MixerOutput)
		{
			MixerOutput mixerOutput = (MixerOutput)output;
			mMixerOutputs.remove(mixerOutput);
		}
    }

    public void closeAllOutputs()
    {
		for(SystemOutput output: mSystemOutputs)
			closeOutput(output);

		for(MixerOutput output: mMixerOutputs)
			closeOutput(output);
    }

    public void close()
    {
        mSystemIsRunning.set(false);
    }

    @Override
    public void run()
    {
        mSystemIsRunning.set(true);

        double averageTimeToProcessSound = mBufferDuration.getInNanoSeconds();
        double multiplicator             = 0.990;
        
        while(mSystemIsRunning.get())
        {
            long duration = System.nanoTime();

			processOutputs();
           
            duration                  = System.nanoTime() - duration;
            averageTimeToProcessSound = (averageTimeToProcessSound + duration) / 2.0;

            if(averageTimeToProcessSound > mBufferDuration.getInNanoSeconds())
                averageTimeToProcessSound = mBufferDuration.getInNanoSeconds();

            if(mUseDynamicLoadScaling.get())
            {
                long nanos = (long)((mBufferDuration.getInNanoSeconds() - averageTimeToProcessSound) * multiplicator);
                suspendThread(nanos);
            }
        }

        closeAllOutputs();
    }

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

		for(SystemOutput output: sysOutputs)
		{
			int sampleRate        = output.getSampleRate();
			int numBytesPerSample = output.getAudioFormat().getSampleSizeInBits() / 8;
			int numChannels       = output.getNumChannels();
			int numBytesToWrite   = (int)(mBufferDuration.getInSamples(sampleRate) * numChannels * numBytesPerSample);

			output.setNumBytesToWrite(numBytesToWrite);
		}

		int numSamples = mMixSystemOutput.getNumBytesToWrite() / mMixSystemOutput.getNumBytesPerSample();
		mMixBuffer = Field.expand(mMixBuffer, numSamples, false);
		Arrays.fill(mMixBuffer, 0, numSamples, 0.0f);

		for(MixerOutput output: mixOutputs)
			output.mix(mMixBuffer, numSamples);

		mMixSystemOutput.setBuffer(mMixBuffer, numSamples);
		
		while(!sysOutputs.isEmpty())
		{
			boolean buffersAreFull = true;

			for(Iterator<SystemOutput> iOutput = sysOutputs.iterator(); iOutput.hasNext(); )
			{
				SystemOutput output = iOutput.next();

				if(output.isOpen())
				{
					if(!output.receivedData())
						output.request(); // if we got no sound data we request it

					if(output.receivedData())
					{
						if(!output.isConverted())
							output.convert();

						buffersAreFull = buffersAreFull && (output.available() == 0);

						if(output.write(256))
							continue;
					}
				}

				iOutput.remove();
			}

			if(buffersAreFull && !mUseDynamicLoadScaling.get())
				suspendThread(50);
		}
	}

    @Override
    public String toString()
    {
        String text = new String();
        int    ctr  = 1;

        for(Mixer.Info info: AudioSystem.getMixerInfo())
        {
            Mixer         mixer        = AudioSystem.getMixer(info);
            Line.Info[]   lineInfos    = mixer.getSourceLineInfo();
            Control[]     controls     = mixer.getControls();
            AudioFormat   audioFormat  = new AudioFormat(44100, 16, 2, true, false);
            DataLine.Info datalineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);

            text += "[" + ctr + "] " + info.getName() + " - " + info.getDescription() + "\n";

            if(controls.length > 0)
            {
                text += "    controls:\n";

                for(Control c: controls)
                    text += "        " + c.toString() + "\n";
            }
            else text += "    no controls\n";

            if(lineInfos.length > 0)
            {
                text += "    number of lines " + audioFormat.toString() + " = " + mixer.getMaxLines(datalineInfo) + "\n";

                for(Line.Info l: lineInfos)
                    text += "        " + l.toString() + "\n";
            }
            else text += "    no lines\n";

            text += "\n";
            ctr  += 1;
        }

        return text;
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

        if(mixers.length == 0)
            return null;

        for(int i=0; i<mixerInfos.length; ++i)
            mixers[i] = AudioSystem.getMixer(mixerInfos[i]);
        
        Arrays.sort(mixers, new Comparator<Mixer>()
        {
            public int compare(Mixer mixer1, Mixer mixer2)
            {
                int numLines1 = mixer1.getMaxLines(dataLineInfo);
                int numLines2 = mixer2.getMaxLines(dataLineInfo);

                if(numLines1 == AudioSystem.NOT_SPECIFIED || numLines1 > numLines2)
                    return -1;

                return 1;
            }
        });

        if(mixers[0].getMaxLines(dataLineInfo) == 0)
            return null;
        
        return mixers[0];
    }

    private static byte[] convertUniformPCM(byte[] pcmBuffer, float[] uniformSamples, int numUniformSamples, int numBytesPerSample)
    {
        int numBitsPerSample = numBytesPerSample * 8;
		long maxValue        = (long)((Math.pow(2, numBitsPerSample) - 1.0) / 2.0);

		pcmBuffer = Field.expand(pcmBuffer, (numBytesPerSample * numUniformSamples), false);

        for(int i=0; i<numUniformSamples; ++i)
        {
            long value = (long)(uniformSamples[i] * maxValue);
            int  index = i * numBytesPerSample;

            value = (value >  maxValue) ? ( maxValue) : (value);
            value = (value < -maxValue) ? (-maxValue) : (value);

            for(int n=0; n<numBytesPerSample; ++n)
                pcmBuffer[index + n] = (byte)(value >>> (n*8));
        }

        return pcmBuffer;
	}

    private static void mixUniformPCM(float[] result, int rBegin, float[] data, int dBegin, int numSamples)
    {
        for(int i=0; i<numSamples; ++i)
        {
            float A = result[rBegin + i];
            float B = data[dBegin + i];

            result[rBegin + i] = A + B - A * B;
        }
    }

	private static float[] convertChannels(float[] buffer, int numFrames, int numChannels, int numRequiredChannels)
    {
        /* Assignments for audio channels
         * channel 0: Front left
         * channel 1: Front right
         * channel 2: Center
         * channel 3: Low frequency (subwoofer)
         * channel 4: Surround left       - ## NEEDS CONFIRMATION ##
         * channel 5: Surround right      - ## NEEDS CONFIRMATION ##
         * channel 6: Surround back left  - ## NEEDS CONFIRMATION ##
         * channel 7: Surround back right - ## NEEDS CONFIRMATION ##
         */

        if(numChannels == numRequiredChannels)
            return buffer;

        // we have to reduce the number of channels
        if(numChannels > numRequiredChannels)
        {
            // stereo/multichannel to mono - maybe this won't work properly for more than 2 channels
            //                             - ## NEEDS CONFIRMATION ##
            if(numRequiredChannels == 1)
            {
                for(int i=0; i<numFrames; ++i)
                {
                    int index   = i * numChannels;
                    float value = 0.0f;

                    for(int c=0; c<numChannels; ++c)
                        value += buffer[index + c];

                    buffer[i] = value / (float)numChannels;
                }
            }
            else
            {
                // not implemented yet
                buffer = null;
            }
        }
        else // we have to increase the number of channels
        {
            // mono to stereo/multichannel
            if(numChannels == 1)
            {
                float[] newUniformPCM = new float[numFrames * numRequiredChannels];

                for(int i=0; i<numFrames; ++i)
                {
                    int index = i * numRequiredChannels;

                    for(int c=0; c<numRequiredChannels; ++c)
                        newUniformPCM[index + c] = buffer[i];
                }

                buffer = newUniformPCM;
            }
            else // stereo/multichannel to multichannel
            {
                /* Stereo widening (from wikipedia):
                 * Widening of the stereo image can be achieved by manipulating the
                 * relationship of the side signal S and the center signal C
                 * C = (L + R) / 2; S = (L - R) / 2
                 * A positive part of the side signal S is now fed into the left channel
                 * and a part with its phase inverted to the right channel.
                 */

                // not implemented yet
                buffer = null;
            }
        }

        return buffer;
    }

	private static float[] convertSampleRate(float[] buffer, int numSamples, int numChannels, int sampleRate, int toSampleRate)
    {
        if(sampleRate == toSampleRate)
            return buffer;

        // not implemented yet
        return null;
    }
}
