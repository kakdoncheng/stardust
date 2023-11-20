package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import engine.gfx.Camera;

public class VoyagerOne extends StardustEntity{
	
	public int points() {
		return 25;
	}
	
	public VoyagerOne(StardustGame game, double x, double y) {
		super(game);
		setXY(x, y);
		setDirection(game.$prng().$double(0, 2*Math.PI));
		setBoundRadius(8);
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
		//voyager one
		-3,-4,-3,2,
		-3,2,-1,2,
		-1,2,-1,-4,
		-1,-4,-3,-4,
		-3,1,-1,1,
		-1,-3,-3,-3,
		-3,-3,-6,-1,
		-3,1,-6,-1,
		-1,-1,-6,-1,
		-5,-1,-6,-2,
		-6,-2,-7,-1,
		-7,-1,-6,0,
		-6,0,-5,-1,
		
		-1,-2,0,-2,
		-1,0,0,0,
		-1,-3,0,-2,
		-1,1,0,0,
		-1,-2,-3,-9,
		-3,-9,-0,-2,
		-3,-8,-2,-9,
		-2,-9,-3,-10,
		-3,-10,-4,-9,
		-4,-9,-3,-8,
		
		-1,0,-.5,6,
		-.5,6,0,0,
		-.5,5,-1,6,
		-1,6,-1,9,
		0,9,0,6,
		0,6,-.5,5,
		-1,6,0,6,
		-1,6.5,0,6.5,
		-1,7,0,7,
		-1,7.5,0,7.5,
		-1,8,0,8,
		-1,8.5,0,8.5,
		-1,9,0,9,
		
		0,0,-15,10,
		0,0,-7,16,
		0,0,24,15,
		24,15,-1,0,
		
		-1,-2,0,-4,
		0,-4,2,-6,
		2,-6,2,4,
		2,4,0,2,
		0,2,-1,0,
		0,2,0,-4,
		2,-2,7,-1,
		7,-1,2,0,
		8,-1,2,-1,
		
		2,-1.75,7,-1,
		7,-1,2,-.25,
	};
	private double scale=1.25;
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
		game.$currentState().addEntity(new ElectromagneticPulse(game,$x(),$y()));
		if(killer instanceof PlayerStarfighter){
			game.$currentState().addEntity(new Power(game, x, y, (StardustEntity) killer));
		}
	}
}
