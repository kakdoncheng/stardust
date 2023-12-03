package stardust.entities.invaders;

import stardust.StardustGame;

public class AlienTypeIka extends Alien{
	
	public int points(){
		return 30;
	}
	
	private static final double frame1[]={
		-2,-3,-4,-3,-4,-3,-4,-1,-4,-1,-2,-1,-2,-1,-2,-3,2,-3,2,-1,2,-1,4,-1,4,-1,4,-3,4,-3,2,-3,-4,-5,-6,-5,-6,-5,-6,-3,-6,-3,-8,-3,-8,-3,-8,1,-8,1,-6,1,-6,1,-6,3,-6,3,-4,3,-4,3,-4,1,-4,1,-2,1,-2,1,-2,3,-2,3,2,3,2,3,2,1,2,1,4,1,4,1,4,3,4,3,6,3,6,3,6,1,6,1,8,1,8,1,8,-3,8,-3,6,-3,6,-3,6,-5,6,-5,4,-5,4,-5,4,-7,4,-7,2,-7,2,-7,2,-9,2,-9,-2,-9,-2,-9,-2,-7,-2,-7,-4,-7,-4,-7,-4,-5,-6,3,-8,3,-8,3,-8,5,-8,5,-6,5,-6,5,-6,3,-6,5,-6,7,-6,7,-4,7,-4,7,-4,5,-4,5,-6,5,6,3,6,5,6,5,8,5,8,5,8,3,8,3,6,3,6,5,4,5,4,5,4,7,4,7,6,7,6,7,6,5,
	};
	private static final double frame2[]={
		-4,-1,-4,-3,-4,-3,-2,-3,-2,-3,-2,-1,-2,-1,-4,-1,2,-1,2,-3,2,-3,4,-3,4,-3,4,-1,4,-1,2,-1,-4,1,-4,3,-4,3,-2,3,-2,3,-2,1,-2,1,2,1,2,1,2,3,2,3,4,3,4,3,4,1,4,1,8,1,8,1,8,-3,8,-3,6,-3,6,-3,6,-5,6,-5,4,-5,4,-5,4,-7,4,-7,2,-7,2,-7,2,-9,2,-9,-2,-9,-2,-9,-2,-7,-2,-7,-4,-7,-4,-7,-4,-5,-4,-5,-6,-5,-6,-5,-6,-3,-6,-3,-8,-3,-8,-3,-8,1,-8,1,-4,1,-4,3,-6,3,-6,3,-6,5,-6,5,-8,5,-8,5,-8,7,-8,7,-6,7,-6,7,-6,5,-6,5,-4,5,-4,5,-4,3,4,3,6,3,6,3,6,5,6,5,8,5,8,5,8,7,8,7,6,7,6,7,6,5,6,5,4,5,4,5,4,3,2,3,2,5,2,5,4,5,4,5,4,7,4,7,2,7,2,7,2,5,2,5,-2,5,-2,5,-2,7,-2,7,-4,7,-4,7,-4,5,-4,5,-2,5,-2,5,-2,3,-2,3,2,3,
	};
	public AlienTypeIka(StardustGame game, double x, double y) {
		super(game, x, y, frame1, frame2); 
	}

}
