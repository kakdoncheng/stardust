package engine.sfx;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

import engine.GameFlags;

public class Audio {
	
	private static final int MAX_SFX_SOURCES=24;
	private static final float MIN_VOLUME=0.1f;
	private static float SFX_VOLUME=0.25f;
	
	private static HashMap<String, AudioData> data;
	private static HashMap<String, Float> sfxq;
	private static AudioSoundEffect[] sfx;
	private static int sfxi;
	
	//private static Thread bgm;
	
	@SuppressWarnings("unused")
	private class BackgroundMusicThread extends Thread{
		private ArrayList<AudioSoundEffect> bgmq;
		private boolean running;
		public void detach(){
			running=false;
		}
		
		public synchronized void init(){
			bgmq=new ArrayList<AudioSoundEffect>();
			running=true;
		}
		public synchronized void update(){
			if(bgmq!=null && bgmq.size()>0){
				if(!bgmq.get(0).isPlaying() && !bgmq.get(0).repeat()){
					if(bgmq.size()>1){
						bgmq.remove(0);
						bgmq.get(0).play();
					}else{
						bgmq.get(0).play();
					}
				}
			}
		}
		public synchronized void addToQueue(String key, int repeat){
			if(bgmq!=null){
				bgmq.add(new AudioSoundEffect(data.get(key), 1, repeat));
			}
		}
		
		public void run(){
			init();
			try{
				while(running){
					update();
					Thread.sleep(10);
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			
			// destroy remaining sources
			for(AudioSoundEffect ase:bgmq){
				//ase.pause();
				ase.destroy();
			}
		}
	}
	
	private static void playSoundEffect(String key, float volume){
		
		if(sfx[sfxi]!=null){
			// find next unused index
			// do not play sfx if full
			/*
			if($sfxCount()<MAX_SFX_SOURCES){
				while(sfx[sfxi].isPlaying()){
					sfxi+=1;
					sfxi%=sfx.length;
				}
			}else{
				return;
			}
			//*/
			sfx[sfxi].destroy();
		}
		
		sfx[sfxi]=new AudioSoundEffect(data.get(key), volume*SFX_VOLUME);
		sfx[sfxi].play();
		sfxi+=1;
		sfxi%=sfx.length;
	}
	
	public static AudioData $AudioData(String key){
		return data.getOrDefault(key, null);
	}
	public static void addSoundEffect(String key, float volume){
		if(data.get(key)==null || volume<MIN_VOLUME){
			return;
		}
		float sfxv=sfxq.getOrDefault(key, 0f);
		if(sfxv<volume){
			sfxq.put(key, volume);
		}
	}
	public static void resolveSoundEffects(){
		for(HashMap.Entry<String, Float> pair: sfxq.entrySet()){
			playSoundEffect(pair.getKey(), pair.getValue());
		}
		sfxq.clear();
	}
	public static int $sfxCount(){
		int c=0;
		for(AudioSoundEffect a:sfx){
			if(a!=null && a.isPlaying()){
				c++;
			}
		}
		return c;
	}
	
	public static void setPitch(double a){
		for(int i=0;i<sfx.length;i++){
			if(sfx[i]!=null){
				sfx[i].setPitch((float)a);
			}
		}
	}
	
	public static void initAL(){
		// init openal and clear error bit
		try {
			AL.create();
		} catch (LWJGLException le) {
			le.printStackTrace();
			return;
		}
		AL10.alGetError();
		
		// init sfx
		sfx=new AudioSoundEffect[MAX_SFX_SOURCES];
		sfxi=0;
		data=new HashMap<String, AudioData>();
		sfxq=new HashMap<String, Float>();
		setDefaultListener();
	}
	
	public static void loadWavData(String name, float pitch, float gain){
		AudioData dat=null;
		try{
			if(GameFlags.is("debug-altwavload")){
				dat=new AudioData("./amb/"+name+".wav", pitch, gain, true);
			}else{
				dat=new AudioData("/amb/"+name+".wav", pitch, gain, false);
			}
		}catch(Exception e){
			System.err.println("engine.sfx.Audio: ERROR: failed to load wav data ("+name+")");
			//e.printStackTrace();
			return;
		}
		data.put(name, dat);
	}
	
	public static void destroyAL(){
		for(int i=0;i<sfx.length;i++){
			if(sfx[i]!=null){
				sfx[i].destroy();
			}
		}
		for(HashMap.Entry<String, AudioData> pair: data.entrySet()){
			pair.getValue().destroy();
		}
		AL.destroy();
	}
	
	private static void setDefaultListener(){
		// set listener
		FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
		FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
		FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f });
		listenerPos.flip();
		listenerVel.flip();
		listenerOri.flip();
	    AL10.alListener(AL10.AL_POSITION,    listenerPos);
	    AL10.alListener(AL10.AL_VELOCITY,    listenerVel);
	    AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
	}
}
