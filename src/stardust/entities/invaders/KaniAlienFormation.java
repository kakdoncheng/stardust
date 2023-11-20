package stardust.entities.invaders;

import stardust.StardustGame;

public class KaniAlienFormation extends AlienFormation{
	private static int[][] rank={
			{0,0,2,0,0,0,0,0,2,0,0},
			{0,0,0,2,0,0,0,2,0,0,0},
			{0,0,2,2,2,2,2,2,2,0,0},
			{0,2,2,0,2,2,2,0,2,2,0},
			{2,2,2,2,2,2,2,2,2,2,2},
			{2,0,2,2,2,2,2,2,2,0,2},
			{2,0,2,0,0,0,0,0,2,0,2},
			{0,0,0,2,2,0,2,2,0,0,0},
	};
	public KaniAlienFormation(StardustGame game, double x, double y) {
		super(game, x, y, rank);
	}
}
