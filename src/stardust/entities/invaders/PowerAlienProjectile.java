package stardust.entities.invaders;

import engine.sfx.Audio;
import stardust.StardustGame;
import stardust.entities.Power;
import stardust.entities.StardustEntity;

public class PowerAlienProjectile extends Power{

	public PowerAlienProjectile(StardustGame game, double x, double y,
			StardustEntity target) {
		super(game, x, y, target);
		this.setDirection(0);
	}
	
	//private int sfxi=0;
	public boolean usePrimary(StardustEntity owner, double dt){
		cd+=dt;
		if(cd>0.1875){
			Audio.addSoundEffect("fire-bogey", 0.625f);
			// sfx
			/*
			if(sfxi<1) {
				Audio.playSoundEffect("invaders-a", 0.625f, -1);
			} else if(sfxi<2) {
				Audio.playSoundEffect("invaders-b", 0.625f, -1);
			} else if(sfxi<3) {
				Audio.playSoundEffect("invaders-c", 0.625f, -1);
			} else {
				Audio.playSoundEffect("invaders-d", 0.625f, -1);
			}
			sfxi+=1;
			sfxi%=4;
			//*/
			
			AlienProjectile e=new AlienProjectile(game, owner.$t(), owner);
			game.$currentState().addEntity(e);
			cd=0;
			ammo--;
			return true;
		}
		return false;
	}
}
