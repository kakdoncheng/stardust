package engine.sfx;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

public class AudioData {
	
	private float pitch;
	private float gain;
	private String path;
	private IntBuffer buffer;
	
	public float $pitch(){
		return pitch;
	}
	public float $gain(){
		return gain;
	}
	public String $path(){
		return path;
	}
	public int $buffer(){
		return buffer.get(0);
	}
	
	public void destroy() {
		AL10.alDeleteBuffers(buffer);
	}
	
	public AudioData(String path, float pitch, float gain) {//, boolean loadFromDisk){
		
		buffer=BufferUtils.createIntBuffer(1);
		this.pitch=pitch;
		this.gain=gain;
		this.path=path;
		
		// generate buffer
		AL10.alGenBuffers(buffer);
		if(AL10.alGetError() != AL10.AL_NO_ERROR){
			System.err.println("engine.sfx.AudioData: ERROR: Failed to generate buffer. ("+path+")");
			return;// AL10.AL_FALSE;
		}
		
		// using input streams
		ByteArrayOutputStream out = null;
		BufferedInputStream in = null;
		byte[] audioBytes = null;

		try {
			out = new ByteArrayOutputStream();
			in = new BufferedInputStream(new FileInputStream(path));
			int read;
			byte[] buff = new byte[1024];
			while ((read = in.read(buff)) > 0) {
				out.write(buff, 0, read);
			}
			out.flush();
			audioBytes = out.toByteArray();
			in.close();
			out.close();
		} catch(FileNotFoundException e){
			System.err.println("engine.sfx.AudioData: ERROR: Audio file not found. ("+path+")");
			//e.printStackTrace();
		} catch (IOException e) {
			System.err.println("engine.sfx.AudioData: ERROR: IO exception. ("+path+")");
			//e.printStackTrace();
		}
		// wrap wav data
		WaveData waveFile = WaveData.create(audioBytes);

		// using lwjgl wavefile util
		/*
		// attempt to load as file for sanity check
		File f=new File(path);
		if (!f.canRead()){
			System.out.println("engine.sfx.AudioData: ERROR: Cannot read file. ("+path+")");
		}

		WaveData waveFile=null;
		// always load from disk
		waveFile=WaveData.create(path);
		if(loadFromDisk){
			waveFile=WaveData.create(path);
		}else{
			waveFile=WaveData.create(Game.class.getResourceAsStream(path));
		}
		//*/
		
		// debug
		//System.out.println(buffer);
		//System.out.println(waveFile);
		
		AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
	}
}
