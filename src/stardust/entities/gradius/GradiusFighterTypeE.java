package stardust.entities.gradius;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class GradiusFighterTypeE extends GradiusShip{
	public int points(){
		return 20;
	}
	
	public GradiusFighterTypeE(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(6);
		setDirection(game.$prng().$double(0, 2)>1?Math.PI*0.25:Math.PI*0.75);
	}
	
	private double cooldown=0;
	public void update(double dt) {
		if(!active){
			return;
		}
		
		// move in zigzag
		if(y<game.$topScreenEdge()){
			setDirection(Math.PI*0.25);
		}
		if(y>game.$bottomScreenEdge()){
			setDirection(Math.PI*0.75);
		}
		setSpeedVector(t,120);
		
		// weapons
		if(distanceTo(target)<240){
			cooldown-=dt;
			if(cooldown<=0){
				game.$currentState().addEntity(new GradiusProjectile(game, directionTo(target), this));
				cooldown=1;
			}
		}
		
		updateBlip(dt);
		updatePosition(dt);
		deactivateIfOutOfBounds();
	}

	//x1, y1, x2, y2 line render
	private double l[]={
		-8,-6,-18,-8,-18,-8,-14,-2,-14,-2,-14,6,-14,6,-8,14,-8,14,-8,8,-8,8,-4,6,-4,6,-4,8,-4,8,-3,8,-3,8,-3,6,-3,6,-1,7,-1,7,1,7,1,7,3,6,3,6,3,8,3,8,4,8,4,8,4,6,4,6,8,8,8,8,8,14,8,14,14,6,14,6,14,-2,14,-2,18,-8,18,-8,8,-6,8,-6,2,-4,2,-4,0,-6,0,-6,-2,-4,-2,-4,-8,-6,-5,-5,-5,-7,-5,-7,-1,-7,-1,-7,-1,-5,-5,-7,-4,-9,-4,-9,-2,-9,-2,-9,-1,-7,1,-7,2,-9,2,-9,4,-9,4,-9,5,-7,5,-7,5,-5,5,-7,1,-7,1,-7,1,-5,-5,-7,-8,-6,-8,-6,-4,6,-4,6,-3,3,-3,3,-3,6,-3,6,-2,-4,-3,6,0,-6,0,-6,3,6,3,6,0,3,0,3,-3,6,2,-4,3,6,3,6,3,3,3,3,4,6,4,6,8,-6,8,-6,14,-2,14,-2,8,8,-8,-6,-14,-2,-14,-2,-8,8,
	};
	private double scale=0.5;
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
		
	}
}