package stardust.states;

import engine.GameFlags;
import engine.State;
import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;
import stardust.StardustGame;
import stardust.entities.Asteroid;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.Explosion;
import stardust.entities.IndicatorEmergencyLanding;
import stardust.entities.IndicatorLowFuel;
import stardust.entities.StardustEntity;
import stardust.entities.luna.Luna;
import stardust.entities.luna.PlayerLunarModule;
import stardust.gfx.CharGraphics;

public class LunarState extends StardustState{

	public LunarState(StardustGame game) {
		super(game);
		reset();
	}

	// internal flags/var
	private boolean bgmClear;
	private double gF;
	private double bgT;
	private double debrisdt;
	private double delay;
	private char[] dis;
	
	// entities
	private PlayerLunarModule player;
	private StardustEntity moon;
	
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
		
		gF=5;
		debrisdt=0;
		
		bgmClear=false;
		
		player=new PlayerLunarModule(game);
		double t=game.$prng().$double(0, 2*Math.PI);
		double dist=game.$prng().$double(80, 160);
		
		// orbit
		player.setSpeedVector(t, gF*gF);
		player.setXY(Vector.vectorToDx(t+Math.PI*1.5, dist), Vector.vectorToDy(t+Math.PI*1.5, dist));
		particles.addEntity(new IndicatorLowFuel(game, player, true));
		
		moon=new Luna(game);
		particles.addEntity(new IndicatorEmergencyLanding(game, moon, true));
		
		targetable.addEntity(player);
		targetable.addEntity(moon);
		//ec.addEntity(new FlashingDefendIndicator(game, moon, true));
		
		dis=String.format("0.%05d", player.$fuel()).toCharArray();
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
			Audio.queueBackgroundMusic("hero-80s-127027/intro");
			Audio.queueBackgroundMusic("hero-80s-127027/loop-a");
			bgmClear=false;
		}
		
		// gravity vector towards moon
		for(StardustEntity e:targetable.$entities()){
			double gt=e.directionTo(moon);
			e.applyAccelerationVector(gt, gF, dt);
		}
		
		// spawn asteroids
		debrisdt-=dt;
		if(debrisdt<0){
			double t=game.$prng().$double(0, 2*Math.PI);
			int ds=game.$prng().$int(-4, 8);
			StardustEntity e=new Asteroid(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2),ds);
			debrisdt=game.$prng().$double(0.125, 2);
			targetable.addEntity(e);
		}
		
		// moon collisions
		for(StardustEntity e:targetable.$entities()){
			if(!e.isCollidable() || e==moon){
				continue;
			}
			if(moon.distanceTo(e)<e.$r()+moon.$r()){
				e.setTarget(moon);
				e.deactivate();
				game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*1));
			}
		}
		
		targetable.update(dt);
		particles.update(dt);
		
		// check end condition
		double error=Math.abs(Vector.tdistanceFromTo(player.directionTo(moon), player.$t()));
		//if(error>Math.PI*0.05){
		//	game.flashRedBorder();
		//}
		if(player.distanceTo(moon)<=player.$r()+moon.$r()+1){
			if(error<Math.PI*0.05){
				player.freeze();
			}
		}
		if(!player.isActive() || player.isFrozen()){
			if(player.isActive()){
				// win
				// set game flags player xy & success
				// set stage back to endless
				GameFlags.markFlag("lunar");
				GameFlags.setFlag("success", 1);
				GameFlags.setFlag("player-x", (int)player.$x());
				GameFlags.setFlag("player-y", (int)player.$y());
				State.setCurrentState(0);
				game.$currentState().reset();
				game.$currentState().addEntity(new ElectromagneticPulse(game,player.$x(),player.$y()));
			} else {
				Audio.clearBackgroundMusicQueue();
				Audio.clearBackgroundMusic();
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
		
		// fuel meter
		if(player!=null) {
			dis=String.format("0.%05d", player.$fuel()).toCharArray();
		}
	}

	public void render(Camera c) {
		if(bgT<0.5){
			if(bgT<0.125){
				renderBackgroundText();
			}
			return;
		}
		
		targetable.render(c);
		particles.render(c);
		
		// fuel meter replacing score
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
		
		// obfuscated score
		/*
		if(GameFlags.is("score")){
			CharGraphics.drawHeaderString(game.$obfScore(),
					(-game.$displayWidth()/2)+18,
					(-game.$displayHeight()/2)+18,
					1);
		}
		//*/
		
		//if(GameFlags.is("debugfps")){
		//	CharGraphics.drawString(String.format("%.1f",Vector.distanceFromTo(moon.$x(), moon.$y(), player.$x(), player.$y())), 
	    //			-game.$displayWidth()/2, +game.$displayHeight()/2-14, 1f);
		//}
		//CharGraphics.drawString(String.format("%.1f %.1f",player.$t(),player.directionTo(moon)), -game.$displayWidth()/2, +game.$displayHeight()/2-14, 1f);
	}

}
