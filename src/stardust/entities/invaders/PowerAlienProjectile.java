package stardust.entities.invaders;

import stardust.StardustGame;
import stardust.entities.Power;
import stardust.entities.StardustEntity;

public class PowerAlienProjectile extends Power{

	public PowerAlienProjectile(StardustGame game, double x, double y,
			StardustEntity target) {
		super(game, x, y, target);
		this.setDirection(0);
	}
	public boolean usePrimary(StardustEntity owner, double dt){
		cd+=dt;
		if(cd>0.1875){
			AlienProjectile e=new AlienProjectile(game, owner.$t(), owner);
			game.$currentState().addEntity(e);
			cd=0;
			ammo--;
			return true;
		}
		return false;
	}
}
