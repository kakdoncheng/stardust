package stardust.states;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.IndicatorDefend;
import stardust.entities.IndicatorDestroy;
import stardust.entities.StardustEntity;
import stardust.entities.silo.AnnihilatingExplosion;
import stardust.entities.silo.AnnihilatingMissile;
import stardust.entities.silo.City;
import stardust.entities.silo.RadarTower;
import stardust.entities.silo.Silo;
import stardust.entities.silo.Warhead;
import stardust.entities.silo.WarheadExplosion;
import stardust.gfx.CharGraphics;
import stardust.gfx.VectorGraphics;
import engine.GameFlags;
import engine.State;
import engine.Vector;
import engine.gfx.Camera;
import engine.input.MouseHandler;
import engine.sfx.Audio;

public class MissileCommandState extends StardustState{

	public MissileCommandState(StardustGame game) {
		super(game);
		reset();
	}

	// internal flags/var
	private boolean click;
	private boolean tagged;
	private boolean bgmClear;
	private boolean siren;
	private double sirenT;
	private int warheads;
	private double delay;
	private double delayw;
	private double delayl;
	private double bgT;
	private char[] dis;
	
	// entities
	private ArrayList<Silo> silos;
	//private ArrayList<City> cities;
	
	public void reset() {
		GameFlags.setFlag("warp", 1);
		clearBackgroundText();
		targetable.clear();
		targetable.setRenderDistance(StardustGame.BOUNDS);
		particles.clear();
		particles.setRenderDistance(StardustGame.BOUNDS);
		game.$camera().hardCenterOnPoint(0, 0);
		bgT=0;
		delay=1;
		
		click=true;
		bgmClear=false;
		sirenT=2;
		siren=false;
		tagged=false;
		warheads=45;
		delayw=4;
		delayl=5;
		
		dis=String.format("0.%05d", warheads).toCharArray();
		Warhead.setff(false);
		
		// missile silos
		silos=new ArrayList<Silo>();
		for(int i=0;i<3;i++){
			Silo a=new Silo(game, -220+(i*220), 125);
			silos.add(a);
			targetable.addEntity(a);
		}
		
		// cities
		//cities=new ArrayList<City>();
		for(int i=0;i<3;i++){
			City a=new City(game, -150+(i*45), 140);
			targetable.addEntity(a);
			targetable.addEntity(new IndicatorDefend(game,-150+(i*45), 128));
			//cities.add(a);
		}
		for(int i=0;i<3;i++){
			City a=new City(game, 150-(i*45), 140);
			targetable.addEntity(a);
			targetable.addEntity(new IndicatorDefend(game, 150-(i*45), 128));
			//cities.add(a);
		}
		targetable.addEntity(new RadarTower(game, 110, 140));
	}

	public void update(double dt) {
		if(bgT<0.5){
			bgT+=dt;
			if(!bgmClear) {
				// dynamically load music here?
				Audio.clearBackgroundMusicQueue();
				Audio.clearBackgroundMusic();
				bgmClear=true;
			}
			
			updateBackgroundText();
			game.hideStardust();
			return;
		}
		if(bgmClear) {
			Audio.queueBackgroundMusic("night-city-knight-127028/intro");
			Audio.queueBackgroundMusic("night-city-knight-127028/loop");
			bgmClear=false;
		}
		
		if(warheads<1 && !tagged && !Warhead.isFF()){
			for(StardustEntity e:targetable.$entities()){
				if(e instanceof Warhead){
					targetable.addEntity(new IndicatorDestroy(game, e));
				}
			}
			tagged=true;
		}
		
		int wave=game.$prng().$int(1, 5);
		delayw-=dt;
		delayl-=0.1*dt;
		if(delayl<=2){
			delayl=2;
		}

		// siren sfx
		sirenT-=dt;
		if(!siren && sirenT<=0) {
			siren=true;
			Audio.playSoundEffect("air-raid-siren", 1, 1);
		}

		if(Warhead.isFF()){
			if(delayw<=0){
				if(warheads>0){
					StardustEntity e=new Warhead(game, game.$prng().$double(-220, 220), -200);
					double tx=game.$prng().$double(-220, 220);
					double ty=120;
					e.setDirection(Vector.directionFromTo(e.$x(), e.$y(), tx, ty));
					e.setSpeedVector(Vector.directionFromTo(e.$x(), e.$y(), tx, ty), 45);
					targetable.addEntity(e);
					warheads--;
					dis=String.format("0.%05d", warheads).toCharArray();
				}
				delayw=0.1;
			}
			if(delayw>0.1){
				delayw=0.1;
			}
		}else if(delayw<=0){
			for(int i=0;i<wave;i++){
				if(warheads>0){
					StardustEntity e=new Warhead(game, game.$prng().$double(-220, 220), -200);
					double tx=game.$prng().$double(-220, 220);
					double ty=120;
					e.setDirection(Vector.directionFromTo(e.$x(), e.$y(), tx, ty));
					e.setSpeedVector(Vector.directionFromTo(e.$x(), e.$y(), tx, ty), 45);
					targetable.addEntity(e);
					warheads--;
					dis=String.format("0.%05d", warheads).toCharArray();
				}
			}
			delayw+=game.$prng().$double(1, delayl);
		} 
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Mouse.isButtonDown(0)){
			if(!click){
				// fire missile from closest silo
				Silo cs=null;
				for(Silo s:silos){
					if(s.isEmpty()){
						continue;
					}
					if(cs==null||Vector.distanceFromTo(s.$x(), s.$y(), MouseHandler.$mx(), MouseHandler.$my())<
							Vector.distanceFromTo(cs.$x(), cs.$y(), MouseHandler.$mx(), MouseHandler.$my())){
						cs=s;
					}
				}
				if(cs!=null){
					cs.fireMissile();
				}
			}
			click=true;
    	}else{
    		click=false;
    	}
		targetable.update(dt);
		particles.update(dt);
		
