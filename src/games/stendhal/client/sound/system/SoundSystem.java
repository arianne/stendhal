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

import org.apache.log4j.Logger;

/**
 *
 * @author silvio
 */
public class SoundSystem extends Thread
{
    public final static class Output extends SignalProcessor
    {
        private SourceDataLine mLine            = null; // the line we will write the PCM data to
        private AudioFormat    mAudioFormat     = null; // the audio format the line was opened with
        private float[]        mUniformPCM      = null; // the interleaved uniform PCM data. every samples value ranges form -1.0 to 1.0
        private byte[]         mPCM             = null; // the uniform PCM data converted to the format defined in "mAudioFormat"
        private int            mNumBytesWritten = 0;    // number of bytes of "mPCM" we have written to "mLine"
        private int            mNumSamplesMixed = 0;    // number of samples we have mixed into the line of "mMixedOutput"
        private int            mNumChannels     = 0;
        private int            mNumSamples      = 0;
        private int            mSampleRate      = 0;
		private boolean        mPCMWasConverted = false;

        private int mRemainingBytesToWritePerCycle = 0;

        private int     getRemainingBytesToWrite() { return mPCM.length - mNumBytesWritten;  }
        private int     getRemainingSamplesToMix() { return mNumSamples - mNumSamplesMixed;  }
        private boolean receivedData            () { return mUniformPCM != null;             }
        private boolean wasPCMConverted         () { return mPCMWasConverted;                }
        private boolean isLineUsable            () { return mLine       != null;             }
        private boolean wasAllDataWritten       () { return mNumBytesWritten >= mPCM.length; }
        private boolean whereAllSamplesMixed    () { return mNumSamplesMixed >= mNumSamples; }

        private int writeToLine(int numBytes)
        {
			assert mAudioFormat.getChannels()        == mNumChannels;
			assert (int)mAudioFormat.getSampleRate() == mSampleRate;

			int written = mLine.write(mPCM, mNumBytesWritten, numBytes);
			mNumBytesWritten += written;
			return written;
        }

        private int mixToOutput(Output output, int offsetInSamples, int numSamplesToMix)
        {
            assert output.mAudioFormat.getChannels()        == mNumChannels;
            assert (int)output.mAudioFormat.getSampleRate() == mSampleRate;

            SoundSystem.mixUniformPCM(output.mUniformPCM, offsetInSamples*mNumChannels, mUniformPCM, mNumSamplesMixed*mNumChannels, numSamplesToMix*mNumChannels);
            mNumSamplesMixed += numSamplesToMix;
            return numSamplesToMix;
        }

        private void convertPCM()
        {
            assert (mAudioFormat.getSampleSizeInBits() % 8) == 0;

            int numBytesPerSample = mAudioFormat.getSampleSizeInBits() / 8;
            mPCM             = SoundSystem.convertUniformPCM(mPCM, mUniformPCM, mNumSamples * mNumChannels, numBytesPerSample);
			mPCMWasConverted = true;
        }

        private void clearData()
        {
            mNumBytesWritten = 0;
            mNumSamplesMixed = 0;
            mUniformPCM      = null;
			mPCMWasConverted = false;
        }

        @Override
        protected void modify(float[] buffer, int samples, int channels, int rate)
        {
            if(buffer != null && samples > 0 && channels > 0 && rate > 0)
            {
                assert samples <= buffer.length;

                mUniformPCM  = buffer;
                mNumChannels = channels;
                mNumSamples  = samples;
                mSampleRate  = rate;

                if(!SoundSystem.convertChannels(this))
                {
                    assert false: "could not convert channels";

                    mUniformPCM  = null;
                    mNumChannels = 0;
                    mNumSamples  = 0;
                    mSampleRate  = 0;
                }

                if(!SoundSystem.convertSampleRate(this))
                {
                    assert false: "could not convert sample rate";

                    mUniformPCM  = null;
                    mNumChannels = 0;
                    mNumSamples  = 0;
                    mSampleRate  = 0;
                }
            }
        }
    }

	private final static Logger      logger                 = Logger.getLogger(SoundSystem.class);
    private final LinkedList<Output> mOutputs               = new LinkedList<Output>();
    private final Output             mMixOutput             = new Output();
    private Mixer                    mMixer                 = null;
    private Time                     mBufferDuration        = null;
    private final AtomicBoolean      mSystemIsRunning       = new AtomicBoolean(false);
    private final AtomicBoolean      mUseDynamicLoadScaling = new AtomicBoolean(false);
	float[]                          mMixBuffer             = null;
	byte[]                           mPCMBuffer             = null;
	private final int                mMaxNumLines;
    
