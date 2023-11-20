package stardust.entities;

import stardust.StardustGame;
import stardust.entities.boss.LascannonBeam;

public class PowerLascannon extends Power{

	public PowerLascannon(StardustGame game, double x, double y,
			StardustEntity target) {
		super(game, x, y, target);
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
