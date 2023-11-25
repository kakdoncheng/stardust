package stardust.states;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.IndicatorDestroy;
import stardust.entities.StardustEntity;
import stardust.entities.asteroids.Bogey;
import stardust.entities.asteroids.ClassicAsteroid;
import stardust.entities.asteroids.ClassicPlayerSpaceship;
import stardust.entities.asteroids.DumbBogey;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.State;
import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class AsteroidsState extends StardustState{

	public AsteroidsState(StardustGame game) {
		super(game);
		reset();
	}

	// internal flags/var
	private boolean bgmClear;
	private double delay;
	private double bogeydt;
	private double dbogeydt;
	private double bgT;
	private boolean tagged;
	
	// entities
	private StardustEntity player;
	private StardustEntity bogey;
	private StardustEntity dbogey;
	
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
		
		bogey=null;
		bogeydt=8;
		dbogey=null;
		dbogeydt=4;
		
		player=new ClassicPlayerSpaceship(game, 0, 0);
		targetable.addEntity(player);
		
		//spawn asteroids
		int dlim=320;
		for(int i=0;i<4;i++){
			double t=game.$prng().$double(0, 2*Math.PI);
			StardustEntity de=new ClassicAsteroid(game,
					player.$x()+Vector.vectorToDx(t,dlim),
					player.$y()+Vector.vectorToDy(t,dlim),
					24);
			targetable.addEntity(de);
		}
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
			double rnd=game.$prng().$double(0, 1);
			if(rnd<0.5) {
				Audio.queueBackgroundMusic("synthwave-background-music-155701/intro-1");
				Audio.queueBackgroundMusic("synthwave-background-music-155701/loop");
			} else {
				Audio.queueBackgroundMusic("synthwave-background-music-155701/intro-2");
				Audio.queueBackgroundMusic("synthwave-background-music-155701/loop");
			}
			bgmClear=false;
		}
		
		// spawn bogeys
		if(dbogey==null||!dbogey.isActive()){
			dbogeydt-=dt;
			if(dbogeydt<0){
				double t=game.$prng().$double(0, 2*Math.PI);
				dbogey=new DumbBogey(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
				dbogeydt=game.$prng().$double(4, 16);
				dbogey.setTarget(player);
				targetable.addEntity(dbogey);
			}
		}
		if(bogey==null||!bogey.isActive()){
			bogeydt-=dt;
			if(bogeydt<0){
				double t=game.$prng().$double(0, 2*Math.PI);
				bogey=new Bogey(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
				bogeydt=game.$prng().$double(8, 32);
				bogey.setTarget(player);
				targetable.addEntity(bogey);
			}
		}
		
		targetable.update(dt);
		particles.update(dt);
		
		// tag last asteroid
		if(targetable.$sizeOf(ClassicAsteroid.class)>1 && tagged){
			tagged=false;
		}
		if(targetable.$sizeOf(ClassicAsteroid.class)<2 && !tagged){
			for(StardustEntity e:targetable.$entities()){
				if(e instanceof ClassicAsteroid){
					targetable.addEntity(new IndicatorDestroy(game, e));
				}
			}
			tagged=true;
		}
		
		// get xy of last asteroid removed
		double lx=0;
		double ly=0;
		for(StardustEntity e:targetable.$lastRemovedEntities()){
			if(e instanceof ClassicAsteroid){
				lx=e.$x();
				ly=e.$y();
			}
		}
		
		// check end condition
		if(!player.isActive() || targetable.$sizeOf(ClassicAsteroid.class)<1){
			if(player.isActive()){
				// win
				// set game flags player xy & success
				// set stage back to endless
				GameFlags.markFlag("asteroids");
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
		//CharGraphics.drawString(ec.$sizeOf(ClassicAsteroid.class)+"", -game.$displayWidth()/2, +game.$displayHeight()/2-14, 1f);
	}

}
