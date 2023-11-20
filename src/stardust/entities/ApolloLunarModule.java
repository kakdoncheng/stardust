package stardust.entities;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class ApolloLunarModule extends StardustEntity{
	
	public int points() {
		return 25;
	}
	
	public ApolloLunarModule(StardustGame game, double x, double y) {
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
		//lower chassis
		-1.5,-.25,-1.5,1,
		-1.5,1,1.5,1,
		1.5,1,1.5,-.25,
		1.5,-.25,-1.5,-.25,
		-.75,-.25,-.75,1,
		.75,-.25,.75,1,
		-.5,1,-1,2,
		-1,2,1,2,
		1,2,.5,1,
		//mid landing gear
		-.75,-.25,-.25,0,
		-.25,0,.25,0,
		.25,0,.75,-.25,
		-.25,0,0,2.5,
		0,2.5,.25,0,
		0,2.5,-.5,2.75,
		-.5,2.75,.5,2.75,
		.5,2.75,0,2.5,
		//left landing gear
		-.75,-.25,-1.5,0,
		-1.5,0,-2.25,0,
		-2.25,0,-3.5,2,
		-3.5,2,-2,0,
		-1.5,0.75,-2.25,0,
		-2.25,0,-1.5,1,
		-1.5,1,-3,1.25,
		-3,1.25,-1.5,.75,
		-3.5,2,-4,2.25,
		-4,2.25,-3,2.25,
		-3,2.25,-3.5,2,
		//flip
		.75,-.25,1.5,0,
		1.5,0,2.25,0,
		2.25,0,3.5,2,
		3.5,2,2,0,
		1.5,0.75,2.25,0,
		2.25,0,1.5,1,
		1.5,1,3,1.25,
		3,1.25,1.5,.75,
		3.5,2,4,2.25,
		4,2.25,3,2.25,
		3,2.25,3.5,2,
		//upper chassis
		-1,-.25,-1.5,-.75,
		-1.5,-.75,-1.5,-2,
		-1.5,-2,-1.25,-2.25,
		-1.25,-2.25,1.25,-2.25,
		1.25,-2.25,1.5,-2,
		1.5,-2,1.5,-.75,
		1.5,-.75,1,-.25,
		0,-2.25,-1.25,-.5,
		-1.25,-.5,1.25,-.5,
		1.25,-.5,0,-2.25,
		-1.25,-2.25,-1.25,-.5,
		1.25,-2.25,1.25,-.5,
		//antennae
		-.75,-2.25,-.5,-2.5,
		-.5,-2.5,.5,-2.5,
		.5,-2.5,.75,-2.25,
		-.75,-2.25,-1.25,-2.75,
		-.5,-2.5,-1,-3
		
	};
	private double scale=3;
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
