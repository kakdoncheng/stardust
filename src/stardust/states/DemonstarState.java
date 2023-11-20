package stardust.states;

import java.util.ArrayList;
import java.util.Iterator;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.Explosion;
import stardust.entities.IndicatorDestroy;
import stardust.entities.StardustEntity;
import stardust.entities.demonstar.AntiMartianProjectile;
import stardust.entities.demonstar.MartianFighter;
import stardust.entities.demonstar.MartianFighterTypeH;
import stardust.entities.demonstar.MartianFighterTypeM;
import stardust.entities.demonstar.MartianFighterTypeT;
import stardust.entities.demonstar.MartianShip;
import stardust.entities.demonstar.MartianSwarmTypeS;
import stardust.entities.demonstar.PlayerSpaceship;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.State;
import engine.gfx.Camera;

public class DemonstarState extends StardustState{

	public DemonstarState(StardustGame game) {
		super(game);
		reset();
	}

	// internal flags/var
	private double bgT;
	private double spawnt;
	private double delay;
	private boolean tagged;
	
	// entities
	private StardustEntity player;
	private ArrayList<StardustEntity> hostiles;
	
	public void reset() {
		GameFlags.setFlag("warp", 1);
		clearBackgroundText();
		ec.clear();
		ec.setRenderDistance(StardustGame.BOUNDS);
		sparks.clear();
		sparks.setRenderDistance(StardustGame.BOUNDS);
		game.$camera().hardCenterOnPoint(0, 0);
		bgT=0;
		delay=1;
		tagged=false;
		
		spawnt=20;
		hostiles=new ArrayList<StardustEntity>();
		
		player=new PlayerSpaceship(game, 0, 0);
		ec.addEntity(player);
		MartianShip.destroyIfOutOfScreenBounds(false);
	}

	public void update(double dt) {
		if(bgT<0.5){
			bgT+=dt;
			updateBackgroundText();
			game.hideStardust();
			return;
		}
		
		// move camera
		double dcy=-180*dt;
		game.$camera().dxy(0, dcy);
		for(StardustEntity e:ec.$entities()){
			e.setXY(e.$x(), e.$y()+dcy);
		}
		
		// spawn enemies
		spawnt-=dt;
		if(spawnt>0){
			if(game.$prng().$double(0, 1)<0.01){
				double tx=game.$prng().$double(-220, 220);
				double ty=game.$topScreenEdge();
				StardustEntity e=new MartianFighter(game,tx,ty);
				ec.addEntity(e);
				hostiles.add(e);
			}
			if(game.$prng().$double(0, 1)<0.005){
				double tx=game.$prng().$double(-220, 220);
				double ty=game.$topScreenEdge();
				StardustEntity e=new MartianFighterTypeT(game,tx,ty);
				ec.addEntity(e);
				hostiles.add(e);
			}
			if(game.$prng().$double(0, 1)<0.0025){
				double tx=game.$prng().$double(-220, 220);
				double ty=game.$topScreenEdge()-8;
				StardustEntity e=new MartianSwarmTypeS(game,tx,ty);
				ec.addEntity(e);
				hostiles.add(e);
			}
			if(game.$prng().$double(0, 1)<0.01){
				double tx=game.$prng().$double(-220, 220);
				double ty=game.$topScreenEdge();
				StardustEntity e=new MartianFighterTypeH(game,tx,ty);
				e.setTarget(player);
				ec.addEntity(e);
				hostiles.add(e);
			}
			if(game.$prng().$double(0, 1)<0.01){
				double tx=game.$prng().$double(-220, 220);
				double ty=game.$topScreenEdge();
				StardustEntity e=new MartianFighterTypeM(game,tx,ty);
				e.setTarget(player);
				ec.addEntity(e);
				hostiles.add(e);
			}
		}else{
			MartianShip.destroyIfOutOfScreenBounds(true);
			if(!tagged){
				for(StardustEntity e:ec.$entities()){
					if(e instanceof MartianShip){
						ec.addEntity(new IndicatorDestroy(game, e));
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
				State.setCurrentState(16);
				game.$currentState().reset();
				for(StardustEntity e:ec.$entities()){
					if(e.equals(player)){
						continue;
					}
					if(e instanceof AntiMartianProjectile){
						game.$currentState().addEntity(e);
						continue;
					}
					game.$currentState().addEntity(new Explosion(game,e.$x(),e.$y(),(int)e.$r()));
				}
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
		
		ec.update(dt);
		sparks.update(dt);
	}

	public void render(Camera c) {
		if(bgT<0.5){
			if(bgT<0.125){
				renderBackgroundText();
			}
			return;
		}
		
		ec.render(c);
		sparks.render(c);
		
		if(GameFlags.is("score")){
			CharGraphics.drawHeaderString(game.$obfScore(),//hostiles.size()+"",//
					(-game.$displayWidth()/2)+18,
					(-game.$displayHeight()/2)+18,
					1);
		}
		//CharGraphics.drawString(String.format("%.1f", spawnt), -game.$displayWidth()/2, +game.$displayHeight()/2-14, 1f);
	}

}