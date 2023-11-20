package stardust.entities;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class SputnikOne extends StardustEntity{
	
	public int points() {
		return 25;
	}
	
	public SputnikOne(StardustGame game, double x, double y) {
		super(game);
		setXY(x, y);
		setDirection(game.$prng().$double(0, 2*Math.PI));
		setBoundRadius(4);
		setSpeedVector(t, 44);
	}
	
	private double tt=0;
	private double dtt=game.$prng().$double(0.25, 0.75);
	
	public void update(double dt) {
		updateBlip(dt);
		tt+=dt*dtt;
		updatePosition(dt);
		wraparoundIfOutOfBounds();
	}
	
	//x1, y1, x2, y2 line render
	private double[] l={
		//main chassis
		0,-6,3,-5,
		3,-5,4,-2,
		4,-2,3,1,
		3,1,0,2,
		3,1,0,1,
		3,-5,0,-5,
		3,-5,3,1,
		4,-2,0,-2,
		//antennae
		0,-6,1,-5,
		1,-5,3,-2,
		3,-2,2,-5,
		2,-5,0,-6,
		3,-2,8,18,
		8,18,3,-1,
		3,-5,14,14,
		14,14,5,-1,
		5,-1,4,-2,
		//copy and flip
		-0,-6,-3,-5,
		-3,-5,-4,-2,
		-4,-2,-3,1,
		-3,1,-0,2,
		-3,1,-0,1,
		-3,-5,-0,-5,
		-3,-5,-3,1,
		-4,-2,-0,-2,
		-0,-6,-1,-5,
		-1,-5,-3,-2,
		-3,-2,-2,-5,
		-2,-5,-0,-6,
		-3,-2,-8,18,
		-8,18,-3,-1,
		-3,-5,-14,14,
		-14,14,-5,-1,
		-5,-1,-4,-2,
		//additional details
		3,-2,0,2,
		0,2,-3,-2
	};
	private double scale=0.75;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotatef((float)Math.toDegrees(tt), 0, 0, 1);
		
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
