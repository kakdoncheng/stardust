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

public class AsteroidsState extends StardustState{

	public AsteroidsState(StardustGame game) {
		super(game);
		reset();
	}

	// internal flags/var
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
		ec.clear();
		ec.setRenderDistance(StardustGame.BOUNDS);
		sparks.clear();
		sparks.setRenderDistance(StardustGame.BOUNDS);
		game.$camera().hardCenterOnPoint(0, 0);
		bgT=0;
		delay=1;
		tagged=false;
		
		bogey=null;
		bogeydt=8;
		dbogey=null;
		dbogeydt=4;
		
		player=new ClassicPlayerSpaceship(game, 0, 0);
		ec.addEntity(player);
		
		//spawn asteroids
		int dlim=320;
		for(int i=0;i<4;i++){
			double t=game.$prng().$double(0, 2*Math.PI);
			StardustEntity de=new ClassicAsteroid(game,
					player.$x()+Vector.vectorToDx(t,dlim),
					player.$y()+Vector.vectorToDy(t,dlim),
					24);
			ec.addEntity(de);
		}
	}

	public void update(double dt) {
		if(bgT<0.5){
			bgT+=dt;
			updateBackgroundText();
			game.hideStardust();
			return;
		}
		
		// spawn bogeys
		if(dbogey==null||!dbogey.isActive()){
			dbogeydt-=dt;
			if(dbogeydt<0){
				double t=game.$prng().$double(0, 2*Math.PI);
				dbogey=new DumbBogey(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
				dbogeydt=game.$prng().$double(4, 16);
				dbogey.setTarget(player);
				ec.addEntity(dbogey);
			}
		}
		if(bogey==null||!bogey.isActive()){
			bogeydt-=dt;
			if(bogeydt<0){
				double t=game.$prng().$double(0, 2*Math.PI);
				bogey=new Bogey(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
				bogeydt=game.$prng().$double(8, 32);
				bogey.setTarget(player);
				ec.addEntity(bogey);
			}
		}
		
		ec.update(dt);
		sparks.update(dt);
		
		// tag last asteroid
		if(ec.$sizeOf(ClassicAsteroid.class)>1 && tagged){
			tagged=false;
		}
		if(ec.$sizeOf(ClassicAsteroid.class)<2 && !tagged){
			for(StardustEntity e:ec.$entities()){
				if(e instanceof ClassicAsteroid){
					ec.addEntity(new IndicatorDestroy(game, e));
				}
			}
			tagged=true;
		}
		
		// get xy of last asteroid removed
		double lx=0;
		double ly=0;
		for(StardustEntity e:ec.$lastRemovedEntities()){
			if(e instanceof ClassicAsteroid){
				lx=e.$x();
				ly=e.$y();
			}
		}
		
		// check end condition
		if(!player.isActive() || ec.$sizeOf(ClassicAsteroid.class)<1){
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
		
		ec.render(c);
		sparks.render(c);
		
		if(GameFlags.is("score")){
			CharGraphics.drawHeaderString(game.$obfScore(),
					(-game.$displayWidth()/2)+18,
					(-game.$displayHeight()/2)+18,
					1);
		}
		//CharGraphics.drawString(ec.$sizeOf(ClassicAsteroid.class)+"", -game.$displayWidth()/2, +game.$displayHeight()/2-14, 1f);
	}

}
