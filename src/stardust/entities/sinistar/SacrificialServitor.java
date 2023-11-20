package stardust.entities.sinistar;

import org.lwjgl.opengl.GL11;

import engine.gfx.Camera;
import stardust.StardustGame;

public class SacrificialServitor extends SinisterEntity{

	public int points() {
		return 0;
	}
	public boolean isSet(){
		return !(dist>0);
	}
	
	private double dist;
	
	public SacrificialServitor(StardustGame game, double x, double y) {
		super(game);
		this.setBoundRadius(8);
		this.setXY(x, y);
		this.setDirection(game.$prng().$double(0, Math.PI*2));
		
		dist=game.$prng().$double(0, 80);
		this.offsetTR(t+Math.PI, dist);//);
		this.setSpeedVector(t, 60);
		blip();
	}

	public void update(double dt) {
		//updateBlip(dt);
		dist-=$speed()*dt;
		if(dist>0){
			updatePosition(dt);
		}
		//
	}

	// x1, y1, x2, y2 line render
	private double l[]={
			0,-1,4,0,4,0,6,-2,6,-2,7,0,7,0,7,4,7,4,5,8,5,8,3,9,3,9,4,8,4,8,5,4,5,4,4,2,4,2,3,3,3,3,1,3,1,3,0,2,0,2,-1,3,-1,3,-3,3,-3,3,-4,2,-4,2,-5,4,-5,4,-4,8,-4,8,-3,9,-3,9,-5,8,-5,8,-7,4,-7,4,-7,0,-7,0,-6,-2,-6,-2,-4,0,-4,0,0,-1,-2,3,-1,6,-1,6,-1,3,-1,3,0,4,0,4,1,3,1,3,1,6,1,6,2,3,-3,3,0,-1,0,-1,3,3,-5,-1,-4,-3,-4,-3,-2,-3,-2,-3,-1,-1,1,-1,2,-3,2,-3,4,-3,4,-3,5,-1,-5,-1,5,-1,-3,-3,-2,-4,-2,-4,2,-4,2,-4,3,-3,-2,-4,-3,-5,-3,-5,3,-5,3,-5,2,-4,-3,-5,-2,-6,-2,-6,-2,-5,2,-5,2,-6,2,-6,3,-5,-4,0,-4,2,-4,2,-7,4,4,0,4,2,4,2,7,4,
	};
	private double scale=1;
	public void render(Camera c) {
		// render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
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
		
	}

}
