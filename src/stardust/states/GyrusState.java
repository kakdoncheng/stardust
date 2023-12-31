package stardust.states;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.IndicatorDestroy;
import stardust.entities.RadarScan;
import stardust.entities.StardustEntity;
import stardust.entities.gradius.GradiusShip;
import stardust.entities.gyrus.GyrusAsteroid;
import stardust.entities.gyrus.GyrusFighter;
import stardust.entities.gyrus.GyrusPlayerSpaceship;
import stardust.entities.gyrus.GyrusShip;
import stardust.entities.gyrus.GyrusStardust;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.State;
import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class GyrusState extends StardustState{

	public GyrusState(StardustGame game) {
		super(game);
		reset();
	}

	// internal flags/var
	private double bgT;
	private double delay;
	private double tt;
	private boolean tagged;
	private int kills;
	private char[] dis;
	private boolean bgmClear;
	
	// entities
	private RadarScan rc;
	private StardustEntity player;
	
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
		tt=0;
		kills=0;
		tagged=false;
		bgmClear=false;
		
		dis=String.format("0.%05d", kills).toCharArray();
		
		rc=new RadarScan(game, 0, 0);
		player=new GyrusPlayerSpaceship(game);

		targetable.addEntity(player);
		//ec.addEntity(new ElectromagneticPulse(game,0,0));
		for(int i=0;i<64;i++){
			targetable.addEntity(new GyrusStardust(game, game.$prng().$double(1, 20)));
		}
		for(int i=0;i<128;i++){
			targetable.addEntity(new GyrusStardust(game, game.$prng().$double(1, 160)));
		}
	}

	public void update(double dt) {
		game.hideStardust();
		if(bgT<0.5){
			bgT+=dt;
			if(!bgmClear) {
				// dynamically load music here?
				Audio.clearBackgroundMusicQueue();
				Audio.clearBackgroundMusic();
				bgmClear=true;
			}
			
			updateBackgroundText();
			return;
		}
		if(bgmClear) {
			Audio.queueBackgroundMusic("retro-synthwave-short-version-176294/loop");
			bgmClear=false;
		}
		
		tt+=4*Math.PI*dt;
		tt=Vector.constrainTheta(tt);
		
		// spawn stardust
		if(game.$prng().$double(0, 1)<0.8){
			targetable.addEntity(new GyrusStardust(game));
		}
		
		// spawn hostiles
		if(kills<20){
			if(game.$prng().$double(0, 1)<0.05){
				targetable.addEntity(new GyrusAsteroid(game));
			}
			if(game.$prng().$double(0, 1)<0.025){
				targetable.addEntity(new GyrusFighter(game));
			}
			//if(game.$prng().$double(0, 1)<0.01){
			//	ec.addEntity(new GyrusBogey(game));
			//}
		}else{
			if(!tagged){
				for(StardustEntity e:targetable.$entities()){
					if(e instanceof GradiusShip){
						targetable.addEntity(new IndicatorDestroy(game, e));
					}
				}
				tagged=true;
			}
		}
		
		// count kills
		// get xy of last enemy removed
		double lx=0;
		double ly=0;
		for(StardustEntity e:targetable.$lastRemovedEntities()){
			if(!(e instanceof GyrusShip)){
				continue;
			}
			if(e.$killer()!=null && e.$killer().equals(player)){
				kills++;
				dis=String.format("0.%05d", kills).toCharArray();
				lx=e.$x();
				ly=e.$y();
			}
			
		}
		
		// check end condition
		if(!player.isActive() || kills>=10){
			rc.deactivate();
			if(player.isActive()){
				// win
				// set game flags player xy & success
				// set stage back to endless
				GameFlags.markFlag("gyruss");
				GameFlags.setFlag("success", 1);
				GameFlags.setFlag("player-x", (int)player.$x());
				GameFlags.setFlag("player-y", (int)player.$y());
				State.setCurrentState(0);
				game.$currentState().reset();
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
		if(rc.isActive()){
			rc.update(dt);
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
		if(rc.isActive()){
			rc.render(c);
		}
		
		// render origin star
		double rr=2;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslated(c.$cx(0), c.$cy(0), 0);
		GL11.glRotated(Math.toDegrees(tt), 0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);
		StardustEntity.setActualRadarColor(0.25);
		GL11.glVertex2d(-rr/2, -rr/2);
		GL11.glVertex2d(-rr/2, rr/2);
		GL11.glVertex2d(rr/2, rr/2);
		GL11.glVertex2d(rr/2, -rr/2);
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
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
	}

}
