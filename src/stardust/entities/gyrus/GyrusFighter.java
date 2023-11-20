package stardust.entities.gyrus;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import engine.gfx.Camera;

public class GyrusFighter extends GyrusShip{

	public GyrusFighter(StardustGame game) {
		super(game, game.$prng().$double(0, 2*Math.PI), 1);
		setBoundRadius(6);
		sp=160;
		spt=0;//game.$prng().$double(-0.05*Math.PI, 0.05*Math.PI);
	}
	
	private double sp;
	private double spt;
	private double cd=2;
	
	public void update(double dt) {
		updateBlip(dt);
		
		t+=spt*dt;
		double ddx=sp*dt*$ndxscale();
		dx+=ddx;
		
		updateNormalizedXY();
		updateNormalizedBoundRadius();
		deactivateIfOutOfBounds();
		
		cd-=dt;
		if(cd<=0){
			game.$currentState().addEntity(new GyrusProjectile(game, this));
			cd=2;
		}
	}
	
	//x1, y1, x2, y2 line render
	private double l[]={
			//0,4,-2,-1,-2,-1,-2,2,-2,2,-4,-1,-4,-1,-10,-4,-10,-4,-10,-5,-10,-5,-3,-6,-3,-6,-2,-9,-2,-9,-1,-6,-1,-6,0,-7,0,-7,1,-6,1,-6,2,-9,2,-9,3,-6,3,-6,10,-5,10,-5,10,-4,10,-4,4,-1,4,-1,2,2,2,2,2,-1,2,-1,0,4,0,3,-1,0,-1,0,0,-2,0,-2,1,0,1,0,0,3,
			0,3,-2,-1,-2,-1,-2,2,-2,2,-4,-1,-4,-1,-10,-3,-10,-3,-4,-4,-4,-4,-4,-7,-4,-7,-2,-4,-2,-4,0,-5,0,-5,2,-4,2,-4,4,-7,4,-7,4,-4,4,-4,10,-3,10,-3,4,-1,4,-1,2,2,2,2,2,-1,2,-1,0,3,0,3,-1,0,-1,0,0,-2,0,-2,1,0,1,0,0,3,-4,-7,-2,-3,-2,-3,-2,-4,2,-4,2,-3,2,-3,4,-7,
	};
	private double scale=1.25;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t),0,0,1);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale*$ndxscale(), l[i+1]*c.$zoom()*scale*$ndxscale());
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale*$ndxscale(), l[i+3]*c.$zoom()*scale*$ndxscale());
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public void onDeath() {
		
	}
}
