package org.torpidity.tank.base;
import javax.sound.sampled.*;
import javazoom.jl.player.*;
import javazoom.jl.decoder.*;
import java.io.*;

public class SoundHandler
{
	private static Player p;
	private static SourceDataLine line;
	
	public static void playSound(String filename)
	{	String extension = filename.substring(filename.lastIndexOf(".")+1);

		try
		{	BufferedInputStream bs = new BufferedInputStream(new FileInputStream(filename));
			if (extension.equals("mp3"))
			{	p = new Player(bs);
				p.play();
			}
			else if (extension.equals("wav"))
			{	AudioInputStream in = AudioSystem.getAudioInputStream(bs);
				AudioFormat format = in.getFormat();
				DataLine.Info info = new DataLine.Info(SourceDataLine.class,format);
				line = (SourceDataLine)AudioSystem.getLine(info);
				
				byte[] data = new byte[4096];
				int length = 0;
				line.open();
				line.start();
				
				while((length = in.read(data, 0, data.length)) > 0)
				{
				    line.write(data, 0, length);
				}
			}
		}
		catch(UnsupportedAudioFileException e) { System.out.println(e); return; }
		catch(LineUnavailableException e) { System.out.println(e); return; }
		catch(JavaLayerException e) { System.out.println(e); return; }
		catch(IOException e) { System.out.println(e); return; }
	}
	
	public static void stop()
	{	if (p != null) p.close();
		if (line != null) line.stop();
	}
}