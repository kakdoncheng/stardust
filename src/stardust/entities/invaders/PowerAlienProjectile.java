package stardust.entities.invaders;

import stardust.StardustGame;
import stardust.entities.AntiMatterBomb;
import stardust.entities.Power;
import stardust.entities.StardustEntity;
import stardust.entities.boss.LascannonBeam;

public class PowerAlienProjectile extends Power{

	public PowerAlienProjectile(StardustGame game, double x, double y,
			StardustEntity target) {
		super(game, x, y, target);
		ammo=90;
	}
	public boolean usePrimary(StardustEntity owner, double dt){
		cd+=dt;
		if(cd>0.125){
			LascannonBeam e=new LascannonBeam(game, owner.$x(),owner.$y(),owner.$t(), owner);
			e.offsetTR(owner.$t(), 4);
			e.setOXY(e.$x(), e.$y());
			game.$currentState().addEntity(e);
			cd=0;
			ammo--;
			return true;
		}
		return false;
	}
	public void useSecondary(StardustEntity owner){
		ammo=0;
		game.$currentState().addEntity(new AntiMatterBomb(game, owner.$t(), owner));
	}
}
