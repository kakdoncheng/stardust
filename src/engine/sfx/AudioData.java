package engine.sfx;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

import engine.Game;

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
	
	public AudioData(String path, float pitch, float gain, boolean loadFromDisk){
		
		buffer=BufferUtils.createIntBuffer(1);
		this.pitch=pitch;
		this.gain=gain;
		this.path=path;
		
		// load wav data to buffer
		AL10.alGenBuffers(buffer);
		if(AL10.alGetError() != AL10.AL_NO_ERROR){
			System.out.println("engine.sfx.AudioData: ERROR: Failed to load wav data. ("+path+")");
			return;// AL10.AL_FALSE;
		}
		
		WaveData waveFile=null;
		if(loadFromDisk){
			waveFile=WaveData.create(path);
		}else{
			waveFile=WaveData.create(Game.class.getResourceAsStream(path));
		}
		AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
	}
}
