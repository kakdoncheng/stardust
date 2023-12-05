package stardust.states;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.Explosion;
import stardust.entities.IndicatorDangerUp;
import stardust.entities.Spark;
import stardust.entities.StardustEntity;
import stardust.entities.boss.Demonstar;
import stardust.entities.demonstar.PlayerSpaceship;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.State;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class DemonstarBossState extends StardustState{

	public DemonstarBossState(StardustGame game) {
		super(game);
		reset();
	}

	// internal flags/var
	private String bname="Prototype Dæmonstar";
	private char[] dis;
	private double disT;//, timer;
	private int disi;
	private double timer;
	private double delay;
	
	// entities
	private Demonstar boss;
	private PlayerSpaceship player;
	private double bossT, spawned;
	
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
		targetable.clear();
		targetable.setRenderDistance(StardustGame.BOUNDS);
		particles.clear();
		particles.setRenderDistance(StardustGame.BOUNDS);
		
		dis="*.*****                  ".toCharArray();
		timer=0;
		disT=0;
		disi=0;
		bossT=0;
		spawned=0;
		delay=1.5;
		
		boss=null;
		player=new PlayerSpaceship(game, GameFlags.valueOf("player-x"), GameFlags.valueOf("player-y"));
		player.stopInvt();
		targetable.addEntity(player);
		
		double dty=24+game.$topScreenEdge();
		StardustEntity e=new IndicatorDangerUp(game,0,dty);
		targetable.addEntity(e);
	}

	public void update(double dt) {
		// check end condition
		if((boss!=null && boss.$health()<1) || !player.isActive()){
			delay-=dt;
			if(!player.isActive()){
				Audio.clearBackgroundMusicQueue();
				Audio.clearBackgroundMusic();
				game.flashRedBorder();
				if(delay>1){
					delay=1;
				}
			}
			if(delay<=0){
				if(player.isActive()){
					Audio.clearBackgroundMusicQueue();
					Audio.clearBackgroundMusic();
					GameFlags.markFlag("demonstar");
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

		if(boss!=null && boss.$health()<1){
			game.$camera().centerOnEntity(boss, dt);
		}else{
			// move camera
			double dcy=-180*dt;
			game.$camera().dxy(0, dcy);
			for(StardustEntity e:targetable.$entities()){
				e.setXY(e.$x(), e.$y()+dcy);
			}
		}
		
		if(timer<3){
			timer+=dt;
		}else{
			disT+=dt;
			if(disT>=0.125&&disi<bname.length()){
				disT-=0.125;
				dis[disi]=bname.charAt(disi);
				disi++;
			}
		}
		
		// spawn boss
		bossT+=dt;
		if(bossT>6&&spawned<1){
			boss=new Demonstar(game,0,game.$topScreenEdge()-48);
			boss.setTarget(player);
			targetable.addEntity(boss);
			//ec.addEntity(new FlashingDestroyIndicator(game, boss, true));
			//StardustEntity pulse=new ElectromagneticPulse(game,boss.$x(),boss.$y());
			//ec.addEntity(pulse);
			spawned=1;
		}
		
		// set new state/reset
		// see ceraphim state for reference
		
		targetable.update(dt);
		particles.update(dt);
	}

	public void render(Camera c) {
		
		targetable.render(c);
		particles.render(c);
		
		if(GameFlags.is("score")){
			String s="";
			for(char ch:dis){
				if(ch=='*'){
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
