package stardust.states;

import java.util.ArrayList;
import java.util.Iterator;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.Explosion;
import stardust.entities.IndicatorDestroy;
import stardust.entities.Spark;
import stardust.entities.StardustEntity;
import stardust.entities.gradius.AntiGradiusProjectile;
import stardust.entities.gradius.GradiusDroneSwarm;
import stardust.entities.gradius.GradiusFighterTypeE;
import stardust.entities.gradius.GradiusFighterTypeH;
import stardust.entities.gradius.GradiusFighterTypeM;
import stardust.entities.gradius.GradiusShip;
import stardust.entities.gradius.PlayerSpacecraft;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.State;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class GradiusState extends StardustState{

	public GradiusState(StardustGame game) {
		super(game);
		reset();
	}

	// internal flags/var
	private double bgT;
	private double spawnt;
	private double delay;
	private boolean tagged;
	private boolean bgmClear;
	
	// entities
	private StardustEntity player;
	private ArrayList<StardustEntity> hostiles;
	
	// override for tracer dot
	public void addEntity(StardustEntity e){
		if(e instanceof Spark || e instanceof Explosion){
			particles.addEntity(e);
		}else{
			targetable.addEntity(e);
		}
	}
	
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
		tagged=false;
		bgmClear=false;
		
		spawnt=20;
		hostiles=new ArrayList<StardustEntity>();
		
		player=new PlayerSpacecraft(game, 0, 0);
		targetable.addEntity(player);
		GradiusShip.destroyIfOutOfScreenBounds(false);
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
			Audio.queueBackgroundMusic("smoothy-157149/loop");
			bgmClear=false;
		}
		
		// move camera
		double dcx=180*dt;
		game.$camera().dxy(dcx, 0);
		for(StardustEntity e:targetable.$entities()){
			e.setXY(e.$x()+dcx, e.$y());
		}
		
		// spawn hostiles
		spawnt-=dt;
		if(spawnt>0){
			if(game.$prng().$double(0, 1)<0.03){
				double tx=game.$rightScreenEdge()+8;
				double ty=game.$prng().$double(-140, 140);
				StardustEntity e=new GradiusFighterTypeE(game,tx,ty);
				e.setTarget(player);
				targetable.addEntity(e);
				hostiles.add(e);
			}
			if(game.$prng().$double(0, 1)<0.02){
				double tx=game.$rightScreenEdge();
				double ty=game.$prng().$double(-140, 140);
				StardustEntity e=new GradiusFighterTypeH(game,tx,ty);
				e.setTarget(player);
				targetable.addEntity(e);
				hostiles.add(e);
			}
			if(game.$prng().$double(0, 1)<0.01){
				double tx=game.$rightScreenEdge();
				double ty=game.$prng().$double(-140, 140);
				StardustEntity e=new GradiusFighterTypeM(game,tx,ty);
				targetable.addEntity(e);
				hostiles.add(e);
			}
			if(game.$prng().$double(0, 1)<0.01){
				double tx=game.$rightScreenEdge()+8;
				double ty=game.$prng().$double(-140, 140);
				StardustEntity e=new GradiusDroneSwarm(game,tx,ty);
				targetable.addEntity(e);
				hostiles.add(e);
			}
		}else{
			GradiusShip.destroyIfOutOfScreenBounds(true);
			if(!tagged){
				for(StardustEntity e:targetable.$entities()){
					if(e instanceof GradiusShip){
						targetable.addEntity(new IndicatorDestroy(game, e));
					}
				}
				tagged=true;
			}
		}
		
		// count
		// get xy of last enemy removed
		double lx=0;
		double ly=0;
		Iterator<StardustEntity> ie=hostiles.iterator();
		while(ie.hasNext()){
			StardustEntity e=ie.next();
			if(!e.isActive()){
				ie.remove();
				lx=e.$x();
				ly=e.$y();
			}
		}
		
		// check end condition
		if(!player.isActive() || (spawnt<0 && hostiles.size()<1)){
			if(player.isActive()){
				// win
				// set game flags player xy & success
				// set stage to boss
				GameFlags.setFlag("player-x", (int)player.$x());
				GameFlags.setFlag("player-y", (int)player.$y());
				State.setCurrentState(17);
				game.$currentState().reset();
				for(StardustEntity e:targetable.$entities()){
					if(e.equals(player)){
						continue;
					}
					if(e instanceof AntiGradiusProjectile){
						game.$currentState().addEntity(e);
						continue;
					}
					game.$currentState().addEntity(new Explosion(game,e.$x(),e.$y(),(int)e.$r()));
				}
				game.$currentState().addEntity(new ElectromagneticPulse(game,lx,ly));
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
		
		targetable.update(dt);
		particles.update(dt);
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
	}

}