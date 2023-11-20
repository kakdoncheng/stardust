package stardust.entities.gradius;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class GradiusDrone extends GradiusShip{
	public int points(){
		return 10;
	}
	
	public GradiusDrone(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(6);
		setDirection(0);
		setSpeedVector(Math.PI*0.5,90);
	}
	
	public void update(double dt) {
		if(!active){
			return;
		}
		t+=2*Math.PI*dt;
		updateBlip(dt);
		updatePosition(dt);
		deactivateIfOutOfBounds();
	}

	//x1, y1, x2, y2 line render
	private double l[]={
			-4,-3,-8,-6,-8,-6,-6,-16,-6,-16,-16,-6,-16,-6,-14,-4,-14,-4,-14,4,-14,4,-16,6,-16,6,-6,16,-6,16,-8,6,-8,6,-4,3,-4,-4,-4,4,-4,4,4,4,4,4,4,-4,4,-4,-4,-4,-4,-4,-2,-7,-2,-7,2,-7,2,-7,4,-4,-4,4,-2,7,-2,7,2,7,2,7,4,4,4,-3,8,-6,8,-6,6,-16,6,-16,16,-6,16,-6,14,-4,14,-4,14,4,14,4,16,6,16,6,6,16,6,16,8,6,-16,-6,-16,6,16,-6,16,6,8,6,4,3,-8,-6,-8,6,8,-6,8,6,4,-3,-4,-3,-4,3,4,3,-8,-6,-14,-4,-14,4,-8,6,14,-4,8,-6,14,4,8,6,
	};
	private double scale=0.375;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t),0,0,1);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void onDeath() {
		if(killer!=null){
			game.$currentState().addEntity(new GradiusProjectile(game, directionTo(killer), this));
		}
	}
}
