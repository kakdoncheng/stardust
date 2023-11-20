package stardust.entities.gyrus;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import engine.Vector;
import engine.gfx.Camera;

public class GyrusStardust extends GyrusEntity{
	
	public GyrusStardust(StardustGame game) {
		this(game, 1);
		//blip();
	}

	public GyrusStardust(StardustGame game, double dx) {
		super(game, game.$prng().$double(0, 2*Math.PI), dx);
		setBoundRadius(0);
		setXY(0,0);
	}

	private boolean dot=game.$prng().$double(0,2)>1;
	private double tt=game.$prng().$double(0, 2*Math.PI);
	private double sp=game.$prng().$double(120, 240);
	public void update(double dt) {
		tt+=Math.PI*dt;
		double ddx=sp*dt*$ndxscale();
		//if(ddx<0.25){
		//	ddx=0.25;
		//}
		dx+=ddx;
		updateBlip(dt);
		updateNormalizedXY();
		deactivateIfOutOfScreenBounds();
	}

	public void render(Camera c) {
		double rr=$ndxscale()*c.$zoom();
		if(!dot||(dot&&rr<2)){
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_LINES);
			setRadarColor(1);
			GL11.glVertex2d(c.$cx($x()), c.$cy($y()));
			GL11.glVertex2d(c.$cx($x()+Vector.vectorToDx(t, rr)), c.$cy($y()+Vector.vectorToDy(t, rr)));
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}else{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPushMatrix();
			GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
			GL11.glRotated(Math.toDegrees(tt), 0, 0, 1);
			GL11.glBegin(GL11.GL_QUADS);
			setRadarColor(1);
			GL11.glVertex2d(-rr/2, -rr/2);
			GL11.glVertex2d(-rr/2, rr/2);
			GL11.glVertex2d(rr/2, rr/2);
			GL11.glVertex2d(rr/2, -rr/2);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		
	}

	public void blip(){
		super.blip();
		alpha=0.75;
	}
	public boolean isCollidable(){
		return false;
	}
	public void onDeath() {
		
	}

}
