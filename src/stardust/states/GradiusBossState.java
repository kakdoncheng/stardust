package stardust.states;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.IndicatorDangerRight;
import stardust.entities.StardustEntity;
import stardust.entities.boss.PossessedMachine;
import stardust.entities.gradius.PlayerSpacecraft;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.State;
import engine.gfx.Camera;

public class GradiusBossState extends StardustState{

	public GradiusBossState(StardustGame game) {
		super(game);
		reset();
	}

	// internal flags/var
	private String bname="Baelruul, the Obiliterator";
	private char[] dis;
	private double disT;//, timer;
	private int disi;
	private double timer;
	private double delay;
	
	// entities
	private PossessedMachine boss;
	private PlayerSpacecraft player;
	private double bossT, spawned;
	
	public void reset() {
		GameFlags.setFlag("warp", 1);
		ec.clear();
		ec.setRenderDistance(StardustGame.BOUNDS);
		sparks.clear();
		sparks.setRenderDistance(StardustGame.BOUNDS);
		
		dis="*.*****                                ".toCharArray();
		timer=0;
		disT=0;
		disi=0;
		bossT=0;
		spawned=0;
		delay=1.5;
		
		boss=null;
		player=new PlayerSpacecraft(game, GameFlags.valueOf("player-x"), GameFlags.valueOf("player-y"));
		player.stopInvt();
		ec.addEntity(player);
		
		double dtx=game.$rightScreenEdge()-48;
		StardustEntity e=new IndicatorDangerRight(game,dtx,0);
		ec.addEntity(e);
	}

	public void update(double dt) {
		// check end condition
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
					GameFlags.markFlag("gradius");
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
			double dcx=180*dt;
			game.$camera().dxy(dcx, 0);
			for(StardustEntity e:ec.$entities()){
				e.setXY(e.$x()+dcx, e.$y());
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
			boss=new PossessedMachine(game,game.$rightScreenEdge(),0);
			boss.setTarget(player);
			ec.addEntity(boss);
			//ec.addEntity(new FlashingDestroyIndicator(game, boss, true));
			//StardustEntity pulse=new ElectromagneticPulse(game,boss.$x(),boss.$y());
			//ec.addEntity(pulse);
			spawned=1;
		}
		
		// set new state/reset
		// see ceraphim state for reference
		
		ec.update(dt);
		sparks.update(dt);
	}

	public void render(Camera c) {
		
		ec.render(c);
		sparks.render(c);
		
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
