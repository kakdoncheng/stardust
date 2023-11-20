package stardust.states;

import java.util.ArrayList;
import java.util.Iterator;

import stardust.StardustGame;
import stardust.entities.AntiMatterExplosion;
import stardust.entities.Asteroid;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.PlayerStarfighter;
import stardust.entities.StardustEntity;
import stardust.entities.boss.Sinistar;
import stardust.entities.sinistar.Servitor;
import stardust.entities.sinistar.Sigil;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.State;
import engine.Vector;
import engine.entities.Point;
import engine.gfx.Camera;

public class SinistarState extends StardustState{
	
	public SinistarState(StardustGame game) {
		super(game);
		reset();
	}

	private String bname="The Invincible Star";
	private char[] dis;
	private double disT, timer;
	private int disi;

	private Point point;
	private Sinistar boss;
	private StardustEntity player;
	private StardustEntity sigil;
	private ArrayList<StardustEntity> debris;
	
	private double dx, dy;
	private double bossT, spawned, sSpawned;
	private double delay;

	public void reset(){
		GameFlags.setFlag("warp", 1);
		clearBackgroundText();
		ec.clear();
		ec.setRenderDistance(StardustGame.BOUNDS);
		sparks.clear();
		sparks.setRenderDistance(StardustGame.BOUNDS);
		//
		
		debris=new ArrayList<StardustEntity>();
		
		dis="*.*****           ".toCharArray();
		timer=0;
		disT=0;
		disi=0;
		bossT=0;
		spawned=0;
		sSpawned=0;
		delay=1.5;
		
		// spawn player
		player=new PlayerStarfighter(game, 0, 0);
		game.$camera().hardCenterOnPoint(0, 0);
		ec.addEntity(player);
		
		// boss xy
		boss=null;
		sigil=null;
		double dlim=600.0;//Math.min(game.$displayWidth()/3, game.$displayHeight()/3)/game.$camera().$zoom();
		double t=game.$prng().$double(0, 2*Math.PI);
		dx=Vector.vectorToDx(t, dlim);
		dy=Vector.vectorToDy(t, dlim);
		point=new Point(game, player.$x()+dx, player.$y()+dy);
	}

	public void update(double dt) {

		// camera tracking
		if(bossT<4){
			game.$camera().centerOnEntity(point, dt);
		}else{
			if(player!=null){
				if(boss!=null){
					game.$camera().centerOnEntity(boss.$health()>0?player:boss, dt);
				}else{
					game.$camera().centerOnEntity(player, dt);
				}
			}
		}
		
		// score text
		if(timer<6){
			timer+=dt;
		}else{
			disT+=dt;
			if(disT>=0.125&&disi<bname.length()){
				disT-=0.125;
				dis[disi]=bname.charAt(disi);
				disi++;
			}
		}
		
		bossT+=dt;
		if(bossT>0.25&&sSpawned<1){
			//spawn sigil
			sigil=new Sigil(game, player.$x()+dx, player.$y()+dy);
			ec.addEntity(sigil);
			sSpawned=1;
		}
		if(bossT>3&&spawned<1){
			//spawn boss
			boss=new Sinistar(game, player.$x()+dx, player.$y()+dy);
			boss.setTarget(player);
			ec.addEntity(boss);
			sigil.deactivate();
			//double ci=2*Math.PI;
			//double cis=ci/256;
			//for(double i=0;i<ci-cis;i+=cis){
			//	game.$currentState().addEntity(new RadiantProjectile(game, i, boss));
			//}
			StardustEntity pulse=new AntiMatterExplosion(game, boss.$x(), boss.$y(), boss);
			ec.addEntity(pulse);
			spawned=1;
		}
		
		// spawn servants
		if(game.$prng().$double(0, 1)<0.005){
			double t=game.$prng().$double(0, 2*Math.PI);
			StardustEntity e=new Servitor(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
			e.setTarget(player);
			ec.addEntity(e);
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
		
		ec.update(dt);
		sparks.update(dt);
		
		// check end condition
		if(spawned>0){
			if((boss!=null && boss.$health()<1) || !player.isActive()){
				delay-=dt;
				if(!player.isActive()){
					game.flashRedBorder();
					if(delay>1){
						delay=1;
					}
				}
				if(delay<=0){
					if(player.isActive()){
						GameFlags.setFlag("success", 1);
						GameFlags.setFlag("player-x", (int)player.$x());
						GameFlags.setFlag("player-y", (int)player.$y());
						State.setCurrentState(0);
						game.$currentState().reset();
						game.$currentState().addEntity(new ElectromagneticPulse(game,boss.$x(),boss.$y()));
					}else{
						if(!GameFlags.is("goto-portal")){
							State.setCurrentState(0);
						}else{
							State.setCurrentState(-1);
						}
						game.$currentState().reset();
					}
				}
			}
		}
		
		
	}

	public void render(Camera c) {
		
		ec.render(c);
		sparks.render(c);
		
		String s="";
		for(char ch:dis){
			if(ch=='*'){
				s+=game.$prng().$string(1);
			}else{
				s+=ch+"";
			}
		}
		
		if(GameFlags.is("score")){
			CharGraphics.drawHeaderString(String.format("%s",s),
					(-game.$displayWidth()/2)+18,
					(-game.$displayHeight()/2)+18,
					1);
		}
	}
}
