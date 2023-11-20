package stardust.entities;

import stardust.StardustGame;

public class PowerBulletHail extends Power{

	public PowerBulletHail(StardustGame game, double x, double y,
			StardustEntity target) {
		super(game, x, y, target);
	}
	public boolean usePrimary(StardustEntity owner, double dt){
		cd+=dt;
		if(cd>0.1){
			for(int i=0;i<3;i++){
				game.$currentState().addEntity(new LeadProjectile(game, owner.$t()+game.$prng().$double(-0.125*i, 0.125*i), owner));
			}
			cd=0;
			ammo--;
			return true;
		}
		return false;
	}
	public void useSecondary(StardustEntity owner){
		for(int i=0;i<ammo*2;i++){
			game.$currentState().addEntity(new LeadProjectile(game, owner.$t()+game.$prng().$double(-Math.PI/4, Math.PI/4), owner));
		}
		ammo=0;
	}
}
