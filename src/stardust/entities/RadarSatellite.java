package stardust.entities;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class RadarSatellite extends StardustEntity {

	public int points() {
		return 10;
	}
	
	public RadarSatellite(StardustGame game, double x, double y) {
		super(game);
		setXY(x, y);
		setDirection(game.$prng().$double(0, 2*Math.PI));
		setBoundRadius(8);
		setSpeedVector(t, 44);
	}
	
	private double bt=0;
	private double tt=0;
	private double dtt=game.$prng().$double(0.25, 0.75);
	
	public void update(double dt) {
		if(!active){
			return;
		}
		updateBlip(dt);
		tt+=dt*dtt;
		updatePosition(dt);
		deactivateIfOutOfBounds();
		
		bt+=dt;
		if(bt>=1.5){
			game.$currentState().addEntity(new RadarBlip(game,x,y));
			bt-=1.5;
		}
	}
	
	//x1, y1, x2, y2 line render
	private double[] l={
		-2,-4,2,-4,
		2,-4,2,-3,
		2,-3,-2,-3,
		-2,-3,-2,-4,
		-2,-2,-1,-3,
		2,-2,1,-3,
		-2,-2,2,-2,
		2,-2,2,2,
		2,2,-2,2,
		-2,2,-2,-2,
		2,2,1,3,
		-2,2,-1,3,
		-1,3,1,3,
		1,3,5,5,
		-1,3,-5,5,
		0,3,5,5,
		0,3,-5,5,
		0,3,0,7,
		//panels
		2,-1,4,-3,
		2,-1,4,1,
		4,1,10,1,
		4,-3,10,-3,
		4,-3,4,1,
		6,-3,6,1,
		8,-3,8,1,
		10,-3,10,1,
		-2,-1,-4,-3,
		-2,-1,-4,1,
		-4,1,-10,1,
		-4,-3,-10,-3,
		-4,-3,-4,1,
		-6,-3,-6,1,
		-8,-3,-8,1,
		-10,-3,-10,1,
		//additional
		2,0,-2,0,
		2,1,-2,1,
		1.5,0,1.5,1,
		-1.5,0,-1.5,1,
	};
	private double scale=1.5;
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
		game.$currentState().addEntity(new ElectromagneticPulse(game,x,y));
		if(killer instanceof PlayerStarfighter){
			game.$currentState().addEntity(new Power(game, x, y, (StardustEntity) killer));
		}
		
	}
}
