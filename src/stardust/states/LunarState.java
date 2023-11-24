package stardust.states;

import stardust.StardustGame;
import stardust.entities.Asteroid;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.Explosion;
import stardust.entities.StardustEntity;
import stardust.entities.luna.Luna;
import stardust.entities.luna.PlayerLunarModule;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.State;
import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

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
		
		moon=new Luna(game);
		
		targetable.addEntity(player);
		targetable.addEntity(moon);
		//ec.addEntity(new FlashingDefendIndicator(game, moon, true));
	}

	public void update(double dt) {
		if(bgT<0.5){
			bgT+=dt;
			if(!bgmClear) {
				Audio.clearBackgroundMusicQueue();
				Audio.clearBackgroundMusic();
				bgmClear=true;
			}
			
			updateBackgroundText();
			game.hideStardust();
			return;
		}
		
		if(bgmClear) {
			Audio.queueBackgroundMusic("night-city-knight-127028/2-loop-2");
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

	public void render(Camera c) {
		if(bgT<0.5){
			if(bgT<0.125){
				renderBackgroundText();
			}
			return;
		}
		
		targetable.render(c);
		particles.render(c);
		
		if(GameFlags.is("score")){
			CharGraphics.drawHeaderString(game.$obfScore(),
					(-game.$displayWidth()/2)+18,
					(-game.$displayHeight()/2)+18,
					1);
		}
		
		//if(GameFlags.is("debugfps")){
		//	CharGraphics.drawString(String.format("%.1f",Vector.distanceFromTo(moon.$x(), moon.$y(), player.$x(), player.$y())), 
	    //			-game.$displayWidth()/2, +game.$displayHeight()/2-14, 1f);
		//}
		//CharGraphics.drawString(String.format("%.1f %.1f",player.$t(),player.directionTo(moon)), -game.$displayWidth()/2, +game.$displayHeight()/2-14, 1f);
	}

}
