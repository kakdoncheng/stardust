package stardust.unused;

import stardust.StardustGame;
import stardust.entities.PlayerStarfighter;

public class PlayerAntiStardustfighter extends PlayerStarfighter{

	public PlayerAntiStardustfighter(StardustGame game, double x, double y) {
		super(game, x, y);
	}
	
	protected void fireProjectile(){
		game.$currentState().addEntity(new AntiStardustProjectile(game, t, this));
	}
}
