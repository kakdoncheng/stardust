package stardust.entities;

import stardust.StardustGame;

public class GateAsteroid extends Asteroid{

	public GateAsteroid(StardustGame game, double x, double y) {
		super(game, x, y, 8);
	}
	
	public void update(double dt){
		super.update(dt);
		wraparoundIfOutOfScreenBounds();
	}

}
