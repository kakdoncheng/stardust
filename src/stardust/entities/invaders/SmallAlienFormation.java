package stardust.entities.invaders;

import stardust.StardustGame;

public class SmallAlienFormation extends AlienFormation{
	private static int[][] rank={
			{1,1,1,1,1},
			{2,2,2,2,2},
			{2,2,2,2,2},
			{3,3,3,3,3},
			{3,3,3,3,3},
	};
	public SmallAlienFormation(StardustGame game, double x, double y) {
		super(game, x, y, rank);
	}
}
