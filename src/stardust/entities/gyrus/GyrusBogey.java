package stardust.entities.gyrus;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import engine.gfx.Camera;

public class GyrusBogey extends GyrusShip{

	public GyrusBogey(StardustGame game) {
		super(game, game.$prng().$double(0, 2*Math.PI), 1);
		setBoundRadius(6);
		sp=90;
		spt=game.$prng().$double(-0.2*Math.PI, 0.2*Math.PI);
	}
	
	private double sp;
	private double spt;
	private double tt=0;
	
	public void update(double dt) {
		updateBlip(dt);
		
		t+=spt*dt;
		tt+=Math.PI*dt;
		double ddx=sp*dt*$ndxscale();
		dx+=ddx;
		
		updateNormalizedXY();
		updateNormalizedBoundRadius();
		deactivateIfOutOfBounds();
	}
	
	//x1, y1, x2, y2 line render
	private double l[]={
			-2,-8,-4,-4,-4,-4,-4,4,-4,4,-2,8,-2,8,-3,8,-3,8,-12,10,-12,10,-12,8,-12,8,-6,4,-6,4,-7,0,-7,0,-6,-4,-6,-4,-12,-8,-12,-8,-12,-10,-12,-10,-3,-8,-3,-8,-2,-8,2,-8,3,-8,3,-8,12,-10,12,-10,12,-8,12,-8,6,-4,6,-4,7,0,7,0,6,4,6,4,12,8,12,8,12,10,12,10,3,8,3,8,2,8,2,8,4,4,4,4,4,-4,4,-4,2,-8,-4,-4,-2,-6,-2,-6,2,-6,2,-6,4,-4,4,4,2,6,2,6,-2,6,-2,6,-4,4,-2,-6,-2,6,2,6,2,-6,0,-2,-2,0,-2,0,0,2,0,2,2,0,2,0,0,-2,-3,-8,-6,-4,-6,-4,-4,-4,-4,4,-6,4,-6,4,-3,8,4,4,6,4,6,4,3,8,3,-8,6,-4,6,-4,4,-4,-12,-10,-10,-16,-10,-16,-16,-8,-16,-8,-12,-8,-12,10,-10,16,-10,16,-16,8,-16,8,-12,8,12,-10,10,-16,10,-16,16,-8,16,-8,12,-8,12,8,16,8,16,8,10,16,10,16,12,10,
	};
	private double scale=0.625;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(tt),0,0,1);
		
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
