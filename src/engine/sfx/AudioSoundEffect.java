package engine.sfx;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

public class AudioSoundEffect {
	
	private AudioData data;
	private IntBuffer source;
	private FloatBuffer sourcePos;
	private FloatBuffer sourceVel;
	
	public void setPitch(float a){
		AL10.alSourcef(source.get(0), AL10.AL_PITCH, data.$pitch()*a);
	}
	
	public boolean isPlaying(){
		return AL10.alGetSourcei(source.get(0), AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}
	
	public void play(){
		AL10.alSourcePlay(source.get(0));
	}
	public void pause(){
		AL10.alSourcePause(source.get(0));
	}
	public void destroy() {
		AL10.alDeleteSources(source);
	}
	
	public AudioSoundEffect(AudioData data, float volume){
		this(data, volume, false);
	}
	
	public AudioSoundEffect(AudioData data, float volume, boolean looping){
		this.data=data;
		source=BufferUtils.createIntBuffer(1);
		sourcePos=BufferUtils.createFloatBuffer(3);
		sourceVel=BufferUtils.createFloatBuffer(3);
		AL10.alGenSources(source);
		if(AL10.alGetError() != AL10.AL_NO_ERROR){
			System.out.println("engine.sfx.SoundEffect: ERROR: Failed to bind buffer to source. ("+data.$path()+")");
			// AL10.AL_FALSE;
		}
		
		AL10.alSourcei(source.get(0), AL10.AL_BUFFER, data.$buffer());
		AL10.alSourcef(source.get(0), AL10.AL_PITCH, data.$pitch());
		AL10.alSourcef(source.get(0), AL10.AL_GAIN, data.$gain()*volume);
		AL10.alSource(source.get(0), AL10.AL_POSITION, (FloatBuffer) sourcePos.position(0*3));
		AL10.alSource(source.get(0), AL10.AL_VELOCITY, (FloatBuffer) sourceVel.position(0*3));
		AL10.alSourcei(source.get(0), AL10.AL_LOOPING, looping?AL10.AL_TRUE:AL10.AL_FALSE);
		
		if(AL10.alGetError() != AL10.AL_NO_ERROR){
			System.out.println("engine.sfx.SoundEffect: ERROR: Failed to create sound effect. ("+data.$path()+")");
			// AL10.AL_FALSE;
		}
	}

}
