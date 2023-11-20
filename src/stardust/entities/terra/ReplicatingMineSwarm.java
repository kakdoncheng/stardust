package stardust.entities.terra;

import stardust.StardustGame;
import stardust.entities.StardustEntity;
import engine.Vector;
import engine.gfx.Camera;

public class ReplicatingMineSwarm extends StardustEntity{

	public ReplicatingMineSwarm(StardustGame game, double x, double y) {
		super(game);
		this.setXY(x, y);
		this.setBoundRadius(0);
		this.setDirection(0);
	}

	private int ammo=2;
	private double cooldown=0;
	private double delay=1;
	
	public void update(double dt) {
		if(delay>0){
			delay-=dt;
			return;
		}
		
		cooldown+=dt;
		if(cooldown>0.05 && ammo>0){
			double tt=game.$prng().$double(0, 2*Math.PI);
			double ddx=game.$prng().$double(0, 16);
			game.$currentState().addEntity(new ReplicatingMine(game, x+Vector.vectorToDx(tt, ddx), y+Vector.vectorToDy(tt, ddx), tt));
			cooldown=0;
			ammo--;
		}
		if(ammo<1){
			deactivate();
		}
	}

	public void render(Camera c) {
		
	}


	public boolean isCollidable(){
		return false;
	}
	
	public void onDeath() {
		
	}

}
