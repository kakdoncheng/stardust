package stardust.unused;

import stardust.StardustGame;
import stardust.entities.StardustEntity;
import stardust.entities.gradius.GradiusFighterTypeE;
import stardust.entities.gradius.GradiusShip;
import engine.gfx.Camera;

public class GradiusSwarmTypeE extends GradiusShip{

	public GradiusSwarmTypeE(StardustGame game, double x, double y) {
		super(game);
		this.setXY(x, y);
		this.setBoundRadius(0);
		this.setDirection(0);
	}

	private int ammo=4;
	private double cooldown=0;
	
	public void update(double dt) {
		cooldown+=dt;
		if(cooldown>0.2 && ammo>0){
			StardustEntity e=new GradiusFighterTypeE(game,x,y);
			e.setTarget(target);
			game.$currentState().addEntity(e);
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
