package stardust;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import stardust.entities.MouseProxy;
import stardust.entities.Stardust;
import stardust.entities.StardustEntity;
import stardust.gfx.CharGraphics;
import stardust.gfx.VectorGraphics;
import stardust.states.AsteroidsState;
import stardust.states.CeraphimState;
import stardust.states.DemonstarBossState;
import stardust.states.DemonstarState;
import stardust.states.EndlessState;
import stardust.states.GradiusBossState;
import stardust.states.GradiusState;
import stardust.states.GyrusState;
import stardust.states.LunarState;
import stardust.states.MissileCommandState;
import stardust.states.PortalScreen;
import stardust.states.SinistarState;
import stardust.states.SpaceInvadersState;
import stardust.states.StardustState;
import stardust.states.TerraState;
import stardust.unused.HeavyWeaponState;
import stardust.unused.LoopingSpaceInvadersState;
import stardust.unused.PongState;
import engine.Game;
import engine.GameFlags;
import engine.State;
import engine.Vector;
import engine.entities.Entity;
import engine.gfx.Camera;
import engine.gfx.TextureLoader;
import engine.input.MouseHandler;
import engine.sfx.Audio;

public class StardustGame extends Game{
	
	public static final String credits="Game Design, Programming, & Art: Linh-Han Van 01.01.2022";
	public static final String version="v0.2.0+20221124";
	
	public StardustGame() {
		super(0, 0, "Stardust");
		init();
		loop();
	}
	
	// mouse proxy object
	private MouseProxy mp;
	public StardustEntity $mouseProxy(){
		return mp;
	}

