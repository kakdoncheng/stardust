package stardust.entities;

import stardust.StardustGame;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class ElectromagneticPulse extends StardustEntity{

	public ElectromagneticPulse(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(0);
		Audio.addSoundEffect("explosion-emp", 1);
	}

	public void update(double dt) {
		for(int i=0;i<360;i++){
			game.$currentState().addEntity(new Spark(game, x,y,16*game.$prng().$int(32, 46)));
		}
		game.$currentState().addEntity(new RadarBlip(game, x,y));
		active=false;
	}

	public void render(Camera c) {
	}
	
	public boolean isCollidable(){
		return false;
	}

	public void onDeath() {
		
	}
}
