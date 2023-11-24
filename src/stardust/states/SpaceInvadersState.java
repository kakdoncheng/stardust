package stardust.states;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.IndicatorDestroy;
import stardust.entities.StardustEntity;
import stardust.entities.invaders.AlienFormation;
import stardust.entities.invaders.ClassicAlienFormation;
import stardust.entities.invaders.PlayerCannon;
import stardust.entities.invaders.Ufo;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.State;
import engine.gfx.Camera;

public class SpaceInvadersState extends StardustState{

	public SpaceInvadersState(StardustGame game) {
		super(game);
		reset();
	}

	// internal flags/var
	private double ufodt;
	private double delay;
	private double bgT;
	private boolean tagged;
	//private double adl;
	
	// entities
	private StardustEntity ufo;
	private AlienFormation swarm;
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
		
		tagged=false;
		ufo=null;
		ufodt=4;
		//adl=128;
		delay=1;
		
		player=new PlayerCannon(game, 0, 144);
		swarm=new ClassicAlienFormation(game,0,-100);
		
		swarm.setTarget(player);
		
		targetable.addEntity(player);
		targetable.addEntity(swarm);
	}

	public void update(double dt) {
		if(bgT<0.5){
			bgT+=dt;
			updateBackgroundText();
			game.hideStardust();
			return;
		}
		
		// spawn ufo
		if(ufo==null||!ufo.isActive()){
			ufodt-=dt;
			if(ufodt<0){
				if(game.$prng().$double(0, 1)>0.5){
					ufo=new Ufo(game,-game.$displayWidth()/game.$camera().$zoom()/2,-144);
					ufo.setSpeedVector(Math.PI*1.5, 80);
				}else{
					ufo=new Ufo(game,game.$displayWidth()/game.$camera().$zoom()/2,-144);
					ufo.setSpeedVector(Math.PI*0.5, 80);
				}
				
				ufodt=game.$prng().$double(1, 4);
				ufo.setTarget(player);
				targetable.addEntity(ufo);
			}
		}
		
		targetable.update(dt);
		particles.update(dt);
		
		// tag last alien
		if(swarm.$lastAlien()!=null&&!tagged){
			targetable.addEntity(new IndicatorDestroy(game, swarm.$lastAlien()));
			tagged=true;
		}
		
		// check end condition
		if(!player.isActive() || swarm.$killedLastAlien()!=null){
			if(player.isActive()){
				// win
				// set game flags player xy & success
				// set stage back to endless
				GameFlags.markFlag("invaders");
				GameFlags.setFlag("success", 1);
				GameFlags.setFlag("player-x", (int)player.$x());
				GameFlags.setFlag("player-y", (int)player.$y());
				State.setCurrentState(0);
				game.$currentState().reset();
				game.$currentState().addEntity(new ElectromagneticPulse(game,swarm.$killedLastAlien().$x(),swarm.$killedLastAlien().$y()));
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
		
		//game.$camera().centerOnEntity(player, dt);
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
		
		/*
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1,0,0,0.25);
		GL11.glVertex2d(c.$cx(adl), c.$cy(144));
		GL11.glVertex2d(c.$cx(-adl), c.$cy(144));
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//*/
		
		//VectorGraphics.renderDotCursor();
		
		if(GameFlags.is("score")){
			CharGraphics.drawHeaderString(game.$obfScore(),
					(-game.$displayWidth()/2)+18,
					(-game.$displayHeight()/2)+18,
					1);
		}
	}

}