	public void init(){

		GameFlags.init();
		GameFlags.addFlag("debug");
		GameFlags.addFlag("debuginv");
		GameFlags.addFlag("debugfps");
		GameFlags.addFlag("debugshow");
		GameFlags.addFlag("score");
		
		GameFlags.addFlag("asteroids");
		GameFlags.addFlag("lunar");
		GameFlags.addFlag("invaders");
		GameFlags.addFlag("demonstar");
		GameFlags.addFlag("gradius");
		GameFlags.addFlag("gyruss");
		GameFlags.addFlag("warhead");
		GameFlags.addFlag("terra");
		GameFlags.addFlag("begin");
		
		GameFlags.setFlag("goto-portal", 1);
		GameFlags.setFlag("flash-hiscore", 0);
		
		GameFlags.setFlag("warp", 0);
		GameFlags.setFlag("success", 0);
		GameFlags.setFlag("player-x", 0);
		GameFlags.setFlag("player-y", 0);
		GameFlags.setFlag("player-ft", 0);
		GameFlags.setFlag("player-speed", 0);
		
		GameFlags.markFlag("debug");
		//GameFlags.markFlag("debuginv");
		//GameFlags.markFlag("debugshow");
		//GameFlags.markFlag("debugfps");
		GameFlags.markFlag("score");
		
		// naive bloom
		GameFlags.setFlag("enable-altbloom", 1);
		//GameFlags.setFlag("enable-bloom", 1);
		GameFlags.setFlag("bloom-intensity", 3);
		//GameFlags.setFlag("debug-showhitbox", 1);
		
		// deprecated, will always load audio from disk
		//GameFlags.setFlag("debug-altwavload", 1);
		
		// sfx
		Audio.initAL();
		Audio.loadWavData("fire-nuke", 1, 0.8f);
		Audio.loadWavData("fire-blaster", 1, 0.8f);
		Audio.loadWavData("fire-blasterlow", 1, 0.8f);
		Audio.loadWavData("fire-bogey", 1, 1);
		Audio.loadWavData("fire-energy", 1, 1);
		Audio.loadWavData("fire-gradius", 1, 1);
		Audio.loadWavData("fire-halo", 1, 1);
		Audio.loadWavData("fire-inferno", 1, 1);
		Audio.loadWavData("fire-lascannon", 1, 1);
		Audio.loadWavData("fire-martian", 1, 1);
		Audio.loadWavData("fire-rebound", 1, 1);
		Audio.loadWavData("fire-slug", 1, 1);
		Audio.loadWavData("fire-stardust", 1, 1);
		Audio.loadWavData("fire-starfighter", 1, 1);
		Audio.loadWavData("explosion-1", 1, 1);
		Audio.loadWavData("explosion-2", 1, 1);
		Audio.loadWavData("explosion-nuke", 1, 0.8f);
		Audio.loadWavData("explosion-emp", 1, 1);
		Audio.loadWavData("scream-0", 1, 1);
		Audio.loadWavData("scream-1", 1, 1);
		Audio.loadWavData("air-raid-siren", 1, 1);
		Audio.loadWavData("blip", 1, 1);
		Audio.loadWavData("pongf4", 1, 1);
		Audio.loadWavData("pongf5", 1, 1);
		Audio.loadWavData("invaders-a", 1, 32);
		Audio.loadWavData("invaders-b", 1, 32);
		Audio.loadWavData("invaders-c", 1, 32);
		Audio.loadWavData("invaders-d", 1, 32);
		
		// bgm
		// might need to dynamically load?
		Audio.loadWavData("80s-synth-wave-110473/loop-1", 1, 1);
		Audio.loadWavData("80s-synth-wave-110473/loop-2", 1, 1);
		Audio.loadWavData("80s-synth-wave-110473/loop-3", 1, 1);
		Audio.loadWavData("80s-synth-wave-110473/loop-lo-a", 1, 1);
		Audio.loadWavData("80s-synth-wave-110473/loop-lo-b", 1, 1);
		Audio.loadWavData("80s-synth-wave-110473/loop-lo", 1, 1);
		Audio.loadWavData("arcade-171561/intro-1", 1, 1);
		Audio.loadWavData("arcade-171561/intro-2", 1, 1);
		Audio.loadWavData("arcade-171561/loop", 1, 1);
		Audio.loadWavData("background-trap-154361/intro", 1, 1);
		Audio.loadWavData("background-trap-154361/loop", 1, 1);
		Audio.loadWavData("escape-151399/intro", 1, 1);
		Audio.loadWavData("escape-151399/loop-1", 1, 1);
		Audio.loadWavData("escape-151399/loop-2", 1, 1);
		Audio.loadWavData("escape-151399/loop-all", 1, 1);
		Audio.loadWavData("lifelike-126735/begin", 1, 1);
		Audio.loadWavData("lifelike-126735/loop-1", 1, 1);
		Audio.loadWavData("lifelike-126735/loop-2", 1, 1);
		Audio.loadWavData("lifelike-126735/loop-3", 1, 1);
		Audio.loadWavData("night-city-knight-127028/1-intro", 1, 1);
		Audio.loadWavData("night-city-knight-127028/2-intro", 1, 1);
		Audio.loadWavData("night-city-knight-127028/2-loop-1", 1, 1);
		Audio.loadWavData("night-city-knight-127028/2-loop-2", 1, 1);
		Audio.loadWavData("night-city-knight-127028/3-intro", 1, 1);
		Audio.loadWavData("night-city-knight-127028/3-loop-lo", 1, 1);
		Audio.loadWavData("night-city-knight-127028/4-intro-lo", 1, 1);
		Audio.loadWavData("night-city-knight-127028/4-loop-1", 1, 1);
		Audio.loadWavData("night-city-knight-127028/4-loop-2", 1, 1);
		Audio.loadWavData("night-city-knight-127028/4-loop-3", 1, 1);
		Audio.loadWavData("night-city-knight-127028/intro", 1, 1);
		Audio.loadWavData("night-city-knight-127028/loop", 1, 1);
		Audio.loadWavData("password-infinity-123276/intro", 1, 1);
		Audio.loadWavData("password-infinity-123276/loop-1", 1, 1);
		Audio.loadWavData("password-infinity-123276/loop-2", 1, 1);
		Audio.loadWavData("password-infinity-123276/loop-3-lo", 1, 1);
		Audio.loadWavData("retro-synthwave-short-version-176294/loop", 1, 1);
		Audio.loadWavData("smoothy-157149/loop", 1, 1);
		Audio.loadWavData("synthwave-background-music-155701/intro-1", 1, 1);
		Audio.loadWavData("synthwave-background-music-155701/intro-2", 1, 1);
		Audio.loadWavData("synthwave-background-music-155701/loop", 1, 1);
		
		Audio.loadWavData("moondeity-x-phonk-killer-death-dagger/intro", 1, 1);
		Audio.loadWavData("moondeity-x-phonk-killer-death-dagger/loop", 1, 1);
		Audio.loadWavData("moondeity-x-phonk-killer-electric-shock/intro", 1, 1);
		Audio.loadWavData("moondeity-x-phonk-killer-electric-shock/loop", 1, 1);
		Audio.loadWavData("moondeity-x-phonk-killer-megalomania/intro", 1, 1);
		Audio.loadWavData("moondeity-x-phonk-killer-megalomania/loop-1", 1, 1);
		Audio.loadWavData("mr-tom-spacesynth/intro-a", 1, 1);
		Audio.loadWavData("mr-tom-spacesynth/intro-b", 1, 1);
		Audio.loadWavData("mr-tom-spacesynth/loop", 1, 1);
		
		active=true;
		setResolution(1920, 1080);
		//setResolution(1366, 768);
		setFullscreen(true);
		setFixedStep(true);
		//setFPSLimit(30);
		createDisplay();
		TextureLoader.init();
		CharGraphics.init();
		
		// temp bloom
		DISPLAY_BUFFER_ID_0=createDisplayBuffer();
		//DISPLAY_BUFFER_ID_1=createDisplayBuffer();
		
		// adjust zoom ratios to current display resolution
		double ratioWidth=this.$displayWidth()/960.0;
		double ratioHeight=this.$displayHeight()/540.0;
		ZOOM=Math.max(ratioWidth, ratioHeight);
		
		MAIN_CAMERA_ZOOM=1.5*ZOOM;
		BC1_CAMERA_ZOOM=1.0*ZOOM;
		BC2_CAMERA_ZOOM=0.5*ZOOM;
		
		mc=new Camera();
		mc.setZoom(MAIN_CAMERA_ZOOM);
		bc1=new Camera();
		bc1.setZoom(BC1_CAMERA_ZOOM);
		bc2=new Camera();
		bc2.setZoom(BC2_CAMERA_ZOOM);
		
		Mouse.setGrabbed(true);
		Mouse.setCursorPosition($displayWidth()/2, $displayHeight()/2+128);
		MouseHandler.setDisplayDimensions($displayWidth(), $displayHeight());
		MouseHandler.focusOnCamera(mc);
		
		mp=new MouseProxy(this);
		
		// init states
		State.addState(-1, new PortalScreen(this));
		State.addState(0, new EndlessState(this));
		State.addState(1, new AsteroidsState(this));
		State.addState(2, new SpaceInvadersState(this));
		State.addState(3, new LunarState(this));
		State.addState(4, new TerraState(this));
		State.addState(5, new MissileCommandState(this));
		State.addState(6, new DemonstarState(this));
		State.addState(7, new GradiusState(this));
		State.addState(8, new GyrusState(this));
		State.addState(9, new CeraphimState(this));
		State.addState(16, new DemonstarBossState(this));
		State.addState(17, new GradiusBossState(this));
		State.addState(26, new SinistarState(this));
		State.addState(27, new HeavyWeaponState(this));
		State.addState(28, new PongState(this));
		State.addState(29, new LoopingSpaceInvadersState(this));
		
		Audio.enableBackgroundMusic(true);
		State.setCurrentState(-1);
		State.$currentState().reset();
		
	}
	
