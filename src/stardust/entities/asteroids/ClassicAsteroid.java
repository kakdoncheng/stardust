package stardust.entities.asteroids;

import stardust.StardustGame;
import stardust.entities.Asteroid;

public class ClassicAsteroid extends Asteroid{

	public ClassicAsteroid(StardustGame game, double x, double y, int size) {
		super(game, x, y, size);
	}
	
	public void update(double dt) {
		if(!active){
			return;
		}
		updateBlip(dt);
		tt+=dtt*dt;
		updatePosition(dt);
		wraparoundIfOutOfScreenBounds();
	}
	
	public void onDeath(){
		if(r>4){
			game.$currentState().addEntity(new ClassicAsteroid(game, x, y, (int)(r/2)));
			game.$currentState().addEntity(new ClassicAsteroid(game, x, y, (int)(r/2)));
		}
	}

}
