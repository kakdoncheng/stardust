package main;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import engine.GameFlags;
import engine.sfx.Audio;

public class AudioTestLauncher {
	
	public static void main(String[] args){
		GameFlags.init();
		//GameFlags.setFlag("debug-altwavload", 1);
		Audio.initAL();
		
		System.out.println("AL init done");
		
		Audio.loadWavData("explosion-1", 1, 1);
		Audio.loadWavData("explosion-2", 1, 1);
		Audio.loadWavData("explosion-nuke", 1, 0.8f);
		Audio.loadWavData("explosion-emp", 1, 1);
		
		System.out.println("load wav done");
		
		IntBuffer source;
		FloatBuffer sourcePos;
		FloatBuffer sourceVel;
		source=BufferUtils.createIntBuffer(1);
		sourcePos=BufferUtils.createFloatBuffer(3);
		sourceVel=BufferUtils.createFloatBuffer(3);
		
		AL10.alGenSources(source);
		AL10.alSource(source.get(0), AL10.AL_POSITION, (FloatBuffer) sourcePos.position(0*3));
		AL10.alSource(source.get(0), AL10.AL_VELOCITY, (FloatBuffer) sourceVel.position(0*3));
		AL10.alSourcei(source.get(0), AL10.AL_LOOPING, AL10.AL_FALSE);
		
		System.out.println(Audio.$AudioData("explosion-1").$buffer());
		
		// static
		AL10.alSourcei(source.get(0), AL10.AL_BUFFER, Audio.$AudioData("explosion-nuke").$buffer());
		
		// queues
		//AL10.alSourceQueueBuffers(source.get(0), Audio.$AudioData("explosion-nuke").$buffer());
		//System.out.println(AL10.alGetSourcei(source.get(0), AL10.AL_BUFFERS_QUEUED));
		//System.out.println(AL10.alGetSourcei(source.get(0), AL10.AL_BUFFERS_PROCESSED));
		
		//AL10.alSourceQueueBuffers(source.get(0), Audio.$AudioData("explosion-emp").$buffer());
		//System.out.println(AL10.alGetSourcei(source.get(0), AL10.AL_BUFFERS_QUEUED));
		//System.out.println(AL10.alGetSourcei(source.get(0), AL10.AL_BUFFERS_PROCESSED));
		
		//System.out.println(AL10.alGetSourcei(source.get(0), AL10.AL_SOURCE_TYPE));
		
		AL10.alSourcePlay(source);
		System.out.println(AL10.alGetSourcei(source.get(0), AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING);
		
		try{
			while(true){
				//System.out.println(AL10.alGetSourcei(source.get(0), AL10.AL_BUFFERS_PROCESSED));
				//if(AL10.alGetSourcei(source.get(0), AL10.AL_BUFFERS_PROCESSED)==AL10.alGetSourcei(source.get(0), AL10.AL_BUFFERS_QUEUED)){
				//	AL10.alSourceUnqueueBuffers(source.get(0));
				//	AL10.alSourceQueueBuffers(source.get(0), Audio.$AudioData("explosion-nuke").$buffer());
				//	AL10.alSourcePlay(source);
				//}
				if(AL10.alGetSourcei(source.get(0), AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING){
					AL10.alSourcePlay(source.get(0));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//System.out.println(AL10.alGetSourcei(source.get(0), AL10.AL_BUFFERS_PROCESSED));
		Audio.destroyAL();
	}
}
