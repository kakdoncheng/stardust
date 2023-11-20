package stardust.entities.silo;

import stardust.StardustGame;
import stardust.entities.StardustEntity;

public class WarheadExplosion extends AnnihilatingExplosion{
	
	public WarheadExplosion(StardustGame game, double x, double y, int r,
			StardustEntity owner) {
		super(game, x, y, r, owner);
	}

	public WarheadExplosion(StardustGame game, double x, double y, int r,
			StardustEntity owner, boolean mute) {
		super(game, x, y, r, owner, mute);
	}

}