		// speed up warheads when no ammo left
		boolean isff=true;
		for(Silo s:silos){
			if(!s.isEmpty()){
				isff=false;
			}
		}
		// only speed up warheads when all ammo has exploded
		if(isff){
			for(StardustEntity e:this.$entities()){
				if(e instanceof AnnihilatingMissile){
					isff=false;
					break;
				}
				if(e instanceof AnnihilatingExplosion && !(e instanceof WarheadExplosion)){
					isff=false;
					break;
				}
			}
		}
		if(isff){
			Warhead.setff(true);
		}
		
		// trigger warheads that hit ground
		for(StardustEntity e:targetable.$entities()){
			if(e instanceof Warhead){
				if(e.$y()>140){
					e.deactivate();
				}
			}
		}
		
		// get xy of last object removed
		double lx=0;
		double ly=0;
		double lx2=0;
		double ly2=0;
		for(StardustEntity e:targetable.$lastRemovedEntities()){
			if(e instanceof AnnihilatingExplosion){
				lx=e.$x();
				ly=e.$y();
			}
			if(e instanceof City){
				lx2=e.$x();
				ly2=e.$y();
			}
		}
		
		// check end condition
		if(targetable.$sizeOf(City.class)<1 || (warheads<1 && targetable.$sizeOf(Warhead.class)<1 && targetable.$sizeOf(WarheadExplosion.class)<1 && targetable.$sizeOf(AnnihilatingExplosion.class)<1)){
			if(targetable.$sizeOf(City.class)>0){
				// win
				// set game flags player xy & success
				// set stage back to endless
				GameFlags.markFlag("warhead");
				GameFlags.setFlag("success", 1);
				GameFlags.setFlag("player-x", (int)lx);
				GameFlags.setFlag("player-y", (int)ly);
				State.setCurrentState(0);
				game.$currentState().reset();
				game.$currentState().addEntity(new ElectromagneticPulse(game,lx,ly));
			}else{
				Audio.clearBackgroundMusicQueue();
				Audio.clearBackgroundMusic();
				if(delay>0.999){
					game.$currentState().addEntity(new ElectromagneticPulse(game,lx2,ly2));
				}
			}
			game.flashRedBorder();
			delay-=dt;
			if(delay<=0){
				if(!GameFlags.is("goto-portal")){
					State.setCurrentState(0);
				}else{
					State.setCurrentState(-1);
				}
				game.$currentState().reset();
			}
		}
		
		
	}

	private double l[]={
		//-45,-4,-43,-4,-1,-4,1,-4,43,-4,45,-4,-1,-4,-4,0,1,-4,4,0,-45,-4,-48,0,-43,-4,-40,0,43,-4,40,0,45,-4,48,0,40,0,4,0,-4,0,-40,0,-48,0,-68,0,48,0,68,0,
		-6,0,-2,-5,-2,-5,-1,-4,-1,-4,1,-4,1,-4,2,-5,2,-5,6,0,-50,0,-46,-5,-46,-5,-45,-4,-45,-4,-43,-4,-43,-4,-42,-5,-42,-5,-38,0,38,0,42,-5,42,-5,43,-4,43,-4,45,-4,45,-4,46,-5,46,-5,50,0,38,0,6,0,-6,0,-38,0,-50,0,-68,0,50,0,68,0,
	};
	private double scale=5;
	public void render(Camera c) {
		if(bgT<0.5){
			if(bgT<0.125){
				renderBackgroundText();
			}
			return;
		}
		
		// blackout floor
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		// x+(int)(-340*c.$zoom())
		// y+(int)(140*c.$zoom())
		GL11.glScissor(0, 0, game.$displayWidth(), game.$displayHeight()/2-(int)(140*c.$zoom()));
		//GL11.glClearColor(0,0,0.25f,1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		//GL11.glClearColor(0,0,0,0);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		//GL11.glPushMatrix();
		//GL11.glTranslatef(c.$cx(0), c.$cy(0), 0);
		//GL11.glBegin(GL11.GL_QUADS);
		//GL11.glColor4d(0, 0, 0, 1);
		//GL11.glVertex2d(-340*c.$zoom(), 140*c.$zoom());
		//GL11.glVertex2d(340*c.$zoom(), 140*c.$zoom());
		//GL11.glVertex2d(340*c.$zoom(), 280*c.$zoom());
		//GL11.glVertex2d(-340*c.$zoom(), 280*c.$zoom());
		//GL11.glEnd();
		//GL11.glPopMatrix();
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// render floor
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(0), c.$cy(140), 0);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1, 1, 1, 0.5);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		targetable.render(c);
		particles.render(c);
		VectorGraphics.renderDotCursor();
		
		if(GameFlags.is("score")){
			String s="";
			for(char ch:dis){
				//s+=ch+"";
				if(ch=='0'){
					s+=game.$prng().$string(1);
				}else{
					s+=ch+"";
				}
			}
			CharGraphics.drawHeaderString(String.format("%s",s),
					(-game.$displayWidth()/2)+18,
					(-game.$displayHeight()/2)+18,
					1);
		}
	}

}
