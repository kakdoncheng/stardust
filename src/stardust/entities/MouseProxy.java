package stardust.entities;

import engine.gfx.Camera;
import engine.input.MouseHandler;
import stardust.StardustGame;

public class MouseProxy extends StardustEntity{

	public MouseProxy(StardustGame game) {
		super(game);
	}
	
	// proxy returns mouse coordinates
	public double $x(){
		return MouseHandler.$mx();
	}
	public double $y(){
		return MouseHandler.$my();
	}

	// unused
	public void update(double dt) {}
	public void render(Camera c) {}
	public void onDeath() {}

}
