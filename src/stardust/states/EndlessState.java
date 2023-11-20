package stardust.states;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import stardust.StardustGame;
import stardust.entities.ApolloLunarModule;
import stardust.entities.ApolloServiceModule;
import stardust.entities.Asteroid;
import stardust.entities.Drone;
import stardust.entities.DroneTypeB;
import stardust.entities.DroneTypeH;
import stardust.entities.DroneTypeL;
import stardust.entities.GateAsteroid;
import stardust.entities.IndicatorDestroy;
import stardust.entities.PlayerStarfighter;
import stardust.entities.RadarSatellite;
import stardust.entities.SputnikOne;
import stardust.entities.StardustEntity;
import stardust.entities.Starfighter;
import stardust.entities.StarfighterTypeB;
import stardust.entities.StarfighterTypeC;
import stardust.entities.StarfighterTypeE;
import stardust.entities.StarfighterTypeI;
import stardust.entities.StarfighterTypeM;
import stardust.entities.StarfighterTypeN;
import stardust.entities.VostokOne;
import stardust.entities.VoyagerOne;
import stardust.entities.asteroids.Bogey;
import stardust.entities.asteroids.DumbBogey;
import stardust.entities.demonstar.MartianFighter;
import stardust.entities.demonstar.MartianFighterTypeH;
import stardust.entities.demonstar.MartianFighterTypeM;
import stardust.entities.demonstar.MartianFighterTypeT;
import stardust.entities.demonstar.MartianShip;
import stardust.entities.demonstar.MartianSwarmTypeS;
import stardust.entities.gradius.GradiusDroneSwarm;
import stardust.entities.gradius.GradiusFighterTypeE;
import stardust.entities.gradius.GradiusFighterTypeH;
import stardust.entities.gradius.GradiusFighterTypeM;
import stardust.entities.gradius.GradiusShip;
import stardust.entities.invaders.SmallAlienFormation;
import stardust.entities.invaders.Ufo;
import stardust.entities.terra.ReplicatingMine;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.State;
import engine.Vector;
import engine.gfx.Camera;

public class EndlessState extends StardustState{

	public EndlessState(StardustGame game) {
		super(game);
		reset();
	}

	// internal flags/var
	private boolean lmb;
	private double wt;
	private double lunardt;
	private double lunarsdt;
	private double sputnikdt;
	private double vostokdt;
	private double voyagerdt;
	private double delay;
	
	// focused entities
	private StardustEntity player;
	private StardustEntity gate;
	
	// space race era junk
	private StardustEntity lunar;
	private StardustEntity lunars;
	private StardustEntity sputnik;
	private StardustEntity vostok;
	private StardustEntity voyager;
	private ArrayList<StardustEntity> debris;
	//private ArrayList<StardustEntity> hostiles;
	
