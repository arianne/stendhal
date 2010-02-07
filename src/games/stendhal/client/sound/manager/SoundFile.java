/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package games.stendhal.client.sound.manager;

import games.stendhal.client.sound.system.processors.Recorder;
import games.stendhal.client.sound.system.processors.OggVorbisDecoder;
import games.stendhal.client.sound.system.processors.PCMStreamConverter;
import games.stendhal.client.sound.system.SignalProcessor;

import games.stendhal.common.resource.Resource;
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

	private int                   mNumChannels;
    private int                   mSampleRate;
	private SignalProcessor       mGenerator = null;
	private final Resource        mResource;
	private final boolean         mEnableStreaming;
    private final int             mOutputNumSamples;
    private final Type            mFileType;
	private final SignalProcessor mPropagator = new SignalProcessor()
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

	@SuppressWarnings("empty-statement")
    public SoundFile(Resource resource, Type fileType, int outputNumSamplesPerChannel, boolean enableStreaming) throws IOException
    {
		assert resource != null;
		assert fileType != null;
		
		mResource         = resource;
        mFileType         = fileType;
        mEnableStreaming  = enableStreaming;
        mOutputNumSamples = outputNumSamplesPerChannel;

        SignalProcessor decoder = chooseDecoder(resource, fileType, outputNumSamplesPerChannel);

        if(decoder == null || !resource.exists())
            throw new IOException("could not load audio resource: " + resource.getURI());

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
    public SoundFile clone() throws CloneNotSupportedException
    {
        SoundFile file = null;
        
        try
        {
            file = new SoundFile(mResource, mFileType, mOutputNumSamples, mEnableStreaming);
        }
        catch(IOException exception)
        {
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
                decoder.open(mResource.getInputStream(), 256, mOutputNumSamples);
            }
            catch(IOException exception) { }
            
        }
        else if(mGenerator.getClass() == PCMStreamConverter.class)
        {
            PCMStreamConverter converter = (PCMStreamConverter)mGenerator;
            converter.close();

            try
            {
                AudioInputStream stream = AudioSystem.getAudioInputStream(mResource.getInputStream());
                converter.open(stream, stream.getFormat(), mOutputNumSamples);
            }
            catch(IOException                   exception) { }
            catch(UnsupportedAudioFileException exception) { }
        }
    }

	private SignalProcessor chooseDecoder(Resource resource, Type fileType, int outputNumSamplesPerChannel)
    {
        SignalProcessor decoder = null;

        switch(fileType)
        {
        case OGG:
            try
            {
                InputStream stream = resource.getInputStream();

				if(stream != null)
				{
					OggVorbisDecoder oggdec = new OggVorbisDecoder(stream, 256, outputNumSamplesPerChannel);

					mNumChannels = oggdec.getNumChannels();
					mSampleRate  = oggdec.getSampleRate();
					decoder      = oggdec;
				}
            }
            catch(Exception exception) { decoder = null; }
            break;

        case WAV:
            try
            {
                AudioInputStream stream = AudioSystem.getAudioInputStream(resource.getInputStream());
                decoder                 = new PCMStreamConverter(stream, stream.getFormat(), outputNumSamplesPerChannel);

                mNumChannels = stream.getFormat().getChannels();
                mSampleRate  = (int)stream.getFormat().getSampleRate();
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