	public void destroy() {
		Audio.destroyAL();
		Display.destroy();
	}

	public boolean stopSignal() {
		return Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE);
	}
	
	private static final int DUST=256;
	public static final int BOUNDS=640;
	
	private static double ZOOM=2;
	public static double MAIN_CAMERA_ZOOM=1.5*ZOOM;
	public static double BC1_CAMERA_ZOOM=1.0*ZOOM;
	public static double BC2_CAMERA_ZOOM=0.5*ZOOM;
	
	private Camera mc, bc1, bc2;
	private ArrayList<StardustEntity> stars0=new ArrayList<StardustEntity>();
	private ArrayList<StardustEntity> stars1=new ArrayList<StardustEntity>();
	
	public Camera $camera(){
		return mc;
	}
	public StardustState $currentState(){
		return (StardustState) State.$currentState();
	}
	public ArrayList<StardustEntity> $dust(int layer){
		if(layer<1){
			return stars0;
		}else{
			return stars1;
		}
	}
	public Camera $dustCamera(int layer){
		if(layer<1){
			return bc1;
		}else{
			return bc2;
		}
	}
	
	// internal flags
	private boolean yes=false;
	private boolean hideStardust=false;
	public void hideStardust(){
		hideStardust=true;
	}
	
	private boolean redBorder=false;
	public void flashRedBorder(){
		redBorder=true;
	}
	
	private boolean redWarning=false;
	public void flashRedWarning(){
		redWarning=true;
	}
	
	private double bulletTime=1;
	public boolean isBulletTimeActive(){
		return bulletTime<0.26;
	}
	
	private double flasht=0; 
	private void flashScore(){
		if(flasht<=0){
			flasht=0.999;
		}
	}
	public void resetFlash(){
		flasht=3;
	}
	
	private int[] warp=null;
	private int[] warpd=null; 
	private int warpi=0;
	public void resetWarpFlags(){
		warp=new int[]{0,0,0,1,1,2,3,0,0,1};
		warpd=new int[]{1,2,3,4,5,8,6,9};
		// shuffle first 6
		for(int i=0;i<6;i++){
			int ii=$prng().$int(i, 6);
			if(i!=ii){
				int t=warpd[i];
				warpd[i]=warpd[ii];
				warpd[ii]=t;
			}
		}
		// demonstar/gradius
		if($prng().$double(0, 2)<1){
			warpd[6]=7;
		}
		warpi=0;
		
		//for(int i:warpd){
		//	System.out.print(i+",");
		//}
		//System.out.println();
	}
	private void checkForWarp(){
		if(!GameFlags.is("warp")&&warp[stage]>0){
			State.setCurrentState(warpd[warpi]);
			this.$currentState().reset();
			warpi++;
			warp[stage]--;
			resetFlash();
		}
	}
	
	private int histage=0;
	private int hiscore=0;
	public String $hiscore(){
		if(flasht>0){
			return flasht%0.5>=0.25?String.format("%d.%05d",histage,hiscore):"";
		}
		return String.format("%d.%05d",histage,hiscore);
	}
	
	private int stage=0;
	private int score=0;
	private int[] levelUpReq={
			//300, 900, 2700, 8000, 16000, 32000, 48000, 64000, 90000
			100, 300, 900, 1800, 3600, 7200, 16000, 24000, 32000
	};
	public int $stage(){
		return stage;
	}
	public void resetScore(){
		if(stage>histage||(stage>=histage&&score>hiscore)){
			histage=stage;
			hiscore=score;
			if(histage>0){
				GameFlags.setFlag("flash-hiscore", 1);
			}
		}
		stage=0;
		score=0;
	}
	public void addToScore(int points){
		//score+=points*(stage+1);
		int mod=stage;//stage-1;
		if(mod<1){
			mod=1;
		}
		score+=points*mod;
		if(stage<levelUpReq.length && score>levelUpReq[stage]){
			score=levelUpReq[stage];
		}
	}
	public String $score(){
		if(flasht>0){
			return flasht%0.5>=0.25?String.format("%d.%05d",stage,score):"";
		}
		return String.format("%d.%05d",stage,score);
	}
	public String $obfScore(){
		if(flasht>0){
			return flasht%0.5>=0.25?String.format("%s.%s",$prng().$string(1),$prng().$string(5)):"";
		}
		return String.format("%s.%s",$prng().$string(1),$prng().$string(5));
	}
	
	public void update(double dt){
		// bullet time
		flasht-=dt/$runSpeed();
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
			bulletTime-=2*dt;
			if(bulletTime<0.25){
				bulletTime=0.25;
				flashScore();
			}
		}else{
			bulletTime+=2*dt;
			if(bulletTime>1){
				bulletTime=1;
			}
		}
		Audio.setSFXPitch(bulletTime);
		this.setRunSpeed(bulletTime);
		
		if(GameFlags.is("debug")){
			
			// go to stage & reset
			if (Keyboard.isKeyDown(Keyboard.KEY_0)) {
				State.setCurrentState(0);
				this.$currentState().reset();
			}else if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
				State.setCurrentState(1);
				this.$currentState().reset();
			}else if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
				State.setCurrentState(2);
				this.$currentState().reset();
			}else if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
				State.setCurrentState(3);
				this.$currentState().reset();
			}else if (Keyboard.isKeyDown(Keyboard.KEY_4)) {
				State.setCurrentState(4);
				this.$currentState().reset();
			}else if (Keyboard.isKeyDown(Keyboard.KEY_5)) {
				State.setCurrentState(5);
				this.$currentState().reset();
			}else if (Keyboard.isKeyDown(Keyboard.KEY_6)) {
				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
					State.setCurrentState(26);
				}else{
					State.setCurrentState(6);
				}
				this.$currentState().reset();
			}else if (Keyboard.isKeyDown(Keyboard.KEY_7)) {
				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
					State.setCurrentState(27);
				}else{
					State.setCurrentState(7);
				}
				this.$currentState().reset();
			}else if (Keyboard.isKeyDown(Keyboard.KEY_8)) {
				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
					State.setCurrentState(28);
				}else{
					State.setCurrentState(8);
				}
				this.$currentState().reset();
			}else if (Keyboard.isKeyDown(Keyboard.KEY_9)) {
				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
					State.setCurrentState(29);
				}else{
					State.setCurrentState(9);
				}
				this.$currentState().reset();
			}
			
			// debug zoom camera
			if (Keyboard.isKeyDown(Keyboard.KEY_MINUS)) {
				mc.dZoom(-dt);
				bc1.setZoom(mc.$zoom()*(BC1_CAMERA_ZOOM/MAIN_CAMERA_ZOOM));
				bc2.setZoom(mc.$zoom()*(BC2_CAMERA_ZOOM/MAIN_CAMERA_ZOOM));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_EQUALS)) {
				mc.dZoom(+dt);
				bc1.setZoom(mc.$zoom()*(BC1_CAMERA_ZOOM/MAIN_CAMERA_ZOOM));
				bc2.setZoom(mc.$zoom()*(BC2_CAMERA_ZOOM/MAIN_CAMERA_ZOOM));
			}
			
			// debug move camera
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
				mc.dxy(-dt*120, 0);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
				mc.dxy(dt*120, 0);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)){
				mc.dxy(0, -dt*120);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
				mc.dxy(0, dt*120);
			}
		}
		
		
		bc1.hardCenterOnPoint(mc.$dx(), mc.$dy());
		bc2.hardCenterOnPoint(mc.$dx(), mc.$dy());
		
		//radar color wack
		if(yes){
			StardustEntity.shiftColor(dt*1.25);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {// || GameFlags.is("debugshow")) {
			yes=true;
		}
		
		//update stars
		Iterator<StardustEntity> ie=stars0.iterator();
		double dx1=BOUNDS*(MAIN_CAMERA_ZOOM/BC1_CAMERA_ZOOM);
		double dx2=BOUNDS*(MAIN_CAMERA_ZOOM/BC2_CAMERA_ZOOM);
		while(ie.hasNext()){
			StardustEntity e=ie.next();
			if(!e.isActive() || Vector.distanceFromTo(e.$x(),e.$y(),mc.$dx(),mc.$dy())>dx1+1){
				ie.remove();
			}
			e.update(dt);
		}
		ie=stars1.iterator();
		while(ie.hasNext()){
			StardustEntity e=ie.next();
			if(!e.isActive() || Vector.distanceFromTo(e.$x(),e.$y(),mc.$dx(),mc.$dy())>dx2+2){
				ie.remove();
			}
			e.update(dt);
		}
				
		// generate stardust
		while(stars0.size()<DUST){
			double t=$prng().$double(0, 2*Math.PI);
			Stardust de=new Stardust(this,
					mc.$dx()+Vector.vectorToDx(t,$prng().$double(0, dx1)),
					mc.$dy()+Vector.vectorToDy(t,$prng().$double(0, dx1)),
					$prng().$int(-1, 3));
			stars0.add(de);
		}
		while(stars1.size()<DUST){
			double t=$prng().$double(0, 2*Math.PI);
			Stardust de=new Stardust(this,
					mc.$dx()+Vector.vectorToDx(t,$prng().$double(0, dx2)),
					mc.$dy()+Vector.vectorToDy(t,$prng().$double(0, dx2)),
					$prng().$int(-1, 3));
			stars1.add(de);
		}
		
		// update current state
		redBorder=false;
		redWarning=false;
		hideStardust=false;
		if(stage<levelUpReq.length && score>=levelUpReq[stage]){
			score=0;
			stage++;
		}
		checkForWarp();
		if(this.$currentState()!=null){
			this.$currentState().update(dt);
		}
		
		Audio.resolveSoundEffects();
	}
	
	// temp post processing
	private static int DISPLAY_BUFFER_ID_0=0;
	//private static int DISPLAY_BUFFER_ID_1=1;
	private static int DISPLAY_BUFFER_SIZE=2048;
	private int createDisplayBuffer(){
		int bytesPerPixel = 4;
		int glType = GL11.GL_RGBA;
		ByteBuffer data = ByteBuffer.allocateDirect(DISPLAY_BUFFER_SIZE*DISPLAY_BUFFER_SIZE*bytesPerPixel);
		IntBuffer id = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder()).asIntBuffer();
		GL11.glGenTextures(id); // Create Texture In OpenGL
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id.get(0));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D,0,glType,DISPLAY_BUFFER_SIZE,DISPLAY_BUFFER_SIZE,0,glType,GL11.GL_UNSIGNED_BYTE,data);
		return id.get(0);
	}
	private void updateDisplayBuffer(int id){
		GL11.glLoadIdentity();
		//GL13.glActiveTexture(id);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		//GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D,0, 0, 0, 0, 0,SCREEN_BUFFER_SIZE,SCREEN_BUFFER_SIZE);
		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D,0,(DISPLAY_BUFFER_SIZE-$displayWidth())/2,(DISPLAY_BUFFER_SIZE-$displayHeight())/2,0,0,DISPLAY_BUFFER_SIZE,DISPLAY_BUFFER_SIZE);
	}
	private void renderDisplayBuffer(int id, float dx, float dy, double a){
		GL11.glPushMatrix();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		GL11.glTranslatef(dx, dy, 0);
		GL11.glColor4d(1,1,1,a);
		float scale=1f;
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(-DISPLAY_BUFFER_SIZE/2, DISPLAY_BUFFER_SIZE/2, 0);
        GL11.glTexCoord2f(1/scale, 0);
        GL11.glVertex3f(DISPLAY_BUFFER_SIZE/2, DISPLAY_BUFFER_SIZE/2, 0);
        GL11.glTexCoord2f(1/scale, 1/scale);
        GL11.glVertex3f(DISPLAY_BUFFER_SIZE/2, -DISPLAY_BUFFER_SIZE/2, 0);
        GL11.glTexCoord2f(0, 1/scale);
        GL11.glVertex3f(-DISPLAY_BUFFER_SIZE/2, -DISPLAY_BUFFER_SIZE/2, 0);
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	// screen edge calculations
	public double $topScreenEdge(){
		return mc.$dy()-($displayHeight()/mc.$zoom()/2);
	}
	public double $bottomScreenEdge(){
		return mc.$dy()+($displayHeight()/mc.$zoom()/2);
	}
	public double $leftScreenEdge(){
		return mc.$dx()-($displayWidth()/mc.$zoom()/2);
	}
	public double $rightScreenEdge(){
		return mc.$dx()+($displayWidth()/mc.$zoom()/2);
	}
	
	public void render() {
		// clear screen
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		// render stars
		if(!hideStardust){
			for(Entity e:stars0){
				e.render(bc1);
			}
			for(Entity e:stars1){
				e.render(bc2);
			}
		}
		
		// render game state
		if(this.$currentState()!=null){
			this.$currentState().render(mc);
		}
		
		// render out of bounds
		VectorGraphics.beginVectorRender();
		GL11.glColor4d(1,0,0,0.5);
		VectorGraphics.renderVectorCircle(mc.$dx(), mc.$dy(), BOUNDS, 64, mc);
		VectorGraphics.endVectorRender();
		
		// render red border
		if(redBorder){
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4d(1,0,0,0.5);
			GL11.glVertex2d(1-$displayWidth()/2,1-$displayHeight()/2);
			GL11.glVertex2d(1-$displayWidth()/2,$displayHeight()/2);
			GL11.glVertex2d(1-$displayWidth()/2,$displayHeight()/2);
			GL11.glVertex2d($displayWidth()/2,$displayHeight()/2);
			GL11.glVertex2d($displayWidth()/2,$displayHeight()/2);
			GL11.glVertex2d($displayWidth()/2,1-$displayHeight()/2);
			GL11.glVertex2d($displayWidth()/2,1-$displayHeight()/2);
			GL11.glVertex2d(1-$displayWidth()/2,1-$displayHeight()/2);
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		
		if(redWarning){
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4d(1,0,0,1);
			GL11.glVertex2d(1-$displayWidth()/2,1-$displayHeight()/2);
			GL11.glVertex2d(1-$displayWidth()/2,$displayHeight()/2);
			GL11.glVertex2d(1-$displayWidth()/2,$displayHeight()/2);
			GL11.glVertex2d($displayWidth()/2,$displayHeight()/2);
			GL11.glVertex2d($displayWidth()/2,$displayHeight()/2);
			GL11.glVertex2d($displayWidth()/2,1-$displayHeight()/2);
			GL11.glVertex2d($displayWidth()/2,1-$displayHeight()/2);
			GL11.glVertex2d(1-$displayWidth()/2,1-$displayHeight()/2);
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			// red warning strings
			CharGraphics.drawRedHeaderString("! WARNING !", -99, 24-$displayHeight()/2, 1f);
			CharGraphics.drawRedHeaderString("! IMMINENT DEATH !", -162, $displayHeight()/2-48-8, 1f);
		}
		
		// temp post processing
		if(GameFlags.is("enable-altbloom")){
			updateDisplayBuffer(DISPLAY_BUFFER_ID_0);
			double a=0.5;
			for(int i=1;i<=6;i++){
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, -i, 0, a/i);
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, i, 0, a/i);
			}
			for(int i=1;i<=1;i++){
				//float ic=i*0.70710678118f;
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, 0, -i, a/(i+1));
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, 0, i, a/(i+1));
			}
		}else if(GameFlags.is("enable-bloom")){
			// old bloom
			///*
			updateDisplayBuffer(DISPLAY_BUFFER_ID_0);
			double a=0.225;
			//double b=0.85;
			float c=0.70710678118f; // 1/sqrt(2)
			for(int i=1;i<=GameFlags.valueOf("bloom-intensity");i++){
				float ic=i*c;
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, -i, 0, a/(i+1));
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, i, 0, a/(i+1));
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, 0, -i, a/(i+1));
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, 0, i, a/(i+1));
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, -ic, -ic, a/i);
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, ic, -ic, a/i);
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, -ic, ic, a/i);
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, ic, ic, a/i);
			}
			//*/
			
			/*
			// first pass
			double a=0.5;
			double b=1;
			updateDisplayBuffer(DISPLAY_BUFFER_ID_0);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			for(int i=1;i<=6;i++){
				GL11.glDisable(GL11.GL_BLEND);
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, -i, 0, a/i);
				renderDisplayBuffer(DISPLAY_BUFFER_ID_0, i, 0, a/i);
				GL11.glEnable(GL11.GL_BLEND);
			}
			updateDisplayBuffer(DISPLAY_BUFFER_ID_1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			
			for(int i=1;i<=6;i++){
				GL11.glDisable(GL11.GL_BLEND);
				renderDisplayBuffer(DISPLAY_BUFFER_ID_1, 0, -i, b/i);
				renderDisplayBuffer(DISPLAY_BUFFER_ID_1, 0, i, b/i);
				GL11.glEnable(GL11.GL_BLEND);
			}
			//GL11.glDisable(GL11.GL_BLEND);
			//renderDisplayBuffer(DISPLAY_BUFFER_ID_1, 0, 0, 1);
			//GL11.glEnable(GL11.GL_BLEND);
			//*/
		}
		
		// test
		/*
		updateOffScreenBuffer();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		float[] v=new float[]{
				-0.5f, -0.5f, 0,
				0.5f, -0.5f, 0,
		        0.5f, 0.5f, 0,
				-0.5f, 0.5f, 0,
				//-SCREEN_BUFFER_SIZE/2, SCREEN_BUFFER_SIZE/2, 0,
				//SCREEN_BUFFER_SIZE/2, SCREEN_BUFFER_SIZE/2, 0,
				//SCREEN_BUFFER_SIZE/2, -SCREEN_BUFFER_SIZE/2, 0,
				//-SCREEN_BUFFER_SIZE/2, -SCREEN_BUFFER_SIZE/2, 0,
		};
		float[] t=new float[]{
				0,0,
				1,0,
				1,1,
				0,1,
		};
		int[] i=new int[]{
				0,1,2,
				2,3,0,
		};
		Model m=new Model(v, t, i);
		Shader s=new Shader("/default.vs", "/blur.fs");
		s.bindAttribute(0, "vertices");
		s.bindAttribute(1, "textures");
		s.validate();
		
		//GL11.glColor4d(1,1,1,1);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, SCREEN_BUFFER_ID);
		
		s.start();
		s.setUniform("t", 0);
		//s.setUniform("targetHeight", SCREEN_BUFFER_SIZE);
		//s.setUniform("originalTexture", 0);
		m.render();
		s.stop();
		
		s.destroy();
		//*/
		
		// debug text
		//CharGraphics.drawString(String.format("%d",this.$currentState().$entities().size()), -$displayWidth()/2, -$displayHeight()/2, 1f);
		if(GameFlags.is("debugfps")){
			CharGraphics.drawString(String.format("LVL %s",$score()), -$displayWidth()/2, -$displayHeight()/2, 1f);
			CharGraphics.drawString(String.format("FPS %.1f",$FPS()), -$displayWidth()/2, -$displayHeight()/2+(14*1), 1f);
			CharGraphics.drawString(String.format("CAP %.1f%%",$tickCapacity()), -$displayWidth()/2, -$displayHeight()/2+(14*2), 1f);
			CharGraphics.drawString(String.format("RAM %d/%d KB",(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024, Runtime.getRuntime().totalMemory()/1024), -$displayWidth()/2, -$displayHeight()/2+(14*3), 1f);
			CharGraphics.drawString(String.format("SFX %d",Audio.$sfxCount()), -$displayWidth()/2, -$displayHeight()/2+(14*4), 1f);
			CharGraphics.drawString(String.format("AMX %.1f AMY %.1f",MouseHandler.$ax(),MouseHandler.$ay()), -$displayWidth()/2, -$displayHeight()/2+(14*5), 1f);
			CharGraphics.drawString(String.format("CMX %.1f CMY %.1f",MouseHandler.$mx(),MouseHandler.$my()), -$displayWidth()/2, -$displayHeight()/2+(14*6), 1f);
			CharGraphics.drawString(String.format("EEE %d",$currentState().$entities().size()), -$displayWidth()/2, -$displayHeight()/2+(14*7), 1f);
		}
		
		// abstention
		//if(isBulletTimeActive() && flasht%0.5>=0.25) {
		//	CharGraphics.drawRedHeaderString("! Ð²Ð¾Ð·Ð´ÐµÑ€Ð¶Ð°Ð½Ð¸Ðµ !", ($displayWidth()/2)-(18*15), ($displayHeight()/2)-36, 1f);
		//}
	}

}