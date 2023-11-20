package stardust.entities;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class VostokOne extends StardustEntity{
	
	public int points() {
		return 25;
	}
	
	public VostokOne(StardustGame game, double x, double y) {
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
	private double l[]={
		0,0,-2,-6,-2,-6,-8,-8,-8,-8,-14,-6,-14,-6,-16,0,-16,0,-14,6,-14,6,-8,8,-8,8,-2,6,-2,6,0,0,-16,0,-2,-6,-2,-6,-2,6,-2,6,-16,0,-2,-6,0,-8,0,-8,0,8,0,8,-2,6,0,-8,2,-8,2,-8,2,8,2,8,0,8,2,-8,4,-9,4,-9,4,9,4,9,2,8,4,-9,14,-2,14,-2,14,2,14,2,4,9,14,1,4,4,14,-1,4,-5,14,0,4,0,14,-2,16,-3,16,-3,16,3,16,3,14,2,2,-9,7,-9,7,-9,7,-7,2,-9,2,-8,2,-8,1,-10,1,-10,-1,-10,-1,-10,-2,-6,0,-10,0,-24,1,-10,13,-19,-1,-10,-13,-19,-8,-3,-10,-2,-10,-2,-11,0,-11,0,-10,2,-10,2,-8,3,-8,3,-6,2,-6,2,-5,0,-5,0,-6,-2,-6,-2,-8,-3,-14,-6,-2,-6,-2,6,-14,6,16,-1,17,-1,17,-1,17,1,17,1,16,1,
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
		game.$currentState().addEntity(new ElectromagneticPulse(game,$x(),$y()));
		if(killer instanceof PlayerStarfighter){
			game.$currentState().addEntity(new Power(game, x, y, (StardustEntity) killer));
		}
	}

}
