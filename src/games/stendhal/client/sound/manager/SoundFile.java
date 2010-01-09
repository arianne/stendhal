/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.manager;

import games.stendhal.client.sound.system.SignalProcessor;
import games.stendhal.client.sound.system.processors.OggVorbisDecoder;
import games.stendhal.client.sound.system.processors.PCMStreamConverter;
import games.stendhal.client.sound.system.processors.Recorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author silvio
 */
public class SoundFile extends SignalProcessor implements Cloneable
{
    public static enum Type { OGG, WAV }

    private SignalProcessor mPropagator;
    private SignalProcessor mGenerator = null;
    private String          mFilePath  = null;
    private Type            mFileType  = null;
    private boolean         mEnableStreaming;
    private int             mOutputNumSamples;
    private int             mNumChannels;
    private int             mSampleRate;
    

    @SuppressWarnings("empty-statement")
    public SoundFile(String filePath, Type fileType, int outputNumSamplesPerChannel, boolean enableStreaming) throws IOException
    {
        mFilePath         = filePath;
        mFileType         = fileType;
        mEnableStreaming  = enableStreaming;
        mOutputNumSamples = outputNumSamplesPerChannel;
        mPropagator       = new SignalProcessor()
        {
            @Override
            protected void modify(float[] data, int samples, int channels, int rate)
            {
                SoundFile.this.propagate(data, samples, channels, rate);
            }

            @Override
            protected void finished()
            {
                SoundFile.this.quit();
            }
        };

        SignalProcessor decoder = chooseDecoder(filePath, fileType, outputNumSamplesPerChannel);

        if(decoder == null)
            throw new IOException("could not load audio file: " + filePath);

        if(enableStreaming)
        {
            decoder.connectTo(mPropagator, true);
            mGenerator = decoder;
        }
        else
        {
            Recorder recorder = new Recorder(outputNumSamplesPerChannel);
            recorder.connectTo(decoder, false);

            while(recorder.request())
                /* pass */;

            recorder.disconnect();
            recorder.trim();
            recorder.connectTo(mPropagator, true);
            mGenerator = recorder;
        }
    }

    public int getNumChannels() { return mNumChannels; }
    public int getSampleRate () { return mSampleRate;  }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        SoundFile file;
        
        try
        {
            file = new SoundFile(mFilePath, mFileType, mOutputNumSamples, mEnableStreaming);
        }
        catch(IOException exception)
        {
            file = null;
            assert false: "catched exception - " + exception;
        }

        return file;
    }

    public void restart()
    {
        if(mGenerator.getClass() == Recorder.class)
        {
            Recorder recorder = (Recorder)mGenerator;
            recorder.restart();
        }
        else if(mGenerator.getClass() == OggVorbisDecoder.class)
        {
            OggVorbisDecoder decoder = (OggVorbisDecoder)mGenerator;
            decoder.close();

            try
            {
                decoder.open(new FileInputStream(mFilePath), 256, mOutputNumSamples);
            }
            catch(IOException exception) { }
            
        }
        else if(mGenerator.getClass() == PCMStreamConverter.class)
        {
            PCMStreamConverter converter = (PCMStreamConverter)mGenerator;
            converter.close();

            try
            {
                AudioInputStream stream = AudioSystem.getAudioInputStream(new File(mFilePath));
                converter.open(stream, stream.getFormat(), mOutputNumSamples);
            }
            catch(IOException                   exception) { }
            catch(UnsupportedAudioFileException exception) { }
        }
    }

    private SignalProcessor chooseDecoder(String filePath, Type fileType, int outputNumSamplesPerChannel)
    {
        SignalProcessor decoder = null;
        InputStream  stream = this.getClass().getResourceAsStream("/" + filePath);

        switch(fileType)
        {
        case OGG:
            try
            {
                OggVorbisDecoder oggdec = new OggVorbisDecoder(stream, 256, outputNumSamplesPerChannel);

                mNumChannels = oggdec.getNumChannels();
                mSampleRate  = oggdec.getSampleRate();
                decoder      = oggdec;
            }
            catch(Exception exception) { decoder = null; }
            break;

        case WAV:
            try
            {
                AudioInputStream auidoStream = AudioSystem.getAudioInputStream(stream);
                decoder                 = new PCMStreamConverter(auidoStream, auidoStream.getFormat(), outputNumSamplesPerChannel);

                mNumChannels = auidoStream.getFormat().getChannels();
                mSampleRate  = (int)auidoStream.getFormat().getSampleRate();
            }
            catch(Exception exception) { decoder = null; }
            break;
        }

        return decoder;
    }

    @Override
    protected boolean generate()
    {
        return mPropagator.request();
    }
}