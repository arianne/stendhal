/**
 * 
 */
package games.stendhal.tools;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Checks if all sound files can be played on the current system.
 * @author mtotz
 */
public class CheckSounds
{
  private static String getString(String s, int width, char c)
  {
    while (s.length() < width)
    {
      s += c;
    }
    return s;
  }
  
  public static void main(String[] args) throws Exception
  {
    ZipInputStream zipFile = new ZipInputStream(CheckSounds.class.getResourceAsStream("/data/sounds/stensounds0.jar"));

    Map<String, AudioFormat> formatMap = new TreeMap<String, AudioFormat>();
    Map<String, String > fileFormatMap = new TreeMap<String, String>();
    ZipEntry entry = zipFile.getNextEntry();
    do
    {
      byte[] temp = new byte[(int) entry.getSize()];
      zipFile.read(temp);
      
      try
      {
        AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(temp));
        AudioFormat format = ais.getFormat();
        String formatString = format.toString();

        fileFormatMap.put(entry.getName(), formatString);
        if (!formatMap.containsKey(formatString))
        {
          formatMap.put(formatString, format);
        }
      }
      catch (UnsupportedAudioFileException e)
      {
        System.out.println(entry.getName()+" cannot be read, the file format is not supported");
      }
      
      zipFile.closeEntry();
      entry = zipFile.getNextEntry();
    } while (entry != null);
    
    zipFile.close();
    
    Mixer.Info[] mixerList = AudioSystem.getMixerInfo();
    int[] width = new int[mixerList.length];

    System.out.println("\n\n--- Result ---\n");
    System.out.println("installed mixer: ");
    for (int i = 0; i < mixerList.length; i++)
    {
      Mixer.Info mixer = mixerList[i];
      width[i] = Math.max(mixer.getName().length(), "unsupported".length());
      System.out.println(mixer.getName()+" - "+mixer.getDescription());
    }
    System.out.println("Default: "+AudioSystem.getMixer(null).getMixerInfo().getName());
    System.out.println("\n");
    

    System.out.println(formatMap.size()+" audio formats\nThe maximum available lines for the format is in brackets.");
    for (int i = 0; i < mixerList.length; i++)
    {
      System.out.print(getString(mixerList[i].getName(), width[i], ' ')+" | ");
    }
    System.out.println("Format");
    for (int i = 0; i < mixerList.length; i++)
    {
      System.out.print(getString("", width[i], '-')+"-+-");
    }
    System.out.println("---------------------");

    for (String key : formatMap.keySet() )
    {
      DataLine.Info info = new DataLine.Info(Clip.class, formatMap.get(key));
      for (int i = 0; i < mixerList.length; i++)
      {
        Mixer mixer = AudioSystem.getMixer(mixerList[i]);
        System.out.print(getString((mixer.isLineSupported(info) ? "  " : "un")+"supported ("+mixer.getMaxLines(info)+")",width[i], ' ')+" | ");
      }
      
      System.out.print(key);
      System.out.println();
    }
    System.out.println("done");
  }


}
