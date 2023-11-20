package stardust.entities.silo;

import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;
import stardust.StardustGame;
import stardust.entities.StardustEntity;

public class AnnihilatingExplosionCluster extends StardustEntity{

	public AnnihilatingExplosionCluster(StardustGame game, double x, double y, StardustEntity owner, int amt) {
		super(game);
		setXY(x, y);
		setBoundRadius(0);
		setDirection(0);
		this.owner=owner;
		this.amt=amt;
		Audio.addSoundEffect("explosion-nuke", 1);
	}
	
	private StardustEntity owner;
	private int amt;
	private double cdt=0;

	public void update(double dt) {
		if(amt<1){
			deactivate();
		}
		cdt-=dt;
		if(cdt<=0){
			double dist=game.$prng().$double(0, 32);
			double dth=game.$prng().$double(0, Math.PI*2);
			game.$currentState().addEntity(new AnnihilatingExplosion(game,
					x+Vector.vectorToDx(dth, dist),
					y+Vector.vectorToDy(dth, dist),
					game.$prng().$int(8, 17),owner,true));
			cdt=1.0/30;
			amt--;
		}
	}

	public void render(Camera c) {
		
	}

	public void onDeath() {
		
	}
	
	public boolean isCollidable(){
		return false;
	}

}