    public SoundSystem(AudioFormat audioFormat, Time bufferDuration, int useMaxMixerLines) throws SoundSystemException
    {
        assert audioFormat    != null;
        assert bufferDuration != null;

        mMixer          = tryToFindMixer(audioFormat);
        mBufferDuration = bufferDuration;
		mMaxNumLines    = useMaxMixerLines;

        if(mMixer == null)
        {
            DataLine.Info datalineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);

            try
            {
                mMixOutput.mLine = (SourceDataLine)AudioSystem.getLine(datalineInfo);

                if(!AudioSystem.isLineSupported(datalineInfo))
                    throw new SoundSystemException("line is not supported - " + audioFormat.toString());

				mMixOutput.mAudioFormat = audioFormat;
                mMixOutput.mLine.open();
            }
            catch(LineUnavailableException exception)
            {
                mMixOutput.mLine.close();
                throw new SoundSystemException("line is unavailable - " + exception.toString());
            }
        }
		else
		{
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);

			try
            {
                mMixOutput.mLine        = (SourceDataLine)mMixer.getLine(info);
                mMixOutput.mAudioFormat = audioFormat;
                mMixOutput.mLine.open();
            }
            catch(LineUnavailableException e)
            {
                mMixOutput.mLine.close();
                mMixOutput.mLine        = null;
                mMixOutput.mAudioFormat = null;
            }
		}
    }

    public SoundSystem(Mixer mixer, AudioFormat audioFormat, Time bufferDuration, int useMaxMixerLines)
    {
		assert audioFormat    != null;
        assert mixer          != null;
        assert bufferDuration != null;

        mMixer          = mixer;
        mBufferDuration = bufferDuration;
		mMaxNumLines    = useMaxMixerLines;

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);

		try
		{
			mMixOutput.mLine        = (SourceDataLine)mMixer.getLine(info);
			mMixOutput.mAudioFormat = audioFormat;
			mMixOutput.mLine.open();
		}
		catch(LineUnavailableException e)
		{
			mMixOutput.mLine.close();
			mMixOutput.mLine        = null;
			mMixOutput.mAudioFormat = null;
		}
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

		mMaxNumLines            = 0;
        mMixOutput.mLine        = outputLine;
        mMixOutput.mAudioFormat = outputLine.getFormat();
        mMixOutput.mNumChannels = mMixOutput.mAudioFormat.getChannels();
        mMixOutput.mSampleRate  = (int)mMixOutput.mAudioFormat.getSampleRate();
        mBufferDuration         = bufferDuration;
    }

    public Output openOutput(AudioFormat audioFormat)
	{
        Output        output          = new Output();
        DataLine.Info info            = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
        boolean       lineUnavailable = false;
        
        if(mMixer != null && mMixer.isLineSupported(info) && mOutputs.size() < mMaxNumLines)
        {
            try
            {
                output.mLine        = (SourceDataLine)mMixer.getLine(info);
                output.mAudioFormat = audioFormat;
                output.mLine.open();
            }
            catch(LineUnavailableException e)
            {
                output.mLine.close();
                output.mLine        = null;
                output.mAudioFormat = null;
                lineUnavailable     = true;
            }
        }
        else
        {
            lineUnavailable = true;
        }

        if(lineUnavailable && mMixOutput.isLineUsable())
            output.mAudioFormat = mMixOutput.mAudioFormat;

        synchronized(this)
        {
            mOutputs.add(output);
        }
        
        return output;
    }

    public synchronized void closeOutput(Output output)
    {
        if(output.isLineUsable())
        {
            output.mLine.close();
            output.mLine = null;
        }

        output.clearData();
        output.disconnect();

        mOutputs.remove(output);
    }

    public synchronized void closeAllOutputs()
    {
        for(Output output: mOutputs)
        {
            if(output.isLineUsable())
            {
                output.mLine.close();
                output.mLine = null;
            }

            output.clearData();
            output.disconnect();
        }

        mOutputs.clear();

        if(mMixOutput.isLineUsable())
        {
            mMixOutput.mLine.stop();
            mMixOutput.mLine.close();
            mMixOutput.mLine = null;
            mMixOutput.clearData();
        }
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
        double multiplicator             = 0.995;
        
        while(mSystemIsRunning.get())
        {
            long duration = System.nanoTime();

            try
			{
                processOutputs();
            }
			catch(RuntimeException e)
			{
                logger.warn(e, e);
            }

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

    private int min(int a, int b, int c)
    {
        return Math.min(a, Math.min(b,c));
    }

    private void prepareManualMixing()
    {
        int numBytesPerSample = mMixOutput.mAudioFormat.getSampleSizeInBits() / 8;
        int numChannels       = mMixOutput.mAudioFormat.getChannels();
        int sampleRate        = (int)mMixOutput.mAudioFormat.getSampleRate();
        int numSamples        = (int)mBufferDuration.getInSamples(sampleRate);

		mMixBuffer                                = Field.expand(mMixBuffer, (numSamples * numChannels), false);
		mMixOutput.mUniformPCM                    = mMixBuffer;
        mMixOutput.mNumSamples                    = numSamples;
        mMixOutput.mRemainingBytesToWritePerCycle = numSamples * numChannels * numBytesPerSample;
        Arrays.fill(mMixOutput.mUniformPCM, 0, numSamples, 0.0f);
    }
    
    private void processOutputs()
    {
        LinkedList<Output> outputsWithUsableLines = new LinkedList<Output>();

        if(mMixOutput.isLineUsable())
        {
            prepareManualMixing();
            outputsWithUsableLines.add(mMixOutput);
        }

        synchronized(this)
        {
            for(Output output: mOutputs)
            {
				/* happens for example if the audio device is in use by an other applicatin
                if(output.mAudioFormat == null)
                	continue; //*/

                int numBytesPerSample = output.mAudioFormat.getSampleSizeInBits() / 8;
                int numChannels       = output.mAudioFormat.getChannels();
                int sampleRate        = (int)output.mAudioFormat.getSampleRate();

                if(output.isLineUsable())
                {
                    output.mRemainingBytesToWritePerCycle =
                            (int)mBufferDuration.getInSamples(sampleRate) * numChannels * numBytesPerSample;

                    outputsWithUsableLines.add(output);
                }
                else if(mMixOutput.isLineUsable())
                {
                    int remainingSamplesToMix = mMixOutput.mNumSamples;
                    int numSmplesMixed        = 0;

                    while(remainingSamplesToMix > 0)
                    {
                        if(!output.receivedData())
                            output.request(); // if we got no sound data we request it

                        if(output.receivedData())
                        {
                            int numSamplesToMix = Math.min(output.getRemainingSamplesToMix(), remainingSamplesToMix);
                            int count           = output.mixToOutput(mMixOutput, numSmplesMixed, numSamplesToMix);

                            remainingSamplesToMix -= count;
                            numSmplesMixed        += count;

                            if(output.whereAllSamplesMixed())
                                output.clearData();
                        }
                        else break;
                    }
                }
            }
        }

        while(!outputsWithUsableLines.isEmpty())
        {
            boolean lineBuffersAreFull = true;

            for(Iterator<Output> iOutput = outputsWithUsableLines.iterator(); iOutput.hasNext(); )
            {
                Output output = iOutput.next();

                if(!output.receivedData())
                    output.request(); // if we got no sound data we request it

                if(output.receivedData())
                {
                    if(!output.mLine.isRunning())
                        output.mLine.start();

                    if(!output.wasPCMConverted())
                        output.convertPCM();

                    assert output.getRemainingBytesToWrite()     != 0;
                    assert output.mRemainingBytesToWritePerCycle != 0;

                    lineBuffersAreFull = lineBuffersAreFull && (output.mLine.available() == 0);
                    
                    int numBytesWritable =
                            min(output.mLine.available(), output.getRemainingBytesToWrite(), output.mRemainingBytesToWritePerCycle);

					output.mRemainingBytesToWritePerCycle -= output.writeToLine(numBytesWritable);

					if(output.wasAllDataWritten())
						output.clearData();

					if(output.mRemainingBytesToWritePerCycle <= 0)
						iOutput.remove();
                }
                else
                {
                    // if after we requested data we still don't have any, we quit processing of this output
                    iOutput.remove();
                    break;
                }
            }

            if(lineBuffersAreFull)
            {
                if(!mUseDynamicLoadScaling.get())
                    suspendThread(50);
            }
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

    private static boolean convertChannels(Output output)
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

        if(!output.receivedData() || output.mNumChannels == output.mAudioFormat.getChannels())
            return true;

        int numRequiredChannels = output.mAudioFormat.getChannels();

        // we have to reduce the number of channels
        if(output.mNumChannels > numRequiredChannels)
        {
            // stereo/multichannel to mono - maybe this won't work properly for more than 2 channels
            //                             - ## NEEDS CONFIRMATION ##
            if(numRequiredChannels == 1)
            {
                for(int i=0; i<output.mNumSamples; ++i)
                {
                    int index   = i * output.mNumChannels;
                    float value = 0.0f;

                    for(int c=0; c<output.mNumChannels; ++c)
                        value += output.mUniformPCM[index + c];

                    output.mUniformPCM[i] = value / (float)output.mNumChannels;
                }
            }
            else
            {
                // not implemented yet
                return false;
            }
        }
        else // we have to increase the number of channels
        {
            // mono to stereo/multichannel
            if(output.mNumChannels == 1)
            {
                float[] newUniformPCM = new float[output.mNumSamples * numRequiredChannels];

                for(int i=0; i<output.mNumSamples; ++i)
                {
                    int index = i * numRequiredChannels;

                    for(int c=0; c<numRequiredChannels; ++c)
                        newUniformPCM[index + c] = output.mUniformPCM[i];
                }

                output.mUniformPCM = newUniformPCM;
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
                return false;
            }
        }

        output.mNumChannels = numRequiredChannels;
        return true;
    }

    private static boolean convertSampleRate(Output output)
    {
        if(!output.receivedData() || output.mSampleRate == (int)output.mAudioFormat.getSampleRate())
            return true;

        // not implemented yet
        return false;
    }
}
