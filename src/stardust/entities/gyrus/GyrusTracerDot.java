package stardust.entities.gyrus;

import engine.Vector;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class GyrusTracerDot extends GyrusEntity{
	public GyrusTracerDot(StardustGame game, double t, double dx, double r, double a, double ds, int seg) {
		super(game, t, dx);
		blip();
		this.setBoundRadius(r);
		this.alpha=a;
		this.ds=ds;
		this.seg=seg;
	}
	
	private double ds;
	private double tt=game.$prng().$double(0, Math.PI);
	private int seg=8;
	
	public void update(double dt) {
		updateBlip(dt);
		double dr=Math.sqrt(nr)*2*dt;
		nr-=(dr<dt?dt:dr)*ds;
		if(nr<=ndr){
			deactivate();
		}
		
		dx+=140*dt;
		updateNormalizedXY();
		updateNormalizedBoundRadius();
	}

	public void render(Camera c) {
		double ci=2*Math.PI;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(tt), 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(double i=0;i<ci;i+=ci/seg){
			double dxyx1=Vector.vectorToDx(i,r*c.$zoom()), 
					dxyy1=Vector.vectorToDy(i,r*c.$zoom()), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),r*c.$zoom()),
					dxyy2=Vector.vectorToDy(i+(ci/seg),r*c.$zoom());
			GL11.glVertex2d(dxyx1, dxyy1);
			GL11.glVertex2d(dxyx2, dxyy2);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public boolean isCollidable(){
		return false;
	}
	public void onDeath() {
		
	}
}
