package stardust.entities.gradius;

import stardust.StardustGame;
import engine.gfx.Camera;

public class GradiusDroneSwarm extends GradiusShip{

	public GradiusDroneSwarm(StardustGame game, double x, double y) {
		super(game);
		this.setXY(x, y);
		this.setBoundRadius(0);
		this.setDirection(0);
	}

	private int ammo=4;
	private double cooldown=0;
	
	public void update(double dt) {
		cooldown+=dt;
		if(cooldown>0.25 && ammo>0){
			game.$currentState().addEntity(new GradiusDrone(game,x,y));
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