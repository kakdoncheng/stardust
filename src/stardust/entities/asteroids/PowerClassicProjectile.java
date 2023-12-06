package stardust.entities.asteroids;

import stardust.StardustGame;
import stardust.entities.Power;
import stardust.entities.StardustEntity;
import stardust.states.EndlessState;

public class PowerClassicProjectile extends Power{

	public PowerClassicProjectile(StardustGame game, double x, double y,
			StardustEntity target) {
		super(game, x, y, target);
		ammo=120;
		wild=game.$currentState() instanceof EndlessState;
	}
	public boolean usePrimary(StardustEntity owner, double dt){
		cd+=dt;
		if(cd>0.05){
			ClassicProjectile cp=new ClassicProjectile(game, owner.$t(), owner);
			cp.applyAccelerationVector(owner.$speedt(), owner.$speed(), 1);
			game.$currentState().addEntity(cp);
			cp=new ClassicProjectile(game, owner.$t()+game.$prng().$double(-0.125, 0.125), owner);
			cp.applyAccelerationVector(owner.$speedt(), owner.$speed(), 1);
			game.$currentState().addEntity(cp);
			cd=0;
			ammo--;
			return true;
		}
		return false;
	}
	
	private boolean wild;
	
	// override methods
	public void update(double dt) {
		//blip();
		updateBlip(dt);
		rt+=Math.PI*dt;
		
		if(target!=null && distanceTo(target)<120){
			rotateTowards(target, 3*Math.PI, dt);
			setSpeedVector(t, speed+(120-distanceTo(target)));
		}else{
			setSpeedVector(t, speed);
		}
		
		updatePosition(dt);
		if(wild) {
			deactivateIfOutOfBounds();
		} else {
			wraparoundIfOutOfScreenBounds();
		}
		//
	}
	
	public String toString() {
		return "Heavy Blaster";
	}
}
