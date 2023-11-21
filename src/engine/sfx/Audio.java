package engine.sfx;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

public class Audio {
	
	private static final int MAX_SFX_SOURCES=24;
	private static final float SFX_VOLUME_MIN=0.1f;
	private static final float SFX_VOLUME_MULTIPLIER=0.25f;
	
	private static HashMap<String, AudioData> data;
	private static HashMap<String, Float> sfxq;
	private static AudioSoundEffect[] sfx;
	private static int sfxi;
	
	private static BackgroundMusicThread bgmt;
	
	// background music
	// have separate thread for sfx?
	// need methods to clear queue, kill buffers, kill buffers with fade-out
	private class BackgroundMusicThread extends Thread{
		
		private boolean running;
		private int bgmi;
		private AudioSoundEffect[] bgm;
		private ArrayList<String> bgmq;
		
		public void detach(){
			running=false;
		}
		
		private void init(){
			bgmi=0;
			bgm=new AudioSoundEffect[2];
			bgmq=new ArrayList<String>();
			running=true;
		}
		private void update(){
			// assume two-slot buffer for current bgm
			// assume fifo queue for queued bgm
			int nexti=(bgmi+1)%2;
			
			// check queue first,
			// if queue.size > 0, check buffers
			if(bgmq.size()>0) {
				// if current index null, pop queue & play current index
				if(bgm[bgmi]==null) {
					String bgmk=popQueue();
					bgm[bgmi]=new AudioSoundEffect(data.get(bgmk), 1);
					bgm[bgmi].play();
				}
				// if opposite index null, pop queue
				if(bgm[nexti]==null) {
					String bgmk=popQueue();
					bgm[bgmi]=new AudioSoundEffect(data.get(bgmk), 1);
				}
			}
			
			// if current bgm index not playing, check opposite index
			if(bgm[bgmi]!=null && !bgm[bgmi].isPlaying()) {
				// if opposite index not null, destroy current index, swap index
				if(bgm[nexti]!=null) {
					bgm[bgmi].destroy();
					bgm[bgmi]=null;
					bgmi=nexti;
				}
				// then play current index
				bgm[bgmi].play();
			}
			
			//System.out.println(bgm[bgmi]+" "+bgm[nexti]);
		}
		
		// handle queue
		public synchronized void addToQueue(String key){
			bgmq.add(key);
		}
		private synchronized String popQueue() {
			return bgmq.remove(0);
		}
		
		public void run(){
			//System.out.println("engine.sfx.Audio$BackgroundMusicThread: INFO: Starting.");
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
			for(int i=0; i<bgm.length; i++) {
				if(bgm[i]!=null) {
					bgm[i].destroy();
				}
			}
			//System.out.println("engine.sfx.Audio$BackgroundMusicThread: INFO: Stopped.");
		}
	}
	
	// sound effects
	public static int $sfxCount(){
		int c=0;
		for(AudioSoundEffect a:sfx){
			if(a!=null && a.isPlaying()){
				c++;
			}
		}
		return c;
	}
	public static void setSFXPitch(double a){
		for(int i=0;i<sfx.length;i++){
			if(sfx[i]!=null){
				sfx[i].setPitch((float)a);
			}
		}
	}
	public static void addSoundEffect(String key, float volume){
		if(data.get(key)==null || volume<SFX_VOLUME_MIN){
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
		sfx[sfxi]=new AudioSoundEffect(data.get(key), volume*SFX_VOLUME_MULTIPLIER);
		sfx[sfxi].play();
		sfxi+=1;
		sfxi%=sfx.length;
	}
	
	
	// background music
	public static void queueBackgroundMusic(String key){
		if(data.get(key)==null){
			return;
		}
		bgmt.addToQueue(key);
	}
	
	// loading wav data
	public static AudioData $AudioData(String key){
		return data.getOrDefault(key, null);
	}
	public static void loadWavData(String name, float pitch, float gain){
		AudioData dat=null;
		try{
			// always load from disk
			dat=new AudioData("./amb/"+name+".wav", pitch, gain, true);
			/*
			if(GameFlags.is("debug-altwavload")){
				dat=new AudioData("./amb/"+name+".wav", pitch, gain, true);
			}else{
				dat=new AudioData("/amb/"+name+".wav", pitch, gain, false);
			}
			//*/
		}catch(Exception e){
			System.err.println("engine.sfx.Audio: ERROR: failed to load wav data ("+name+")");
			//e.printStackTrace();
			return;
		}
		data.put(name, dat);
	}
	
	
	// create/destroy AL functions
	public static void initAL(){
		// init openal
		try {
			AL.create();
		} catch (LWJGLException le) {
			le.printStackTrace();
			return;
		}
		// clear error bit
		AL10.alGetError();
		
		// init sfx
		sfx=new AudioSoundEffect[MAX_SFX_SOURCES];
		sfxi=0;
		data=new HashMap<String, AudioData>();
		sfxq=new HashMap<String, Float>();
		
		// init bgm thread
		bgmt=new Audio().new BackgroundMusicThread();
		bgmt.start();
		
		// set listener
		setDefaultListener();
	}
	public static void destroyAL(){
		// attempt to destroy background music thread
		if(bgmt!=null) {
			bgmt.detach();
		}
		
		for(int i=0;i<sfx.length;i++){
			if(sfx[i]!=null){
				sfx[i].destroy();
			}
		}
		for(HashMap.Entry<String, AudioData> pair: data.entrySet()){
			pair.getValue().destroy();
		}
		
		// finish destroying thread before destroying AL context
		while(bgmt.isAlive()) {
			continue;
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
