package stardust.entities.gyrus;

import stardust.StardustGame;
import stardust.entities.StardustEntity;
import engine.Vector;

public abstract class GyrusEntity extends StardustEntity{

	// uses t and dx to simulate one point perspective
	// still uses xy for collisions
	
	protected static double ndx=160;
	protected static double ndr=0.125;
	protected double $ndxscale(){
		return dx/ndx;
	}
	protected void updateNormalizedXY(){
		x=Vector.vectorToDx(t, dx);
		y=Vector.vectorToDy(t, dx);
	}
	
	protected double nr;
	public void setBoundRadius(double r){
		nr=r;
	}
	protected void updateNormalizedBoundRadius(){
		r=nr*$ndxscale();
		if(nr<ndr){
			nr=ndr;
		}
	}
	public GyrusEntity(StardustGame game, double t, double dx) {
		super(game);
		this.t=t;
		this.dx=dx;
		updateNormalizedXY();
		updateNormalizedBoundRadius();
	}

}
