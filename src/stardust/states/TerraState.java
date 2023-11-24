package stardust.states;

import java.util.ArrayList;
import java.util.Iterator;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.IndicatorDefend;
import stardust.entities.IndicatorDestroy;
import stardust.entities.StardustEntity;
import stardust.entities.terra.Frigate;
import stardust.entities.terra.PlayerOrbitalDrone;
import stardust.entities.terra.ReplicatingMine;
import stardust.entities.terra.Terra;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.State;
import engine.Vector;
import engine.gfx.Camera;

public class TerraState extends StardustState{

	public TerraState(StardustGame game) {
		super(game);
		reset();
	}
	
	private StardustEntity player;
	private StardustEntity planet;
	private ArrayList<StardustEntity> hostiles;
	
	private double mt;
	private double wavet;
	private double wavec;
	private double bgT;
	private double delay;
	private boolean tagged;

	public void reset() {
		GameFlags.setFlag("warp", 1);
		clearBackgroundText();
		targetable.clear();
		targetable.setRenderDistance(StardustGame.BOUNDS);
		particles.clear();
		particles.setRenderDistance(StardustGame.BOUNDS);
		game.$camera().hardCenterOnPoint(0, 0);
		mt=0;
		wavet=16;
		wavec=30;
		bgT=0;
		delay=1;
		tagged=false;
		
		hostiles=new ArrayList<StardustEntity>();
		
		player=new PlayerOrbitalDrone(game, 0, 0, 55);
		planet=new Terra(game, 0, 0);
		targetable.addEntity(player);
		targetable.addEntity(planet);
		targetable.addEntity(new IndicatorDefend(game, planet, true));
	}

	private void spawnMine(int dist){
		double tt=game.$prng().$double(0, 2*Math.PI);
		double xx=Vector.vectorToDx(tt, dist);
		double yy=Vector.vectorToDy(tt, dist);
		StardustEntity e=new ReplicatingMine(game, xx, yy, Vector.directionFromTo(xx, yy, planet.$x(), planet.$y()));
		hostiles.add(e);
		targetable.addEntity(e);
	}
	private void spawnShip(int dist){
		double tt=game.$prng().$double(0, 2*Math.PI);
		double xx=Vector.vectorToDx(tt, dist);
		double yy=Vector.vectorToDy(tt, dist);
		targetable.addEntity(new Frigate(game, xx, yy, Vector.directionFromTo(xx, yy, planet.$x(), planet.$y())));
	}
	
	public void update(double dt) {
		if(bgT<0.5){
			bgT+=dt;
			updateBackgroundText();
			game.hideStardust();
			return;
		}
		mt-=dt;
		if(mt<=0 && wavet>0){
			spawnMine(360);
			if(game.$prng().$double(0, 1)>0.5){
				spawnShip(360);
			}
			mt+=game.$prng().$double(0.25, 2);
		}
		
		wavet-=dt;
		if(wavet<=0){
			if(wavec>0){
				spawnMine(game.$prng().$int(240, 300));
				wavec--;
				wavet=0.125;
			}
		}
		
		if(wavet<-0.25 && !tagged){
			for(StardustEntity e:targetable.$entities()){
				if(e instanceof ReplicatingMine){
					targetable.addEntity(new IndicatorDestroy(game, e));
				}
			}
			tagged=true;
		}
		targetable.update(dt);
		particles.update(dt);
		
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
		if(!planet.isActive() || hostiles.size()<1){
			if(planet.isActive()){
				// win
				// set game flags player xy & success
				// set stage back to endless
				GameFlags.markFlag("terra");
				GameFlags.setFlag("success", 1);
				GameFlags.setFlag("player-x", (int)player.$x());
				GameFlags.setFlag("player-y", (int)player.$y());
				State.setCurrentState(0);
				game.$currentState().reset();
				game.$currentState().addEntity(new ElectromagneticPulse(game,lx,ly));
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
		
		
	}
}