	public void reset() {
		GameFlags.setFlag("warp", 0);
		ec.clear();
		ec.setRenderDistance(StardustGame.BOUNDS);
		sparks.clear();
		sparks.setRenderDistance(StardustGame.BOUNDS);
		//game.$camera().hardCenterOnPoint(0, 0);
		
		debris=new ArrayList<StardustEntity>();
		//hostiles=new ArrayList<StardustEntity>();
		lmb=false;
		wt=0;
		delay=1;
		
		lunar=null;
		lunardt=3;
		lunars=null;
		lunarsdt=3;
		vostok=null;
		vostokdt=6;
		sputnik=null;
		sputnikdt=9;
		voyager=null;
		voyagerdt=12;
		
		if(GameFlags.is("success")){
			GameFlags.setFlag("success", 0);
			player=new PlayerStarfighter(game, GameFlags.valueOf("player-x"), GameFlags.valueOf("player-y"));
			player.setSpeedVector((double)GameFlags.valueOf("player-ft")/10000.0, GameFlags.valueOf("player-speed"));
		}else{
			GameFlags.setFlag("begin", 0);
			GameFlags.setFlag("asteroids", 0);
			GameFlags.setFlag("lunar", 0);
			GameFlags.setFlag("invaders", 0);
			GameFlags.setFlag("demonstar", 0);
			GameFlags.setFlag("gradius", 0);
			GameFlags.setFlag("gyruss", 0);
			GameFlags.setFlag("warhead", 0);
			GameFlags.setFlag("terra", 0);
			game.resetScore();
			game.resetWarpFlags();
			player=new PlayerStarfighter(game, game.$prng().$double(-360, 360), game.$prng().$double(-360, 360));
		}
		
		//player=new PlayerSpaceship(game, GameFlags.$valueOf("player-x"), GameFlags.$valueOf("player-y"));
		//player=new ClassicPlayerSpaceship(game, GameFlags.$valueOf("player-x"), GameFlags.$valueOf("player-y"));
		ec.addEntity(player);
		
		double t=game.$prng().$double(0, 2*Math.PI);
		gate=new GateAsteroid(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/4),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/4));
		ec.addEntity(gate);
		if(!GameFlags.is("begin")){
			GameFlags.setFlag("begin", 1);
			ec.addEntity(new IndicatorDestroy(game, gate));
		}else{
			gate.deactivate();
		}
		
		game.resetFlash();
		MartianShip.destroyIfOutOfScreenBounds(false);
		GradiusShip.destroyIfOutOfScreenBounds(false);
	}

	public void update(double dt) {
		wt+=dt;
		wt%=1;
		// test radarblip
		///*
		if(Mouse.isButtonDown(0) && wt>-1){
			if(!lmb){
				//ec.addEntity(new RadarBlip(game, MouseHandler.$mx(), MouseHandler.$my()));
			}
			lmb=true;
		}else{
			lmb=false;
		}
		//*/
		
		// non-player entities
		if(!gate.isActive()){
			
			// core hostiles
			// phase one
			if(game.$prng().$double(0, 1)<0.001){
				double t=game.$prng().$double(0, 2*Math.PI);
				StardustEntity e=new RadarSatellite(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
				ec.addEntity(e);
			}
			//if(game.$prng().$double(0, 1)<0.00075){
			//	double t=game.$prng().$double(0, 2*Math.PI);
			//	StardustEntity e=new RadarSatelliteTypeH(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
			//	ec.addEntity(e);
			//}
			
			if(game.$stage()>0){
				if(game.$prng().$double(0, 1)<0.005){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new Starfighter(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					e.setTarget(player);
					ec.addEntity(e);
				}
			}
			if(game.$stage()>1){
				if(game.$prng().$double(0, 1)<0.0025){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new StarfighterTypeC(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					e.setTarget(player);
					ec.addEntity(e);
				}
			}
			if(game.$stage()>2){
				if(game.$prng().$double(0, 1)<0.0025){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new Drone(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					e.setTarget(player);
					e.setSpeedVector(e.directionTo(player), 60);
					ec.addEntity(e);
				}
			}
			if(game.$stage()>3){
				if(game.$prng().$double(0, 1)<0.001){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new StarfighterTypeM(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					e.setTarget(player);
					ec.addEntity(e);
				}
			}
			if(game.$stage()>4){
				if(game.$prng().$double(0, 1)<0.0025){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new StarfighterTypeN(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					e.setTarget(player);
					ec.addEntity(e);
				}
			}
			if(game.$stage()>5){
				if(game.$prng().$double(0, 1)<0.001){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new DroneTypeH(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					e.setTarget(player);
					e.setSpeedVector(e.directionTo(player), 60);
					ec.addEntity(e);
				}
			}
			
			// phase two
			if(game.$stage()>6){
				if(game.$prng().$double(0, 1)<0.002){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new StarfighterTypeI(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					e.setTarget(player);
					ec.addEntity(e);
				}
				if(game.$prng().$double(0, 1)<0.002){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new StarfighterTypeE(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					e.setTarget(player);
					ec.addEntity(e);
				}
			}
			if(game.$stage()>7){
				if(game.$prng().$double(0, 1)<0.001){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new DroneTypeL(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					e.setTarget(player);
					e.setSpeedVector(e.directionTo(player), 60);
					ec.addEntity(e);
				}
			}
			if(game.$stage()>8){
			}
			
			// space race relics
			if(GameFlags.is("lunar")){
				if(lunars==null||!lunars.isActive()){
					lunarsdt-=dt;
					if(lunarsdt<0){
						double t=game.$prng().$double(0, 2*Math.PI);
						lunars=new ApolloServiceModule(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
						lunarsdt=game.$prng().$double(30, 90);
						ec.addEntity(lunars);
					}
				}
				if(lunar==null||!lunar.isActive()){
					lunardt-=dt;
					if(lunardt<0){
						double t=game.$prng().$double(0, 2*Math.PI);
						lunar=new ApolloLunarModule(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
						lunardt=game.$prng().$double(30, 90);
						ec.addEntity(lunar);
					}
				}
				if(vostok==null||!vostok.isActive()){
					vostokdt-=dt;
					if(vostokdt<0){
						double t=game.$prng().$double(0, 2*Math.PI);
						vostok=new VostokOne(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
						vostokdt=game.$prng().$double(30, 90);
						ec.addEntity(vostok);
					}
				}
				if(sputnik==null||!sputnik.isActive()){
					sputnikdt-=dt;
					if(sputnikdt<0){
						double t=game.$prng().$double(0, 2*Math.PI);
						sputnik=new SputnikOne(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
						sputnikdt=game.$prng().$double(30, 90);
						ec.addEntity(sputnik);
					}
				}
				if(voyager==null||!voyager.isActive()){
					voyagerdt-=dt;
					if(voyagerdt<0){
						double t=game.$prng().$double(0, 2*Math.PI);
						voyager=new VoyagerOne(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
						voyagerdt=game.$prng().$double(30, 90);
						ec.addEntity(voyager);
					}
				}
			}
			
			// missile command
			if(GameFlags.is("warhead")){
				if(game.$prng().$double(0, 1)<0.005){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new StarfighterTypeB(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					e.setTarget(player);
					ec.addEntity(e);
				}
				if(game.$prng().$double(0, 1)<0.0025){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new DroneTypeB(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					e.setTarget(player);
					e.setSpeedVector(e.directionTo(player), 60);
					ec.addEntity(e);
				}
			}
			
			// asteroids
			if(GameFlags.is("asteroids")){
				if(game.$prng().$double(0, 1)<0.001){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new Bogey(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					e.setTarget(player);
					ec.addEntity(e);
				}
				if(game.$prng().$double(0, 1)<0.00125){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new DumbBogey(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					e.setTarget(player);
					ec.addEntity(e);
				}
			}
			
			// space invaders
			if(GameFlags.is("invaders")){
				if(game.$prng().$double(0, 1)<0.001){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new SmallAlienFormation(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					//e.setSwornTarget(player);
					ec.addEntity(e);
				}
				if(game.$prng().$double(0, 1)<0.001){
					double t=game.$prng().$double(0, 2*Math.PI);
					StardustEntity e=new Ufo(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
					//e.setSwornTarget(player);
					ec.addEntity(e);
				}
			}
			
			// clash n slash
			if(GameFlags.is("terra")){
				if(game.$prng().$double(0, 1)<0.005){
					double t=game.$prng().$double(0, 2*Math.PI);
					double xx=player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2);
					double yy=player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2);
					StardustEntity e=new ReplicatingMine(game, xx, yy,
							Vector.directionFromTo(xx, yy, player.$x(), player.$y()));
					//e.setSwornTarget(player);
					ec.addEntity(e);
				}
			}
			
			// demonstar
			if(GameFlags.is("demonstar")){
				if(game.$prng().$double(0, 1)<0.0025){
					double tx=game.$camera().$dx()+game.$prng().$double(-220, 220);
					double ty=game.$camera().$dy()-game.$displayHeight()/game.$camera().$zoom()/2;
					ec.addEntity(new MartianFighter(game,tx,ty));
				}
				if(game.$prng().$double(0, 1)<0.0025){
					double tx=game.$camera().$dx()+game.$prng().$double(-220, 220);
					double ty=game.$camera().$dy()-game.$displayHeight()/game.$camera().$zoom()/2;
					ec.addEntity(new MartianFighterTypeT(game,tx,ty));
				}
				if(game.$prng().$double(0, 1)<0.0025){
					double tx=game.$camera().$dx()+game.$prng().$double(-220, 220);
					double ty=game.$camera().$dy()-game.$displayHeight()/game.$camera().$zoom()/2;
					ec.addEntity(new MartianSwarmTypeS(game,tx,ty));
				}
				if(game.$prng().$double(0, 1)<0.005){
					double tx=game.$camera().$dx()+game.$prng().$double(-220, 220);
					double ty=game.$camera().$dy()-game.$displayHeight()/game.$camera().$zoom()/2;
					StardustEntity e=new MartianFighterTypeH(game,tx,ty);
					e.setTarget(player);
					ec.addEntity(e);
				}
				if(game.$prng().$double(0, 1)<0.005){
					double tx=game.$camera().$dx()+game.$prng().$double(-220, 220);
					double ty=game.$camera().$dy()-game.$displayHeight()/game.$camera().$zoom()/2;
					StardustEntity e=new MartianFighterTypeM(game,tx,ty);
					e.setTarget(player);
					ec.addEntity(e);
				}
			}
			
			// gradius
			if(GameFlags.is("gradius")){
				if(game.$prng().$double(0, 1)<0.005){
					double tx=game.$rightScreenEdge()+8;
					double ty=game.$camera().$dy()+game.$prng().$double(-140, 140);
					StardustEntity e=new GradiusFighterTypeE(game,tx,ty);
					e.setTarget(player);
					ec.addEntity(e);
				}
				if(game.$prng().$double(0, 1)<0.0025){
					double tx=game.$rightScreenEdge();
					double ty=game.$camera().$dy()+game.$prng().$double(-140, 140);
					StardustEntity e=new GradiusFighterTypeH(game,tx,ty);
					e.setTarget(player);
					ec.addEntity(e);
				}
				if(game.$prng().$double(0, 1)<0.005){
					double tx=game.$rightScreenEdge();
					double ty=game.$camera().$dy()+game.$prng().$double(-140, 140);
					StardustEntity e=new GradiusFighterTypeM(game,tx,ty);
					ec.addEntity(e);
				}
				if(game.$prng().$double(0, 1)<0.0025){
					double tx=game.$rightScreenEdge()+8;
					double ty=game.$camera().$dy()+game.$prng().$double(-140, 140);
					StardustEntity e=new GradiusDroneSwarm(game,tx,ty);
					ec.addEntity(e);
				}
			}
			
			// spawn debris
			Iterator<StardustEntity> ie=debris.iterator();
			while(ie.hasNext()){
				StardustEntity e=ie.next();
				if(!e.isActive()){
					ie.remove();
				}
			}
			if(debris.size()<32){
				double t=game.$prng().$double(0, 2*Math.PI);
				int ds=game.$prng().$int(-4, 16);
				if(ds<4){
					ds=4;
				}
				if(game.$prng().$double(0, 1)<1.0/20){
					ds=24;
				}
				StardustEntity de=new Asteroid(game,game.$camera().$dx()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),
						game.$camera().$dy()+Vector.vectorToDy(t,StardustGame.BOUNDS/2),
						ds);
				debris.add(de);
				ec.addEntity(de);
			}
		}
		
		
		ec.update(dt);
		sparks.update(dt);
		
		// scoring
		for(StardustEntity e:ec.$lastRemovedEntities()){
			if(e.$killer()==player){
				//double mult=1+(player.$speed()/200);
				//game.addToScore((int)(e.points()*mult));
				game.addToScore(e.points());
			}
		}
		
		game.$camera().centerOnEntity(player, dt);
		
		// auto reset on player death
		if(!player.isActive()){
			//rc.deactivate();
			game.flashRedBorder();
			delay-=dt;
			if(delay<=0){
				if(!GameFlags.is("goto-portal")){
					reset();
				}else{
					State.setCurrentState(-1);
					game.$currentState().reset();
				}
			}
		}
	}

	public void render(Camera c) {
		ec.render(c);
		sparks.render(c);
		
		// render cursor
		//VectorGraphics.renderDotCursor();
		
		// display score
		if(GameFlags.is("score")){
			CharGraphics.drawHeaderString(game.$score(),
					(-game.$displayWidth()/2)+18,
					(-game.$displayHeight()/2)+18,
					1);
			if(Keyboard.isKeyDown(Keyboard.KEY_TAB)){
				CharGraphics.drawHeaderString(game.$hiscore(), //"ЛР лр"+
						(-game.$displayWidth()/2)+18,
						(-game.$displayHeight()/2)+(18*2)+10,
						1);
			}
		}
		
		// flashing warning
		/*
		if(wt/2<0.25){
			// red border
			
			// red warning strings
			CharGraphics.drawRedString("! Опа�?но�?ть !", -140, 24-game.$displayHeight()/2, 2f);
			CharGraphics.drawRedString("! Верна�? Смерть !", -180, game.$displayHeight()/2-48, 2f);
		}
		//*/
		//CharGraphics.drawString(debris.size()+"", 8-game.$displayWidth()/2, 8-game.$displayHeight()/2, 2f);
	}

}
