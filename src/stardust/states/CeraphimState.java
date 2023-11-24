package stardust.states;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.PlayerStarfighter;
import stardust.entities.StardustEntity;
import stardust.entities.boss.Ceraphim;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.State;
import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class CeraphimState extends StardustState{
	
	public CeraphimState(StardustGame game) {
		super(game);
		reset();
	}

	private String bname="Ceraphim, the End of the World";
	private char[] dis;
	private double disT, timer, bgT;
	private int disi;
	private boolean bgmClear;

	private Ceraphim boss;
	private StardustEntity player;
	private double bossT, spawned;
	private double delay;

	public void reset(){
		GameFlags.setFlag("warp", 1);
		clearBackgroundText();
		targetable.clear();
		targetable.setRenderDistance(StardustGame.BOUNDS);
		particles.clear();
		particles.setRenderDistance(StardustGame.BOUNDS);
		game.$camera().hardCenterOnPoint(0, 0);
		bgmClear=false;
		
		dis="*.*****                                 ".toCharArray();
		bgT=0;
		timer=0;
		disT=0;
		disi=0;
		bossT=0;
		spawned=0;
		delay=1.5;
		
		//spawn player
		boss=null;
		player=new PlayerStarfighter(game,0,0);
		targetable.addEntity(player);
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
			// if statement below is deprecated by
			//this.playRandomBGMSequence();
			double bgmi=game.$prng().$double(0, 1);
			if(bgmi<0.2) {
				Audio.queueBackgroundMusic("night-city-knight-127028/2-loop-2");
			}else if(bgmi<0.4) {
				Audio.queueBackgroundMusic("night-city-knight-127028/4-loop-2");
			}else if(bgmi<0.6) {
				Audio.queueBackgroundMusic("night-city-knight-127028/3-intro");
			}else if(bgmi<0.8) {
				Audio.queueBackgroundMusic("night-city-knight-127028/4-loop-1");
			}else {
				Audio.queueBackgroundMusic("night-city-knight-127028/4-loop-3");
			}
			bgmClear=false;
		}
		
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

		if(player!=null){
			if(boss!=null){
				game.$camera().centerOnEntity(boss.$health()>0?player:boss, dt);
			}else{
				game.$camera().centerOnEntity(player, dt);
			}
		}
		
		if(timer<4){
			timer+=dt;
			if(timer%1>=0.5){
				//game.flashRedWarning();
			}
		}else{
			disT+=dt;
			if(disT>=0.125&&disi<bname.length()){
				disT-=0.125;
				dis[disi]=bname.charAt(disi);
				disi++;
			}
		}
		
		bossT+=dt;
		if(bossT>8&&spawned<1){
			//spawn boss
			double dlim=Math.min(game.$displayWidth()/3, game.$displayHeight()/3)/game.$camera().$zoom();
			double t=game.$prng().$double(0, 2*Math.PI);
			double dx=Vector.vectorToDx(t, dlim);
			double dy=Vector.vectorToDy(t, dlim);
			boss=new Ceraphim(game,player.$x()+dx,player.$y()+dy);
			boss.setTarget(player);
			targetable.addEntity(boss);
			//ec.addEntity(new FlashingDestroyIndicator(game, boss, true));
			//double ci=2*Math.PI;
			//double cis=ci/256;
			//for(double i=0;i<ci-cis;i+=cis){
			//	game.$currentState().addEntity(new RadiantProjectile(game, i, boss));
			//}
			
			StardustEntity pulse=new ElectromagneticPulse(game,boss.$x(),boss.$y());
			targetable.addEntity(pulse);
			spawned=1;
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