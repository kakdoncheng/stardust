package stardust.entities.invaders;

import stardust.StardustGame;

public class ClassicAlienFormation extends AlienFormation{
	///*
	private static int[][] rank={
			{1,1,1,1,1,1,1,1,1,1,1},
			{2,2,2,2,2,2,2,2,2,2,2},
			{2,2,2,2,2,2,2,2,2,2,2},
			{3,3,3,3,3,3,3,3,3,3,3},
			{3,3,3,3,3,3,3,3,3,3,3},
	};
	//*/
	/*
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
	//*/
	public ClassicAlienFormation(StardustGame game, double x, double y) {
		super(game, x, y, rank);
	}

}
